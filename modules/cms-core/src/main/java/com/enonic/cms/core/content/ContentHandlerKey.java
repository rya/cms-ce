/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;


public class ContentHandlerKey
    extends AbstractIntegerBasedKey
    implements Serializable
{
    public ContentHandlerKey( String key )
    {
        init( key );
    }

    public ContentHandlerKey( int key )
    {
        init( key );
    }

    public ContentHandlerKey( Integer key )
    {
        init( key );
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ContentHandlerKey key = (ContentHandlerKey) o;

        return toInt() == key.toInt();
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 457, 635 ).append( toInt() ).toHashCode();
    }
}
