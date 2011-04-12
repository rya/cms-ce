/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.resource.FileResource;
import com.enonic.cms.core.structure.menuitem.MenuItemKey;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.framework.util.UrlPathEncoder;

import com.enonic.cms.core.MockSitePropertiesService;
import com.enonic.cms.core.SitePropertyNames;
import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.vhost.VirtualHostHelper;
import com.enonic.cms.portal.image.ImageService;
import com.enonic.cms.portal.rendering.WindowRendererContext;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;
import com.enonic.cms.store.resource.FileResourceService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.resource.FileResourceName;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 20, 2009
 * Time: 10:03:44 AM
 */
public class PostProcessInstructionExecutorImplTest
{
    private static final Logger LOG = LoggerFactory.getLogger( PostProcessInstructionExecutorImplTest.class.getName() );

    private PostProcessInstructionExecutorImpl executor;

    private static final String HOME_DIR = "/_public/mysite";

    private String timeStamp;

    private String serverName;

    private Calendar now;

    private MockHttpServletRequest request;

    private PostProcessInstructionContext context;

    private static final String MOCK_PATH_TO_REQUESTED_MENUITEM = "path/to/menuitem";

    @Before
    public void setUp()
    {
        executor = new PostProcessInstructionExecutorImpl();

        serverName = "test.com";

        now = Calendar.getInstance();
        timeStamp = Long.toHexString( now.getTimeInMillis() );

        executor.setFileResourceService( setUpResourceServiceMock( now ) );
        executor.setMenuItemDao( setUpMenuItemDaoMock() );

        request = setUpMockRequest( serverName );
        ServletRequestAccessor.setRequest( request );
        context = setUpContext( request );

        // make sure site-path isn't applied in the final path
        VirtualHostHelper.setBasePath( request, "" );

        context.setSiteURLResolverEnableHtmlEscaping( setUpSiteResolverMock( true ) );
        context.setSiteURLResolverDisableHtmlEscaping( setUpSiteResolverMock( false ) );
    }

    @Test
    public void testCreateResourceUrl()
        throws Exception
    {
        String resolvedPath = "_public/test/path/test.css";
        CreateResourceUrlInstruction instruction = doCreateResourceUrlInstruction( resolvedPath, null, true );

        String result = executor.execute( instruction, context );
        assertEquals( "http://" + serverName + "/" + resolvedPath + "?_ts=" + timeStamp, result );

        resolvedPath = "_public/test/path/test.css";

        String[] params = {"a", "1", "b", "2"};
        instruction = doCreateResourceUrlInstruction( resolvedPath, params, true );

        result = executor.execute( instruction, context );

        LOG.info( result );

        verifyPath( result, resolvedPath, new String[]{"a", "b"}, null, true );
    }

    @Test
    public void testCreateResourceUrl_nested_function()
        throws Exception
    {
        ContentDao contentDao = setUpSingleContentData();
        executor.setContentDao( contentDao );

        String resolvedPath = "_public/test/path/test.css";

        CreateAttachmentUrlInstruction attachmentUrlInstruction = createCreateAttachmentUrlInstruction( "1/binary/1", true );
        String attachmentUrlResult = executor.execute( attachmentUrlInstruction, context );

        String[] params = {"a", PostProcessInstructionSerializer.serialize( attachmentUrlInstruction )};

        CreateResourceUrlInstruction instruction = doCreateResourceUrlInstruction( resolvedPath, params, true );
        String result = executor.execute( instruction, context );

        verifyPath( result, resolvedPath, new String[]{"a"}, null, true );

        assertTrue( "Should contain result of nested function", result.contains( UrlPathEncoder.encode( attachmentUrlResult ) ) );
    }

