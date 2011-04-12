/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.Collection;

import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreKey;

public class GroupQuery
{

    private UserStoreKey userStoreKey = null;

    private boolean globalOnly = false;

    private String query = "";

    private String orderBy = "";

    private Integer index = 0;

    private Integer count = null;

    private Collection<GroupType> groupTypes;

    private boolean orderAscending = true;

    private boolean includeBuiltInGroups = true;

    private boolean includeDeleted = false;

    private boolean includeUserGroups = false;

    private boolean includeAnonymousGroups = false;

    public UserStoreKey getUserStoreKey()
    {
        return userStoreKey;
    }

    public void setUserStoreKey( UserStoreKey userStoreKey )
    {
        this.userStoreKey = userStoreKey;
    }

    public boolean isGlobalOnly()
    {
        return globalOnly;
    }

    public void setGlobalOnly( boolean globalOnly )
    {
        this.globalOnly = globalOnly;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public String getOrderBy()
    {
        return orderBy;
    }

    public void setOrderBy( String orderBy )
    {
        this.orderBy = orderBy;
    }

    public Integer getIndex()
    {
        return index;
    }

    public void setIndex( Integer index )
    {
        this.index = index;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount( Integer count )
    {
        this.count = count;
    }

    public Collection<GroupType> getGroupTypes()
    {
        return groupTypes;
    }

    public void setGroupTypes( Collection<GroupType> groupTypes )
    {
        this.groupTypes = groupTypes;
    }

    public boolean isOrderAscending()
    {
        return orderAscending;
    }

    public void setOrderAscending( boolean orderAscending )
    {
        this.orderAscending = orderAscending;
    }

    public boolean isIncludeBuiltInGroups()
    {
        return includeBuiltInGroups;
    }

    public void setIncludeBuiltInGroups( boolean includeBuiltInGroups )
    {
        this.includeBuiltInGroups = includeBuiltInGroups;
    }

    public boolean isIncludeDeleted()
    {
        return includeDeleted;
    }

    public void setIncludeDeleted( boolean includeDeleted )
    {
        this.includeDeleted = includeDeleted;
    }

    public boolean isIncludeUserGroups()
    {
        return includeUserGroups;
    }

    public void setIncludeUserGroups( boolean includeUserGroups )
    {
        this.includeUserGroups = includeUserGroups;
    }

    public boolean isIncludeAnonymousGroups()
    {
        return includeAnonymousGroups;
    }

    public void setIncludeAnonymousGroups( boolean inc )
    {
        includeAnonymousGroups = inc;
    }

    public void validate()
        throws IllegalArgumentException
    {
    }
}
