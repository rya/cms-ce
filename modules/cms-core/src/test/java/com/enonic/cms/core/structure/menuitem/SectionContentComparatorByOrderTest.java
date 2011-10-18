package com.enonic.cms.core.structure.menuitem;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.structure.menuitem.section.SectionContentEntity;
import com.enonic.cms.core.structure.menuitem.section.SectionContentKey;

import static org.junit.Assert.*;

public class SectionContentComparatorByOrderTest
{
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
    public void set_keeps_unapproved_section_content_with_same_order_as_another_approved()
    {
        SectionContentEntity sc0 = createUnapprovedSectionContent( 0, createContent( 1 ) );
        SectionContentEntity sc1 = createApprovedSectionContent( 0, createContent( 2 ) );
        SectionContentEntity sc2 = createApprovedSectionContent( 1000, createContent( 3 ) );

        SortedSet<SectionContentEntity> set = new TreeSet<SectionContentEntity>( new SectionContentComparatorByOrder() );
        set.add( sc0 );
        set.add( sc1 );
        set.add( sc2 );

        SectionContentEntity[] array = set.toArray( new SectionContentEntity[set.size()] );

        SectionContentEntity[] expectedOrder = new SectionContentEntity[]{sc0, sc1, sc2};
        assertArrayEquals( expectedOrder, array );

    }

    @Test
    public void set_keeps_unapproved_section_content_with_same_order_as_another_unapproved()
    {
        SectionContentEntity sc0 = createUnapprovedSectionContent( 0, createContent( 1 ) );
        SectionContentEntity sc1 = createUnapprovedSectionContent( 0, createContent( 2 ) );
        SectionContentEntity sc2 = createApprovedSectionContent( 0, createContent( 3 ) );
        SectionContentEntity sc3 = createApprovedSectionContent( 1000, createContent( 4 ) );

        SortedSet<SectionContentEntity> set = new TreeSet<SectionContentEntity>( new SectionContentComparatorByOrder() );
        set.add( sc0 );
        set.add( sc1 );
        set.add( sc2 );
        set.add( sc3 );

        SectionContentEntity[] array = set.toArray( new SectionContentEntity[set.size()] );

        SectionContentEntity[] expectedOrder = new SectionContentEntity[]{sc0, sc1, sc2, sc3};
        assertArrayEquals( expectedOrder, array );

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
}