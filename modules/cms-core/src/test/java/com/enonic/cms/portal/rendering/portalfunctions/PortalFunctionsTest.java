/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.enonic.cms.core.resource.ResourceService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.MenuItemDao;

import com.enonic.cms.business.MockSitePropertiesService;
import com.enonic.cms.business.SitePropertyNames;
import com.enonic.cms.business.SiteURLResolver;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.portal.PortalInstanceKey;
import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.portal.instruction.CreateAttachmentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateContentUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateImageUrlInstruction;
import com.enonic.cms.domain.portal.instruction.CreateResourceUrlInstruction;
import com.enonic.cms.domain.portal.instruction.PostProcessInstructionSerializer;
import com.enonic.cms.domain.portal.instruction.RenderWindowInstruction;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.ContentHomeEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.domain.structure.menuitem.section.SectionContentKey;
import com.enonic.cms.domain.structure.portlet.PortletKey;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PortalFunctionsTest
{
    private PortalFunctions portalFunctions = new PortalFunctions();

    private MenuItemDao menuItemDao;

    private ContentDao contentDao;

    private ResourceService resourceService;

    private MockHttpServletRequest request;

    private SiteKey siteKey1 = new SiteKey( 1 );

    private SiteEntity site1;

    private SiteKey siteKey2 = new SiteKey( 2 );

    private SiteEntity site2;

    private PortalFunctionsContext context;

    private ContentBinaryDataDao contentBinaryDataDao;

    private static final String HOME_DIR = "/_public/mysite";

    @Before
    public void setUp()
    {
        request = new MockHttpServletRequest();
        request.setRequestURI( "/site/" + siteKey1.toString() + "/" );
        ServletRequestAccessor.setRequest( request );

        portalFunctions.setRequest( request );

        MockSitePropertiesService sitePropertiesService = new MockSitePropertiesService();
        sitePropertiesService.setProperty( siteKey1, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        sitePropertiesService.setProperty( siteKey2, SitePropertyNames.URL_DEFAULT_CHARACTER_ENCODING, "UTF-8" );
        sitePropertiesService.setProperty( siteKey2, SitePropertyNames.SITE_URL, "http://site2.com/" );
        SiteURLResolver siteURLResolver = new SiteURLResolver();
        siteURLResolver.setSitePropertiesService( sitePropertiesService );
        portalFunctions.setSitePropertiesService( sitePropertiesService );

        menuItemDao = mock( MenuItemDao.class );
        contentDao = mock( ContentDao.class );
        resourceService = mock( ResourceService.class );
        contentBinaryDataDao = mock( ContentBinaryDataDao.class );

        site1 = new SiteEntity();
        site1.setKey( siteKey1.toInt() );
        site1.setPathToPublicResources( new ResourceKey( HOME_DIR ) );

        site2 = new SiteEntity();
        site2.setKey( siteKey2.toInt() );
        site2.setPathToPublicResources( new ResourceKey( HOME_DIR ) );

        context = new PortalFunctionsContext();
        context.setSite( site1 );

        CreateAttachmentUrlFunction createAttachmentUrlFunction = new CreateAttachmentUrlFunction();
        createAttachmentUrlFunction.setContentDao( contentDao );

        portalFunctions.setSiteURLResolver( siteURLResolver );
        portalFunctions.setMenuItemDao( menuItemDao );
        portalFunctions.setContentDao( contentDao );
        portalFunctions.setContext( context );
        portalFunctions.setContentBinaryDataDao( contentBinaryDataDao );
        portalFunctions.setCreateAttachmentUrlFunction( createAttachmentUrlFunction );
    }

    @Test
    public void testGetInstanceKeyWhenSite()
    {
        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createSite( new SiteKey( 9 ) );
        context.setPortalInstanceKey( portalInstanceKey );

        assertEquals( "SITE:9", portalFunctions.getInstanceKey() );
    }

    @Test
    public void testGetInstanceKeyWhenPage()
    {
        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createPage( new MenuItemKey( 178 ) );
        context.setPortalInstanceKey( portalInstanceKey );

        assertEquals( "PAGE:178", portalFunctions.getInstanceKey() );
    }

    @Test
    public void testGetInstanceKeyWhenWindow()
    {
        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createWindow( new MenuItemKey( 178 ), new PortletKey( 93 ) );
        context.setPortalInstanceKey( portalInstanceKey );

        assertEquals( "WINDOW:178:93", portalFunctions.getInstanceKey() );
    }

    @Test
    public void testCreateUrl()
    {
        context.setSite( site1 );

        String url = portalFunctions.createUrl( null, null );
        assertEquals( "http://localhost/site/1", url );

        url = portalFunctions.createUrl( "", null );
        assertEquals( "http://localhost/site/1", url );

        url = portalFunctions.createUrl( "/", null );
        assertEquals( "http://localhost/site/1/", url );

        url = portalFunctions.createUrl( "/Frontpage", null );
        assertEquals( "http://localhost/site/1/Frontpage", url );

        url = portalFunctions.createUrl( "Frontpage", null );
        assertEquals( "http://localhost/site/1/Frontpage", url );

        url = portalFunctions.createUrl( "Frontpage/news", null );
        assertEquals( "http://localhost/site/1/Frontpage/news", url );

        url = portalFunctions.createUrl( "/Frontpage/", null );
        assertEquals( "http://localhost/site/1/Frontpage/", url );

        url = portalFunctions.createUrl( "/Frontpage/news", null );
        assertEquals( "http://localhost/site/1/Frontpage/news", url );

        url = portalFunctions.createUrl( "/Frontpage/news", new String[]{"en", "1"} );
        assertEquals( "http://localhost/site/1/Frontpage/news?en=1", url );

        url = portalFunctions.createUrl( "/Frontpage/news", new String[]{"en", "1", "to", "2"} );
        assertEquals( "http://localhost/site/1/Frontpage/news?en=1&to=2", url );

        url = portalFunctions.createUrl( "/Frontpage/news", new String[]{"en", "1", "to", "", "tre", "3"} );
        assertEquals( "http://localhost/site/1/Frontpage/news?en=1&to=&tre=3", url );

        url = portalFunctions.createUrl( "/Frontpage/news", new String[]{"en", "1", "to", null, "tre", "3"} );
        assertEquals( "http://localhost/site/1/Frontpage/news?en=1&to=&tre=3", url );
    }

    @Test
    public void testCreatePageUrlOnlyParams()
    {
        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        context.setOriginalSitePath( originalSitePath );

        context.setMenuItem( createMenuItem( "1", "Frontpage" ) );

        String url = portalFunctions.createPageUrl( null );
        assertEquals( "http://localhost/site/1/Frontpage", url );

        url = portalFunctions.createPageUrl( new String[]{"en", "1"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=1", url );

        originalSitePath.addParam( "balle", "rusk" );
        url = portalFunctions.createPageUrl( new String[]{"en", "1"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=1", url );

        url = portalFunctions.createPageUrl( new String[]{"en", "", "to", "2"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=&to=2", url );

        url = portalFunctions.createPageUrl( new String[]{"en", null, "to", "2"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=&to=2", url );
    }

    @Test
    public void testCreatePageUrlIllegalParamsSize()
    {
        final SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        context.setOriginalSitePath( originalSitePath );
        context.setMenuItem( createMenuItem( "0", "Frontpage" ) );

        try
        {
            portalFunctions.createPageUrl( new String[]{"en", "1", "to"} );
            fail( "Expected PortalFunctionException" );
        }
        catch ( PortalFunctionException e )
        {
            assertEquals( "Illegal parameter. Illegal params size: 3: en, 1, to", e.getMessage() );
        }
    }

    @Test
    public void testCreatePageUrlOnlyParamsExpectPageIdToPath()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/page" );
        originalSitePath.addParam( "id", "0" );
        context.setOriginalSitePath( originalSitePath );
        context.setMenuItem( createMenuItem( "0", "Frontpage" ) );

        when( menuItemDao.findByKey( 0 ) ).thenReturn( createMenuItem( "0", "/Frontpage", site1 ) );

        String url = portalFunctions.createPageUrl( null );
        assertEquals( "http://localhost/site/1/Frontpage", url );
    }

    @Test
    public void testCreatePageUrlOnlyParamsQueryParamsIncluded()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "include", "true" );
        context.setOriginalSitePath( originalSitePath );
        context.setMenuItem( createMenuItem( "1", "Frontpage" ) );

        String url = portalFunctions.createPageUrl( new String[]{"en", "1"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=1", url );
    }

    /**
     * Anders and Jørund found out that url parameters must override query parameters. A simple counter example, with links that increase
     * and decrease a counter passed as a parameter, proves why this behaviour is correct.
     */
    @Test
    public void testCreatePageUrlOnlyParamsGivenParamsOverrideQueryParams()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "en", "2" );
        context.setOriginalSitePath( originalSitePath );
        context.setMenuItem( createMenuItem( "1", "Frontpage" ) );

        String url = portalFunctions.createPageUrl( new String[]{"en", "1"} );
        assertEquals( "http://localhost/site/1/Frontpage?en=1", url );
    }

    @Test
    public void testCreatePageUrl()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        context.setOriginalSitePath( originalSitePath );

        MenuItemKey menuItemKey = new MenuItemKey( 101 );

        when( menuItemDao.findByKey( 101 ) ).thenReturn( createMenuItem( "101", "Frontpage", site1 ) );

        String url = portalFunctions.createPageUrl( menuItemKey, null );
        assertEquals( "http://localhost/site/1/Frontpage", url );
    }

    @Test
    public void testCreatePageUrlWhenPageNotFound()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );

        MenuItemKey menuItemKey = new MenuItemKey( 59 );

        when( menuItemDao.findByKey( 59 ) ).thenReturn( null );

        try
        {
            portalFunctions.createPageUrl( menuItemKey, new String[]{} );
            fail( "Expected PortalFunctionException" );
        }
        catch ( PortalFunctionException e )
        {
            assertEquals( "menuitem does not exist: 59", e.getFailureReason() );
        }

    }

    @Test
    public void testCreatePageUrlInContextOfADirectWindowRendering()
    {
        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage/_window/myportlet" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setMenuItem( createMenuItem( "1", "Frontpage", site1 ) );

        assertEquals( "http://localhost/site/1/Frontpage", portalFunctions.createPageUrl() );
        assertEquals( "http://localhost/site/1/Frontpage?param1=value1",
                      portalFunctions.createPageUrl( new String[]{"param1", "value1"} ) );
    }

    @Test
    public void testCreatePageUrlOtherSite()
    {
        SitePath currentSitePath = new SitePath( siteKey1, "/Frontpage" );
        context.setOriginalSitePath( currentSitePath );

        MenuItemKey menuItemKey = new MenuItemKey( 101 );

        when( menuItemDao.findByKey( 101 ) ).thenReturn( createMenuItem( "101", "Frontpage", site2 ) );

        String url = portalFunctions.createPageUrl( menuItemKey, null );
        assertEquals( "http://site2.com/Frontpage", url );
    }

    @Test
    public void testCreateResourceUrl()
        throws Exception
    {
        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );

        when( resourceService.getResourceFile( isA( ResourceKey.class ) ) ).thenReturn( null );

        String path = "/_public/shared/scripts/test.js";
        String[] params = null;
        String url = portalFunctions.createResourceUrl( resolvePath( path ) );
        CreateResourceUrlInstruction instruction = createCreateResourceUrlInstruction( path, params );
        assertEquals( url, PostProcessInstructionSerializer.serialize( instruction ) );

        path = "/_public/shared/scripts/test.js";
        params = new String[]{"a", "b", "c", "d"};
        url = portalFunctions.createResourceUrl( resolvePath( path ), params );
        instruction = createCreateResourceUrlInstruction( path, params );
        assertEquals( url, PostProcessInstructionSerializer.serialize( instruction ) );

        path = "~/images/test.gif";
        params = null;
        url = portalFunctions.createResourceUrl( path, params );
        instruction = createCreateResourceUrlInstruction( resolvePath( path ), params );
        assertEquals( url, PostProcessInstructionSerializer.serialize( instruction ) );

        path = "~/images/test.gif";
        params = new String[]{"a", "b", "c", "d"};
        url = portalFunctions.createResourceUrl( path, params );
        instruction = createCreateResourceUrlInstruction( resolvePath( path ), params );
        assertEquals( url, PostProcessInstructionSerializer.serialize( instruction ) );

        try
        {
            portalFunctions.createResourceUrl( "/illegal_path/image/test.gif" );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Path does not start with /_public: /illegal_path/image/test.gif", e.getMessage() );
        }

        try
        {
            portalFunctions.createResourceUrl( "/illegal_path/image/test.gif", new String[]{"a", "b", "c", "d"} );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Path does not start with /_public: /illegal_path/image/test.gif", e.getMessage() );
        }

    }

    @Test
    public void testCreateAttachmentUrl_noKey()
        throws Exception
    {
        try
        {
            portalFunctions.createAttachmentUrl( "" );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Invalid Attachment Key '': Path is empty", e.getMessage() );
        }
    }

    @Test
    public void testCreateAttachmentUrl_contentKey()
        throws Exception
    {
        String[] params = null;

        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.FILE.getHandlerClassShortName() );
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );

        CategoryEntity category = new CategoryEntity();
        category.setContentType( contentType );

        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setCategory( category );

        injectContentToContentDao( new ContentKey( 1 ), contentEntity );

        String url = portalFunctions.createAttachmentUrl( "1" );

        CreateAttachmentUrlInstruction expectedInstruction = new CreateAttachmentUrlInstruction();
        expectedInstruction.setNativeLinkKey( "1" );
        expectedInstruction.setParams( params );

        assertEquals( PostProcessInstructionSerializer.serialize( expectedInstruction ), url );
    }


    @Test
    public void testCreateAttachmentUrl_labelKey()
        throws Exception
    {
        String[] params = null;

        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.FILE.getHandlerClassShortName() );
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );

        CategoryEntity category = new CategoryEntity();
        category.setContentType( contentType );

        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setCategory( category );

        injectContentToContentDao( new ContentKey( 1 ), contentEntity );

        String url = portalFunctions.createAttachmentUrl( "1/label/large" );

        CreateAttachmentUrlInstruction expectedInstruction = new CreateAttachmentUrlInstruction();
        expectedInstruction.setNativeLinkKey( "1/label/large" );
        expectedInstruction.setParams( params );

        assertEquals( PostProcessInstructionSerializer.serialize( expectedInstruction ), url );
    }

    private String resolvePath( String path )
    {
        return path.replaceFirst( "~", HOME_DIR );
    }

    private CreateResourceUrlInstruction createCreateResourceUrlInstruction( String path, String[] params )
    {
        CreateResourceUrlInstruction instruction = new CreateResourceUrlInstruction();
        instruction.setResolvedPath( path );
        instruction.setParams( params );
        return instruction;
    }

    @Test
    public void testCreateResourceUrlNoContentHome()
    {
        SiteKey siteKey2 = new SiteKey( 2 );
        SiteEntity siteWithoutContentHome = new SiteEntity();

        /* Setup portalFunction to use a site without content home */
        PortalFunctionsContext context = new PortalFunctionsContext();
        context.setSite( siteWithoutContentHome );
        portalFunctions.setContext( context );

        SitePath originalSitePath = new SitePath( siteKey2, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );

        try
        {
            portalFunctions.createResourceUrl( "~/images/test.gif" );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Cannot use ~ paths when no public home dir set for site: ~/images/test.gif", e.getMessage() );
        }

        try
        {
            portalFunctions.createResourceUrl( "~/images/test.gif", new String[]{"a", "b", "c", "d"} );
            fail();
        }
        catch ( Exception e )
        {
            assertEquals( "Cannot use ~ paths when no public home dir set for site: ~/images/test.gif", e.getMessage() );
        }
    }

    @Test
    public void testCreateBinaryUrl()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );

        BinaryDataKey binaryKey = new BinaryDataKey( 101 );

        ContentBinaryDataEntity binaryDataEntity = new ContentBinaryDataEntity();
        ContentVersionEntity versionEntity = new ContentVersionEntity();
        ContentEntity contentEntity = new ContentEntity();
        contentEntity.setKey( new ContentKey( 100 ) );
        versionEntity.setContent( contentEntity );
        binaryDataEntity.setContentVersion( versionEntity );

        when( contentBinaryDataDao.findByBinaryKey( 101 ) ).thenReturn( binaryDataEntity );

        String url = portalFunctions.createBinaryUrl( binaryKey, null );

        assertEquals( "http://localhost/site/1/_attachment/100/binary/101", url );
    }

    @Test
    public void testCreateServicesUrl()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setPortalInstanceKey( PortalInstanceKey.createPage( new MenuItemKey( 123 ) ) );

        String url = portalFunctions.createServicesUrl( "user", "login", null );
        assertEquals( "http://localhost/site/1/_services/user/login?_instanceKey=PAGE%3A123&_ticket=##ticket##", url );
    }

    @Test
    public void testCreateServicesUrl2()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setPortalInstanceKey( PortalInstanceKey.createWindow( new MenuItemKey( 123 ), new PortletKey( 101 ) ) );

        String url = portalFunctions.createServicesUrl( "user", "login", null );
        assertEquals( "http://localhost/site/1/_services/user/login?_instanceKey=WINDOW%3A123%3A101&_ticket=##ticket##", url );
    }

    @Test
    public void testCreateServicesUrl3()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setPortalInstanceKey( PortalInstanceKey.createWindow( new MenuItemKey( 123 ), new PortletKey( 101 ) ) );

        String url = portalFunctions.createServicesUrl( "user", "login", new String[]{"_redirect", "www.vg.no"} );
        assertEquals( "http://localhost/site/1/_services/user/login?_instanceKey=WINDOW%3A123%3A101&_redirect=www.vg.no&_ticket=##ticket##",
                      url );
    }

    @Test
    public void testCreateServicesUrl4()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setPortalInstanceKey( PortalInstanceKey.createWindow( new MenuItemKey( 123 ), new PortletKey( 101 ) ) );

        String url = portalFunctions.createServicesUrl( "user", "login", "www.vg.no", null );
        assertEquals( "http://localhost/site/1/_services/user/login?_instanceKey=WINDOW%3A123%3A101&_redirect=www.vg.no&_ticket=##ticket##",
                      url );
    }

    @Test
    public void testCreateServicesUrl5()
    {

        SitePath originalSitePath = new SitePath( siteKey1, "/Frontpage" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        context.setPortalInstanceKey( PortalInstanceKey.createWindow( new MenuItemKey( 123 ), new PortletKey( 101 ) ) );

        String url = portalFunctions.createServicesUrl( "user", "login", null, new String[]{"_redirect", "www.vg.no"} );
        assertEquals( "http://localhost/site/1/_services/user/login?_instanceKey=WINDOW%3A123%3A101&_redirect=www.vg.no&_ticket=##ticket##",
                      url );
    }

    @Test
    public void testBasePathOverriding()
    {
        SitePath originalSitePath = new SitePath( siteKey1, "/news" );
        originalSitePath.addParam( "dummy", "shall be ignored" );
        context.setOriginalSitePath( originalSitePath );
        request.setAttribute( Attribute.BASEPATH_OVERRIDE_ATTRIBUTE_NAME, "http://localhost:8080/cms-server/admin/preview/1/" );

        String url = portalFunctions.createUrl( "home", null );
        assertEquals( "http://localhost:8080/cms-server/admin/preview/1/home", url );
    }

    @Test
    public void testCreateContentUrl()
        throws Exception
    {
        SitePath originalSitePath = new SitePath( siteKey1, "/home" );
        context.setOriginalSitePath( originalSitePath );

        ContentKey contentKey = new ContentKey( 123 );
        ContentEntity content = createContent( "123", "Obama was elected president" );
        injectContentToContentDao( contentKey, content );

        CreateContentUrlInstruction instruction = new CreateContentUrlInstruction();
        instruction.setContentKey( contentKey.toString() );
        String url = portalFunctions.createContentUrl( contentKey, null );

        assertEquals( url, PostProcessInstructionSerializer.serialize( instruction ) );
    }

    private void injectContentToContentDao( ContentKey contentKey, ContentEntity content )
    {
        when( contentDao.findByKey( contentKey ) ).thenReturn( content );
    }

    @Test
    public void testCreateImageUrl()
        throws Exception
    {
        context.setEncodeImageUrlParams( false );

        CreateImageUrlInstruction instruction = new CreateImageUrlInstruction();

        instruction.setKey( "123" );
        assertEquals( PostProcessInstructionSerializer.serialize( instruction ), portalFunctions.createImageUrl( "123" ) );

        instruction.setFilter( "scalemax(100)" );
        assertEquals( PostProcessInstructionSerializer.serialize( instruction ), portalFunctions.createImageUrl( "123", "scalemax(100)" ) );

        instruction.setBackground( "0xFFFFFF" );
        assertEquals( PostProcessInstructionSerializer.serialize( instruction ),
                      portalFunctions.createImageUrl( "123", "scalemax(100)", "0xFFFFFF" ) );

        instruction.setFormat( "jpg" );
        assertEquals( PostProcessInstructionSerializer.serialize( instruction ),
                      portalFunctions.createImageUrl( "123", "scalemax(100)", "0xFFFFFF", "jpg" ) );

        instruction.setQuality( "99" );
        assertEquals( PostProcessInstructionSerializer.serialize( instruction ),
                      portalFunctions.createImageUrl( "123", "scalemax(100)", "0xFFFFFF", "jpg", "99" ) );
    }

    @Test(expected = PortalFunctionException.class)
    public void testCreateImageUrl_emptyKey()
        throws Exception
    {
        context.setEncodeImageUrlParams( false );

        portalFunctions.createImageUrl( null );
    }


    @Test(expected = PortalFunctionException.class)
    public void testCreateImageUrl_bogusKey()
        throws Exception
    {
        context.setEncodeImageUrlParams( false );

        portalFunctions.createImageUrl( "ost" );
    }

    @Test
    public void testCreateImageUrl_userKey()
        throws Exception
    {
        context.setEncodeImageUrlParams( false );

        portalFunctions.createImageUrl( "user/THISISAUSERTYPEKEY1234" );
    }


    @Test
    public void getPageKey()
    {
        int key = 3;
        PortalInstanceKey portalInstanceKey = PortalInstanceKey.createPage( new MenuItemKey( key ) );
        context.setPortalInstanceKey( portalInstanceKey );

        assertEquals( "" + key, portalFunctions.getPageKey() );
    }

    @Test
    public void testCreateWindowPlaceHolder()
        throws IOException
    {
        RenderWindowInstruction instruction = new RenderWindowInstruction();
        instruction.setPortletWindowKey( "123:11" );

        assertEquals( PostProcessInstructionSerializer.serialize( instruction ),
                      portalFunctions.createWindowPlaceholder( "123:11", null ) );

        instruction.setParams( new String[]{"p1", "v1", "p2", "v2"} );

        assertEquals( PostProcessInstructionSerializer.serialize( instruction ),
                      portalFunctions.createWindowPlaceholder( "123:11", new String[]{"p1", "v1", "p2", "v2"} ) );

    }

    private SiteEntity createSite( SiteKey key, String name )
    {
        SiteEntity site = new SiteEntity();
        site.setKey( key.toInt() );
        site.setName( name );
        return site;
    }

    private MenuItemEntity createMenuItem( String key, String name )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( Integer.valueOf( key ) );
        menuItem.setName( name );
        return menuItem;
    }

    private MenuItemEntity createMenuItem( String key, String name, SiteEntity site )
    {
        return createMenuItem( key, name, null, site );
    }

    private MenuItemEntity createMenuItem( String key, String name, MenuItemEntity parent, SiteEntity site )
    {
        MenuItemEntity menuItem = new MenuItemEntity();
        menuItem.setKey( Integer.valueOf( key ) );
        menuItem.setName( name );
        menuItem.setSite( site );
        menuItem.setParent( parent );
        return menuItem;
    }

    private ContentEntity createContent( String contentKeyStr, String contentTitle )
    {
        ContentVersionEntity contentVersion = new ContentVersionEntity();
        contentVersion.setTitle( contentTitle );
        ContentEntity content = new ContentEntity();
        content.setName( PrettyPathNameCreator.generatePrettyPathName( contentTitle ) );
        content.setKey( new ContentKey( contentKeyStr ) );
        content.setMainVersion( contentVersion );
        return content;
    }

    private ContentHomeEntity createContentHome( SiteEntity site, MenuItemEntity menuItem )
    {
        ContentHomeEntity contentHome = new ContentHomeEntity();
        contentHome.setSite( site );
        contentHome.setMenuItem( menuItem );
        return contentHome;
    }

    private SectionContentEntity createApprovedSectionContent( MenuItemEntity menuItem, SectionContentKey sectionContentKey )
    {
        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( sectionContentKey );
        sectionContent.setMenuItem( menuItem );
        sectionContent.setOrder( Integer.valueOf( "1" ) );
        sectionContent.setApproved( true );
        return sectionContent;
    }
}
