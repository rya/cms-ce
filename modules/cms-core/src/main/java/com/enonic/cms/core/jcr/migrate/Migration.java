/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Migration
{
    private static final Logger LOG = LoggerFactory.getLogger( Migration.class );

    private Future<Boolean> futureResult;

    @Autowired
    private MigrationProcess migrationProcess;

    public boolean isInProgress()
    {
        return ( futureResult != null ) && ( !futureResult.isDone() );
    }

    public void start()
    {
        if ( isInProgress() )
        {
            return;
        }

        LOG.info( "Starting migration" );
        ExecutorService executor = Executors.newSingleThreadExecutor();
        futureResult = executor.submit( migrationProcess );
    }

    public boolean isSuccessful()
    {
        if ( ( futureResult == null ) || ( !futureResult.isDone() ) )
        {
            return false;
        }
        try
        {
            return futureResult.get( 0, TimeUnit.NANOSECONDS );
        }
        catch ( InterruptedException e )
        {
            return false;
        }
        catch ( ExecutionException e )
        {
            return false;
        }
        catch ( TimeoutException e )
        {
            return false;
        }
    }

}
