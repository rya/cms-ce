package com.enonic.cms.core.plugin.installer;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.service.url.URLStreamHandlerService;
import java.util.Dictionary;

public class InstallerActivatorTest
{
    @Test
    public void testStartup()
        throws Exception
    {
        final BundleContext context = Mockito.mock(BundleContext.class);
        final InstallerActivator activator = new InstallerActivator();

        activator.start(context);

        Mockito.verify(context, Mockito.times(1)).registerService(
                Mockito.eq(URLStreamHandlerService.class.getName()),
                Mockito.any(TransformerStreamHandler.class), Mockito.any(Dictionary.class));

        Mockito.verify(context, Mockito.times(1)).registerService(
                Mockito.eq(BundleInstaller.class.getName()),
                Mockito.any(BundleInstaller.class), Mockito.any(Dictionary.class));

        activator.stop(context);
    }
}
