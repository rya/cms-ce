/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.junit.Test;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;

import static org.junit.Assert.*;


public class UrlDataEntryTest
{
    @Test
    public void value_is_stripped_for_any_newlines()
    {
        DataEntryConfig config = new UrlDataEntryConfig( "test", true, "Test", "contentdata/test", 100 );
        UrlDataEntry entry = new UrlDataEntry( config, "\r\nhttp://CarriageReturnNewline\r\n" );
        assertEquals( "http://CarriageReturnNewline", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_carriage_returns()
    {
        DataEntryConfig config = new UrlDataEntryConfig( "test", true, "Test", "contentdata/test", 0 );
        UrlDataEntry entry = new UrlDataEntry( config, "\rhttp://CarriageReturn\r" );
        assertEquals( "http://CarriageReturn", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_formfeed()
    {
        DataEntryConfig config = new UrlDataEntryConfig( "test", true, "Test", "contentdata/test", 0 );
        UrlDataEntry entry = new UrlDataEntry( config, "\fhttp://FormFeed\f" );
        assertEquals( "http://FormFeed", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_tab()
    {
        DataEntryConfig config = new UrlDataEntryConfig( "test", true, "Test", "contentdata/test", 0 );
        UrlDataEntry entry = new UrlDataEntry( config, "\thttp://Tab\tTab\t" );
        assertEquals( "http://TabTab", entry.getValue() );
    }

}
