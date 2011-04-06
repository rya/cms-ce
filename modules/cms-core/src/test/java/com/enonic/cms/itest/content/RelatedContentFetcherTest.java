/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.store.dao.ContentEntityDao;
import com.enonic.cms.store.dao.GroupEntityDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.business.core.content.ContentService;
import com.enonic.cms.business.core.content.RelatedContentFetcher;
import com.enonic.cms.business.core.content.access.ContentAccessResolver;
import com.enonic.cms.business.core.content.command.CreateContentCommand;
import com.enonic.cms.business.core.content.command.UpdateContentCommand;
import com.enonic.cms.business.core.security.SecurityHolder;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.RelatedChildContent;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedParentContent;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class RelatedContentFetcherTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupEntityDao groupEntityDao;

    private OverridingContentEntityDao contentDao;

    @Autowired
    protected ContentService contentService;

    private ContentAccessResolver contentAccessResolver;

    private DomainFixture fixture;

    public RelatedContentFetcherTest()
    {
        super();
    }

    @Before
    public void setUp()
    {
        contentAccessResolver = new ContentAccessResolver( groupEntityDao );

        fixture = new DomainFixture( hibernateTemplate );
        DomainFactory factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        //SecurityHolder.setUser( findUserByName( User.ANONYMOUS_UID ).getKey() );
        SecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyRelatingContent", "title" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addRelatedContentInput( "myRelatedContent", "relatedcontent", "contentdata/myRelatedContent", "My related content", false,
                                        true );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyRelatingContent", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyRelatingContent", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();

        contentDao = new OverridingContentEntityDao();
        contentDao.setHibernateTemplate( hibernateTemplate );
    }

    @Test
    public void xxxx()
    {
        /**
         *  Ole
         *   -> Kjell
         *      -> Ola
         *         -> Odin
         *
         */

        // setup:

        CreateContentCommand createCommand;

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Odin" );
        ContentKey odinContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Ola", odinContentKey );
        ContentKey olaContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Brita" );
        ContentKey britaContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Kjell", olaContentKey, britaContentKey );
        ContentKey kjellContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Ole", kjellContentKey );
        ContentKey oleContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Agnes", kjellContentKey );
        ContentKey agnesContentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( createContentResultSet( kjellContentKey ) );

        // verify:
        //assertEquals( 3, resultSet.getDinstinctSetOfContent().size() );
        Collection relatedContent = resultSet.getDistinctCollectionOfRelatedContent();
        System.out.println( "asdfasdf" );
    }

    @Test
    public void fetch_children_when_one_content_relates_another()
    {
        // setup
        CreateContentCommand createCommand;

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Son" );
        ContentKey sonContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Father", sonContentKey );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( 0 );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( createContentResultSet( fatherContentKey ) );

        // verify:
        assertEquals( 1, resultSet.getDinstinctSetOfContent().size() );

    }

    @Test
    public void fetch_parents_when_one_content_relates_another()
    {
        // setup
        CreateContentCommand createCommand;

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Son" );
        ContentKey sonContentKey = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Father", sonContentKey );
        ContentKey fatherContentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxChildrenLevel( 0 );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( createContentResultSet( sonContentKey ) );

        // verify:
        assertEquals( 1, resultSet.getDinstinctSetOfContent().size() );

    }

    @Test
    public void three_content_relates_each_other_in_a_circle()
    {
        // setup
        CreateContentCommand createCommand;

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content C" );
        ContentKey contentKeyC = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content A", contentKeyC );
        ContentKey contentKeyA = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content B", contentKeyA );
        ContentKey contentKeyB = contentService.createContent( createCommand );

        UpdateContentCommand updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( contentKeyC, "Content C", contentKeyB );
        contentService.updateContent( updateCommand );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( createContentResultSet( contentKeyA, contentKeyB, contentKeyC ) );

        // verify: all are in the main set
        assertEquals( 3, resultSet.getDinstinctSetOfContent().size() );
        assertEquals( 3, resultSet.getContentKeys().size() );
        assertEquals( convertToSet( fixture.findContentByKey( contentKeyA ), fixture.findContentByKey( contentKeyB ),
                                    fixture.findContentByKey( contentKeyC ) ), resultSet.getDinstinctSetOfContent() );
        assertRelatedContent( ContentKey.convertToList( contentKeyA, contentKeyB, contentKeyC ), resultSet.getContentKeys() );

        // verify: each content has a root related children and parents
        ContentEntity contentA = fixture.findContentByKey( contentKeyA );
        List<RelatedChildContent> contentArelatedChildren =
            convertRelatedChildContentToList( resultSet.getRootRelatedChildren( contentA.getMainVersion() ) );
        assertEquals( 1, contentArelatedChildren.size() );
        assertEquals( contentKeyC, contentArelatedChildren.get( 0 ).getContent().getKey() );
        List<RelatedParentContent> contentArelatedParents =
            convertRelatedParentContentToList( resultSet.getRootRelatedParents( contentA ) );
        assertEquals( 1, contentArelatedParents.size() );
        assertEquals( contentKeyB, contentArelatedParents.get( 0 ).getContent().getKey() );

        ContentEntity contentB = fixture.findContentByKey( contentKeyB );
        List<RelatedChildContent> contentBrelatedChildren =
            convertRelatedChildContentToList( resultSet.getRootRelatedChildren( contentB.getMainVersion() ) );
        assertEquals( 1, contentBrelatedChildren.size() );
        assertEquals( contentKeyA, contentBrelatedChildren.get( 0 ).getContent().getKey() );
        List<RelatedParentContent> contentBrelatedParents =
            convertRelatedParentContentToList( resultSet.getRootRelatedParents( contentB ) );
        assertEquals( 1, contentBrelatedParents.size() );
        assertEquals( contentKeyC, contentBrelatedParents.get( 0 ).getContent().getKey() );

        ContentEntity contentC = fixture.findContentByKey( contentKeyC );
        List<RelatedChildContent> contentCrelatedChildren =
            convertRelatedChildContentToList( resultSet.getRootRelatedChildren( contentC.getMainVersion() ) );
        assertEquals( 1, contentCrelatedChildren.size() );
        assertEquals( contentKeyB, contentCrelatedChildren.get( 0 ).getContent().getKey() );
        List<RelatedParentContent> contentCrelatedParents =
            convertRelatedParentContentToList( resultSet.getRootRelatedParents( contentC ) );
        assertEquals( 1, contentCrelatedParents.size() );
        assertEquals( contentKeyA, contentCrelatedParents.get( 0 ).getContent().getKey() );
    }

    @Test
    public void common_related_child_between_two_content_is_contained_in_root_related_children_for_both_content()
    {
        // setup
        CreateContentCommand createCommand;

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Common child 1" );
        ContentKey commonChild = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content A", commonChild );
        ContentKey contentKeyA = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content B", commonChild );
        ContentKey contentKeyB = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( 0 );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( createContentResultSet( contentKeyA, contentKeyB ) );

        // verify
        assertEquals( 1, resultSet.getDinstinctSetOfContent().size() );
        assertEquals( 1, resultSet.getContentKeys().size() );
        assertEquals( convertToSet( fixture.findContentByKey( commonChild ) ), resultSet.getDinstinctSetOfContent() );
        assertRelatedContent( ContentKey.convertToList( commonChild ), resultSet.getContentKeys() );

        ContentEntity contentA = fixture.findContentByKey( contentKeyA );
        List<RelatedChildContent> contentArelatedChildren =
            convertRelatedChildContentToList( resultSet.getRootRelatedChildren( contentA.getMainVersion() ) );
        assertEquals( 1, contentArelatedChildren.size() );
        assertEquals( commonChild, contentArelatedChildren.get( 0 ).getContent().getKey() );

        ContentEntity contentB = fixture.findContentByKey( contentKeyB );
        List<RelatedChildContent> contentBrelatedChildren =
            convertRelatedChildContentToList( resultSet.getRootRelatedChildren( contentB.getMainVersion() ) );
        assertEquals( 1, contentBrelatedChildren.size() );
        assertEquals( commonChild, contentBrelatedChildren.get( 0 ).getContent().getKey() );
    }

    @Test
    public void eternal_loop_is_prevented_for_related_children_with_circular_reference_and_all_other_are_included()
    {
        // setup content to update
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 1" );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 2" );
        ContentKey content_2 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 3" );
        ContentKey content_3 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 4" );
        ContentKey content_4 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 5" );
        ContentKey content_5 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 6" );
        ContentKey content_6 = contentService.createContent( createCommand );

        assertEquals( 6, fixture.countAllContent() );

        UpdateContentCommand updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_1, "Relating content 1 to 2 and 6", content_2, content_6 );
        contentService.updateContent( updateCommand );

        updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_2, "Relating content 2 to 3 and 5", content_3, content_5 );
        contentService.updateContent( updateCommand );

        updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_3, "Relating content 3 to 1 and 4", content_1, content_4 );
        contentService.updateContent( updateCommand );

        assertEquals( 6, fixture.countAllContent() );

        contentDao.setMaxExpectedFindRelatedChildrenByKeysAttempts( 6 );

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( 0 );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( fixture.findContentByKey( content_1 ) );

        List<ContentKey> expectedRelatedContentKeys = new ArrayList<ContentKey>();
        expectedRelatedContentKeys.add( content_1 );
        expectedRelatedContentKeys.add( content_2 );
        expectedRelatedContentKeys.add( content_3 );
        expectedRelatedContentKeys.add( content_4 );
        expectedRelatedContentKeys.add( content_5 );
        expectedRelatedContentKeys.add( content_6 );
        assertRelatedContent( expectedRelatedContentKeys, resultSet.getContentKeys() );

    }

    @Test
    public void eternal_loop_is_prevented_for_related_children_with_multiple_circular_references()
    {
        // setup content to update
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 1" );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 2" );
        ContentKey content_2 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 3" );
        ContentKey content_3 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 4" );
        ContentKey content_4 = contentService.createContent( createCommand );

        UpdateContentCommand updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_1, "Relating content 1 to 2, 3 and 4", content_2, content_3,
                                                                  content_4 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_2, "Relating content 2 to 1", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_3, "Relating content 3 to 1", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_4, "Relating content 4 to 1", content_1 );
        contentService.updateContent( updateCommand );

        assertEquals( 4, fixture.countAllContent() );

        contentDao.setMaxExpectedFindRelatedChildrenByKeysAttempts( 4 );

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( 0 );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( fixture.findContentByKey( content_1 ) );

        List<ContentKey> expectedRelatedContentKeys = new ArrayList<ContentKey>();
        expectedRelatedContentKeys.add( content_1 );
        expectedRelatedContentKeys.add( content_2 );
        expectedRelatedContentKeys.add( content_3 );
        expectedRelatedContentKeys.add( content_4 );
        assertRelatedContent( expectedRelatedContentKeys, resultSet.getContentKeys() );

    }

    @Test
    public void eternal_loop_is_prevented_for_related_children_of_children()
    {
        // setup content to update
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 1" );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 2" );
        ContentKey content_2 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 3" );
        ContentKey content_3 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 4" );
        ContentKey content_4 = contentService.createContent( createCommand );

        UpdateContentCommand updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_1, "Relating content 1 to 2", content_2 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_2, "Relating content 2 to 3", content_3 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_3, "Relating content 3 to 4", content_4 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_4, "Relating content 4 to 3", content_3 );
        contentService.updateContent( updateCommand );

        assertEquals( 4, fixture.countAllContent() );

        contentDao.setMaxExpectedFindRelatedChildrenByKeysAttempts( 4 );

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( 0 );
        relatedContentFetcher.setMaxChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( fixture.findContentByKey( content_1 ) );

        List<ContentKey> expectedRelatedContentKeys = new ArrayList<ContentKey>();
        expectedRelatedContentKeys.add( content_2 );
        expectedRelatedContentKeys.add( content_3 );
        expectedRelatedContentKeys.add( content_4 );
        assertRelatedContent( expectedRelatedContentKeys, resultSet.getContentKeys() );

    }

    @Test
    public void eternal_loop_is_prevented_for_related_parents_with_circular_reference()
    {
        // setup content to update
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 1" );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 2" );
        ContentKey content_2 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 3" );
        ContentKey content_3 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 4" );
        ContentKey content_4 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 5" );
        ContentKey content_5 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 6" );
        ContentKey content_6 = contentService.createContent( createCommand );

        assertEquals( 6, fixture.countAllContent() );

        UpdateContentCommand updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_1, "Relating content 1 to 2 and 6", content_2 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_2, "Relating content 2 to 3 and 5", content_3 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_3, "Relating content 3 to 1 and 4", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_4, "Relating content 4 to 1", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_5, "Relating content 5 to 2", content_2 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_6, "Relating content 6 to 3", content_3 );
        contentService.updateContent( updateCommand );

        assertEquals( 6, fixture.countAllContent() );

        contentDao.setMaxExpectedFindRelatedChildrenByKeysAttempts( 6 );

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );
        relatedContentFetcher.setMaxParentLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxChildrenLevel( 0 );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( fixture.findContentByKey( content_1 ) );

        List<ContentKey> expectedRelatedContentKeys = new ArrayList<ContentKey>();
        expectedRelatedContentKeys.add( content_1 );
        expectedRelatedContentKeys.add( content_2 );
        expectedRelatedContentKeys.add( content_3 );
        expectedRelatedContentKeys.add( content_4 );
        expectedRelatedContentKeys.add( content_5 );
        expectedRelatedContentKeys.add( content_6 );
        assertRelatedContent( expectedRelatedContentKeys, resultSet.getContentKeys() );
    }

    @Test
    public void eternal_loop_is_prevented_for_related_parent_children_with_circular_reference()
    {
        // setup content to update
        CreateContentCommand createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 1" );
        ContentKey content_1 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 2" );
        ContentKey content_2 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 3" );
        ContentKey content_3 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 4" );
        ContentKey content_4 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 5" );
        ContentKey content_5 = contentService.createContent( createCommand );

        createCommand = setupDefaultCreateContentCommandForMyRelatingContent( "Content not relating yet 6" );
        ContentKey content_6 = contentService.createContent( createCommand );

        assertEquals( 6, fixture.countAllContent() );

        UpdateContentCommand updateCommand =
            setupDefaultUpdateContentCommandForMyRelatingContent( content_1, "Relating content 1 to 2 and 6", content_2 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_2, "Relating content 2 to 3 and 5", content_3 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_3, "Relating content 3 to 1 and 4", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_4, "Relating content 4 to 1", content_1 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_5, "Relating content 5 to 2", content_2 );
        contentService.updateContent( updateCommand );

        updateCommand = setupDefaultUpdateContentCommandForMyRelatingContent( content_6, "Relating content 6 to 3", content_3 );
        contentService.updateContent( updateCommand );

        assertEquals( 6, fixture.countAllContent() );

        contentDao.setMaxExpectedFindRelatedChildrenByKeysAttempts( 6 );

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, contentAccessResolver );
        relatedContentFetcher.setRunningUser( fixture.findUserByName( "testuser" ) );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxParentChildrenLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxParentLevel( Integer.MAX_VALUE );
        relatedContentFetcher.setMaxChildrenLevel( 0 );
        relatedContentFetcher.setIncludeOfflineContent( true );

        RelatedContentResultSet resultSet = relatedContentFetcher.fetch( fixture.findContentByKey( content_1 ) );

        List<ContentKey> expectedRelatedContentKeys = new ArrayList<ContentKey>();
        expectedRelatedContentKeys.add( content_1 );
        expectedRelatedContentKeys.add( content_2 );
        expectedRelatedContentKeys.add( content_3 );
        expectedRelatedContentKeys.add( content_4 );
        expectedRelatedContentKeys.add( content_5 );
        expectedRelatedContentKeys.add( content_6 );
        assertRelatedContent( expectedRelatedContentKeys, resultSet.getContentKeys() );
    }

    private CreateContentCommand setupDefaultCreateContentCommandForMyRelatingContent( String title, ContentKey... contentToRelateTo )
    {
        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createCommand.setCategory( fixture.findCategoryByName( "MyCategory" ).getKey() );
        createCommand.setCreator( fixture.findUserByName( "testuser" ).getKey() );
        createCommand.setPriority( 0 );
        createCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createCommand.setStatus( ContentStatus.APPROVED );
        createCommand.setContentName( "testcontent" );

        CustomContentData contentData =
            new CustomContentData( fixture.findContentTypeByName( "MyRelatingContent" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );
        if ( contentToRelateTo != null )
        {
            RelatedContentsDataEntry relatedContentsDataEntry =
                new RelatedContentsDataEntry( contentData.getInputConfig( "myRelatedContent" ) );
            for ( ContentKey singleContentToRelateTo : contentToRelateTo )
            {
                relatedContentsDataEntry.add(
                    new RelatedContentDataEntry( contentData.getInputConfig( "myRelatedContent" ), singleContentToRelateTo ) );
            }
            contentData.add( relatedContentsDataEntry );
        }
        createCommand.setContentData( contentData );

        return createCommand;
    }

    private UpdateContentCommand setupDefaultUpdateContentCommandForMyRelatingContent( ContentKey contentKeyToUpdate, String title,
                                                                                       ContentKey... contentToRelateTo )
    {
        ContentEntity contentToUpdate = fixture.findContentByKey( contentKeyToUpdate );

        UpdateContentCommand command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( contentToUpdate.getMainVersion().getKey() );
        command.setUpdateAsMainVersion( true );
        command.setContentKey( contentToUpdate.getKey() );
        command.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );
        command.setModifier( fixture.findUserByName( "testuser" ).getKey() );
        command.setPriority( 0 );
        command.setLanguage( fixture.findLanguageByCode( "en" ) );
        command.setStatus( ContentStatus.APPROVED );
        command.setSyncAccessRights( false );

        CustomContentData contentData =
            new CustomContentData( fixture.findContentTypeByName( "MyRelatingContent" ).getContentTypeConfig() );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), title ) );
        if ( contentToRelateTo != null )
        {
            RelatedContentsDataEntry relatedContentsDataEntry =
                new RelatedContentsDataEntry( contentData.getInputConfig( "myRelatedContent" ) );
            for ( ContentKey singleContentToRelateTo : contentToRelateTo )
            {
                relatedContentsDataEntry.add(
                    new RelatedContentDataEntry( contentData.getInputConfig( "myRelatedContent" ), singleContentToRelateTo ) );
            }
            contentData.add( relatedContentsDataEntry );
        }
        command.setContentData( contentData );

        return command;
    }


    private void assertRelatedContent( Collection<ContentKey> expectedRelatedContentKeys, Collection<ContentKey> actual )
    {
        for ( ContentKey expectedContentKey : expectedRelatedContentKeys )
        {
            assertTrue( "expected related content with key: " + expectedContentKey, actual.contains( expectedContentKey ) );
        }

        assertEquals( "unexpected number of related content", expectedRelatedContentKeys.size(), actual.size() );
    }

    private ContentResultSet createContentResultSet( ContentKey... contentKeys )
    {
        List<ContentEntity> list = new ArrayList<ContentEntity>();
        for ( ContentKey contentKey : contentKeys )
        {
            list.add( fixture.findContentByKey( contentKey ) );
        }
        return new ContentResultSetNonLazy( list, 0, list.size() );
    }

    private Set<ContentEntity> convertToSet( ContentEntity... contents )
    {
        return new HashSet<ContentEntity>(Arrays.asList(contents));
    }

    private List<RelatedChildContent> convertRelatedChildContentToList( Iterable<RelatedChildContent> iterable )
    {
        List<RelatedChildContent> list = new ArrayList<RelatedChildContent>();
        for ( RelatedChildContent rcc : iterable )
        {
            list.add( rcc );
        }
        return list;
    }

    private List<RelatedParentContent> convertRelatedParentContentToList( Iterable<RelatedParentContent> iterable )
    {
        List<RelatedParentContent> list = new ArrayList<RelatedParentContent>();
        for ( RelatedParentContent item : iterable )
        {
            list.add( item );
        }
        return list;
    }

    class OverridingContentEntityDao
        extends ContentEntityDao
    {
        private int numberOfFindRelatedChildrenByKeysAttempts = 0;

        private int maxExpectedFindRelatedChildrenByKeysAttempts = Integer.MAX_VALUE;

        public void setMaxExpectedFindRelatedChildrenByKeysAttempts( int maxExpectedFindRelatedChildrenByKeysAttempts )
        {
            this.maxExpectedFindRelatedChildrenByKeysAttempts = maxExpectedFindRelatedChildrenByKeysAttempts;
        }

        @Override
        public Collection<RelatedChildContent> findRelatedChildrenByKeys( List<ContentVersionKey> contentVersionKeys )
        {
            numberOfFindRelatedChildrenByKeysAttempts++;
            if ( numberOfFindRelatedChildrenByKeysAttempts > maxExpectedFindRelatedChildrenByKeysAttempts )
            {
                fail( "max expected findRelatedChildrenByKeys attempts exceeded : " + numberOfFindRelatedChildrenByKeysAttempts );
            }
            return super.findRelatedChildrenByKeys( contentVersionKeys );
        }
    }

}
