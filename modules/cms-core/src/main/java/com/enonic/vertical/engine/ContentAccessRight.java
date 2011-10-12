/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public final class ContentAccessRight
    extends AccessRight
{
    private boolean update;

    public ContentAccessRight()
    {
    }

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    public boolean getUpdate()
    {
        return update;
    }
}
