/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource.access;

import javax.inject.Inject;

import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.MemberOfResolver;

public class ResourceAccessResolver
{
    @Inject
    private MemberOfResolver memberOfResolver;

    public boolean hasAccessToResourceTree( UserEntity executor )
    {
        return memberOfResolver.hasDeveloperPowers( executor );
    }

    public boolean hasAccess( ResourceKey resourceKey, UserEntity executor )
    {

        if ( resourceKey.isPublic() )
        {
            return true;
        }

        if ( memberOfResolver.hasDeveloperPowers( executor ) )
        {
            return true;
        }

        return false;
    }
}
