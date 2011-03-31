/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.lob;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * A simple implementation of blob.
 */
public final class SimpleBlob
    implements Blob
{
    /**
     * Input stream.
     */
    private final InputStream stream;

    /**
     * Length.
     */
    private final int length;

    /**
     * Need reset?
     */
    private boolean needsReset = false;

    /**
     * Construct the blob.
     */
    public SimpleBlob( byte[] bytes )
    {
        this( new ByteArrayInputStream( bytes ), bytes.length );
    }

    /**
     * Construct the blob.
     */
    public SimpleBlob( InputStream stream, int length )
    {
        this.stream = stream;
        this.length = length;
    }

    /**
     * @see java.sql.Blob#length()
     */
    public long length()
        throws SQLException
    {
        return this.length;
    }

    /**
     * @see java.sql.Blob#truncate(long)
     */
    public void truncate( long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#getBytes(long,int)
     */
    public byte[] getBytes( long pos, int len )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#setBytes(long,byte[])
     */
    public int setBytes( long pos, byte[] bytes )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#setBytes(long,byte[],int,int)
     */
    public int setBytes( long pos, byte[] bytes, int i, int j )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#position(byte[],long)
     */
    public long position( byte[] bytes, long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#getBinaryStream()
     */
    public InputStream getBinaryStream()
        throws SQLException
    {
        try
        {
            if ( this.needsReset )
            {
                this.stream.reset();
            }
        }
        catch ( IOException e )
        {
            throw new SQLException( "could not reset reader" );
        }

        this.needsReset = true;
        return this.stream;
    }

    /**
     * @see java.sql.Blob#setBinaryStream(long)
     */
    public OutputStream setBinaryStream( long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Blob#position(Blob,long)
     */
    public long position( Blob blob, long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void free()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public InputStream getBinaryStream( long pos, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}

