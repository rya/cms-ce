/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.jdbc.delegate;

/**
 * This is the base delegating object.
 */
public abstract class DelegatingBase
{
    /**
     * Returns the delegate.
     */
    public abstract Object getDelegate();

    /**
     * Return the inner delegate.
     */
    public Object getInnerDelegate()
    {
        Object delegate = getDelegate();
        if ( delegate instanceof DelegatingBase )
        {
            return ( (DelegatingBase) delegate ).getInnerDelegate();
        }
        else
        {
            return delegate;
        }
    }

    /**
     * This will return the inner delegate hash code.
     */
    public int hashCode()
    {
        return getInnerDelegate().hashCode();
    }

    /**
     * Return true if equals,
     */
    public boolean equals( Object other )
    {
        if ( other == this )
        {
            return true;
        }

        if ( other instanceof DelegatingBase )
        {
            other = ( (DelegatingBase) other ).getInnerDelegate();
        }

        return getInnerDelegate().equals( other );
    }
}
