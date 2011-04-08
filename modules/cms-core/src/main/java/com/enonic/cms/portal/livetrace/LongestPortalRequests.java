/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

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

    public synchronized void add( PortalRequestTrace item )
    {
        list.add( item );
        doRetainSize();
    }

    public synchronized List<PortalRequestTrace> getList()
    {
        return ImmutableList.copyOf( list );
    }

    private void doRetainSize()
    {
        if ( list.size() > maxSize )
        {
            list.remove( list.last() );
        }
    }
}
