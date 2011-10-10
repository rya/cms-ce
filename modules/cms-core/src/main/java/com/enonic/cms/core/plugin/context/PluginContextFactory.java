package com.enonic.cms.core.plugin.context;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

final class PluginContextFactory
    implements ServiceFactory
{
    public Object getService( final Bundle bundle, final ServiceRegistration reg )
    {
        return new PluginContextImpl( bundle );
    }

    public void ungetService( final Bundle bundle, final ServiceRegistration reg, final Object o )
    {
        // Do nothing
    }
}
