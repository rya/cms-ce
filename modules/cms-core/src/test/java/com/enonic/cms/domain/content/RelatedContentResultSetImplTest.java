/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import org.junit.Test;

import com.enonic.cms.core.content.resultset.RelatedChildContent;
import com.enonic.cms.core.content.resultset.RelatedContent;
import com.enonic.cms.core.content.resultset.RelatedContentResultSetImpl;

import static org.junit.Assert.*;

/**
 * Nov 3, 2010
 */
public class RelatedContentResultSetImplTest
{

    @Test
    public void overwrite()
    {
        // setup
        int parent_contentKey = 1848;
        int parent_versionKey = 2997;

        int child1_contentKey = 1846;
        int child1_versionKey = 2993;

        ContentEntity child1Content = createContent( child1_contentKey, child1_versionKey );
        ContentEntity parentContent = createContent( parent_contentKey, parent_versionKey, child1Content );

        RelatedContentResultSetImpl set1 = new RelatedContentResultSetImpl();
        RelatedChildContent rcc_child1 = createRelatedChildContent( parent_versionKey, child1Content );
        set1.addRootRelatedChild( rcc_child1 );
        set1.add( rcc_child1 );

        RelatedContentResultSetImpl set2 = new RelatedContentResultSetImpl();
        rcc_child1 = createRelatedChildContent( parent_versionKey, child1Content );
        set2.addRootRelatedChild( rcc_child1 );
        set2.add( rcc_child1 );

        // verify setup
        assertEquals( 1, set1.size() );
        assertEquals( 1, set2.size() );

        // exercise
        set1.overwrite( set2 );

        // verify
        assertFalse( set1.isEmpty() );
        assertEquals( 1, set1.size() );
        assertSet( ContentKey.convertToSet(new ContentKey(child1_contentKey)), ContentKey.convertToSet( set1.getContentKeys() ) );
        assertEquals( createContentSet( child1Content ), set1.getDinstinctSetOfContent() );

        List<RelatedContent> rootRelatedChildren =
            convertToRelatedContentList( set1.getRootRelatedChildren( parentContent.getMainVersion() ) );
        assertEquals( 1, rootRelatedChildren.size() );
        assertEquals( createRelatedContentList( rcc_child1 ), rootRelatedChildren );

        assertSet( createRelatedContentSet( rcc_child1 ), createRelatedContentSet( set1.getDistinctCollectionOfRelatedContent() ) );

        // verify: root related children
        /*List<RelatedChildContent> actualRootRelatedChildren =
            convertToRelatedChildContentList( set1.getRootRelatedChildren( createVersion( parent_versionKey ) ) );
        assertEquals( 1, actualRootRelatedChildren.size() );

        List<ContentKey> actualRootRelatedChildrenKeys = extractContentKeysFromRelatedChildContent( actualRootRelatedChildren );
        assertEquals( ContentKey.convertToList( new int[]{child2Content.getKey().toInt()} ), actualRootRelatedChildrenKeys );*/
    }


