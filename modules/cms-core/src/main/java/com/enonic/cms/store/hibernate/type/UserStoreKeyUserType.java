/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.security.userstore.UserStoreKey;


public class UserStoreKeyUserType
    extends AbstractIntegerBasedUserType<UserStoreKey>
{
    public UserStoreKeyUserType()
    {
        super( UserStoreKey.class );
    }

    public boolean isMutable()
    {
        return false;
    }

    public UserStoreKey get( int value )
    {
        return new UserStoreKey( value );
    }

    public Integer getIntegerValue( UserStoreKey value )
    {
        return value.toInt();
    }
}