/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getCategories, which allows you to retrieve a sub tree of categories within a content repository.
 */
public final class GetCategoriesParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Identifies the category that will be the root of the sub tree.
     * This field is mandatory.  If not set by user, no categories will be returned.
     */
    public int categoryKey = -1;

    /**
     * Defines how many levels of the sub tree to return. The value zero (0) tells the method to retrieve the entire sub tree.
     * Default is 0.
     */
    public int levels = 0;

    /**
     * Defines whether to retrieve the top category defined in category or not.
     * Default is false.
     */
    public boolean includeTopCategory = false;

    /**
     * Defines if whether to include the number of published contents in the category or not.
     * Default is false.
     */
    public boolean includeContentCount = false;
}
