/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.vertical.VerticalLogger;

public final class VerticalUserServicesLogger
    extends VerticalLogger
{
    private final static LogFacade LOG = LogFacade.get(VerticalUserServicesLogger.class);

    public static void warnUserServices( String message, Throwable throwable )
    {
        LOG.warningCause(message, throwable);
    }

    public static void errorUserServices( String message, Throwable throwable )
    {
        LOG.errorCause(message, throwable);
        throw new VerticalUserServicesException(message, throwable);
    }
}
