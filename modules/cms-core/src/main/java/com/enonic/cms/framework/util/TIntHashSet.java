/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.HashSet;

import com.google.common.primitives.Ints;

/**
 * Drop-in replacement for similar trove class.
 */
public final class TIntHashSet
{
    private final HashSet<Integer> set;

    public TIntHashSet()
    {
        this.set = new HashSet<Integer>();
    }

    public void add( final int... values )
    {
        for ( int value : values )
        {
            this.set.add( value );
        }
    }

    public boolean contains( final int value )
    {
        return this.set.contains( value );
    }

    public int size()
    {
        return this.set.size();
    }

    public int[] toArray()
    {
        return Ints.toArray( this.set );
    }
}
