package com.enonic.cms.itest.content.imports;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
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

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.InvalidContentTypeConfigException;
import com.enonic.cms.core.content.imports.ImportException;
import com.enonic.cms.core.content.imports.ImportJob;
import com.enonic.cms.core.content.imports.ImportJobFactory;
import com.enonic.cms.core.content.imports.ImportResult;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.AssertTool;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:com/enonic/cms/itest/base-core-test-context.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ImportServiceImplTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private ImportJobFactory importJobFactory;

    @Autowired
    private ContentService contentService;

    private String personContentTypeXml;


    @Before
    public void setUp()
        throws IOException
    {
        personContentTypeXml = resourceToString( new ClassPathResource( "com/enonic/cms/itest/content/imports/personContentType.xml" ) );

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );

        fixture.save( factory.createContentHandler( "MyHandler", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "PersonCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                                 XMLDocumentFactory.create( personContentTypeXml ).getAsJDOMDocument() ) );
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
    public void importing_once_without_status_setting_creates_drafts()
        throws UnsupportedEncodingException
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
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getStatus() );

    }

    @Test
    public void order_of_related_contents_is_of_same_order_as_in_import_source_when_source_is_csv()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Father" ) );
        createCommand.setContentData( contentData );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mother" ) );
        createCommand.setContentData( contentData );
        ContentKey motherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Daughter" ) );
        createCommand.setContentData( contentData );
        ContentKey daughterContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "4" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Grand daughter" ) );
        createCommand.setContentData( contentData );
        contentService.createContent( createCommand );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import name='test-related-content' status='2' sync='person-no' mode='csv'>";
        importsConfig += "  <mapping src='1' dest='person-no'/>";
        importsConfig += "  <mapping src='2' dest='name'/>";
        importsConfig +=
            "  <mapping dest='related_persons' separator='|' relatedcontenttype='PersonCty' relatedfield='person-no' src='3'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "4;Grand daughter;1|3|2";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify: one content updated
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        Object[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        ContentKey[] expectedKeys = {fatherContentKey, daughterContentKey, motherContentKey};
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test(expected = InvalidContentTypeConfigException.class)
    public void expect_exception_when_mapping_is_missing_separator_and_destination_is_to_relatedcontent_with_multiple_true()
        throws UnsupportedEncodingException
    {
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import name='test-related-content' status='2' sync='person-no' mode='csv'>";
        importsConfig += "  <mapping src='1' dest='person-no'/>";
        importsConfig += "  <mapping src='2' dest='name'/>";
        importsConfig += "  <mapping dest='related_persons' relatedcontenttype='PersonCty' relatedfield='person-no' src='3' separator=''/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "4;Grand daughter;1";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        job.start();
    }

    @Test
    public void order_of_related_contents_is_of_same_order_as_in_import_source_when_source_is_csv_and_values_are_content_keys()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Father" ) );
        createCommand.setContentData( contentData );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mother" ) );
        createCommand.setContentData( contentData );
        ContentKey motherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Daughter" ) );
        createCommand.setContentData( contentData );
        ContentKey daughterContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "4" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Grand daughter" ) );
        createCommand.setContentData( contentData );
        contentService.createContent( createCommand );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import name='test-related-content' status='2' sync='person-no' mode='csv'>";
        importsConfig += "  <mapping src='1' dest='person-no'/>";
        importsConfig += "  <mapping src='2' dest='name'/>";
        importsConfig += "  <mapping dest='related_persons' separator='|' src='3'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "4;Grand daughter;" + fatherContentKey + "|" + daughterContentKey + "|" + motherContentKey;

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify: one content updated
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        Object[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        ContentKey[] expectedKeys = {fatherContentKey, daughterContentKey, motherContentKey};
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test
    public void order_of_related_contents_of_type_date_is_of_same_order_as_in_import_source_when_source_is_csv()
        throws UnsupportedEncodingException
    {
        // setup content type
        ContentTypeConfigBuilder dateCtyConfig = new ContentTypeConfigBuilder( "Date", "date" );
        dateCtyConfig.startBlock( "Date" );
        dateCtyConfig.addInput( "date", "date", "contentdata/date", "Date", true );
        dateCtyConfig.endBlock();
        dateCtyConfig.addIndexParameter( "date" );
        Document configAsXmlBytes = XMLDocumentFactory.create( dateCtyConfig.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "DateCty", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );
        fixture.save( factory.createUnit( "DatesUnit" ) );
        fixture.save( factory.createCategory( "Dates", "DateCty", "DatesUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Dates", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeConfig dateCtyCfg = fixture.findContentTypeByName( "DateCty" ).getContentTypeConfig();

        CreateContentCommand createCommand = setupDefaultCreateContentCommand( "Dates", ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( dateCtyCfg );
        contentData.add( new DateDataEntry( dateCtyCfg.getInputConfig( "date" ), new DateMidnight( 2000, 1, 1 ).toDate() ) );
        createCommand.setContentData( contentData );
        ContentKey date2000ContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommand( "Dates", ContentStatus.APPROVED );
        contentData = new CustomContentData( dateCtyCfg );
        contentData.add( new DateDataEntry( dateCtyCfg.getInputConfig( "date" ), new DateMidnight( 2005, 1, 1 ).toDate() ) );
        createCommand.setContentData( contentData );
        ContentKey date2005ContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommand( "Dates", ContentStatus.APPROVED );
        contentData = new CustomContentData( dateCtyCfg );
        contentData.add( new DateDataEntry( dateCtyCfg.getInputConfig( "date" ), new DateMidnight( 2010, 1, 1 ).toDate() ) );
        createCommand.setContentData( contentData );
        ContentKey date2010ContentKey = contentService.createContent( createCommand );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import name='test-related-content' status='2' sync='person-no' mode='csv'>";
        importsConfig += "  <mapping src='1' dest='person-no'/>";
        importsConfig += "  <mapping src='2' dest='name'/>";
        importsConfig += "  <mapping dest='related_dates' separator='|' relatedcontenttype='DateCty' relatedfield='date' src='3'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "4;Grand daughter;2005-01-01|2010-01-01|2000-01-01";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify: one content updated
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 0, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_dates" );
        Object[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        ContentKey[] expectedKeys = {date2005ContentKey, date2010ContentKey, date2000ContentKey};
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test
    public void order_of_related_contents_is_of_same_order_as_in_import_source_when_source_is_xml()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Father" ) );
        createCommand.setContentData( contentData );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mother" ) );
        createCommand.setContentData( contentData );
        ContentKey motherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Daughter" ) );
        createCommand.setContentData( contentData );
        ContentKey daughterContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "4" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Grand daughter" ) );
        createCommand.setContentData( contentData );
        contentService.createContent( createCommand );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='test-related-content' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig +=
            "  <mapping dest='related_persons' relatedcontenttype='PersonCty' relatedfield='person-no' src='related-persons/related-person/@id'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='4'>";
        importData += "     <name>Grand daughter</name>";
        importData += "     <related-persons>";
        importData += "         <related-person id='1'/>";
        importData += "         <related-person id='3'/>";
        importData += "         <related-person id='2'/>";
        importData += "     </related-persons>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify: one content updated
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        Object[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        ContentKey[] expectedKeys = {fatherContentKey, daughterContentKey, motherContentKey};
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test
    public void order_of_related_contents_is_changed_to_same_order_as_in_import_source()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Father" ) );
        createCommand.setContentData( contentData );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mother" ) );
        createCommand.setContentData( contentData );
        ContentKey motherContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Daughter" ) );
        createCommand.setContentData( contentData );
        ContentKey daughterContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "4" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Grand daughter" ) );
        RelatedContentsDataEntry relatedPersonsDataEntry =
            new RelatedContentsDataEntry( contentTypeConfig.getInputConfig( "related_persons" ) );
        relatedPersonsDataEntry.add(
            new RelatedContentDataEntry( contentTypeConfig.getInputConfig( "related_persons" ), fatherContentKey ) );
        relatedPersonsDataEntry.add(
            new RelatedContentDataEntry( contentTypeConfig.getInputConfig( "related_persons" ), daughterContentKey ) );
        relatedPersonsDataEntry.add(
            new RelatedContentDataEntry( contentTypeConfig.getInputConfig( "related_persons" ), motherContentKey ) );
        contentData.add( relatedPersonsDataEntry );
        createCommand.setContentData( contentData );
        contentService.createContent( createCommand );

        // verify setup: related content keys must be in inserted order
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        Object[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        ContentKey[] expectedKeys = {fatherContentKey, daughterContentKey, motherContentKey};
        assertArrayEquals( expectedKeys, actualKeys );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='test-related-content' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig +=
            "  <mapping dest='related_persons' relatedcontenttype='PersonCty' relatedfield='person-no' src='related-persons/related-person/@id'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='4'>";
        importData += "     <name>Grand daughter</name>";
        importData += "     <related-persons>";
        importData += "         <related-person id='3'/>";
        importData += "         <related-person id='2'/>";
        importData += "         <related-person id='1'/>";
        importData += "     </related-persons>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify: one content updated
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        actualKeys = related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        expectedKeys = new ContentKey[]{daughterContentKey, motherContentKey, fatherContentKey};
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test
    public void order_of_related_contents_is_not_changed_when_imported_twice_with_same_order()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='test-related-content' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig +=
            "  <mapping dest='related_persons' relatedcontenttype='PersonCty' relatedfield='person-no' src='related-persons/related-person/@id'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1'>";
        importData += "     <name>Father</name>";
        importData += "  </person>";
        importData += "  <person id='2'>";
        importData += "     <name>Mother</name>";
        importData += "  </person>";
        importData += "  <person id='3'>";
        importData += "     <name>Daughter</name>";
        importData += "  </person>";
        importData += "  <person id='4'>";
        importData += "     <name>Grand daughter</name>";
        importData += "     <related-persons>";
        importData += "         <related-person id='2'/>";
        importData += "         <related-person id='3'/>";
        importData += "         <related-person id='1'/>";
        importData += "     </related-persons>";
        importData += "  </person>";
        importData += "</persons>";

        // setup: first import
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify setup: content inserted
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 4, result.getInserted().size() );
        assertEquals( 0, result.getUpdated().size() );

        ContentKey fatherContentKey = fixture.findMainContentVersionByTitle( "Father" ).getContent().getKey();
        ContentKey motherContentKey = fixture.findMainContentVersionByTitle( "Mother" ).getContent().getKey();
        ContentKey daughterContentKey = fixture.findMainContentVersionByTitle( "Daughter" ).getContent().getKey();

        // verify setup: related content keys are in same order as in import source
        CustomContentData grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        RelatedContentsDataEntry related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        ContentKey[] expectedKeys = new ContentKey[]{motherContentKey, daughterContentKey, fatherContentKey};
        ContentKey[] actualKeys =
            related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        assertArrayEquals( expectedKeys, actualKeys );

        // exercise: second import
        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "test-related-content";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        // verify: content skipped
        assertEquals( 4, result.getSkipped().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 0, result.getUpdated().size() );

        // verify: related content keys are in same order as in import source
        grandDaughterCCD = (CustomContentData) fixture.findMainContentVersionByTitle( "Grand daughter" ).getContentData();
        related_persons = (RelatedContentsDataEntry) grandDaughterCCD.getEntry( "related_persons" );
        actualKeys = related_persons.getRelatedContentKeys().toArray( new ContentKey[related_persons.getRelatedContentKeys().size()] );
        assertArrayEquals( expectedKeys, actualKeys );
    }

    @Test
    public void importing_once_as_drafts_creates_drafts()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-draft-without-sync' status='0'>";
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
        command.importName = "xml-import-as-draft-without-sync";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getStatus() );

    }

    @Test
    public void importing_once_as_approved_creates_approved_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-approved-without-sync' status='2'>";
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
        command.importName = "xml-import-as-approved-without-sync";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        assertEquals( ContentStatus.APPROVED, fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getStatus() );

    }


    @Test
    public void importing_same_source_twice_with_sync_off_creates_new_content()
        throws UnsupportedEncodingException
    {

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-draft-without-sync' status='0'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "  </person>";
        firstImportSource += "  <person id='1002'>";
        firstImportSource += "     <name>Ane Skriubakken</name>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-without-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify setup
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        // exercise
        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-without-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 2, result.getInserted().size() );

        assertEquals( 4, fixture.countAllContent() );
        assertEquals( 2, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 2, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );
    }


    @Test
    public void importing_same_source_twice_with_sync_on_do_not_change_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-approved-with-sync' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='htmlarea' dest='htmlarea'/>";
        importsConfig += "  <mapping src='xml' dest='xml'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "     <htmlarea>\r\n<p>test</p></htmlarea>";
        firstImportSource += "     <xml></xml>";
        firstImportSource += "  </person>";
        firstImportSource += "  <person id='1002'>";
        firstImportSource += "     <name>Ane Skriubakken</name>";
        firstImportSource += "     <htmlarea></htmlarea>";
        firstImportSource += "     <xml></xml>";
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

        // verify setup
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        Date originalJrundModifiedAt = fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getModifiedAt();

        // exercise
        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-approved-with-sync";
        command.inputStream = new ByteArrayInputStream( firstImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        // verify
        assertEquals( 2, result.getSkipped().size() );
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( originalJrundModifiedAt, fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getModifiedAt() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );
    }

    @Test
    public void importing_second_changed_source_since_first_source_with_sync_on_do_change_content()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync-and-sex' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='@sex' dest='sex'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skriubakken</name>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
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
        assertEquals( 1, fixture.countAllContent() );

        // exercise

        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001' sex='M'>";
        secondImportSource += "     <name>Jørund Vier Qhawe Bekhizizwe Skriubakken</name>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-with-sync-and-sex";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 0, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Qhawe Bekhizizwe Skriubakken" ) );

        ContentEntity contentJrund = fixture.findAllContent().get( 0 );

        assertEquals( 1, contentJrund.getVersionCount() );

        ContentVersionEntity firstVersion = contentJrund.getVersions().get( 0 );
        assertEquals( ContentStatus.DRAFT, firstVersion.getStatus() );

        CustomContentData secondCotentData = (CustomContentData) firstVersion.getContentData();
        assertEquals( "M", ( (SelectorDataEntry) secondCotentData.getEntry( "sex" ) ).getValue() );
    }

    @Test
    public void importing_with_sync_on_content_key()
        throws UnsupportedEncodingException
    {

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync-on-contenkey' status='0' sync='@key'>";
        importsConfig += "  <mapping src='@content-key' dest='@key'/>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jørund Vier Skrivbakken</name>";
        firstImportSource += "  </person>";
        firstImportSource += "  <person id='1002'>";
        firstImportSource += "     <name>Ane Skrivbakken</name>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
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
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );

        // find content keys
        ContentKey contentKeyForJrund = fixture.findFirstContentVersionByTitle( "Jørund Vier Skrivbakken" ).getContent().getKey();
        ContentKey contentKeyForAne = fixture.findFirstContentVersionByTitle( "Ane Skrivbakken" ).getContent().getKey();
        fixture.flushAndClearHibernateSesssion();

        // exercise

        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person content-key='" + contentKeyForJrund + "' id='1001'>";
        secondImportSource += "     <name>Jørund Vier Skriubakken</name>";
        secondImportSource += "  </person>";
        secondImportSource += "  <person content-key='" + contentKeyForAne + "' id='1002'>";
        secondImportSource += "     <name>Ane Skriubakken</name>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-with-sync-on-contenkey";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 2, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 0, fixture.countContentVersionsByTitle( "Jørund Vier Skrivbakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 0, fixture.countContentVersionsByTitle( "Ane Skrivbakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );
    }


    @Test
    public void importing_xml_with_block_with_base_to_input_in_nonGroupBlock()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-with-block-with-base-to-input-in-nonGroupBlock' status='0'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <block base='inner'>";
        importsConfig += "    <mapping src='non-group-block-test-input' dest='non-group-block-test-input'/>";
        importsConfig += "  </block>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1001'>";
        importData += "     <name>Jørund Vier Skriubakken</name>";
        importData += "     <inner><non-group-block-test-input>test value 1</non-group-block-test-input></inner>";
        importData += "  </person>";
        importData += "  <person id='1002'>";
        importData += "     <name>Ane Skriubakken</name>";
        importData += "     <inner><non-group-block-test-input>test value 2</non-group-block-test-input></inner>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-with-block-with-base-to-input-in-nonGroupBlock";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        CustomContentData contentDataJrund =
            (CustomContentData) fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getContentData();
        assertEquals( "test value 1", ( (TextDataEntry) contentDataJrund.getEntry( "non-group-block-test-input" ) ).getValue() );

        CustomContentData contentDataAne = (CustomContentData) fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getContentData();
        assertEquals( "test value 2", ( (TextDataEntry) contentDataAne.getEntry( "non-group-block-test-input" ) ).getValue() );
    }

    @Test
    public void importing_xml_without_block_to_input_in_nonGroupBlock()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-without-block-to-input-in-nonGroupBlock' status='0'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='non-group-block-test-input' dest='non-group-block-test-input'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1001'>";
        importData += "     <name>Jørund Vier Skriubakken</name>";
        importData += "     <non-group-block-test-input>test value 1</non-group-block-test-input>";
        importData += "  </person>";
        importData += "  <person id='1002'>";
        importData += "     <name>Ane Skriubakken</name>";
        importData += "     <non-group-block-test-input>test value 2</non-group-block-test-input>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-without-block-to-input-in-nonGroupBlock";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getInserted().size() );
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Ane Skriubakken" ) );

        CustomContentData contentDataJrund =
            (CustomContentData) fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getContentData();
        assertEquals( "test value 1", ( (TextDataEntry) contentDataJrund.getEntry( "non-group-block-test-input" ) ).getValue() );

        CustomContentData contentDataAne = (CustomContentData) fixture.findFirstContentVersionByTitle( "Ane Skriubakken" ).getContentData();
        assertEquals( "test value 2", ( (TextDataEntry) contentDataAne.getEntry( "non-group-block-test-input" ) ).getValue() );
    }

    @Test
    public void imported_value_from_htmlarea_does_not_contain_referred_root_element_in_mapping()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='0'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1001'>";
        importData += "     <name>Jørund Vier Skriubakken</name>";
        importData += "     <html><div>some text</div></html>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        CustomContentData contentDataJrund =
            (CustomContentData) fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getContentData();
        HtmlAreaDataEntry htmlAreaDataEntry = (HtmlAreaDataEntry) contentDataJrund.getEntry( "htmlarea" );
        Document htmlAreaAsDoc = XMLDocumentFactory.create( htmlAreaDataEntry.getValue() ).getAsJDOMDocument();
        AssertTool.assertXPathExist( "/div", htmlAreaAsDoc );
    }

    @Test
    public void imported_value_from_htmlarea_includes_all_root_elements()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='0'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1001'>";
        importData += "     <name>Jørund Vier Skriubakken</name>";
        importData += "     <html><div>first root element</div><p>second root element</p></html>";
        importData += "  </person>";
        importData += "</persons>";

        // exercise
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 1, result.getInserted().size() );
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Jørund Vier Skriubakken" ) );

        CustomContentData contentDataJrund =
            (CustomContentData) fixture.findFirstContentVersionByTitle( "Jørund Vier Skriubakken" ).getContentData();
        HtmlAreaDataEntry htmlAreaDataEntry = (HtmlAreaDataEntry) contentDataJrund.getEntry( "htmlarea" );

        String importedHtmlAreaValueAsString = XMLDocumentFactory.create( htmlAreaDataEntry.getValue() ).getAsString();
        assertEquals( "<div>first root element</div><p>second root element</p>", importedHtmlAreaValueAsString );
    }

    @Test
    public void import_with_status_setting_set_to_draft_keeps_contents_status()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Approved" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.ARCHIVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Archived" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_archived = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 3, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='0'><name>Draft updated</name></person>";
        importData += "  <person id='2'><name>Approved updated</name></person>";
        importData += "  <person id='3'><name>Archived updated</name></person>";
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
        assertEquals( 3, result.getUpdated().size() );
        assertEquals( 3, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        assertEquals( ContentStatus.DRAFT, fixture.findContentByKey( contentKey_draft ).getMainVersion().getStatus() );
        assertEquals( 1, fixture.countContentVersionsByContent( contentKey_draft ) );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 0, contentKey_draft ).getStatus() );

        // verify: content Approved have unchanged status
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // verify: content Archived have unchanged status
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentByKey( contentKey_archived ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_archived ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_archived ).getStatus() );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_archived ).getStatus() );
    }

    @Test
    public void import_with_status_setting_set_to_approved_keeps_contents_status()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Approved" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.ARCHIVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Archived" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_archived = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 3, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='2' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='0'><name>Draft updated</name></person>";
        importData += "  <person id='2'><name>Approved updated</name></person>";
        importData += "  <person id='3'><name>Archived updated</name></person>";
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
        assertEquals( 3, result.getUpdated().size() );
        assertEquals( 3, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        assertEquals( ContentStatus.DRAFT, fixture.findContentByKey( contentKey_draft ).getMainVersion().getStatus() );
        assertEquals( 1, fixture.countContentVersionsByContent( contentKey_draft ) );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 0, contentKey_draft ).getStatus() );

        // verify: content Approved have unchanged status
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // verify: content Archived have unchanged status
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentByKey( contentKey_archived ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_archived ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_archived ).getStatus() );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_archived ).getStatus() );
    }

    @Test
    public void update_strategy_draft()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Approved" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.ARCHIVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Archived" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_archived = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 3, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-CONTENT-DRAFT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='0'><name>Draft updated</name></person>";
        importData += "  <person id='2'><name>Approved updated</name></person>";
        importData += "  <person id='3'><name>Archived updated</name></person>";
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
        assertEquals( 3, result.getUpdated().size() );
        assertEquals( 3, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        assertEquals( ContentStatus.DRAFT, fixture.findContentByKey( contentKey_draft ).getMainVersion().getStatus() );
        assertEquals( 1, fixture.countContentVersionsByContent( contentKey_draft ) );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 0, contentKey_draft ).getStatus() );

        // verify: content Approved is unchanged and new draft is updated
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // verify: content Archived have become a draft, and the previous main version is still archived
        assertEquals( ContentStatus.DRAFT, fixture.findContentByKey( contentKey_archived ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_archived ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_archived ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_archived ).getStatus() );
    }

    @Test
    public void updateStrategy_UPDATECONTENTDRAFT_updates_existing_draft_and_keeps_the_approved_version_as_current()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create approved content
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Original approved version" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        // setup: create draft for approved content
        UpdateContentCommand updateCommand =
            UpdateContentCommand.storeNewVersionIfChanged( fixture.findContentByKey( contentKey_approved ).getMainVersion().getKey() );
        updateCommand.setContentKey( contentKey_approved );
        updateCommand.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );
        updateCommand.setModifier( fixture.findUserByName( "testuser" ).getKey() );
        updateCommand.setPriority( 0 );
        updateCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        updateCommand.setStatus( ContentStatus.DRAFT );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Original draft version" ) );
        updateCommand.setContentData( contentData );
        contentService.updateContent( updateCommand );

        // verify setup content
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertTrue( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-CONTENT-DRAFT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='2'><name>Update by import</name></person>";
        importData += "</persons>";

        // exercise:
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, fixture.countAllContent() );

        // verify: content Approved is unchanged
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertTrue( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( "expected two versions", 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( "Original approved version",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 0, contentKey_approved ).getContentData() ) );

        // verify: imported data becomes as the new draft
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );
        assertEquals( "Update by import",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 1, contentKey_approved ).getContentData() ) );
    }

    @Test
    public void updateStrategy_UPDATECONTENTDRAFT_draft_is_not_updated_when_import_source_is_equal_to_the_draft()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create approved content
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Original approved version" ) );

        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        // setup: create a draft for the approved content
        UpdateContentCommand updateCommand =
            UpdateContentCommand.storeNewVersionIfChanged( fixture.findContentByKey( contentKey_approved ).getMainVersion().getKey() );
        updateCommand.setContentKey( contentKey_approved );
        updateCommand.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );
        updateCommand.setModifier( fixture.findUserByName( "testuser" ).getKey() );
        updateCommand.setPriority( 0 );
        updateCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        updateCommand.setStatus( ContentStatus.DRAFT );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Unchanged draft version" ) );
        contentData.add( new HtmlAreaDataEntry( contentTypeConfig.getInputConfig( "htmlarea" ), "" ) );
        updateCommand.setContentData( contentData );
        contentService.updateContent( updateCommand );

        // verify setup content
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertTrue( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-CONTENT-DRAFT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='2'><name>Unchanged draft version</name></person>";
        importData += "</persons>";

        // exercise:
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 1, result.getSkipped().size() );
        assertEquals( 1, fixture.countAllContent() );

        // verify: content Approved is unchanged
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertTrue( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( "expected two versions", 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( "Original approved version",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 0, contentKey_approved ).getContentData() ) );

        // verify:
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );
        assertEquals( "Unchanged draft version",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 1, contentKey_approved ).getContentData() ) );
    }

    @Test
    public void updateStrategy_UPDATECONTENTDRAFT_draft_is_not_created_when_import_source_is_equal_for_content()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create approved content
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Unchanged" ) );
        contentData.add( new HtmlAreaDataEntry( contentTypeConfig.getInputConfig( "htmlarea" ), "" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 1, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-CONTENT-DRAFT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='1'><name>Unchanged</name></person>";
        importData += "</persons>";

        // exercise:
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( importData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 0, result.getUpdated().size() );
        assertEquals( 1, result.getSkipped().size() );
        assertEquals( 1, fixture.countAllContent() );

        // verify: content Approved is unchanged, and no draft is created
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertFalse( "expected no draft", fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( 1, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( "Unchanged",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 0, contentKey_approved ).getContentData() ) );

    }

    @Test
    public void update_strategy_approve()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Approved" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.ARCHIVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Archived" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_archived = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 3, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-AND-APPROVE-CONTENT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='0'><name>Draft updated</name></person>";
        importData += "  <person id='2'><name>Approved updated</name></person>";
        importData += "  <person id='3'><name>Archived updated</name></person>";
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
        assertEquals( 3, result.getUpdated().size() );
        assertEquals( 3, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_draft ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_draft ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_draft ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 1, contentKey_draft ).getStatus() );

        // verify: content Approved is unchanged and new draft is updated
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // verify: content Archived have become a draft, and the previous main version is still archived
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_archived ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_archived ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_archived ).getStatus() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 1, contentKey_archived ).getStatus() );
    }

    @Test
    public void update_strategy_approve_updating_approved_content_with_draft()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create approved content
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Original approved version" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        // setup: create draft for approved content
        UpdateContentCommand updateCommand =
            UpdateContentCommand.storeNewVersionIfChanged( fixture.findContentByKey( contentKey_approved ).getMainVersion().getKey() );
        updateCommand.setContentKey( contentKey_approved );
        updateCommand.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );
        updateCommand.setModifier( fixture.findUserByName( "testuser" ).getKey() );
        updateCommand.setPriority( 0 );
        updateCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        updateCommand.setStatus( ContentStatus.DRAFT );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Original draft version" ) );
        updateCommand.setContentData( contentData );
        contentService.updateContent( updateCommand );

        // verify setup content
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertTrue( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.DRAFT, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-AND-APPROVE-CONTENT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='2'><name>Update by import</name></person>";
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
        assertEquals( 1, fixture.countAllContent() );

        // verify: content Approved is unchanged and new draft is updated
        assertEquals( ContentStatus.APPROVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertFalse( fixture.findContentByKey( contentKey_approved ).hasDraft() );
        assertEquals( 3, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( "Original approved version",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 0, contentKey_approved ).getContentData() ) );

        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );
        assertEquals( "Original draft version",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 1, contentKey_approved ).getContentData() ) );

        assertEquals( ContentStatus.APPROVED, fixture.findContentVersionByContent( 2, contentKey_approved ).getStatus() );
        assertEquals( "Update by import",
                      getTextDataEntryValue( "name", fixture.findContentVersionByContent( 2, contentKey_approved ).getContentData() ) );
    }

    @Test
    public void update_strategy_archive()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup: create one content for each status
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Draft" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_draft = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "2" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Approved" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_approved = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.ARCHIVED );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "3" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Archived" ) );
        createCommand.setContentData( contentData );
        ContentKey contentKey_archived = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 3, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import' update-strategy='UPDATE-AND-ARCHIVE-CONTENT' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "  <mapping src='html' dest='htmlarea'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String importData = "";
        importData += "<persons>";
        importData += "  <person id='0'><name>Draft updated</name></person>";
        importData += "  <person id='2'><name>Approved updated</name></person>";
        importData += "  <person id='3'><name>Archived updated</name></person>";
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
        assertEquals( 3, result.getUpdated().size() );
        assertEquals( 3, fixture.countAllContent() );

        // verify: content Draft have unchanged status
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentByKey( contentKey_draft ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_draft ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_draft ).getStatus() );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_draft ).getStatus() );

        // verify: content Approved is unchanged and new draft is updated
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentByKey( contentKey_approved ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_approved ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_approved ).getStatus() );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_approved ).getStatus() );

        // verify: content Archived have become a draft, and the previous main version is still archived
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentByKey( contentKey_archived ).getMainVersion().getStatus() );
        assertEquals( 2, fixture.countContentVersionsByContent( contentKey_archived ) );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 0, contentKey_archived ).getStatus() );
        assertEquals( ContentStatus.ARCHIVED, fixture.findContentVersionByContent( 1, contentKey_archived ).getStatus() );
    }

    @Test
    public void import_causing_update_when_update_content_name_setting_is_true_updates_content_name()
        throws UnsupportedEncodingException
    {
        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig +=
            "<import base='/persons/person' mode='xml' name='xml-import-as-draft-with-sync' status='0' sync='person-no' update-content-name='true'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String firstImportSource = "";
        firstImportSource += "<persons>";
        firstImportSource += "  <person id='1001'>";
        firstImportSource += "     <name>Jorund</name>";
        firstImportSource += "  </person>";
        firstImportSource += "</persons>";

        // setup
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
        assertEquals( 1, fixture.countAllContent() );

        // verify setup: that content name is as expected
        assertEquals( "jorund", fixture.findAllContent().get( 0 ).getName() );

        // exercise

        String secondImportSource = "";
        secondImportSource += "<persons>";
        secondImportSource += "  <person id='1001'>";
        secondImportSource += "     <name>Vier</name>";
        secondImportSource += "  </person>";
        secondImportSource += "</persons>";

        command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.importName = "xml-import-as-draft-with-sync";
        command.inputStream = new ByteArrayInputStream( secondImportSource.getBytes( "UTF-8" ) );
        job = importJobFactory.createImportJob( command );
        result = job.start();

        fixture.flushAndClearHibernateSesssion();

        // verify
        assertEquals( 0, result.getSkipped().size() );
        assertEquals( 1, result.getUpdated().size() );
        assertEquals( 0, result.getInserted().size() );
        assertEquals( 1, fixture.countAllContent() );
        assertEquals( 0, fixture.countContentVersionsByTitle( "Jorund" ) );
        assertEquals( 1, fixture.countContentVersionsByTitle( "Vier" ) );

        // verify: that content name have changed
        assertEquals( "vier", fixture.findAllContent().get( 0 ).getName() );

    }

    @Test
    public void updated_content_during_import_does_not_inherit_previous_updated_content_available_dates()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig contentTypeConfig = fixture.findCategoryByName( "Persons" ).getContentType().getContentTypeConfig();

        // setup:
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.APPROVED );
        createCommand.setAvailableFrom( new DateTime( 2008, 1, 1, 0, 0, 0, 0 ).toDate() );
        createCommand.setAvailableTo( new DateTime( 2010, 5, 1, 0, 0, 0, 0 ).toDate() );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "0" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mr Zero" ) );
        createCommand.setContentData( contentData );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForPersons( ContentStatus.DRAFT );
        createCommand.setAvailableFrom( new DateTime( 2010, 6, 1, 0, 0, 0, 0 ).toDate() );
        createCommand.setAvailableTo( null );
        contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "person-no" ), "1" ) );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Mr One" ) );
        createCommand.setContentData( contentData );
        ContentKey content_2 = contentService.createContent( createCommand );

        // verify setup content
        assertEquals( 2, fixture.countAllContent() );
        assertEquals( new DateTime( 2008, 1, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_1 ).getAvailableFrom() );
        assertEquals( new DateTime( 2010, 5, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_1 ).getAvailableTo() );
        assertEquals( new DateTime( 2010, 6, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_2 ).getAvailableFrom() );
        assertEquals( null, fixture.findContentByKey( content_2 ).getAvailableTo() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='0' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String impData = "";
        impData += "<persons>";
        impData += "<person id='0'><name>Mr Zero update</name></person>";
        impData += "<person id='1'><name>Mr One update</name></person>";
        impData += "</persons>";

        // exercise:
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.publishFrom = new DateTime( 2010, 8, 1, 0, 0, 0, 0 );
        command.publishTo = null;
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( impData.getBytes( "UTF-8" ) );
        ImportJob job = importJobFactory.createImportJob( command );
        ImportResult result = job.start();

        // verify
        assertEquals( 2, result.getUpdated().size() );
        assertEquals( 2, fixture.countAllContent() );

        // verify content after import
        assertEquals( new DateTime( 2008, 1, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_1 ).getAvailableFrom() );
        assertEquals( new DateTime( 2010, 5, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_1 ).getAvailableTo() );
        assertEquals( new DateTime( 2010, 6, 1, 0, 0, 0, 0 ).toDate(), fixture.findContentByKey( content_2 ).getAvailableFrom() );
    }

    @Test
    public void exception_is_thrown_when_given_publishFrom_is_not_before_publishTo()
        throws UnsupportedEncodingException
    {
        // verify no content is setup
        assertEquals( 0, fixture.countAllContent() );

        // setup content type with needed import configuration
        String importsConfig = "";
        importsConfig += "<imports>";
        importsConfig += "<import base='/persons/person' mode='xml' name='xml-import' status='1' sync='person-no'>";
        importsConfig += "  <mapping src='@id' dest='person-no'/>";
        importsConfig += "  <mapping src='name' dest='name'/>";
        importsConfig += "</import>";
        importsConfig += "</imports>";

        String changedContentTypeXml = personContentTypeXml.replace( "<imports/>", importsConfig );
        updateContentType( "PersonCty", changedContentTypeXml );

        String impData = "";
        impData += "<persons>";
        impData += "<person id='0'><name>Mr Zero update</name></person>";
        impData += "<person id='1'><name>Mr One update</name></person>";
        impData += "</persons>";

        // exercise:
        ImportContentCommand command = new ImportContentCommand();
        command.executeInOneTransaction = true;
        command.importer = fixture.findUserByName( "testuser" );
        command.categoryToImportTo = fixture.findCategoryByName( "Persons" );
        command.publishFrom = new DateTime( 2012, 8, 1, 0, 0, 0, 0 );
        command.publishTo = new DateTime( 2010, 8, 1, 0, 0, 0, 0 );
        command.importName = "xml-import";
        command.inputStream = new ByteArrayInputStream( impData.getBytes( "UTF-8" ) );
        try
        {
            importJobFactory.createImportJob( command );
            fail( "Expected ImportException" );
        }
        catch ( Throwable e )
        {
            assertTrue( "Expected ImportException", e instanceof ImportException );
            ImportException importException = (ImportException) e;
            assertEquals(
                "Given publishFrom (2012-08-01T00:00:00.000+02:00) bust be before given publishTo (2010-08-01T00:00:00.000+02:00)",
                importException.getMessage() );
        }
    }


    private String getTextDataEntryValue( String inputName, ContentData contentData )
    {
        CustomContentData customContentData = (CustomContentData) contentData;
        TextDataEntry textDataEntry = (TextDataEntry) customContentData.getEntry( inputName );
        return textDataEntry.getValue();
    }

    private CreateContentCommand setupDefaultCreateContentCommandForPersons( ContentStatus contentStatus )
    {
        return setupDefaultCreateContentCommand( "Persons", contentStatus );
    }

    private CreateContentCommand setupDefaultCreateContentCommand( String categoryName, ContentStatus contentStatus )
    {
        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createCommand.setCategory( fixture.findCategoryByName( categoryName ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setPriority( 0 );
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createCommand.setStatus( contentStatus );
        createCommand.setContentName( "testContent" );
        return createCommand;
    }

    private void updateContentType( String contentTypeName, String contentTypeXml )
    {
        ContentTypeEntity contentType = fixture.findContentTypeByName( contentTypeName );
        contentType.setData( XMLDocumentFactory.create( contentTypeXml ).getAsJDOMDocument() );
        fixture.flushAndClearHibernateSesssion();
    }

    private String resourceToString( Resource resource )
        throws IOException
    {
        return IOUtils.toString( resource.getInputStream() );
    }
}
