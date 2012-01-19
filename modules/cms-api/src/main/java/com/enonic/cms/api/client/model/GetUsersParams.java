/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class GetUsersParams
    extends AbstractParams
{

    private static final long serialVersionUID = -4022116431377137922L;

    /**
     * Specify user store either by name or key. When using the key, prefix with a hash (userStore = #key).
     */
    public String userStore = null;

    /**
     * If set to false; only direct memeberships are listed. If set to true; indirect memberships are also listed. Default is false.
     */
    public boolean normalizeGroups = false;

    /**
     * If set to true; deleted users are included. Default is false.
     */
    public boolean includeDeletedUsers = false;

    /**
     * If set to true; memberships are included. Default is false.
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

    /**
     * If set to true, additional attributes for the user will be included.
     */
    public boolean includeCustomUserFields = false;

}