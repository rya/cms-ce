/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.lib;

import com.enonic.cms.portal.rendering.portalfunctions.PortalFunctionException;
import com.enonic.cms.portal.rendering.portalfunctions.PortalFunctionsContext;
import com.enonic.cms.portal.rendering.portalfunctions.PortalFunctionsFactory;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.WindowKey;
import net.sf.saxon.om.Item;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portal xslt functions.
 */
public final class PortalFunctions
{
    public static final String NAMESPACE_URI = "http://www.enonic.com/cms/portal";

    public static final String OLD_NAMESPACE_URI = "http://www.enonic.com/cms/xslt/portal";

    public static final String UNDEFINED = "[undefined]";

    private static final Logger LOG = LoggerFactory.getLogger( PortalFunctions.class );

    public static String getInstanceKey()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().getInstanceKey();
        }
        catch ( Exception e )
        {
            return handleException( "getInstanceKey", e );
        }
    }

    public static Boolean isWindowInline()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().isWindowInline();
        }
        catch ( Exception e )
        {
            final String failureReason = resolveFailureReason( e );
            final String failureMessage = buildFailureMessage( "isWindowInline", failureReason );
            LOG.warn( failureMessage );
            return null;
        }
    }

    public static String getPageKey()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().getPageKey();
        }
        catch ( Exception e )
        {
            return handleException( "getPageKey", e );
        }
    }

    public static String getWindowKey()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().getWindowKey();
        }
        catch ( Exception e )
        {
            return handleException( "getWindowKey", e );
        }
    }

    public static String createWindowPlaceholder( final Object windowKey )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createWindowPlaceholder( toString( windowKey ), null );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowPlaceholder", e );
        }
    }

    public static String createWindowPlaceholder( final Object windowKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createWindowPlaceholder(
                    toString(windowKey), toStringArray(params) );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowPlaceholder", e );
        }
    }


    public static String createUrl( final Object local )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createUrl( toString( local ) );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowPlaceholder", e );
        }
    }

    public static String createUrl( final Object local, final Object[] params )
    {

        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createUrl(
                    toString(local), toStringArray(params) );
        }
        catch ( Exception e )
        {
            return handleException( "createUrl", e );
        }
    }

    public static String createWindowUrl()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createWindowUrl();
        }
        catch ( Exception e )
        {
            return handleException( "createWindowUrl", e );
        }
    }

    public static String createWindowUrl( final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createWindowUrl( toStringArray(params) );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowUrl", e );
        }
    }

    public static String createWindowUrl( final Object windowKey, final Object[] params )
    {
        try
        {
            final WindowKey windowKeyObj = new WindowKey( toString( windowKey ) );
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createWindowUrl( windowKeyObj, toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowUrl", e );
        }
    }

    public static String createWindowUrl( final Object windowKey, final Object[] params, final Object outputFormat )
    {
        try
        {
            final WindowKey windowKeyObj = new WindowKey( toString( windowKey ) );
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createWindowUrl( windowKeyObj, toStringArray( params ), toString( outputFormat ) );
        }
        catch ( Exception e )
        {
            return handleException( "createWindowUrl", e );
        }
    }

    public static String createPageUrl()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createPageUrl();
        }
        catch ( Exception e )
        {
            return handleException( "createPageUrl", e );
        }
    }

    public static String createPageUrl( final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createPageUrl( toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createPageUrl", e );
        }
    }

    public static String createPageUrl( final Object menuItemKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createPageUrl( new MenuItemKey( toString( menuItemKey ) ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createPageUrl", e );
        }
    }

    public static String createContentUrl( final Object contentKey )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createContentUrl( new ContentKey( toString( contentKey ) ) );
        }
        catch ( Exception e )
        {
            return handleException( "createContentUrl", e );
        }
    }

    public static String createContentUrl( final Object contentKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createContentUrl( new ContentKey( toString( contentKey ) ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createContentUrl", e );
        }
    }


    public static String createPermalink( final Object contentKey )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createPermalink( new ContentKey( toString( contentKey ) ) );
        }
        catch ( Exception e )
        {
            return handleException( "createPermalink", e );
        }
    }

    public static String createPermalink( final Object contentKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createPermalink( new ContentKey( toString( contentKey ) ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createPermalink", e );
        }
    }


    public static String createServicesUrl( final Object handler, final Object operation )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createServicesUrlWithoutParams( toString( handler ), toString( operation ) );
        }
        catch ( Exception e )
        {
            return handleException( "createServicesUrl", e );
        }
    }

    public static String createServicesUrl( final Object handler, final Object operation, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createServicesUrl( toString( handler ), toString( operation ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createServicesUrl", e );
        }
    }

    public static String createServicesUrl( final Object handler, final Object operation, final Object redirect,
                                            final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createServicesUrl( toString( handler ), toString( operation ), toString( redirect ),
                                        toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createServicesUrl", e );
        }
    }

    public static String createBinaryUrl( final Object binaryKey )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createBinaryUrl( new BinaryDataKey( toString( binaryKey ) ) );
        }
        catch ( Exception e )
        {
            return handleException( "createBinaryUrl", e );
        }
    }

    public static String createBinaryUrl( final Object binaryKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createBinaryUrl( new BinaryDataKey( toString( binaryKey ) ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createBinaryUrl", e );
        }
    }

    public static String createAttachmentUrl( final Object nativeLinkKey )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createAttachmentUrl( toString( nativeLinkKey ) );
        }
        catch ( Exception e )
        {
            return handleException( "createAttachmentUrl", e );
        }
    }

    public static String createAttachmentUrl( final Object nativeLinkKey, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createAttachmentUrl( toString( nativeLinkKey ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createAttachmentUrl", e );
        }
    }

    public static String createResourceUrl( final Object resourcePath )
    {
        final String resourcePathStr = toString( resourcePath );
        if ( resourcePathStr == null || resourcePathStr.length() == 0 )
        {
            final String failureReason = "resourcePath cannot be null";
            LOG.warn( buildFailureMessage( "createResourceUrl", failureReason ) );
            return UNDEFINED + ": " + failureReason;
        }

        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createResourceUrl( resourcePathStr );
        }
        catch ( Exception e )
        {
            return handleException( "createResourceUrl", e );
        }
    }

    public static String createResourceUrl( final Object resourcePath, final Object[] params )
    {
        final String resourcePathStr = toString( resourcePath );
        if ( resourcePathStr == null || resourcePathStr.length() == 0 )
        {
            final String failureReason = "resourcePath cannot be null";
            LOG.warn( buildFailureMessage( "createResourceUrl", failureReason ) );
            return UNDEFINED + ": " + failureReason;
        }

        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createResourceUrl( resourcePathStr, toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "createResourceUrl", e );
        }
    }


    public static String createCaptchaImageUrl()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createCaptchaImageUrl();
        }
        catch ( Exception e )
        {
            return handleException( "createCaptchaImageUrl", e );
        }
    }

    public static String createCaptchaFormInputName()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createCaptchaFormInputName();
        }
        catch ( Exception e )
        {
            return handleException( "createCaptchaFormInputName", e );
        }
    }

    public static String isCaptchaEnabled( final Object handler, final Object operation )
    {
        try
        {
            boolean captchaEnabled = PortalFunctionsFactory.get().createPortalFunctions()
                    .isCaptchaEnabled( toString( handler ), toString( operation ) );
            return captchaEnabled ? "true" : "false";
        }
        catch ( Exception e )
        {
            return handleException( "isCaptchaEnabled", e );
        }
    }

    public static String localize( final Object phrase )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().localize( toString( phrase ) );
        }
        catch ( Exception e )
        {
            return handleException( "localize", e );
        }
    }

    public static String localize( final Object phrase, final Object[] params )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .localize( toString( phrase ), toStringArray( params ) );
        }
        catch ( Exception e )
        {
            return handleException( "localize", e );
        }
    }

    public static String getLocale()
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().getLocale();
        }
        catch ( Exception e )
        {
            return handleException( "getLocale", e );
        }
    }

    public static String localize( final Object phrase, final Object[] params, final Object locale )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .localize( toString( phrase ), toStringArray( params ), toString( locale ) );
        }
        catch ( Exception e )
        {
            return handleException( "localize", e );
        }
    }

    public static String createImageUrl( final Object key )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().createImageUrl( toString( key ) );
        }
        catch ( Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    public static String createImageUrl( final Object key, final Object filter )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createImageUrl( toString( key ), toString( filter ) );
        }
        catch ( Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    public static String createImageUrl( final Object key, final Object filter, final Object background )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createImageUrl( toString( key ), toString( filter ), toString( background ) );
        }
        catch ( Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    public static String createImageUrl( final Object key, final Object filter, final Object background,
                                         final Object format )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createImageUrl( toString( key ), toString( filter ), toString( background ), toString( format ) );
        }
        catch ( Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    public static String createImageUrl( final Object key, final Object filter, final Object background,
                                         final Object format, final Object quality )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions()
                    .createImageUrl( toString( key ), toString( filter ), toString( background ),
                                     toString( format ), toString( quality ) );
        }
        catch ( Exception e )
        {
            return handleException( "createImageUrl", e );
        }
    }

    public static Boolean imageExists( final Object key )
    {
        try
        {
            return PortalFunctionsFactory.get().createPortalFunctions().imageExists( toString( key ) );
        }
        catch ( Exception e )
        {
            final String failureReason = resolveFailureReason( e );
            final String failureMessage = buildFailureMessage( "imageExists", failureReason );
            LOG.warn( failureMessage );
            return null;
        }
    }

    private static String handleException( final String functioname, final Exception e )
    {
        final String failureReason = resolveFailureReason( e );
        final String failureMessage = buildFailureMessage( functioname, failureReason );

        LOG.warn( failureMessage );
        return UNDEFINED + ": " + failureReason;
    }

    private static String resolveFailureReason( final Exception e )
    {
        final String failureReason;

        if ( e instanceof PortalFunctionException )
        {
            PortalFunctionException pfe = (PortalFunctionException) e;
            failureReason = pfe.getFailureReason();
        }
        else
        {
            failureReason = e.getMessage();
        }
        return failureReason;
    }

    private static String buildFailureMessage( String functionName, String failureReason )
    {
        final PortalFunctionsContext portalFunctionsContext = PortalFunctionsFactory.get().getContext();

        StringBuffer message = new StringBuffer();
        message.append( "Failure calling function " ).append( functionName );
        MenuItemEntity menuItem = portalFunctionsContext.getMenuItem();
        if ( menuItem != null )
        {
            message.append( " during request to [" );
            message.append( menuItem.getPathAsString() ).append( "]" );
        }
        SiteEntity site = portalFunctionsContext.getSite();
        if ( site != null )
        {
            message.append( " in site [" );
            message.append( site.getName() ).append( "]" );
        }

        message.append( ". Reason: " );
        message.append( failureReason );
        return message.toString();
    }

    public static String md5( final Object value )
    {
        return DigestUtils.md5Hex( toString( value ) );
    }

    public static String sha( final Object value )
    {
        return DigestUtils.shaHex( toString( value ) );
    }

    private static String[] toStringArray(final Object[] params)
    {
        final String[] strings = new String[params.length];
        for (int i = 0; i< strings.length; i++) {
            strings[i] = toString(params[i]);
        }

        return strings;
    }

    private static String toString(final Object value)
    {
        if (value instanceof Item) {
            return ((Item)value).getStringValue().trim();
        } else if (value != null) {
            return value.toString().trim();
        } else {
            return null;
        }
    }
}
