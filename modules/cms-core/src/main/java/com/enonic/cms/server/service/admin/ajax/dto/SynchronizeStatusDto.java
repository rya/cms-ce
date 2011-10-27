/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax.dto;

import org.directwebremoting.annotations.DataTransferObject;

import java.util.Date;

@DataTransferObject
public final class SynchronizeStatusDto
{
    private final String userStoreKey;

    private String type;

    private boolean completed;

    private String message;

    private Date startedDate;

    private Date finishedDate;

    public SynchronizeStatusDto( final String userStoreKey )
    {
        this.userStoreKey = userStoreKey;
        this.completed = true;
    }

    public String getUserStoreKey()
    {
        return userStoreKey;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public Date getStartedDate()
    {
        return startedDate;
    }

    public void setStartedDate( final Date value )
    {
        startedDate = value;
    }

    public Date getFinishedDate()
    {
        return finishedDate;
    }

    public void setFinishedDate( final Date value )
    {
        finishedDate = value;
    }
}
