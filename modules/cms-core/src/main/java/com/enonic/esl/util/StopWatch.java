/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

public class StopWatch
{
    protected long tStart = -1;

    protected long tEnd = -1;

    public StopWatch()
    {
    }

    public void start()
    {
        tStart = System.currentTimeMillis();
    }

    public long stop()
    {
        tEnd = System.currentTimeMillis();
        return ( tEnd - tStart );
    }

    public void reset()
    {
        tStart = 0;
        tEnd = 0;
    }
}
