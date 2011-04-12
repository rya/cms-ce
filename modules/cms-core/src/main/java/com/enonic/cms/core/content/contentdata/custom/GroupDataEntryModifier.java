/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

/**
 * Mar 23, 2010
 */
public class GroupDataEntryModifier
{
    private GroupDataEntry existing;

    public GroupDataEntryModifier( GroupDataEntry existing )
    {
        this.existing = existing;
    }

    public GroupDataEntry modify( GroupDataEntry modifyingGroupDataEntry )
    {
        if ( modifyingGroupDataEntry == null )
        {
            throw new IllegalArgumentException( "Given modifyingGroupDataEntry cannot be null" );
        }

        boolean noNeedToModify = modifyingGroupDataEntry.hasAllDataEntries();

        if ( noNeedToModify )
        {
            return modifyingGroupDataEntry;
        }

        GroupDataEntry modifiedGroupDataEntry = new GroupDataEntry( existing.getName(), existing.getXPath(), existing.getGroupIndex() );
        modifiedGroupDataEntry.setConfig( existing.getConfig() );

        // add all new ones
        for ( DataEntry newDataEntry : modifyingGroupDataEntry.getEntries() )
        {
            modifiedGroupDataEntry.add( newDataEntry );
        }

        // add existing ones that is not already added
        for ( DataEntry existingDataEntry : existing.getEntries() )
        {
            boolean dataEntryNotAlreadyAdded = modifiedGroupDataEntry.getEntry( existingDataEntry.getName() ) == null;
            if ( dataEntryNotAlreadyAdded )
            {
                modifiedGroupDataEntry.add( existingDataEntry );
            }
        }

        return modifiedGroupDataEntry;
    }
}
