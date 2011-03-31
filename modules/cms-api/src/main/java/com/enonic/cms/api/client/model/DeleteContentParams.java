/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

import java.io.Serializable;


public class DeleteContentParams
    extends AbstractParams
    implements Serializable
{

    private static final long serialVersionUID = 3249551916910973316L;

    /**
     * The key of the content to delete.
     */
    public Integer contentKey;


}