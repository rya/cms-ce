/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

/**
 * Apr 21, 2010
 */
public class BlockGroupDataEntriesModifier
{
    private BlockGroupDataEntries existing;

    private boolean purge;

    public BlockGroupDataEntriesModifier( BlockGroupDataEntries existing, boolean purge )
    {
        this.existing = existing;
        this.purge = purge;
    }

    public BlockGroupDataEntries modify( final BlockGroupDataEntries modifyingBlockGroupDataEntries )
    {
        BlockGroupDataEntries modifiedEntries = new BlockGroupDataEntries( existing.getBlockName() );

        for ( GroupDataEntry existingGroupDataEntry : existing.getGroupDataEntries() )
        {
            int index = existingGroupDataEntry.getGroupIndex();
            GroupDataEntry modifyingGroupDataEntry = modifyingBlockGroupDataEntries.getGroupDataEntry( index );
            if ( modifyingGroupDataEntry == null )
            {
                boolean keepExistingEntry = !purge;
                if ( keepExistingEntry )
                {
                    modifiedEntries.add( existingGroupDataEntry );
                }
            }
            else
            {
                GroupDataEntryModifier groupDataEntryModifier = new GroupDataEntryModifier( existingGroupDataEntry );
                modifiedEntries.add( groupDataEntryModifier.modify( modifyingGroupDataEntry ) );
            }
        }

        for ( GroupDataEntry newEntry : modifyingBlockGroupDataEntries.getGroupDataEntries() )
        {
            if ( !modifiedEntries.hasEntry( newEntry ) )
            {
                modifiedEntries.add( newEntry );
            }
        }

        modifiedEntries.reorganizeBySuccesiveIndexOrder();
        return modifiedEntries;
    }

}
