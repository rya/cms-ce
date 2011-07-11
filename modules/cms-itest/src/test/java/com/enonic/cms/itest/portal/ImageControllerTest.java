package com.enonic.cms.itest.portal;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import com.enonic.cms.framework.time.MockTimeService;

import com.enonic.cms.core.SitePathResolver;
import com.enonic.cms.core.SitePropertiesService;
import com.enonic.cms.core.client.InternalClientContentService;
import com.enonic.cms.core.content.ContentAndVersion;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.preview.ContentPreviewContext;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.AutoLoginService;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteService;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.portal.image.ImageService;
import com.enonic.cms.portal.mvc.controller.ImageController;
import com.enonic.cms.portal.mvc.controller.ImageRequestException;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.SiteDao;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
/**
 * TODO: Re-introduce ignores tests when createImageContent is available via either ContentService or InternalServiceImpl
 */
public class ImageControllerTest
{
    @Autowired
    protected HibernateTemplate hibernateTemplate;

    protected DomainFactory factory;

    protected DomainFixture fixture;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AutoLoginService autoLoginService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private InternalClientContentService internalClientContentService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private SitePropertiesService sitePropertiesService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SitePathResolver sitePathResolver;

    private PreviewService previewService;

    private ImageController imageController = new ImageController();

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

    private SiteEntity site1;


    @Before
    public void before()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        httpServletRequest.setCharacterEncoding( "UTF-8" );
        ServletRequestAccessor.setRequest( httpServletRequest );
        loginUserInPortal( fixture.findUserByName( "testuser" ).getKey() );

        imageController.setSecurityService( securityService );
        imageController.setAutoLoginService( autoLoginService );
        imageController.setContentDao( contentDao );
        imageController.setGroupDao( groupDao );
        imageController.setSiteDao( siteDao );
        imageController.setImageService( imageService );
        imageController.setDisableParamEncoding( true );
        imageController.setSitePathResolver( sitePathResolver );
        imageController.setSitePropertiesService( sitePropertiesService );
        imageController.setSiteService( siteService );

        previewService = Mockito.mock( PreviewService.class );
        Mockito.when( previewService.isInPreview() ).thenReturn( false );
        Mockito.when( previewService.getPreviewContext() ).thenReturn( PreviewContext.NO_PREVIEW );
        imageController.setPreviewService( previewService );

        MockTimeService timeService = new MockTimeService( new DateTime( 2011, 6, 27, 12, 0, 0, 0 ) );
        imageController.setTimeService( timeService );

        site1 = factory.createSite( "MySite", new Date(), null, "en" );
        fixture.save( site1 );
        MenuItemEntity firstPage = createPage( "Firstpage", null, "MySite" );
        fixture.save( firstPage );

