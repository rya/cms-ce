/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.ContentVersionKey;


public class ContentVersionKeyUserType
    extends AbstractIntegerBasedUserType<ContentVersionKey>
{
    public ContentVersionKeyUserType()
    {
        super( ContentVersionKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public ContentVersionKey get( int value )
    {
        return new ContentVersionKey( value );
    }

    public Integer getIntegerValue( ContentVersionKey value )
    {
        return value.toInt();
    }
}