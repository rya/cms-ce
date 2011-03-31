/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt.trace;

/**
 * This class implements the recursion monitor for thread local.
 */
final class RecursionMonitorThreadLocal
    extends ThreadLocal<RecursionMonitor>
{
    /**
     * Max depth.
     */
    private final int maxDepth;

    /**
     * Constructs the monitor.
     */
    public RecursionMonitorThreadLocal( int maxDepth )
    {
        this.maxDepth = maxDepth;
    }

    @Override
    protected RecursionMonitor initialValue()
    {
        return new RecursionMonitor( this.maxDepth );
    }
}
