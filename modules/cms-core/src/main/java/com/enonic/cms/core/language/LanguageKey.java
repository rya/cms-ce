/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.language;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.AbstractIntegerBasedKey;
import com.enonic.cms.core.IntBasedKey;

/**
 * Created by rmy - Date: Oct 5, 2009
 */
public class LanguageKey
    extends AbstractIntegerBasedKey
    implements Serializable, IntBasedKey
{

    public LanguageKey( String languageKey )
    {
        init( languageKey );
    }

    public LanguageKey( int languageKey )
    {
        init( languageKey );
    }

    public LanguageKey( Integer languageKey )
    {
        init( languageKey );
    }

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

        LanguageKey languageKey = (LanguageKey) o;

        return intValue == languageKey.intValue;
    }

    public int hashCode()
    {
        final int initialNonZeroOddNumber = 757;
        final int multiplierNonZeroOddNumber = 351;
        return new HashCodeBuilder( initialNonZeroOddNumber, multiplierNonZeroOddNumber ).append( intValue ).toHashCode();
    }

}
