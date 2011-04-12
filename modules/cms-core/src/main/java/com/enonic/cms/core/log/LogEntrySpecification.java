/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import com.enonic.cms.core.security.user.UserEntity;

import java.util.Date;

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

    public boolean isAllowDuplicateEntries()
    {
        return allowDuplicateEntries;
    }

    public Date getDateFilter()
    {
        return dateFilter;
    }

    public Table[] getTableTypes()
    {
        return tableTypes;
    }


}