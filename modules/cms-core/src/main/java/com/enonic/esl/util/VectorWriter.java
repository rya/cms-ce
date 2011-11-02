/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

public class VectorWriter
    extends Writer
{
    private StringBuffer buffer = new StringBuffer( 256 );

    private Vector v = new Vector();

    public void clear()
    {
        if ( v != null )
        {
            buffer.setLength( 0 );
            v.clear();
        }
    }

    public void close()
        throws IOException
    {
        if ( v != null )
        {
            flush();
            v.clear();
            v = null;
            buffer = null;
        }
    }

    public void flush()
        throws IOException
    {
        if ( v != null )
        {
            v.add( buffer.toString() );
            buffer.setLength( 0 );
        }
        else
        {
            throw new IOException( "writer is closed" );
        }
    }

    public String[] toStringArray()
    {
        if ( v != null )
        {
            int len = v.size();
            String[] sa = new String[len];
            for ( int i = 0; i < len; i++ )
            {
                sa[i] = (String) v.elementAt( i );
            }

            return sa;
        }

        return new String[0];
    }

    public void write( char[] cbuf, int off, int len )
        throws IOException
    {
        if ( v != null && buffer != null )
        {
            buffer.append( cbuf, off, len );
            int idx;
            while ( ( idx = buffer.toString().indexOf( '\r' ) ) >= 0 )
            {
                v.add( buffer.substring( 0, idx ) );
                if ( buffer.length() > idx + 1 && buffer.charAt( idx + 1 ) == '\n' )
                {
                    buffer.delete( 0, idx + 2 );
                }
                else
                {
                    buffer.delete( 0, idx + 1 );
                }
            }
            while ( ( idx = buffer.toString().indexOf( '\n' ) ) >= 0 )
            {
                v.add( buffer.substring( 0, idx ) );
                buffer.delete( 0, idx + 1 );
            }
        }
        else
        {
            throw new IOException( "writer is closed" );
        }
    }

    /**
     * Write a portion of a string.
     *
     * @param str A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IOException If an I/O error occurs
     */
    public void write( String str, int off, int len )
        throws IOException
    {
        char cbuf[] = new char[len];
        str.getChars( off, ( off + len ), cbuf, 0 );
        write( cbuf, 0, len );
    }
}
