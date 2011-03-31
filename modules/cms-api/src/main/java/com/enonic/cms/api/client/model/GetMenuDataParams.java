/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getMenuData, which returns information about the settings for a specified site (previously called
 * menu). This includes login page, front page, error page and more.
 */
public final class GetMenuDataParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * The site key (menu key).
     * This field is mandatory.  If not set by user, no site data will be returned.
     */
    public int menuKey = -1;
}
