package com.enonic.cms.api.plugin.ext;

/**
 * TaskHandler is a superclass that all scheduled tasks must implement.  The implemented class, must
 * then be declared as a spring bean, and the bean will then appear as a selection in the
 * admin console in the section for scheduling tasks.
 */
public abstract class TaskHandler
    extends ExtensionBase
{
    private String name;
    private String cron;

    /**
     * Unique qualifying name of the task. This should be globally unique.
     */
    public final String getName()
    {
        return this.name != null ? this.name : getClass().getName();
    }

    public final void setName(final String name)
    {
        this.name = name;
    }

    public final String getCron()
    {
        return this.cron;
    }

    public final void setCron(final String cron)
    {
        this.cron = cron;
    }

    /**
     * The execute method is the entry point for the task plugin.
     */
    public abstract void execute()
        throws Exception;
}
