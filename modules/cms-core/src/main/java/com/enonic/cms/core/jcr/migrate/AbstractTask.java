/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.text.MessageFormat;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.enonic.cms.core.jdbc.JdbcDynaResult;

public abstract class AbstractTask
{
    private final StopWatch stopWatch;
    private JdbcTemplate template;
    private Log log;

    public AbstractTask()
    {
        this.stopWatch = new StopWatch();
    }

    public final void setTemplate(final JdbcTemplate template)
    {
        this.template = template;
    }

    protected final JdbcDynaResult jdbcQuery(final String sql, final Object... args)
    {
        final SqlRowSet rowSet = this.template.queryForRowSet(sql, args);
        return new JdbcDynaResult(rowSet);
    }

    protected final int jdbcQueryInt(final String sql, final Object... args)
    {
        return this.template.queryForInt(sql, args);
    }

    protected final void logInfo(final String message, final Object... args)
    {
        if ( log == null )
        {
            System.out.println( MessageFormat.format( message, args ) );
        }
        else
        {
            log.logInfo( MessageFormat.format( message, args ) );
        }
    }

    protected final void logWarning(final String message, final Object... args)
    {
        if ( log == null )
        {
            System.err.println( MessageFormat.format( message, args ) );
        }
        else
        {
            log.logWarning( MessageFormat.format( message, args ) );
        }
    }

    protected final void logStart(final String message, final Object... args)
    {
        logInfo(message, args);
        this.stopWatch.reset();
        this.stopWatch.start();
    }

    protected final void logStop(final String message, final Object... args)
    {
        this.stopWatch.stop();
        logInfo(message + " (" + this.stopWatch.getTime() + " ms)", args);
    }

    public abstract void execute();

    public void setLog( Log log )
    {
        this.log = log;
    }
}
