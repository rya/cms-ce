/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security;

import java.util.Date;

/**
 * Jul 10, 2009
 */
public class RememberedLoginEntity
{
    private RememberedLoginKey key;

    private String guid;

    private Date createdAt;

    public RememberedLoginKey getKey()
    {
        return key;
    }

    public void setKey( RememberedLoginKey key )
    {
        this.key = key;
    }

    public String getGuid()
    {
        return guid;
    }

    public void setGuid( String guid )
    {
        this.guid = guid;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Date createdAt )
    {
        this.createdAt = createdAt;
    }
}
