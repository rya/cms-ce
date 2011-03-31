/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class GetContentVersionsParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    public int[] contentVersionKeys = new int[]{-1};

    /**
     * The number of levels to recurse down, to look for related children of this version.  All the children appearing as related content
     * will be main versions of that content, even if the supplied content key(s) to find children of are not main versions.  If this value
     * is large enough that recursive calls may discover one of the originating content versions as childrens children, the main version of
     * the requested version, will appear as a related child of itself in the returned XML.
     */
    public int childrenLevel = 0;

    /**
     * Wheter the content (main version) of the given versions is required to be online or not.
     */
    public boolean contentRequiredToBeOnline;
}
