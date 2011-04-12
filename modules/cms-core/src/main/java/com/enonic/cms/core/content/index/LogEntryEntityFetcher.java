/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import com.enonic.cms.core.log.LogEntryEntity;
import com.enonic.cms.core.log.LogEntryKey;

import java.util.List;
import java.util.Map;

/**
 * This interface defines the content entity fetcher.
 */
public interface LogEntryEntityFetcher
{

    Map<LogEntryKey, LogEntryEntity> fetch( List<LogEntryKey> keys );

}
