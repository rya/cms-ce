package com.enonic.cms.core.plugin.container;

import java.util.Map;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;

public final class FelixOsgiContainer
    extends OsgiContainer
    implements FelixConstants
{
    private Felix felix = null;

    @Override
    public void start()
        throws Exception
    {
        if ( isRunning() )
        {
            return;
        }

        try
        {
            doStart();
        }
        catch ( Exception e )
        {
            this.felix = null;
            throw e;
        }
    }

    @Override
    public void stop()
        throws Exception
    {
        if ( !isRunning() )
        {
            return;
        }

        try
        {
            doStop();
        }
        finally
        {
            this.felix = null;
        }
    }

    private boolean isRunning()
    {
        return this.felix != null;
    }

    private void doStart()
        throws Exception
    {
        this.felix = new Felix( getConfigMap() );
        this.logger.info( "Starting Felix OSGi Container ({0})", this.felix.getVersion() );
        this.felix.start();

        this.logger.info( "OSGi container started and running" );
    }

    private void doStop()
        throws Exception
    {
        this.felix.stop();
        this.felix.waitForStop( 5000 );
    }

    @Override
    protected Map<String, Object> getConfigMap()
        throws Exception
    {
        final FelixLogBridge logBridge = new FelixLogBridge();

        final Map<String, Object> map = super.getConfigMap();
        map.put( LOG_LEVEL_PROP, String.valueOf( logBridge.getLogLevel() ) );
        map.put( LOG_LOGGER_PROP, logBridge );
        map.put( IMPLICIT_BOOT_DELEGATION_PROP, "false" );
        map.put( SYSTEMBUNDLE_ACTIVATORS_PROP, getActivators() );

        return map;
    }
}
