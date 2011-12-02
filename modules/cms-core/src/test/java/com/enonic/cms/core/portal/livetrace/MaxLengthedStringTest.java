package com.enonic.cms.core.portal.livetrace;


import org.junit.Test;

import static org.junit.Assert.*;

public class MaxLengthedStringTest
{
    @Test
    public void input_string_is_unchanged_when_input_string_is_shorted_than_max_allowed()
    {
        MaxLengthedString str = new MaxLengthedString( "start67890123456789012345678901234567end" );
        assertEquals( 40, str.toString().length() );
        assertEquals( "start67890123456789012345678901234567end", str.toString() );
    }

    @Test
    public void max_length_is_kept_when_input_string_is_longer_than_max_allowed()
    {
        MaxLengthedString str = new MaxLengthedString( "start67890123456789012345678901234567end", 30 );
        assertEquals( 30, str.toString().length() );
        assertEquals( "start...(chopped)...1234567end", str.toString() );
    }
}
