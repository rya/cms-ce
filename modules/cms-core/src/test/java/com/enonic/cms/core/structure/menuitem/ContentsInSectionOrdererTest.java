package com.enonic.cms.core.structure.menuitem;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

import static org.junit.Assert.*;

/**
 * Oct 17, 2010
 */
public class ContentsInSectionOrdererTest
{
    private final static int ORDER_SPACE = 1000;

    private static int nextSectionContentKey = 0;

    private MenuItemKey sectionKey0 = new MenuItemKey( 0 );

    private MenuItemEntity section0;

    @Before
    public void before()
    {
        section0 = new MenuItemEntity();
        section0.setKey( sectionKey0.toInt() );
    }

    @Test
    public void keep_same_order_of_two()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void revert_order_of_the_two_first_when_there_is_only_two()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 1 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 0, actualOrder.get( 0 ).getOrder() );
        assertEquals( 1000, actualOrder.get( 1 ).getOrder() );
    }

    @Test
    public void revert_order_of_the_two_first_when_there_is_three()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3000, createContent( 3 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 3 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 0, actualOrder.get( 0 ).getOrder() );
        assertEquals( 1000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 3000, actualOrder.get( 2 ).getOrder() );
    }

    @Test
    public void none()
    {
        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void one()
    {
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 1 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 0, actualOrder.get( 0 ).getOrder() );
    }

    @Test
    public void two_unordered()
    {
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 2 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void one_addition_to_one_oldStyle_order()
    {
        section0.addSectionContent( createApprovedSectionContent( 1, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 2 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void inserting_one_into_oldStyle_order()
    {
        section0.addSectionContent( createApprovedSectionContent( 1, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 4 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 3 ) );

        // exercise
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }


    @Test
    public void moving_first_to_second_and_last_to_second_last()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3000, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 5 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 0, actualOrder.get( 0 ).getOrder() );
        assertEquals( 1000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 3000, actualOrder.get( 2 ).getOrder() );
        assertEquals( 3500, actualOrder.get( 3 ).getOrder() );
        assertEquals( 4000, actualOrder.get( 4 ).getOrder() );

        assertSectionContentSetContains( changedSC, new ContentKey( 2 ), new ContentKey( 5 ) );
    }

    @Test
    public void reorder_4_of_five()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3000, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 5 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 1000, actualOrder.get( 0 ).getOrder() );
        assertEquals( 4000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 5000, actualOrder.get( 2 ).getOrder() );
        assertEquals( 6000, actualOrder.get( 3 ).getOrder() );
        assertEquals( 7000, actualOrder.get( 4 ).getOrder() );
        assertSectionContentSetContains( changedSC, new ContentKey( 2 ), new ContentKey( 3 ) );
    }

    @Test
    public void revert_order_of_five()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3000, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 5 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 3000, actualOrder.get( 0 ).getOrder() );
        assertEquals( 4000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 5000, actualOrder.get( 2 ).getOrder() );
        assertEquals( 6000, actualOrder.get( 3 ).getOrder() );
        assertEquals( 7000, actualOrder.get( 4 ).getOrder() );

        assertSectionContentSetContains( changedSC, new ContentKey( 5 ), new ContentKey( 3 ), new ContentKey( 2 ), new ContentKey( 1 ) );
    }

    @Test
    public void all_content_is_not_ordered_when_moving_first_content_two_down()
    {
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3000, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 5 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify: order
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );

        // Verify: only the moved content is updated
        assertSectionContentSetContains( changedSC, new ContentKey( 1 ) );
    }

    @Test
    public void test4()
    {
        section0.addSectionContent( createApprovedSectionContent( 1, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 3, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 5 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void test5()
    {
        TreeSet<SectionContentEntity> sectionContents = new TreeSet<SectionContentEntity>();
        sectionContents.add( createApprovedSectionContent( -999, createContent( 1 ) ) );
        sectionContents.add( createApprovedSectionContent( 1, createContent( 2 ) ) );
        sectionContents.add( createApprovedSectionContent( 3000, createContent( 3 ) ) );
        sectionContents.add( createApprovedSectionContent( 4000, createContent( 4 ) ) );
        sectionContents.add( createApprovedSectionContent( 5000, createContent( 5 ) ) );

        section0.setSectionContent( sectionContents );
        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 5 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    @Test
    public void test6()
    {
        section0.addSectionContent( createApprovedSectionContent( -1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( -63, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 4 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 5 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 5 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> scChanged = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        printOrder( actualOrder );
        assertSectionContentSetContains( scChanged, new ContentKey( 5 ) );
    }

    @Test
    public void move_first_to_last()
    {
        section0.addSectionContent( createApprovedSectionContent( -1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 3 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( 0, actualOrder.get( 0 ).getOrder() );
        assertEquals( 1000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 2000, actualOrder.get( 2 ).getOrder() );

        assertSectionContentSetContains( changedSC, new ContentKey( 1 ) );
    }

    @Test
    public void move_last_to_first()
    {
        section0.addSectionContent( createApprovedSectionContent( -1000, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 3 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );
        ContentsInSectionOrderer orderer = new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE );
        Set<SectionContentEntity> changedSC = orderer.order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
        assertEquals( -2000, actualOrder.get( 0 ).getOrder() );
        assertEquals( -1000, actualOrder.get( 1 ).getOrder() );
        assertEquals( 0, actualOrder.get( 2 ).getOrder() );

        assertSectionContentSetContains( changedSC, new ContentKey( 3 ) );
    }

    @Test
    public void content_not_included_in_order_is_ordered_on_top()
    {
        section0.addSectionContent( createUnapprovedSectionContent( 0, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 1000, createContent( 3 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 2000, createContent( 4 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 2 ) );
        expectedOrder.add( new ContentKey( 4 ) );
        expectedOrder.add( new ContentKey( 3 ) );

        // exercise
        new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE ).order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertEquals( 1, actualOrder.get( 0 ).getContent().getKey().toInt() );
        assertEquals( 2, actualOrder.get( 1 ).getContent().getKey().toInt() );
        assertEquals( 4, actualOrder.get( 2 ).getContent().getKey().toInt() );
        assertEquals( 3, actualOrder.get( 3 ).getContent().getKey().toInt() );
    }

    @Test
    public void ordering_below_Integer_MIN_VALUE_resets_the_order_values()
    {
        section0.addSectionContent( createApprovedSectionContent( Integer.MIN_VALUE, createContent( 1 ) ) );
        section0.addSectionContent( createApprovedSectionContent( Integer.MIN_VALUE + 1000, createContent( 2 ) ) );
        section0.addSectionContent( createApprovedSectionContent( 0, createContent( 3 ) ) );

        List<ContentKey> expectedOrder = new ArrayList<ContentKey>();
        expectedOrder.add( new ContentKey( 3 ) );
        expectedOrder.add( new ContentKey( 1 ) );
        expectedOrder.add( new ContentKey( 2 ) );

        // exercise
        new ContentsInSectionOrderer( expectedOrder, section0, ORDER_SPACE ).order();

        // Verify
        List<SectionContentEntity> actualOrder = toNewSortedArrayList( section0.getSectionContents() );
        assertOrder( expectedOrder, actualOrder );
    }

    private List<SectionContentEntity> toNewSortedArrayList( Set<SectionContentEntity> set )
    {
        List<SectionContentEntity> list = Lists.newArrayList( set );
        Collections.sort( list, new SectionContentComparatorByOrder() );
        return list;
    }


    private void assertOrder( List<ContentKey> expectedOrder, List<SectionContentEntity> actualOrder )
    {
        assertEquals( "size", expectedOrder.size(), actualOrder.size() );
        Integer previousOrderNo = null;
        for ( int i = 0; i < expectedOrder.size(); i++ )
        {
            SectionContentEntity sectionContent = actualOrder.get( i );
            assertEquals( expectedOrder.get( i ), sectionContent.getContent().getKey() );

            int currOrderNo = sectionContent.getOrder();
            if ( previousOrderNo != null )
            {
                assertTrue( previousOrderNo < currOrderNo );
            }

            previousOrderNo = currOrderNo;
        }
    }

    private SectionContentEntity createApprovedSectionContent( int order, ContentEntity content )
    {
        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( nextSectionContentKey++ ) );
        sectionContent.setApproved( true );
        sectionContent.setMenuItem( section0 );
        sectionContent.setContent( content );
        sectionContent.setOrder( order );
        return sectionContent;
    }

    private SectionContentEntity createUnapprovedSectionContent( int order, ContentEntity content )
    {
        SectionContentEntity sectionContent = new SectionContentEntity();
        sectionContent.setKey( new SectionContentKey( nextSectionContentKey++ ) );
        sectionContent.setApproved( false );
        sectionContent.setMenuItem( section0 );
        sectionContent.setContent( content );
        sectionContent.setOrder( order );
        return sectionContent;
    }

    private ContentEntity createContent( int key )
    {
        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );
        return content;
    }

    private void printOrder( List<SectionContentEntity> sectionContents )
    {
        for ( SectionContentEntity sc : sectionContents )
        {
            System.out.println( sc.getOrder() + " : " + sc.getContent().getKey() );
        }
    }

    private void assertSectionContentSetContains( Set<SectionContentEntity> scSet, ContentKey... expectedKeys )
    {
        assertEquals( "expected set to be of size " + expectedKeys.length, expectedKeys.length, scSet.size() );
        for ( ContentKey contentKey : expectedKeys )
        {
            assertTrue( "expected content " + contentKey + " not found in set", contentIsInSectionContentSet( contentKey, scSet ) );
        }
    }

    private boolean contentIsInSectionContentSet( ContentKey contentKey, Set<SectionContentEntity> scSet )
    {
        for ( SectionContentEntity sc : scSet )
        {
            if ( sc.getContent().getKey().equals( contentKey ) )
            {
                return true;
            }

        }
        return false;
    }
}