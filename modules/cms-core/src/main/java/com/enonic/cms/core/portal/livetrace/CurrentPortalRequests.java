/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Oct 7, 2010
 */
public class CurrentPortalRequests
{
    private LinkedList<PortalRequestTrace> currentPortalRequestTraces = new LinkedList<PortalRequestTrace>();

    public synchronized void add( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.addLast( trace );
    }

    public synchronized void remove( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.remove( trace );
    }

    public synchronized List<PortalRequestTrace> getList()
    {
        return ImmutableList.copyOf( currentPortalRequestTraces );
    }

    public int getSize()
    {
        return currentPortalRequestTraces.size();
    }
}
