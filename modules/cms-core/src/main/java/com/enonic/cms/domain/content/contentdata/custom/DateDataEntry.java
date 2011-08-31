/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.contentdata.custom;

import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.esl.util.DateUtil;

import com.enonic.cms.api.util.Preconditions;

import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;

public class DateDataEntry
    extends AbstractInputDataEntry
    implements TitleDataEntry
{
    private Date value;

    public DateDataEntry( DataEntryConfig config, Date value )
    {
        super( config, DataEntryType.DATE );
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.enonic.cms.domain.content.contentdata.custom.AbstractDataEntry#validate()
     */
    @Override
    public void validate()
    {
        // yet to be done
    }

    public boolean breaksRequiredContract()
    {
        return value == null;
    }

    public Date getValue()
    {
        return value;
    }

    public String getValueAsTitle()
    {
        Preconditions.checkNotNull( value );
        return DateUtil.formatDate( value );
    }

    public boolean hasValue()
    {
        return value != null;
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

        DateDataEntry that = (DateDataEntry) o;

        if ( value != null ? !value.equals( that.value ) : that.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 655, 489 ).appendSuper( super.hashCode() ).append( value ).toHashCode();
    }
}