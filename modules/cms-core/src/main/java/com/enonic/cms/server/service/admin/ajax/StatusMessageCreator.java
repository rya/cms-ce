/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.admin.ajax;

import com.enonic.cms.core.security.userstore.connector.synchronize.status.GroupMembershipsStatus;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.RemoteUsersStatus;

import com.enonic.cms.business.AdminConsoleTranslationService;

import com.enonic.cms.core.security.userstore.connector.synchronize.status.RemoteGroupsStatus;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.UserMembershipsStatus;
import com.enonic.cms.core.security.userstore.connector.synchronize.status.SynchronizeStatus;
import com.enonic.cms.core.security.userstore.status.LocalGroupsStatus;
import com.enonic.cms.core.security.userstore.status.LocalUsersStatus;

public final class StatusMessageCreator
{

    private String languageCode;

    private final AdminConsoleTranslationService adminConsoleTranslationService = AdminConsoleTranslationService.getInstance();

    public StatusMessageCreator()
    {
        this.languageCode = adminConsoleTranslationService.getDefaultLanguageCode();
    }

    public StatusMessageCreator( final String languageCode )
    {
        if ( languageCode == null )
        {
            throw new IllegalArgumentException( "languageCode cannot be NULL" );
        }
        this.languageCode = languageCode;
    }


    public String createMessage( SynchronizeStatus status )
    {
        if ( status.isCompleted() )
        {
            return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchCompleted%", languageCode ) );
        }

        String message = null;

        switch ( status.getType() )
        {
            case USERS_ONLY:
                message = createMessageForUsersOnly( status );
                break;
            case GROUPS_ONLY:
                message = createMessageForGroupsOnly( status );
                break;
            case USERS_AND_GROUPS:
                message = createMessageForUsersAndGroups( status );
                break;
        }

        if ( message != null )
        {
            return message;
        }

        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchGettingInfo%", languageCode ) );
    }

    private String createMessageForUsersOnly( SynchronizeStatus status )
    {
        RemoteUsersStatus remote = status.getRemoteUsersStatus();
        LocalUsersStatus local = status.getLocalUsersStatus();
        UserMembershipsStatus memberships = status.getUserMembershipsStatus();

        if ( remote.inProgress() )
        {
            return createSyncRemoteUserProgress( remote );
        }

        if ( local.inProgress() )
        {
            return createSyncLocalUserProgress( local );
        }

        if ( memberships.inProgress() )
        {
            return createSyncUserMembershipsProgress( memberships );
        }

        return null;
    }

    private String createMessageForGroupsOnly( SynchronizeStatus status )
    {
        RemoteGroupsStatus remote = status.getRemoteGroupsStatus();
        LocalGroupsStatus local = status.getLocalGroupsStatus();
        GroupMembershipsStatus memberships = status.getGroupMembershipsStatus();

        if ( remote.inProgress() )
        {
            return createSyncRemoteGroupProgress( remote );
        }

        if ( local.inProgress() )
        {
            return createSyncLocalGroupProgress( local );
        }

        if ( memberships.inProgress() )
        {
            return createSyncGroupMembershipsProgress( memberships );
        }

        return null;
    }

    private String createMessageForUsersAndGroups( SynchronizeStatus status )
    {
        RemoteUsersStatus remoteUsers = status.getRemoteUsersStatus();
        LocalUsersStatus localUsers = status.getLocalUsersStatus();
        UserMembershipsStatus userMemberships = status.getUserMembershipsStatus();
        RemoteGroupsStatus remoteGroups = status.getRemoteGroupsStatus();
        LocalGroupsStatus localGroups = status.getLocalGroupsStatus();
        GroupMembershipsStatus groupMemberships = status.getGroupMembershipsStatus();

        if ( remoteUsers.inProgress() )
        {
            return createSyncRemoteUserProgress( remoteUsers );
        }

        if ( localUsers.inProgress() )
        {
            return createSyncLocalUserProgress( localUsers );
        }

        if ( remoteGroups.inProgress() )
        {
            return createSyncRemoteGroupProgress( remoteGroups );
        }

        if ( localGroups.inProgress() )
        {
            return createSyncLocalGroupProgress( localGroups );
        }

        if ( userMemberships.inProgress() )
        {
            return createSyncUserMembershipsProgress( userMemberships );
        }

        if ( groupMemberships.inProgress() )
        {
            return createSyncGroupMembershipsProgress( groupMemberships );
        }

        return null;
    }

    private String createSyncRemoteUserProgress( RemoteUsersStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingRemoteUsers%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createSyncLocalUserProgress( LocalUsersStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingLocalUsers%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createSyncRemoteGroupProgress( RemoteGroupsStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingRemoteGroups%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createSyncLocalGroupProgress( LocalGroupsStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingLocalGroups%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createSyncUserMembershipsProgress( UserMembershipsStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingUserMemberships%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createSyncGroupMembershipsProgress( GroupMembershipsStatus status )
    {
        return createProgressMessage( adminConsoleTranslationService.getTranslation( "%synchSynchronizingInProgress%", languageCode ),
                                      adminConsoleTranslationService.getTranslation( "%synchSynchronizingGroupMemberships%", languageCode ),
                                      status.getCurrentCount(), status.getTotalCount() );
    }

    private String createProgressMessage( String heading )
    {
        return createProgressMessage( heading, null );
    }

    private String createProgressMessage( String heading, String progress )
    {
        StringBuffer str = new StringBuffer();
        str.append( "<div>" );
        str.append( "<p>" ).append( heading ).append( "</p>" );

        if ( progress != null )
        {
            str.append( "<div>" ).append( progress ).append( "</div>" );
        }

        str.append( "</div>" );
        return str.toString();
    }

    private String createProgressMessage( String heading, String progress, int current, int total )
    {
        StringBuffer str = new StringBuffer();
        str.append( progress ).append( " " );
        str.append( current ).append( "/" ).append( total );
        int percent = Math.round( (float) current / (float) total * 100f );
        str.append( " (" ).append( percent ).append( "%)" );
        return createProgressMessage( heading, str.toString() );
    }
}
