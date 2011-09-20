/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.jcr.migrate;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class LogEntry
{
    private final LogLevel level;

    private final String message;

    private final Throwable cause;

    public LogEntry( LogLevel level, String message, Throwable cause )
    {
        this.level = level;
        this.message = message;
        this.cause = cause;
    }

    public LogEntry( LogLevel level, String message )
    {
        this.level = level;
        this.message = message;
        this.cause = null;
    }

    public LogLevel getLevel()
    {
        return level;
    }

    public String getMessage()
    {
        return message;
    }

    public Throwable getCause()
    {
        return cause;
    }

    public String getStacktrace()
    {
        if ( cause == null )
        {
            return null;
        }
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter( sw );
        cause.printStackTrace( pw );
        return sw.toString();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append( "[" ).append( level.name() ).append( "] " );
        sb.append( message );
        return sb.toString();
    }

}