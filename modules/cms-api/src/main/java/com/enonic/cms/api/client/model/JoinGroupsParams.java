/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class JoinGroupsParams
    extends AbstractParams
{

    private static final long serialVersionUID = 6041090124686662459L;

    /**
     * Specify user either by qualified name ([userStoreKey:]&lt;user name&gt;) or key. If null the logged in user is used.
     * When specifying a key, prefix with a hash (user = "#xxx").
     */
    public String user = null;

    /**
     * Specify group either by qualified name ([userStoreKey:]&lt;group name&gt;) or key. When specifying a key, prefix with a hash (group =
     * #xxx).
     */
    public String group = null;

    /**
     * Specify groups to join. Use same format as above.
     */
    public String[] groupsToJoin = null;

}