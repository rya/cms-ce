/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.lob;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

import com.enonic.cms.framework.io.ReaderInputStream;

/**
 * A simple implementation of clob.
 */
public final class SimpleClob
    implements Clob
{
    /**
     * Input reader.
     */
    private final Reader reader;

    /**
     * Length.
     */
    private final int length;

    /**
     * Need reset?
     */
    private boolean needsReset = false;

    /**
     * Construct the clob.
     */
    public SimpleClob( String text )
    {
        this( new StringReader( text ), text.length() );
    }

    /**
     * Construct the clob.
     */
    public SimpleClob( Reader reader, int length )
    {
        this.reader = reader;
        this.length = length;
    }

    /**
     * Construct the clob.
     */
    public SimpleClob( InputStream in, int length )
    {
        this( new InputStreamReader( in ), length );
    }

    /**
     * @see java.sql.Clob#length()
     */
    public long length()
        throws SQLException
    {
        return this.length;
    }

    /**
     * @see java.sql.Clob#truncate(long)
     */
    public void truncate( long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#getAsciiStream()
     */
    public InputStream getAsciiStream()
        throws SQLException
    {
        try
        {
            if ( this.needsReset )
            {
                this.reader.reset();
            }
        }
        catch ( IOException e )
        {
            throw new SQLException( "could not reset reader" );
        }

        this.needsReset = true;
        return new ReaderInputStream( this.reader );
    }

    /**
     * @see java.sql.Clob#setAsciiStream(long)
     */
    public OutputStream setAsciiStream( long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#getCharacterStream()
     */
    public Reader getCharacterStream()
        throws SQLException
    {
        try
        {
            if ( this.needsReset )
            {
                this.reader.reset();
            }
        }
        catch ( IOException e )
        {
            throw new SQLException( "could not reset reader" );
        }

        this.needsReset = true;
        return this.reader;
    }

    /**
     * @see java.sql.Clob#setCharacterStream(long)
     */
    public Writer setCharacterStream( long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#getSubString(long,int)
     */
    public String getSubString( long pos, int len )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#setString(long,String)
     */
    public int setString( long pos, String string )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#setString(long,String,int,int)
     */
    public int setString( long pos, String string, int i, int j )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#position(String,long)
     */
    public long position( String string, long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see java.sql.Clob#position(Clob,long)
     */
    public long position( Clob colb, long pos )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public void free()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public Reader getCharacterStream( long pos, long length )
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }
}