    @Test
    public void testCreateResourceUrl_outputEscaping()
        throws Exception
    {
        String resolvedPath = "_public/test/path/test.css";
        CreateResourceUrlInstruction instruction = doCreateResourceUrlInstruction( resolvedPath, null, false );

        String result = executor.execute( instruction, context );
        assertEquals( "http://" + serverName + "/" + resolvedPath + "?_ts=" + timeStamp, result );

        resolvedPath = "_public/test/path/test.css";

        String[] params = {"a", "1", "b", "2"};
        instruction = doCreateResourceUrlInstruction( resolvedPath, params, false );

        result = executor.execute( instruction, context );

        verifyPath( result, resolvedPath, new String[]{"a", "b"}, null, false );
    }


    private CreateResourceUrlInstruction doCreateResourceUrlInstruction( String resolvedPath, String[] params,
                                                                         boolean disableOutputEscaping )
    {
        CreateResourceUrlInstruction instruction = new CreateResourceUrlInstruction();
        instruction.setResolvedPath( resolvedPath );
        instruction.setParams( params );
        instruction.setDisableOutputEscaping( disableOutputEscaping );
        return instruction;
    }

    @Test
    public void testCreateContentUrl_noHome()
        throws Exception
    {
        ContentDao contentDao = setUpSingleContentData();
        executor.setContentDao( contentDao );

        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( "123" );

        String result = executor.execute( instruction, context );

        assertEquals( "http://" + serverName + "/" + "123" + "/contentName", result );
    }

    @Test
    public void testCreateAttachmentUrl_contentkey_binary_binarykey()
    {
        ContentDao contentDao = setUpSingleContentData();

        executor.setContentDao( contentDao );

        CreateAttachmentUrlInstruction instruction = createCreateAttachmentUrlInstruction( "1/binary/1", true );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_attachment/1/binary/1", null, null, true );
    }

    @Test
    public void testCreateAttachmentUrl_contentkey()
    {
        ContentDao contentDao = setUpSingleContentData();

        executor.setContentDao( contentDao );

        CreateAttachmentUrlInstruction instruction = createCreateAttachmentUrlInstruction( "1", true );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_attachment/1", null, null, true );
    }

    @Test
    public void testCreateAttachmentUrl_contentkey_label()
    {
        ContentDao contentDao = setUpContentData( new String[]{"small", "large"} );

        executor.setContentDao( contentDao );

        CreateAttachmentUrlInstruction instruction = createCreateAttachmentUrlInstruction( "1/label/large", true );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_attachment/1/label/large", null, null, true );
    }


    @Test
    public void testCreateAttachmentUrl_contentkey_label_not_found()
    {
        ContentDao contentDao = setUpContentData( new String[]{"dummy1", "dummy2"} );

        executor.setContentDao( contentDao );

        CreateAttachmentUrlInstruction instruction = createCreateAttachmentUrlInstruction( "1/label/large", true );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_attachment/1/label/large", null, new String[]{"_ts"}, true );
    }

    @Test
    public void testCreateAttachmentUrl_nested_function()
        throws Exception
    {
        String resolvedPath = "_public/test/path/test.css";
        CreateResourceUrlInstruction createResourceUrlInstruction = doCreateResourceUrlInstruction( resolvedPath, null, true );
        String createResourceResult = executor.execute( createResourceUrlInstruction, context );

        ContentDao contentDao = setUpSingleContentData();

        executor.setContentDao( contentDao );

        CreateAttachmentUrlInstruction instruction = createCreateAttachmentUrlInstruction( "1/binary/1", true );
        instruction.setParams( new String[]{"a", PostProcessInstructionSerializer.serialize( createResourceUrlInstruction )} );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_attachment/1/binary/1", null, null, true );
        assertTrue( "Should contain result of nested function", result.contains( UrlPathEncoder.encode( createResourceResult ) ) );
    }


