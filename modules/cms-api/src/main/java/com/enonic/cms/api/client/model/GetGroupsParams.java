/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class GetGroupsParams
    extends AbstractParams
{

    private static final long serialVersionUID = -6214979826162545451L;

    /**
     * Specify user store either by name or key. When specifying a key, prefix with a hash (userStore = #xxx).
     */
    public String userStore = null;

    /**
     * If specified, only groups of these types are returned.
     */
    public String[] groupTypes = null;

    /**
     * If set to true; deleted groups are included. Default is false.
     */
    public boolean includeDeletedGroups = false;

    /**
     * If set to true; built-in groups are included. Default is false.
     */
    public boolean includeBuiltInGroups = false;

    /**
     * Specify to include which groups that are member of a group. Default is false.
     */
    public boolean includeMembers = false;

    /**
     * Specify to include the groups memberships. Default is false.
     */
    public boolean includeMemberships = false;

    /**
     * Specify start position in the list of matching users. Default is 0.
     */
    public int index = 0;

    /**
     * Specify the number of users to return. Default is 100.
     */
    public int count = 100;


}