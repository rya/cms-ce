/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.livetrace;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.collect.ImmutableList;

/**
 * Oct 7, 2010
 */
public class CurrentPortalRequests
{
    private LinkedHashSet<PortalRequestTrace> currentPortalRequestTraces = new LinkedHashSet<PortalRequestTrace>();

    private AtomicInteger size = new AtomicInteger( 0 );

    public synchronized void add( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.add( trace );
        size.incrementAndGet();
    }

    public synchronized void remove( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.remove( trace );
        size.decrementAndGet();
    }

    public synchronized List<PortalRequestTrace> getList()
    {
        return ImmutableList.copyOf( currentPortalRequestTraces );
    }

    public int getSize()
    {
        return size.get();
    }
}
