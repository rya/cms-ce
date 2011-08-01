package com.enonic.cms.admin.userstore;

import java.util.ArrayList;
import java.util.List;


public final class UserStoreConfigModel
{

    private String key;

    private String name;

    private boolean defaultStore;

    private String connectorName;

    private String configXML;

    private List<UserStoreConfigFieldModel> userFields;


    public UserStoreConfigModel()
    {
        userFields = new ArrayList<UserStoreConfigFieldModel>();
    }


    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public boolean getDefaultStore()
    {
        return defaultStore;
    }

    public void setDefaultStore( boolean defaultStore )
    {
        this.defaultStore = defaultStore;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public void setConnectorName( String connectorName )
    {
        this.connectorName = connectorName;
    }

    public String getConfigXML()
    {
        return configXML;
    }

    public void setConfigXML( String configXML )
    {
        this.configXML = configXML;
    }

    public List<UserStoreConfigFieldModel> getUserFields()
    {
        return userFields;
    }

    public void setUserFields( List<UserStoreConfigFieldModel> userFields )
    {
        this.userFields = userFields;
    }

    public void addUserField( UserStoreConfigFieldModel userField )
    {
        this.userFields.add( userField );
    }

}
