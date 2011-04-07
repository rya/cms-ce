/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <code>Base64</code> provides Base64 encoding/decoding of strings and streams.
 */
public final class Base64Util
{

    /**
     * Encoding table (the 64 valid base64 characters).
     */
    private final static char[] BASE64CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /**
     * Decoding table.
     */
    private final static byte[] DECODETABLE = new byte[128];

    /** Populate the decode table. */
    static
    {
        // initialize decoding table
        for ( int i = 0; i < DECODETABLE.length; i++ )
        {
            DECODETABLE[i] = 0x7f;
        }

        // build decoding table
        for ( int i = 0; i < BASE64CHARS.length; i++ )
        {
            DECODETABLE[BASE64CHARS[i]] = (byte) i;
        }
    }

    /**
     * pad character.
     */
    private static final char BASE64PAD = '=';

    /**
     * Private constructor.
     */
    private Base64Util()
    {
    }

    /**
     * Outputs base64 representation of the specified stream data to a <code>String</code>.
     */
    public static String encode( byte[] data )
    {
        StringWriter writer = new StringWriter();
        try
        {
            encode( data, 0, data.length, writer );
        }
        catch ( IOException ioe )
        {
            throw new IllegalStateException( ioe );
        }
        return writer.toString();
    }

    /**
     * Outputs base64 representation of the specified data to a <code>Writer</code>.
     */
    public static void encode( byte[] data, int off, int len, Writer writer )
        throws IOException
    {
        if ( len == 0 )
        {
            return;
        }
        if ( len < 0 || off >= data.length || len + off > data.length )
        {
            throw new IllegalArgumentException();
        }
        char[] enc = new char[4];
        while ( len >= 3 )
        {
            int i = ( ( data[off] & 0xff ) << 16 ) + ( ( data[off + 1] & 0xff ) << 8 ) + ( data[off + 2] & 0xff );
            enc[0] = BASE64CHARS[i >> 18];
            enc[1] = BASE64CHARS[( i >> 12 ) & 0x3f];
            enc[2] = BASE64CHARS[( i >> 6 ) & 0x3f];
            enc[3] = BASE64CHARS[i & 0x3f];
            writer.write( enc, 0, 4 );
            off += 3;
            len -= 3;
        }
        // add padding if necessary
        if ( len == 1 )
        {
            int i = data[off] & 0xff;
            enc[0] = BASE64CHARS[i >> 2];
            enc[1] = BASE64CHARS[( i << 4 ) & 0x3f];
            enc[2] = BASE64PAD;
            enc[3] = BASE64PAD;
            writer.write( enc, 0, 4 );
        }
        else if ( len == 2 )
        {
            int i = ( ( data[off] & 0xff ) << 8 ) + ( data[off + 1] & 0xff );
            enc[0] = BASE64CHARS[i >> 10];
            enc[1] = BASE64CHARS[( i >> 4 ) & 0x3f];
            enc[2] = BASE64CHARS[( i << 2 ) & 0x3f];
            enc[3] = BASE64PAD;
            writer.write( enc, 0, 4 );
        }
    }

}
