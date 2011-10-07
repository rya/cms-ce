/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

/**
 * Created by rmy - Date: Sep 3, 2009
 */
public class UserStorageExistingEmailException
    extends RuntimeException
{
    private String message;

    public UserStorageExistingEmailException( String email, String userStoreName )
    {
        message = "Email address already exists in userstore '" + userStoreName + "' : " + email;
    }


    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
}
