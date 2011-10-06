/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.category.CategoryKey;

public class CategoryKeyUserType
    extends AbstractIntegerBasedUserType<CategoryKey>
{
    public CategoryKeyUserType()
    {
        super( CategoryKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public CategoryKey get( int value )
    {
        return new CategoryKey( value );
    }

    public Integer getIntegerValue( CategoryKey value )
    {
        return value.toInt();
    }
}