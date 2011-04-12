/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.util.Map;

import com.enonic.cms.core.security.userstore.connector.config.*;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.domain.AbstractPagedXmlCreator;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigXmlCreator;
import com.enonic.cms.core.security.userstore.connector.config.GroupPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserPolicyConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfigXmlCreator;

public class UserStoreXmlCreator
    extends AbstractPagedXmlCreator
{
    private Map<String, UserStoreConnectorConfig> connectorConfigs;

    public UserStoreXmlCreator( final Map<String, UserStoreConnectorConfig> connectorConfigs )
    {
        this.connectorConfigs = connectorConfigs;
    }

    public Document createUserStoreNotFoundDocument( String userstore )
    {
        Element userStoreEl = new Element( "userstore" );
        userStoreEl.addContent( new Element( "message" ).setText( "Userstore not found: " + userstore ) );
        return new Document( userStoreEl );
    }

    public Document createUserStoresDocument( final UserStoreEntity userStore )
    {
        Element userStoresEl = new Element( getRootName() );
        userStoresEl.addContent( doCreateUserStoreElement( userStore ) );
        return new Document( userStoresEl );
    }

    public Document createUserStoreDocument( final UserStoreEntity userStore )
    {
        return new Document( doCreateUserStoreElement( userStore ) );
    }

    public Element createUserStoreElement( final UserStoreEntity userStore )
    {
        return doCreateUserStoreElement( userStore );
    }

    @Override
    public Element createElement( Object obj )
    {
        return doCreateUserStoreElement( (UserStoreEntity) obj );
    }

    public Element doCreateUserStoreElement( final UserStoreEntity userStore )
    {
        final Element userStoreElem = new Element( "userstore" );
        userStoreElem.setAttribute( "key", String.valueOf( userStore.getKey() ) );
        userStoreElem.setAttribute( "name", userStore.getName() );
        userStoreElem.setAttribute( "default", String.valueOf( userStore.isDefaultUserStore() ) );
        userStoreElem.setAttribute( "remote", String.valueOf( userStore.isRemote() ) );
        if ( userStore.isRemote() )
        {
            userStoreElem.addContent( doCreateConnectorElement( userStore ) );
        }

        userStoreElem.addContent( doCreateConfigElement( userStore ) );

        return userStoreElem;
    }

    private Element doCreateConnectorElement( final UserStoreEntity userStore )
    {
        Element connector = new Element( "connector" );
        connector.setAttribute( "name", userStore.getConnectorName() );
        connector.addContent( doCreateConnectorConfigElement( userStore.getConnectorName() ) );
        return connector;
    }

    private Element doCreateConnectorConfigElement( final String userStoreConnectorConfigName )
    {
        UserStoreConnectorConfig userStoreConnectorConfig = connectorConfigs.get( userStoreConnectorConfigName );

        if ( userStoreConnectorConfig == null )
        {
            userStoreConnectorConfig =
                new UserStoreConnectorConfig( userStoreConnectorConfigName, null, UserPolicyConfig.ALL_FALSE, GroupPolicyConfig.ALL_FALSE );
            //FIXME this adds an error for the CreateUpdateUserStoreWizard, localization not possible
            final String errorMessage = InvalidUserStoreConnectorConfigException.createMessage( userStoreConnectorConfigName,
                                                                                                "No configuration found in cms.properties" );
            userStoreConnectorConfig.addErrorMessage( errorMessage );
        }

        return UserStoreConnectorConfigXmlCreator.createConnectorConfigElement(userStoreConnectorConfig);
    }

    private Element doCreateConfigElement( final UserStoreEntity userStore )
    {
        final Document configAsXmlDocument = userStore.getConfigAsXMLDocument();
        if ( configAsXmlDocument != null )
        {
            return (Element) configAsXmlDocument.getRootElement().detach();
        }
        else
        {
            return UserStoreConfigXmlCreator.createEmptyConfigElement();
        }
    }

    @Override
    public String getRootName()
    {
        return "userstores";
    }

}
