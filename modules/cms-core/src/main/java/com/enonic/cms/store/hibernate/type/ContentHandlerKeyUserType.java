/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.ContentHandlerKey;

public class ContentHandlerKeyUserType
    extends AbstractIntegerBasedUserType<ContentHandlerKey>
{
    public ContentHandlerKeyUserType()
    {
        super( ContentHandlerKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public ContentHandlerKey get( int value )
    {
        return new ContentHandlerKey( value );
    }

    public Integer getIntegerValue( ContentHandlerKey value )
    {
        return value.toInt();
    }
}