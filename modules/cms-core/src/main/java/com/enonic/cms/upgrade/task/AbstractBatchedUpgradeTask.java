/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.task;

import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.upgrade.UpgradeContext;

public abstract class AbstractBatchedUpgradeTask<K>
    extends AbstractUpgradeTask
{
    public AbstractBatchedUpgradeTask( final int modelNumber )
    {
        super( modelNumber );
    }

    @Override
    final public boolean isRunTransactional()
    {
        return false;
    }

    public int getBatchSize()
    {
        return 20;
    }

    protected abstract void upgradeBatch( final UpgradeContext context, final List<K> keys )
        throws Exception;

    protected abstract List<K> getAllKeys( final UpgradeContext context )
        throws Exception;

    public void upgrade( final UpgradeContext context )
        throws Exception
    {

        final BatchedList<K> batcher = new BatchedList<K>( getAllKeys( context ), getBatchSize() );

        int batchCount = 0;

        while ( batcher.hasMoreBatches() )
        {
            final long startBatchMS = System.currentTimeMillis();

            context.logInfo( "Batch started! Batch size: " + batcher.getBatchSize() + ", Batch " + ++batchCount + " of " +
                batcher.getTotalBatchCount() );

            context.execute( new TransactionCallback()
            {
                public Object doInTransaction( TransactionStatus transactionStatus )
                {
                    try
                    {
                        upgradeBatch( context, batcher.getNextBatch() );
                    }
                    catch ( Exception ex )
                    {
                        throw new RuntimeException( ex );
                    }
                    return null;
                }
            } );
            context.logInfo( "Batch completed! Time spent: " + ( System.currentTimeMillis() - startBatchMS ) + " ms." );
        }

    }
}
