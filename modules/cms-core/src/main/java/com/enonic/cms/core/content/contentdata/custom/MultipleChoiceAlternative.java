/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class MultipleChoiceAlternative
{

    private String alternativeText;

    private boolean correct = false;

    public MultipleChoiceAlternative( String alternativeText, boolean correct )
    {
        this.alternativeText = alternativeText;
        this.correct = correct;
    }

    public String getAlternativeText()
    {
        return alternativeText;
    }

    public boolean isCorrect()
    {
        return correct;
    }

    public String isCorrectAsString()
    {
        return correct ? "true" : "false";
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

        MultipleChoiceAlternative that = (MultipleChoiceAlternative) o;

        if ( correct != that.correct )
        {
            return false;
        }
        if ( alternativeText != null ? !alternativeText.equals( that.alternativeText ) : that.alternativeText != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 330, 940 ).appendSuper( super.hashCode() ).append( correct ).append( alternativeText ).toHashCode();
    }
}
