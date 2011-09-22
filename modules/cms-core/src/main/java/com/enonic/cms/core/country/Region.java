/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.country;

/**
 * Aug 4, 2009
 */
public class Region
{
    private String code;

    private String englishName;

    private String localName;

    public Region( String code, String englishName, String localName )
    {
        if ( code == null )
        {
            throw new IllegalArgumentException( "code cannot be null" );
        }
        this.code = code;
        this.englishName = englishName;
        this.localName = localName;
    }

    public String getCode()
    {
        return code;
    }

    public String getEnglishName()
    {
        return englishName;
    }

    public String getLocalName()
    {
        return localName;
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

        Region region = (Region) o;

        if ( !code.equals( region.code ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        final int initialNonZeroOddNumber = 557;
        final int multiplierNonZeroOddNumber = 735;

        return initialNonZeroOddNumber * multiplierNonZeroOddNumber * code.hashCode();
    }

    public String toString()
    {
        return code;
    }
}
