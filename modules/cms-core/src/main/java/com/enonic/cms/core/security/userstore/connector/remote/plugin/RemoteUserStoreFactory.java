/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote.plugin;

import java.util.Properties;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RemoteUserStoreFactory
{
    private final static Logger LOG = LoggerFactory.getLogger( RemoteUserStoreFactory.class );

    public RemoteUserStorePlugin create( String type )
    {
        return create( type, null );
    }

    public RemoteUserStorePlugin create( String type, Properties props )
    {
        RemoteUserStorePlugin userStorePlugin = createInstance( type );
        if ( props != null )
        {
            BeanMap bean = new BeanMap( userStorePlugin );
            for ( Object key : props.keySet() )
            {
                String strKey = key.toString();
                String strValue = props.getProperty( strKey );

                // Preventing this property to be set as false if it blank (since it will be confusing for the user)
                if ( "readUserSyncAttributeAsBinary".equals( strKey ) && StringUtils.isBlank( strValue ) )
                {
                    continue;
                }

                try
                {
                    bean.put( strKey, strValue );
                }
                catch ( Exception e )
                {
                    LOG.warn( "Failed to set property [" + strKey + "] with value [" + strValue + "]." );
                }
            }
        }
        return userStorePlugin;
    }

    private RemoteUserStorePlugin createInstance( String type )
    {
        Class<?> clz = getDirectoryClass( type );
        if ( !RemoteUserStorePlugin.class.isAssignableFrom( clz ) )
        {
            throw new IllegalArgumentException( "Class [" + clz + "] is not a valid remote userstore" );
        }

        try
        {
            return (RemoteUserStorePlugin) clz.newInstance();
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Failed to create userstore plugin instance from class [" + clz + "]", e );
        }
    }

    private Class<?> getDirectoryClass( String type )
    {
        try
        {
            return Class.forName( type );
        }
        catch ( Exception e )
        {
            throw new IllegalArgumentException( "Illegal remote userstore [" + type + "]", e );
        }
    }
}