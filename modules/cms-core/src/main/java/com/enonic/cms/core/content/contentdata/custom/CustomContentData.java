/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class CustomContentData
    extends AbstractDataEntrySet
    implements DataEntrySet, ContentData
{
    private String titleInputName;

    public CustomContentData( ContentTypeConfig config )
    {
        super( "contentdata", null, "contentdata" );

        if ( config == null )
        {
            throw new IllegalArgumentException( "Given config cannot be null" );
        }

        setConfig( config );

        this.titleInputName = config.getForm().getTitleInputName();
    }

    public String getTitleInputName()
    {
        return titleInputName;
    }

    public DataEntry getTitleDataEntry()
    {
        if ( titleInputName == null )
        {
            return null;
        }
        return entryMap.get( titleInputName );
    }

    public List<DataEntry> getNonGroupDataEntries()
    {
        final List<DataEntry> nonGroupEntries = new ArrayList<DataEntry>();
        for ( DataEntry dataEntry : getEntries() )
        {
            if ( !( dataEntry instanceof GroupDataEntry ) )
            {
                nonGroupEntries.add( dataEntry );
            }
        }

        return nonGroupEntries;
    }

    public BlockGroupDataEntries getBlockGroupDataEntries( String blockName )
    {
        BlockGroupDataEntries blockGroupDataEntries = new BlockGroupDataEntries( blockName );
        for ( GroupDataEntry groupDataEntry : getGroupDataSets( blockName ) )
        {
            blockGroupDataEntries.add( groupDataEntry );
        }
        return blockGroupDataEntries;
    }

    public void validate()
    {
        // validate required data
        validateRequiredDataEntries();

        // Validate order of entries in group-data-entries
        validateOrderIndexOfGroupEntries();

        // Validate each data entry
        for ( DataEntry dataEntry : entries )
        {
            dataEntry.validate();
        }
    }

    private void validateRequiredDataEntries()
    {
        for ( DataEntryConfig dataEntryConfig : getContentTypeConfig().getForm().getInputConfigs() )
        {
            DataEntry dataEntry = getEntry( dataEntryConfig.getName() );

            if ( dataEntryConfig.isRequired() )
            {
                validateRequiredDataEntry( dataEntryConfig, dataEntry );
            }
        }
    }

    private void validateOrderIndexOfGroupEntries()
    {
        for ( DataEntry dataEntry : entries )
        {
            if ( dataEntry instanceof GroupDataEntry )
            {
                final GroupDataEntry groupDataEntry = (GroupDataEntry) dataEntry;

                int expectedGroupDataEntryPosition = resolveActualPositionOfGroupDataEntry( groupDataEntry );

                if ( expectedGroupDataEntryPosition != groupDataEntry.getGroupIndex() )
                {
                    throw new InvalidContentDataException(
                        "Unexpected position of group data entry for group '" + groupDataEntry.getName() + "', got position " +
                            groupDataEntry.getGroupIndex() + ", expected position " + expectedGroupDataEntryPosition );
                }
            }
        }
    }

    /**
     * Resolve given groupDataEntry's position among it's brothers and sisters (those with same name).
     */
    private int resolveActualPositionOfGroupDataEntry( GroupDataEntry groupDataEntry )
    {
        int position = 0;

        final List<GroupDataEntry> groupDataEntryWithSameName = getGroupDataSets( groupDataEntry.getName() );

        for ( GroupDataEntry currGroupDataEntry : groupDataEntryWithSameName )
        {
            position++;

            if ( currGroupDataEntry == groupDataEntry )
            {
                return position;
            }
        }
        // indicates not found (should never happen with current usage of this method)
        return -1;
    }

    public String getTitle()
    {
        TextDataEntry titleDataEntry = (TextDataEntry) getTitleDataEntry();
        if ( titleDataEntry == null )
        {
            throw new TitleDataEntryNotFoundException( getTitleInputName() );
        }
        return titleDataEntry.getValue();
    }

    @Override
    public DataEntryConfig getInputConfig( String name )
    {
        return config.getInputConfig( name );
    }

    @Override
    public ContentTypeConfig getContentTypeConfig()
    {
        return (ContentTypeConfig) super.getContentTypeConfig();
    }

    public List<BinaryDataEntry> getRemovedBinaryDataEntries( CustomContentData compareToThis )
    {
        List<BinaryDataEntry> removedEntries = new ArrayList<BinaryDataEntry>();
        List<BinaryDataEntry> currentList = getBinaryDataEntryList();
        for ( BinaryDataEntry binaryDataEntry : currentList )
        {
            if ( !compareToThis.hasBinaryDataEntry( binaryDataEntry ) )
            {
                removedEntries.add( binaryDataEntry );
            }
        }

        return removedEntries;
    }
}