/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.content.binary.BinaryDataKey;


public class BinaryDataKeyUserType
    extends AbstractIntegerBasedUserType<BinaryDataKey>
{
    public BinaryDataKeyUserType()
    {
        super( BinaryDataKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public BinaryDataKey get( int value )
    {
        return new BinaryDataKey( value );
    }

    public Integer getIntegerValue( BinaryDataKey value )
    {
        return value.toInt();
    }
}