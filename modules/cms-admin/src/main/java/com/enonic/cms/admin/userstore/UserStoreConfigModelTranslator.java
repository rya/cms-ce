package com.enonic.cms.admin.userstore;

import java.util.List;
import java.util.Map;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;
import com.enonic.cms.core.security.userstore.connector.config.UserStoreConnectorConfig;


public final class UserStoreConfigModelTranslator
{

    public static UserStoreConfigModel toModelWithFields( final UserStoreEntity entity )
    {
        final UserStoreConfigModel model = new UserStoreConfigModel();
        if ( entity != null )
        {
            model.setKey( entity.getKey().toString() );
            model.setName( entity.getName() );
            model.setDefaultStore( entity.getDefaultStore() > 0 );
            model.setConnectorName( entity.getConnectorName() );

            UserStoreConfig config = entity.getConfig();
            if ( config != null )
            {
                for ( UserStoreUserFieldConfig fieldConfig : config.getUserFieldConfigs() )
                {
                    model.addUserField( new UserStoreConfigFieldModel( fieldConfig ) );
                }
            }

        }
        return model;
    }

    public static UserStoreConfigModel toModelWithXML( final UserStoreEntity entity )
    {
        final UserStoreConfigModel model = new UserStoreConfigModel();
        if ( entity != null )
        {
            model.setKey( entity.getKey().toString() );
            model.setName( entity.getName() );
            model.setDefaultStore( entity.getDefaultStore() > 0 );
            model.setConnectorName( entity.getConnectorName() );
            model.setConfigXML( JDOMUtil.serialize( entity.getConfigAsXMLDocument(), 2, true ) );
        }
        return model;
    }

    public static UserStoreConfigsModel toModel( final List<UserStoreEntity> list )
    {
        final UserStoreConfigsModel model = new UserStoreConfigsModel();
        model.setTotal( list.size() );

        for ( final UserStoreEntity entity : list )
        {
            model.addUserStoreConfig( toModelWithFields( entity ) );
        }

        return model;
    }

    public static UserStoreConnectorsModel toModel( Map<String, UserStoreConnectorConfig> map )
    {
        final UserStoreConnectorsModel model = new UserStoreConnectorsModel();
        model.setTotal( map.size() );

        for ( final UserStoreConnectorConfig entity : map.values() )
        {
            model.addUserStoreConnector( toModel( entity ) );
        }

        return model;
    }

    public static UserStoreConnectorModel toModel( final UserStoreConnectorConfig entity )
    {
        final UserStoreConnectorModel model = new UserStoreConnectorModel();
        if ( entity != null ) {
            model.setName( entity.getName() );
            model.setPluginType( entity.getPluginType() );
            model.setCanCreateUser( entity.canCreateUser() );
            model.setCanUpdateUser( entity.canUpdateUser() );
            model.setCanUpdateUserPassword( entity.canUpdateUserPassword() );
            model.setCanDeleteUser( entity.canDeleteUser() );
            model.setCanCreateGroup( entity.canCreateGroup() );
            model.setCanUpdateGroup( entity.canUpdateGroup() );
            model.setCanReadGroup( entity.canReadGroup() );
            model.setCanDeleteGroup( entity.canDeleteGroup() );
            model.setGroupsLocal( entity.groupsStoredLocal() );
        }
        return model;
    }
}
