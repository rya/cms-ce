/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.io;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;


public final class HTMLTagListener
    extends Writer
{

    // writer attributes

    private Writer writer;

    // tag-listener map: associates a tag to a listener

    private Map<String, Listener> tagListenerMap;

    private static int LISTEN = 0;

    private static int INSIDE_TAG = 1;

    private static int TAG_FINISHED = 2;

    private int state = LISTEN;

    // buffer containing the full tag (including '<' og '>') when inside or just finished tag

    private StringBuffer tagBuffer = new StringBuffer( 20 );

    // buffer holding other data other than the tag

    private StringBuffer buffer = new StringBuffer( 20 );

    // the current tag being visited

    private String currentTag;

    // maps a state to a tag name which names the listener used when tag is heard

    private final Map<String, Object> tagStateMap = new HashMap<String, Object>();

    public interface Listener
    {
        public void heard( HTMLTagListener obj )
            throws IOException;
    }

    public HTMLTagListener( Listener listener, String tag, Writer writer, boolean writeBeforeHeard )
    {
        this.writer = writer;
        this.tagListenerMap = new HashMap<String, Listener>();
        this.tagListenerMap.put( tag, listener );
    }

    public HTMLTagListener( Map<String, Listener> tagListenerMap, Writer writer, boolean writeBeforeHeard )
    {
        this.writer = writer;
        this.tagListenerMap = tagListenerMap;
    }

    public Writer getWriter()
    {
        return writer;
    }

    /**
     * @see java.io.Writer#close()
     */
    public void close()
        throws IOException
    {
    }

    /**
     * @see java.io.Writer#flush()
     */
    public void flush()
        throws IOException
    {
    }

    /**
     * @see java.io.Writer#write(char[],int,int)
     */
    public void write( char[] cbuf, int off, int len )
        throws IOException
    {

        for ( int i = off; i < off + len; i++ )
        {

            // if we are listening for a tag start..
            if ( state == LISTEN )
            {
                buffer.append( cbuf[i] );
                if ( cbuf[i] == '<' )
                {
                    state = INSIDE_TAG;
                    if ( buffer.length() > 1 )
                    {
                        buffer.setLength( buffer.length() - 1 );
                        writer.write( buffer.toString() );
                    }
                    buffer.setLength( 0 );

                    // start tag buffer
                    tagBuffer.append( '<' );
                }
            }

            // if we are inside a tag..
            else if ( state == INSIDE_TAG || state == TAG_FINISHED )
            {
                // if the tag is closed..
                if ( cbuf[i] == '>' )
                {
                    if ( state != TAG_FINISHED )
                    {
                        currentTag = buffer.toString();
                        buffer.setLength( 0 );
                    }
                    tagBuffer.append( '>' );

                    // emit the event if tags match
                    if ( tagListenerMap.containsKey( currentTag ) )
                    {
                        Listener listener = tagListenerMap.get( currentTag );
                        listener.heard( this );
                    }
                    else
                    {
                        writeTagBuffer();
                    }

                    tagBuffer.setLength( 0 );
                    currentTag = null;
                    state = LISTEN;
                }
                else
                {
                    if ( state != TAG_FINISHED && cbuf[i] == ' ' )
                    {
                        state = TAG_FINISHED;
                        currentTag = buffer.toString();
                        buffer.setLength( 0 );
                    }
                    else if ( state == INSIDE_TAG )
                    {
                        buffer.append( cbuf[i] );
                    }

                    // update tag buffer
                    tagBuffer.append( cbuf[i] );
                }
            }
        }
    }

    public void writeTagBuffer()
        throws IOException
    {
        writer.write( tagBuffer.toString() );
    }

    public StringBuffer getTagBuffer()
    {
        return tagBuffer;
    }

    public Object getTagState( String tag, Object initialValue )
    {
        if ( tag != null )
        {
            if ( tagStateMap.containsKey( tag ) )
            {
                return tagStateMap.get( tag );
            }
            else
            {
                tagStateMap.put( tag, initialValue );
                return initialValue;
            }
        }
        else
        {
            return null;
        }
    }

    public void setTagState( String tag, Object state )
    {
        if ( tagStateMap != null )
        {
            tagStateMap.put( tag, state );
        }
    }

    public Object getCurrentTagState( Object initialValue )
    {
        return getTagState( currentTag, initialValue );
    }

    public void setCurrentTagState( Object state )
    {
        setTagState( currentTag, state );
    }
}