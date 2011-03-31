/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.xml;

import java.util.Arrays;

public final class IllegalCharacterCleaner
{

    // All ASCII controll characters and some other selected special characters.

    private static final String CHARS_TO_REPLACE = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\u0008\u000b\u000c\u000e" +
        "\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f";

    private static final String CHARS_TO_REPLACE_WITH = " ";

    /**
     * Holder of all illegal XML chars. *
     */
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

    public byte[] cleanByteArray( final byte[] bytes, char replacement )
    {
        for ( int i = 0; i < bytes.length; i++ )
        {
            byte aByte = bytes[i];
            if ( Arrays.binarySearch( ILLEGAL_XML_1_0_CHARS, aByte ) >= 0 )
            {
                bytes[i] = (byte) replacement;
            }
        }
        return bytes;
    }

    public String cleanXml( String xml )
    {
        if ( xml != null )
        {
            xml = xml.replaceAll( "[" + CHARS_TO_REPLACE + "]", CHARS_TO_REPLACE_WITH );
            xml = xml.trim();
            return xml;
        }
        return null;
    }


}
