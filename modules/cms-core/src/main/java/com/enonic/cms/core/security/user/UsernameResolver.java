/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.domain.user.UserInfo;

/**
 * Created by rmy - Date: Sep 18, 2009
 */
public class UsernameResolver
    extends AbstractUserPropertyResolver
{
    public UsernameResolver( UserStoreConfig userStoreConfig )
    {
        super( userStoreConfig );
    }

    public String resolveUsername( final StoreNewUserCommand command )
    {
        userName = command.getUsername();
        displayName = command.getDisplayName();

        UserInfo userInfo = command.getUserInfo();
        if ( userInfo != null )
        {
            setUserInfoFields( userInfo );
        }

        String resolvedUsername = doResolve();

        if ( StringUtils.isBlank( resolvedUsername ) )
        {
            throw new IllegalArgumentException( "Could not resolve user name" );
        }

        return stripBlankspaces( resolvedUsername );
    }

    private static String stripBlankspaces( String resolvedUsername )
    {
        return resolvedUsername.replaceAll( "\\s+", "" );
    }


    private String doResolve()
    {

        // Check existing display name - use it if valid
        String displayName = this.displayName;
        if ( displayName != null && displayName.trim().length() > 0 )
        {
            return displayName.trim();
        }

        // Resolve display name from prefix, firstName, middleName, lastName, suffix - use it if valid
        displayName = resolveFrom( prefix, firstName, middleName, lastName, suffix );
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
