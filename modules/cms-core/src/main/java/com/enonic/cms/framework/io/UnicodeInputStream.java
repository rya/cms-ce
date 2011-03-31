/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

/**
 * This is an input stream that is unicode BOM (byte order mark) aware.
 */
public class UnicodeInputStream
    extends InputStream
{
    /**
     * The maximum amount of bytes to read for a BOM
     */
    private static final int MAX_BOM_SIZE = 4;

    /**
     * True if the BOM itself should be skipped and not read.
     */
    private final boolean skipBOM;

    /**
     * Pushback input stream.
     */
    private final PushbackInputStream in;

    /**
     * Byte order mark.
     */
    private ByteOrderMark bom;

    /**
     * Bom buffer.
     */
    private final byte[] bomBuffer = new byte[MAX_BOM_SIZE];

    /**
     * Bom position.
     */
    private int bomPos = 0;

    /**
     * Construct the stream.
     */
    public UnicodeInputStream( InputStream in )
        throws IOException
    {
        this( in, true );
    }

    /**
     * Construct the stream.
     */
    public UnicodeInputStream( InputStream in, boolean skipBOM )
        throws IOException
    {
        if ( in == null )
        {
            throw new IllegalArgumentException( "InputStream is null" );
        }

        this.in = new PushbackInputStream( in, MAX_BOM_SIZE );
        this.skipBOM = skipBOM;
        this.bom = readBom();
    }

    /**
     * Return the encoding from stream based on BOM.
     */
    public String getEncoding()
    {
        return this.bom != null ? this.bom.getEncoding() : null;
    }

    /**
     * Return the byte order mark.
     */
    public ByteOrderMark getByteOrderMark()
    {
        return this.bom;
    }

    /**
     * Read bom byte.
     */
    private boolean readBomByte()
        throws IOException
    {
        if ( this.bomPos >= this.bomBuffer.length )
        {
            return false;
        }

        int res = this.in.read();
        if ( res == -1 )
        {
            return false;
        }

        this.bomBuffer[this.bomPos++] = (byte) res;
        return true;
    }

    /**
     * Matches bom.
     */
    private ByteOrderMark matchBom()
        throws IOException
    {
        while ( readBomByte() )
        {
            ByteOrderMark bom = ByteOrderMark.resolve( this.bomBuffer );
            if ( bom != null )
            {
                return bom;
            }
        }

        return null;
    }

    /**
     * Pushback bom.
     */
    private void pushbackBom( ByteOrderMark bom )
        throws IOException
    {
        int count = this.bomPos;
        int start = 0;

        if ( ( bom != null ) && this.skipBOM )
        {
            start = bom.getBytes().length;
            count = ( this.bomPos - start );

            if ( count < 0 )
            {
                throw new IOException( "Match has more bytes than available!" );
            }
        }

        this.in.unread( this.bomBuffer, start, count );
    }

    /**
     * Read the bom.
     */
    private ByteOrderMark readBom()
        throws IOException
    {
        this.bomPos = 0;
        ByteOrderMark bom = matchBom();
        pushbackBom( bom );
        return bom;
    }

    public int read()
        throws IOException
    {
        return in.read();
    }

    public void close()
        throws IOException
    {
        in.close();
    }

    public void reset()
        throws IOException
    {
        in.reset();
    }

    public int available()
        throws IOException
    {
        return in.available();
    }

    public void mark( int i )
    {
        in.mark( i );
    }

    public boolean markSupported()
    {
        return in.markSupported();
    }

    public int read( byte[] bytes )
        throws IOException
    {
        return in.read( bytes );
    }

    public int read( byte[] bytes, int i, int i1 )
        throws IOException
    {
        return in.read( bytes, i, i1 );
    }

    public long skip( long l )
        throws IOException
    {
        return in.skip( l );
    }
}


