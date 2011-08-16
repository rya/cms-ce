package com.enonic.cms.api.client.model;


import java.io.Serializable;

public class DeleteCategoryParams
    extends AbstractParams
    implements Serializable
{
    private static final long serialVersionUID = -7788355284318304492L;

    /**
     * The key of the category to delete.
     */
    public Integer key;

    /**
     * Whether or not to delete any sub-categories. Must be true if category contains sub-categories.
     */
    public boolean recursive = false;

    /**
     * Whether or not to delete any content in the categories. Must be true if category or it's sub-categories contains any content.
     */
    public boolean includeContent = false;
}