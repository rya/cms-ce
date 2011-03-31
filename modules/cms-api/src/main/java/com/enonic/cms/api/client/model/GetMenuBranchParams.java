/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getMenuBranch, which returns a branch of the menu structure. The method will locate the "top level"
 * menu item of the current menu item, and return the entire tree beneath it. Only pages that are checked as "show in menu" will be included
 * in the xml.
 */
public final class GetMenuBranchParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * This parameter specifies which menuItem to start processing from.
     * This field is mandatory.  If not set by user, no data will be returned.
     */
    public int menuItemKey = -1;

    /**
     * Defines whether to include the uppermost level of the menu in the result.
     * Default is false.
     */
    public boolean includeTopLevel = false;

    /**
     * Specifies start level for the xml result, if set to 0 the top level is the start level, 1 is one step down from the top etc.
     * Default is 0.
     */
    public int startLevel = 0;

    /**
     * Specifies how many descending levels from start level to fetch, if set to 0 all levels are fetched.
     * Default is 0.
     */
    public int levels = 0;
}
