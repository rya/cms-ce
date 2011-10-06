/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;
import com.enonic.cms.domain.IntBasedKey;

public class ContentVersionKey
    extends AbstractIntegerBasedKey
    implements Serializable, IntBasedKey
{
    public ContentVersionKey( String key )
    {
        init( key );
    }

    public ContentVersionKey( int key )
    {
        init( key );
    }

    public ContentVersionKey( Integer key )
    {
        init( key );
    }

    @Override
    protected int minAllowedValue()
    {
        return -1; // must be allowed for preview
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

        ContentVersionKey contentKey = (ContentVersionKey) o;

        return intValue == contentKey.intValue;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 713, 599 ).append( intValue ).toHashCode();
    }

    public static List<ContentVersionKey> createList( Collection<ContentVersionEntity> versions )
    {
        List<ContentVersionKey> list = new ArrayList<ContentVersionKey>();
        if ( versions == null || versions.size() == 0 )
        {
            return list;
        }

        for ( ContentVersionEntity version : versions )
        {
            list.add( version.getKey() );
        }

        return list;
    }

}