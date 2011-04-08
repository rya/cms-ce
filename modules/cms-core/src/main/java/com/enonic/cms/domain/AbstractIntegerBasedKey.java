/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.io.Serializable;

public abstract class AbstractIntegerBasedKey
    implements IntBasedKey, Serializable, Comparable<AbstractIntegerBasedKey>
{
    protected int intValue;

    protected Integer integerValue;

    private String stringValue;

    protected void init( final String value )
    {
        if ( value == null )
        {
            throw new InvalidKeyException( value, this.getClass() );
        }

        try
        {
            init( Integer.parseInt( value ) );
        }
        catch ( NumberFormatException e )
        {
            throw new InvalidKeyException( value, this.getClass() );
        }
    }

    protected void init( final Integer value )
    {
        if ( value == null )
        {
            throw new InvalidKeyException( value, this.getClass() );
        }

        validate( value );
        this.intValue = value;
        this.integerValue = value;
        this.stringValue = String.valueOf( value );
    }

    protected void validate( final int value )
    {
        validateRange( value );
    }

    /**
     * Override this if needed.
     */
    protected void validateRange( final int value )
    {
        if ( value < minAllowedValue() || value > maxAllowedValue() )
        {
            throw new InvalidKeyException( value, this.getClass(), "Invalid range" );
        }
    }

    /**
     * Override this if needed.
     */
    protected int maxAllowedValue()
    {
        return Integer.MAX_VALUE;
    }

    /**
     * Override this if needed.
     */
    protected int minAllowedValue()
    {
        return 0;
    }

    public int toInt()
    {
        return intValue;
    }

    protected int intValue()
    {
        return intValue;
    }

    public Integer integerValue()
    {
        return integerValue;
    }

    public int compareTo( AbstractIntegerBasedKey other )
    {
        if ( other == null )
        {
            throw new NullPointerException();
        }

        return integerValue.compareTo( other.integerValue );
    }

    /**
     * @return the key value as string.
     */
    public String toString()
    {
        return stringValue;
    }

}
