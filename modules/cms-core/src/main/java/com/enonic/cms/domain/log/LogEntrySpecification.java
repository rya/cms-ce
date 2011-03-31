/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.log;

import java.util.Date;

import com.enonic.cms.domain.security.user.UserEntity;

/**
 * This class represents the specification of a getContentByCategory search.
 */
public class LogEntrySpecification
{

    private UserEntity user;

    private LogType[] types;

    private Table[] tableTypes;

    private boolean allowDuplicateEntries = false;

    private Date dateFilter;

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public LogType[] getTypes()
    {
        return types;
    }

    public void setTypes( LogType[] types )
    {
        this.types = types;
    }

    public boolean isAllowDuplicateEntries()
    {
        return allowDuplicateEntries;
    }

    public void setAllowDuplicateEntries( boolean allowDuplicateEntries )
    {
        this.allowDuplicateEntries = allowDuplicateEntries;
    }

    public Date getDateFilter()
    {
        return dateFilter;
    }

    public void setDateFilter( Date dateFilter )
    {
        this.dateFilter = dateFilter;
    }

    public Table[] getTableTypes()
    {
        return tableTypes;
    }

    public void setTableTypes( Table[] tableTypes )
    {
        this.tableTypes = tableTypes;
    }


}