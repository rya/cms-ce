/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.log;

import com.enonic.cms.domain.ResultSet;

import java.util.Collection;
import java.util.List;

public interface LogEntryResultSet
    extends ResultSet
{
    LogEntryKey getKey(int index);

    List<LogEntryKey> getKeys();

    Collection<LogEntryEntity> getLogEntries();
}
