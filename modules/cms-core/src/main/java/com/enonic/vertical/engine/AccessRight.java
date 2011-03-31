/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.io.Serializable;

public abstract class AccessRight
    implements Serializable
{
    // constants:

    public final static int CATEGORY = 0;

    public final static int CONTENT = 1;

    public final static int MENUITEM = 2;

    public final static int MENUITEM_DEFAULT = 3;

    public final static int SECTION = 4;

    // instance fields:

    private int key;

    private int type;

    private boolean read = false;

    public AccessRight( int key, int type )
    {
        this.type = type;
        this.key = key;
    }

    public boolean getRead()
    {
        return read;
    }

    public void setRead( boolean read )
    {
        this.read = read;
    }
}
