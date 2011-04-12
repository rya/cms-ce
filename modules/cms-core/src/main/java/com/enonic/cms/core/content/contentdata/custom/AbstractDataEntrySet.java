/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contentdata.MissingRequiredContentDataException;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contenttype.CtySet;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public abstract class AbstractDataEntrySet
    implements DataEntrySet
{

    protected String name;

    protected String xpath;

    protected CtySet config;

    protected List<DataEntry> entries = new ArrayList<DataEntry>();

    protected List<DataEntrySet> dataEntrySetEntries = new ArrayList<DataEntrySet>();

    protected Map<String, DataEntry> entryMap = new HashMap<String, DataEntry>();

    protected DataEntryType type;

    /**
     * A shallow copy constructor.
     */
    protected AbstractDataEntrySet( AbstractDataEntrySet other )
    {
        this.name = other.name;
        this.xpath = other.xpath;
        this.config = other.config;
        this.entries = other.entries;
        this.dataEntrySetEntries = other.dataEntrySetEntries;
        this.entryMap = other.entryMap;
        this.type = other.type;
    }

    protected AbstractDataEntrySet( String name, DataEntryType type, String xpath )
    {
        this.name = name;
        this.type = type;
        this.xpath = xpath;
    }

    public String getName()
    {
        return name;
    }

    public String getXPath()
    {
        return xpath;
    }

    public DataEntryType getType()
    {
        return type;
    }

    public void setConfig( CtySet value )
    {
        this.config = value;
    }

    public CtySet getConfig()
    {
        return config;
    }

    public void add( DataEntry entry )
    {
        if ( entry instanceof AbstractDataEntrySet )
        {
            CtySetConfig setConfig = getSetConfig( entry.getName() );
            if ( setConfig == null )
            {
                throw new IllegalArgumentException( "No configuration for entry with name: " + entry.getName() );
            }

            AbstractDataEntrySet dataEntrySet = (AbstractDataEntrySet) entry;
            dataEntrySet.setConfig( setConfig );
            dataEntrySetEntries.add( dataEntrySet );
        }
        else if ( entry instanceof AbstractInputDataEntry )
        {
            validateEntry( (AbstractInputDataEntry) entry );
        }
        else
        {
            throw new IllegalArgumentException( "Unknown type of entry: " + entry.getClass().getName() );
        }

        entries.add( entry );
        entryMap.put( entry.getName(), entry );
    }

    private void validateEntry( AbstractInputDataEntry entry )
    {
        DataEntryConfig inputConfig = getInputConfigByRelateiveXPath( entry.getXPath() );

        if ( inputConfig == null )
        {
            throw new IllegalArgumentException( "No configuration for entry '" + entry.getName() + "' with xpath: " + entry.getXPath() );
        }
        // Validate that entry is of same type as when you lookup in contenttype config by xpath
        if ( !inputConfig.getType().isCompatible( entry.getType() ) )
        {
            throw new IllegalArgumentException( "Configuration for entry '" + entry.getName() + "' not compatible. Expected " +
                inputConfig.getType().getCompatibleDataEntryTypesAsCommaSeparatedString() + ", got " + entry.getType() + "." );
        }
    }

    public int numberOfEntries()
    {
        return entries.size();
    }

    public List<DataEntry> getEntries()
    {
        return entries;
    }

    public boolean hasValue()
    {
        /* Groups has allways value */
        return true;
    }

    public boolean breaksRequiredContract()
    {
        // No required contract on sets (groups), so it cant break!
        return false;
    }

    public List<DataEntry> getEntries( DataEntryType type )
    {
        List<DataEntry> entriesOfType = new ArrayList<DataEntry>();
        for ( DataEntry entry : entries )
        {
            if ( entry.getType() == type )
            {
                entriesOfType.add( entry );
            }
        }
        return entriesOfType;
    }

    public DataEntry getEntry( String name )
    {
        DataEntry dataEntry = entryMap.get( name );
        if ( dataEntry != null )
        {
            return dataEntry;
        }

        // if not found on this level, look thru the data entry sets
        for ( DataEntrySet dataEntrySet : dataEntrySetEntries )
        {
            dataEntry = dataEntrySet.getEntry( name );
            if ( dataEntry != null )
            {
                return dataEntry;
            }
        }

        return null;
    }

    public boolean hasGroupDataEntry( String name, int groupIndex )
    {
        GroupDataEntry existing = getGroupDataEntry( name, groupIndex );
        return existing != null;
    }

    public GroupDataEntry getGroupDataEntry( String name, int groupIndex )
    {
        for ( DataEntry dataEntry : getEntries() )
        {
            if ( dataEntry instanceof GroupDataEntry )
            {
                GroupDataEntry groupDataEntry = (GroupDataEntry) dataEntry;

                if ( groupDataEntry.getName().equals( name ) && groupDataEntry.getGroupIndex() == groupIndex )
                {
                    return groupDataEntry;
                }
            }
        }

        return null;
    }

    public Set<ContentKey> resolveRelatedContentKeys()
    {
        Set<ContentKey> keys = new HashSet<ContentKey>();

        for ( DataEntry entry : getEntries() )
        {
            if ( entry instanceof RelationDataEntry )
            {
                final ContentKey contentKey = ( (RelationDataEntry) entry ).getContentKey();
                if ( contentKey != null )
                {
                    keys.add( contentKey );
                }
            }
            else if ( entry instanceof RelationsDataEntry )
            {
                keys.addAll( ( (RelationsDataEntry) entry ).getRelatedContentKeys() );
            }
            else if ( entry instanceof DataEntrySet )
            {
                keys.addAll( ( (DataEntrySet) entry ).resolveRelatedContentKeys() );
            }
            else if ( entry instanceof HtmlAreaDataEntry )
            {
                keys.addAll( ( (HtmlAreaDataEntry) entry ).resolveRelatedContentKeys() );
            }
        }

        return keys;
    }

    public boolean hasRelatedChild( ContentKey contentKey )
    {
        return resolveRelatedContentKeys().contains( contentKey );
    }

    public List<BinaryDataEntry> getBinaryDataEntryList()
    {

        List<BinaryDataEntry> entries = new ArrayList<BinaryDataEntry>();
        for ( DataEntry entry : getEntries() )
        {
            if ( entry instanceof BinaryDataEntry )
            {
                entries.add( (BinaryDataEntry) entry );
            }
            else if ( entry instanceof DataEntrySet )
            {
                entries.addAll( ( (DataEntrySet) entry ).getBinaryDataEntryList() );
            }
        }
        return entries;
    }

    public boolean hasBinaryDataEntry( BinaryDataEntry subject )
    {

        for ( DataEntry entry : getEntries() )
        {
            if ( entry instanceof BinaryDataEntry )
            {
                BinaryDataEntry binaryDataEntry = (BinaryDataEntry) entry;
                if ( binaryDataEntry.hasExistingBinaryKey() &&
                    binaryDataEntry.getExistingBinaryKey().equals( subject.getExistingBinaryKey() ) )
                {
                    return true;
                }
            }
            else if ( entry instanceof DataEntrySet )
            {
                if ( ( (DataEntrySet) entry ).hasBinaryDataEntry( subject ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void replaceBinaryKeyPlaceholders( List<BinaryDataKey> binaryDatas )
    {
        for ( DataEntry entry : getEntries() )
        {
            if ( entry instanceof BinaryDataEntry )
            {
                BinaryDataEntry binaryDataEntry = (BinaryDataEntry) entry;
                if ( binaryDataEntry.hasBinaryKeyPlaceholder() )
                {
                    String placeHolder = binaryDataEntry.getBinaryKeyPlaceholder();
                    int index = Integer.valueOf( placeHolder.substring( 1 ) );
                    BinaryDataKey key = binaryDatas.get( index );
                    binaryDataEntry.setExistingBinaryKey( key.toInt() );
                }
            }
            else if ( entry instanceof DataEntrySet )
            {
                ( (DataEntrySet) entry ).replaceBinaryKeyPlaceholders( binaryDatas );
            }
        }
    }

    public void turnBinaryKeysIntoPlaceHolders( Map<BinaryDataKey, Integer> indexByBinaryDataKey )
    {
        for ( DataEntry entry : getEntries() )
        {
            if ( entry instanceof BinaryDataEntry )
            {
                BinaryDataEntry binaryDataEntry = (BinaryDataEntry) entry;
                if ( binaryDataEntry.hasExistingBinaryKey() )
                {
                    BinaryDataKey binaryDataKey = new BinaryDataKey( binaryDataEntry.getExistingBinaryKey() );
                    Integer index = indexByBinaryDataKey.get( binaryDataKey );
                    if ( index != null )
                    {
                        binaryDataEntry.setExistingBinaryKey( null );
                        binaryDataEntry.setBinaryKeyPlaceholder( "%" + index );
                    }
                }
            }
            else if ( entry instanceof DataEntrySet )
            {
                ( (DataEntrySet) entry ).turnBinaryKeysIntoPlaceHolders( indexByBinaryDataKey );
            }
        }
    }

    public List<GroupDataEntry> getGroupDataSets( final String name )
    {
        final List<GroupDataEntry> groupDatasets = new ArrayList<GroupDataEntry>();
        for ( final DataEntry entry : entries )
        {
            if ( entry instanceof GroupDataEntry )
            {
                final GroupDataEntry groupDataEntry = (GroupDataEntry) entry;
                if ( name.equals( groupDataEntry.getName() ) )
                {
                    groupDatasets.add( groupDataEntry );
                }
            }
        }
        return groupDatasets;
    }

    public DataEntryConfig getInputConfig( String name )
    {
        return config.getInputConfig( name );
    }

    public DataEntryConfig getInputConfigByRelateiveXPath( String relativeXpath )
    {
        return config.getInputConfigByRelativeXPath( relativeXpath );
    }

    public CtySet getContentTypeConfig()
    {
        return config;
    }

    public CtySetConfig getSetConfig( String name )
    {
        return config.getSetConfig( name );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractDataEntrySet ) )
        {
            return false;
        }

        AbstractDataEntrySet that = (AbstractDataEntrySet) o;
        if ( !equalsEntriesOrder( this.getEntries( DataEntryType.GROUP ), that.getEntries( DataEntryType.GROUP ) ) )
        {
            return false;
        }
        if ( !equalsEntriesIgnoreOrder( this.getEntries(), that.getEntries() ) )
        {
            return false;
        }

        return true;
    }

    private boolean equalsEntriesOrder( List<DataEntry> entriesA, List<DataEntry> entriesB )
    {
        if ( entriesA.size() != entriesB.size() )
        {
            return false;
        }

        int i = 0;
        for ( DataEntry entryA : entriesA )
        {
            final DataEntry entryB = entriesB.get( i++ );
            if ( !entryA.equals( entryB ) )
            {
                return false;
            }
        }

        return true;
    }

    private boolean equalsEntriesIgnoreOrder( List<DataEntry> entriesA, List<DataEntry> entriesB )
    {
        if ( entriesA.size() != entriesB.size() )
        {
            return false;
        }

        for ( DataEntry entryA : entriesA )
        {
            if ( !entriesB.contains( entryA ) )
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        final HashCodeBuilder builder = new HashCodeBuilder( 241, 459 ).appendSuper( super.hashCode() );
        for ( DataEntry entry : entries )
        {
            builder.append( entry );
        }
        return builder.toHashCode();
    }

    protected void validateRequiredDataEntry( final DataEntryConfig dataEntryConfig, final DataEntry dataEntry )
    {
        if ( dataEntry == null )
        {
            throw MissingRequiredContentDataException.missingDataEntry( dataEntryConfig );
        }
        else if ( !dataEntry.hasValue() )
        {
            throw MissingRequiredContentDataException.missingDataEntryValue( dataEntryConfig );
        }
        else if ( dataEntry.breaksRequiredContract() )
        {
            throw MissingRequiredContentDataException.missingDataEntryValue( dataEntryConfig );
        }
    }
}