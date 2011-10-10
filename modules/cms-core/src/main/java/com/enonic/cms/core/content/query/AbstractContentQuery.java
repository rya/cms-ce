/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.query;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.index.ContentIndexQuery;
import com.enonic.cms.core.content.index.ContentIndexQuery.CategoryAccessTypeFilterPolicy;
import com.enonic.cms.core.security.user.UserEntity;

public abstract class AbstractContentQuery
{
    private UserEntity user;

    private Collection<ContentTypeKey> contentTypesFilter = null;

    private boolean contentOnline = false;

    private Date onlineAtDate = null;

    private boolean useContentTypeFilter = false;

    private int index = 0;

    private int count = Integer.MAX_VALUE;

    private int levels;

    private Collection<CategoryAccessType> categoryAccessTypeFilter;

    private CategoryAccessTypeFilterPolicy categoryAccessTypeFilterPolicy;

    private String query;

    private String orderBy;

    public UserEntity getUser()
    {
        return user;
    }

    public boolean isUserEnterpriseAdmin()
    {
        return user.isEnterpriseAdmin();
    }

    public void setUser( UserEntity value )
    {
        this.user = value;
    }

    protected boolean hasSecurityFilter()
    {
        return !user.isEnterpriseAdmin();
    }


    public void setFilterContentOnlineAt( Date onlineAt )
    {
        this.contentOnline = true;
        this.onlineAtDate = onlineAt;
    }

    /**
     * Opens up the content filter, so that all content, online or offline will be searched.
     */
    public void setFilterIncludeOfflineContent()
    {
        this.contentOnline = false;
    }

    public boolean isFilterContentOnline()
    {
        return this.contentOnline;
    }

    /**
     * @param contentTypesFilter If this parameter is not <code>null</code>, the results are limited to the content types in this list.
     */
    public void setContentTypeFilter( Collection<ContentTypeKey> contentTypesFilter )
    {
        this.contentTypesFilter = contentTypesFilter;
        if ( contentTypesFilter != null )
        {
            this.useContentTypeFilter = true;
        }
    }

    public Collection<ContentTypeKey> getContentTypeFilter()
    {
        return contentTypesFilter;
    }

    public boolean useContentTypeFilter()
    {
        return useContentTypeFilter;
    }

    protected void checkAndApplyPublishedOnlyFilter( ContentIndexQuery query )
    {

        if ( this.isFilterContentOnline() )
        {
            query.setContentOnlineAtFilter( new Date() );
            query.setContentStatusFilter( ContentStatus.APPROVED.getKey() );
        }
    }

    /**
     * @param index The starting index within the result set, from where to start listing the contents that are returned.
     */
    public void setIndex( int index )
    {
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setOnlineAtDate( Date onlineAtDate )
    {
        this.onlineAtDate = onlineAtDate;
    }

    public Date getOnlineAtDate()
    {
        return this.onlineAtDate;
    }

    /**
     * @param count The maximum number of contents to return in one go.
     */
    public void setCount( int count )
    {
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    public int getLevels()
    {
        return levels;
    }

    /**
     * @param levels The number of levels below each menu item or category, to look for content.
     */
    public void setLevels( int levels )
    {
        this.levels = levels;
    }

    public void setCategoryAccessTypeFilter( Collection<CategoryAccessType> categoryAccessTypeFilter,
                                             CategoryAccessTypeFilterPolicy policy )
    {
        this.categoryAccessTypeFilter = categoryAccessTypeFilter;
        this.categoryAccessTypeFilterPolicy = policy;
    }

    public Collection<CategoryAccessType> getCategoryAccessTypeFilter()
    {
        return categoryAccessTypeFilter;
    }

    public CategoryAccessTypeFilterPolicy getCategoryAccessTypeFilterPolicy()
    {
        return categoryAccessTypeFilterPolicy;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public String getQuery()
    {
        return query;
    }

    public boolean hasQuery()
    {
        return !StringUtils.isBlank( query );
    }

    public void setOrderBy( String orderBy )
    {
        this.orderBy = orderBy;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public void validate()
    {

    }

}
