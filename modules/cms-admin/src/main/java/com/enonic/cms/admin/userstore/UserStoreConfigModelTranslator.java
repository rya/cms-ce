package com.enonic.cms.admin.userstore;

import java.util.List;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreUserFieldConfig;


public final class UserStoreConfigModelTranslator
{

    public static UserStoreConfigModel toModelWithFields( final UserStoreEntity entity )
    {
        final UserStoreConfigModel model = new UserStoreConfigModel();
        if ( entity != null )
        {
            model.setKey( entity.getKey().toString() );
            model.setName( entity.getName() );
            model.setDefaultStore( entity.getDefaultStore() );
            model.setConnectorName( entity.getConnectorName() );
            model.setDeleted( entity.getDeleted() != null && entity.getDeleted() > 0 );

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
            model.setDefaultStore( entity.getDefaultStore() );
            model.setConnectorName( entity.getConnectorName() );
            model.setDeleted( entity.getDeleted() != null && entity.getDeleted() > 0 );
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
}
