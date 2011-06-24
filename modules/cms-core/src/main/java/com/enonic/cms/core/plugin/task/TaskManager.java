package com.enonic.cms.core.plugin.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.ExtensionListener;


/**
 * Created by IntelliJ IDEA.
 * User: rmy
 * Date: 6/23/11
 * Time: 12:43 PM
 */
final class TaskManager
    implements ExtensionListener, DisposableBean
{
    private final static LogFacade LOG = LogFacade.get( TaskManager.class );

    private final Map<String, ScheduledTask> tasks;

    private Map<String, LocalTask> taskQueue;

    private final Timer timer;

    public TaskManager()
    {
        this.tasks = new HashMap<String, ScheduledTask>();
        this.taskQueue = new HashMap<String, LocalTask>();
        this.timer = new Timer();
    }

    public void destroy()
        throws Exception
    {
        this.timer.cancel();
    }

    public void extensionAdded( final Extension ext )
    {
        if ( ext instanceof TaskHandler )
        {
            LOG.info( "Scheduled task added: " + ( (TaskHandler) ext ).getName() );

            addTask( (TaskHandler) ext );
        }
    }

    public void extensionRemoved( Extension ext )
    {
        if ( ext instanceof TaskHandler )
        {
            LOG.info( "Scheduled task removed: " + ( (TaskHandler) ext ).getName() );

            removeTask( ( (TaskHandler) ext ).getName() );
        }
    }

    private void addTask( final TaskHandler taskHandler )
    {
        if ( taskExists( taskHandler.getName() ) )
        {
            return;
        }

        if ( StringUtils.isEmpty( taskHandler.getCron() ) )
        {
            LOG.warning( "No schedule definition for taskHandler: '" + taskHandler.getName() + "' found. Task not scheduled" );
            return;
        }

        ScheduledTask task = new ScheduledTask( taskHandler );

        try
        {
            task.initCronSequenceGenerator();
        }
        catch ( Exception e )
        {
            LOG.warning( "Illegal cron-definition for taskHandler: '" + taskHandler.getName() + "'. Task not scheduled" );
            return;
        }

        this.tasks.put( task.getId(), task );

        schedule( task );

    }


    private void removeTask( final String taskId )
    {
        this.tasks.remove( taskId );

        LocalTask queuedTask = this.taskQueue.get( taskId );

        if ( queuedTask != null )
        {
            queuedTask.cancel();
            this.taskQueue.remove( taskId );
        }
    }


    private void schedule( ScheduledTask task )
    {
        if ( !taskExists( task.getId() ) )
        {
            return;
        }

        final LocalTask localTask = new LocalTask( task );
        this.timer.schedule( localTask, task.getNextExecutionTime() );
        this.taskQueue.put( task.getId(), localTask );

    }

    private boolean taskExists( String id )
    {
        return this.tasks.containsKey( id );
    }

    private void execute( String id )
    {
        ScheduledTask task = tasks.get( id );

        if ( task == null )
        {
            return;
        }

        task.run();
    }

    private final class LocalTask
        extends TimerTask
    {
        private final ScheduledTask entry;

        public LocalTask( final ScheduledTask task )
        {
            this.entry = task;
        }

        @Override
        public void run()
        {
            try
            {
                execute( this.entry.getId() );
            }
            finally
            {
                schedule( this.entry );
            }
        }
    }

    protected int numberOfTasks()
    {
        return this.tasks.size();
    }
}

