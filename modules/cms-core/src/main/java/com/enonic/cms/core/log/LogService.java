/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

public interface LogService
{
    LogEntryKey storeNew( LogEntryEntity logEntry );

    LogEntryKey storeNew( StoreNewLogEntryCommand command );
}
