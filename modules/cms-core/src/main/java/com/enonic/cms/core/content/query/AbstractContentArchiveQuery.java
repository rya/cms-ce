/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQuery.CategoryAccessTypeFilterPolicy;
import com.enonic.cms.core.security.group.GroupKey;

public abstract class AbstractContentArchiveQuery
    extends AbstractContentQuery
{
    private Collection<ContentKey> contentKeyFilter;

    private boolean useContentKeyFilter;

    private Collection<CategoryKey> categoryKeyFilter;

    private boolean useCategoryKeyFilter;

    private boolean adminBrowseOnly;

    private Collection<CategoryAccessType> categoryAccessTypeFilter;

    private CategoryAccessTypeFilterPolicy categoryAccessTypeFilterPolicy;

    public Collection<ContentKey> getContentKeyFilter()
    {
        return contentKeyFilter;
    }

    public void setContentKeyFilter( Collection<ContentKey> value )
    {
        this.contentKeyFilter = value;
        useContentKeyFilter = contentKeyFilter != null;
    }

    public boolean hasContentFilter()
    {
        return contentKeyFilter != null;
    }

    public Collection<CategoryKey> getCategoryKeyFilter()
    {
        return categoryKeyFilter;
    }

    public void setCategoryKeyFilter( final Collection<CategoryKey> categories, final int numLevels )
    {
        this.categoryKeyFilter = categories;
        this.setLevels( numLevels );
        useCategoryKeyFilter = categoryKeyFilter != null;
    }

    public boolean hasCategoryFilter()
    {
        return categoryKeyFilter != null;
    }

    public void setFilterAdminBrowseOnly( boolean value )
    {
        this.adminBrowseOnly = value;
    }

    public boolean isFilterAdminBrowseOnly()
    {
        return this.adminBrowseOnly;
    }

    public boolean useContentKeyFilter()
    {
        return useContentKeyFilter;
    }

    public boolean useCategoryKeyFilter()
    {
        return useCategoryKeyFilter;
    }

    public ContentIndexQuery createAndSetupContentQuery( Set<CategoryKey> categoryKeys, Collection<GroupKey> securityFilter )
    {

        ContentIndexQuery query = new ContentIndexQuery( this.getQuery(), this.getOrderBy() );

        if ( this.useContentTypeFilter() )
        {
            query.setContentTypeFilter( this.getContentTypeFilter() );
        }

        if ( this.useContentKeyFilter() )
        {
            query.setContentFilter( this.getContentKeyFilter() );
        }

        query.setIndex( this.getIndex() );
        query.setCount( this.getCount() );
        query.setCategoryFilter( categoryKeys );
        query.setSecurityFilter( securityFilter );
        if ( categoryAccessTypeFilter != null )
        {
            query.setCategoryAccessTypeFilter( categoryAccessTypeFilter, categoryAccessTypeFilterPolicy );
        }

        checkAndApplyPublishedOnlyFilter( query );

        if ( this.isFilterAdminBrowseOnly() && hasSecurityFilter() )
        {
            Collection<CategoryAccessType> catAccessFilter = new ArrayList<CategoryAccessType>();
            catAccessFilter.add( CategoryAccessType.ADMIN_BROWSE );
            query.setCategoryAccessTypeFilter( catAccessFilter, CategoryAccessTypeFilterPolicy.AND );
        }

        return query;
    }

    public void setCategoryAccessTypeFilter( Collection<CategoryAccessType> categoryAccessTypeFilter,
                                             CategoryAccessTypeFilterPolicy policy )
    {
        this.categoryAccessTypeFilter = categoryAccessTypeFilter;
        this.categoryAccessTypeFilterPolicy = policy;
    }


}