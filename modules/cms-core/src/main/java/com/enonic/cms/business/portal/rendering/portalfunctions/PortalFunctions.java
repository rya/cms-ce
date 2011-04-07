/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.rendering.portalfunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.enonic.cms.framework.util.URLUtils;
import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.captcha.CaptchaService;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.PortletDao;

import com.enonic.cms.business.SitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.SiteURLResolver;
import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageRequestParser;
import com.enonic.cms.business.localization.LocalizationService;
import com.enonic.cms.business.localization.resource.LocalizationResourceBundleUtils;
import com.enonic.cms.business.portal.image.ImageService;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.business.resolver.locale.LocaleResolverService;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.AttachmentNativeLinkKeyWithBinaryKey;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.portal.ReservedLocalPaths;
import com.enonic.cms.domain.portal.Ticket;
import com.enonic.cms.domain.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateImageUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializer;
import com.enonic.cms.domain.portal.instruction.RenderWindowInstruction;
import com.enonic.cms.domain.resolver.ResolverContext;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.WindowKey;
import com.enonic.cms.domain.structure.portlet.PortletEntity;
import com.enonic.cms.domain.structure.portlet.PortletKey;

public class PortalFunctions
{
    Pattern IMAGE_KEY_FORMAT = Pattern.compile( "(\\d+(/\\w+){0,2}?)|(^user/.+$)" );

    private static final String CAPTCHA_RELATIVE_URL = "_captcha";

    private static final String RESOURCE_PATH_PUBLIC_ROOT = "/" + ReservedLocalPaths.PATH_RESOURCE;

    private boolean encodeURIs;

    private SiteURLResolver siteURLResolver;

    private HttpServletRequest request;

    private CaptchaService captchaService;

    private LocalizationService localizeService;

    private LocaleResolverService localeResolverService;

    private ContentDao contentDao;

    private MenuItemDao menuItemDao;

    private PortletDao portletDao;

    private ContentBinaryDataDao contentBinaryDataDao;

    private PortalFunctionsContext context;

    private ImageService imageService;

    private SecurityService securityService;

    private CreateAttachmentUrlFunction createAttachmentUrlFunction;

    private SitePropertiesService sitePropertiesService;

    public String getInstanceKey()
    {
        return context.getPortalInstanceKey().toString();
    }

    public String getWindowKey()
    {
        if ( !context.getPortalInstanceKey().isWindow() )
        {
            throw new PortalFunctionException( "Not in a context of a window" );
        }
        return context.getPortalInstanceKey().getWindowKey().asString();
    }

    public Boolean isWindowInline()
    {
        if ( !context.getPortalInstanceKey().isWindow() )
        {
            throw new PortalFunctionException( "Not in a context of a window" );
        }
        return context.isRenderedInline();
    }

    public String createUrl( String local )
    {
        return createUrl( local, null );
    }

    public String createUrl( String local, String[] params )
    {
        SitePath sitePath = new SitePath( context.getSite().getKey(), new Path( local != null ? local : "" ) );
        addParamsToSitePath( params, sitePath );
        return siteURLResolver.createUrl( request, sitePath, true );
    }

    public String createWindowUrl()
    {
        if ( !context.getPortalInstanceKey().isWindow() )
        {
            throw new PortalFunctionException( "Not in a context of a window" );
        }
        return createWindowUrl( context.getPortalInstanceKey().getWindowKey(), null, null );
    }

    public String createWindowUrl( String[] params )
    {
        if ( !context.getPortalInstanceKey().isWindow() )
        {
            throw new PortalFunctionException( "Not in a context of a window" );
        }
        return createWindowUrl( context.getPortalInstanceKey().getWindowKey(), params, null );
    }

    public String createWindowUrl( WindowKey windowKey, String[] params )
    {
        return createWindowUrl( windowKey, params, null );
    }

    public String createWindowUrl( WindowKey windowKey, String[] params, String outputFormat )
    {
        MenuItemKey menuItemKey = windowKey.getMenuItemKey();

        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey.toInt() );
        if ( menuItem == null )
        {
            throw new PortalFunctionException( "Menuitem does not exist: " + menuItemKey );
        }
        boolean menuItemIsInAnotherSite = !menuItem.getSite().equals( context.getSite() );
        if ( menuItemIsInAnotherSite )
        {
            throw new PortalFunctionException( "Menuitem exist in another site: " + menuItemKey );
        }

        PortletKey portletKey = windowKey.getPortletKey();
        PortletEntity portlet = portletDao.findByKey( portletKey.toInt() );
        if ( portlet == null )
        {
            throw new PortalFunctionException( "Portlet does not exist: " + menuItemKey );
        }

