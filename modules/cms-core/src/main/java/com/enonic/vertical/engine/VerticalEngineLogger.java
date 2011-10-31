/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.vertical.VerticalLogger;
import com.enonic.vertical.VerticalRuntimeException;

public final class VerticalEngineLogger
    extends VerticalLogger
{
    private final static LogFacade LOG = LogFacade.get(VerticalEngineLogger.class);

    public static void errorSecurity(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalSecurityException(message, throwable);
    }

    public static void errorSecurity(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalSecurityException(format(message, msgData), throwable);
    }

    public static void errorCopy(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalRuntimeException(message, throwable);
    }

    public static void errorCreate(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalCreateException(format(message, msgData), throwable);
    }

    public static void errorCreate(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalCreateException(format(message, msgData), throwable);
    }

    public static void errorCreate(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalCreateException(message, throwable);
    }

    public static void errorRemove(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalRemoveException(format(message, msgData), throwable);
    }

    public static void errorRemove(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalRemoveException(format(message, msgData), throwable);
    }

    public static void errorRemove(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalRemoveException(message, throwable);
    }

    public static void errorUpdate(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalUpdateException(format(message, msgData), throwable);
    }

    public static void errorUpdate(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalUpdateException(format(message, msgData), throwable);
    }

    public static void errorUpdate(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalUpdateException(message, throwable);
    }

    public static void fatalEngine(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalRuntimeException(format(message, msgData), throwable);
    }

    public static void fatalEngine(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalRuntimeException(format(message, msgData), throwable);
    }

    public static void fatalEngine(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalRuntimeException(message, throwable);
    }
}
