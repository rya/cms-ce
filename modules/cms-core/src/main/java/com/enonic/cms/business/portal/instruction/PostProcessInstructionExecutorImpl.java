/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.instruction;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageRequestParams;
import com.enonic.cms.core.image.ImageRequestParser;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.dao.SectionContentDao;
import com.enonic.cms.store.resource.FileResourceService;

import com.enonic.cms.business.portal.image.ImageService;
import com.enonic.cms.business.portal.rendering.WindowRenderer;
import com.enonic.cms.business.portal.rendering.WindowRendererFactory;
import com.enonic.cms.business.portal.rendering.portalfunctions.PortalFunctionException;

import com.enonic.cms.domain.Path;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.binary.AttachmentNativeLinkKey;
import com.enonic.cms.domain.content.binary.AttachmentNativeLinkKeyParser;
import com.enonic.cms.domain.content.binary.AttachmentNativeLinkKeyWithBinaryKey;
import com.enonic.cms.domain.content.binary.AttachmentNativeLinkKeyWithLabel;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.nativelink.NativeLinkKey;
import com.enonic.cms.domain.portal.PathToContentResolver;
import com.enonic.cms.domain.portal.ReservedLocalPaths;
import com.enonic.cms.domain.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateImageUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionType;
import com.enonic.cms.domain.portal.instruction.RenderWindowInstruction;
import com.enonic.cms.domain.portal.rendering.RenderedWindowResult;
import com.enonic.cms.domain.resource.FileResource;
import com.enonic.cms.domain.resource.FileResourceName;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.page.WindowKey;

/**
 * Created by rmy - Date: Nov 19, 2009
 */