        Path localPath = menuItem.getPath();
        String portletPathStr = "_window/" + portlet.getName().toLowerCase();
        if ( outputFormat != null )
        {
            portletPathStr = portletPathStr + "." + outputFormat;
        }
        localPath = localPath.appendPath( new Path( portletPathStr ) );
        SitePath sitePath = new SitePath( context.getSite().getKey(), localPath );
        addParamsToSitePath( params, sitePath );

        return siteURLResolver.createUrl( request, sitePath, true );
    }

    public String createPageUrl()
    {
        return createPageUrl( null );
    }

    public String createPageUrl( String[] params )
    {

        MenuItemKey menuItemKey = context.getMenuItem().getMenuItemKey();

        if ( encodeURIs )
        {
            return encodeURI( "page", menuItemKey != null ? menuItemKey.toString() : "NA", params );
        }

        if ( menuItemKey == null )
        {
            final String failureReason = "createPageUrl must be called in context of a menuitem or window";
            throw new PortalFunctionException( failureReason );
        }

        if ( params != null && params.length % 2 == 1 )
        {
            String paramsStr = "";
            for ( String param : params )
            {
                paramsStr += paramsStr.length() > 0 ? ", " + param : param;
            }
            final String failureReason = "Illegal parameter. Illegal params size: " + params.length + ": " + paramsStr;
            throw new PortalFunctionException( failureReason );
        }

        SitePath originalSitePath = context.getOriginalSitePath().removeWindowReference();

        Path requestedLocalPath = originalSitePath.getLocalPath();
        if ( requestedLocalPath != null && requestedLocalPath.startsWith( "/page" ) && originalSitePath.getParam( "id" ) != null )
        {

            return createPageUrl( menuItemKey, params );
        }
        SitePath pageSitePath = originalSitePath.createNewInSameSite( requestedLocalPath, new HashMap<String, String[]>() );

        addParamsToSitePath( params, pageSitePath );
        return siteURLResolver.createUrl( request, pageSitePath, true );
    }

    public String createPageUrl( MenuItemKey menuItemKey, String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "page", String.valueOf( menuItemKey ), params );
        }

        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey.toInt() );

        if ( menuItem == null )
        {
            final String failureReason = "menuitem does not exist: " + menuItemKey;
            throw new PortalFunctionException( failureReason );
        }

        String path = menuItem.getPathAsString();

        SiteEntity currentSite = context.getSite();
        SiteEntity menuItemSite = menuItem.getSite();

        final boolean menuItemIsOnOtherSite = !currentSite.equals( menuItemSite );
        final boolean debugMode = RenderTrace.isTraceOn();
        final boolean createUrlOnOtherSiteUsingSiteUrl = !debugMode && menuItemIsOnOtherSite;

        if ( createUrlOnOtherSiteUsingSiteUrl )
        {
            return createUrlOnSiteUsingSiteURL( params, path, menuItemSite );
        }

        SitePath pageSitePath = new SitePath( menuItemSite.getKey(), new Path( path ) );
        addParamsToSitePath( params, pageSitePath );
        return siteURLResolver.createUrl( request, pageSitePath, true );
    }

    private String createUrlOnSiteUsingSiteURL( String[] params, String path, SiteEntity siteEntity )
    {
        String siteUrl = sitePropertiesService.getProperty( SitePropertyNames.SITE_URL, siteEntity.getKey() );

        if ( !URLUtils.verifyValidURL( siteUrl ) )
        {
            throw new PortalFunctionException(
                "Not able to create link to site " + siteEntity.getKey().toInt() + ": No valid cms.site.url defined in site" +
                    siteEntity.getKey().toInt() + ".properties: " + siteUrl );
        }

        SitePath pageSitePath = new SitePath( siteEntity.getKey(), new Path( path ) );
        addParamsToSitePath( params, pageSitePath );
        return siteURLResolver.createUrlWithBasePathOverride( request, pageSitePath, true, siteUrl );
    }

    public String createContentUrl( ContentKey contentKey )
    {
        return createContentUrl( contentKey, null );
    }

    public String createContentUrl( ContentKey contentKey, String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "content", contentKey.toString(), params );
        }

        if ( contentKey == null )
        {
            throw new PortalFunctionException( "ContentKey is null" );
        }

        ContentEntity content = contentDao.findByKey( contentKey );

        if ( content == null )
        {
            throw new PortalFunctionException( "Content with key : " + contentKey + " not found" );
        }

        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( contentKey.toString() );
        instruction.setParams( params );

        return serializePostProcessInstruction( instruction );
    }

    public String createPermalink( ContentKey contentKey )
    {
        return createPermalink( contentKey, null );
    }

    public String createPermalink( ContentKey contentKey, String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "content", contentKey.toString(), params );
        }

        if ( contentKey == null )
        {
            throw new PortalFunctionException( "ContentKey is null" );
        }

        ContentEntity content = contentDao.findByKey( contentKey );

        if ( content == null )
        {
            throw new PortalFunctionException( "Content with key : " + contentKey + " not found" );
        }

        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( contentKey.toString() );
        instruction.setParams( params );
        instruction.setCreateAsPermalink( true );

        return serializePostProcessInstruction( instruction );
    }


    public String createServicesUrlWithoutParams( String handler, String operation )
    {
        return createServicesUrl( handler, operation, null, null );
    }

    public String createServicesUrl( String handler, String operation, String[] params )
    {
        return createServicesUrl( handler, operation, null, params );
    }

    public String createServicesUrl( String handler, String operation, String redirect, String[] params )
    {
        if ( context.getPortalInstanceKey().getMenuItemKey() == null )
        {
            final String failureReason = "createServicesUrl must be called in context of a menuitem or window";
            throw new PortalFunctionException( failureReason );
        }

        SitePath sitePath = new SitePath( context.getSite().getKey(), ReservedLocalPaths.PATH_USERSERVICES );
        StringBuffer handlerAndOperationPath = new StringBuffer();
        handlerAndOperationPath.append( handler );
        handlerAndOperationPath.append( "/" ).append( operation );
        sitePath = sitePath.appendPath( new Path( handlerAndOperationPath.toString() ) );
        sitePath.addParam( "_instanceKey", UrlPathEncoder.encode( context.getPortalInstanceKey().toString() ) );

        if ( StringUtils.isNotEmpty( redirect ) )
        {
            sitePath.addParam( "_redirect", UrlPathEncoder.encode( redirect ) );
            //addRedirectParameter( redirect, sitePath );
        }

        sitePath.addParam( Ticket.getParameterName(), Ticket.getPlaceholder() );
        addParamsToSitePath( params, sitePath );
        return siteURLResolver.createUrl( request, sitePath, true );
    }


    public String createBinaryUrl( BinaryDataKey binaryDataKey )
    {
        return createBinaryUrl( binaryDataKey, null );
    }

    public String createBinaryUrl( final BinaryDataKey binaryDataKey, final String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "binary", binaryDataKey.toString(), params );
        }

        ContentBinaryDataEntity contentBinaryData = contentBinaryDataDao.findByBinaryKey( binaryDataKey.toInt() );
        if ( contentBinaryData == null )
        {
            final String failureReason = "content binary data for binary does not exist: " + binaryDataKey;
            throw new PortalFunctionException( failureReason );
        }

        final ContentKey contentKey = contentBinaryData.getContentVersion().getContent().getKey();
        final AttachmentNativeLinkKeyWithBinaryKey nativeLinkKey = new AttachmentNativeLinkKeyWithBinaryKey( contentKey, binaryDataKey );

        SitePath sitePath = new SitePath( context.getSite().getKey(),
                                          new Path( ReservedLocalPaths.PATH_ATTACHMENT + "/" + nativeLinkKey.asUrlRepresentation() ) );

        addParamsToSitePath( params, sitePath );
        return siteURLResolver.createUrl( request, sitePath, true );
    }

    public String createAttachmentUrl( String nativeLinkKey )
    {
        return createAttachmentUrl( nativeLinkKey, null );
    }

    public String createAttachmentUrl( String nativeLinkKey, String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "attachment", nativeLinkKey, params );
        }

        MenuItemKey requestedMenuItemKey = getRequestedMenuItemKey();

        CreateAttachmentUrlInstruction instruction =
            createAttachmentUrlFunction.createAttachmentUrl( nativeLinkKey, params, requestedMenuItemKey );
        return serializePostProcessInstruction( instruction );
    }

    private MenuItemKey getRequestedMenuItemKey()
    {
        MenuItemKey menuItemKey = null;

        MenuItemEntity currentMenuItem = context.getMenuItem();

        if ( currentMenuItem != null )
        {
            menuItemKey = currentMenuItem.getMenuItemKey();
        }
        return menuItemKey;
    }

    public String createImageUrl( final String key )
    {
        return doCreateImageUrl( key, null, null, null, null );
    }

    public String createImageUrl( final String key, final String filter )
    {
        return doCreateImageUrl( key, filter, null, null, null );
    }

    public String createImageUrl( String key, String filter, String background )
    {
        return doCreateImageUrl( key, filter, background, null, null );
    }

    public String createImageUrl( String key, String filter, String background, String format )
    {
        return doCreateImageUrl( key, filter, background, format, null );
    }

    public String createImageUrl( final String key, final String filter, final String background, final String format,
                                  final String quality )
    {
        return doCreateImageUrl( key, filter, background, format, quality );
    }

    private String doCreateImageUrl( final String key, final String filter, final String background, final String format,
                                     final String quality )
    {
        verifyImageKey( key );

        CreateImageUrlInstruction instruction = new CreateImageUrlInstruction();
        instruction.setKey( key );
        instruction.setFilter( filter );
        instruction.setFormat( format );
        instruction.setQuality( quality );
        instruction.setBackground( background );

        MenuItemKey requestedMenuItemKey = getRequestedMenuItemKey();

        if ( requestedMenuItemKey != null )
        {
            instruction.setRequestedMenuItemKey( requestedMenuItemKey.toString() );
        }

        return serializePostProcessInstruction( instruction );
    }

    private void verifyImageKey( String imageKey )
    {
        if ( StringUtils.isBlank( imageKey ) )
        {
            throw new PortalFunctionException( "Imagekey can not be empty" );
        }

        Matcher m = IMAGE_KEY_FORMAT.matcher( imageKey );

        if ( !m.matches() )
        {
            throw new PortalFunctionException( "Invalid imagekey: " + imageKey );
        }
    }

    public String createResourceUrl( String pathAsString )
    {
        return createResourceUrl( pathAsString, null );
    }

    public String createResourceUrl( String pathAsString, String[] params )
    {
        if ( encodeURIs )
        {
            return encodeURI( "resource", pathAsString, params );
        }

        if ( StringUtils.isBlank( pathAsString ) )
        {
            throw new PortalFunctionException( "Path is blank" );
        }

        String homeDir = resolvePathToPublicHome();

        boolean isTildePath = pathAsString.startsWith( "~" );
        if ( isTildePath && homeDir == null )
        {
            final String failureReason = "Cannot use ~ paths when no public home dir set for site: " + pathAsString;
            throw new PortalFunctionException( failureReason );
        }
        if ( isTildePath && !homeDir.startsWith( RESOURCE_PATH_PUBLIC_ROOT ) )
        {
            final String failureReason = "Public home does not start with " + RESOURCE_PATH_PUBLIC_ROOT + ": " + homeDir;
            throw new PortalFunctionException( failureReason );
        }

        String resolvedPath = pathAsString;
        if ( isTildePath )
        {
            resolvedPath = pathAsString.replaceFirst( "~", homeDir );
        }

        if ( !resolvedPath.startsWith( RESOURCE_PATH_PUBLIC_ROOT ) )
        {
            final String failureReason = "Path does not start with " + RESOURCE_PATH_PUBLIC_ROOT + ": " + pathAsString;
            throw new PortalFunctionException( failureReason );
        }

        CreateResourceUrlInstruction instruction = new CreateResourceUrlInstruction();
        instruction.setResolvedPath( resolvedPath );
        instruction.setParams( params );

        return serializePostProcessInstruction( instruction );
    }

    private String serializePostProcessInstruction( PostProcessInstruction instruction )
    {
        String serializedInstruction;

        try
        {
            serializedInstruction = PostProcessInstructionSerializer.serialize( instruction );
        }
        catch ( IOException e )
        {
            String failureReason = "Creation of post-process-instruction failed";
            throw new PortalFunctionException( failureReason, e );
        }

        return serializedInstruction;
    }


    public String createCaptchaImageUrl()
    {
        SitePath captchaSitePath = new SitePath( context.getSite().getKey(), new Path( CAPTCHA_RELATIVE_URL ) );
        return siteURLResolver.createUrl( request, captchaSitePath, true );
    }

    public String createCaptchaFormInputName()
    {
        return CaptchaService.FORM_VARIABLE_CAPTCHA_RESPONSE;
    }

    public boolean isCaptchaEnabled( String handler, String operation )
    {
        SiteKey siteKey = context.getSite().getKey();
        return captchaService.hasCaptchaCheck( siteKey, handler, operation );
    }

    public String localize( String phrase )
    {
        return localizeService.getLocalizedPhrase( context.getSite(), phrase, context.getLocale() );
    }

    public String localize( String phrase, String[] params )
    {
        return localizeService.getLocalizedPhrase( context.getSite(), phrase, params, context.getLocale() );
    }

    public String localize( String phrase, String[] params, String locale )
    {
        Locale parsedLocale = LocalizationResourceBundleUtils.parseLocaleString( locale );

        return localizeService.getLocalizedPhrase( context.getSite(), phrase, params, parsedLocale );
    }

    public String getLocale()
    {
        ResolverContext resolverContext = new ResolverContext( request, context.getSite(), context.getMenuItem(), null );

        Locale locale = localeResolverService.getLocale( resolverContext );
        if ( locale == null )
        {
            return null;
        }

        return locale.toString();
    }

    public String getPageKey()
    {
        MenuItemKey menuItemKey = context.getPortalInstanceKey().getMenuItemKey();

        if ( menuItemKey == null )
        {
            final String failureReason = "pageKey is not available on site context";
            throw new PortalFunctionException( failureReason );
        }
        return menuItemKey.toString();

    }

    public String createWindowPlaceholder( final String portletWindowKey, final String[] params )
    {
        RenderWindowInstruction instruction = new RenderWindowInstruction();
        instruction.setPortletWindowKey( portletWindowKey );
        instruction.setParams( params );

        return serializePostProcessInstruction( instruction );
    }

    public Boolean imageExists( String key )
    {
        ImageRequestParser parser = new ImageRequestParser();
        ImageRequest request = parser.parse( "_image/" + key, null, false );
        request.setRequester( securityService.getRunAsUser() );
        request.setRequestDateTime( new DateTime() );
        return imageService.canAccess( request );
    }

    private void addParamsToSitePath( String[] params, SitePath sitePath )
    {
        if ( params != null )
        {
            for ( int i = 0; i < params.length / 2; i++ )
            {
                String name = params[i * 2];
                String value = params[i * 2 + 1];
                sitePath.addParam( UrlPathEncoder.encode( name ), UrlPathEncoder.encode( value == null ? "" : value ) );
            }
        }
    }

    /**
     * This is used to create a parseable "native" url format. These URLs are interpreted by remote clients. Typically "Enonic web clipping
     * porlet (JSR 168)"
     */
    private String encodeURI( String type, String key, String[] params )
    {
        StringBuffer encodedURI = new StringBuffer( "{cmsurl:" );
        encodedURI.append( type );
        encodedURI.append( "-" );
        encodedURI.append( key );

        if ( params != null )
        {
            encodedURI.append( "?" );
            for ( int i = 0; i < params.length / 2; i++ )
            {
                String name = params[i * 2];
                String value = params[i * 2 + 1];
                encodedURI.append( name );
                encodedURI.append( "=" );
                encodedURI.append( value );

                if ( i < ( params.length / 2 ) - 1 )
                {
                    encodedURI.append( "&" );
                }
            }
        }

        encodedURI.append( "}" );

        return encodedURI.toString();
    }

    private String resolvePathToPublicHome()
    {
        ResourceKey publicPath = context.getSite().getPathToPublicResources();
        if ( publicPath != null )
        {
            String asString = publicPath.toString().trim();
            if ( asString.length() == 0 )
            {
                return null;
            }

            return asString;
        }
        return null;
    }

    public void setEncodeURIs( boolean value )
    {
        this.encodeURIs = value;
    }

    public void setSiteURLResolver( SiteURLResolver value )
    {
        this.siteURLResolver = value;
    }

    public void setRequest( HttpServletRequest value )
    {
        this.request = value;
    }

    public void setCaptchaService( CaptchaService captchaService )
    {
        this.captchaService = captchaService;
    }

    public void setLocalizeService( LocalizationService localizeService )
    {
        this.localizeService = localizeService;
    }

    public void setLocaleResolvingService( LocaleResolverService localeResolverService )
    {
        this.localeResolverService = localeResolverService;
    }

    public void setContentDao( ContentDao value )
    {
        this.contentDao = value;
    }

    public void setMenuItemDao( MenuItemDao value )
    {
        this.menuItemDao = value;
    }

    public void setPortletDao( PortletDao portletDao )
    {
        this.portletDao = portletDao;
    }

    public void setContext( PortalFunctionsContext context )
    {
        this.context = context;
    }

    public void setContentBinaryDataDao( ContentBinaryDataDao contentBinaryDataDao )
    {
        this.contentBinaryDataDao = contentBinaryDataDao;
    }

    public void setImageService( ImageService imageService )
    {
        this.imageService = imageService;
    }

    public void setSecurityService( SecurityService securityService )
    {
        this.securityService = securityService;
    }

    public void setCreateAttachmentUrlFunction( CreateAttachmentUrlFunction value )
    {
        this.createAttachmentUrlFunction = value;
    }

    public void setSitePropertiesService( SitePropertiesService sitePropertiesService )
    {
        this.sitePropertiesService = sitePropertiesService;
    }
}
