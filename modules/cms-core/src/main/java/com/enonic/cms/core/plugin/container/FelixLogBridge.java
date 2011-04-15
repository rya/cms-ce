package com.enonic.cms.core.plugin.container;

import org.apache.felix.framework.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import com.enonic.cms.api.util.LogFacade;

final class FelixLogBridge
    extends Logger
{
    private final static LogFacade LOG = LogFacade.get( FelixLogBridge.class );

    private final static String FQCN = FelixLogBridge.class.getName();

    public FelixLogBridge()
    {
        if ( LOG.isDebugEnabled() )
        {
            setLogLevel( LOG_DEBUG );
        }
        else if ( LOG.isInfoEnabled() )
        {
            setLogLevel( LOG_INFO );
        }
        else if ( LOG.isWarningEnabled() )
        {
            setLogLevel( LOG_WARNING );
        }
        else
        {
            setLogLevel( LOG_ERROR );
        }
    }

    protected void doLog( final Bundle bundle, final ServiceReference sr, final int level, final String msg, final Throwable cause )
    {
        String message = msg;
        if ( sr != null )
        {
            message = message + " (" + sr.toString() + ")";
        }

        if ( level == LOG_ERROR )
        {
            LOG.errorFqcn( FQCN, cause, message );
        }
        else if ( level == LOG_INFO )
        {
            LOG.infoFqcn( FQCN, cause, message );
        }
        else if ( level == LOG_WARNING )
        {
            LOG.warningFqcn( FQCN, cause, message );
        }
        else
        {
            LOG.debugFqcn( FQCN, cause, message );
        }
    }
}
