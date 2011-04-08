/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.ImmutableList;

/**
 * Oct 6, 2010
 */
public class HistoryOfPortalRequests
{
    private static AtomicLong histoyCounter = new AtomicLong();

    private int maxSize;

    private LinkedList<PastPortalRequestTrace> list = new LinkedList<PastPortalRequestTrace>();

    public HistoryOfPortalRequests( int maxSize )
    {
        this.maxSize = maxSize;
    }

    public void add( PortalRequestTrace portalRequestTrace )
    {
        final long historyCount = histoyCounter.incrementAndGet();

        PastPortalRequestTrace pastPortalRequestTrace = new PastPortalRequestTrace( historyCount, portalRequestTrace );

        synchronized ( list )
        {
            list.addFirst( pastPortalRequestTrace );
            doRetainSize();
        }
    }

    public List<PastPortalRequestTrace> getList()
    {
        synchronized ( list )
        {
            return ImmutableList.copyOf( list );
        }
    }

    public List<PastPortalRequestTrace> getListSince( long historyRecordNumber )
    {
        LinkedList<PastPortalRequestTrace> sinceList = new LinkedList<PastPortalRequestTrace>();
        for ( PastPortalRequestTrace trace : getList() )
        {
            if ( trace.getHistoryRecordNumber() > historyRecordNumber )
            {
                sinceList.addLast( trace );
            }
        }
        return sinceList;
    }

    private void doRetainSize()
    {
        if ( list.size() > maxSize )
        {
            list.removeLast();
        }
    }
}