public class PostProcessInstructionExecutorImpl
    implements PostProcessInstructionExecutor
{
    private static final Logger LOG = LoggerFactory.getLogger( PostProcessInstructionExecutorImpl.class );

    public static final String UNDEFINED = "[undefined]";

    private static final String DEFAULT_FILE_FORMAT = "png";

    private static final String TIMESTAMP_PARAM_NAME = "_ts";

    private FileResourceService fileResourceService;

    private final ImageRequestParser requestParser = new ImageRequestParser();

    private ContentDao contentDao;

    private MenuItemDao menuItemDao;

    private ImageService imagesService;

    private WindowRendererFactory windowRendererFactory;

    private SectionContentDao sectionContentDao;

    public String execute( PostProcessInstruction instruction, PostProcessInstructionContext context )
    {
        PostProcessInstructionType type = instruction.getType();

        if ( type == PostProcessInstructionType.CREATE_WINDOWPLACEHOLDER && context.isInContextOfWindow() )
        {
            throw new IllegalArgumentException( "Not allowed to render portlet inside portlet" );
        }

        try
        {
            switch ( type )
            {
                case CREATE_WINDOWPLACEHOLDER:
                    return executeRenderWindowInstruction( (RenderWindowInstruction) instruction, context );
                case CREATE_RESOURCEURL:
                    return executeCreateResourceUrlInstruction( (CreateResourceUrlInstruction) instruction, context );
                case CREATE_ATTACHMENTURL:
                    return executeCreateAttachmentUrlInstruction( (CreateAttachmentUrlInstruction) instruction, context );
                case CREATE_IMAGEURL:
                    return executeCreateImageUrlInstruction( (CreateImageUrlInstruction) instruction, context );
                case CREATE_CONTENTURL:
                    return executeCreateContentUrlInstruction( (CreateContentUrlInstruction) instruction, context );
            }
        }
        catch ( Exception e )
        {
            return handleException( type.name(), e, context );
        }

        return null;
    }

    private String executeCreateContentUrlInstruction( CreateContentUrlInstruction instruction, PostProcessInstructionContext context )
    {
        final ContentKey contentKey = new ContentKey( instruction.getContentKey() );

        Path localPath;

        if ( instruction.isCreateAsPermalink() )
        {
            localPath = resolveContentPermalink( contentKey, context );
        }
        else
        {
            localPath = resolveContentUrlLocalPath( contentKey, context );
        }

        SitePath sitePath = new SitePath( context.getSite().getKey(), localPath );
        addParamsToSitePath( instruction.getParams(), sitePath );

        String result = doCreateUrl( instruction.doDisableOutputEscaping(), context, sitePath );

        return returnResult( instruction, result );
    }

    private String returnResult( PostProcessInstruction instruction, String result )
    {
        if ( instruction.doUrlEncodeResult() )
        {
            result = UrlPathEncoder.encode( result );
        }

        return result;
    }

    private Path resolveContentUrlLocalPath( ContentKey contentKey, PostProcessInstructionContext context )
    {
        ContentEntity content = resolveContent( contentKey, context );

        if ( content == null || content.isDeleted() )
        {
            return new Path( PathToContentResolver.CONTENT_PATH_SEPARATOR + contentKey );
        }

        PathToContentResolver pathToContentResolver = new PathToContentResolver( sectionContentDao );

        return pathToContentResolver.resolveContentUrlLocalPath( content, context.getSite().getKey() );
    }

    private Path resolveContentPermalink( ContentKey contentKey, PostProcessInstructionContext context )
    {
        ContentEntity content = resolveContent( contentKey, context );

        if ( content == null || content.isDeleted() )
        {
            return new Path( PathToContentResolver.CONTENT_PATH_SEPARATOR + contentKey );
        }

        PathToContentResolver pathToContentResolver = new PathToContentResolver( sectionContentDao );
        return pathToContentResolver.resolveContentPermalink( content );
    }

    private String executeCreateResourceUrlInstruction( CreateResourceUrlInstruction instruction, PostProcessInstructionContext context )
    {
        String resolvedPath = instruction.getResolvedPath();
        String[] params = instruction.getParams();

        parseParamsForPostProcessInstructions( params, context );

        SitePath sitePath = new SitePath( context.getSite().getKey(), new Path( resolvedPath ) );
        addParamsToSitePath( params, sitePath );

        addTimestampParameterForResource( resolvedPath, sitePath );

        String result = doCreateUrl( instruction.doDisableOutputEscaping(), context, sitePath );

        return returnResult( instruction, result );
    }

    private String executeCreateAttachmentUrlInstruction( CreateAttachmentUrlInstruction instruction,
                                                          PostProcessInstructionContext context )
    {
        String[] params = instruction.getParams();
        String nativeLinkKey = instruction.getNativeLinkKey();
        Path nativeLinkAsPath = new Path( nativeLinkKey );
        parseParamsForPostProcessInstructions( params, context );

        AttachmentNativeLinkKey nativeKey = AttachmentNativeLinkKeyParser.parse( nativeLinkAsPath );

        ContentEntity content = resolveContent( nativeKey.getContentKey(), context );

        BinaryDataEntity binaryData = findBinaryData( nativeKey, content );

        String menuItemPath = getMenuItemPath( instruction.getRequestedMenuItemKey() );

        String resolvedPath = menuItemPath + ReservedLocalPaths.PATH_ATTACHMENT + "/" + nativeKey.asUrlRepresentation();

        SitePath sitePath = new SitePath( context.getSite().getKey(), new Path( resolvedPath ) );

        addParamsToSitePath( params, sitePath );
        addTimestampParamForBinary( binaryData, sitePath );

        String result = doCreateUrl( instruction.doDisableOutputEscaping(), context, sitePath );

        return returnResult( instruction, result );
    }

    private String getMenuItemPath( String menuItemKey )
    {
        String menuItemPath = "";

        if ( StringUtils.isNotEmpty( menuItemKey ) )
        {
            MenuItemEntity menuItem = menuItemDao.findByKey( new MenuItemKey( menuItemKey ) );

            if ( menuItem != null )
            {
                menuItemPath = menuItem.getPathAsString() + "/";
            }
        }

        return menuItemPath;
    }

    private String executeCreateImageUrlInstruction( CreateImageUrlInstruction instruction, PostProcessInstructionContext context )
    {
        String key = instruction.getKey();
        String format = instruction.getFormat();
        String filter = instruction.getFilter();
        String background = instruction.getBackground();
        String quality = instruction.getQuality();

        String name = ensureFormatExtension( key, format );

        String menuItemPath = getMenuItemPath( instruction.getRequestedMenuItemKey() );

        String resolvedPath = menuItemPath + ReservedLocalPaths.PATH_IMAGE + "/" + name;

        final SitePath sitePath = new SitePath( context.getSite().getKey(), resolvedPath );

        final ImageRequestParams params = new ImageRequestParams();
        params.setFilter( filter );
        params.setBackgroundColor( background );
        params.setQuality( quality );

        sitePath.addParams( params.getParams( context.doEncodeImageUrlParams() ) );

        ImageRequest imageRequest = requestParser.parse( sitePath, context.doEncodeImageUrlParams() );
        Long imageTimestamp = imagesService.getImageTimestamp( imageRequest );

        if ( imageTimestamp != null )
        {
            addTimeStampParameter( imageTimestamp, sitePath );
        }

        String result = doCreateUrl( instruction.doDisableOutputEscaping(), context, sitePath );

        return returnResult( instruction, result );
    }

    private String executeRenderWindowInstruction( RenderWindowInstruction instruction, PostProcessInstructionContext context )
    {
        WindowKey portletWindowKey = new WindowKey( instruction.getPortletWindowKey() );

        String[] params = instruction.getParams();

        parseParamsForPostProcessInstructions( params, context );

        HashMap<String, String> map = createParamsMap( params );

        WindowRenderer windowRenderer = windowRendererFactory.createPortletRenderer( context.getWindowRendererContext() );

        RequestParameters portletParams = new RequestParameters();

        for ( Map.Entry<String, String> entry : map.entrySet() )
        {
            portletParams.addParameterValue( entry.getKey(), entry.getValue() );
        }

        RenderedWindowResult renderedWindowResult = windowRenderer.renderWindowInline( portletWindowKey, portletParams );

        return renderedWindowResult.getContent();
    }

    private void parseParamsForPostProcessInstructions( String[] params, PostProcessInstructionContext context )
    {
        for ( int i = 0; i < params.length / 2; i++ )
        {
            String value = params[i * 2 + 1];

            if ( StringUtils.isNotBlank( value ) )
            {
                params[i * 2 + 1] = executePostProcessInstructionForParam( value, context );
            }
        }
    }

    private String executePostProcessInstructionForParam( String paramValue, PostProcessInstructionContext context )
    {
        PostProcessInstructionProcessor processor = new PostProcessInstructionProcessor( context, this );

        return processor.processInstructions( paramValue );
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

    private void addTimestampParameterForResource( String resourcePath, SitePath sitePath )
    {
        FileResourceName fileResourceName = new FileResourceName( resourcePath );

        FileResource fileResource = fileResourceService.getResource( fileResourceName );

        if ( fileResource != null )
        {
            Long timeStamp = fileResource.getLastModified().getMillis();
            addTimeStampParameter( timeStamp, sitePath );
        }
    }

    private void addTimestampParamForBinary( BinaryDataEntity binaryData, SitePath sitePath )
    {
        if ( binaryData != null )
        {
            addTimeStampParameter( binaryData.getCreatedAt().getTime(), sitePath );
        }
    }


    private void addTimeStampParameter( Long timeStamp, SitePath sitePath )
    {
        sitePath.addParam( UrlPathEncoder.encode( TIMESTAMP_PARAM_NAME ), UrlPathEncoder.encode( Long.toHexString( timeStamp ) ) );
    }


    private BinaryDataEntity findBinaryData( NativeLinkKey nativeKey, ContentEntity content )
    {
        BinaryDataEntity binaryData = null;
        if ( nativeKey instanceof AttachmentNativeLinkKeyWithBinaryKey )
        {
            AttachmentNativeLinkKeyWithBinaryKey binaryNativeLinkKey = (AttachmentNativeLinkKeyWithBinaryKey) nativeKey;
            binaryData = content.getMainVersion().getSingleBinaryDataFromKey( binaryNativeLinkKey.getBinaryKey() );
        }
        else if ( nativeKey instanceof AttachmentNativeLinkKeyWithLabel )
        {
            String label = ( (AttachmentNativeLinkKeyWithLabel) nativeKey ).getLabel();
            binaryData = content.getMainVersion().getSingleBinaryData( label );
        }
        else
        {
            binaryData = content.getMainVersion().getSourceBinaryData();
        }
        return binaryData;
    }


    private String ensureFormatExtension( String key, String format )
    {
        String name = key;
        if ( !StringUtils.isBlank( format ) )
        {
            name = name + "." + format;
        }
        else
        {
            name = name + "." + DEFAULT_FILE_FORMAT;
        }
        return name;
    }


    private HashMap<String, String> createParamsMap( String[] params )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( ( params != null ) && ( params.length > 0 ) )
        {
            for ( int i = 0; i < ( params.length / 2 ); i++ )
            {
                map.put( params[i * 2], params[i * 2 + 1] );
            }
        }
        return map;
    }

    private String doCreateUrl( boolean disableHtmlEscaping, PostProcessInstructionContext context, SitePath sitePath )
    {
        String createdUrl = "";

        if ( disableHtmlEscaping )
        {
            createdUrl = context.getSiteURLResolverDisableHtmlEscaping().createUrl( context.getHttpRequest(), sitePath, true );
        }
        else
        {
            createdUrl = context.getSiteURLResolverEnabledHtmlEscaping().createUrl( context.getHttpRequest(), sitePath, true );
        }

        return createdUrl;
    }

    private static String handleException( final String functioname, final Exception e, PostProcessInstructionContext context )
    {
        final String failureReason = resolveFailureReason( e );
        final String failureMessage = buildFailureMessage( functioname, failureReason, context );

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

    private static String buildFailureMessage( String functionName, String failureReason, PostProcessInstructionContext context )
    {

        StringBuffer message = new StringBuffer();
        message.append( "Failure calling function " ).append( functionName );

        SiteEntity site = context.getSite();
        if ( site != null )
        {
            message.append( " in site [" );
            message.append( site.getName() ).append( "]" );
        }

        if ( context.getWindowRendererContext() != null )
        {
            if ( context.getWindowRendererContext().getMenuItem() != null )
            {
                MenuItemEntity menuItem = context.getWindowRendererContext().getMenuItem();
                message.append( " in menu item [" ).append( menuItem.getMenuItemKey().toString() ).append( ": " ).append(
                    menuItem.getDisplayName() ).append( "]" );
            }
            if ( context.getWindowRendererContext().getPageTemplate() != null )
            {
                message.append( " using page template [" ).append( context.getWindowRendererContext().getPageTemplate().getName() ).append(
                    "]" );
            }
            if ( context.getWindowRendererContext().getOriginalUrl() != null )
            {
                message.append( " originated with url [" ).append( context.getWindowRendererContext().getOriginalUrl() ).append( "]" );
            }
        }

        message.append( ". Reason: " );
        message.append( failureReason );
        return message.toString();
    }

    private ContentEntity resolveContent( ContentKey contentKey, PostProcessInstructionContext context )
    {
        if ( context.getPreviewContext().isPreviewingContent() &&
            context.getPreviewContext().getContentPreviewContext().isContentPreviewed( contentKey ) )
        {
            return context.getPreviewContext().getContentPreviewContext().getContentPreviewed();
        }

        return contentDao.findByKey( contentKey );
    }

    @Autowired
    public void setMenuItemDao( MenuItemDao menuItemDao )
    {
        this.menuItemDao = menuItemDao;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setImagesService( ImageService imagesService )
    {
        this.imagesService = imagesService;
    }

    @Autowired
    public void setWindowRendererFactory( WindowRendererFactory windowRendererFactory )
    {
        this.windowRendererFactory = windowRendererFactory;
    }

    @Autowired
    public void setFileResourceService( FileResourceService fileResourceService )
    {
        this.fileResourceService = fileResourceService;
    }

    @Autowired
    public void setSectionContentDao( SectionContentDao sectionContentDao )
    {
        this.sectionContentDao = sectionContentDao;
    }
}
