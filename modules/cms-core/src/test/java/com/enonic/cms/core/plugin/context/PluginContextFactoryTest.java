package com.enonic.cms.core.plugin.context;

import com.enonic.cms.api.plugin.PluginContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;

import static org.junit.Assert.*;

public class PluginContextFactoryTest
{
    private PluginContextFactory factory;

    @Before
    public void setUp()
    {
        this.factory = new PluginContextFactory();
    }

    @Test
    public void testGetService()
    {
        final Bundle bundle = Mockito.mock(Bundle.class);
        final Object service = this.factory.getService(bundle, null);

        assertNotNull(service);
        assertTrue(service instanceof PluginContext);
    }

    @Test
    public void testUnGetService()
    {
        this.factory.ungetService(null, null, null);
    }
}
