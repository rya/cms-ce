/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.Iterator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Apr 22, 2010
 */
public class BlockGroupDataEntriesTest
{
    @Test
    public void numberOfEntries_Should_return_1_when_adding_only_one_entry()
    {
        BlockGroupDataEntries entries = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry = new GroupDataEntry( "Event", "events", 1 );

        entries.add( entry );

        assertEquals( 1, entries.numberOfEntries() );
    }

    @Test
    public void reorganizeBySuccesiveIndexOrder()
    {
        BlockGroupDataEntries entries = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry1 = new GroupDataEntry( "Event", "events", 1 );
        entries.add( entry1 );

        GroupDataEntry entry2 = new GroupDataEntry( "Event", "events", 3 );
        entries.add( entry2 );

        // exercise
        entries.reorganizeBySuccesiveIndexOrder();

        // verify
        assertEquals( 2, entries.numberOfEntries() );

        Iterator<GroupDataEntry> entryIterator = entries.getGroupDataEntries().iterator();
        assertEquals( 1, entryIterator.next().getGroupIndex() );
        assertEquals( 2, entryIterator.next().getGroupIndex() );
        assertEquals( 2, entries.lasIndex() );
        assertEquals( 3, entries.expectedNextIndex() );

    }

    @Test
    public void adding_an_entry_Should_throw_exception_When_an_entry_with_same_index_exists()
    {
        BlockGroupDataEntries entries = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry1 = new GroupDataEntry( "Event", "events", 1 );
        entries.add( entry1 );

        GroupDataEntry entry2 = new GroupDataEntry( "Event", "events", 1 );

        try
        {
            entries.add( entry2 );
            fail( "Expected exception" );
        }
        catch ( Throwable e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "GroupDataEntry at index 1 already exists.", e.getMessage() );
        }
    }
}
