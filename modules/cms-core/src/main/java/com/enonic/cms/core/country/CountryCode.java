/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

/**
 * Aug 4, 2009
 */
public class CountryCode
{
    private String stringValue;

    public CountryCode( String stringValue )
    {
        if ( stringValue == null )
        {
            throw new IllegalArgumentException( "stringValue cannot be null" );
        }
        this.stringValue = stringValue.toUpperCase();
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

        CountryCode that = (CountryCode) o;

        if ( !stringValue.equals( that.stringValue ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 445;
        final int multiplierNonZeroOddNumber = 635;

        return initialNonZeroOddNumber * multiplierNonZeroOddNumber * stringValue.hashCode();
    }

    public String toString()
    {
        return stringValue;
    }
}
