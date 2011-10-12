/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public final class MenuAccessRight
    extends AccessRight
{
    private boolean administrate = false;

    public MenuAccessRight()
    {
    }

    public void setAdministrate( boolean administrate )
    {
        this.administrate = administrate;
    }

    public boolean getAdministrate()
    {
        return administrate;
    }
}
