package com.enonic.cms.core.plugin.logger;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

public class LoggerActivatorTest
{
    @Test
    public void testActivate()
        throws Exception
    {
        final BundleContext context = Mockito.mock(BundleContext.class);
        final LoggerActivator activator = new LoggerActivator();

        activator.start(context);

        Mockito.verify(context, Mockito.times(1)).addBundleListener(Mockito.any(BundleEventLogger.class));

        activator.stop(context);
    }
}
