/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.datasources;

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

import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.internal.service.DataSourceServiceImpl;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.itest.DomainFixture;

import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DataSourceServiceImpl_getContentByCategory_queryTest
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
    }

    @Test
    public void query_content_on_qualifiedName()
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();

        // setup content type, unit category, users, and rights
        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Creator", "testuserstore" );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-querier", "read, admin_browse" ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content assigned to content-creator
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "Test Dummy" ) );
        ContentKey expectedContentKey = contentService.createContent(
            createCreateContentCommand( "MyCategory", "content-creator", ContentStatus.APPROVED, new DateTime( 2020, 1, 1, 0, 0, 0, 0 ),
                                        "content-creator", contentData, new DateTime( 2010, 1, 1, 0, 0, 0, 0 ), null ) );

        UserEntity contentCreator = fixture.findUserByName( "content-creator" );

        AssignContentCommand assignCommand = new AssignContentCommand();
        assignCommand.setAssigneeKey( contentCreator.getKey() );
        assignCommand.setAssignerKey( contentCreator.getKey() );
        assignCommand.setContentKey( expectedContentKey );

        contentService.assignContent( assignCommand );

        // setup another content assigned to some one else
        contentService.createContent(
            createCreateContentCommand( "MyCategory", User.ROOT_UID, ContentStatus.APPROVED, new DateTime( 2020, 1, 1, 0, 0, 0, 0 ),
                                        User.ROOT_UID, contentData, new DateTime( 2010, 1, 1, 0, 0, 0, 0 ), null ) );

        // setup: verify that 2 content is created
        assertEquals( 2, fixture.countAllContent() );

        // exercise
        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int[] categoryKeys = new int[]{fixture.findCategoryByName( "MyCategory" ).getKey().toInt()};
        int levels = 1;
        String query = "assignee/qualifiedName = '" + fixture.findUserByName( "content-creator" ).getQualifiedName().toString() + "'";
        String orderyBy = "";
        int index = 0;
        int count = 100;
        boolean includeData = false;
        int childrenLevel = 0;
        int parentLevel = 0;

        XMLDocument xmlDocResult =
            dataSourceService.getContentByCategory( context, categoryKeys, levels, query, orderyBy, index, count, includeData,
                                                    childrenLevel, parentLevel );

        // verify
        AssertTool.assertXPathEquals( "/contents/content/@key", xmlDocResult.getAsJDOMDocument(),
                                      new String[]{expectedContentKey.toString()} );
    }

    private CreateContentCommand createCreateContentCommand( String categoryName, String creatorUid, ContentStatus contentStatus,
                                                             DateTime dueDate, String assigneeUserName, ContentData contentData,
                                                             DateTime availableFrom, DateTime availableTo )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testcontent" );

        if ( availableFrom != null )
        {
            createContentCommand.setAvailableFrom( availableFrom.toDate() );
        }
        if ( availableTo != null )
        {
            createContentCommand.setAvailableTo( availableTo.toDate() );
        }
        return createContentCommand;
    }
}
