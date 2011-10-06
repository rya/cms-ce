/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.enonic.cms.core.content.contentdata.MissingRequiredContentDataException;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class GroupDataEntry
    extends AbstractDataEntrySet
    implements DataEntrySet
{
    private int groupIndex;


    public GroupDataEntry( GroupDataEntry other, int newGroupIndex )
    {
        super( other );
        this.groupIndex = newGroupIndex;
    }

    public GroupDataEntry( String name, String xpath )
    {
        super( resolveName( name, xpath ), DataEntryType.GROUP, xpath );
    }

    public GroupDataEntry( String name, String xpath, int groupIndex )
    {
        super( resolveName( name, xpath ), DataEntryType.GROUP, xpath );
        this.groupIndex = groupIndex;
    }

    public void validate()
    {
        // validate required data
        doValidateRequiredDataEntries();

        // Validate each data entry
        for ( DataEntry dataEntry : entries )
        {
            dataEntry.validate();
        }
    }

    private void doValidateRequiredDataEntries()
    {
        for ( DataEntryConfig dataEntryConfig : getConfig().getInputConfigs() )
        {
            DataEntry dataEntry = getEntry( dataEntryConfig.getName() );

            if ( dataEntryConfig.isRequired() )
            {
                validateRequiredDataEntry( dataEntryConfig, dataEntry );
            }
        }
    }

    @Override
    protected void validateRequiredDataEntry( final DataEntryConfig dataEntryConfig, final DataEntry dataEntry )
    {
        if ( dataEntry == null )
        {
            throw MissingRequiredContentDataException.missingDataEntryInGroup( dataEntryConfig, this );
        }
        else if ( !dataEntry.hasValue() )
        {
            throw MissingRequiredContentDataException.missingDataEntryValueInGroup( dataEntryConfig, this );
        }
        else if ( dataEntry.breaksRequiredContract() )
        {
            throw MissingRequiredContentDataException.missingDataEntryValueInGroup( dataEntryConfig, this );
        }
    }

    public boolean hasAllDataEntries()
    {
        for ( DataEntryConfig dataEntryConfig : getConfig().getInputConfigs() )
        {
            if ( !entryMap.containsKey( dataEntryConfig.getName() ) )
            {
                return false;
            }
        }
        return true;
    }

    private static String resolveName( String name, String xpath )
    {
        if ( name == null || name.trim().length() == 0 )
        {
            return xpath;
        }

        return name;
    }

    public int getGroupIndex()
    {
        return groupIndex;
    }

    public void setGroupIndex( int groupIndex )
    {
        this.groupIndex = groupIndex;
    }

    @Override
    public String toString()
    {
        ToStringBuilder b = new ToStringBuilder( this );
        b.append( "name", getName() );
        b.append( "groupIndex", groupIndex );
        return b.toString();
    }
}
