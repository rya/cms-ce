package com.enonic.cms.core.plugin.logger;

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
    private Bundle bundle;

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

        this.bundle = Mockito.mock(Bundle.class);
        Mockito.when(this.bundle.getSymbolicName()).thenReturn("some_id");
        Mockito.when(this.bundle.getLocation()).thenReturn("some_location");
    }

    @Test
    public void testLogStarted()
    {
        testLog(BundleEvent.STARTED);
    }

    @Test
    public void testLogUninstall()
    {
        testLog(BundleEvent.UNINSTALLED);
    }

    @Test
    public void testLogInstall()
    {
        testLog(BundleEvent.INSTALLED);
    }

    private void testLog(final int eventType)
    {
        final BundleEvent event = Mockito.mock(BundleEvent.class);
        Mockito.when(event.getBundle()).thenReturn(this.bundle);
        Mockito.when(event.getType()).thenReturn(eventType);

        final BundleEventLogger logger = new BundleEventLogger();
        logger.bundleChanged(event);

        assertNotNull(this.logRecord);
        assertNotNull(this.logRecord.getMessage());
    }
}
