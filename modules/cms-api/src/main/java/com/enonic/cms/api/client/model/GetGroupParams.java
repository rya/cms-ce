/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class GetGroupParams
    extends AbstractParams
{

    private static final long serialVersionUID = -8677566277616296562L;

    /**
     * Specify group either by qualified group name (&lt;userStoreKey&gt;:&lt;group name&gt;) or key. When specifying a key, prefix with a
     * hash (group = #xxx).
     */
    public String group;

    /**
     * Specify to include specified group's members. Default is false.
     */
    public boolean includeMembers = false;

    /**
     * Specify to include the groups memberships. Default is false.
     */
    public boolean includeMemberships = false;

    /**
     * If set to false; only direct memeberships are listed. If set to true; indirect memberships are also listed.
     */
    public boolean normalizeGroups = false;
}