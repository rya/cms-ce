/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax;

import java.util.Date;

import com.enonic.cms.core.security.userstore.UserStoreKey;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreType;
import com.enonic.cms.server.service.admin.ajax.dto.SynchronizeStatusDto;

import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJob;
import com.enonic.cms.core.security.userstore.connector.synchronize.SynchronizeUserStoreJobFactory;

import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;

public final class SyncUserStoreExecutor
{
    private final String userStoreKey;

    private final SynchronizeUserStoreJobFactory factory;

    private SynchronizeUserStoreJob job;

    private Date started;

    private Date finished;


    public SyncUserStoreExecutor( String userStoreKey, SynchronizeUserStoreJobFactory factory )
    {
        this.userStoreKey = userStoreKey;
        this.factory = factory;
    }

    private boolean inProgress()
    {
        if ( started == null ) /* Never started */
        {
            return false;
        }

        if ( finished == null ) /* Started but never finished */
        {
            return true;
        }

        if ( started.after( finished ) ) /* Started after last finished */
        {
            return true;
        }

        return false; /* Finished after last started */
    }

    private void createJob( boolean users, boolean groups, int batchSize )
    {
        SynchronizeUserStoreType type = getSyncType( users, groups );
        UserStoreKey key = UserStoreKey.parse( this.userStoreKey );
        this.job = this.factory.createSynchronizeUserStoreJob( key, type, batchSize );
    }

    public boolean start( boolean users, boolean groups, int batchSize )
    {
        if ( inProgress() )
        {
            return false;
        }

        this.started = new Date();
        createJob( users, groups, batchSize );
        this.job.start();
        this.finished = new Date();
        return true;
    }

    public SynchronizeStatusDto getStatus( final String languageCode )
    {
        final SynchronizeStatusDto dto = new SynchronizeStatusDto( this.userStoreKey );
        dto.setStartedDate( this.started );
        dto.setFinishedDate( this.finished );
        dto.setCompleted( !inProgress() );

        final SynchronizeStatus status = getJobStatus();
        if ( status != null )
        {
            dto.setCompleted( status.isCompleted() );
            dto.setType( status.getType().toString() );
            dto.setMessage( new StatusMessageCreator( languageCode ).createMessage( status ) );
        }

        return dto;
    }

    private SynchronizeStatus getJobStatus()
    {
        return this.job != null ? this.job.getStatus() : null;
    }

    private SynchronizeUserStoreType getSyncType( boolean users, boolean groups )
    {
        if ( users && groups )
        {
            return SynchronizeUserStoreType.USERS_AND_GROUPS;
        }
        else if ( groups && !users )
        {
            return SynchronizeUserStoreType.GROUPS_ONLY;
        }
        else if ( users && !groups )
        {
            return SynchronizeUserStoreType.USERS_ONLY;
        }
        else
        {
            return null;
        }
    }
}
