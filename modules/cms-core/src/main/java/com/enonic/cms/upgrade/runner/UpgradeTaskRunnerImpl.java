/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.runner;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.task.UpgradeTask;

public final class UpgradeTaskRunnerImpl
    implements UpgradeTaskRunner
{
    private final TransactionTemplate template;

    public UpgradeTaskRunnerImpl( TransactionTemplate template )
    {
        this.template = template;
    }

    private void doRunUpgradeTask( UpgradeContext context, UpgradeTask task )
        throws Throwable
    {
        try
        {
            task.upgrade( context );
            context.updateModelNumber( task.getModelNumber() );
        }
        catch ( Throwable ue )
        {
            context.logError( ue.getMessage(), ue );

            throw ue;
        }
    }

    public void runUpgradeTask( UpgradeContext context, UpgradeTask task )
        throws Throwable
    {
        doRunUpgradeTask( context, task );
    }

    public void runUpgradeTaskInTx( final UpgradeContext context, final UpgradeTask task )
        throws Throwable
    {

        Throwable error = (Throwable) context.execute( new TransactionCallback()
        {
            public Object doInTransaction( TransactionStatus status )
            {
                try
                {
                    doRunUpgradeTask( context, task );
                    return null;
                }
                catch ( Throwable e )
                {
                    status.setRollbackOnly();
                    return e;
                }
            }
        } );

        if ( error != null )
        {
            throw error;
        }
    }
}
