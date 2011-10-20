package com.enonic.cms.core.task;

import java.util.Date;
import java.util.TimeZone;

import org.springframework.scheduling.support.CronSequenceGenerator;

import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.util.LogFacade;

final class ScheduledTask
    implements Runnable
{
    private final static LogFacade LOG = LogFacade.get( ScheduledTask.class );

    private final String id;

    private final String cron;

    private final TaskHandler taskHandler;

    private Date lastActualExecutionTime;

    private Date lastCompletionTime;

    private Date lastFailedCompletionTime;

    private CronSequenceGenerator cronGenerator;

    public ScheduledTask( TaskHandler taskHandler )
    {
        this.id = taskHandler.getName();
        this.cron = taskHandler.getCron();
        this.taskHandler = taskHandler;
    }

    public String getId()
    {
        return id;
    }

    public void initCronSequenceGenerator()
    {
        cronGenerator = new CronSequenceGenerator( this.cron, TimeZone.getDefault() );
    }

    public Date getNextExecutionTime()
    {
        return this.cronGenerator.next( new Date() );
    }

    public void run()
    {
        try
        {
            this.lastActualExecutionTime = new Date();
            taskHandler.execute();
            this.lastCompletionTime = new Date();
        }
        catch ( Exception e )
        {
            lastFailedCompletionTime = new Date();
            LOG.warningCause("Task invocation failed with exception for TaskHandler: " + taskHandler.getName(), e);

        }
    }

    public Date getLastActualExecutionTime()
    {
        return lastActualExecutionTime;
    }

    public Date getLastCompletionTime()
    {
        return lastCompletionTime;
    }

    public Date getLastFailedCompletionTime()
    {
        return lastFailedCompletionTime;
    }
}
