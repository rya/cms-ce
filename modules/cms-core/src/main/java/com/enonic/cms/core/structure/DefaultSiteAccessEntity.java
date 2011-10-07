/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class DefaultSiteAccessEntity
    implements Serializable
{
    private DefaultSiteAccessKey key;

    private boolean readAccess;

    private boolean createAccess;

    private boolean publishAccess;

    private boolean deleteAccess;

    private boolean adminAccess;

    private boolean updateAccess;

    private boolean addAccess;

    public DefaultSiteAccessKey getKey()
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

    public boolean isDeleteAccess()
    {
        return deleteAccess;
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

    public void setKey( DefaultSiteAccessKey key )
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

    public void setDeleteAccess( boolean deleteAccess )
    {
        this.deleteAccess = deleteAccess;
    }

    public void setAdminAccess( boolean adminAccess )
    {
        this.adminAccess = adminAccess;
    }

    public void setUpdateAccess( boolean updateAccess )
    {
        this.updateAccess = updateAccess;
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
        if ( !( o instanceof DefaultSiteAccessEntity ) )
        {
            return false;
        }

        DefaultSiteAccessEntity that = (DefaultSiteAccessEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 555, 771 ).append( key ).toHashCode();
    }
}
