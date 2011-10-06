/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.binary.ContentBinaryDataKey;


public class ContentBinaryDataKeyUserType
    extends AbstractIntegerBasedUserType<ContentBinaryDataKey>
{
    public ContentBinaryDataKeyUserType()
    {
        super( ContentBinaryDataKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public ContentBinaryDataKey get( int value )
    {
        return new ContentBinaryDataKey( value );
    }

    public Integer getIntegerValue( ContentBinaryDataKey value )
    {
        return value.toInt();
    }
}