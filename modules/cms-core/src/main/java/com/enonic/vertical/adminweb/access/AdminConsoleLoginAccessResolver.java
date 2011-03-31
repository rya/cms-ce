/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.access;

import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.user.UserEntity;

public class AdminConsoleLoginAccessResolver
{
    public boolean hasAccess( UserEntity user )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        for ( GroupEntity group : user.getAllMembershipsGroups() )
        {
            switch ( group.getType() )
            {
                case USERSTORE_ADMINS:
                case ADMINS:
                case CONTRIBUTORS:
                case EXPERT_CONTRIBUTORS:
                case DEVELOPERS:
                    return true;
            }
        }
        return false;
    }
}
