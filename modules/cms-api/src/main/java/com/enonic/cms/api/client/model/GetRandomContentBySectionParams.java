/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class GetRandomContentBySectionParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Key(s) of the menuitems (sections) from which the contents are to be retrieved.
     * This field is mandatory.  If not set by user, no content will be returned.
     */
    public int[] menuItemKeys = null;

    /**
     * Specify how many levels to include of the sub menu. 0 = all, 1 = only current, 2 = current and 1 level below, etc.
     * Default is 0.
     */
    public int levels = 0;

    /**
     * Syntax as specified in the Content Query Language (se documentation).
     * Default is empty, which, if not overruled, will return every content for the given categories.
     */
    public String query = "";

    /**
     * Specifies the count of how many content elements to include in the result.
     * Default is 100.
     */
    public int count = 100;

    /**
     * Specifies if the data element of the contents should be included in the XML.
     * Default is false.
     */
    public boolean includeData = false;

    /**
     * How many levels of children related contents to retrieve.
     * Default is 0.
     */
    public int childrenLevel = 0;

    /**
     * How many levels of parent related contents to retrieve.
     * Default is 0.
     */
    public int parentLevel = 0;

    /**
     * Specifies if user rights information should be included in the XML.
     * Default is false.
     */
    public boolean includeUserRights = false;

    /**
     * Specifies if content that matches the search, but is offline, should be included in the result set.
     * Default is false.
     */
    public boolean includeOfflineContent = false;
}
