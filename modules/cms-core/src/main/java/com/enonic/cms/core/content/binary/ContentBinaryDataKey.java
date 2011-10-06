/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;


public class ContentBinaryDataKey
    extends AbstractIntegerBasedKey
    implements Serializable
{

    public ContentBinaryDataKey( String key )
    {
        init( key );
    }

    public ContentBinaryDataKey( int key )
    {
        init( key );
    }

    public ContentBinaryDataKey( Integer key )
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

        ContentBinaryDataKey key = (ContentBinaryDataKey) o;

        return intValue == key.intValue;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 245, 643 ).append( intValue ).toHashCode();
    }

}