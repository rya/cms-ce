/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.HashMap;

import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

/**
 * Drop-in replacement for similar trove class.
 */
public final class TIntIntHashMap
{
    private final HashMap<Integer, Integer> map;

    public TIntIntHashMap()
    {
        this.map = Maps.newHashMap();
    }

    public int[] keys()
    {
        return Ints.toArray( this.map.keySet() );
    }

    public void put( final int key, final int value )
    {
        this.map.put( key, value );
    }

    public boolean contains( final int value )
    {
        return this.map.containsValue( value );
    }

    public boolean containsKey( final int key )
    {
        return this.map.containsKey( key );
    }

    public int get( final int key )
    {
        return this.map.get( key );
    }

    public int size()
    {
        return this.map.size();
    }
}