    @Test
    public void testCreateImageUrl()
    {
        ImageService imageService = Mockito.mock( ImageService.class );
        Mockito.when( imageService.getImageTimestamp( Mockito.<ImageRequest>any() ) ).thenReturn( now.getTimeInMillis() );
        executor.setImagesService( imageService );

        CreateImageUrlInstruction instruction = new CreateImageUrlInstruction();
        instruction.setDisableOutputEscaping( true );

        String background = "0x00000";
        String type = "png";
        String filter = "rounded(10);";
        String key = "1";

        instruction.setBackground( background );
        instruction.setFilter( filter );
        instruction.setFormat( type );
        instruction.setKey( key );
        instruction.setRequestedMenuItemKey( "1" );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_image/1.png",
                    new String[]{"_ts", "_background", "_filter", "_quality"}, null, true );
    }


    @Test
    public void testCreateImageUrl_invalidImage()
    {
        ImageService imageService = Mockito.mock( ImageService.class );
        Mockito.when( imageService.getImageTimestamp( Mockito.<ImageRequest>any() ) ).thenReturn( null );
        executor.setImagesService( imageService );

        CreateImageUrlInstruction instruction = new CreateImageUrlInstruction();

        String background = "0x00000";
        String type = "png";
        String filter = "rounded(10);";
        String key = "1";

        instruction.setBackground( background );
        instruction.setFilter( filter );
        instruction.setFormat( type );
        instruction.setKey( key );
        instruction.setRequestedMenuItemKey( "1" );

        String result = executor.execute( instruction, context );

        verifyPath( result, MOCK_PATH_TO_REQUESTED_MENUITEM + "/" + "_image/1.png", null, new String[]{"_ts"}, true );
    }

    private ContentDao setUpSingleContentData()
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();

        BinaryDataEntity binaryData = new BinaryDataEntity();
        binaryData.setKey( 1 );
        binaryData.setCreatedAt( now.getTime() );

        ContentBinaryDataEntity contentBinaryDataEntity = new ContentBinaryDataEntity();
        contentBinaryDataEntity.setBinaryData( binaryData );

        contentVersion.addContentBinaryData( contentBinaryDataEntity );

        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( "123" ) );
        content.setName( "contentName" );
        content.setMainVersion( contentVersion );

        ContentDao contentDao = createContentDaoMock( content );
        return contentDao;
    }

    private ContentDao setUpContentData( String[] labels )
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();

        if ( labels != null && labels.length > 0 )
        {
            for ( int i = 0; i < labels.length; i++ )
            {
                BinaryDataEntity binaryData = new BinaryDataEntity();
                binaryData.setKey( i );
                binaryData.setCreatedAt( now.getTime() );

                ContentBinaryDataEntity contentBinaryDataEntity = new ContentBinaryDataEntity();
                contentBinaryDataEntity.setBinaryData( binaryData );

                contentBinaryDataEntity.setLabel( labels[i] );

                contentVersion.addContentBinaryData( contentBinaryDataEntity );
            }
        }

        ContentEntity content = new ContentEntity();
        content.setMainVersion( contentVersion );

        ContentDao contentDao = createContentDaoMock( content );
        return contentDao;
    }

    private ContentDao createContentDaoMock( ContentEntity content )
    {
        ContentDao contentDao = Mockito.mock( ContentDao.class );
        Mockito.when( contentDao.findByKey( Mockito.<ContentKey>any() ) ).thenReturn( content );
        return contentDao;
    }

    private CreateAttachmentUrlInstruction createCreateAttachmentUrlInstruction( String nativeLinkKey, boolean disableOutputEscaping )
    {
        CreateAttachmentUrlInstruction instruction = new CreateAttachmentUrlInstruction();
        instruction.setNativeLinkKey( nativeLinkKey );
        instruction.setDisableOutputEscaping( disableOutputEscaping );
        instruction.setRequestedMenuItemKey( "1" );

        return instruction;
    }

    private PostProcessInstructionContext setUpContext( MockHttpServletRequest request )
    {
        PostProcessInstructionContext context = new PostProcessInstructionContext();
        WindowRendererContext windowRendererContext = new WindowRendererContext();
        context.setHttpRequest( request );
        windowRendererContext.setHttpRequest( request );

        SiteEntity site = new SiteEntity();
        site.setKey( 1 );
        site.setPathToPublicResources( new ResourceKey( HOME_DIR ) );
        context.setSite( site );
        windowRendererContext.setSite( site );

        context.setWindowRendererContext( windowRendererContext );
        context.setPreviewContext( PreviewContext.NO_PREVIEW );
        return context;
    }

    private SiteURLResolver setUpSiteResolverMock( boolean htmlEscapeParameterAmps )
    {
        SiteKey siteKey = new SiteKey( 1 );

        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );
        siteURLResolver.setHtmlEscapeParameterAmps( htmlEscapeParameterAmps );
        return siteURLResolver;
    }

    private MockHttpServletRequest setUpMockRequest( String serverName )
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName( serverName );
        request.setRequestURI( "/site/0/" );
        return request;
    }

    private FileResourceService setUpResourceServiceMock( Calendar now )
    {
        FileResourceService resourceService = Mockito.mock( FileResourceService.class );

        FileResource fileResource = new FileResource( new FileResourceName( "test" ) );
        fileResource.setLastModified( new DateTime( now.getTimeInMillis() ) );

        Mockito.when( resourceService.getResource( Mockito.<FileResourceName>any() ) ).thenReturn( fileResource );

        return resourceService;
    }

    private MenuItemDao setUpMenuItemDaoMock()
    {
        MenuItemEntity mockMenuItemEntity = Mockito.mock( MenuItemEntity.class );
        Mockito.when( mockMenuItemEntity.getPathAsString() ).thenReturn( MOCK_PATH_TO_REQUESTED_MENUITEM );

        MenuItemDao menuItemDao = Mockito.mock( MenuItemDao.class );
        Mockito.when( menuItemDao.findByKey( Mockito.any( MenuItemKey.class ) ) ).thenReturn( mockMenuItemEntity );

        return menuItemDao;
    }

    private void verifyPath( String matchUrl, String expectedLocalPath, String[] expectedParams, String[] notExpectedParams,
                             boolean disableOutputEscaping )
    {
        String url;

        if ( matchUrl.contains( "?" ) )
        {
            url = matchUrl.substring( 0, matchUrl.indexOf( "?" ) );
        }
        else
        {
            url = matchUrl;
        }

        assertEquals( getServerPath( expectedLocalPath ), url );

        List<String> paramList = getParamList( matchUrl, disableOutputEscaping );

        if ( expectedParams != null )
        {
            for ( String expectedParam : expectedParams )
            {
                assertTrue( "Parameter '" + expectedParam + "' expected", paramList.contains( expectedParam ) );
            }
        }

        if ( notExpectedParams != null )
        {
            for ( String notExpectedParam : notExpectedParams )
            {
                assertFalse( "Parameter '" + notExpectedParam + "' not expected", paramList.contains( notExpectedParam ) );
            }
        }
    }

    private List<String> getParamList( String result, boolean disableOutputEscaping )
    {
        if ( !result.contains( "?" ) )
        {
            return new ArrayList<String>();
        }

        if ( !disableOutputEscaping )
        {
            result = StringUtils.replace( result, "&amp;", "&" );
        }

        List<String> paramsList = Arrays.asList( StringUtils.split( result.substring( result.indexOf( "?" ) + 1 ), "&" ) );

        List<String> normalizedParams = new ArrayList<String>();

        for ( String param : paramsList )
        {
            normalizedParams.add( param.substring( 0, param.indexOf( "=" ) ) );
        }
        return normalizedParams;
    }


    private String getServerPath( String localPath )
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "http://" );
        buffer.append( serverName );
        buffer.append( "/" );
        buffer.append( localPath );

        return buffer.toString();
    }
}
