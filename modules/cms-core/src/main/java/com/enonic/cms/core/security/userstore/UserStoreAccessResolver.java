/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.userstore.UserStoreEntity;

/**
 * Jul 9, 2009
 */
public class UserStoreAccessResolver
{
    private MemberOfResolver memberOfResolver;

    public boolean hasDeleteUserAccess( UserEntity user, UserStoreEntity userstore )
    {
        return memberOfResolver.hasUserStoreAdministratorPowers( user, userstore.getKey() );
    }

    public boolean hasCreateUserAccess( UserEntity user, UserStoreEntity userstore )
    {
        return memberOfResolver.hasUserStoreAdministratorPowers( user, userstore.getKey() );
    }

    public boolean hasUpdateUserAccess( UserEntity updater, UserStoreEntity userstore, boolean allowedToUpdateSelf,
                                        UserEntity userToUpdate )
    {
        if ( allowedToUpdateSelf && updater.equals( userToUpdate ) )
        {
            return true;
        }

        return memberOfResolver.hasUserStoreAdministratorPowers( updater, userstore.getKey() );
    }

    public boolean hasCreateUserStoreAccess( UserEntity user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user );
    }

    public boolean hasUpdateUserStoreAccess( UserEntity user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user );
    }

    public boolean hasDeleteUserStoreAccess( UserEntity user )
    {
        return memberOfResolver.hasEnterpriseAdminPowers( user );
    }

    public void setMemberOfResolver( MemberOfResolver memberOfResolver )
    {
        this.memberOfResolver = memberOfResolver;
    }
}
