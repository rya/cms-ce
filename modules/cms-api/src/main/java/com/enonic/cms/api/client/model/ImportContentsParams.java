/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.util.Date;

/**
 * This class implements parameters for importContents.
 */
public final class ImportContentsParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    public int categoryKey = -1;

    public String importName = null;

    public String data = null;

    public Date publishFrom = null;

    public Date publishTo = null;

    /**
     * Specify assignee either by qualified user name ([userStoreKey:]&lt;group name&gt;) or key.
     * When specifying a key, prefix with a hash (user = #xxx).
     */
    public String assignee;

    public String assignmentDescription;

    public Date assignmentDueDate;

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "ImportContentsParams {" + " categoryKey: " + categoryKey + " importName: " + importName + " data: " + data +
            " publishFrom: " + publishFrom + " publishTo: " + publishTo + " }";

    }

}
