/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Migration
{
    private final Log log;

    private Future<Boolean> futureResult;

    @Autowired
    private MigrationProcess migrationProcess;

    Migration()
    {
        log = new MigrationLog();
    }

    @PostConstruct
    private void init()
    {
        migrationProcess.setLog( log );
    }

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

        log.clear();
        log.logInfo( "Starting migration..." );
        final ExecutorService executor = Executors.newSingleThreadExecutor();
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

    public List<LogEntry> getLogEntries()
    {
        return log.getEntries();
    }
}
