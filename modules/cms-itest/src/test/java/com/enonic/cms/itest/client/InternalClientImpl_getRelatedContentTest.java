/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.model.GetRelatedContentsParams;
import com.enonic.cms.core.client.InternalClientImpl;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.time.MockTimeService;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import static com.enonic.cms.itest.util.AssertTool.assertXPathEquals;
import static org.junit.Assert.*;

public class InternalClientImpl_getRelatedContentTest
    extends AbstractSpringTest
{

    private static final DateTime DATE_TIME_2010_01_01 = new DateTime( 2010, 1, 1, 0, 0, 0, 0 );

    private static final DateTime DATE_TIME_2010_07_01_12_00_00_0 = new DateTime( 2010, 7, 1, 12, 0, 0, 0 );

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFixture fixture;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    private InternalClientImpl internalClient;

    @Autowired
    private PreviewService previewService;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    private ContentKey[] departments = new ContentKey[3];


    @Before
    public void setUp()
    {
        fixture = new DomainFixture( hibernateTemplate );
        DomainFactory factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        ServletRequestAccessor.setRequest( new MockHttpServletRequest( "GET", "/" ) );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content types: Person and Department
        ContentTypeConfigBuilder personCtyConf = new ContentTypeConfigBuilder( "Person", "name" );
        personCtyConf.startBlock( "Person" );
        personCtyConf.addInput( "name", "text", "contentdata/name", "Name", true );
        personCtyConf.endBlock();
        Document personConfigAsXmlBytes = XMLDocumentFactory.create( personCtyConf.toString() ).getAsJDOMDocument();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personConfigAsXmlBytes ) );

        ContentTypeConfigBuilder deptCtyConf = new ContentTypeConfigBuilder( "Department", "name" );
        deptCtyConf.startBlock( "Department" );
        deptCtyConf.addInput( "name", "text", "contentdata/name", "Name", true );
        deptCtyConf.addRelatedContentInput( "employee", "contentdata/employee", "Employee", false, true, "Person" );
        deptCtyConf.endBlock();
        Document deptConfigAsXmlBytes = XMLDocumentFactory.create( deptCtyConf.toString() ).getAsJDOMDocument();
        fixture.save(
            factory.createContentType( "Department", ContentHandlerName.CUSTOM.getHandlerClassShortName(), deptConfigAsXmlBytes ) );

        // setup archive: Company Unit with 2 categories: Employees and Departments.
        fixture.save( factory.createUnit( "Company", "en" ) );

        fixture.save( factory.createCategory( "Employees", "Person", "Company", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.save( factory.createCategory( "Departments", "Department", "Company", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save( factory.createCategoryAccessForUser( "Employees", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "Employees", "content-querier", "read, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "Departments", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "Departments", "content-querier", "read, admin_browse" ) );

        // create test data: 8 persons in 3 departments:
        // dept A with persons Anne, Birger, Cecilie, Daniel, Even og Frank
        // dept B with persons Anne, Cecilie, Even, Frank og Hanne
        // dept C with persons Anne, Cecilie, Even og Grete
        ContentKey empAnneKey = createAndStorePersonContent( "Anne" );
        ContentKey empBirgerKey = createAndStorePersonContent( "Birger" );
        ContentKey empCecilieKey = createAndStorePersonContent( "Cecilie" );
        ContentKey empDanielKey = createAndStorePersonContent( "Daniel" );
        ContentKey empEvenKey = createAndStorePersonContent( "Even" );
        ContentKey empFrankKey = createAndStorePersonContent( "Frank" );
        ContentKey empGreteKey = createAndStorePersonContent( "Grete" );
        ContentKey empHanneKey = createAndStorePersonContent( "Hanne" );

        departments[0] =
            createAndStoreDepartmentContent( "dept A", empAnneKey, empBirgerKey, empCecilieKey, empDanielKey, empEvenKey, empFrankKey );
        departments[1] =
            createAndStoreDepartmentContent( "dept B", empAnneKey, empBirgerKey, empCecilieKey, empEvenKey, empFrankKey, empHanneKey );
        departments[2] = createAndStoreDepartmentContent( "dept C", empAnneKey, empCecilieKey, empEvenKey, empGreteKey );

        fixture.flushAndClearHibernateSesssion();

        SecurityHolder.setUser( fixture.findUserByName( "content-querier" ).getKey() );
        SecurityHolder.setRunAsUser( fixture.findUserByName( "content-querier" ).getKey() );

        internalClient = new InternalClientImpl();
        internalClient.setSecurityService( securityService );
        internalClient.setContentService( contentService );
        internalClient.setPreviewService( previewService );
        internalClient.setContentDao( contentDao );
        internalClient.setUserDao( userDao );
        internalClient.setTimeService( new MockTimeService( DATE_TIME_2010_07_01_12_00_00_0 ) );
        internalClient.setLivePortalTraceService( livePortalTraceService );

    }

    @Test
    public void get_single_related_parent()
    {
        ContentEntity greteEntity = fixture.findContentByName( "Grete" );

        GetRelatedContentsParams params = new GetRelatedContentsParams();
        params.contentKeys = new int[]{greteEntity.getKey().toInt()};
        params.relation = -1;
        params.requireAll = false;

        Document resultAsJDom = internalClient.getRelatedContent( params );

        assertXPathEquals( "/contents/content/@key", resultAsJDom, departments[2].toString() );
        assertXPathEquals( "/contents/content/name", resultAsJDom, "dept C" );
    }

    @Test
    public void get_all_eight_related_children()
    {
        GetRelatedContentsParams params = new GetRelatedContentsParams();
        params.contentKeys = new int[]{departments[0].toInt(), departments[1].toInt(), departments[2].toInt()};
        params.relation = 1;
        params.requireAll = false;

        Document resultAsJDom = internalClient.getRelatedContent( params );

        Element rootElement = resultAsJDom.getRootElement();
        List persons = rootElement.getChildren( "content" );
        assertEquals( "Union of all department members", 8, persons.size() );
    }

    @Test
    public void get_three_out_of_eight_related_children_requireAll()
    {
        GetRelatedContentsParams params = new GetRelatedContentsParams();
        params.contentKeys = new int[]{departments[0].toInt(), departments[1].toInt(), departments[2].toInt()};
        params.relation = 1;
        params.requireAll = true;

        Document resultAsJDom = internalClient.getRelatedContent( params );

        Element rootElement = resultAsJDom.getRootElement();
        List<Element> persons = rootElement.getChildren( "content" );
        assertEquals( "Intersection of all department members", 3, persons.size() );

        Set<String> names = new HashSet<String>();
        for ( Element person : persons )
        {
            names.add( person.getChild( "name" ).getText() );
        }

        assertEquals( "3 different names are in alle departments", 3, names.size() );
        assertTrue( "Anne is in all departments", names.contains( "Anne" ) );
        assertTrue( "Cecilie is in all departments", names.contains( "Cecilie" ) );
        assertTrue( "Even is in all departments", names.contains( "Even" ) );
    }

    @Test
    public void get_all_three_related_parents()
    {
        ContentEntity birgerEntity = fixture.findContentByName( "Birger" );
        ContentEntity cecilieEntity = fixture.findContentByName( "Cecilie" );
        ContentEntity evenEntity = fixture.findContentByName( "Even" );
        ContentEntity frankEntity = fixture.findContentByName( "Frank" );

        GetRelatedContentsParams params = new GetRelatedContentsParams();
        params.contentKeys = new int[]{birgerEntity.getKey().toInt(), cecilieEntity.getKey().toInt(), evenEntity.getKey().toInt(),
            frankEntity.getKey().toInt()};
        params.relation = -1;
        params.requireAll = false;

        Document resultAsJDom = internalClient.getRelatedContent( params );
        Element rootElement = resultAsJDom.getRootElement();
        List departments = rootElement.getChildren( "content" );
        assertEquals( "Union of all employee departments", 3, departments.size() );
    }

    @Test
    public void get_two_out_of_three_related_parents_requireAll()
    {
        ContentEntity birgerEntity = fixture.findContentByName( "Birger" );
        ContentEntity cecilieEntity = fixture.findContentByName( "Cecilie" );
        ContentEntity evenEntity = fixture.findContentByName( "Even" );
        ContentEntity frankEntity = fixture.findContentByName( "Frank" );

        GetRelatedContentsParams params = new GetRelatedContentsParams();
        params.contentKeys = new int[]{birgerEntity.getKey().toInt(), cecilieEntity.getKey().toInt(), evenEntity.getKey().toInt(),
            frankEntity.getKey().toInt()};
        params.relation = -1;
        params.requireAll = true;

        Document resultAsJDom = internalClient.getRelatedContent( params );
        Element rootElement = resultAsJDom.getRootElement();
        List<Element> departments = rootElement.getChildren( "content" );
        assertEquals( "Union of all employee departments", 2, departments.size() );

        Set<String> names = new HashSet<String>();
        for ( Element department : departments )
        {
            names.add( department.getChild( "name" ).getText() );
        }

        assertEquals( "2 different represents all of these employees", 2, names.size() );
        assertTrue( "dept A represents all", names.contains( "dept A" ) );
        assertTrue( "dept B represents all", names.contains( "dept B" ) );
    }


    private ContentKey createAndStoreDepartmentContent( String name, ContentKey... employeeKeys )
    {
        CustomContentData deptContentData = new CustomContentData( fixture.findContentTypeByName( "Department" ).getContentTypeConfig() );
        DataEntryConfig nameInputConfig = deptContentData.getInputConfig( "name" );
        deptContentData.add( new TextDataEntry( nameInputConfig, name ) );
        RelatedContentsDataEntry relatedEmployeeList = new RelatedContentsDataEntry( deptContentData.getInputConfig( "employee" ) );
        for ( ContentKey employeeKey : employeeKeys )
        {
            relatedEmployeeList.add( new RelatedContentDataEntry( deptContentData.getInputConfig( "employee" ), employeeKey ) );
        }
        deptContentData.add( relatedEmployeeList );
        return contentService.createContent(
            createDepartmentCreateContentCommand( "content-creator", ContentStatus.APPROVED, name, deptContentData, DATE_TIME_2010_01_01,
                                                  null ) );
    }

    private CreateContentCommand createDepartmentCreateContentCommand( String creatorUid, ContentStatus contentStatus, String contentName,
                                                                       CustomContentData contentData, DateTime availableFrom,
                                                                       DateTime availableTo )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( "Departments" ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( contentName );

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

    private ContentKey createAndStorePersonContent( String name )
    {
        CustomContentData empContentData = new CustomContentData( fixture.findContentTypeByName( "Person" ).getContentTypeConfig() );
        empContentData.add( new TextDataEntry( empContentData.getInputConfig( "name" ), name ) );
        return contentService.createContent(
            createPersonCreateContentCommand( "content-creator", ContentStatus.APPROVED, name, empContentData, DATE_TIME_2010_01_01,
                                              null ) );
    }

    private CreateContentCommand createPersonCreateContentCommand( String creatorUid, ContentStatus contentStatus, String contentName,
                                                                   ContentData contentData, DateTime availableFrom, DateTime availableTo )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( "Employees" ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( contentName );

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
