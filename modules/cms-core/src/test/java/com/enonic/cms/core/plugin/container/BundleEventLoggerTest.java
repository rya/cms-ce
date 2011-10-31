package com.enonic.cms.core.plugin.container;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BundleEventLoggerTest
{
    private LogRecord logRecord;

    @Before
    public void setUp()
    {
        final Logger logger = Logger.getLogger(BundleEventLogger.class.getName());
        logger.setFilter(new Filter()
        {
            public boolean isLoggable(LogRecord record)
            {
                logRecord = record;
                return false;
            }
        });

        logger.setLevel(Level.FINEST);
    }

    @Test
    public void testLogStarted()
    {
        fireEvent(1, BundleEvent.STARTED);
        assertNotNull(this.logRecord);
        assertNotNull(this.logRecord.getMessage());
    }

    @Test
    public void testLogUninstall()
    {
        fireEvent(1, BundleEvent.UNINSTALLED);
        assertNotNull(this.logRecord);
        assertNotNull(this.logRecord.getMessage());
    }

    @Test
    public void testLogInstall()
    {
        fireEvent(1, BundleEvent.INSTALLED);
        assertNotNull(this.logRecord);
        assertNotNull(this.logRecord.getMessage());
    }

    @Test
    public void testNoLogFramework()
    {
        fireEvent(0, BundleEvent.INSTALLED);
        assertNull(this.logRecord);
    }

    private void fireEvent(final long bundleId, final int eventType)
    {
        final Bundle bundle = Mockito.mock(Bundle.class);
        Mockito.when(bundle.getSymbolicName()).thenReturn("some_id");
        Mockito.when(bundle.getLocation()).thenReturn("some_location");
        Mockito.when(bundle.getBundleId()).thenReturn(bundleId);

        final BundleEvent event = Mockito.mock(BundleEvent.class);
        Mockito.when(event.getBundle()).thenReturn(bundle);
        Mockito.when(event.getType()).thenReturn(eventType);

        final BundleEventLogger logger = new BundleEventLogger();
        logger.bundleChanged(event);
    }
}
