/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.LinkedHashSet;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Oct 7, 2010
 */
public class CurrentPortalRequests
{
    private LinkedHashSet<PortalRequestTrace> currentPortalRequestTraces = new LinkedHashSet<PortalRequestTrace>();

    public synchronized void add( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.add( trace );
    }

    public synchronized void remove( PortalRequestTrace trace )
    {
        currentPortalRequestTraces.remove( trace );
    }

    public synchronized List<PortalRequestTrace> getList()
    {
        return ImmutableList.copyOf( currentPortalRequestTraces );
    }
}
