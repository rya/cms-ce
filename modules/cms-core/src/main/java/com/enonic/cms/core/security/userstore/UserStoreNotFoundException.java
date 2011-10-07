/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.domain.NotFoundErrorType;

public class UserStoreNotFoundException
    extends RuntimeException
    implements NotFoundErrorType
{

    private UserStoreKey key;

    private String name;

    private String message;

    public UserStoreNotFoundException( UserStoreKey key )
    {
        this.key = key;
        message = "Userstore not found, key: '" + key + "'";
    }

    public UserStoreNotFoundException( String name )
    {
        this.name = name;
        message = "Userstore not found, name: '" + name + "'";
    }

    public UserStoreKey getUserStoreKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getMessage()
    {
        return message;
    }
}