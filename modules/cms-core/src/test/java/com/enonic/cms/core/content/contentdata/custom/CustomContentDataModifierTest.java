/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

import static org.junit.Assert.*;

/**
 * Mar 22, 2010
 */
public class CustomContentDataModifierTest
{
    private ContentHandlerEntity contentHandler;

    private ContentTypeConfig config;


    @Before
    public void before()
    {
        contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );
        contentType.setData( createPersonContentTypeXml() );
        config = contentType.getContentTypeConfig();
    }

    @Test
    public void modify_existing_with_empty_ccd_do_not_change_anything()
    {
        // setup
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "person-no" ), "1001" ) );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        assertEquals( "1001", ( (TextDataEntry) modifiedContentData.getEntry( "person-no" ) ).getValue() );
        assertEquals( "Jørund Vier Skriubakken", ( (TextDataEntry) modifiedContentData.getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modify_existing_input_changes_only_that_input()
    {
        // setup
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "person-no" ), "1001" ) );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );
        newContentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Qhawe Bekhaizizwe Skriubakken" ) );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        assertEquals( "1001", ( (TextDataEntry) modifiedContentData.getEntry( "person-no" ) ).getValue() );
        assertEquals( "Qhawe Bekhaizizwe Skriubakken", ( (TextDataEntry) modifiedContentData.getEntry( "name" ) ).getValue() );
    }

    @Test
    public void modified_gets_new_group_data_entry_when_new_has_one_entry()
    {
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );
        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        newContentData.add( birth );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry modified = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertNotNull( modified );
        assertEquals( "Birth", ( (TextDataEntry) modified.getEntry( "event-name" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 2 ) );
    }

    @Test
    public void modified_gets_new_group_data_entry_when_new_has_two_entries()
    {
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );
        GroupDataEntry newBirth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        newBirth.setConfig( config.getSetConfig( "Events" ) );
        newBirth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        newBirth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        newContentData.add( newBirth );

        GroupDataEntry newMarriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        newMarriage.setConfig( config.getSetConfig( "Events" ) );
        newMarriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        newMarriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        newContentData.add( newMarriage );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry first = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) first.getEntry( "event-name" ) ).getValue() );
        GroupDataEntry second = modifiedContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( "Marriage", ( (TextDataEntry) second.getEntry( "event-name" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 3 ) );
    }

    @Test
    public void modify_with_existing_group_data_entries_and_changed_existing_ones()
    {
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );
        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1956, 4, 19 ).toDate() ) );
        existing.add( birth );

        GroupDataEntry marriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        marriage.setConfig( config.getSetConfig( "Events" ) );
        marriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        marriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2001, 4, 19 ).toDate() ) );
        existing.add( marriage );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );
        GroupDataEntry newBirth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        newBirth.setConfig( config.getSetConfig( "Events" ) );
        newBirth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        newBirth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        newContentData.add( newBirth );

        GroupDataEntry newMarriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        newMarriage.setConfig( config.getSetConfig( "Events" ) );
        newMarriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        newMarriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        newContentData.add( newMarriage );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry first = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( new DateMidnight( 1976, 4, 19 ).toDate(), ( (DateDataEntry) first.getEntry( "event-date" ) ).getValue() );
        GroupDataEntry second = modifiedContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) second.getEntry( "event-date" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 3 ) );
    }

    @Test
    public void modify_with_existing_group_data_entries_and_second_entry_changed()
    {
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1956, 4, 19 ).toDate() ) );
        existing.add( birth );

        GroupDataEntry marriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        marriage.setConfig( config.getSetConfig( "Events" ) );
        marriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        marriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2001, 2, 14 ).toDate() ) );
        existing.add( marriage );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );

        GroupDataEntry newMarriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        newMarriage.setConfig( config.getSetConfig( "Events" ) );
        newMarriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        newMarriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        newContentData.add( newMarriage );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry first = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( new DateMidnight( 1956, 4, 19 ).toDate(), ( (DateDataEntry) first.getEntry( "event-date" ) ).getValue() );
        GroupDataEntry second = modifiedContentData.getGroupDataEntry( "Events", 2 );
        assertEquals( new DateMidnight( 2008, 2, 14 ).toDate(), ( (DateDataEntry) second.getEntry( "event-date" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 3 ) );
    }

    @Test
    public void missing_last_group_data_entry_removed_when_setup_to_purge()
    {
        // setup
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( birth );

        GroupDataEntry marriage = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        marriage.setConfig( config.getSetConfig( "Events" ) );
        marriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        marriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( marriage );

        CustomContentData newContentData = new CustomContentData( config );
        newContentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry newBirth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        newBirth.setConfig( config.getSetConfig( "Events" ) );
        newBirth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        newBirth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        newContentData.add( newBirth );

        // exercise
        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry modified = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) modified.getEntry( "event-name" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 2 ) );
    }

    @Test
    public void missing_middle_group_data_entry_removed_when_setup_to_purge()
    {
        // setup
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( birth );

        GroupDataEntry confirmation = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        confirmation.setConfig( config.getSetConfig( "Events" ) );
        confirmation.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Confirmation" ) );
        confirmation.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1991, 4, 4 ).toDate() ) );
        existing.add( confirmation );

        GroupDataEntry marriage = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        marriage.setConfig( config.getSetConfig( "Events" ) );
        marriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        marriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( marriage );

        CustomContentData newContentData = new CustomContentData( config );
        newContentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry newBirth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        newBirth.setConfig( config.getSetConfig( "Events" ) );
        newBirth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        newBirth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        newContentData.add( newBirth );

        GroupDataEntry newMarriage = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        newMarriage.setConfig( config.getSetConfig( "Events" ) );
        newMarriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        newMarriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        newContentData.add( newMarriage );

        // exercise
        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry actualEvent1 = modifiedContentData.getGroupDataEntry( "Events", 1 );
        assertEquals( "Birth", ( (TextDataEntry) actualEvent1.getEntry( "event-name" ) ).getValue() );

        GroupDataEntry actualEvent2 = modifiedContentData.getGroupDataEntry( "Events", 2 );
        assertNotNull( actualEvent2 );
        assertEquals( "Marriage", ( (TextDataEntry) actualEvent2.getEntry( "event-name" ) ).getValue() );

        assertNull( modifiedContentData.getGroupDataEntry( "Events", 3 ) );
    }


    @Test
    public void missing_first_group_data_entry_removed_when_setup_to_purge()
    {
        // setup
        CustomContentData existing = new CustomContentData( config );
        existing.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry birth = new GroupDataEntry( "Events", "contentdata/events/event", 1 );
        birth.setConfig( config.getSetConfig( "Events" ) );
        birth.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Birth" ) );
        birth.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1976, 4, 19 ).toDate() ) );
        existing.add( birth );

        GroupDataEntry confirmation = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        confirmation.setConfig( config.getSetConfig( "Events" ) );
        confirmation.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Confirmation" ) );
        confirmation.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1991, 4, 4 ).toDate() ) );
        existing.add( confirmation );

        GroupDataEntry marriage = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        marriage.setConfig( config.getSetConfig( "Events" ) );
        marriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        marriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        existing.add( marriage );

        CustomContentData newContentData = new CustomContentData( config );
        newContentData.add( new TextDataEntry( config.getInputConfig( "name" ), "Jørund Vier Skriubakken" ) );

        GroupDataEntry newConfirmation = new GroupDataEntry( "Events", "contentdata/events/event", 2 );
        newConfirmation.setConfig( config.getSetConfig( "Events" ) );
        newConfirmation.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Confirmation" ) );
        newConfirmation.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 1991, 4, 4 ).toDate() ) );
        newContentData.add( newConfirmation );

        GroupDataEntry newMarriage = new GroupDataEntry( "Events", "contentdata/events/event", 3 );
        newMarriage.setConfig( config.getSetConfig( "Events" ) );
        newMarriage.add( new TextDataEntry( config.getInputConfig( "event-name" ), "Marriage" ) );
        newMarriage.add( new DateDataEntry( config.getInputConfig( "event-date" ), new DateMidnight( 2008, 2, 14 ).toDate() ) );
        newContentData.add( newMarriage );

        // exercise
        CustomContentDataModifier modifier = new CustomContentDataModifier( existing );
        modifier.addBlockGroupToPurge( "Events" );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        GroupDataEntry actualEvent1 = modifiedContentData.getGroupDataEntry( "Events", 1 );
        GroupDataEntry actualEvent2 = modifiedContentData.getGroupDataEntry( "Events", 2 );
        assertNotNull( actualEvent1 );
        assertNotNull( actualEvent2 );
        assertEquals( "Confirmation", ( (TextDataEntry) actualEvent1.getEntry( "event-name" ) ).getValue() );
        assertEquals( "Marriage", ( (TextDataEntry) actualEvent2.getEntry( "event-name" ) ).getValue() );
        assertNull( modifiedContentData.getGroupDataEntry( "Events", 3 ) );
    }

    //@Test

    public void htmlarea_equals()
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContent", "title" );
        ctyconf.startBlock( "MyContent" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addInput( "htmlarea", "htmlarea", "contentdata/htmlarea", "Htmlarea", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );
        contentType.setData( configAsXmlBytes );
        config = contentType.getContentTypeConfig();

        CustomContentData existingContentData = new CustomContentData( config );
        existingContentData.add( new TextDataEntry( config.getInputConfig( "title" ), "One" ) );
        existingContentData.add( new HtmlAreaDataEntry( config.getInputConfig( "htmlarea" ), null ) );

        // exercise
        CustomContentData newContentData = new CustomContentData( config );
        newContentData.add( new TextDataEntry( config.getInputConfig( "title" ), "One" ) );
        newContentData.add( new HtmlAreaDataEntry( config.getInputConfig( "htmlarea" ), "" ) );

        CustomContentDataModifier modifier = new CustomContentDataModifier( existingContentData );
        CustomContentData modifiedContentData = modifier.modify( newContentData );

        // verify
        HtmlAreaDataEntry existingHtmlArea = (HtmlAreaDataEntry) existingContentData.getEntry( "htmlarea" );
        HtmlAreaDataEntry newHtmlArea = (HtmlAreaDataEntry) newContentData.getEntry( "htmlarea" );
        HtmlAreaDataEntry modifiedHtmlArea = (HtmlAreaDataEntry) modifiedContentData.getEntry( "htmlarea" );
        assertNotNull( modifiedHtmlArea );
        assertEquals( existingContentData, newContentData );
        assertEquals( newContentData, modifiedContentData );
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
}
