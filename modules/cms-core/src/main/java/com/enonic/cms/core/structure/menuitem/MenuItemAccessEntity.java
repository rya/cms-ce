/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class MenuItemAccessEntity
    implements Serializable
{
    private MenuItemAccessKey key;

    private boolean readAccess;

    private boolean createAccess;

    private boolean publishAccess;

    private boolean adminAccess;

    private boolean updateAccess;

    private boolean deleteAccess;

    private boolean addAccess;

    public MenuItemAccessKey getKey()
    {
        return key;
    }

    public boolean isReadAccess()
    {
        return readAccess;
    }

    public boolean isCreateAccess()
    {
        return createAccess;
    }

    public boolean isPublishAccess()
    {
        return publishAccess;
    }

    public boolean isAdminAccess()
    {
        return adminAccess;
    }

    public boolean isUpdateAccess()
    {
        return updateAccess;
    }

    public boolean isAddAccess()
    {
        return addAccess;
    }

    public boolean isDeleteAccess()
    {
        return deleteAccess;
    }

    public void setKey( MenuItemAccessKey key )
    {
        this.key = key;
    }

    public void setReadAccess( boolean readAccess )
    {
        this.readAccess = readAccess;
    }

    public void setCreateAccess( boolean createAccess )
    {
        this.createAccess = createAccess;
    }

    public void setPublishAccess( boolean publishAccess )
    {
        this.publishAccess = publishAccess;
    }

    public void setAdminAccess( boolean adminAccess )
    {
        this.adminAccess = adminAccess;
    }

    public void setUpdateAccess( boolean updateAccess )
    {
        this.updateAccess = updateAccess;
    }

    public void setDeleteAccess( boolean deleteAccess )
    {
        this.deleteAccess = deleteAccess;
    }

    public void setAddAccess( boolean addAccess )
    {
        this.addAccess = addAccess;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof MenuItemAccessEntity ) )
        {
            return false;
        }

        MenuItemAccessEntity that = (MenuItemAccessEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 445, 653 ).append( key ).toHashCode();
    }
}
