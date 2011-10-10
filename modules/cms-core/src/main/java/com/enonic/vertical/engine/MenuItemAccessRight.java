/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.cms.core.structure.menuitem.MenuItemKey;

public final class MenuItemAccessRight
    extends AccessRight
{

    private boolean create;

    private boolean update = false;

    private boolean delete = false;

    private boolean publish = false;

    private boolean administrate = false;

    private boolean add = false;
    //private boolean adminread = false;

    public MenuItemAccessRight( int key )
    {
        super( key, AccessRight.MENUITEM );
    }

    public MenuItemAccessRight( MenuItemKey key )
    {
        super( key.toInt(), AccessRight.MENUITEM );
    }

    public void setCreate( boolean create )
    {
        this.create = create;
    }

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    public void setDelete( boolean delete )
    {
        this.delete = delete;
    }

    public void setPublish( boolean publish )
    {
        this.publish = publish;
    }

    public void setAdministrate( boolean administrate )
    {
        this.administrate = administrate;
    }

    public void setAdd( boolean add )
    {
        this.add = add;
    }

    /*public void setAdminRead(boolean adminread) {
        this.adminread = adminread;
    }*/

    public boolean getCreate()
    {
        return create;
    }

    public boolean getUpdate()
    {
        return update;
    }

    public boolean getDelete()
    {
        return delete;
    }

    public boolean getPublish()
    {
        return publish;
    }

    public boolean getAdministrate()
    {
        return administrate;
    }

    public boolean getAdd()
    {
        return add;
    }

    /*public boolean getAdminRead() {
        return adminread;
    }*/
}
