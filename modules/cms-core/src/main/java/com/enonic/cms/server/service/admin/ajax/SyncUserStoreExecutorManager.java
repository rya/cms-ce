/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax;

import java.util.HashMap;

import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;
import com.enonic.cms.server.service.admin.ajax.dto.SynchronizeStatusDto;

public final class SyncUserStoreExecutorManager
{
    private final SynchronizeUserStoreJobFactory factory;

    private final HashMap<String, SyncUserStoreExecutor> executorMap;

    public SyncUserStoreExecutorManager( SynchronizeUserStoreJobFactory factory )
    {
        this.factory = factory;
        this.executorMap = new HashMap<String, SyncUserStoreExecutor>();
    }

    public SynchronizeStatusDto getStatus( final String userStoreKey, final String languageCode )
    {
        SyncUserStoreExecutor executor = getExecutor( userStoreKey, false );
        return executor != null ? executor.getStatus( languageCode ) : new SynchronizeStatusDto( userStoreKey );
    }

    public boolean start( String userStoreKey, boolean users, boolean groups, int batchSize )
    {
        SyncUserStoreExecutor executor = getExecutor( userStoreKey, true );
        return executor.start( users, groups, batchSize );
    }

    private synchronized SyncUserStoreExecutor getExecutor( String userStoreKey, boolean createIfNeeded )
    {
        SyncUserStoreExecutor executor = this.executorMap.get( userStoreKey );
        if ( executor == null && createIfNeeded )
        {
            executor = new SyncUserStoreExecutor( userStoreKey, this.factory );
            this.executorMap.put( userStoreKey, executor );
        }

        return executor;
    }


}
