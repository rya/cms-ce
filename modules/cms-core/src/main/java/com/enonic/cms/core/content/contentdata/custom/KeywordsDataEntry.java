/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class KeywordsDataEntry
    extends AbstractInputDataEntry
{
    private List<String> keywords = new ArrayList<String>();


    public KeywordsDataEntry( DataEntryConfig config )
    {
        super( config, DataEntryType.KEYWORDS );
    }

    public KeywordsDataEntry( DataEntryConfig config, List<String> keywords )
    {
        super( config, DataEntryType.KEYWORDS );
        this.keywords = keywords;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.enonic.cms.core.content.contentdata.custom.AbstractDataEntry#validate()
     */
    @Override
    public void validate()
    {
        //yet to be done
    }

    public boolean breaksRequiredContract()
    {
        return keywords.isEmpty();
    }

    public KeywordsDataEntry addKeyword( final String value )
    {
        if ( StringUtils.isNotBlank( value ) )
        {
            keywords.add( value );
        }
        return this;
    }

    public List<String> getKeywords()
    {
        return keywords;
    }

    public boolean hasValue()
    {
        return !keywords.isEmpty();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        KeywordsDataEntry that = (KeywordsDataEntry) o;

        if ( !equalsEntries( keywords, that.getKeywords() ) )
        {
            return false;
        }

        return true;
    }

    private boolean equalsEntries( List<String> entriesA, List<String> entriesB )
    {
        if ( entriesA.size() != entriesB.size() )
        {
            return false;
        }

        for ( int i = 0; i < entriesA.size(); i++ )
        {
            final String entryA = entriesA.get( i );
            final String entryB = entriesB.get( i );
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
        final HashCodeBuilder builder = new HashCodeBuilder( 171, 759 ).appendSuper( super.hashCode() );
        for ( String keyword : keywords )
        {
            builder.append( keyword );
        }
        return builder.toHashCode();
    }
}
