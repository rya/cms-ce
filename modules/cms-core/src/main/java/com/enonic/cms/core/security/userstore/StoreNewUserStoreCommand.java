/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;


public class StoreNewUserStoreCommand
{
    private String name;

    private Boolean defaultStore;

    private String connectorName;

    private UserKey storer;

    private UserStoreConfig userStoreConfig;

    public String getName()
    {
        return name;
    }

    public void setName( final String value )
    {
        name = value;
    }

    public Boolean isDefaultStore()
    {
        return defaultStore;
    }

    public void setDefaultStore( final Boolean value )
    {
        defaultStore = value;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( final String value )
    {
        connectorName = value;
    }

    public UserKey getStorer()
    {
        return storer;
    }

    public void setStorer( final UserKey value )
    {
        this.storer = value;
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
