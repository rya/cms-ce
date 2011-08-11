/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.util.Date;

/**
 * Jun 10, 2010
 */
public class AssignContentParams
    extends AbstractParams
{
    private static final long serialVersionUID = -748825321990935557L;

    /**
     * The key of the content to set assignment on.
     */
    public Integer contentKey;

    /**
     * Specify assignee eiter by qualified user name ([userStoreKey:]&lt;group name&gt;) or key.
     * If userStoreKey is omitted the internal user store is used. When specifying a key, prefix with a hash (user = #xxx).
     */
    public String assignee;

    public String assignmentDescription;

    public Date assignmentDueDate;
}
