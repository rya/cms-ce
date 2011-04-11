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
import com.enonic.cms.core.preview.ContentPreviewContext;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.domain.content.ContentAndVersion;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.portal.datasource.DataSourceContext;
import com.enonic.cms.domain.security.user.User;

import static com.enonic.cms.itest.test.AssertTool.assertSingleXPathValueEquals;
import static com.enonic.cms.itest.test.AssertTool.assertXPathEquals;
import static com.enonic.cms.itest.test.AssertTool.assertXPathNotExist;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Aug 3, 2010
 * Time: 9:17:14 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class DatasourceServiceImpl_getContentTest
{
    private static final DateTime DATE_TIME_2010_01_01 = new DateTime( 2010, 1, 1, 0, 0, 0, 0 );

    private static final DateTime DATE_TIME_2010_07_01_12_00_00_0 = new DateTime( 2010, 7, 1, 12, 0, 0, 0 );

    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private SecurityService securityService;

    private DataSourceServiceImpl dataSourceService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private UserDao userDao;

    private XMLBytes personConfigAsXmlBytes;

    private MockHttpServletRequest httpRequest;

    @Before
    public void setUp()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        httpRequest = new MockHttpServletRequest( "GET", "/" );
        ServletRequestAccessor.setRequest( httpRequest );

        fixture.save( factory.createUnit( "MyUnit", "en" ) );

        fixture.createAndStoreNormalUserWithUserGroup( "content-creator", "Creator", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "content-querier", "Querier", "testuserstore" );

        // setup content type: Person
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.addRelatedContentInput( "my-relatedcontent", "relatedcontent", "contentdata/my-relatedcontent", "My relatedcontent", false,
                                        false );
        ctyconf.addRelatedContentInput( "my-relatedcontents", "relatedcontent", "contentdata/my-relatedcontents", "My relatedcontents",
                                        false, true );
        ctyconf.endBlock();
        personConfigAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();

        fixture.save(
            factory.createContentType( "MyPersonType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), personConfigAsXmlBytes ) );

        fixture.save(
            factory.createCategory( "MyPersonCategory", "MyPersonType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyPersonCategory", "content-querier", "read, admin_browse" ) );

        // setup content type: Related
        ContentTypeConfigBuilder ctyconfMyRelated = new ContentTypeConfigBuilder( "MyRelatedType", "title" );
        ctyconfMyRelated.startBlock( "General" );
        ctyconfMyRelated.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconfMyRelated.addRelatedContentInput( "myRelatedContent", "relatedcontent", "contentdata/myRelatedContent", "My related content",
                                                 false, true );
        ctyconfMyRelated.endBlock();
        XMLBytes myRelatedconfigAsXmlBytes = XMLDocumentFactory.create( ctyconfMyRelated.toString() ).getAsBytes();

        fixture.save(
            factory.createContentType( "MyRelatedType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), myRelatedconfigAsXmlBytes ) );

        fixture.save(
            factory.createCategory( "MyRelatedCategory", "MyRelatedType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, false ) );

        fixture.save(
            factory.createCategoryAccessForUser( "MyRelatedCategory", "content-creator", "read, create, approve, admin_browse" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyRelatedCategory", "content-querier", "read, admin_browse" ) );

        dataSourceService = new DataSourceServiceImpl();
        dataSourceService.setUserDao( userDao );
        dataSourceService.setContentDao( contentDao );
        dataSourceService.setContentService( contentService );
        dataSourceService.setSecurityService( securityService );
        dataSourceService.setTimeService( new MockTimeService( DATE_TIME_2010_07_01_12_00_00_0 ) );
    }

    @Test
    public void getContent()
    {
        ContentKey expectedContentKey = createPersonContent( "Test Dummy" );

        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );
        String query = "";
        String orderBy = "";
        int index = 0;
        int count = 100;
        boolean includeData = false;
        int childrenLevel = 0;
        int parentLevel = 0;

        int[] contentKeys = new int[]{expectedContentKey.toInt()};
        XMLDocument xmlDocResult =
            dataSourceService.getContent( context, contentKeys, query, orderBy, index, count, includeData, childrenLevel, parentLevel );

        assertXPathEquals( "/contents/content/@key", xmlDocResult.getAsJDOMDocument(), expectedContentKey.toString() );
    }

    @Test
    public void getContent_10_parameters_datasource()
    {
        ContentKey expectedContentKey = createPersonContent( "Test Dummy" );

        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 0;
        int parentChildrenLevel = 1;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;

        ContentTypeEntity personContentType = fixture.findContentTypeByName( "MyPersonType" );

        int[] filterByContentTypes = new int[]{personContentType.getKey()};

        // Query the content with access
        int[] contentKeys = new int[]{expectedContentKey.toInt()};
        XMLDocument xmlDocResult =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );
        assertXPathEquals( "/contents/content/@key", xmlDocResult.getAsJDOMDocument(), expectedContentKey.toString() );
    }

    @Test
    public void getContent_with_related()
    {
        ContentKey requested1Grandchild1 = createPersonContent( "Grandchild of 1" );
        ContentKey requested1Child1 = createPersonContentWithRelatedContent( "Child of 1", requested1Grandchild1 );
        ContentKey requested2Child1 = createPersonContent( "Child of 2" );
        ContentKey requested1 = createPersonContentWithRelatedContent( "Requested content 1", requested1Child1 );
        ContentKey requested2 = createPersonContentWithRelatedContent( "Requested content 2", requested2Child1 );

        DataSourceContext context = new DataSourceContext();
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 2;
        int parentChildrenLevel = 0;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{requested1.toInt(), requested2.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathEquals( "/contents/content/@key", resultAsJDOM, requested1.toString(), requested2.toString() );
        assertXPathEquals( "/contents/content[ @key=" + requested1 + "]/relatedcontentkeys/@count", resultAsJDOM, "1" );
        assertXPathEquals( "/contents/content[ @key=" + requested1 + "]/relatedcontentkeys/relatedcontentkey/@key", resultAsJDOM,
                           requested1Child1.toString() );
        assertXPathEquals(
            "/contents/content[ @key=" + requested1 + "]/relatedcontentkeys/relatedcontentkey[ @key = " + requested1Child1 + "]/@level",
            resultAsJDOM, "1" );

        assertXPathEquals( "/contents/relatedcontents/content/@key", resultAsJDOM, requested1Child1.toString(), requested2Child1.toString(),
                           requested1Grandchild1.toString() );


    }

    @Test
    public void getContent_in_preview_of_draft_content_returns_previewed_content()
    {
        // Setup
        ContentKey contentKey = createPersonContent( "Content 1", ContentStatus.DRAFT );

        ContentEntity contentInPreview = new ContentEntity( fixture.findContentByKey( contentKey ) );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        contentVersionInPreview.setTitle( "Previewed" );
        contentInPreview.setMainVersion( contentVersionInPreview );
        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentInPreview.getMainVersion() );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );

        DataSourceContext context = new DataSourceContext( previewContext );
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 0;
        int parentChildrenLevel = 0;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{contentKey.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();
        assertXPathEquals( "/contents/content/@key", resultAsJDOM, contentKey.toString() );
        assertSingleXPathValueEquals( "/contents/content/title", resultAsJDOM, "Previewed" );
    }

    @Test
    public void getContent_in_preview_of_archived_content_returns_previewed_content()
    {
        // Setup
        ContentKey contentKey = createPersonContent( "Content 1", ContentStatus.ARCHIVED );

        ContentEntity contentInPreview = new ContentEntity( fixture.findContentByKey( contentKey ) );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        contentVersionInPreview.setTitle( "Previewed" );
        contentInPreview.setMainVersion( contentVersionInPreview );
        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentInPreview.getMainVersion() );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );

        DataSourceContext context = new DataSourceContext( previewContext );
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 0;
        int parentChildrenLevel = 0;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{contentKey.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();
        assertXPathEquals( "/contents/content/@key", resultAsJDOM, contentKey.toString() );
        assertSingleXPathValueEquals( "/contents/content/title", resultAsJDOM, "Previewed" );
    }

    @Test
    public void getContent_in_preview_of_content_with_related_draft_returns_previewed_content_with_related()
    {
        ContentKey relatedTo1 = createPersonContent( "Child of requested content", ContentStatus.DRAFT );
        ContentKey content1 = createPersonContentWithRelatedContent( "Request content", relatedTo1 );
        ContentKey parentOf1 = createPersonContentWithRelatedContent( "Parent to requested content", ContentStatus.DRAFT, content1 );

        ContentEntity contentInPreview = new ContentEntity( fixture.findContentByKey( content1 ) );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentVersionInPreview );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );
        DataSourceContext context = new DataSourceContext( previewContext );
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 1;
        int childrenLevel = 1;
        int parentChildrenLevel = 1;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{content1.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathEquals( "/contents/content/@key", resultAsJDOM, content1.toString() );
        assertXPathEquals( "/contents/content/relatedcontentkeys/@count", resultAsJDOM, "2" );
        assertXPathEquals( "/contents/content/relatedcontentkeys/relatedcontentkey/@key", resultAsJDOM, parentOf1.toString(),
                           relatedTo1.toString() );

        assertXPathEquals( "/contents/content/relatedcontentkeys/relatedcontentkey[ @key = " + relatedTo1 + "]/@level", resultAsJDOM, "1" );

        assertXPathEquals( "/contents/content/relatedcontentkeys/relatedcontentkey[ @key = " + parentOf1 + "]/@level", resultAsJDOM, "-1" );

        assertXPathEquals( "/contents/relatedcontents/@count", resultAsJDOM, "3" );
        assertXPathEquals( "/contents/relatedcontents/content/@key", resultAsJDOM, relatedTo1.toString(), parentOf1.toString(),
                           content1.toString() );
    }

    @Test
    public void getContent_in_preview_of_content_with_related_draft_where_relation_is_unsaved_returns_previewed_content_with_related()
    {
        // setup: persist content
        ContentKey sonContentKey = createPersonContent( "Child of requested content", ContentStatus.DRAFT );
        ContentKey fatherContentKey = createPersonContentWithRelatedContent( "Request content", null );

        ContentEntity contentInPreview = new ContentEntity( fixture.findContentByKey( fatherContentKey ) );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyPersonType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), "Requested content" ) );
        contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "my-relatedcontent" ), sonContentKey ) );
        contentVersionInPreview.setContentData( contentData );
        contentVersionInPreview.addRelatedChild( fixture.findContentByKey( sonContentKey ) );

        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentVersionInPreview );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );
        DataSourceContext context = new DataSourceContext( previewContext );
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 1;
        int parentChildrenLevel = 0;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{fatherContentKey.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathEquals( "/contents/content/@key", resultAsJDOM, fatherContentKey.toString() );
        assertXPathEquals( "/contents/content/relatedcontentkeys/@count", resultAsJDOM, "1" );
        assertXPathEquals( "/contents/content/relatedcontentkeys/relatedcontentkey/@key", resultAsJDOM, sonContentKey.toString() );

        assertXPathEquals( "/contents/content/relatedcontentkeys/relatedcontentkey[ @key = " + sonContentKey + "]/@level", resultAsJDOM,
                           "1" );

        assertXPathEquals( "/contents/relatedcontents/@count", resultAsJDOM, "1" );
        assertXPathEquals( "/contents/relatedcontents/content/@key", resultAsJDOM, sonContentKey.toString() );
    }


    @Test
    public void getContent_in_preview_of_content_with_removed_related_relation_is_unsaved_returns_previewed_content_without_related()
    {
        // setup: father with relation to son and daughter and the son having a child
        ContentKey grandSon = contentService.createContent(
            createCreateContentCommand( "MyRelatedCategory", createMyRelatedContentData( "Grand son" ), "content-creator" ) );

        ContentKey son = contentService.createContent(
            createCreateContentCommand( "MyRelatedCategory", createMyRelatedContentData( "Son", grandSon ), "content-creator" ) );

        ContentKey daughter = contentService.createContent(
            createCreateContentCommand( "MyRelatedCategory", createMyRelatedContentData( "Daughter" ), "content-creator" ) );

        ContentKey father = contentService.createContent(
            createCreateContentCommand( "MyRelatedCategory", createMyRelatedContentData( "Father", son, daughter ), "content-creator" ) );

        // setup: verify content is created
        assertEquals( 4, fixture.countAllContent() );

        // exercise: relation from father to son is removed
        ContentEntity contentInPreview = new ContentEntity( fixture.findContentByKey( father ) );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        contentVersionInPreview.setContentData( createMyRelatedContentData( "Father", daughter ) );
        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentVersionInPreview );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );
        DataSourceContext context = new DataSourceContext( previewContext );

        context.setUser( fixture.findUserByName( "content-querier" ) );

        int[] contentKeys = new int[]{father.toInt()};
        int parentLevel = 0;
        int childrenLevel = 1;
        int parentChildrenLevel = 0;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        XMLDocument xmlDocResult =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // verify: result does not include father's relation to the son
        Document jdomDocResult = xmlDocResult.getAsJDOMDocument();

        assertXPathEquals( "/contents/content/@key", jdomDocResult, father );
        assertXPathEquals( "/contents/content[title = 'Father']/relatedcontentkeys/@count", jdomDocResult, "1" );
        assertXPathEquals( "/contents/content[title = 'Father']/relatedcontentkeys/relatedcontentkey [@level = 1]/@key", jdomDocResult,
                           daughter );

        assertXPathEquals( "/contents/relatedcontents/content/@key", jdomDocResult, daughter );
        assertXPathNotExist( "/contents/relatedcontents/content[title = 'Son']", jdomDocResult );
    }

    @Test
    public void getContent_in_preview_of_content_with_related_approved_and_draft_returns_previewed_content_with_related()
    {
        ContentKey relatedTo1 = createPersonContent( "Related draft", ContentStatus.DRAFT );
        ContentKey relatedTo2 = createPersonContent( "Related approved", ContentStatus.APPROVED );
        ContentKey parent1 = createPersonContentWithRelatedContents( "Parent content 1", ContentStatus.DRAFT, relatedTo1, relatedTo2 );

        ContentEntity contentInPreview = fixture.findContentByKey( parent1 );
        ContentVersionEntity contentVersionInPreview = new ContentVersionEntity( contentInPreview.getMainVersion() );
        ContentAndVersion contentAndVersionInPreview = new ContentAndVersion( contentInPreview, contentVersionInPreview );
        ContentPreviewContext contentPreviewContext = new ContentPreviewContext( contentAndVersionInPreview );
        PreviewContext previewContext = new PreviewContext( contentPreviewContext );
        DataSourceContext context = new DataSourceContext( previewContext );
        context.setUser( fixture.findUserByName( "content-querier" ) );
        int parentLevel = 0;
        int childrenLevel = 1;
        int parentChildrenLevel = 1;
        boolean updateStatistics = false;
        boolean relatedTitlesOnly = false;
        boolean includeUserRights = false;
        int[] filterByCategories = new int[]{};
        boolean categoryRecursive = false;
        int[] filterByContentTypes = null;

        // Exercise
        int[] contentKeys = new int[]{parent1.toInt()};
        XMLDocument resultAsXMLDocument =
            dataSourceService.getContent( context, contentKeys, parentLevel, childrenLevel, parentChildrenLevel, updateStatistics,
                                          relatedTitlesOnly, includeUserRights, filterByCategories, categoryRecursive,
                                          filterByContentTypes );

        // Verify
        Document resultAsJDOM = resultAsXMLDocument.getAsJDOMDocument();

        assertXPathEquals( "/contents/content/@key", resultAsJDOM, parent1.toString() );
        assertXPathEquals( "/contents/relatedcontents/content/@key", resultAsJDOM, relatedTo2.toString(), relatedTo1.toString() );
    }

    private ContentKey createPersonContent( String name )
    {
        return createPersonContentWithRelatedContent( name, ContentStatus.APPROVED, null );
    }

    private ContentKey createPersonContent( String name, ContentStatus status )
    {
        return createPersonContentWithRelatedContent( name, status, null );
    }

    private ContentKey createPersonContentWithRelatedContent( String name, ContentKey relatedContent )
    {
        return createPersonContentWithRelatedContent( name, ContentStatus.APPROVED, relatedContent );
    }

    private ContentKey createPersonContentWithRelatedContent( String name, ContentStatus status, ContentKey relatedContent )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyPersonType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), name ) );
        if ( relatedContent != null )
        {
            contentData.add( new RelatedContentDataEntry( contentData.getInputConfig( "my-relatedcontent" ), relatedContent ) );
        }

        ContentKey expectedContentKey = contentService.createContent(
            createCreateContentCommand( "MyPersonCategory", "content-creator", status, new DateTime( 2020, 1, 1, 0, 0, 0, 0 ),
                                        "content-creator", contentData, new DateTime( 2010, 1, 1, 0, 0, 0, 0 ), null ) );
        return expectedContentKey;
    }

    private ContentKey createPersonContentWithRelatedContents( String name, ContentStatus status, ContentKey... relatedContents )
    {
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyPersonType" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "name" ), name ) );
        if ( relatedContents != null )
        {
            RelatedContentsDataEntry relatedContentsDataEntry =
                new RelatedContentsDataEntry( contentData.getInputConfig( "my-relatedcontents" ) );
            contentData.add( relatedContentsDataEntry );
            for ( ContentKey relatedContent : relatedContents )
            {
                relatedContentsDataEntry.add(
                    new RelatedContentDataEntry( contentData.getInputConfig( "my-relatedcontents" ), relatedContent ) );
            }
        }

        ContentKey expectedContentKey = contentService.createContent(
            createCreateContentCommand( "MyPersonCategory", "content-creator", status, new DateTime( 2020, 1, 1, 0, 0, 0, 0 ),
                                        "content-creator", contentData, new DateTime( 2010, 1, 1, 0, 0, 0, 0 ), null ) );
        return expectedContentKey;
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

