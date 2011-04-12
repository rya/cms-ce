/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import java.io.Serializable;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.core.security.userstore.config.UserStoreConfigParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import com.enonic.cms.framework.util.LazyInitializedJDOMDocument;

import com.enonic.cms.core.security.userstore.config.UserStoreConfigXmlCreator;

public class UserStoreEntity
    implements Serializable
{
    private UserStoreKey key;

    private String name;

    private Integer deleted;

    private Integer defaultStore;

    private String connectorName;

    private LazyInitializedJDOMDocument config;

    private transient Document configAsDocument;

    public UserStoreEntity()
    {
        // Default constructor used by Hibernate.
    }

    public UserStoreEntity( UserStoreEntity source )
    {
        this();

        this.key = source.getKey();
        this.name = source.getName();
        this.deleted = source.getDeleted();
        this.defaultStore = source.getDefaultStore();
        this.connectorName = source.getConnectorName();
        this.config = source.getConfigAsXMLDocument() != null ? LazyInitializedJDOMDocument.parse( source.getConfigAsXMLDocument() ) : null;
    }

    public UserStoreKey getKey()
    {
        return key;
    }

    public void setKey( UserStoreKey key )
    {
        this.key = key;
    }

    public String getName()
    {
        return name;
    }

    public Integer getDeleted()
    {
        return deleted;
    }

    public boolean isDeleted()
    {
        return deleted != null && deleted == 1;
    }

    public Integer getDefaultStore()
    {
        return defaultStore;
    }

    public boolean isDefaultUserStore()
    {
        return defaultStore != null && defaultStore == 1;
    }

    public String getConnectorName()
    {
        return connectorName;
    }

    public UserStoreConfig getConfig()
    {
        if ( this.config == null )
        {
            return new UserStoreConfig();
        }
        return UserStoreConfigParser.parse(this.config.getDocument().getRootElement(), true);
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted ? 1 : 0;
    }

    public void setDefaultStore( boolean defaultStore )
    {
        this.defaultStore = defaultStore ? 1 : 0;
    }

    public void setConnectorName( final String value )
    {
        connectorName = value;
    }

    public void setConfig( final UserStoreConfig value )
    {
        config = LazyInitializedJDOMDocument.parse( UserStoreConfigXmlCreator.createDocument( value ) );

        // Invalidate cache 
        configAsDocument = null;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof UserStoreEntity ) )
        {
            return false;
        }

        UserStoreEntity that = (UserStoreEntity) o;

        if ( !key.equals( that.getKey() ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 843, 989 ).append( key ).toHashCode();
    }


    public boolean isLocal()
    {
        return StringUtils.isEmpty( connectorName );
    }

    public boolean isRemote()
    {
        return !isLocal();
    }

    public Document getConfigAsXMLDocument()
    {
        if ( config == null )
        {
            return null;
        }

        if ( configAsDocument == null )
        {
            configAsDocument = config.getDocument();
        }

        return (Document) configAsDocument.clone();
    }
}
