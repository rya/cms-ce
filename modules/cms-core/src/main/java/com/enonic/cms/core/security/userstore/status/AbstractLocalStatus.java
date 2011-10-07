/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.status;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractLocalStatus
{
    /* Progress */
    private final AtomicInteger totalCount = new AtomicInteger( -1 );

    private final AtomicInteger currentCount = new AtomicInteger( -1 );

    /* Actions */
    private final AtomicInteger deletedCount = new AtomicInteger( 0 );

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

    public int getDeletedCount()
    {
        return deletedCount.get();
    }

    public void deleted()
    {
        deletedCount.incrementAndGet();
        currentCount.incrementAndGet();
    }

    public boolean inProgress()
    {
        int total = totalCount.get();
        return total > -1 && total != currentCount.get();
    }
}