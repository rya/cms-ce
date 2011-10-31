/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.vertical.VerticalLogger;

public class VerticalAdminLogger
    extends VerticalLogger
{
    private final static LogFacade LOG = LogFacade.get(VerticalAdminLogger.class);

    public static void errorAdmin(String message, Object[] msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalAdminException(format(message, msgData), throwable);
    }

    public static void errorAdmin(String message, Object msgData, Throwable throwable)
    {
        LOG.errorCause(message, throwable, msgData);
        throw new VerticalAdminException(format(message, msgData), throwable);
    }

    public static void errorAdmin(String message, Throwable throwable)
    {
        LOG.errorCause(message, throwable);
        throw new VerticalAdminException(message, throwable);
    }
}

