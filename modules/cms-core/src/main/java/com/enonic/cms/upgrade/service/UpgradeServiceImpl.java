/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.enonic.cms.framework.jdbc.dialect.Dialect;

import com.enonic.cms.store.DatabaseAccessor;
import com.enonic.cms.store.support.ConnectionFactory;
import com.enonic.cms.upgrade.UpgradeContext;
import com.enonic.cms.upgrade.UpgradeException;
import com.enonic.cms.upgrade.UpgradeService;
import com.enonic.cms.upgrade.log.UpgradeLog;
import com.enonic.cms.upgrade.runner.UpgradeTaskRunner;
import com.enonic.cms.upgrade.runner.UpgradeTaskRunnerImpl;
import com.enonic.cms.upgrade.task.ReflectionUpgradeTaskLocator;
import com.enonic.cms.upgrade.task.UpgradeTask;
import com.enonic.cms.upgrade.task.UpgradeTaskLocator;

@Transactional
public final class UpgradeServiceImpl
    implements UpgradeService, InitializingBean
{
    private SqlOperationHelper sqlHelper;

    private int currentModelNumber = -1;

    private int targetModelNumber = -1;

    private UpgradeTaskLocator upgradeTaskLocator;

    private Dialect dialect;

    private ConnectionFactory connectionFactory;

    private List<UpgradeTask> upgradeTasks;

    private PropertyResolver propertyResolver;

    private UpgradeTaskRunner upgradeTaskRunner;

    private TransactionTemplate transactionTemplate;

    public void setDialect( Dialect dialect )
    {
        this.dialect = dialect;
    }

    public void setConnectionFactory( ConnectionFactory connectionFactory )
    {
        this.connectionFactory = connectionFactory;
    }

    public void setPropertyResolver( PropertyResolver propertyResolver )
    {
        this.propertyResolver = propertyResolver;
    }

    public void setTransactionTemplate( TransactionTemplate transactionTemplate )
    {
        this.transactionTemplate = transactionTemplate;
    }

    public void afterPropertiesSet()
        throws Exception
    {
        if ( this.upgradeTaskLocator == null )
        {
            this.upgradeTaskLocator = new ReflectionUpgradeTaskLocator();
        }

        this.upgradeTaskRunner = new UpgradeTaskRunnerImpl( this.transactionTemplate );
        this.sqlHelper = new SqlOperationHelper( this.dialect, this.connectionFactory, this.transactionTemplate );
        this.upgradeTasks = this.upgradeTaskLocator.getTasks();
        Collections.sort( this.upgradeTasks );
    }

    public boolean needsUpgrade()
    {
        final int current = getCurrentModelNumber();
        final int target = getTargetModelNumber();
        return current < target;
    }

    public boolean needsSoftwareUpgrade()
    {
        return getTargetModelNumber() < getCurrentModelNumber();
    }

    public boolean needsOldUpgradeSystem()
    {
        return getCurrentModelNumber() < 0;
    }

    public int getCurrentModelNumber()
    {
        try
        {
            if ( this.currentModelNumber < 0 )
            {
                this.currentModelNumber = this.sqlHelper.getModelNumber();
            }

            return this.currentModelNumber;
        }
        catch ( Exception e )
        {
            throw new IllegalStateException( "Failed to find target model number", e );
        }
    }

    public int getTargetModelNumber()
    {
        if ( this.targetModelNumber < 0 )
        {
            this.targetModelNumber = DatabaseAccessor.getLatestDatabase().getVersion();
        }

        return this.targetModelNumber;
    }

    public boolean upgrade( UpgradeLog log )
        throws UpgradeException
    {
        return doUpgrade( log, false );
    }

    public boolean upgradeStep( UpgradeLog log )
        throws UpgradeException
    {
        return doUpgrade( log, true );
    }

    private boolean doUpgrade( UpgradeLog log, boolean single )
        throws UpgradeException
    {
        try
        {
            if ( needsUpgrade() && !needsOldUpgradeSystem() )
            {
                int targetModel = single ? getNextModelNumber() : getTargetModelNumber();
                boolean canUpgrade = canUpgrade( log, getCurrentModelNumber(), getTargetModelNumber() );
                if ( canUpgrade )
                {
                    doUpgrade( log, getCurrentModelNumber(), targetModel );
                    return true;
                }
            }
            return false;
        }
        finally
        {
            this.currentModelNumber = -1;
            this.targetModelNumber = -1;
        }
    }

    private int getNextModelNumber()
    {
        int currentModelNumber = getCurrentModelNumber();

        int lowestNewModelNumber = getTargetModelNumber();

        for ( UpgradeTask task : this.upgradeTasks )
        {
            int modelNumber = task.getModelNumber();

            if ( ( modelNumber > currentModelNumber ) && ( modelNumber < lowestNewModelNumber ) )
            {
                lowestNewModelNumber = modelNumber;
            }
        }

        return lowestNewModelNumber;
    }

    private void doUpgrade( UpgradeLog log, int fromModel, int toModel )
        throws UpgradeException
    {
        try
        {
            doUpgrade( createContext( log ), fromModel, toModel );
        }
        catch ( Throwable e )
        {
            throw new UpgradeException( "Upgrade from " + fromModel + " to " + toModel + " failed", e );
        }
    }

    private void doUpgrade( UpgradeContext context, int fromModel, int toModel )
        throws Throwable
    {
        context.logInfo( "Upgrading from " + fromModel + " to " + toModel );
        for ( UpgradeTask task : this.upgradeTasks )
        {
            int modelNumber = task.getModelNumber();

            if ( ( modelNumber > fromModel ) && ( modelNumber <= toModel ) )
            {
                doUpgradeStep( context, task );
            }
        }

        context.logInfo( "Upgrade was successful" );
    }

    private void doUpgradeStep( UpgradeContext context, UpgradeTask task )
        throws Throwable
    {
        context.setCurrentModelNumber( task.getModelNumber() );
        context.logInfo( "Upgrading to model " + task.getModelNumber() );
        long start = System.currentTimeMillis();
        try
        {
            if ( task.isRunTransactional() )
            {
                this.upgradeTaskRunner.runUpgradeTaskInTx( context, task );
            }
            else
            {
                this.upgradeTaskRunner.runUpgradeTask( context, task );
            }
        }
        catch ( Throwable e )
        {
            context.logError(
                "Upgrade to " + task.getModelNumber() + " failed.  Time spent: " + ( System.currentTimeMillis() - start ) + " ms.", e );
            throw e;
        }
        context.logInfo(
            "Upgrade to " + task.getModelNumber() + " was successful. Time spent: " + ( System.currentTimeMillis() - start + " ms." ) );
    }

    private UpgradeContext createContext( UpgradeLog log )
    {
        return new UpgradeContextImpl( log, this.propertyResolver, this.sqlHelper, getCurrentModelNumber() );
    }

    /**
     * Check if you can upgrade.
     */
    public boolean canUpgrade( UpgradeLog log )
    {
        return canUpgrade( log, getCurrentModelNumber(), getTargetModelNumber() );
    }

    private boolean canUpgrade( UpgradeLog log, int fromModel, int toModel )
    {
        UpgradeContext context = createContext( log );
        context.logInfo( "Checking if your current model #" + fromModel + " is upgradeable to model #" + toModel + " ..." );
        boolean canUpgrade = true;
        for ( UpgradeTask task : this.upgradeTasks )
        {
            int modelNumber = task.getModelNumber();
            if ( ( modelNumber > fromModel ) && ( modelNumber <= toModel ) )
            {
                context.setCurrentModelNumber( modelNumber );
                if ( !task.canUpgrade( context ) )
                {
                    context.logError( "Upgrade check NOT OK" );
                    canUpgrade = false;
                }
                else
                {
                    context.logInfo( "Upgrade check OK" );
                }
            }
        }
        context.setCurrentModelNumber( -1 );
        if ( canUpgrade )
        {
            context.logInfo( "Upgrade check completed. No issues found." );
        }
        else
        {
            context.logError( "Cannot upgrade. Please resolve the upgrade issues listed by each model and try again." );
        }
        return canUpgrade;
    }
}
