/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */

package com.enonic.cms.api.client.model;

public class CreateCategoryParams
    extends AbstractParams
{
    private static final long serialVersionUID = 8835663063064609797L;

    /**
     * Specify the type of content (by key) to be allowed in the category. Null creates a category that allows no content to be added. Default is null.
     */
    public Integer contentTypeKey;

    /**
     * Specify the type of content (by name) to be allowed in the category. Null creates a category that allows no content to be added. Default is null.
     */
    public String contentTypeName;

    public Integer parentCategoryKey;

    public String name;

    /**
     * Default is false.
     */
    public boolean autoApprove = false;
}
