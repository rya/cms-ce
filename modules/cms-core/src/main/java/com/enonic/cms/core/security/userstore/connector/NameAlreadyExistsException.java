/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector;

public class NameAlreadyExistsException
    extends RuntimeException
{
    private final String userStoreName;

    private final String name;

    public NameAlreadyExistsException( final String userStoreName, final String name )
    {
        super( createMessage( userStoreName, name ) );
        this.userStoreName = userStoreName;
        this.name = name;
    }

    public static String createMessage( final String userStoreName, final String name )
    {
        return "Name: '" + name + "' already exists in userstore '" + userStoreName + "'";
    }

    public String getUserStoreName()
    {
        return userStoreName;
    }

    public String getName()
    {
        return name;
    }
}