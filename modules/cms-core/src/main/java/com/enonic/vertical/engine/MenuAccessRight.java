/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public final class MenuAccessRight
    extends AccessRight
{

    private boolean create;

    private boolean update = false;

    private boolean delete = false;

    private boolean publish = false;

    private boolean administrate = false;
    //private boolean adminread = false;

    public MenuAccessRight( int key )
    {
        super( key, AccessRight.MENUITEM_DEFAULT );
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

    /*public void setAdminRead(boolean adminread) {
        this.adminread = adminread;
    }*/

    public boolean getAdministrate()
    {
        return administrate;
    }

    /*public boolean getAdminRead() {
        return adminread;
    }*/
}
