package com.enonic.cms.api.plugin.ext;

import java.util.Properties;

/**
 * TaskHandler is a superclass that all scheduled tasks must implement.  The implemented class, must
 * then be declared as a spring bean, and the bean will then appear as a selection in the
 * admin console in the section for scheduling tasks.
 */
public abstract class TaskHandler
    extends ExtensionBase
{
    private final String name;

    public TaskHandler()
    {
        this.name = getClass().getSimpleName();
    }

    /**
     * Name is an attribute that is primarly needed for future use, if we at some time might want or
     * need to distinguish between different plugins of the same class.  At this time, we
     * only instantiate it with the class name and leave it at that.
     */
    public final String getName()
    {
        return name;
    }

    /**
     * The execute method is the entry point for the task plugin.  This is the method that will be called according to the schedule set in
     * the admin console.
     *
     * @param props These properties are set in the admin console, and passed to this method for customization of the execution.
     * @throws Exception The implementing class may throw any Exception.  The exception will be printed in the Enonic CMS error log, and
     *                   execution will be terminated for the current execution, but will start over, next time the task is scheduled to run.
     */
    public abstract void execute( Properties props )
        throws Exception;
}
