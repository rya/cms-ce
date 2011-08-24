package com.enonic.cms.launcher.logging;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class LogService
{
    private final LogListenerHandler handler;

    public LogService()
    {
        this.handler = new LogListenerHandler();

        final LogManager manager = LogManager.getLogManager();
        manager.reset();

        final Logger logger = manager.getLogger("");
        logger.addHandler(this.handler);
        logger.setLevel(Level.INFO);
    }

    public void addListener(final LogListener listener)
    {
        this.handler.addListener(listener);
    }
}
