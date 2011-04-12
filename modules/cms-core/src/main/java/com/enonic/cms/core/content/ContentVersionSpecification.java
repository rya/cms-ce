/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;

import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.index.ContentIndexQuery.CategoryAccessTypeFilterPolicy;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

/**
 * This class represents the specification of a getContentByCategory search.
 */
public class ContentVersionSpecification
{

    private UserKey modifier;

    private Integer contentStatus;

    private Collection<CategoryAccessType> categoryAccessTypeFilter;

    private CategoryAccessTypeFilterPolicy categoryAccessTypeFilterPolicy;

    private UserEntity user;

    private Collection<GroupKey> securityFilter;

    public void setModifier( UserKey key )
    {
        this.modifier = key;
    }

    public void setStatus( int status )
    {
        this.contentStatus = status;
    }

    public Integer getContentStatus()
    {
        return contentStatus;
    }

    public void setContentStatus( Integer contentStatus )
    {
        this.contentStatus = contentStatus;
    }

    public UserKey getModifier()
    {
        return modifier;
    }

    public void setCategoryAccessTypeFilter( final Collection<CategoryAccessType> categoryAccessTypeFilter,
                                             final CategoryAccessTypeFilterPolicy policy )
    {
        if ( policy == null )
        {
            throw new IllegalArgumentException( "NULL policy not allowed" );
        }
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

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public void setSecurityFilter( Collection<GroupKey> filter )
    {
        securityFilter = filter;
    }

    public Collection<GroupKey> getSecurityFilter()
    {
        return securityFilter;
    }
}