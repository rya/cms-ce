/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.HashMap;
import java.util.Map;

import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import com.enonic.cms.core.content.contenttype.CtyImportBlockConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;

public class ImportDataEntry
{
    private CtyImportMappingConfig syncMapping;

    private String syncValue;

    private final Map<CtyImportMappingConfig, AbstractSourceValue> configAndValue =
        new HashMap<CtyImportMappingConfig, AbstractSourceValue>();

    private final Map<CtyImportMappingConfig, AbstractSourceValue> configAndMetadataValue =
        new HashMap<CtyImportMappingConfig, AbstractSourceValue>();

    private final Multimap<CtyImportBlockConfig, ImportDataEntry> blockConfigAndValue = LinkedHashMultimap.create();

    public ImportDataEntry( CtyImportMappingConfig syncMapping )
    {
        this.syncMapping = syncMapping;
    }

    public void add( final CtyImportMappingConfig mapping, final AbstractSourceValue value )
    {
        configAndValue.put( mapping, value );

        if ( mapping.equals( syncMapping ) )
        {
            setSyncValue( value );
        }
    }

    public void addMetadata( final CtyImportMappingConfig mapping, final AbstractSourceValue value )
    {
        configAndMetadataValue.put( mapping, value );

        if ( mapping.equals( syncMapping ) )
        {
            setSyncValue( value );
        }
    }

    public void addBlock( final CtyImportBlockConfig block, final ImportDataEntry entry )
    {
        blockConfigAndValue.put( block, entry );
    }

    public String getSyncValue()
    {
        return syncValue;
    }

    public Map<CtyImportMappingConfig, AbstractSourceValue> getConfigAndValueMap()
    {
        return configAndValue;
    }

    public Map<CtyImportMappingConfig, AbstractSourceValue> getConfigAndMetadataValueMap()
    {
        return configAndMetadataValue;
    }

    public Multimap<CtyImportBlockConfig, ImportDataEntry> getBlockConfigAndValue()
    {
        return blockConfigAndValue;
    }

    private void setSyncValue( AbstractSourceValue value )
    {
        if ( value instanceof StringSourceValue )
        {
            this.syncValue = ( (StringSourceValue) value ).getValue();
        }
        else
        {
            throw new ImportException( "Invalid source value type. Expected: " + StringSourceValue.class.getSimpleName() + ", Was: " +
                value.getClass().getSimpleName() );
        }
    }
}
