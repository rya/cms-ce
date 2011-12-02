/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;
import java.util.TreeSet;

import com.google.common.collect.ImmutableList;

/**
 * Oct 6, 2010
 */
public class LongestPortalRequests
{
    private int maxSize;

    private TreeSet<PortalRequestTrace> list;

    public LongestPortalRequests( int maxSize )
    {
        this.maxSize = maxSize;
        list = new TreeSet<PortalRequestTrace>( new PortalRequestTraceComparatorByLongestTime() );
    }

    public void add( PortalRequestTrace item )
    {
        synchronized ( list )
        {
            list.add( item );
            doRetainSize();
        }
    }

    public List<PortalRequestTrace> getList()
    {
        synchronized ( list )
        {
            return ImmutableList.copyOf( list );
        }
    }

    private void doRetainSize()
    {
        if ( list.size() > maxSize )
        {
            list.remove( list.last() );
        }
    }

    public void clear()
    {
        synchronized ( list )
        {
            list.clear();
        }
    }
}
