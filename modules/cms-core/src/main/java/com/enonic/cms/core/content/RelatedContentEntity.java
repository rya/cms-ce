/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class RelatedContentEntity
    implements Serializable
{

    private RelatedContentKey key;

    public RelatedContentKey getKey()
    {
        return key;
    }

    public void setKey( RelatedContentKey key )
    {
        this.key = key;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RelatedContentEntity ) )
        {
            return false;
        }

        RelatedContentEntity that = (RelatedContentEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 435, 575 ).append( key ).toHashCode();
    }
}
