/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getSubMenu.
 */
public final class GetSubMenuParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Defines the key of the menu item, beneath which to retrieve the sub menu.
     * This field is mandatory.  If not set by user, no data will be returned.
     */
    public int menuItemKey = -1;

    /**
     * Specifies which menu item is selected and will tag the path in the XML accordingly.
     * Default is -1, which means no menu item will be tagged.
     */
    public int tagItem = -1;

    /**
     * Defines the number of depth-levels in the menu to retrieve. If set to zero (0) all levels will be retrieved.
     * Default is 0.
     */
    public int levels = 0;
}
