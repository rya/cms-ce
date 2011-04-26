/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group;

public interface GroupStorageService
{

    GroupKey storeNewGroup( StoreNewGroupCommand command );

    void addMembershipToGroup( GroupEntity groupToAdd, GroupEntity groupToAddTo );

    void removeMembershipFromGroup( GroupEntity groupToRemove, GroupEntity groupToRemoveFrom );

    void updateGroup( UpdateGroupCommand command );

    void deleteGroup( final DeleteGroupCommand command );

    Long count();
}
