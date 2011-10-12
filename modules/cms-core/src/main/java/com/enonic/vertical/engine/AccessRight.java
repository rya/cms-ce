/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

public abstract class AccessRight
{
    // constants:

    public final static int CATEGORY = 0;

    public final static int CONTENT = 1;

    public final static int MENUITEM = 2;

    public final static int MENUITEM_DEFAULT = 3;

    public final static int SECTION = 4;

    private boolean read = false;

    public boolean getRead()
    {
        return read;
    }

    public void setRead( boolean read )
    {
        this.read = read;
    }
}
