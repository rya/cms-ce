/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical;

import com.enonic.cms.api.util.LogFacade;

public class VerticalLogger
{
    private final static LogFacade LOG = LogFacade.get(VerticalLogger.class);

    public static void debug(String message, Throwable throwable)
    {
        LOG.debugCause(message, throwable);
    }

    public static void info(String message, Object msgData, Throwable throwable)
    {
        LOG.infoCause(message, throwable, msgData);
    }

    public static void warn(String message, Object[] msgData, Throwable throwable)
    {
        LOG.warningCause(message, throwable, msgData);
    }

    public static void warn(String message, Object msgData, Throwable throwable)
    {
        LOG.warningCause(message, throwable, msgData);
    }

    public static void warn(String message, Throwable throwable)
    {
        LOG.warningCause(message, throwable);
    }

    public static void error(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
    }

    public static void error(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
    }

    public static void error(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
    }
}
