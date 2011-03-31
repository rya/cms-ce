/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getMenuItem.
 */
public final class GetMenuItemParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Specifies which menu item to retrieve.
     * This field is mandatory.  If not set by user, no data will be returned.
     */
    public int menuItemKey = -1;

    /**
     * Defines whether to retrieve the parent menu items (path) to the current page.
     * Default is false.
     */
    public boolean withParents = false;

    /**
     * Defines if the result should also include all details related to the menu item.
     * Default is false.
     */
    public boolean details = false;
}
