/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class CtyImportBlockConfig
    implements CtyImportMappingContainer
{
    private final CtyImportConfig importConfig;

    /**
     * The XPATH to the XML elements that represents this block.
     */
    private String base;

    private final String destination;

    private final String sync;

    private final boolean purge;

    private List<CtyImportMappingConfig> mappings = new ArrayList<CtyImportMappingConfig>();

    public CtyImportBlockConfig( final CtyImportConfig importConfig, final String base, final String destination, final String sync,
                                 final boolean purge )
    {
        this.importConfig = importConfig;
        this.base = base;
        this.destination = destination;
        this.sync = sync;
        this.purge = purge;
    }

    public boolean addMapping( final CtyImportMappingConfig mapping )
    {
        if ( mappings.contains( mapping ) )
        {
            return false;
        }
        mappings.add( mapping );
        return true;
    }

    public String getName()
    {
        return importConfig.getName();
    }

    public CtyImportConfig getImportConfig()
    {
        return importConfig;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getBase()
    {
        return base;
    }

    public String getSync()
    {
        return sync;
    }

    public boolean purgeRemainingEntries()
    {
        return purge;
    }

    public boolean hasSyncSetting()
    {
        return sync != null;
    }

    public List<CtyImportMappingConfig> getMappings()
    {
        return mappings;
    }

    public CtyImportMappingConfig getSyncMapping()
    {
        return getMapping( sync );
    }

    private CtyImportMappingConfig getMapping( final String destination )
    {
        for ( final CtyImportMappingConfig mapping : mappings )
        {
            if ( mapping.getDestination().equals( destination ) )
            {
                return mapping;
            }
        }
        return null;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        CtyImportBlockConfig that = (CtyImportBlockConfig) o;

        if ( destination != null ? !destination.equals( that.destination ) : that.destination != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 913, 139 ).append( destination ).toHashCode();
    }
}
