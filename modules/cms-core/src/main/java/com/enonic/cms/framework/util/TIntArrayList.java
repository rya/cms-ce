/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.ArrayList;

import com.google.common.primitives.Ints;

/**
 * Drop-in replacement for similar trove class.
 */
public final class TIntArrayList
{
    private final ArrayList<Integer> list;

    public TIntArrayList()
    {
        this.list = new ArrayList<Integer>();
    }

    public int get( final int index )
    {
        return this.list.get( index );
    }

    public void add( final int... values )
    {
        for ( int value : values )
        {
            this.list.add( value );
        }
    }

    public boolean contains( final int value )
    {
        return this.list.contains( value );
    }

    public int size()
    {
        return this.list.size();
    }

    public int[] toArray()
    {
        return Ints.toArray( this.list );
    }
}
