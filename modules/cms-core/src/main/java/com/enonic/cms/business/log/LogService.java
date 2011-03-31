/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.log;

import com.enonic.cms.domain.log.LogEntryEntity;
import com.enonic.cms.domain.log.LogEntryKey;
import com.enonic.cms.domain.log.LogEntryResultSet;
import com.enonic.cms.domain.log.LogEntrySpecification;
import com.enonic.cms.domain.log.StoreNewLogEntryCommand;

public interface LogService
{
    LogEntryResultSet getLogEntries( LogEntrySpecification spec, String orderBy, int count, int index );

    LogEntryKey storeNew( LogEntryEntity logEntry );

    LogEntryKey storeNew( StoreNewLogEntryCommand command );
}
