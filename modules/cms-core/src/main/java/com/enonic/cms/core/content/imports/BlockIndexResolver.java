/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;

public class BlockIndexResolver
{
    final private ContentEntity existingContent;

    final private Map<String, Integer> newGroupIndexCountMap = new HashMap<String, Integer>();

    public BlockIndexResolver( final ContentEntity existingContent )
    {
        this.existingContent = existingContent;
    }

    public int resolve( final String blockName, final String syncInputName, final String syncValue )
    {
        if ( existingContent == null || syncInputName == null )
        {
            return resolveNextBlockIndex( blockName, 0 );
        }

        return resolveFromExistingContent( blockName, syncInputName, syncValue );
    }

    private int resolveFromExistingContent( final String blockName, final String syncInputName, final String syncValue )
    {
        final ContentVersionEntity existingMainVersion = existingContent.getMainVersion();
        final CustomContentData existingContentData = (CustomContentData) existingMainVersion.getContentData();
        final List<GroupDataEntry> existingGroupDataEntries = existingContentData.getGroupDataSets( blockName );

        for ( final GroupDataEntry groupDataEntry : existingGroupDataEntries )
        {
            final DataEntry dataEntryForSync = groupDataEntry.getEntry( syncInputName );

            if ( dataEntryForSync.getType() == DataEntryType.TEXT )
            {
                final TextDataEntry textDataEntry = (TextDataEntry) dataEntryForSync;
                if ( textDataEntry.getValue().equals( syncValue ) )
                {
                    return groupDataEntry.getGroupIndex();
                }
            }
        }
        return resolveNextBlockIndex( blockName, existingGroupDataEntries.size() );
    }

    private int resolveNextBlockIndex( final String groupName, final int existingGroupDataEntryCount )
    {
        int index = existingGroupDataEntryCount;
        if ( newGroupIndexCountMap.containsKey( groupName ) )
        {
            index = newGroupIndexCountMap.get( groupName );
        }

        index++;
        newGroupIndexCountMap.put( groupName, index );

        return index;
    }
}
