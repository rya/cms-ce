/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

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

    /*public void setAdminRead(boolean adminread) {
        this.adminread = adminread;
    }*/

    /*public boolean getAdminRead() {
        return adminread;
    }*/
}
