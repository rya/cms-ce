/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.stringbased;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.AbstractInputDataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public abstract class AbstractStringBasedInputDataEntry
    extends AbstractInputDataEntry
{
    protected String value;

    protected AbstractStringBasedInputDataEntry( DataEntryConfig config, DataEntryType type, String value )
    {
        super( config, type );
        this.value = value;
    }

    protected abstract void customValidate();

    public final void validate()
    {
        customValidate();
    }

    public String getValue()
    {
        return value;
    }

    public boolean hasValue()
    {
        return value != null;
    }

    public boolean isEmpty()
    {
        return value == null || value.length() == 0;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof AbstractStringBasedInputDataEntry ) )
        {
            return false;
        }
        if ( !super.equals( o ) )
        {
            return false;
        }

        AbstractStringBasedInputDataEntry that = (AbstractStringBasedInputDataEntry) o;

        if ( value != null ? !value.equals( that.value ) : that.value != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 447, 657 ).appendSuper( super.hashCode() ).append( value ).toHashCode();
    }

    protected static void validateMaxLength( String value, Integer maxLength, String name, String xpath )
    {

        if ( value != null && maxLength != null )
        {
            if ( value.length() > maxLength )
            {
                throw new InvalidContentDataException(
                    "Invalid field length, field " + name + " with xpath " + xpath + " has max length = " + maxLength +
                        ", but has size = " + value.length() );
            }
        }

    }
}
