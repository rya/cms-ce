/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector;

/**
 * Created by rmy - Date: Sep 4, 2009
 */
public class EmailAlreadyExistsException
    extends RuntimeException
{
    private final String userStoreName;

    private final String userName;

    private final String email;

    public EmailAlreadyExistsException( final String userStoreName, final String userName, final String email )
    {
        super( createMessage( userStoreName, userName, email ) );
        this.userStoreName = userStoreName;
        this.userName = userName;
        this.email = email;
    }

    public static String createMessage( final String userStoreName, final String userName, final String email )
    {
        return "Email '" + email + "' already exists in userstore '" + userStoreName + "'";
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getEmail()
    {
        return email;
    }
}