    @Test
    public void retainRelatedRootChildren_having_two_children_and_retaining_only_one()
    {
        // setup
        RelatedContentResultSetImpl set = new RelatedContentResultSetImpl();

        int parent_contentKey = 1848;
        int parent_versionKey = 2997;

        int child1_contentKey = 1846;
        int child1_versionKey = 2993;

        int child1child1_contentKey = 1849;
        int child1child1_versionKey = 3000;

        int child2_contentKey = 1847;
        int child2_versionKey = 2995;

        ContentEntity child1child1Content = createContent( child1child1_contentKey, child1child1_versionKey );
        ContentEntity child1Content = createContent( child1_contentKey, child1_versionKey, child1child1Content );
        ContentEntity child2Content = createContent( child2_contentKey, child2_versionKey, child1child1Content );
        ContentEntity parentContent = createContent( parent_contentKey, parent_versionKey, child1Content, child2Content );

        RelatedChildContent rcc_child1 = createRelatedChildContent( parent_versionKey, child1Content );
        set.addRootRelatedChild( rcc_child1 );
        set.add( rcc_child1 );

        RelatedChildContent rcc_child1child1 = createRelatedChildContent( child1_versionKey, child1child1Content );
        set.add( rcc_child1child1 );

        RelatedChildContent rcc_child2 = createRelatedChildContent( parent_versionKey, child2Content );
        set.addRootRelatedChild( rcc_child2 );
        set.add( rcc_child2 );

        // verify setup
        assertEquals( 3, set.size() );
        assertSet( ContentKey.convertToSet( new ContentKey( child1_contentKey ), new ContentKey( child1child1_contentKey ),
                                            new ContentKey( child2_contentKey ) ), ContentKey.convertToSet( set.getContentKeys() ) );
        assertEquals( createContentSet( child1Content, child1child1Content, child2Content ), set.getDinstinctSetOfContent() );
        assertEquals( createRelatedContentList( rcc_child1, rcc_child2 ),
                      convertToRelatedContentList( set.getRootRelatedChildren( parentContent.getMainVersion() ) ) );
        assertSet( createRelatedContentSet( rcc_child1, rcc_child1child1, rcc_child2 ),
                   createRelatedContentSet( set.getDistinctCollectionOfRelatedContent() ) );

        // exercise
        List<ContentKey> childrenToRetain = new ArrayList<ContentKey>();
        childrenToRetain.add( child2Content.getKey() );
        set.retainRelatedRootChildren( new ContentVersionKey( parent_versionKey ), childrenToRetain );

        // verify
        assertFalse( set.isEmpty() );
        assertEquals( 1, set.size() );
        assertSet( ContentKey.convertToSet( new ContentKey( child2_contentKey ) ), ContentKey.convertToSet( set.getContentKeys() ) );
        assertEquals( createContentSet( child2Content ), set.getDinstinctSetOfContent() );
        assertEquals( createRelatedContentList( rcc_child2 ),
                      convertToRelatedContentList( set.getRootRelatedChildren( parentContent.getMainVersion() ) ) );
        assertSet( createRelatedContentSet( rcc_child2 ), createRelatedContentSet( set.getDistinctCollectionOfRelatedContent() ) );

        // verify: root related children
        List<RelatedChildContent> actualRootRelatedChildren =
            convertToRelatedChildContentList( set.getRootRelatedChildren( createVersion( parent_versionKey ) ) );
        assertEquals( 1, actualRootRelatedChildren.size() );

        List<ContentKey> actualRootRelatedChildrenKeys = extractContentKeysFromRelatedChildContent( actualRootRelatedChildren );
        assertEquals( ContentKey.convertToList( new int[]{child2Content.getKey().toInt()} ), actualRootRelatedChildrenKeys );
    }

    @Test
    public void retainRelatedRootChildren_having_two_children_and_retaining_both()
    {
        // setup
        RelatedContentResultSetImpl set = new RelatedContentResultSetImpl();

        int parent_contentKey = 1848;
        int parent_versionKey = 2997;

        int child1_contentKey = 1846;
        int child1_versionKey = 2993;

        int child1child1_contentKey = 1849;
        int child1child1_versionKey = 3000;

        int child2_contentKey = 1847;
        int child2_versionKey = 2995;

        ContentEntity child1child1Content = createContent( child1child1_contentKey, child1child1_versionKey );
        ContentEntity child1Content = createContent( child1_contentKey, child1_versionKey, child1child1Content );
        ContentEntity child2Content = createContent( child2_contentKey, child2_versionKey, child1child1Content );
        ContentEntity parentContent = createContent( parent_contentKey, parent_versionKey, child1Content, child2Content );

        RelatedChildContent rcc_child1 = createRelatedChildContent( parent_versionKey, child1Content );
        set.addRootRelatedChild( rcc_child1 );
        set.add( rcc_child1 );

        RelatedChildContent rcc_child1child1 = createRelatedChildContent( child1_versionKey, child1child1Content );
        set.add( rcc_child1child1 );

        RelatedChildContent rcc_child2 = createRelatedChildContent( parent_versionKey, child2Content );
        set.addRootRelatedChild( rcc_child2 );
        set.add( rcc_child2 );

        // verify setup
        assertEquals( 3, set.size() );
        assertSet( ContentKey.convertToSet( new ContentKey( child1_contentKey ), new ContentKey( child1child1_contentKey ),
                                            new ContentKey( child2_contentKey ) ), ContentKey.convertToSet( set.getContentKeys() ) );
        assertEquals( createContentSet( child1Content, child1child1Content, child2Content ), set.getDinstinctSetOfContent() );
        assertEquals( createRelatedContentList( rcc_child1, rcc_child2 ),
                      convertToRelatedContentList( set.getRootRelatedChildren( parentContent.getMainVersion() ) ) );
        assertSet( createRelatedContentSet( rcc_child1, rcc_child1child1, rcc_child2 ),
                   createRelatedContentSet( set.getDistinctCollectionOfRelatedContent() ) );

        // exercise
        List<ContentKey> childrenToRetain = new ArrayList<ContentKey>();
        childrenToRetain.add( child1Content.getKey() );
        childrenToRetain.add( child2Content.getKey() );
        set.retainRelatedRootChildren( new ContentVersionKey( parent_versionKey ), childrenToRetain );

        // verify
        assertFalse( set.isEmpty() );
        assertEquals( 3, set.size() );
        assertSet( ContentKey.convertToSet( new ContentKey( child1_contentKey ), new ContentKey( child1child1_contentKey ),
                                            new ContentKey( child2_contentKey ) ), ContentKey.convertToSet( set.getContentKeys() ) );
        assertEquals( createContentSet( child1Content, child1child1Content, child2Content ), set.getDinstinctSetOfContent() );
        assertEquals( createRelatedContentList( rcc_child1, rcc_child2 ),
                      convertToRelatedContentList( set.getRootRelatedChildren( parentContent.getMainVersion() ) ) );
        assertSet( createRelatedContentSet( rcc_child1, rcc_child1child1, rcc_child2 ),
                   createRelatedContentSet( set.getDistinctCollectionOfRelatedContent() ) );

        // verify: root related children
        List<RelatedChildContent> actualRootRelatedChildren =
            convertToRelatedChildContentList( set.getRootRelatedChildren( createVersion( parent_versionKey ) ) );
        assertEquals( 2, actualRootRelatedChildren.size() );

        List<ContentKey> actualRootRelatedChildrenKeys = extractContentKeysFromRelatedChildContent( actualRootRelatedChildren );
        assertEquals( ContentKey.convertToList( new int[]{child1Content.getKey().toInt(), child2Content.getKey().toInt()} ),
                      actualRootRelatedChildrenKeys );
    }

