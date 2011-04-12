/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.security.group.GroupKey;

public class CategoryAccessKey
    implements Serializable
{

    private CategoryKey categoryKey;

    private GroupKey groupKey;

    public CategoryAccessKey()
    {
    }

    public CategoryAccessKey( CategoryKey categoryKey, GroupKey groupKey )
    {
        this.categoryKey = categoryKey;
        this.groupKey = groupKey;
    }

    public CategoryKey getCategoryKey()
    {
        return categoryKey;
    }

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public void setCategoryKey( CategoryKey categoryKey )
    {
        this.categoryKey = categoryKey;
    }

    public void setGroupKey( GroupKey groupKey )
    {
        this.groupKey = groupKey;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof CategoryAccessKey ) )
        {
            return false;
        }

        CategoryAccessKey that = (CategoryAccessKey) o;

        if ( categoryKey != null ? !categoryKey.equals( that.getCategoryKey() ) : that.getCategoryKey() != null )
        {
            return false;
        }
        if ( groupKey != null ? !groupKey.equals( that.getGroupKey() ) : that.getGroupKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 597, 753 ).append( categoryKey ).append( groupKey ).toHashCode();
    }
}
