/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.log.LogEntryEntity;
import com.enonic.cms.core.log.LogEntryKey;
import com.enonic.cms.store.dao.LogEntryDao;

public final class LogEntryEntityFetcherImpl
    implements LogEntryEntityFetcher
{

    private final LogEntryDao logEntryDao;

    public LogEntryEntityFetcherImpl( LogEntryDao dao )
    {
        this.logEntryDao = dao;
    }

    public Map<LogEntryKey, LogEntryEntity> fetch( List<LogEntryKey> keys )
    {

        Map<LogEntryKey, LogEntryEntity> map = new LinkedHashMap<LogEntryKey, LogEntryEntity>();
        if ( keys != null && keys.size() > 0 )
        {
            // performance: fetching one and one will go faster when content is cached
            int i = 0;
            for ( LogEntryKey key : keys )
            {
                map.put( key, logEntryDao.findByKey( key ) );
                i++;
            }
        }

        return map;
    }
}