    private RelatedChildContent createRelatedChildContent( int parentVersionKey, ContentEntity content )
    {
        return new RelatedChildContent( new ContentVersionKey( parentVersionKey ), content );
    }

    private ContentEntity createContent( int key, int versionKey, ContentEntity... childContents )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setKey( new ContentVersionKey( versionKey ) );
        content.setMainVersion( version );
        version.setContent( content );

        if ( childContents != null && childContents.length > 0 )
        {
            for ( ContentEntity childContent : childContents )
            {
                version.addRelatedChild( childContent );
            }
        }
        return content;
    }

    private ContentVersionEntity createVersion( int key )
    {
        ContentVersionEntity content = new ContentVersionEntity();
        content.setKey( new ContentVersionKey( key ) );
        return content;
    }

    private List<RelatedContent> convertToRelatedContentList( Iterable<RelatedChildContent> it )
    {
        List<RelatedContent> list = new ArrayList<RelatedContent>();
        for ( RelatedChildContent relatedChildContent : it )
        {
            list.add( relatedChildContent );
        }
        return list;
    }

    private List<RelatedChildContent> convertToRelatedChildContentList( Iterable<RelatedChildContent> it )
    {
        List<RelatedChildContent> list = new ArrayList<RelatedChildContent>();
        for ( RelatedChildContent relatedChildContent : it )
        {
            list.add( relatedChildContent );
        }
        return list;
    }

    private List<ContentKey> extractContentKeysFromRelatedChildContent( Iterable<RelatedChildContent> it )
    {
        List<ContentKey> list = new ArrayList<ContentKey>();
        for ( RelatedChildContent rcc : it )
        {
            list.add( rcc.getContent().getKey() );
        }
        return list;
    }

    private List<ContentKey> extractContentKeysFromContent( Iterable<ContentEntity> it )
    {
        List<ContentKey> list = new ArrayList<ContentKey>();
        for ( ContentEntity c : it )
        {
            list.add( c.getKey() );
        }
        return list;
    }

    private Set<ContentEntity> createContentSet( ContentEntity... contents )
    {
        Set<ContentEntity> set = new HashSet<ContentEntity>();
        set.addAll( Arrays.asList( contents ) );
        return set;
    }

    private Set<RelatedContent> createRelatedContentSet( RelatedContent... rcontents )
    {
        Set<RelatedContent> set = new HashSet<RelatedContent>();
        set.addAll( Arrays.asList( rcontents ) );
        return set;
    }

    private Set<RelatedContent> createRelatedContentSet( Collection<RelatedContent> collection )
    {
        Set<RelatedContent> set = new HashSet<RelatedContent>();
        set.addAll( collection );
        return set;
    }

    private List<RelatedContent> createRelatedContentList( RelatedContent... rcontents )
    {
        return Arrays.asList( rcontents );
    }

    private static void assertSet( Set expectedSet, Set actualSet )
    {
        org.junit.Assert.assertEquals( "sets do not have same size", expectedSet.size(), actualSet.size() );
        for ( Object expectedItem : expectedSet )
        {
            org.junit.Assert.assertTrue( "actual set " + printIterable( actualSet ) + " does not contain: " + expectedItem,
                                         actualSet.contains( expectedItem ) );
        }
    }

    private static String printIterable( Iterable it )
    {
        StringBuffer s = new StringBuffer();
        s.append( "<" );
        Iterator iterator = it.iterator();
        while ( iterator.hasNext() )
        {
            Object o = iterator.next();
            s.append( o );
            if ( iterator.hasNext() )
            {
                s.append( "," );
            }
        }
        s.append( ">" );
        return s.toString();
    }

}
