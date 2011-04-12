/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.hibernate.type;

import com.enonic.cms.core.security.user.UserKey;

/**
 *
 */
public class UserKeyUserType
    extends AbstractStringBasedUserType<UserKey>
{
    public UserKeyUserType()
    {
        super( UserKey.class );
    }

    public UserKey get( String value )
    {
        return new UserKey( value );
    }

    public String getStringValue( UserKey value )
    {
        return value.toString();
    }

    public boolean isMutable()
    {
        return false;
    }
}
