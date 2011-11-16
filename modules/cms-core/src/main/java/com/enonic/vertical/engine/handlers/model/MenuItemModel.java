package com.enonic.vertical.engine.handlers.model;

import com.enonic.cms.core.structure.menuitem.MenuItemType;

public class MenuItemModel
{
    private int primaryKey; // new (in new created site) menu item primary key
    private Integer type; // menu item type ( page/ URL / label / section / shortcut )
    private Integer shortcutKey; // old shortcut key (linked to) in case SHORTCUT type, null otherwise

    public MenuItemModel( int primaryKey, Integer type, Integer shortcutKey )
    {
        this.primaryKey = primaryKey;
        this.type = type;
        this.shortcutKey = shortcutKey;
    }
    public boolean isShortcut()
    {
        return MenuItemType.SHORTCUT.getKey().equals( type );
    }

    public Integer getShortcutKey()
    {
        return shortcutKey;
    }
    public int getPrimaryKey()
    {
        return primaryKey;
    }
}
