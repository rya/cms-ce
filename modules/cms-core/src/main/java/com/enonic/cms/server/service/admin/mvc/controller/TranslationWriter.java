/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.mvc.controller;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

public final class TranslationWriter
    extends PrintWriter
{

    /**
     * The language map.
     */
    private Map translationMap;

    /**
     * State: Listen. The translation writer listens for the variable start character (%).
     */
    private static int LISTEN = 0;

    /**
     * State: Inside variable. The translation writer are inside a variable.
     */
    private static int INSIDE_VARIABLE = 1;

    /**
     * State: Variable finished. The translation writer just read the variable end character (%).
     */
    //private static int VARIABLE_FINISHED = 2;

    /**
     * The translation writer state. Initial state: Listen.
     */
    private int state = LISTEN;

    /**
     * The current variable. Empty when in listen state.
     */
    protected StringBuffer variable = new StringBuffer();

    /**
     * Creates a new translation writer.
     *
     * @param translationMap
     * @param out            a writer to print out to
     */
    public TranslationWriter( Map translationMap, Writer out )
    {
        super( out );
        this.translationMap = translationMap;
    }

    /**
     * Writes a single character and handles translation of variables.
     *
     * @param c
     */
    public void write( int c )
    {
        // if we are listening for a tag start..
        if ( state == LISTEN )
        {
            if ( c == '%' )
            {
                state = INSIDE_VARIABLE;
                variable.append( '%' );
            }
            else
            {
                super.write( c );
            }
        }

        // if we are inside a tag..
        else if ( state == INSIDE_VARIABLE )
        {
            // if the tag is closed..
            if ( c == '%' )
            {
                variable.append( '%' );

                translate();

                state = LISTEN;
                variable.setLength( 0 );

                return;
            }

            // A variable may contain only letters a-z/A-Z and numbers 0-9
            boolean letter = ( ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) );
            boolean number = ( c >= '0' && c <= '9' );

            // If it is not a letter or number, or it does not start with a letter
            if ( !( letter || number ) || ( variable.length() == 1 && !letter ) )
            {
                variable.append( (char) c );
                super.write( variable.toString() );
                variable.setLength( 0 );
                state = LISTEN;
            }
            else
            {
                variable.append( (char) c );
            }

        }
    }

    /**
     * Translate the variable.
     */
    private void translate()
    {
        String key = variable.toString();
        if ( translationMap.containsKey( key ) )
        {
            String translated = (String) translationMap.get( key );
            write( translated );
        }
        else
        {
            write( key );
        }
    }

    /**
     * @see java.io.Writer#close()
     */
    public void close()
    {
        if ( variable.length() > 0 )
        {
            write( variable.toString() );
            super.flush();
        }
        super.close();
    }

    /**
     * @see java.io.Writer#flush()
     */
    public void flush()
    {
        super.flush();
    }

    /**
     * @see java.io.Writer#write(char[],int,int)
     */
    public void write( char[] cbuf, int off, int len )
    {
        for ( int i = off; i < off + len; i++ )
        {
            write( cbuf[i] );
        }
    }
}