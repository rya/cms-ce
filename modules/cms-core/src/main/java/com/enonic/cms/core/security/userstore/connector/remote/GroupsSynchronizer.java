/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.connector.remote;

import java.util.Collection;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupSpecification;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreEntity;
import com.enonic.cms.domain.user.remote.RemoteGroup;

public class GroupsSynchronizer
    extends AbstractBaseGroupSynchronizer
{
    public GroupsSynchronizer( final UserStoreEntity userStore, boolean syncGroup, boolean syncMemberships, boolean syncMembers )
    {
        super( userStore, syncGroup, syncMemberships, syncMembers );
    }

    public void synchronize( final Collection<RemoteGroup> remoteGroupsToSync, final MemberCache memberCache )
    {
        for ( final RemoteGroup remoteGroup : remoteGroupsToSync )
        {
            createUpdateOrResurrectLocalGroup( remoteGroup, memberCache );
        }
    }

    private void createUpdateOrResurrectLocalGroup( final RemoteGroup remoteGroup, final MemberCache memberCache )
    {
        final GroupSpecification spec = new GroupSpecification();
        spec.setUserStoreKey( userStore.getKey() );
        spec.setName( remoteGroup.getId() );
        spec.setSyncValue( remoteGroup.getSync() );
        spec.setType( GroupType.USERSTORE_GROUP );

        GroupEntity localGroup = groupDao.findSingleBySpecification( spec );

        if ( syncGroup )
        {
            if ( localGroup == null )
            {
                localGroup = createLocalGroup( remoteGroup );
                status.groupCreated();
            }
            else
            {
                final boolean resurrected = updateAndResurrectLocalGroup( localGroup, remoteGroup );
                status.groupUpdated( resurrected );
            }
        }
        if ( syncMembers )
        {
            syncGroupMembers( localGroup, remoteGroup, memberCache );
        }
        if ( syncMemberships )
        {
            syncGroupMemberships( localGroup, remoteGroup, memberCache );
        }
    }

    private GroupEntity createLocalGroup( final RemoteGroup remoteGroup )
    {
        final GroupEntity newLocalGroup = new GroupEntity();
        newLocalGroup.setName( remoteGroup.getId() );
        newLocalGroup.setRestricted( true );
        newLocalGroup.setSyncValue( remoteGroup.getSync() );
        newLocalGroup.setType( GroupType.USERSTORE_GROUP );
        newLocalGroup.setUserStore( userStore );
        newLocalGroup.setDeleted( 0 );

        groupDao.storeNew( newLocalGroup );
        groupDao.getHibernateTemplate().flush();
        return newLocalGroup;
    }

    private boolean updateAndResurrectLocalGroup( final GroupEntity localGroup, final RemoteGroup remoteGroup )
    {
        final boolean resurrected = localGroup.isDeleted() == true;
        // force resurrection
        localGroup.setDeleted( 0 );

        return resurrected;
    }
}
