/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax.dto;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 5, 2010
 * Time: 1:57:55 PM
 */
public class UserDto
{

    String key;

    String userGroupKey;

    String displayName;

    String qualifiedName;

    String email;

    String value;

    String label;

    String highestAccessRight;

    boolean photoExists;

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getUserGroupKey()
    {
        return userGroupKey;
    }

    public void setUserGroupKey( String userGroupKey )
    {
        this.userGroupKey = userGroupKey;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public boolean isPhotoExists()
    {
        return photoExists;
    }

    public void setPhotoExists( boolean photoExists )
    {
        this.photoExists = photoExists;
    }

    public String getQualifiedName()
    {
        return qualifiedName;
    }

    public void setQualifiedName( String qualifiedName )
    {
        this.qualifiedName = qualifiedName;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel( String label )
    {
        this.label = label;
    }

    public String getHighestAccessRight()
    {
        return highestAccessRight;
    }

    public void setHighestAccessRight( String highestAccessRight )
    {
        this.highestAccessRight = highestAccessRight;
    }
}