        site1.setFirstPage( firstPage );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createContentHandler( "Image content", ContentHandlerName.IMAGE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "ImageContentType", ContentHandlerName.IMAGE.getHandlerClassShortName() ) );
        fixture.save( factory.createUnit( "ImageUnit" ) );
        fixture.save( factory.createCategory( "ImageCategory", "ImageContentType", "ImageUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "ImageCategory", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    @Ignore
    public void request_content_image_without_format_gives_png_as_content_type()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String imageRequestPath = "_image/" + contentKey.toString();
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( "image/png", httpServletResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    @Ignore
    public void request_content_image_with_format_gives_same_content_type()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String imageRequestPath = "_image/" + contentKey.toString() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( "image/jpg", httpServletResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    @Ignore
    public void request_content_image_with_label_small()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String imageRequestPath = "_image/" + contentKey.toString() + "/label/small";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( "image/png", httpServletResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
    }

    @Test
    public void request_content_image_that_does_not_exist()
        throws Exception
    {
        String imageRequestPath = "_image/123" + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains( "Resource '/_image/123.jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_content_image_with_label_that_does_not_exist()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String imageRequestPath = "_image/" + contentKey + "/label/nonexistinglabel.jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains(
                "Resource '/_image/" + contentKey + "/label/nonexistinglabel.jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_content_image_that_is_deleted()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );
        ContentEntity content = fixture.findContentByKey( contentKey );
        content.setDeleted( true );

        String imageRequestPath = "_image/" + contentKey + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains( "Resource '/_image/" + contentKey + ".jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_content_image_that_is_not_online()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

        String imageRequestPath = "_image/" + contentKey + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains( "Resource '/_image/" + contentKey + ".jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_content_image_with_no_access()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        fixture.createAndStoreUserAndUserGroup( "user-with-no-access", "testuser fullname", UserType.NORMAL, "testuserstore" );
        loginUserInPortal( fixture.findUserByName( "user-with-no-access" ).getKey() );

        String imageRequestPath = "_image/" + contentKey + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains( "Resource '/_image/" + contentKey + ".jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_content_image_that_binary_is_on_main_version()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        // setup: draft version of content
        ContentEntity content = fixture.findContentByKey( contentKey );

        BinaryDataEntity binaryDataOfMainVersion = content.getMainVersion().getBinaryData( "source" );

        // exercise & verify
        String imageRequestPath = "_image/" + contentKey + "/binary/" + binaryDataOfMainVersion.getKey() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );

        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
        assertEquals( "image/jpg", httpServletResponse.getContentType() );
    }

    @Test
    @Ignore
    public void exception_thrown_for_request_to_content_image_that_binary_is_not_on_main_version()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        // setup: draft version of content
        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionEntity draftVersion = createDraftVersion( "Arn.JPG" );
        draftVersion.setContentDataXml( content.getMainVersion().getContentDataAsXmlString() );
        content.setDraftVersion( draftVersion );
        content.addVersion( draftVersion );
        fixture.save( draftVersion );

        BinaryDataEntity binaryDataForDraftVersion = factory.createBinaryData( "Arn.JPG", bytes.length );
        binaryDataForDraftVersion.setBlobKey( content.getMainVersion().getBinaryData( "source" ).getBlobKey() );
        fixture.save( binaryDataForDraftVersion );

        ContentBinaryDataEntity contentBinaryDataForDraftVersion =
            factory.createContentBinaryData( "source", binaryDataForDraftVersion, draftVersion );
        draftVersion.addContentBinaryData( contentBinaryDataForDraftVersion );
        fixture.save( contentBinaryDataForDraftVersion );

        fixture.flushAndClearHibernateSesssion();

        // exercise & verify
        String imageRequestPath = "_image/" + contentKey + "/binary/" + binaryDataForDraftVersion.getKey() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_version", draftVersion.getKey().toString() );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );

        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            ImageRequestException imageRequestException = (ImageRequestException) e;
            assertTrue( imageRequestException.getMessage().contains(
                "Resource '/_image/" + contentKey + "/binary/" + binaryDataForDraftVersion.getKey() + ".jpg' not found" ) );
        }
    }

    @Test
    @Ignore
    public void request_user_image()
        throws Exception
    {
        byte[] bytes = loadImage( "Arn.JPG" );
        UserEntity testUser = fixture.findUserByName( "testuser" );
        testUser.setPhoto( bytes );

        fixture.flushAndClearHibernateSesssion();

        String imageRequestPath = "_image/user/" + testUser.getKey() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );
        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertEquals( "image/jpg", httpServletResponse.getContentType() );
    }

    @Test
    @Ignore
    public void response_ok_for_request_to_content_image_that_is_not_online_when_is_related_to_content_in_preview()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

        // setup: preview
        Mockito.when( previewService.isInPreview() ).thenReturn( true );

        ContentEntity contentPreviewed = new ContentEntity();

        ContentData contentData = Mockito.mock( ContentData.class );
        Mockito.when( contentData.resolveRelatedContentKeys() ).thenReturn( Sets.newHashSet( contentKey ) );
        ContentVersionEntity versionPreviewed = Mockito.mock( ContentVersionEntity.class );
        Mockito.when( versionPreviewed.getContentData() ).thenReturn( contentData );
        versionPreviewed.setContentData( contentData );
        ContentAndVersion contentAndVersionPreviewed = new ContentAndVersion( contentPreviewed, versionPreviewed );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionPreviewed );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );

        Mockito.when( previewService.getPreviewContext() ).thenReturn( previewContext );

        // exercise & verify
        String imageRequestPath = "_image/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );

        imageController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
    }

    @Test
    @Ignore
    public void exception_thrown_for_request_to_content_image_that_is_not_online_when_is_not_related_to_content_in_preview()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadImage( "Arn.JPG" );
        ContentKey contentKey =
            createImageContent( "MyImage.jpg", 2, bytes, "ImageCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

        // setup: preview
        Mockito.when( previewService.isInPreview() ).thenReturn( true );

        ContentEntity contentPreviewed = new ContentEntity();

        ContentData contentData = Mockito.mock( ContentData.class );
        Mockito.when( contentData.resolveRelatedContentKeys() ).thenReturn( Sets.newHashSet( new ContentKey( 666 ) ) );
        ContentVersionEntity versionPreviewed = Mockito.mock( ContentVersionEntity.class );
        Mockito.when( versionPreviewed.getContentData() ).thenReturn( contentData );
        versionPreviewed.setContentData( contentData );
        ContentAndVersion contentAndVersionPreviewed = new ContentAndVersion( contentPreviewed, versionPreviewed );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionPreviewed );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );

        Mockito.when( previewService.getPreviewContext() ).thenReturn( previewContext );

        // exercise & verify
        String imageRequestPath = "_image/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, imageRequestPath );
        httpServletRequest.setParameter( "_background", "0xffffff" );
        httpServletRequest.setParameter( "_quality", "100" );

        try
        {
            imageController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ImageRequestException );
            assertTrue( e.getMessage().contains( "Resource '/_image/" + contentKey + "' not found" ) );
        }
    }

    private MenuItemEntity createPage( String name, String parentName, String siteName )
    {
        return factory.createPageMenuItem( name, 0, name, name, siteName, "testuser", "testuser", false, false, "en", parentName, 0,
                                           new Date(), false, null );
    }

    private ContentKey createImageContent( String name, int contentStatus, byte[] bytes, String categoryName, DateTime availableFrom,
                                           DateTime availableTo )
        throws IOException
    {
        /*ImageContentDataInput imageContentDataInput = new ImageContentDataInput();
        imageContentDataInput.name = new ImageNameInput( name );
        imageContentDataInput.binary = new ImageBinaryInput( bytes, name );

        CreateImageContentParams params = new CreateImageContentParams();
        params.categoryKey = fixture.findCategoryByName( categoryName ).getKey().toInt();
        params.publishFrom = availableFrom != null ? availableFrom.toDate() : null;
        params.publishTo = availableTo != null ? availableTo.toDate() : null;
        params.status = contentStatus;
        params.contentData = imageContentDataInput;

        return new ContentKey( internalClientContentService.createImageContent( params ) ); */
        return null;
    }

    private byte[] loadImage( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( ImageControllerTest.class.getName().replace( ".", "/" ) + "-" + fileName );
        return ByteStreams.toByteArray( resource.getInputStream() );
    }

    private ContentVersionEntity createDraftVersion( String title )
    {
        ContentVersionEntity draftVersion = factory.createContentVersion( "" + ContentStatus.DRAFT.getKey(), "testuser/testuserstore" );
        draftVersion.setCreatedAt( new Date() );
        draftVersion.setModifiedAt( new Date() );
        draftVersion.setModifiedBy( fixture.findUserByName( "testuser" ) );
        draftVersion.setTitle( title );
        return draftVersion;
    }

    private void loginUserInPortal( UserKey userKey )
    {
        SecurityHolder.setRunAsUser( userKey );
        SecurityHolder.setUser( userKey );
    }

    private void setPathInfoAndRequestURI( MockHttpServletRequest httpServletRequest, String imageRequestPath )
    {
        httpServletRequest.setRequestURI( "/site/" + site1.getKey() + "/" + imageRequestPath );
        httpServletRequest.setPathInfo( "/" + site1.getKey() + "/" + imageRequestPath );
    }
}
