package com.enonic.cms.itest.content;

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

import com.google.common.collect.Lists;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentIndexServiceImpl_ContentIndexQueryTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private ContentIndexService contentIndexService;

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

        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContent", "myTitle" );
        ctyconf.startBlock( "MyContent" );
        ctyconf.addInput( "myTitle", "text", "contentdata/my-title", "Title", true );
        ctyconf.endBlock();
        Document configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsJDOMDocument();

        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "content-querier", "read, admin_browse, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void having_one_matching_content_query_returns_one_when_index_is_0_and_count_1()
    {
        // setup
        contentService.createContent( createContentCommand( "a-1", "a-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 0 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 1, result.getLength() );
        assertEquals( 1, result.getTotalCount() );
    }

    @Test
    public void having_one_matching_content_query_returns_none_when_index_is_1_and_count_1()
    {
        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 0, result.getLength() );
        assertEquals( 1, result.getTotalCount() );
    }

    @Test
    public void having_two_matching_content_query_returns_one_when_index_is_1_and_count_1()
    {
        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 1, result.getLength() );
        assertEquals( 2, result.getTotalCount() );
    }

    @Test
    public void having_three_matching_content_query_returns_two_when_index_is_1_and_count_2()
    {
        // setup
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-3", "c-3", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 2 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 2, result.getLength() );
        assertEquals( 3, result.getTotalCount() );
    }

    @Test
    public void having_three_matching_content_query_returns_one_when_index_is_1_and_count_1()
    {
        // setup
        contentService.createContent( createContentCommand( "a-1", "a-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-1", "c-1", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-2", "c-2", "MyCategory" ) );
        contentService.createContent( createContentCommand( "c-3", "c-3", "MyCategory" ) );
        fixture.flushAndClearHibernateSesssion();

        // exercise
        ContentIndexQuery query = new ContentIndexQuery( "title CONTAINS \"c\"" );
        query.setSecurityFilter( Lists.newArrayList( fixture.findUserByName( "content-querier" ).getUserGroupKey() ) );
        query.setIndex( 1 );
        query.setCount( 1 );
        query.setCategoryFilter( Lists.newArrayList( fixture.findCategoryByName( "MyCategory" ).getKey() ) );
        ContentResultSet result = contentIndexService.query( query );

        // verify
        assertEquals( 3, result.getTotalCount() );
        assertEquals( 1, result.getLength() );
        assertEquals( "c-2", result.getContent( 0 ).getName() );
    }


    private TextDataEntry createTextDataEntry( String name, String value )
    {
        return new TextDataEntry( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig().getInputConfig( name ), value );
    }

    private CreateContentCommand createContentCommand( String name, String title, String categoryName )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
        contentData.add( createTextDataEntry( "myTitle", title ) );
        return createContentCommand( name, categoryName, contentData, "content-querier" );
    }

    private CreateContentCommand createContentCommand( String name, String categoryName, ContentData contentData, String creatorUid )
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
        createContentCommand.setContentName( name );
        return createContentCommand;
    }

}
