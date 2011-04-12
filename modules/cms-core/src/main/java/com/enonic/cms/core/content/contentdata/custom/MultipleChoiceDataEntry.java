/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;


public class MultipleChoiceDataEntry
    extends AbstractInputDataEntry
{

    private String text;

    private List<MultipleChoiceAlternative> alternatives;

    public MultipleChoiceDataEntry( DataEntryConfig config, String text, List<MultipleChoiceAlternative> alternatives )
    {
        super( config, DataEntryType.MULTIPLE_CHOICE );
        this.text = text;
        this.alternatives = alternatives;

    }

    public void validate()
    {

    }

    public boolean breaksRequiredContract()
    {
        if ( alternatives == null || text == null )
        {
            return true;
        }
        if ( alternatives.size() < 2 )
        {
            return true;
        }
        return StringUtils.isBlank( text );
    }

    public boolean hasValue()
    {
        return text != null || alternatives != null;
    }

    public String getText()
    {
        return text;
    }

    public List<MultipleChoiceAlternative> getAlternatives()
    {
        return alternatives;
    }

    @Override
    public String toString()
    {
        return "MultipleChoiceDataEntry: '" + text + "', with " + ( alternatives == null ? "NULL" : alternatives.size() ) +
            " alternatives.";
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

        MultipleChoiceDataEntry that = (MultipleChoiceDataEntry) o;

        if ( alternatives != null ? !alternatives.equals( that.alternatives ) : that.alternatives != null )
        {
            return false;
        }
        if ( text != null ? !text.equals( that.text ) : that.text != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 402, 679 ).appendSuper( super.hashCode() ).append( text ).append( alternatives ).toHashCode();
    }
}
