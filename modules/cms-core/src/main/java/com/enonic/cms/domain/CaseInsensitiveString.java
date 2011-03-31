/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A class that should work exactely like String, except that when comparing to another String, the case is ignored on the equals method.
 * This is important because frameworks like Hibernate can use this instead of <code>java.lang.String</code>, as keys or other base values
 * where the comparator operation needs to be case insensitve.
 * <p/>
 * Not all methods of String are implemented yet.  If any other methods are needed, just implement them by calling the same method on the
 * <code>caseInsensitiveValue</code> string.
 */
public class CaseInsensitiveString
    implements Serializable, Comparable<CaseInsensitiveString>
{
    private String caseInsensitiveValue;

    public CaseInsensitiveString( String s )
    {
        if ( s == null )
        {
            throw new NullPointerException( "CaseInsensitiveString may not take a null value." );
        }
        caseInsensitiveValue = s;
    }

    public int compareTo( CaseInsensitiveString otherCaseInsensitiveString )
    {
        return caseInsensitiveValue.toLowerCase().compareTo( otherCaseInsensitiveString.toString().toLowerCase() );
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

        CaseInsensitiveString that = (CaseInsensitiveString) o;

        if ( caseInsensitiveValue.equalsIgnoreCase( that.caseInsensitiveValue ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder( 495, 73 ).append( caseInsensitiveValue.toLowerCase() ).toHashCode();
    }

    @Override
    public String toString()
    {
        return caseInsensitiveValue;
    }
}
