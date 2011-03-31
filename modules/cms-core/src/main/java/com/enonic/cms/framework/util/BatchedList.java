/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.List;

public class BatchedList<T>
{
    private final List<T> sourceList;

    private final int totalSize;

    private final int batchSize;

    private int lastPosition;

    public int getBatchSize()
    {
        return batchSize;
    }

    public int getTotalBatchCount()
    {
        return ( totalSize / batchSize ) + ( totalSize % batchSize == 0 ? 0 : 1 );
    }

    public BatchedList( final List<T> sourceList, final int batchSize )
    {
        if ( sourceList == null )
        {
            throw new IllegalArgumentException( "Given sourceList cannot be null." );
        }
        this.sourceList = sourceList;
        this.totalSize = sourceList.size();
        this.batchSize = batchSize;
    }

    public List<T> getNextBatch()
    {
        if ( lastPosition > totalSize )
        {
            return null;
        }

        final List<T> nextBatch =
            sourceList.subList( lastPosition, lastPosition + batchSize < totalSize ? lastPosition + batchSize : totalSize );

        lastPosition += batchSize;

        return nextBatch;
    }

    public boolean hasMoreBatches()
    {
        return !( lastPosition > totalSize );
    }
}
