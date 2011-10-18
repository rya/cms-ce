/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

public interface MenuItemService
{
    String getPageKeyByPath( MenuItemEntity menuItemEntity, String path );

    void execute( MenuItemServiceCommand... commands );
}
