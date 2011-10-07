/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.xmlbased;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Document;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.InvalidContentDataException;
import com.enonic.cms.core.content.contentdata.custom.AbstractInputDataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public abstract class AbstractXmlBasedInputDataEntry
    extends AbstractInputDataEntry
{
    protected Document value = null;

    private String valueAsString = null;

    protected AbstractXmlBasedInputDataEntry( final DataEntryConfig config, final DataEntryType type, final String value )
    {
        super( config, type );
        if ( StringUtils.isNotBlank( value ) )
        {
            try
            {
                this.value = JDOMUtil.parseDocument( value );
                this.valueAsString = JDOMUtil.prettyPrintDocument( this.value, "", true );
            }
            catch ( final Exception e )
            {
                throw new InvalidContentDataException( "Could not parse input: " + this.getName(), e );
            }
        }
    }

    protected abstract void customValidate();

    public void validate()
    {
        customValidate();
    }

    public Document getValue()
    {
        if ( value == null )
        {
            return null;
        }
        return (Document) value.clone();
    }

    public String getValueAsString()
    {
        return valueAsString;
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

        final AbstractXmlBasedInputDataEntry that = (AbstractXmlBasedInputDataEntry) o;

        if ( valueAsString != null ? !valueAsString.equals( that.valueAsString ) : that.valueAsString != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 497, 697 ).appendSuper( super.hashCode() ).append( value ).toHashCode();
    }
}