package com.enonic.cms.launcher.logging;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

final class LogListenerHandler
    extends Handler
{
    private final List<LogListener> listeners;

    public LogListenerHandler()
    {
        this.listeners = new ArrayList<LogListener>();
    }

    public void addListener(final LogListener listener)
    {
        this.listeners.add(listener);
    }

    @Override
    public void publish(final LogRecord record)
    {
        for (final LogListener listener : this.listeners) {
            listener.log(record);
        }
    }

    @Override
    public void flush()
    {
        // Do nothing
    }

    @Override
    public void close()
    {
        // Do nothing
    }
}
