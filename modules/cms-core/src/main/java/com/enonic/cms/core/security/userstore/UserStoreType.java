/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

public enum UserStoreType
{
    LOCAL,
    REMOTE;

    public static UserStoreType parse( final String type )
    {
        if ( type.trim().toLowerCase().equals( "local" ) )
        {
            return UserStoreType.LOCAL;
        }
        else if ( type.trim().toLowerCase().equals( "remote" ) )
        {
            return UserStoreType.REMOTE;
        }
        else
        {
            throw new IllegalArgumentException( "Invalid user store type:" + type );
        }
    }
}
