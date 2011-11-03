package com.enonic.cms.itest.portal;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;

import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.content.file.FileBinaryInput;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;
import com.enonic.cms.api.client.model.content.file.FileNameInput;
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
import com.enonic.cms.core.portal.mvc.controller.AttachmentController;
import com.enonic.cms.core.portal.mvc.controller.AttachmentRequestException;
import com.enonic.cms.core.preview.ContentPreviewContext;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

public class AttachmentControllerTest
    extends AbstractSpringTest
{
    @Autowired
    protected HibernateTemplate hibernateTemplate;

    protected DomainFactory factory;

    protected DomainFixture fixture;

    @Autowired
    private InternalClientContentService internalClientContentService;

    private PreviewService previewService;

    @Autowired
    private AttachmentController attachmentController;

    private MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();

    private MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

    private MockServletContext servletContext = new MockServletContext();

    private SiteEntity site1;


    @Before
    public void before()
        throws Exception
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        httpServletRequest.setCharacterEncoding( "UTF-8" );
        ServletRequestAccessor.setRequest( httpServletRequest );
        loginUserInPortal( fixture.findUserByName( "testuser" ).getKey() );

        attachmentController.setServletContext( servletContext );

        previewService = Mockito.mock( PreviewService.class );
        Mockito.when( previewService.isInPreview() ).thenReturn( false );
        Mockito.when( previewService.getPreviewContext() ).thenReturn( PreviewContext.NO_PREVIEW );
        attachmentController.setPreviewService( previewService );

        MockTimeService timeService = new MockTimeService( new DateTime( 2011, 6, 27, 12, 0, 0, 0 ) );
        attachmentController.setTimeService( timeService );

        site1 = factory.createSite( "MySite", new Date(), null, "en" );
        fixture.save( site1 );
        MenuItemEntity firstPage = createPage( "Firstpage", null, "MySite" );
        fixture.save( firstPage );

        site1.setFirstPage( firstPage );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "FileContentType", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        fixture.save( factory.createUnit( "FileUnit" ) );
        fixture.save( factory.createCategory( "AttachmentCategory", "FileContentType", "FileUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "AttachmentCategory", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void request_content_attachment_with_label_source()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String attachmentRequestPath = "_attachment/" + contentKey.toString() + "/label/source";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( "text/plain", httpServletResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertEquals( 4, httpServletResponse.getContentLength() );
    }

    @Test
    public void request_content_attachment_with_no_label()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String attachmentRequestPath = "_attachment/" + contentKey.toString();
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( "text/plain", httpServletResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertEquals( 4, httpServletResponse.getContentLength() );
    }

    @Test
    public void request_content_attachment_that_does_not_exist()
        throws Exception
    {
        String attachmentRequestPath = "_attachment/123" + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void request_content_attachment_with_label_that_does_not_exist()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String attachmentRequestPath = "_attachment/" + contentKey + "/label/nonexistinglabel.jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Invalid binary path: '/" + attachmentRequestPath + "', message: Unsupported label 'nonexistinglabel'",
                          attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void request_content_attachment_that_is_deleted()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );
        ContentEntity content = fixture.findContentByKey( contentKey );
        content.setDeleted( true );

        String attachmentRequestPath = "_attachment/" + contentKey + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void request_content_attachment_that_is_not_online()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

        String attachmentRequestPath = "_attachment/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void request_content_attachment_with_no_access()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        fixture.createAndStoreUserAndUserGroup( "user-with-no-access", "testuser fullname", UserType.NORMAL, "testuserstore" );
        loginUserInPortal( fixture.findUserByName( "user-with-no-access" ).getKey() );

        String attachmentRequestPath = "_attachment/" + contentKey + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void request_content_attachment_that_binary_is_on_main_version()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        // setup: draft version of content
        ContentEntity content = fixture.findContentByKey( contentKey );

        BinaryDataEntity binaryDataOfMainVersion = content.getMainVersion().getBinaryData( "source" );

        // exercise & verify
        String attachmentRequestPath = "_attachment/" + contentKey + "/binary/" + binaryDataOfMainVersion.getKey() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
        assertEquals( "text/plain", httpServletResponse.getContentType() );
    }

    @Test
    public void exception_thrown_for_request_to_content_attachment_that_binary_is_not_on_main_version()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

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
        String attachmentRequestPath = "_attachment/" + contentKey + "/binary/" + binaryDataForDraftVersion.getKey() + ".jpg";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    @Test
    public void response_ok_for_request_to_content_attachment_that_is_not_online_when_is_related_to_content_in_preview()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

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
        String attachmentRequestPath = "_attachment/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
    }

    @Test
    public void exception_thrown_for_request_to_content_attachment_that_is_not_online_when_is_not_related_to_content_in_preview()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 13, 0, 0, 0 ), null );

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
        String attachmentRequestPath = "_attachment/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        try
        {
            attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof AttachmentRequestException );
            AttachmentRequestException attachmentRequestException = (AttachmentRequestException) e;
            assertEquals( "Failed to serve attachment request [/" + attachmentRequestPath + "] on site " + site1.getKey() +
                              ": Attachment not found, path: '/" + attachmentRequestPath + "'", attachmentRequestException.getMessage() );
        }
    }

    private MenuItemEntity createPage( String name, String parentName, String siteName )
    {
        return factory.createPageMenuItem( name, 0, name, name, siteName, "testuser", "testuser", false, false, "en", parentName, 0,
                                           new Date(), false, null );
    }

    private ContentKey createFileContent( String name, int contentStatus, byte[] bytes, String categoryName, DateTime availableFrom,
                                          DateTime availableTo )
        throws IOException
    {
        FileContentDataInput fileContentDataInput = new FileContentDataInput();
        fileContentDataInput.name = new FileNameInput( name );
        fileContentDataInput.binary = new FileBinaryInput( bytes, name );

        CreateFileContentParams params = new CreateFileContentParams();
        params.categoryKey = fixture.findCategoryByName( categoryName ).getKey().toInt();
        params.publishFrom = availableFrom != null ? availableFrom.toDate() : null;
        params.publishTo = availableTo != null ? availableTo.toDate() : null;
        params.status = contentStatus;
        params.fileContentData = fileContentDataInput;

        return new ContentKey( internalClientContentService.createFileContent( params ) );
    }

    private byte[] loadFile( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( AttachmentControllerTest.class.getName().replace( ".", "/" ) + "-" + fileName );
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

    private void setPathInfoAndRequestURI( MockHttpServletRequest httpServletRequest, String attachmentRequestPath )
    {
        httpServletRequest.setRequestURI( "/site/" + site1.getKey() + "/" + attachmentRequestPath );
        httpServletRequest.setPathInfo( "/" + site1.getKey() + "/" + attachmentRequestPath );
    }
}
