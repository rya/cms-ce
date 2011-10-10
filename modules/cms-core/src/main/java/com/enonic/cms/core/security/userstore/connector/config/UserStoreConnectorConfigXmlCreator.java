/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.config;

import java.util.Collection;

import org.jdom.Document;
import org.jdom.Element;

/**
 * Aug 21, 2009
 */
public class UserStoreConnectorConfigXmlCreator
{
    public static Document createUserStoreConnectorConfigsDocument( Collection<UserStoreConnectorConfig> configs )
    {
        final Element rootEl = new Element( "userstore-connector-configs" );
        for ( final UserStoreConnectorConfig config : configs )
        {
            rootEl.addContent( doCreateConnectorConfigElement( config ) );
        }
        return new Document( rootEl );
    }

    public static Element createConnectorConfigElement( UserStoreConnectorConfig config )
    {
        return doCreateConnectorConfigElement( config );
    }

    private static Element doCreateConnectorConfigElement( UserStoreConnectorConfig config )
    {
        Element configEl = new Element( "config" );
        configEl.setAttribute( "name", config.getName() );
        configEl.setAttribute( "groups-stored-local", String.valueOf( config.groupsStoredLocal() ) );
        configEl.setAttribute( "groups-stored-remote", String.valueOf( config.groupsStoredRemote() ) );
        if ( config.getPluginType() != null )
        {
            configEl.setAttribute( "plugin-type", config.getPluginType() );
        }
        configEl.addContent( doCreateUserPolicyElement( config ) );
        configEl.addContent( doCreateGroupPolicyElement( config ) );
        configEl.addContent( doCreateErrorsElement( config ) );
        return configEl;
    }

    private static Element doCreateGroupPolicyElement( UserStoreConnectorConfig config )
    {
        Element groupPolicyEl = new Element( "group-policy" );
        groupPolicyEl.setAttribute( "can-read", String.valueOf( config.canReadGroup() ) );
        groupPolicyEl.setAttribute( "can-create", String.valueOf( config.canCreateGroup() ) );
        groupPolicyEl.setAttribute( "can-update", String.valueOf( config.canUpdateGroup() ) );
        groupPolicyEl.setAttribute( "can-delete", String.valueOf( config.canDeleteGroup() ) );
        return groupPolicyEl;
    }

    private static Element doCreateUserPolicyElement( UserStoreConnectorConfig config )
    {
        Element userPolicyEl = new Element( "user-policy" );
        userPolicyEl.setAttribute( "can-create", String.valueOf( config.canCreateUser() ) );
        userPolicyEl.setAttribute( "can-update", String.valueOf( config.canUpdateUser() ) );
        userPolicyEl.setAttribute( "can-delete", String.valueOf( config.canDeleteUser() ) );
        return userPolicyEl;
    }

    private static Element doCreateErrorsElement( UserStoreConnectorConfig config )
    {
        Element errorsEl = new Element( "errors" );
        for ( String errorMessage : config.getErrorMessages() )
        {
            Element errorEl = new Element( "error" );
            errorEl.setText( errorMessage );
            errorsEl.addContent( errorEl );
        }
        return errorsEl;
    }
}
