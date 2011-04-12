/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;

public class UpdateUserStoreCommand
{
    private UserStoreKey key;

    private String name;

    private boolean deleted;

    private boolean newDefaultStore = false;

    private String connectorName;

    private UserKey updater;

    private UserStoreConfig userStoreConfig;

    public UserStoreKey getKey()
    {
        return key;
    }

    public void setKey( final UserStoreKey value )
    {
        key = value;
    }

    public String getName()
    {
        return name;
    }

    public void setName( final String value )
    {
        name = value;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( final boolean value )
    {
        deleted = value;
    }

    public boolean getAsNewDefaultStore()
    {
        return newDefaultStore;
    }

    public void setAsNewDefaultStore()
    {
        this.newDefaultStore = true;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( final String value )
    {
        connectorName = value;
    }

    public UserKey getUpdater()
    {
        return updater;
    }

    public void setUpdater( final UserKey value )
    {
        updater = value;
    }

    public UserStoreConfig getConfig()
    {
        return userStoreConfig;
    }

    public void setConfig( final UserStoreConfig value )
    {
        userStoreConfig = value;
    }
}
