/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public abstract class AbstractDataEntry
    implements DataEntry
{

    private String name;

    private DataEntryConfig config;

    private DataEntryType type;

    private String xpath;

    protected AbstractDataEntry( DataEntryConfig config, DataEntryType type )
    {
        if ( type == null )
        {
            throw new IllegalArgumentException( "Given type cannot be null" );
        }
        if ( config == null )
        {
            throw new IllegalArgumentException( "Given config cannot be null" );
        }
        if ( config.getType() == null )
        {
            throw new IllegalArgumentException( "Given config type cannot be null" );
        }
        if ( config.getType().getName() == null )
        {
            throw new IllegalArgumentException( "Given config type name cannot be null" );
        }

        this.name = config.getName();
        this.config = config;
        this.type = type;
        this.xpath = config.getRelativeXPath();
    }

    public abstract void validate();

    public String getName()
    {
        return config.getName();
    }

    public DataEntryConfig getConfig()
    {
        return config;
    }

    public DataEntryType getType()
    {
        return type;
    }

    public String getXPath()
    {
        return xpath;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractDataEntry ) )
        {
            return false;
        }

        AbstractDataEntry that = (AbstractDataEntry) o;

        if ( !name.equals( that.name ) )
        {
            return false;
        }
        if ( !type.equals( that.type ) )
        {
            return false;
        }
        if ( xpath != null ? !xpath.equals( that.xpath ) : that.xpath != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 447, 657 ).append( name ).append( type ).append( xpath ).toHashCode();
    }
}
