/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.saxon.om.NamespaceResolver;

public class CtyImportConfig
    implements CtyImportMappingContainer
{
    final private CtyFormConfig form;

    final private String name;

    final private String sync;

    final private NamespaceResolver namespaceResolver;

    private CtyImportModeConfig mode = CtyImportModeConfig.XML;

    private String separator = ";";

    private Integer skip = 0;

    private String base = null;

    private CtyImportStatusConfig status = CtyImportStatusConfig.DRAFT;

    private CtyImportPurgeConfig purge = CtyImportPurgeConfig.NONE;

    private List<CtyImportMappingConfig> mappings = new ArrayList<CtyImportMappingConfig>();

    private List<CtyImportMappingConfig> metadataMappings = new ArrayList<CtyImportMappingConfig>();

    private List<CtyImportBlockConfig> blocks = new ArrayList<CtyImportBlockConfig>();

    private CtyImportUpdateStrategyConfig updateStrategy = CtyImportUpdateStrategyConfig.UPDATE_CONTENT_KEEP_STATUS;

    public CtyImportConfig( final CtyFormConfig form, final String name, final String sync, final NamespaceResolver namespaceResolver )
    {
        this.form = form;
        this.name = name;
        this.sync = sync;
        this.namespaceResolver = namespaceResolver;
    }

    public CtyImportConfig getImportConfig()
    {
        return this;
    }

    public void setMode( final CtyImportModeConfig mode )
    {
        this.mode = mode;
    }

    public void setSeparator( final String separator )
    {
        if ( separator != null && separator.length() > 1 )
        {
            throw new InvalidImportConfigException( this.getName(), "Separator can only be one character long, was: '" + separator + "'" );
        }
        this.separator = separator;
    }

    public void setSkip( final Integer skip )
    {
        this.skip = skip;
    }

    public void setBase( final String base )
    {
        this.base = base;
    }

    public void setStatus( final CtyImportStatusConfig status )
    {
        this.status = status;
    }

    public void setPurge( final CtyImportPurgeConfig purge )
    {
        this.purge = purge;
    }

    public boolean addMapping( final CtyImportMappingConfig mapping )
    {
        if ( mapping.isMetaDataMapping() )
        {
            return doAddMapping( metadataMappings, mapping );
        }
        else
        {
            return doAddMapping( mappings, mapping );
        }
    }

    public void addBlock( final CtyImportBlockConfig block )
    {
        blocks.add( block );
    }

    private boolean doAddMapping( final List<CtyImportMappingConfig> mappingList, final CtyImportMappingConfig mapping )
    {
        if ( mappingList.contains( mapping ) )
        {
            return false;
        }
        mappingList.add( mapping );
        return true;
    }

    public CtyFormConfig getForm()
    {
        return form;
    }

    public String getName()
    {
        return name;
    }

    public String getSync()
    {
        return sync;
    }

    public boolean isSyncEnabled()
    {
        return sync != null;
    }

    public NamespaceResolver getNamespaceResolver()
    {
        return namespaceResolver;
    }

    public CtyImportModeConfig getMode()
    {
        return mode;
    }

    public String getSeparator()
    {
        if ( mode == CtyImportModeConfig.CSV )
        {
            return separator;
        }
        return null;
    }

    public Integer getSkip()
    {
        if ( mode == CtyImportModeConfig.CSV )
        {
            return skip;
        }
        return null;
    }

    public String getBase()
    {
        if ( mode == CtyImportModeConfig.XML )
        {
            return base;
        }
        return null;
    }

    public CtyImportStatusConfig getStatus()
    {
        return status;
    }

    public CtyImportPurgeConfig getPurge()
    {
        return purge;
    }

    public List<CtyImportMappingConfig> getMappings()
    {
        return mappings;
    }

    public List<CtyImportMappingConfig> getMetadataMappings()
    {
        return metadataMappings;
    }

    public CtyImportMappingConfig getSyncMapping()
    {
        if ( isSyncMappedToContentKey() )
        {
            return findMappingByDestination( metadataMappings, sync );
        }
        else
        {
            return findMappingByDestination( mappings, sync );
        }
    }

    private CtyImportMappingConfig findMappingByDestination( final Collection<CtyImportMappingConfig> mappings, final String destination )
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

    public Iterable<CtyImportBlockConfig> getBlocks()
    {
        return blocks;
    }

    public boolean isSyncMappedToContentKey()
    {
        return "@key".equals( sync );
    }

    public void setUpdateStrategy( CtyImportUpdateStrategyConfig value )
    {
        this.updateStrategy = value;
    }

    public CtyImportUpdateStrategyConfig getUpdateStrategy()
    {
        return updateStrategy;
    }
}
