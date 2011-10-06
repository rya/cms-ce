/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.ArrayList;
import java.util.List;

public class CategoryStatistics
{

    private Integer categoryKey;

    private Integer parentCategoryKey;

    private long size = 0;

    private Long accumulatedSize;

    private CategoryStatistics parent;

    private List<CategoryStatistics> children = new ArrayList<CategoryStatistics>();


    public CategoryStatistics()
    {
        //
    }

    public CategoryStatistics( Integer categoryKey )
    {
        this.categoryKey = categoryKey;
    }


    public Integer getCategoryKey()
    {
        return categoryKey;
    }

    public void setParentCategoryKey( Integer value )
    {
        this.parentCategoryKey = value;
    }


    public Integer getParentCategoryKey()
    {
        return parentCategoryKey;
    }

    public void setParent( CategoryStatistics value )
    {
        this.parent = value;
    }

    public CategoryStatistics getParent()
    {
        return parent;
    }

    public void setSize( long value )
    {
        this.size = value;
    }

    /**
     * Adds an amount to the size of this category.
     *
     * @param amount
     */
    public void addAmount( long amount )
    {
        size = size + amount;
    }

    public long getSize()
    {
        return size;
    }

    public void addChild( CategoryStatistics child )
    {
        children.add( child );
    }

    public Long getAccumulatedSize()
    {
        if ( accumulatedSize == null )
        {
            throw new IllegalStateException( "Size have not been accumulated yet" );
        }
        return accumulatedSize;
    }

    public long calculateAccumulatedSize()
    {
        long sum = size;
        for ( CategoryStatistics cs : children )
        {
            sum += cs.calculateAccumulatedSize();
        }

        accumulatedSize = sum;
        return sum;
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

        CategoryStatistics that = (CategoryStatistics) o;

        if ( !categoryKey.equals( that.getCategoryKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return categoryKey.hashCode();
    }


}
