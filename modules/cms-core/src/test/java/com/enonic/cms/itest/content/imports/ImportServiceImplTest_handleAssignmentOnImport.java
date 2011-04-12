/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.imports.ImportResult;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportService;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.itest.DomainFactory;

import com.enonic.cms.core.content.imports.ImportJobFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Sep 14, 2010
 * Time: 9:31:00 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ImportServiceImplTest_handleAssignmentOnImport
{

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private ImportJobFactory importJobFactory;

    @Autowired
    private ImportService importService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentDao contentDao;

    private String personContentTypeXml;


    @Before
    public void setUp()
        throws IOException
    {
        personContentTypeXml = resourceToString( new ClassPathResource(
                "com/enonic/cms/itest/content/imports/personContentType.xml" ) );

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "PersonCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 XMLDocumentFactory.create( personContentTypeXml ).getAsBytes() ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "Persons", "PersonCty", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Persons", "testuser", "read, create, approve" ) );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        SecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        SecurityHolder.setUser( fixture.findUserByName( "testuser" ).getKey() );
        SecurityHolder.setRunAsUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void import_create_content_status_draft_with_assignment()
        throws Exception
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-without-status-without-sync'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1001'>";
        importData += "     <name>Jørund Vier Skriubakken</name>";
        importData += "  </person>";
        importData += "  <person id='1002'>";
        importData += "     <name>Ane Skriubakken</name>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-without-status-without-sync";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        command.assigneeKey = fixture.findUserByName( "testuser" ).getKey();
        command.assignmentDescription = "Import-test";
        command.assignmentDueDate = Calendar.getInstance().getTime();
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getStatus() );

        for ( ContentKey contentKey : result.getInserted().keySet() )
        {
            final ContentEntity currentContent = fixture.findContentByKey( contentKey );

            assertNotNull( "Assignee should be set for content: " + contentKey, currentContent.getAssignee() );
            assertNotNull( "Assigner should be set", currentContent.getAssigner() );
            assertNotNull( "Assignment descr should be set", currentContent.getAssignmentDescription() );
            assertNotNull( "Assignment duedate should be set", currentContent.getAssignmentDueDate() );
        }
    }


    @Test
    public void import_update_content_status_approved_removes_assignment()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();
        UserEntity testUser = fixture.findUserByName( "testuser" );

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        AssignContentCommand assignCommand = new AssignContentCommand();
        assignCommand.setAssigneeKey( testUser.getKey() );
        assignCommand.setAssignerKey( testUser.getKey() );
        assignCommand.setContentKey( contentKey_draft );

        contentService.assignContent( assignCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity draft = contentDao.findByKey( contentKey_draft );
        assertEquals( testUser, draft.getAssignee() );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft2" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft2 = contentService.createContent( createCommand );

        assignCommand = new AssignContentCommand();
        assignCommand.setAssigneeKey( testUser.getKey() );
        assignCommand.setAssignerKey( testUser.getKey() );
        assignCommand.setContentKey( contentKey_draft2 );

        contentService.assignContent( assignCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity draft2 = contentDao.findByKey( contentKey_draft2 );
        assertEquals( testUser, draft2.getAssignee() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='0' sync='person-no' purge='archive'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='2'><name>Draft2 updated</name></person>";
        importData += "</persons>";

        // exercise: import with status = 0
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 1, result.getArchived().size() );
        assertEquals( 2, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        ContentEntity archived_content = fixture.findContentByKey( contentKey_draft );
        assertEquals( ContentStatus.ARCHIVED, archived_content.getMainVersion().getStatus() );
        assertNull( "Assignee should be null after archiving", archived_content.getAssignee() );

        draft2 = fixture.findContentByKey( contentKey_draft2 );
        assertEquals( "Assignee should be kept on draft", testUser, draft2.getAssignee() );
    }

    private CreateContentCommand setupDefaultCreateContentCommandForPersons( ContentStatus contentStatus )
    {
        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createCommand.setCategory( fixture.findCategoryByName( "Persons" ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setPriority( 0 );
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createCommand.setStatus( contentStatus );
        createCommand.setContentName( "testcontent" );
        return createCommand;
    }

    private void updateContentType( String contentTypeName, String contentTypeXml )
    {
        ContentTypeEntity contentType = fixture.findContentTypeByName( contentTypeName );
        contentType.setData( XMLDocumentFactory.create( contentTypeXml ).getAsBytes() );
        fixture.flushAndClearHibernateSesssion();
    }

    private String resourceToString( Resource resource )
        throws IOException
    {
        return IOUtils.toString( resource.getInputStream() );
    }

}
