package com.enonic.cms.api.util;

import org.junit.Before;
import org.junit.Test;
import java.util.logging.*;
import static org.junit.Assert.*;

public class LogFacadeTest
{
    private LogRecord logRecord;
    private Logger logger;

    @Before
    public void setUp()
    {
        this.logger = Logger.getLogger(LogFacadeTest.class.getName());
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
    public void testIsDebugEnabled()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        this.logger.setLevel(Level.SEVERE);
        assertFalse(facade.isDebugEnabled());

        this.logger.setLevel(Level.FINEST);
        assertTrue(facade.isDebugEnabled());
    }

    @Test
    public void testIsInfoEnabled()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        this.logger.setLevel(Level.SEVERE);
        assertFalse(facade.isInfoEnabled());

        this.logger.setLevel(Level.FINEST);
        assertTrue(facade.isInfoEnabled());
    }

    @Test
    public void testIsWarningEnabled()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        this.logger.setLevel(Level.SEVERE);
        assertFalse(facade.isWarningEnabled());

        this.logger.setLevel(Level.FINEST);
        assertTrue(facade.isWarningEnabled());
    }

    @Test
    public void testLogDebug()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        facade.debug("A message");
        assertLogRecord("A message", Level.FINEST, "testLogDebug", false);

        facade.debug("A {0} message", "nice");
        assertLogRecord("A nice message", Level.FINEST, "testLogDebug", false);

        facade.debug(new Throwable(), "A message");
        assertLogRecord("A message", Level.FINEST, "testLogDebug", true);

        facade.debug(new Throwable(), "A {0} message", "nice");
        assertLogRecord("A nice message", Level.FINEST, "testLogDebug", true);
    }

    @Test
    public void testLogInfo()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        facade.info("A message");
        assertLogRecord("A message", Level.INFO, "testLogInfo", false);

        facade.info("A {0} message", "nice");
        assertLogRecord("A nice message", Level.INFO, "testLogInfo", false);

        facade.info(new Throwable(), "A message");
        assertLogRecord("A message", Level.INFO, "testLogInfo", true);

        facade.info(new Throwable(), "A {0} message", "nice");
        assertLogRecord("A nice message", Level.INFO, "testLogInfo", true);
    }

    @Test
    public void testLogWarning()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        facade.warning("A message");
        assertLogRecord("A message", Level.WARNING, "testLogWarning", false);

        facade.warning("A {0} message", "nice");
        assertLogRecord("A nice message", Level.WARNING, "testLogWarning", false);

        facade.warning(new Throwable(), "A message");
        assertLogRecord("A message", Level.WARNING, "testLogWarning", true);

        facade.warning(new Throwable(), "A {0} message", "nice");
        assertLogRecord("A nice message", Level.WARNING, "testLogWarning", true);
    }

    @Test
    public void testLogError()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        facade.error("A message");
        assertLogRecord("A message", Level.SEVERE, "testLogError", false);

        facade.error("A {0} message", "nice");
        assertLogRecord("A nice message", Level.SEVERE, "testLogError", false);

        facade.error(new Throwable(), "A message");
        assertLogRecord("A message", Level.SEVERE, "testLogError", true);

        facade.error(new Throwable(), "A {0} message", "nice");
        assertLogRecord("A nice message", Level.SEVERE, "testLogError", true);
    }

    @Test
    public void testLogNotEnabled()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class);

        this.logger.setLevel(Level.SEVERE);
        facade.debug("A message");
        assertNull(this.logRecord);
    }

    @Test
    public void testFqcnNotFound()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class, String.class);

        facade.debug("A message");
        assertNotNull(this.logRecord);
        assertNull(this.logRecord.getSourceMethodName());
    }

    @Test
    public void testValidFqcn()
    {
        final LogFacade facade = LogFacade.get(LogFacadeTest.class, LogFacade.class);

        facade.debug("A message");
        assertNotNull(this.logRecord);
        assertEquals("testValidFqcn", this.logRecord.getSourceMethodName());
    }

    private void assertLogRecord(String message, Level level, String methodName, boolean hasThrown)
    {
        assertNotNull(this.logRecord);
        assertEquals(message, this.logRecord.getMessage());
        assertEquals(level, this.logRecord.getLevel());
        assertEquals(LogFacadeTest.class.getName(), this.logRecord.getSourceClassName());
        assertEquals(methodName, this.logRecord.getSourceMethodName());

        if (hasThrown) {
            assertNotNull(this.logRecord.getThrown());
        } else {
            assertNull(this.logRecord.getThrown());
        }
    }
}
