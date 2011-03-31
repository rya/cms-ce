/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;

public class IllegalCharacterCleanerTest
{

    private static byte[] ILLEGAL_XML_1_0_CHARS;

    static
    {
        final StringBuffer buff = new StringBuffer();
        for ( char i = 0x0000; i < 0x0020; i++ )
        {
            if ( i != 0x0009 && i != 0x000A && i != 0x000D )
            {
                buff.append( i );
            }
        }
        ILLEGAL_XML_1_0_CHARS = buff.toString().getBytes();
        Arrays.sort( ILLEGAL_XML_1_0_CHARS );
    }

    private IllegalCharacterCleaner xmlCleaner = new IllegalCharacterCleaner();

    @Test
    public void cleanByteArray()
        throws IOException
    {
        final int length = 65535;

        byte[] bytes = new byte[length];
        int index = 0;
        for ( char i = 0x0000; i < 0xFFFF; i++ )
        {
            bytes[index] = (byte) i;
            index++;
        }

        assertEquals( length, 65535 );

        bytes = xmlCleaner.cleanByteArray( bytes, 'a' );
        assertEquals( bytes.length, 65535 );

        for ( int i = 0; i < bytes.length; i++ )
        {
            assertFalse( "Illegal char found!", Arrays.binarySearch( ILLEGAL_XML_1_0_CHARS, bytes[i] ) >= 0 );
        }

    }

}
