/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Apr 21, 2010
 */
public class BlockGroupDataEntries
{
    private String blockName;

    private Map<Integer, GroupDataEntry> entriesByIndex = new LinkedHashMap<Integer, GroupDataEntry>();

    private int lastIndex = 0;

    public BlockGroupDataEntries( String blockName )
    {
        this.blockName = blockName;
    }

    public String getBlockName()
    {
        return blockName;
    }

    public void add( GroupDataEntry groupDataEntry )
    {
        Integer index = groupDataEntry.getGroupIndex();
        if ( entriesByIndex.containsKey( index ) )
        {
            throw new IllegalArgumentException( "GroupDataEntry at index " + index + " already exists." );
        }

        entriesByIndex.put( index, groupDataEntry );
        lastIndex = index;
    }

    public Iterable<GroupDataEntry> getGroupDataEntries()
    {
        return entriesByIndex.values();
    }

    public int numberOfEntries()
    {
        return entriesByIndex.size();
    }

    public int lasIndex()
    {
        return lastIndex;
    }

    public int expectedNextIndex()
    {
        return lastIndex + 1;
    }

    public GroupDataEntry getGroupDataEntry( int index )
    {
        return entriesByIndex.get( index );
    }

    public boolean hasEntryAtIndex( int index )
    {
        return entriesByIndex.containsKey( index );
    }

    public boolean hasEntry( GroupDataEntry groupDataEntry )
    {
        return entriesByIndex.containsKey( groupDataEntry.getGroupIndex() );
    }

    public void reorganizeBySuccesiveIndexOrder()
    {
        Map<Integer, GroupDataEntry> reorganisedEntriesByIndex = new LinkedHashMap<Integer, GroupDataEntry>();

        int index = 0;
        for ( Map.Entry<Integer, GroupDataEntry> entry : entriesByIndex.entrySet() )
        {
            int newIndex = ++index;
            reorganisedEntriesByIndex.put( index, new GroupDataEntry( entry.getValue(), newIndex ) );
        }

        entriesByIndex = reorganisedEntriesByIndex;
        lastIndex = index;
    }

    @Override
    public String toString()
    {
        ToStringBuilder b = new ToStringBuilder( this );
        b.append( "blockName", blockName );
        b.append( "size", entriesByIndex.size() );
        return b.toString();
    }
}
