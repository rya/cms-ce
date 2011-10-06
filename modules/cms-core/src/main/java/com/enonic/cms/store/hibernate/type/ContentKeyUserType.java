/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.ContentKey;


public class ContentKeyUserType
    extends AbstractIntegerBasedUserType<ContentKey>
{
    public ContentKeyUserType()
    {
        super( ContentKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public ContentKey get( int value )
    {
        return new ContentKey( value );
    }

    public Integer getIntegerValue( ContentKey value )
    {
        return value.toInt();
    }
}