/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;

public class CreateGroupParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = -3605606808456153988L;

    /**
     * Specify the wanted name of the group.
     */
    public String name;

    /**
     * Specify the wanted description of the group.
     */
    public String description;

    /**
     * Specify if the groups is to restricted or not. Default is true. A restricted group is a group that cannot be joined or left via user
     * services.
     */
    public boolean restricted = true;

    /**
     * Specify user store either by name or key. When specifying a key, prefix with a hash (userStore = #xxx).
     */
    public String userStore;
}