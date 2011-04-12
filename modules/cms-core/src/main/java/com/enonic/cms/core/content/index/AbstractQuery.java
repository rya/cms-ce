/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.Collection;

import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.group.GroupKey;

/**
 * This class implements the abstract query.
 */
public abstract class AbstractQuery
{
    /**
     * Category key filter.
     */
    private Collection<CategoryKey> categoryFilter;

    /**
     * Content type key filter.
     */
    private Collection<ContentTypeKey> contentTypeFilter;

    /**
     * Security filter.
     */
    private Collection<GroupKey> securityFilter;

    public Collection<ContentTypeKey> getContentTypeFilter()
    {
        return contentTypeFilter;
    }

    /**
     * @param filter The content types the returned contents must belong to.
     */
    public void setContentTypeFilter( Collection<ContentTypeKey> filter )
    {
        contentTypeFilter = filter;
    }

    public Collection<CategoryKey> getCategoryFilter()
    {
        return this.categoryFilter;
    }

    public int getCategoryFilterSize()
    {
        if ( this.categoryFilter == null )
        {
            return 0;
        }
        return this.categoryFilter.size();
    }

    /**
     * @param categoryFilter The categories the returned contents must belong to.
     */
    public void setCategoryFilter( Collection<CategoryKey> categoryFilter )
    {
        this.categoryFilter = categoryFilter;
    }

    public Collection<GroupKey> getSecurityFilter()
    {
        return securityFilter;
    }

    public boolean hasSecurityFilter()
    {
        return securityFilter != null && securityFilter.size() > 0;
    }

    /**
     * @param filter All the groups the currently logged in user belongs to, or <code>null</code> if the currently logged in user is the
     *               Enterprise Admin.
     */
    public void setSecurityFilter( Collection<GroupKey> filter )
    {
        securityFilter = filter;
    }
}
