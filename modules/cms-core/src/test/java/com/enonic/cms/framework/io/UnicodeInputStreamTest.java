/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import java.io.InputStream;

import junit.framework.TestCase;

public class UnicodeInputStreamTest
    extends TestCase
{
    public void testPlain()
        throws Exception
    {
        assertBom( null, "bom-plain.txt" );
    }

    public void testUTF8Bom()
        throws Exception
    {
        assertBom( ByteOrderMark.UTF8, "bom-utf8.txt" );
    }

    public void testUTF16LEBom()
        throws Exception
    {
        assertBom( ByteOrderMark.UTF16LE, "bom-utf16le.txt" );
    }

    public void testUTF16BEBom()
        throws Exception
    {
        assertBom( ByteOrderMark.UTF16BE, "bom-utf16be.txt" );
    }

    public void testRead()
        throws Exception
    {
        assertRead( "bom-plain.txt", null, "This is a test" );
        assertRead( "bom-utf8.txt", null, "This is a test" );
        assertRead( "bom-utf8.txt", ByteOrderMark.UTF8, "This is a test" );
    }

    private void assertBom( ByteOrderMark bom, String file )
        throws Exception
    {
        UnicodeInputStream in = openFile( file, false );
        assertEquals( bom, in.getByteOrderMark() );
        in.close();
    }

    public void assertRead( String file, ByteOrderMark bom, String content )
        throws Exception
    {
        UnicodeInputStream in = openFile( file, bom == null );
        byte[] contentBytes = content.getBytes();
        byte[] expectedBytes = contentBytes;

        if ( bom != null )
        {
            byte[] buffer = new byte[contentBytes.length + bom.getBytes().length];
            System.arraycopy( bom.getBytes(), 0, buffer, 0, bom.getBytes().length );
            System.arraycopy( contentBytes, 0, buffer, bom.getBytes().length, contentBytes.length );
            expectedBytes = buffer;
        }

        byte[] readBuffer = new byte[expectedBytes.length];
        int len = in.read( readBuffer );
        in.close();

        assertEquals( expectedBytes.length, len );
        for ( int i = 0; i < expectedBytes.length; i++ )
        {
            assertEquals( (int) expectedBytes[i], (int) readBuffer[i] );
        }
    }

    private UnicodeInputStream openFile( String file, boolean skipBom )
        throws Exception
    {
        InputStream in = getClass().getResourceAsStream( file );
        return new UnicodeInputStream( in, skipBom );
    }
}
