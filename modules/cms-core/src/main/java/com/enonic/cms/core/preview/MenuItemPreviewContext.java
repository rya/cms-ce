/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.preview;

import java.io.Serializable;

import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Sep 30, 2010
 */
public class MenuItemPreviewContext
    implements Serializable
{
    private MenuItemEntity menuItemPreviewed;

    public MenuItemPreviewContext( MenuItemEntity menuItemPreviewed )
    {
        this.menuItemPreviewed = menuItemPreviewed;
    }

    public MenuItemEntity getMenuItemPreviewed()
    {
        return menuItemPreviewed;
    }
}
