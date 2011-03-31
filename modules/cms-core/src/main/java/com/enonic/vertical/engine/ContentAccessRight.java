/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public final class ContentAccessRight
    extends AccessRight
{

    private boolean update;

    public ContentAccessRight( int key )
    {
        super( key, AccessRight.MENUITEM );
    }

    public void setUpdate( boolean update )
    {
        this.update = update;
    }

    public void setDelete( boolean delete )
    {
    }

    public boolean getUpdate()
    {
        return update;
    }

}
