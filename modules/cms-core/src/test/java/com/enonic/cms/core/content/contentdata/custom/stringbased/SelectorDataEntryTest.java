/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import java.util.LinkedHashMap;

import org.junit.Test;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DropdownDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RadioButtonDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class SelectorDataEntryTest
{
    @Test
    public void value_is_stripped_for_any_newlines()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "CarriageReturnNewline", "System chars" );

        DataEntryConfig config = new RadioButtonDataEntryConfig( "test", true, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "\r\nCarriageReturn\r\nNewline\r\n" );
        entry.customValidate();
        assertEquals( "CarriageReturnNewline", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_carriage_returns()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "CarriageReturn", "System chars" );

        DataEntryConfig config = new DropdownDataEntryConfig( "test", true, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "\rCarriage\rReturn\r" );
        entry.customValidate();
        assertEquals( "CarriageReturn", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_formfeed()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "FormFeed", "System chars" );

        DataEntryConfig config = new RadioButtonDataEntryConfig( "test", true, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "\fForm\fFeed\f" );
        entry.customValidate();
        assertEquals( "FormFeed", entry.getValue() );
    }

    @Test
    public void value_is_stripped_for_any_tab()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "TabTab", "System chars" );

        DataEntryConfig config = new DropdownDataEntryConfig( "test", true, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "\tTab\tTab\t" );
        entry.customValidate();
        assertEquals( "TabTab", entry.getValue() );
    }

    @Test
    public void custom_validation_failure_test_for_required_field()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "1", "en" );
        optionValuesWithDescription.put( "2", "to" );

        DataEntryConfig config = new DropdownDataEntryConfig( "test", true, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "3" );
        try
        {
            entry.customValidate();
            fail( "Verification of value didn't fail as expected." );
        }
        catch ( Exception e )
        {
            // Just continue.  The previous code should throw an exception.
        }

        entry = new SelectorDataEntry( config, "" );
        try
        {
            entry.customValidate();
            fail( "With mandatory value, empty string should fail." );
        }
        catch ( Exception e )
        {
            // Just continue.  The previous code should throw an exception.
        }

        entry = new SelectorDataEntry( config, null );
        try
        {
            entry.customValidate();
            fail( "With mandatory value, null value input should fail." );
        }
        catch ( Exception e )
        {
            // Just continue.  The previous code should throw an exception.
        }
    }

    @Test
    public void custom_validation_failure_test_for_non_required_field()
    {
        LinkedHashMap<String, String> optionValuesWithDescription = new LinkedHashMap<String, String>();
        optionValuesWithDescription.put( "1", "en" );
        optionValuesWithDescription.put( "2", "to" );
        DataEntryConfig config = new RadioButtonDataEntryConfig( "test", false, "Test", "contentdata/test", optionValuesWithDescription );
        SelectorDataEntry entry = new SelectorDataEntry( config, "3" );
        try
        {
            entry.customValidate();
            fail( "Verification of value didn't fail as expected." );
        }
        catch ( Exception e )
        {
            // Just continue.  The previous code should throw an exception.
        }

        entry = new SelectorDataEntry( config, "" );
        entry.customValidate();

        entry = new SelectorDataEntry( config, null );
        entry.customValidate();
    }

    @Test
    public void custom_validation_fails_with_incorrect_config_type()
    {
        DataEntryConfig config = new TextDataEntryConfig( "test", true, "Test", "contentdata/test" );
        SelectorDataEntry entry = new SelectorDataEntry( config, "\r\nCarriageReturn\r\nNewline\r\n" );
        try
        {
            entry.customValidate();
            fail( "TextDataEntryConfig is not a valid SelectorDataEntryConfig" );
        }
        catch ( Exception e )
        {
            // Just continue.  The previous code should throw an exception.
        }
    }
}
