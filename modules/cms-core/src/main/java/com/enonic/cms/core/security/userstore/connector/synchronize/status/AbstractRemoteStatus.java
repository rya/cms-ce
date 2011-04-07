/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.synchronize.status;

import java.util.concurrent.atomic.AtomicInteger;

public class AbstractRemoteStatus
{
    /* Progress */
    private final AtomicInteger totalCount = new AtomicInteger( -1 );

    private final AtomicInteger currentCount = new AtomicInteger( -1 );

    /* Actions */
    private final AtomicInteger createdCount = new AtomicInteger( 0 );

    private final AtomicInteger updatedCount = new AtomicInteger( 0 );

    private final AtomicInteger resurrectedCount = new AtomicInteger( 0 );

    private final AtomicInteger skippedCount = new AtomicInteger( 0 );

    public void setTotalCount( final int value )
    {
        totalCount.set( value );
        currentCount.set( 0 );
    }

    public int getTotalCount()
    {
        return totalCount.get();
    }

    public int getCurrentCount()
    {
        return currentCount.get();
    }

    public int getCreatedCount()
    {
        return createdCount.get();
    }

    public int getUpdatedCount()
    {
        return updatedCount.get();
    }

    public int getResurrectedCount()
    {
        return resurrectedCount.get();
    }

    public int getSkippedCount()
    {
        return skippedCount.get();
    }

    public void created()
    {
        createdCount.incrementAndGet();
        currentCount.incrementAndGet();
    }

    public void updated( final boolean resurrected )
    {
        updatedCount.incrementAndGet();
        currentCount.incrementAndGet();
        if ( resurrected )
        {
            resurrectedCount.incrementAndGet();
        }
    }

    public void skipped()
    {
        skippedCount.incrementAndGet();
        currentCount.incrementAndGet();
    }

    public boolean inProgress()
    {
        int total = totalCount.get();
        return total > -1 && total != currentCount.get();
    }
}
