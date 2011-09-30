/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <code>Base64</code> provides Base64 encoding/decoding of strings and streams.
 */
public final class Base64Util
{
    /**
     * Charset used for base64 encoded data (7-bit ASCII).
     */
    private final static String CHARSET = "US-ASCII";

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
     * Outputs base64 representation of the specified stream data to a <code>Writer</code>.
     */
    public static void encode( InputStream in, Writer writer )
        throws IOException
    {
        byte[] buffer = new byte[9 * 1024];
        int read;

        while ( ( read = in.read( buffer ) ) > 0 )
        {
            encode( buffer, 0, read, writer );
        }
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

    /**
     * Decode base64 encoded data.
     */
    public static void decode( Reader reader, OutputStream out )
        throws IOException
    {
        char[] chunk = new char[8192];
        int read;
        while ( ( read = reader.read( chunk ) ) > -1 )
        {
            decode( chunk, 0, read, out );
        }
    }

    /**
     * Decode base64 encoded data.
     */
    public static void decode( String data, OutputStream out )
        throws IOException
    {
        char[] chars = data.toCharArray();
        decode( chars, 0, chars.length, out );
    }

    public static byte[] decode( String data )
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream( data.length() );
        try
        {
            decode( data, baos );
        }
        catch ( IOException ioe )
        {
            throw new IllegalStateException( ioe );
        }
        return baos.toByteArray();
    }

    /**
     * Decode base64 encoded data.
     */
    public static void decode( char[] chars, int off, int len, OutputStream out )
        throws IOException
    {
        if ( len == 0 )
        {
            return;
        }
        if ( len < 0 || off >= chars.length || len + off > chars.length )
        {
            throw new IllegalArgumentException();
        }
        char[] chunk = new char[4];
        byte[] dec = new byte[3];
        int posChunk = 0;
        // decode in chunks of 4 characters
        for ( int i = off; i < ( off + len ); i++ )
        {
            char c = chars[i];
            if ( c < DECODETABLE.length && DECODETABLE[c] != 0x7f || c == BASE64PAD )
            {
                chunk[posChunk++] = c;
                if ( posChunk == chunk.length )
                {
                    int b0 = DECODETABLE[chunk[0]];
                    int b1 = DECODETABLE[chunk[1]];
                    int b2 = DECODETABLE[chunk[2]];
                    int b3 = DECODETABLE[chunk[3]];
                    if ( chunk[3] == BASE64PAD && chunk[2] == BASE64PAD )
                    {
                        dec[0] = (byte) ( b0 << 2 & 0xfc | b1 >> 4 & 0x3 );
                        out.write( dec, 0, 1 );
                    }
                    else if ( chunk[3] == BASE64PAD )
                    {
                        dec[0] = (byte) ( b0 << 2 & 0xfc | b1 >> 4 & 0x3 );
                        dec[1] = (byte) ( b1 << 4 & 0xf0 | b2 >> 2 & 0xf );
                        out.write( dec, 0, 2 );
                    }
                    else
                    {
                        dec[0] = (byte) ( b0 << 2 & 0xfc | b1 >> 4 & 0x3 );
                        dec[1] = (byte) ( b1 << 4 & 0xf0 | b2 >> 2 & 0xf );
                        dec[2] = (byte) ( b2 << 6 & 0xc0 | b3 & 0x3f );
                        out.write( dec, 0, 3 );
                    }
                    posChunk = 0;
                }
            }
            else
            {
                throw new IllegalArgumentException( "specified data is not base64 encoded" );
            }
        }
    }
}
