/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector;

/**
 * Jun 24, 2009
 */
public class GroupAlreadyExistsException
    extends RuntimeException
{
    private String userStoreName;

    private String name;

    public GroupAlreadyExistsException( String userStoreName, String groupName )
    {
        super( "Group '" + groupName + "' already exists in userstore '" + userStoreName + "'" );
        this.userStoreName = userStoreName;
        this.name = groupName;
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