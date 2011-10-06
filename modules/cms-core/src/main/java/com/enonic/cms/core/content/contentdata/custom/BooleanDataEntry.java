/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;

public class BooleanDataEntry
    extends AbstractInputDataEntry
{
    private Boolean value;

    public BooleanDataEntry( DataEntryConfig config, Boolean value )
    {
        super( config, DataEntryType.BOOLEAN );
        this.value = value;
    }

    public Boolean getValueAsBoolean()
    {
        return value;
    }

    public String getValueAsString()
    {
        return value.toString();
    }

    public boolean hasValue()
    {
        return value != null;
    }

    @Override
    public void validate()
    {
        // yet to be done
    }

    public boolean breaksRequiredContract()
    {
        return value == null;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BooleanDataEntry ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        BooleanDataEntry that = (BooleanDataEntry) o;

        if ( value != null ? !value.equals( that.value ) : that.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 339, 937 ).appendSuper( super.hashCode() ).append( value ).toHashCode();
    }
}