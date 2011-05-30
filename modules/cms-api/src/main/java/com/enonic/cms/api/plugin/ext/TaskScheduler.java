package com.enonic.cms.api.plugin.ext;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.scheduling.support.CronSequenceGenerator;

/**
 * TaskScheduler is class that execute single task according
 * to the its cron config
 */
public class TaskScheduler
        extends TimerTask
{
    private TaskHandler task;

    private CronSequenceGenerator cron;

    private Timer timer;

    public TaskScheduler( TaskHandler task )
    {
        this();
        this.task = task;
        this.cron = new CronSequenceGenerator( task.getCron(), Calendar.getInstance().getTimeZone() );
    }

    public TaskScheduler()
    {
        timer = new Timer();
    }

    @Override
    public void run()
    {
        try
        {
            task.execute();
            Date executionDate = cron.next( new Date() );
            timer.schedule( this, executionDate );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    public TaskHandler getTask()
    {
        return task;
    }

    public void setTask( TaskHandler task )
    {
        this.task = task;
        this.cron = new CronSequenceGenerator( task.getCron(), Calendar.getInstance().getTimeZone() );
    }

    public void executeTask()
    {
        if ( cron != null )
        {
            Date executionDate = cron.next( new Date() );
            timer.schedule( this, executionDate );
        }

    }


}
