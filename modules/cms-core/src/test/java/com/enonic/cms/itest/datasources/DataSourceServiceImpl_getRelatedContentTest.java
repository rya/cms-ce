/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.datasources;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.portal.datasource.DataSourceContext;
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

import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.internal.service.DataSourceServiceImpl;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.itest.DomainFactory;

import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.security.user.User;

import static com.enonic.cms.itest.test.AssertTool.assertXPathEquals;
import static com.enonic.cms.itest.test.AssertTool.assertXPathNotExist;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DataSourceServiceImpl_getRelatedContentTest
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
    public void parentRelation_with_positive_childrenLevel_and_positive_parentLevel()
    {
        // setup:
        ContentKey grandSon = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand son" ), "content-creator" ) );

        ContentKey grandDaughter = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand daughter" ), "content-creator" ) );

        ContentKey son = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Son", grandSon, grandDaughter ), "content-creator" ) );

        ContentKey daughter = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Daughter" ), "content-creator" ) );

        ContentKey father = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Father", son, daughter ), "content-creator" ) );

        ContentKey grandFather = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand father", father ), "content-creator" ) );

        ContentKey grandMother = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand mother", father ), "content-creator" ) );

        ContentKey grandMothersMother = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand mothers mother", grandMother ),
                                        "content-creator" ) );

        ContentKey grandMothersFather = contentService.createContent(
            createCreateContentCommand( "MyCategory", createMyRelatedContentData( "Grand mothers father", grandMother ),
                                        "content-creator" ) );

        // setup: verify content is created
        assertEquals( 9, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        int[] contentKeys = new int[]{father.toInt()};
        int relation = -1;
        String query = "";
        String orderBy = "";
        int index = 0;
        int count = 100;
        boolean includeData = true;
        int childrenLevel = 10;
        int parentLevel = 10;

        XMLDocument xmlDocResult =
            dataSourceService.getRelatedContent( context, contentKeys, relation, query, orderBy, index, count, includeData, childrenLevel,
                                                 parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        assertXPathEquals( "/contents/@totalcount", jdomDocResult, "2" );
        assertXPathEquals( "/contents/content/@key", jdomDocResult, grandFather, grandMother );
        assertXPathEquals( "/contents/content[title = 'Grand father']/relatedcontentkeys/@count", jdomDocResult, "1" );
        assertXPathEquals( "/contents/content[title = 'Grand father']/relatedcontentkeys/relatedcontentkey [@level = 1]/@key",
                           jdomDocResult, father );
        assertXPathEquals( "/contents/content[title = 'Grand mother']/relatedcontentkeys/@count", jdomDocResult, "3" );
        assertXPathEquals( "/contents/content[title = 'Grand mother']/relatedcontentkeys/relatedcontentkey[@level = 1]/@key", jdomDocResult,
                           father );
        assertXPathEquals( "/contents/content[title = 'Grand mother']/relatedcontentkeys/relatedcontentkey[@level = -1]/@key",
                           jdomDocResult, grandMothersMother, grandMothersFather );

        assertXPathEquals( "/contents/relatedcontents/@count", jdomDocResult, "7" );
        assertXPathEquals( "/contents/relatedcontents/content/@key", jdomDocResult, father, son, daughter, grandSon, grandDaughter,
                           grandMothersMother, grandMothersFather );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Father']/relatedcontentkeys/@count", jdomDocResult, "2" );
        assertXPathEquals( "/contents/relatedcontents/content[title = 'Father']/relatedcontentkeys/relatedcontentkey[@level = 1]/@key",
                           jdomDocResult, son, daughter );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Son']/relatedcontentkeys/@count", jdomDocResult, "2" );
        assertXPathEquals( "/contents/relatedcontents/content[title = 'Son']/relatedcontentkeys/relatedcontentkey[@level = 1]/@key",
                           jdomDocResult, grandDaughter, grandSon );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Daughter']/relatedcontentkeys/@count", jdomDocResult, "0" );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Daughter']/relatedcontentkeys/relatedcontentkey", jdomDocResult );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Grand son']/relatedcontentkeys/@count", jdomDocResult, "0" );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Grand son']/relatedcontentkeys/relatedcontentkey", jdomDocResult );
        assertXPathEquals( "/contents/relatedcontents/content[title = 'Grand daughter']/relatedcontentkeys/@count", jdomDocResult, "0" );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Grand daughter']/relatedcontentkeys/relatedcontentkey",
                             jdomDocResult );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Grand mothers mother']/relatedcontentkeys/@count", jdomDocResult,
                           "0" );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Grand mothers mother']/relatedcontentkeys/relatedcontentkey",
                             jdomDocResult );

        assertXPathEquals( "/contents/relatedcontents/content[title = 'Grand mothers father']/relatedcontentkeys/@count", jdomDocResult,
                           "0" );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Grand mothers father']/relatedcontentkeys/relatedcontentkey",
                             jdomDocResult );

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
}
