package com.enonic.cms.itest.admin;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.io.ByteStreams;

import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.content.file.FileBinaryInput;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;
import com.enonic.cms.api.client.model.content.file.FileNameInput;
import com.enonic.cms.core.client.InternalClientContentService;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.security.AdminSecurityHolder;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.server.service.admin.mvc.controller.AttachmentController;
import com.enonic.cms.store.dao.GroupDao;

import static org.junit.Assert.*;

public class AttachmentControllerTest
    extends AbstractSpringTest
{
    @Autowired
    protected HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupDao groupDao;

    protected DomainFactory factory;

    @Autowired
    protected DomainFixture fixture;

    @Autowired
    private InternalClientContentService internalClientContentService;

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
        factory = fixture.getFactory();

        fixture.initSystemData();
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes( httpServletRequest );
        RequestContextHolder.setRequestAttributes( servletRequestAttributes );
        httpServletRequest.setCharacterEncoding( "UTF-8" );
        ServletRequestAccessor.setRequest( httpServletRequest );

        loginUserInAdmin( fixture.findUserByName( "testuser" ).getKey() );
        loginUserInPortal( fixture.findUserByName( "testuser" ).getKey() );

        attachmentController.setServletContext( servletContext );

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
        String attachmentRequestPath = "_attachment/123";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() == 0 );
    }

    @Test
    public void request_content_attachment_with_label_that_does_not_exist()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        String attachmentRequestPath = "_attachment/" + contentKey + "/label/nonexistinglabel";
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() == 0 );
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

        String attachmentRequestPath = "_attachment/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() == 0 );
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

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
        assertEquals( "text/plain", httpServletResponse.getContentType() );
    }

    @Test
    public void request_content_attachment_with_no_access()
        throws Exception
    {
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        fixture.createAndStoreUserAndUserGroup( "user-with-no-access", "testuser fullname", UserType.NORMAL, "testuserstore" );
        loginUserInAdmin( fixture.findUserByName( "user-with-no-access" ).getKey() );

        String attachmentRequestPath = "_attachment/" + contentKey;
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() == 0 );
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
        String attachmentRequestPath = "_attachment/" + contentKey + "/binary/" + binaryDataOfMainVersion.getKey();
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
        assertEquals( "text/plain", httpServletResponse.getContentType() );
    }

    @Test
    public void response_ok_for_request_to_content_attachment_that_binary_is_not_on_main_version()
        throws Exception
    {
        // setup: content
        byte[] bytes = loadFile( "MyTextFile.txt" );
        ContentKey contentKey =
            createFileContent( "MyTextFile.txt", 2, bytes, "AttachmentCategory", new DateTime( 2011, 6, 27, 10, 0, 0, 0 ), null );

        // setup: draft version of content
        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionEntity draftVersion = createDraftVersion( "MyTextFile.txt" );
        draftVersion.setContentDataXml( content.getMainVersion().getContentDataAsXmlString() );
        content.setDraftVersion( draftVersion );
        content.addVersion( draftVersion );
        fixture.save( draftVersion );

        BinaryDataEntity binaryDataForDraftVersion = factory.createBinaryData( "MyTextFile.txt", bytes.length );
        binaryDataForDraftVersion.setBlobKey( content.getMainVersion().getBinaryData( "source" ).getBlobKey() );
        fixture.save( binaryDataForDraftVersion );

        ContentBinaryDataEntity contentBinaryDataForDraftVersion =
            factory.createContentBinaryData( "source", binaryDataForDraftVersion, draftVersion );
        draftVersion.addContentBinaryData( contentBinaryDataForDraftVersion );
        fixture.save( contentBinaryDataForDraftVersion );

        fixture.flushAndClearHibernateSesssion();

        // exercise & verify
        String attachmentRequestPath = "_attachment/" + contentKey + "/binary/" + binaryDataForDraftVersion.getKey();
        setPathInfoAndRequestURI( httpServletRequest, attachmentRequestPath );
        httpServletRequest.setParameter( "_version", draftVersion.getKey().toString() );

        attachmentController.handleRequestInternal( httpServletRequest, httpServletResponse );

        assertEquals( HttpServletResponse.SC_OK, httpServletResponse.getStatus() );
        assertTrue( "Content Length", httpServletResponse.getContentLength() > 0 );
        assertEquals( "text/plain", httpServletResponse.getContentType() );
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
        PortalSecurityHolder.setImpersonatedUser( userKey );
        PortalSecurityHolder.setUser( userKey );
    }

    private void loginUserInAdmin( UserKey userKey )
    {
        AdminSecurityHolder.setUser( userKey );
    }

    private void setPathInfoAndRequestURI( MockHttpServletRequest httpServletRequest, String attachmentRequestPath )
    {
        httpServletRequest.setRequestURI( "/admin/" + attachmentRequestPath );
        httpServletRequest.setPathInfo( "/" + attachmentRequestPath );
    }
}
