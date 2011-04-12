/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.tracing;

/**
 * This class implements the abstract render trace info.
 */
public abstract class TraceInfo
{
    /**
     * Start time.
     */
    private long startTime;

    /**
     * Total time.
     */
    private long totalTime;

    /**
     * Return the total time.
     */
    public long getTotalTime()
    {
        return this.totalTime;
    }

    /**
     * Enter the trace.
     */
    public void enter()
    {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Exit the trace.
     */
    public void exit()
    {
        this.totalTime = System.currentTimeMillis() - this.startTime;
    }
}
