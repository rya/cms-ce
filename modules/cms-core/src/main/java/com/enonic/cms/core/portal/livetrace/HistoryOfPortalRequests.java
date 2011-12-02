/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Oct 6, 2010
 */
public class HistoryOfPortalRequests
{
    private static long historyCounter = 0;

    private int maxSize;

    private LinkedList<PortalRequestTrace> list = new LinkedList<PortalRequestTrace>();

    public HistoryOfPortalRequests( int maxSize )
    {
        this.maxSize = maxSize;
    }

    public void add( PortalRequestTrace portalRequestTrace )
    {
        synchronized ( list )
        {
            portalRequestTrace.setCompletedNumber( ++historyCounter );
            list.addFirst( portalRequestTrace );
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

    public List<PortalRequestTrace> getListSince( long historyRecordNumber )
    {
        LinkedList<PortalRequestTrace> sinceList = new LinkedList<PortalRequestTrace>();
        for ( PortalRequestTrace trace : getList() )
        {
            if ( trace.getCompletedNumber() > historyRecordNumber )
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

    public int getSize()
    {
        return list.size();
    }
}
