package com.enonic.cms.core.content.category.access;


import com.enonic.cms.core.security.group.GroupKey;

public class CategoryAccessRights
{
    private GroupKey groupKey;

    private boolean readAccess;

    private boolean adminBrowseAccess;

    private boolean createAccess;

    private boolean publishAccess;

    private boolean adminAccess;

    public GroupKey getGroupKey()
    {
        return groupKey;
    }

    public void setGroupKey( GroupKey groupKey )
    {
        this.groupKey = groupKey;
    }

    public boolean isReadAccess()
    {
        return readAccess;
    }

    public void setReadAccess( boolean readAccess )
    {
        this.readAccess = readAccess;
    }

    public boolean isAdminBrowseAccess()
    {
        return adminBrowseAccess;
    }

    public void setAdminBrowseAccess( boolean adminBrowseAccess )
    {
        this.adminBrowseAccess = adminBrowseAccess;
    }

    public boolean isCreateAccess()
    {
        return createAccess;
    }

    public void setCreateAccess( boolean createAccess )
    {
        this.createAccess = createAccess;
    }

    public boolean isPublishAccess()
    {
        return publishAccess;
    }

    public void setPublishAccess( boolean publishAccess )
    {
        this.publishAccess = publishAccess;
    }

    public boolean isAdminAccess()
    {
        return adminAccess;
    }

    public void setAdminAccess( boolean adminAccess )
    {
        this.adminAccess = adminAccess;
    }
}
