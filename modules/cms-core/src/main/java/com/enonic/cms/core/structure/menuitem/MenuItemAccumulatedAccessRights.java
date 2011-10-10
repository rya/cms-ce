/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import com.enonic.cms.core.security.user.UserKey;

/**
 * Nov 19, 2009
 */
public class MenuItemAccumulatedAccessRights
{
    private UserKey user;

    private MenuItemKey menuItem;

    private boolean readAccess;

    private boolean createAccess;

    private boolean publishAccess;

    private boolean adminAccess;

    private boolean updateAccess;

    private boolean deleteAccess;

    private boolean addAccess;

    public MenuItemAccumulatedAccessRights( boolean initialValue, UserKey userKey, MenuItemKey menuItemKey )
    {
        this.user = userKey;
        this.menuItem = menuItemKey;
        setAllTo( initialValue );
    }

    public UserKey getUser()
    {
        return user;
    }

    public MenuItemKey getMenuItem()
    {
        return menuItem;
    }

    public boolean isAllTrue()
    {
        return readAccess && createAccess && publishAccess && adminAccess && updateAccess && deleteAccess && addAccess;
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

    public boolean isDeleteAccess()
    {
        return deleteAccess;
    }

    public boolean isAddAccess()
    {
        return addAccess;
    }

    public void setAllTo( boolean value )
    {
        readAccess = value;
        createAccess = value;
        publishAccess = value;
        adminAccess = value;
        updateAccess = value;
        deleteAccess = value;
        addAccess = value;
    }

    public void accumulate( MenuItemAccessEntity access )
    {
        readAccess = readAccess || access.isReadAccess();
        createAccess = createAccess || access.isCreateAccess();
        publishAccess = publishAccess || access.isPublishAccess();
        adminAccess = adminAccess || access.isAdminAccess();
        updateAccess = updateAccess || access.isUpdateAccess();
        deleteAccess = deleteAccess || access.isDeleteAccess();
        addAccess = addAccess || access.isAddAccess();
    }
}
