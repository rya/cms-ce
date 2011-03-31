/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

/**
 * This class implements parameters for getResource.
 */
public final class GetResourceParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    public String resourcePath = null;

    public boolean includeUsedBy = false;

    public boolean includeData = true;
}
