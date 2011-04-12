/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.cms.core.content.contenttype.CtySetConfig;

/**
 * Mar 22, 2010
 */
public class CustomContentDataModifier
{
    private final CustomContentData existing;

    private final Set<String> blockGroupsToPurgeByName = new HashSet<String>();


    public CustomContentDataModifier( final CustomContentData existing )
    {
        this.existing = existing;
    }

    public void addBlockGroupsToPurge( final Collection<String> blockGroupNames )
    {
        blockGroupsToPurgeByName.addAll( blockGroupNames );
    }

    public void addBlockGroupToPurge( final String name )
    {
        CtySetConfig blockGroupConfig = existing.getContentTypeConfig().getSetConfig( name );
        if ( blockGroupConfig == null )
        {
            throw new IllegalArgumentException( "Block group does not exist: " + name );
        }
        blockGroupsToPurgeByName.add( name );
    }

    public CustomContentData modify( final CustomContentData modifyingContentData )
    {
        final CustomContentData modifiedCCD = new CustomContentData( existing.getContentTypeConfig() );

        // add existing ones if new one isn't found in the newContentData, otherwise use the new one.
        for ( final DataEntry existingDataEntry : existing.getNonGroupDataEntries() )
        {
            final DataEntry dataEntryToAdd = resolveNonGroupDataEntryToUse( modifyingContentData, existingDataEntry );

            if ( dataEntryToAdd != null )
            {
                modifiedCCD.add( dataEntryToAdd );
            }
        }

        // add new ones, if not already added
        for ( final DataEntry modifyingDataEntry : modifyingContentData.getNonGroupDataEntries() )
        {
            boolean notAlreadyAdded = !alreadyAdded( modifiedCCD, modifyingDataEntry );
            if ( notAlreadyAdded )
            {
                modifiedCCD.add( modifyingDataEntry );
            }
        }

        for ( CtySetConfig blockConfig : existing.getConfig().getSetConfig() )
        {
            if ( !blockConfig.hasGroupXPath() )
            {
                continue;
            }

            BlockGroupDataEntries existing = this.existing.getBlockGroupDataEntries( blockConfig.getName() );
            BlockGroupDataEntries modifying = modifyingContentData.getBlockGroupDataEntries( blockConfig.getName() );
            boolean purge = purgeRemainingBlockGroupDataEntries( blockConfig.getName() );

            BlockGroupDataEntriesModifier modifier = new BlockGroupDataEntriesModifier( existing, purge );
            BlockGroupDataEntries modified = modifier.modify( modifying );

            for ( GroupDataEntry groupDataEntry : modified.getGroupDataEntries() )
            {
                modifiedCCD.add( groupDataEntry );
            }
        }

        return modifiedCCD;
    }

    private DataEntry resolveNonGroupDataEntryToUse( final CustomContentData modifyingContentData, final DataEntry existingDataEntry )
    {
        final DataEntry modifyingDataEntry = modifyingContentData.getEntry( existingDataEntry.getName() );

        if ( modifyingDataEntry != null )
        {
            return modifyingDataEntry;
        }
        else
        {
            return existingDataEntry;
        }
    }

    private boolean purgeRemainingBlockGroupDataEntries( final String blockGroupName )
    {
        return blockGroupsToPurgeByName.contains( blockGroupName );
    }

    private boolean alreadyAdded( final CustomContentData contentData, final DataEntry dataEntry )
    {
        if ( dataEntry instanceof GroupDataEntry )
        {
            GroupDataEntry groupDataEntry = (GroupDataEntry) dataEntry;
            return contentData.hasGroupDataEntry( groupDataEntry.getName(), groupDataEntry.getGroupIndex() );
        }
        else
        {
            return contentData.getEntry( dataEntry.getName() ) != null;
        }
    }
}
