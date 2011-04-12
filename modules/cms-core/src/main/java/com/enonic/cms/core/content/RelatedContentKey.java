/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class RelatedContentKey
    implements Serializable
{
    private ContentVersionKey parentContentVersionKey;

    private ContentKey childContentKey;

    public RelatedContentKey()
    {

    }

    public RelatedContentKey( ContentVersionKey parentContentVersionKey, ContentKey childContentKey )
    {
        this.parentContentVersionKey = parentContentVersionKey;
        this.childContentKey = childContentKey;
    }


    public ContentVersionKey getParentContentVersionKey()
    {
        return parentContentVersionKey;
    }

    public ContentKey getChildContentKey()
    {
        return childContentKey;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof RelatedContentKey ) )
        {
            return false;
        }

        RelatedContentKey that = (RelatedContentKey) o;

        if ( childContentKey != null ? !childContentKey.equals( that.getChildContentKey() ) : that.getChildContentKey() != null )
        {
            return false;
        }
        if ( parentContentVersionKey != null
            ? !parentContentVersionKey.equals( that.getParentContentVersionKey() )
            : that.getParentContentVersionKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 313, 629 ).append( childContentKey ).append( parentContentVersionKey ).toHashCode();
    }
}
