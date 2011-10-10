/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.security.user.UserType;

/**
 *
 */
public class UserTypeUserType
    extends AbstractIntegerBasedUserType<UserType>
{
    public UserTypeUserType()
    {
        super( UserType.class );
    }

    public UserType get( int value )
    {
        return UserType.getByKey( value );
    }

    public Integer getIntegerValue( UserType value )
    {
        return value.getKey();
    }

    public boolean isMutable()
    {
        return false;
    }
}
