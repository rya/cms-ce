/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector;

/**
 * Jun 24, 2009
 */
public class UserAlreadyExistsException
    extends RuntimeException
{
    private String userStoreName;

    private String uid;

    public UserAlreadyExistsException( String userStoreName, String uid )
    {
        super( "User '" + uid + "' already exists in userstore '" + userStoreName + "'" );
        this.userStoreName = userStoreName;
        this.uid = uid;
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public String getUid()
    {
        return uid;
    }
}
