/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

public final class TranslationReader
    extends Reader
{

    private final static int VARIABLE_LENGTH = 128;

    private final static int BUFFER_SIZE = 16 * 1024;

    private final static int STATE_LISTEN = 0;

    private final static int STATE_VARIABLE = 1;

    private Map translations;

    private Reader reader;

    private char[] buffer = new char[BUFFER_SIZE * 2];

    private int offset;

    private int length;

    private StringBuffer variable = new StringBuffer( VARIABLE_LENGTH );

    private int state = STATE_LISTEN;

    /**
     * Creates a new translation writer.
     *
     * @param translations
     * @param reader
     */
    public TranslationReader( Map translations, Reader reader )
    {
        this.translations = translations;
        this.reader = reader;
    }

    /**
     * @see java.io.Reader#read(char[],int,int)
     */
    public int read( char[] cbuf, int off, int len )
        throws IOException
    {
        if ( this.length < len )
        {
            int translated = translate();
            while ( translated > 0 && this.length < len )
            {
                translated = translate();
            }
        }
        for ( int i = 0; i < len && i < this.length; i++ )
        {
            cbuf[off + i] = this.buffer[this.offset + i];
        }
        int count = Math.min( len, this.length );
        this.offset += count;
        this.length -= count;
        if ( count <= 0 )
        {
            return -1;
        }
        return count;
    }

    private int translate()
        throws IOException
    {
        int initialLength = this.length;

        // reset buffer
        if ( this.length == 0 )
        {
            this.offset = 0;
        }
        else if ( this.offset > 0 )
        {
            System.arraycopy( this.buffer, this.offset, this.buffer, 0, this.length );
            this.offset = 0;
        }

        // read untranslated text
        char[] untranslated = new char[BUFFER_SIZE];
        int length = this.reader.read( untranslated );
        if ( length < 0 )
        {
            return length;
        }

        // resize buffer if necessary
        if ( this.length + length > ( this.buffer.length - this.buffer.length / 5 ) )
        {
            resizeBuffer( this.length + length );
        }

        for ( int i = 0; i < length; i++ )
        {
            // listen
            if ( state == STATE_LISTEN )
            {
                if ( untranslated[i] == '%' )
                {
                    this.state = STATE_VARIABLE;
                    this.variable.append( '%' );
                }
                else
                {
                    this.buffer[this.length] = untranslated[i];
                    this.length++;
                }
            }

            // variable
            else
            {
                // end of variable reached
                if ( untranslated[i] == '%' )
                {
                    if ( this.variable.length() > 1 )
                    {
                        this.variable.append( untranslated[i] );
                        String key = this.variable.toString();
                        String value;
                        if ( this.translations.containsKey( key ) )
                        {
                            value = (String) this.translations.get( key );
                        }
                        else
                        {
                            value = key;
                        }

                        // resize buffer if necessary
                        if ( this.length + value.length() > this.buffer.length )
                        {
                            resizeBuffer( value.length() + length );
                        }

                        for ( int j = 0; j < value.length(); j++ )
                        {
                            this.buffer[this.length] = value.charAt( j );
                            this.length++;
                        }
                    }
                    else
                    {
                        this.buffer[this.length] = '%';
                        this.length++;
                        this.buffer[this.length] = '%';
                        this.length++;
                    }
                    this.variable.setLength( 0 );
                    this.state = STATE_LISTEN;
                }

                // variable character saved
                else if ( validVariableCharacter( this.variable.length() - 1, untranslated[i] ) )
                {
                    this.variable.append( untranslated[i] );
                }

                // unexpected end of variable
                else
                {
                    for ( int j = 0; j < this.variable.length(); j++ )
                    {
                        this.buffer[this.length] = this.variable.charAt( j );
                        this.length++;
                    }
                    this.buffer[this.length] = untranslated[i];
                    this.length++;
                    this.variable.setLength( 0 );
                    this.state = STATE_LISTEN;
                }
            }
        }

        return this.length - initialLength;
    }

    private void resizeBuffer( int minLength )
    {
        int increase = ( ( ( minLength - this.buffer.length ) / 1024 ) + 1 ) * 1024;
        increase = Math.max( increase, this.buffer.length / 10 );
        char[] newBuffer = new char[this.buffer.length + increase];
        System.arraycopy( this.buffer, this.offset, newBuffer, this.offset, this.length );
        this.buffer = newBuffer;
    }

    private boolean validVariableCharacter( int i, int c )
    {
        boolean letter = ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) );
        boolean number = ( c >= '0' && c <= '9' );
        return ( i == 0 && letter ) || ( i > 0 && ( letter || number ) );
    }

    /**
     * @see java.io.Reader#close()
     */
    public void close()
        throws IOException
    {
        reader.close();
    }

}
