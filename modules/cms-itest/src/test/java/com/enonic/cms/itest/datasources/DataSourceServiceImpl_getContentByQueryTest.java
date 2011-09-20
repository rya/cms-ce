/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.datasources;

import com.enonic.cms.business.core.content.ContentService;
import com.enonic.cms.business.core.content.command.CreateContentCommand;
import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.core.internal.service.DataSourceServiceImpl;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.framework.time.MockTimeService;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DataSourceServiceImpl_getContentByQueryTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

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
        DomainFactory factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content",
                                                    ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

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
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();

        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(),
                                       configAsXmlBytes ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", User.ANONYMOUS_UID,
                                              User.ANONYMOUS_UID, false ) );
        fixture.save(
            factory.createCategory( "MyOtherCategory", "MyContentType", "MyUnit", User.ANONYMOUS_UID,
                                    User.ANONYMOUS_UID, false ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-creator",
                                                           "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-querier", "read, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyOtherCategory", "content-creator",
                                                           "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyOtherCategory", "content-querier", "read, admin_browse" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void content_from_two_different_categories()
    {
        // setup: create same content in two different categories
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), "Test title" ) );
        ContentKey content_1 = contentService.createContent( createCreateContentCommand( "MyCategory", contentData, "content-creator" ) );

        ContentKey content_2 =
            contentService.createContent( createCreateContentCommand( "MyOtherCategory", contentData, "content-creator" ) );

        // setup: verify that 2 content is created
        assertEquals( 2, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );

        String query = "title = 'Test title'";
        String orderyBy = "";
        int index = 0;
        int count = 10;
        boolean includeData = true;
        int childrenLevel = 0;
        int parentLevel = 0;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByQuery( context, query, orderyBy, index, count, includeData, childrenLevel, parentLevel );

        // verify
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();
        AssertTool.assertSingleXPathValueEquals( "/contents/@totalcount", jdomDocResult, "2" );
        AssertTool.assertXPathEquals( "/contents/content/@key", jdomDocResult, content_1, content_2 );
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
