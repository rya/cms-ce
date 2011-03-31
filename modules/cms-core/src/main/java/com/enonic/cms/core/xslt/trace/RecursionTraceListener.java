/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.trace;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trace.InstructionInfo;
import net.sf.saxon.trace.TraceListener;

/**
 * This class counts the number of recursions and either log or throws an exception when recursion depth is exceeded.
 */
public final class RecursionTraceListener
    implements TraceListener
{
    /**
     * Monitor for current thread.
     */
    private final RecursionMonitorThreadLocal monitor;

    /**
     * Constructs the listener.
     */
    public RecursionTraceListener( int maxDepth )
    {
        this.monitor = new RecursionMonitorThreadLocal( maxDepth );
    }

    public void open()
    {
        // Do nothing
    }

    public void close()
    {
        // Do nothing
    }

    public void enter( InstructionInfo info, XPathContext context )
    {
        this.monitor.get().enter( info );
    }

    public void leave( InstructionInfo info )
    {
        this.monitor.get().leave( info );
    }

    public void startCurrentItem( Item item )
    {
        // Do nothing
    }

    public void endCurrentItem( Item item )
    {
        // Do nothing
    }
}
