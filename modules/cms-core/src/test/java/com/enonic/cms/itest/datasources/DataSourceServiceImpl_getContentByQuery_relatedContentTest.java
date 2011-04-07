/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.datasources;

import org.jdom.Document;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.time.MockTimeService;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.internal.service.DataSourceServiceImpl;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.core.content.command.UpdateContentCommand;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.security.user.User;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DataSourceServiceImpl_getContentByQuery_relatedContentTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserDao userDao;

    private DataSourceServiceImpl dataSourceService;

    @Autowired
    private ContentService contentService;

    private static final DateTime DATE_TIME_2010_01_01 = new DateTime( 2010, 1, 1, 0, 0, 0, 0 );


    @Before
    public void setUp()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        MockHttpServletRequest httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        dataSourceService = new DataSourceServiceImpl();
        dataSourceService.setContentService( contentService );
        dataSourceService.setSecurityService( securityService );
        dataSourceService.setTimeService( new MockTimeService( new DateTime( 2010, 7, 1, 12, 0, 0, 0 ) ) );
        dataSourceService.setUserDao( userDao );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContent", "title" );
        ctyconf.startBlock( "MyContent" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addRelatedContentInput( "myRelatedContent", "relatedcontent", "contentdata/myRelatedContent", "My related content", false,
                                        true );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();

        fixture.save(
            factory.createContentType( "MyRelatedType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyRelatedType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.save(
            factory.createCategory( "MyOtherCategory", "MyRelatedType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-querier", "read, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyOtherCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyOtherCategory", "content-querier", "read, admin_browse" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void common_content_related_to_between_two_content_is_listed_both_contents_relatedcontentkeys()
    {
        // setup: create same content in two different categories
        ContentKey commonChildContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Common child" ), "content-creator" ) );

        ContentKey contentA = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Content A", commonChildContentKey ),
                                        "content-creator" ) );

        ContentKey contentB = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Content B", commonChildContentKey ),
                                        "content-creator" ) );

        // setup: verify that 2 content is created
        assertEquals( 3, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        String query = "title STARTS WITH 'Content '";
        String orderyBy = "";
        int index = 0;
        int count = 10;
        boolean includeData = true;
        int childrenLevel = 1;
        int parentLevel = 0;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByQuery( context, query, orderyBy, index, count, includeData, childrenLevel, parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        AssertTool.assertSingleXPathValueEquals( "/contents/@totalcount", jdomDocResult, "2" );
        AssertTool.assertXPathEquals( "/contents/content/@key", jdomDocResult, contentA.toString(), contentB.toString() );
        AssertTool.assertXPathEquals( "/contents/content[ title = 'Content A']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      commonChildContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[ title = 'Content B']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      commonChildContentKey.toString() );
        AssertTool.assertSingleXPathValueEquals( "/contents/relatedcontents/@count", jdomDocResult, "1" );
        AssertTool.assertSingleXPathValueEquals( "/contents/relatedcontents/content/@key", jdomDocResult,
                                                 commonChildContentKey.toString() );
    }

    @Test
    public void content_queried_with_related_children_having_children_existing_as_the_queried_content_is_listed_as_related_content_too()
    {
        // setup: create same content in two different categories
        ContentKey grandChildContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand child" ), "content-creator" ) );

        ContentKey sonContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Son", grandChildContentKey ), "content-creator" ) );

        ContentKey daughterContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Daughter" ), "content-creator" ) );

        ContentKey fatherContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Father", sonContentKey, daughterContentKey ),
                                        "content-creator" ) );

        // setup: verify that the content was created
        assertEquals( 4, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        String query = "categorykey = " + fixture.findCategoryByName( "MyCategory" ).getKey();
        String orderyBy = "@key desc";
        int index = 0;
        int count = 10;
        boolean includeData = true;
        int childrenLevel = 10;
        int parentLevel = 0;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByQuery( context, query, orderyBy, index, count, includeData, childrenLevel, parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        AssertTool.assertSingleXPathValueEquals( "/contents/@totalcount", jdomDocResult, "4" );
        AssertTool.assertXPathEquals( "/contents/content/@key", jdomDocResult, fatherContentKey.toString(), daughterContentKey.toString(),
                                      sonContentKey.toString(), grandChildContentKey.toString() );

        AssertTool.assertXPathEquals( "/contents/content[ title = 'Father']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      sonContentKey.toString(), daughterContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[ title = 'Son']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      grandChildContentKey.toString() );
        AssertTool.assertSingleXPathValueEquals( "/contents/relatedcontents/@count", jdomDocResult, "3" );
        AssertTool.assertXPathEquals( "/contents/relatedcontents/content/@key", jdomDocResult, grandChildContentKey.toString(),
                                      sonContentKey.toString(), daughterContentKey.toString() );
    }

    @Test
    public void content_queried_with_related_parent_having_parent_existing_as_the_queried_content_is_listed_as_related_content_too()
    {
        // setup: create same content in two different categories
        ContentKey grandChildContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand child" ), "content-creator" ) );

        ContentKey sonContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Son", grandChildContentKey ), "content-creator" ) );

        ContentKey daughterContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Daughter" ), "content-creator" ) );

        ContentKey fatherContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Father", sonContentKey, daughterContentKey ),
                                        "content-creator" ) );

        // setup: verify that the content was created
        assertEquals( 4, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        String query = "categorykey = " + fixture.findCategoryByName( "MyCategory" ).getKey();
        String orderyBy = "@key desc";
        int index = 0;
        int count = 10;
        boolean includeData = true;
        int childrenLevel = 0;
        int parentLevel = 10;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByQuery( context, query, orderyBy, index, count, includeData, childrenLevel, parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        AssertTool.assertSingleXPathValueEquals( "/contents/@totalcount", jdomDocResult, "4" );
        AssertTool.assertXPathEquals( "/contents/content/@key", jdomDocResult, fatherContentKey.toString(), daughterContentKey.toString(),
                                      sonContentKey.toString(), grandChildContentKey.toString() );

        AssertTool.assertXPathEquals( "/contents/content[ title = 'Daughter']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      fatherContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[ title = 'Son']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      fatherContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[ title = 'Grand child']/relatedcontentkeys/relatedcontentkey/@key", jdomDocResult,
                                      sonContentKey.toString() );
        AssertTool.assertSingleXPathValueEquals( "/contents/relatedcontents/@count", jdomDocResult, "2" );
        AssertTool.assertXPathEquals( "/contents/relatedcontents/content/@key", jdomDocResult, sonContentKey.toString(),
                                      fatherContentKey.toString() );
    }

    @Test
    public void content_queried_with_both_related_child_and_parent_having_related_content_existing_as_the_queried_content_is_still_listed_as_related_content()
    {
        // setup: create same content in two different categories
        ContentKey grandChildContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand child" ), "content-creator" ) );

        ContentKey sonContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Son", grandChildContentKey ), "content-creator" ) );

        ContentKey daughterContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Daughter" ), "content-creator" ) );

        ContentKey fatherContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Father", sonContentKey, daughterContentKey ),
                                        "content-creator" ) );

        // setup: verify that the content was created
        assertEquals( 4, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        String query = "categorykey = " + fixture.findCategoryByName( "MyCategory" ).getKey();
        String orderyBy = "@key desc";
        int index = 0;
        int count = 10;
        boolean includeData = true;
        int childrenLevel = 10;
        int parentLevel = 10;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByQuery( context, query, orderyBy, index, count, includeData, childrenLevel, parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        AssertTool.assertSingleXPathValueEquals( "/contents/@totalcount", jdomDocResult, "4" );
        AssertTool.assertXPathEquals( "/contents/content/@key", jdomDocResult, fatherContentKey.toString(), daughterContentKey.toString(),
                                      sonContentKey.toString(), grandChildContentKey.toString() );

        AssertTool.assertXPathEquals( "/contents/content[title = 'Father']/relatedcontentkeys/relatedcontentkey [@level = 1]/@key",
                                      jdomDocResult, sonContentKey.toString(), daughterContentKey.toString() );

        AssertTool.assertXPathEquals( "/contents/content[title = 'Daughter']/relatedcontentkeys/relatedcontentkey[@level = -1]/@key",
                                      jdomDocResult, fatherContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[title = 'Son']/relatedcontentkeys/relatedcontentkey[@level = -1]/@key",
                                      jdomDocResult, fatherContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[title = 'Son']/relatedcontentkeys/relatedcontentkey[@level = 1]/@key",
                                      jdomDocResult, grandChildContentKey.toString() );
        AssertTool.assertXPathEquals( "/contents/content[title = 'Grand child']/relatedcontentkeys/relatedcontentkey[@level = -1]/@key",
                                      jdomDocResult, sonContentKey.toString() );
        AssertTool.assertSingleXPathValueEquals( "/contents/relatedcontents/@count", jdomDocResult, "4" );
        AssertTool.assertXPathEquals( "/contents/relatedcontents/content/@key", jdomDocResult, grandChildContentKey.toString(),
                                      sonContentKey.toString(), daughterContentKey.toString(), fatherContentKey.toString() );
    }


    private ContentData createMyRelatedContentData( String title, ContentKey... relatedContents )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyRelatedType" ).getContentTypeConfig() );
        if ( title != null )
        {
            contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );
        }
        if ( relatedContents != null && relatedContents.length > 0 )
        {
            RelatedContentsDataEntry relatedContentsDataEntry =
                new RelatedContentsDataEntry( contentData.getInputConfig( "myRelatedContent" ) );
            for ( ContentKey relatedKey : relatedContents )
            {
                relatedContentsDataEntry.add( new RelatedContentDataEntry( contentData.getInputConfig( "myRelatedContent" ), relatedKey ) );
            }
            contentData.add( relatedContentsDataEntry );
        }
        return contentData;
    }

    private CreateContentCommand createCreateContentCommand( String categoryName, ContentData contentData, String creatorUid )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( ContentStatus.APPROVED );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setAvailableFrom( DATE_TIME_2010_01_01.toDate() );
        createContentCommand.setAvailableTo( null );
        createContentCommand.setContentName( "testcontent" );
        return createContentCommand;
    }

    private UpdateContentCommand updateContentCommand( ContentKey contentKeyToUpdate, ContentData contentData, String updaterUid )
    {
        ContentEntity contentToUpdate = fixture.findContentByKey( contentKeyToUpdate );

        UpdateContentCommand command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( contentToUpdate.getMainVersion().getKey() );
        command.setUpdateAsMainVersion( true );
        command.setSyncAccessRights( false );
        command.setSyncRelatedContent( true );
        command.setContentKey( contentToUpdate.getKey() );
        command.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );
        command.setModifier( fixture.findUserByName( updaterUid ).getKey() );
        command.setPriority( 0 );
        command.setLanguage( fixture.findLanguageByCode( "en" ) );
        command.setStatus( ContentStatus.APPROVED );
        command.setContentData( contentData );
        command.setAvailableFrom( DATE_TIME_2010_01_01.toDate() );
        return command;
    }
}
