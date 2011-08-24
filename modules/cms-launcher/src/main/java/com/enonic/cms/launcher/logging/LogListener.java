package com.enonic.cms.launcher.logging;

import java.util.logging.LogRecord;

public interface LogListener
{
    public void log(final LogRecord record);
}
