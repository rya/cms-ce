/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateMidnight;
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

import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.core.content.imports.ImportJobFactory;

import com.enonic.cms.core.security.SecurityHolder;

import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.contentdata.custom.BlockGroupDataEntries;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.security.user.User;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ImportServiceImpl_importWithBlockGroupsTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private ImportJobFactory importJobFactory;

    @Autowired
    private ImportService importService;

    private String personContentTypeXml;


    @Before
    public void setUp()
        throws IOException
    {
        personContentTypeXml = resourceToString(
            new ClassPathResource(
                    "com/enonic/cms/itest/content/imports/personContentType-importWithBlockGroupsTest.xml" ) );

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
    public void importing_with_sync_added_group_data_entry()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-approved-with-sync' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-06-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Confirmation</name>";
        secondImportSource += "         <date>1991-04-04</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();
        GroupDataEntry jrundEvent1 = newContentData.getGroupDataEntry( "Events", 1 );

        assertEquals( "Birth", ( (TextDataEntry) jrundEvent1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) jrundEvent1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry jrundEvent2 = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Confirmation", ( (TextDataEntry) jrundEvent2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(), ( (DateDataEntry) jrundEvent2.getEntry( "event-date" ) ).getValue() );
    }

    @Test
    public void importing_with_sync_modified_group_data_entry()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-approved-with-sync' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2008-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Confirmation</name>";
        secondImportSource += "         <date>1991-04-16</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Marriage</name>";
        secondImportSource += "         <date>2008-02-14</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that first version is as originally
        originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        originalContentData = (CustomContentData) originalVersion.getContentData();
        originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // verify that new version has changed
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();
        GroupDataEntry newConfirmationEvent = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 16 ).toDate(),
                      ( (DateDataEntry) newConfirmationEvent.getEntry( "event-date" ) ).getValue() );
    }

    @Test
    public void sync_import_first_with_group_entries_second_import_without_but_otherwise_unchanged_does_not_change_content_When_import_config_is_without_blockgroup_mapping()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2008-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-with-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // setup content type with import configuration without block group
        importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync-and-without-block-mapping' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-with-sync-and-without-block-mapping";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 1, result.getSkipped().size() );
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
    }


    @Test
    public void sync_import_first_with_group_entries_second_import_with_one_removed_and_one_changed_changes_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-approved-with-sync' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        fixture.flushAndClearHibernateSesssion();

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-16</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2009-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Marriage</name>";
        secondImportSource += "         <date>2008-02-14</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that first version is as originally
        originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        originalContentData = (CustomContentData) originalVersion.getContentData();
        originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // verify that new version has changed
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();

        GroupDataEntry newBirthEvent = newContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) newBirthEvent.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) newBirthEvent.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry newMarriageEvent = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Marriage", ( (TextDataEntry) newMarriageEvent.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) newMarriageEvent.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event3 = newContentData.getGroupDataEntry( "Events", 3 );
        assertEquals( "Marriage", ( (TextDataEntry) event3.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2009, 2, 14 ).toDate(), ( (DateDataEntry) event3.getEntry( "event-date" ) ).getValue() );
    }

    @Test
    public void sync_block_import_without_purge_first_with_group_entries_second_import_with_one_removed_and_one_changed_changes_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' status='2' sync='person-no' name='xml-import-as-approved-with-sync-and-block-sync-without-purge'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events' sync='event-name' purge='false'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-16</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2008-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-without-purge";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Became father</name>";
        secondImportSource += "         <date>2010-03-18</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-without-purge";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that first version is as originally
        originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        originalContentData = (CustomContentData) originalVersion.getContentData();
        originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // verify that new version has changed
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();

        GroupDataEntry event1 = newContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) event1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) event1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event2 = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Confirmation", ( (TextDataEntry) event2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(), ( (DateDataEntry) event2.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event3 = newContentData.getGroupDataEntry( "Events", 3 );
        assertEquals( "Marriage", ( (TextDataEntry) event3.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) event3.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event4 = newContentData.getGroupDataEntry( "Events", 4 );
        assertEquals( "Became father", ( (TextDataEntry) event4.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2010, 3, 18 ).toDate(), ( (DateDataEntry) event4.getEntry( "event-date" ) ).getValue() );
    }


    @Test
    public void import_with_sync_block_with_purge_removed_last_block_entry_when_last_block_entry_is_removed_in_second_import_source()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' status='2' sync='person-no' name='xml-import-as-approved-with-sync-and-block-sync-with-purge'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events' sync='event-name' purge='true'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2008-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        assertNotNull( originalContentData.getGroupDataEntry( "Events", 1 ) );
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );
        GroupDataEntry originalMarriageEvent = originalContentData.getGroupDataEntry( "Events", 3 );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(),
                      ( (DateDataEntry) originalMarriageEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Confirmation</name>";
        secondImportSource += "         <date>1991-04-04</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that new version has removed last event
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();

        GroupDataEntry event1 = newContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) event1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) event1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event2 = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Confirmation", ( (TextDataEntry) event2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(), ( (DateDataEntry) event2.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event3 = newContentData.getGroupDataEntry( "Events", 3 );
        assertNull( event3 );
    }

    @Test
    public void having_sync_off_and_purge_true()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' status='2' sync='person-no' name='test-jvs'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events' purge='true'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2009-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-jvs";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        job.start();

        fixture.flushAndClearHibernateSesssion();

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Marriage</name>";
        secondImportSource += "         <date>2008-02-14</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-jvs";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that new version has removed last event
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();

        GroupDataEntry event1 = newContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Marriage", ( (TextDataEntry) event1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) event1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event2 = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Birth", ( (TextDataEntry) event2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) event2.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event3 = newContentData.getGroupDataEntry( "Events", 3 );
        assertNull( event3 );
    }

    @Test
    public void import_with_sync_block_with_purge_removed_middle_block_entry_when_middle_block_entry_is_removed_in_second_import_source()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' status='2' sync='person-no' name='xml-import-as-approved-with-sync-and-block-sync-with-purge'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events' sync='event-name' purge='true'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-19</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2008-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        assertNotNull( originalContentData.getGroupDataEntry( "Events", 1 ) );
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );
        GroupDataEntry originalMarriageEvent = originalContentData.getGroupDataEntry( "Events", 3 );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(),
                      ( (DateDataEntry) originalMarriageEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Marriage</name>";
        secondImportSource += "         <date>2008-02-14</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that new version has removed last event
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();

        GroupDataEntry event1 = newContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) event1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) event1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event2 = newContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Marriage", ( (TextDataEntry) event2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) event2.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event3 = newContentData.getGroupDataEntry( "Events", 3 );
        assertNull( event3 );
    }

    @Test
    public void sync_block_import_with_purge_first_with_group_entries_second_import_with_one_removed_and_one_changed_changes_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' status='2' sync='person-no' name='xml-import-as-approved-with-sync-and-block-sync-with-purge'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='events/event' dest='Events' sync='event-name' purge='true'>";
        importsConfig += "    <mapping src='name' dest='event-name'/>";
        importsConfig += "    <mapping src='date' dest='event-date'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <events>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Birth</name>";
        firstImportSource += "         <date>1976-04-16</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Confirmation</name>";
        firstImportSource += "         <date>1991-04-04</date>";
        firstImportSource += "       </event>";
        firstImportSource += "       <event>";
        firstImportSource += "         <name>Marriage</name>";
        firstImportSource += "         <date>2009-02-14</date>";
        firstImportSource += "       </event>";
        firstImportSource += "     </events>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify setup
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        ContentVersionEntity originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        CustomContentData originalContentData = (CustomContentData) originalVersion.getContentData();
        GroupDataEntry originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // exercise
        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "     <events>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Birth</name>";
        secondImportSource += "         <date>1976-04-19</date>";
        secondImportSource += "       </event>";
        secondImportSource += "       <event>";
        secondImportSource += "         <name>Marriage</name>";
        secondImportSource += "         <date>2008-02-14</date>";
        secondImportSource += "       </event>";
        secondImportSource += "     </events>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync-and-block-sync-with-purge";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );

        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        // verify that first version is as originally
        originalVersion = fixture.findContentVersionByTitle( 0, "Jørund Vier Skriubakken" );
        originalContentData = (CustomContentData) originalVersion.getContentData();
        originalConfirmationEvent = originalContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 1991, 4, 4 ).toDate(),
                      ( (DateDataEntry) originalConfirmationEvent.getEntry( "event-date" ) ).getValue() );

        // verify that new version has changed
        ContentVersionEntity newVersion = fixture.findContentVersionByTitle( 1, "Jørund Vier Skriubakken" );
        CustomContentData newContentData = (CustomContentData) newVersion.getContentData();
        BlockGroupDataEntries eventsBlockGroupDataEntries = newContentData.getBlockGroupDataEntries( "Events" );

        assertEquals( 2, eventsBlockGroupDataEntries.numberOfEntries() );

        GroupDataEntry event1 = eventsBlockGroupDataEntries.getGroupDataEntry( 1 );
        assertEquals( "Birth", ( (TextDataEntry) event1.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) event1.getEntry( "event-date" ) ).getValue() );

        GroupDataEntry event2 = eventsBlockGroupDataEntries.getGroupDataEntry( 2 );
        assertEquals( "Marriage", ( (TextDataEntry) event2.getEntry( "event-name" ) ).getValue() );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) event2.getEntry( "event-date" ) ).getValue() );
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