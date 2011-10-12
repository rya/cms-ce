/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public final class MenuItemAccessRight
    extends AccessRight
{
    private boolean publish = false;

    private boolean administrate = false;

    private boolean add = false;

    public MenuItemAccessRight()
    {
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
}
