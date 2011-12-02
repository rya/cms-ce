/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.CreateContentException;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.InvalidContentTypeConfigException;
import com.enonic.cms.core.security.PortalSecurityHolder;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import static org.junit.Assert.*;

/**
 * This test only tests the configuration of the title element of Custom Content.  Other tests may be added to more completely test the
 * use of custom content configurations.
 */
public class ContentServiceImpl_contentConfigTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected ContentService contentService;

    private DomainFactory factory;

    private DomainFixture fixture;

    private SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM.yyyy" );


    @Before
    public void setUp()
    {
        groupEntityDao.invalidateCachedKeys();

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        PortalSecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void testCustomContentWithDateTitle()
        throws ParseException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "fdato" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "fdato", "date", "contentdata/fdato", "Fødselsdato", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );
        fixture.save( factory.createCategory( "Employees", "Person", "UnitForPerson", "testuser", "testuser", false ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeEntity contentType = fixture.findContentTypeByName( "Person" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        contentData.add(
            new DateDataEntry( contentType.getContentTypeConfig().getInputConfig( "fdato" ), dateFormat.parse( "31.07.1966" ) ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( fixture.findUserByName( "testuser" ) );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( "Employees" ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testEmployee_1" );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent );
        ContentVersionEntity v1 = persistedContent.getMainVersion();
        assertEquals( "31.07.1966", v1.getContentData().getTitle() );

    }

    @Test
    public void testCustomContentWithTextAreaTitle_shouldFail()
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "textarea", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );
        fixture.save( factory.createCategory( "Employees", "Person", "UnitForPerson", "testuser", "testuser", false ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeEntity contentType = fixture.findContentTypeByName( "Person" );
        try
        {
            new CustomContentData( contentType.getContentTypeConfig() );
            fail( "Creating this content type should fail.  Having a textarea as a title is illegal." );
        }
        catch ( InvalidContentTypeConfigException e )
        {
            // Success!!!
        }
    }

    @Test
    public void testCreateContentWithTileInputOfTypeDate()
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "birth" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "birth", "date", "contentdata/birth", "Name", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );
        fixture.save( factory.createCategory( "Employees", "Person", "UnitForPerson", "testuser", "testuser", false ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeEntity contentType = fixture.findContentTypeByName( "Person" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        contentData.add( new DateDataEntry( contentType.getContentTypeConfig().getInputConfig( "birth" ),
                                            new DateTime( 1976, 4, 19, 0, 0, 0, 0 ).toDate() ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( fixture.findUserByName( "testuser" ) );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( "Employees" ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testEmployee_1" );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        ContentEntity actualContent = fixture.findContentByKey( contentKey );
        assertNotNull( actualContent );
        assertEquals( "19.04.1976", actualContent.getMainVersion().getContentData().getTitle() );
    }

    @Test
    public void testCreateContentThrowsMissingRequiredContentDataExceptionWhenValueForTitleInputOfTypeDateIsNull()
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "birth" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "birth", "date", "contentdata/birth", "Name", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );
        fixture.save( factory.createCategory( "Employees", "Person", "UnitForPerson", "testuser", "testuser", false ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeEntity contentType = fixture.findContentTypeByName( "Person" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        contentData.add( new DateDataEntry( contentType.getContentTypeConfig().getInputConfig( "birth" ), null ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( fixture.findUserByName( "testuser" ) );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( "Employees" ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testEmployee_1" );

        try
        {
            contentService.createContent( createContentCommand );
        }
        catch ( Exception e )
        {
            assertTrue( "Expected CreateContentException, was: " + e.getClass().getName(), e instanceof CreateContentException );
            assertEquals( "Failed to created content: Missing data for required title input (missing value in data entry): birth",
                          e.getMessage() );
        }
    }


    @Test
    public void testCustomContentWithDropDownTitle()
        throws ParseException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "birthYear" );
        ctyconf.startBlock( "Person" );
        ctyconf.addDropDownInput( "birthYear", "contentdata/birthYear", "Fødselsår", true, "1958", "1958", "1962", "1962", "1966", "1966",
                                  "1970", "1970" );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );
        fixture.save( factory.createCategory( "Employees", "Person", "UnitForPerson", "testuser", "testuser", false ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        ContentTypeEntity contentType = fixture.findContentTypeByName( "Person" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        contentData.add( new SelectorDataEntry( contentType.getContentTypeConfig().getInputConfig( "birthYear" ), "1958" ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( fixture.findUserByName( "testuser" ) );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( "Employees" ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testEmployee_1" );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent );
        assertEquals( "1958", persistedContent.getMainVersion().getContentData().getTitle() );

    }
}
