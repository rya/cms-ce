/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.io;

/**
 * This enumeration defines the various byte order marks.
 */
public enum ByteOrderMark
{
    /**
     * BOM Marker for UTF 8.
     */
    UTF8( "UTF-8", new byte[]{(byte) 0xef, (byte) 0xbb, (byte) 0xbf} ),

    /**
     * BOM Marker for UTF 16, little endian.
     */
    UTF16LE( "UTF-16LE", new byte[]{(byte) 0xff, (byte) 0xfe} ),

    /**
     * BOM Marker for UTF 16, big endian.
     */
    UTF16BE( "UTF-16BE", new byte[]{(byte) 0xfe, (byte) 0xff} );

    /**
     * Character encoding.
     */
    private final String encoding;

    /**
     * BOM marke bytes.
     */
    private final byte[] bytes;

    /**
     * Construct a BOM enum.
     */
    ByteOrderMark( String encoding, byte[] bytes )
    {
        this.encoding = encoding;
        this.bytes = bytes;
    }

    /**
     * Return the encoding.
     */
    public String getEncoding()
    {
        return this.encoding;
    }

    /**
     * Return the BOM bytes.
     */
    public byte[] getBytes()
    {
        return this.bytes;
    }

    /**
     * Return true if the buffer matches.
     */
    public boolean matches( byte[] buffer )
    {
        if ( buffer.length < this.bytes.length )
        {
            return false;
        }

        for ( int i = 0; i < this.bytes.length; i++ )
        {
            if ( this.bytes[i] != buffer[i] )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Return true if it matches the bom.
     */
    public static ByteOrderMark resolve( byte[] buffer )
    {
        for ( ByteOrderMark bom : ByteOrderMark.values() )
        {
            if ( bom.matches( buffer ) )
            {
                return bom;
            }
        }

        return null;
    }
}
