/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.group.access;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;


public interface GroupAccessResolver
{

    boolean hasCreateGroupAccess( UserEntity executor, GroupType userstoreGroup, UserStoreEntity userStore );

    boolean hasDeleteGroupAccess( UserEntity executor, GroupEntity group );

    boolean hasUpdateGroupAccess( UserEntity executor, GroupEntity subject );

    boolean hasAddMembershipAccess( UserEntity executor, GroupEntity groupToAdd, GroupEntity groupToAddTo );

    boolean hasRemoveMembershipAccess( UserEntity executor, GroupEntity groupToRemove, GroupEntity groupToRemoveFrom );
}
