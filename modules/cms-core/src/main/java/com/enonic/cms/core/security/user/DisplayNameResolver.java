/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.domain.user.UserInfo;

public final class DisplayNameResolver
    extends AbstractUserPropertyResolver
{
    public DisplayNameResolver( UserStoreConfig userStoreConfig )
    {
        super( userStoreConfig );
    }

    public String resolveDisplayName( final String name, final String displayName, final UserInfo userInfo )
    {
        this.displayName = displayName;
        this.userName = name;

        if ( userInfo != null )
        {
            setUserInfoFields( userInfo );
        }

        String resolvedDisplayName = doResolve();

        if ( StringUtils.isBlank( resolvedDisplayName ) )
        {
            throw new IllegalArgumentException( "Could not resolve display name" );
        }

        return resolvedDisplayName;
    }

    private String doResolve()
    {
        // Resolve display name from prefix, firstName, middleName, lastName, suffix - use it if valid
        String displayName = resolveFrom( prefix, firstName, middleName, lastName, suffix );
        if ( displayName.length() > 0 )
        {
            return displayName;
        }

        // Resolve display name from nickName - use it if valid
        displayName = resolveFrom( nickName );
        if ( displayName.length() > 0 )
        {
            return displayName;
        }

        // Resolve display name from initials - use it if valid
        displayName = resolveFrom( initials );
        if ( displayName.length() > 0 )
        {
            return displayName;
        }

        // Resolve display name from uid - use it if valid
        displayName = userName;

        if ( displayName != null && displayName.trim().length() > 0 )
        {
            return displayName.trim();
        }

        return null;
    }
}
