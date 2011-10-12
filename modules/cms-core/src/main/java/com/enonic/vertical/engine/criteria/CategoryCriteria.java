/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.criteria;

import java.util.List;

public class CategoryCriteria
{
    private boolean useDisableAttribute = true;

    private List<Integer> categoryKeys = null;

    private int categoryKey = -1;

    public void setCategoryKey( int value )
    {
        categoryKey = value;
    }

    public int getCategoryKey()
    {
        return categoryKey;
    }

    public void addCategoryKeys( List<Integer> categoryKeys )
    {
        if ( this.categoryKeys == null )
        {
            this.categoryKeys = categoryKeys;
        }
        else
        {
            for ( Integer categoryKey : categoryKeys )
            {
                this.categoryKeys.add( categoryKey );
            }
        }
    }

    public List<Integer> getCategoryKeys()
    {
        return categoryKeys;
    }

    public boolean useDisableAttribute()
    {
        return useDisableAttribute;
    }

    public void setUseDisableAttribute( boolean value )
    {
        useDisableAttribute = value;
    }
}
