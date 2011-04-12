/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import com.enonic.cms.core.content.ContentHandlerEntity;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

import static org.junit.Assert.*;

/**
 * Apr 21, 2010
 */
public class BlockGroupDataEntriesModifierTest
{
    private ContentTypeConfig personCTYConfig;

    private ContentTypeConfig numbersCTYConfig;

    @Before
    public void before()
    {
        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );

        ContentTypeEntity personContentType = new ContentTypeEntity();
        personContentType.setHandler( contentHandler );
        personContentType.setData( createPersonContentTypeXml() );
        personCTYConfig = personContentType.getContentTypeConfig();

        ContentTypeEntity numbersContentType = new ContentTypeEntity();
        numbersContentType.setHandler( contentHandler );
        numbersContentType.setData( createNumbersContentTypeXml() );
        numbersCTYConfig = numbersContentType.getContentTypeConfig();
    }

    @Test
    public void modifying_with_third_entry_removed_of_existing_three_entries_Should_return_without_third_entry_When_purge_is_true()
    {
        ContentTypeConfig cfg = personCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry1 = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        entry1.setConfig( cfg.getSetConfig( "Events" ) );
        entry1.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Birth" ) );
        entry1.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( entry1 );

        GroupDataEntry entry2 = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        entry2.setConfig( cfg.getSetConfig( "Events" ) );
        entry2.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Confirmation" ) );
        entry2.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1994, 4, 4 ).toDate() ) );
        existing.add( entry2 );

        GroupDataEntry entry3 = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        entry3.setConfig( cfg.getSetConfig( "Events" ) );
        entry3.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Marriage" ) );
        entry3.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( entry3 );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, true );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Events" );
        modifying.add( entry1 );
        modifying.add( entry2 );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 2, modified.numberOfEntries() );
    }

    @Test
    public void modifying_with_first_entry_removed_of_existing_three_entries_Should_return_without_first_entry_When_purge_is_true()
    {
        ContentTypeConfig cfg = personCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry1 = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        entry1.setConfig( cfg.getSetConfig( "Events" ) );
        entry1.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Birth" ) );
        entry1.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( entry1 );

        GroupDataEntry entry2 = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        entry2.setConfig( cfg.getSetConfig( "Events" ) );
        entry2.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Confirmation" ) );
        entry2.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1994, 4, 4 ).toDate() ) );
        existing.add( entry2 );

        GroupDataEntry entry3 = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        entry3.setConfig( cfg.getSetConfig( "Events" ) );
        entry3.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Marriage" ) );
        entry3.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( entry3 );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, true );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Events" );
        entry1 = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        entry1.setConfig( cfg.getSetConfig( "Events" ) );
        entry1.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Confirmation" ) );
        entry1.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1994, 4, 4 ).toDate() ) );
        modifying.add( entry1 );

        entry2 = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        entry2.setConfig( cfg.getSetConfig( "Events" ) );
        entry2.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Marriage" ) );
        entry2.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        modifying.add( entry2 );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 2, modified.numberOfEntries() );
        assertEquals( "Confirmation", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "event-name" ) ).getValue() );
        assertEquals( "Marriage", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "event-name" ) ).getValue() );
    }

    @Test
    public void modifying_with_second_entry_removed_of_existing_three_entries_Should_return_without_second_entry_When_purge_is_true()
    {
        ContentTypeConfig cfg = personCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Events" );

        GroupDataEntry entry1 = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        entry1.setConfig( cfg.getSetConfig( "Events" ) );
        entry1.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Birth" ) );
        entry1.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( entry1 );

        GroupDataEntry entry2 = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        entry2.setConfig( cfg.getSetConfig( "Events" ) );
        entry2.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Confirmation" ) );
        entry2.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1994, 4, 4 ).toDate() ) );
        existing.add( entry2 );

        GroupDataEntry entry3 = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        entry3.setConfig( cfg.getSetConfig( "Events" ) );
        entry3.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Marriage" ) );
        entry3.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( entry3 );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, true );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Events" );
        entry1 = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        entry1.setConfig( cfg.getSetConfig( "Events" ) );
        entry1.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Birth" ) );
        entry1.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        modifying.add( entry1 );

        entry2 = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        entry2.setConfig( cfg.getSetConfig( "Events" ) );
        entry2.add( new TextDataEntry( cfg.getInputConfig( "event-name" ), "Marriage" ) );
        entry2.add( new DateDataEntry( cfg.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        modifying.add( entry2 );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 2, modified.numberOfEntries() );
        assertTrue( modified.hasEntryAtIndex( 1 ) );
        assertEquals( "Birth", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "event-name" ) ).getValue() );
        assertTrue( modified.hasEntryAtIndex( 2 ) );
        assertEquals( "Marriage", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "event-name" ) ).getValue() );
    }

    @Test
    public void modifying_with_second_entry_removed_of_existing_three_entries_Should_return_with_second_entry_changed_When_purge_is_false()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, false );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        modifying.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Three" ) ) );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 3, modified.numberOfEntries() );
        assertEquals( "One", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 3 ).getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modifying_with_first_entry_removed_of_existing_three_entries_Should_return_with_first_and_second_entry_changed_When_purge_is_false()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, false );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "Two" ) ) );
        modifying.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Three" ) ) );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 3, modified.numberOfEntries() );
        assertEquals( "Two", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 3 ).getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modifying_with_last_entry_removed_of_existing_three_entries_Should_return_unchanged_When_purge_is_false()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, false );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 3, modified.numberOfEntries() );
        assertEquals( "One", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Two", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 3 ).getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modifying_with_all_entries_removed_of_existing_three_entries_Should_return_unchanged_When_purge_is_false()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, false );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 3, modified.numberOfEntries() );
        assertEquals( "One", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Two", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 3 ).getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modifying_with_all_entries_removed_of_existing_three_entries_Should_return_empty_When_purge_is_true()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, true );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 0, modified.numberOfEntries() );
    }

    @Test
    public void modifying_existing_three_entries_with_only_entry_at_index_2_given_Should_return_with_entry_at_index_2_chnaged_When_purge_is_false()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, false );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "2" ) ) );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 3, modified.numberOfEntries() );
        assertEquals( "One", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
        assertEquals( "2", ( (TextDataEntry) modified.getGroupDataEntry( 2 ).getEntry( "name" ) ).getValue() );
        assertEquals( "Three", ( (TextDataEntry) modified.getGroupDataEntry( 3 ).getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modifying_existing_three_entries_with_only_entry_at_index_2_given_Should_return_with_entry_at_index_2_chnaged_When_purge_is_true()
    {
        ContentTypeConfig cfg = numbersCTYConfig;

        BlockGroupDataEntries existing = new BlockGroupDataEntries( "Numbers" );

        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 1, createTextDataEntry( cfg, "name", "One" ) ) );
        existing.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "Two" ) ) );
        existing.add(
            createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 3, createTextDataEntry( cfg, "name", "Three" ) ) );

        BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, true );

        BlockGroupDataEntries modifying = new BlockGroupDataEntries( "Numbers" );
        modifying.add( createGroupDataEntry( cfg, "Numbers", "contentdata/numbers/number", 2, createTextDataEntry( cfg, "name", "2" ) ) );

        // exercise
        BlockGroupDataEntries modified = modifier.modify( modifying );

        // verify
        assertEquals( 1, modified.numberOfEntries() );
        assertEquals( "2", ( (TextDataEntry) modified.getGroupDataEntry( 1 ).getEntry( "name" ) ).getValue() );
    }

    private TextDataEntry createTextDataEntry( ContentTypeConfig config, String name, String value )
    {
        return new TextDataEntry( config.getInputConfig( name ), value );
    }

    private GroupDataEntry createGroupDataEntry( ContentTypeConfig config, String name, String xpath, int index,
                                                 DataEntry... dateEntriesToAdd )
    {
        GroupDataEntry groupDataEntry = new GroupDataEntry( name, xpath, index );
        groupDataEntry.setConfig( config.getSetConfig( name ) );
        for ( DataEntry dataEntryToAdd : dateEntriesToAdd )
        {
            groupDataEntry.add( dataEntryToAdd );
        }
        return groupDataEntry;
    }

    private XMLBytes createPersonContentTypeXml()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<moduledata>" );
        xml.append( "<config name='PersonType' version='1.0'>" );
        xml.append( "     <form>" );

        xml.append( "         <title name='name'/>" );

        xml.append( "         <block name='Person'>" );

        xml.append( "             <input name='person-no' required='true' type='text'>" );
        xml.append( "                 <display>Person number</display>" );
        xml.append( "                 <xpath>contentdata/person-no</xpath>" );
        xml.append( "             </input>" );

        xml.append( "             <input name='name' required='true' type='text'>" );
        xml.append( "                 <display>Name</display>" );
        xml.append( "                 <xpath>contentdata/name</xpath>" );
        xml.append( "             </input>" );

        xml.append( "         </block>" );

        xml.append( "         <block name='Events' group='contentdata/events/event'>" );

        xml.append( "             <input name='event-name' required='true' type='text'>" );
        xml.append( "                 <display>Name</display>" );
        xml.append( "                 <xpath>name</xpath>" );
        xml.append( "             </input>" );

        xml.append( "             <input name='event-date' required='true' type='date'>" );
        xml.append( "                 <display>Date</display>" );
        xml.append( "                 <xpath>date</xpath>" );
        xml.append( "             </input>" );

        xml.append( "         </block>" );

        xml.append( "     </form>" );

        xml.append( "</config>" );
        xml.append( "</moduledata>" );
        return XMLDocumentFactory.create( xml.toString() ).getAsBytes();
    }

    private XMLBytes createNumbersContentTypeXml()
    {
        StringBuffer xml = new StringBuffer();
        xml.append( "<moduledata>" );
        xml.append( "<config name='NumbersType' version='1.0'>" );
        xml.append( "     <form>" );

        xml.append( "         <title name='title'/>" );
        xml.append( "         <block name='General'>" );

        xml.append( "             <input name='title' required='true' type='text'>" );
        xml.append( "                 <display>Title</display>" );
        xml.append( "                 <xpath>contentdata/title</xpath>" );
        xml.append( "             </input>" );

        xml.append( "         </block>" );

        xml.append( "         <block name='Numbers' group='contentdata/numbers/number'>" );

        xml.append( "             <input name='name' required='true' type='text'>" );
        xml.append( "                 <display>Number</display>" );
        xml.append( "                 <xpath>name</xpath>" );
        xml.append( "             </input>" );

        xml.append( "         </block>" );

        xml.append( "     </form>" );

        xml.append( "</config>" );
        xml.append( "</moduledata>" );
        return XMLDocumentFactory.create( xml.toString() ).getAsBytes();
    }
}
