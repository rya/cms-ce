/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.junit.Test;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

import static org.junit.Assert.*;


public class TextDataEntryTest
{
    @Test
    public void value_is_stripped_for_any_newlines()
    {
        DataEntryConfig config = new TextDataEntryConfig( "test", true, "Test", "contentdata/test" );
        TextDataEntry entry = new TextDataEntry( config, "\r\nCarriageReturn\r\nNewline\r\n" );
        assertEquals( "CarriageReturnNewline", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_carriage_returns()
    {
        DataEntryConfig config = new TextDataEntryConfig( "test", true, "Test", "contentdata/test" );
        TextDataEntry entry = new TextDataEntry( config, "\rCarriage\rReturn\r" );
        assertEquals( "CarriageReturn", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_formfeed()
    {
        DataEntryConfig config = new TextDataEntryConfig( "test", true, "Test", "contentdata/test" );
        TextDataEntry entry = new TextDataEntry( config, "\fForm\fFeed\f" );
        assertEquals( "FormFeed", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_tab()
    {
        DataEntryConfig config = new TextDataEntryConfig( "test", true, "Test", "contentdata/test" );
        TextDataEntry entry = new TextDataEntry( config, "\tTab\tTab\t" );
        assertEquals( "TabTab", entry.getValue() );
    }

}

