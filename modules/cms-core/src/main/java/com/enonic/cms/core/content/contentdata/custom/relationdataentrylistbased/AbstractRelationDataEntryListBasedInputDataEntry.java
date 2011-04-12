/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contentdata.custom.AbstractInputDataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public abstract class AbstractRelationDataEntryListBasedInputDataEntry<T extends RelationDataEntry>
    extends AbstractInputDataEntry
{
    protected List<T> entries = new ArrayList<T>();

    protected AbstractRelationDataEntryListBasedInputDataEntry( DataEntryConfig config, DataEntryType type )
    {
        super( config, type );
    }

    protected abstract void customValidate();

    @Override
    public final void validate()
    {
        customValidate();
    }

    protected void addEntry( T value )
    {
        if ( value != null && value.hasValue() )
        {
            entries.add( value );
        }
    }

    public boolean hasValue()
    {
        return !entries.isEmpty();
    }

    public List<T> getEntries()
    {
        return entries;
    }

    public Collection<ContentKey> getRelatedContentKeys()
    {
        Set<ContentKey> keys = new LinkedHashSet<ContentKey>();
        for ( RelationDataEntry entry : entries )
        {
            if ( entry.getContentKey() != null )
            {
                keys.add( entry.getContentKey() );
            }
        }

        return keys;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractRelationDataEntryListBasedInputDataEntry ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        AbstractRelationDataEntryListBasedInputDataEntry that = (AbstractRelationDataEntryListBasedInputDataEntry) o;

        if ( !equalsEntries( this.getEntries(), that.getEntries() ) )
        {
            return false;
        }

        return true;
    }

    private boolean equalsEntries( List entriesA, List entriesB )
    {
        if ( entriesA.size() != entriesB.size() )
        {
            return false;
        }

        for ( int i = 0; i < entriesA.size(); i++ )
        {
            final Object entryA = entriesA.get( i );
            final Object entryB = entriesB.get( i );
            if ( !entryA.equals( entryB ) )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final HashCodeBuilder builder = new HashCodeBuilder( 681, 817 ).appendSuper( super.hashCode() );
        for ( T entry : entries )
        {
            builder.append( entry );
        }
        return builder.toHashCode();
    }

}
