/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getMenu, which returns the entire menu tree of a selected site. Use this method by caution as it
 * generates quite a lot of xml. Only pages that are checked as "show in menu" will be included in the result.
 */
public final class GetMenuParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * The site key (menu key).
     * This field is mandatory.  If not set by user, no data will be returned.
     */
    public int menuKey = -1;

    /**
     * This parameter specifies which menuItem is selected and will tag the path in the xml accordingly.
     * Default is -1, which means no menu item will be tagged.
     */
    public int tagItem = -1;

    /**
     * Defines the number of depth-levels in the menu to retrieve. If set to zero (0) all levels will be retrieved.
     * Default is 0.
     */
    public int levels = 0;
}
