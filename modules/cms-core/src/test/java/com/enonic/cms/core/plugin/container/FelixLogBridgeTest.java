package com.enonic.cms.core.plugin.container;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import java.util.logging.*;

public class FelixLogBridgeTest
{
    private LogRecord logRecord;
    private Logger logger;

    @Before
    public void setUp()
    {
        this.logger = Logger.getLogger(FelixLogBridge.class.getName());
        this.logger.setFilter(new Filter()
        {
            public boolean isLoggable(LogRecord record)
            {
                logRecord = record;
                return false;
            }
        });

        this.logger.setLevel(Level.FINEST);
    }

    @Test
    public void testLogDebug()
    {
        testLog(FelixLogBridge.LOG_DEBUG, Level.FINEST, false);
        testLog(FelixLogBridge.LOG_DEBUG, Level.FINEST, true);
    }

    @Test
    public void testLogInfo()
    {
        testLog(FelixLogBridge.LOG_INFO, Level.INFO, false);
        testLog(FelixLogBridge.LOG_INFO, Level.INFO, true);
    }

    @Test
    public void testLogWarning()
    {
        testLog(FelixLogBridge.LOG_WARNING, Level.WARNING, false);
        testLog(FelixLogBridge.LOG_WARNING, Level.WARNING, true);
    }

    @Test
    public void testLogError()
    {
        testLog(FelixLogBridge.LOG_ERROR, Level.SEVERE, false);
        testLog(FelixLogBridge.LOG_ERROR, Level.SEVERE, true);
    }

    private void testLog(final int level, final Level realLevel, final boolean useServiceRef)
    {
        final Throwable cause = new Throwable();
        final Bundle bundle = Mockito.mock(Bundle.class);
        final ServiceReference ref = Mockito.mock(ServiceReference.class);
        Mockito.when(ref.toString()).thenReturn("ServiceReference");

        this.logger.setLevel(realLevel);

        final FelixLogBridge felixLogger = new FelixLogBridge();
        felixLogger.setLogLevel(level);

        felixLogger.doLog(bundle, useServiceRef ? ref : null, level, "Message", cause);

        assertEquals(realLevel, this.logRecord.getLevel());

        if (useServiceRef) {
            assertEquals("Message (ServiceReference)", this.logRecord.getMessage());
        } else {
            assertEquals("Message", this.logRecord.getMessage());
        }

        assertSame(cause, this.logRecord.getThrown());
    }
}
