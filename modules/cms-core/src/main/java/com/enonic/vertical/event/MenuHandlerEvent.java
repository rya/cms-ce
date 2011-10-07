/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.event;

import com.enonic.cms.core.security.user.User;

public class MenuHandlerEvent
    extends VerticalEvent
{

    private int menuKey;

    private int menuItemKey;

    private String title;

    public MenuHandlerEvent( User user, int menuKey, int menuItemKey, String title, Object source )
    {
        super( user, source );
        this.menuKey = menuKey;
        this.menuItemKey = menuItemKey;
        this.title = title;
    }

    public int getMenuItemKey()
    {
        return menuItemKey;
    }

    public String getTitle()
    {
        return title;
    }
}
