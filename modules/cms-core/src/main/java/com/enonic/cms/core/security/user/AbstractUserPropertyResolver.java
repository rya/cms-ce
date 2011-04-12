/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.user;

import com.enonic.cms.core.security.userstore.config.UserStoreConfig;
import com.enonic.cms.domain.user.UserInfo;
import com.enonic.cms.domain.user.field.UserFieldType;

/**
 * Created by rmy - Date: Sep 18, 2009
 */
public class AbstractUserPropertyResolver
{
    protected final UserStoreConfig userStoreConfig;

    protected String prefix = "";

    protected String suffix = "";

    protected String initials = "";

    protected String firstName = "";

    protected String middleName = "";

    protected String lastName = "";

    protected String nickName = "";

    protected String displayName;

    protected String userName;

    AbstractUserPropertyResolver( UserStoreConfig userStoreConfig )
    {
        this.userStoreConfig = userStoreConfig;
    }

    protected void setUserInfoFields( UserInfo userInfo )
    {
        if ( isUserFieldActive( UserFieldType.PREFIX ) )
        {
            this.prefix = userInfo.getPrefix();
        }

        if ( isUserFieldActive( UserFieldType.FIRST_NAME ) )
        {
            this.firstName = userInfo.getFirstName();
        }

        if ( isUserFieldActive( UserFieldType.MIDDLE_NAME ) )
        {
            this.middleName = userInfo.getMiddleName();
        }

        if ( isUserFieldActive( UserFieldType.LAST_NAME ) )
        {
            this.lastName = userInfo.getLastName();
        }

        if ( isUserFieldActive( UserFieldType.SUFFIX ) )
        {
            this.suffix = userInfo.getSuffix();
        }

        if ( isUserFieldActive( UserFieldType.NICK_NAME ) )
        {
            this.nickName = userInfo.getNickName();
        }

        if ( isUserFieldActive( UserFieldType.INITIALS ) )
        {
            this.initials = userInfo.getInitials();
        }
    }

    protected String resolveFrom( final String... parts )
    {
        final StringBuilder builder = new StringBuilder();
        for ( final String part : parts )
        {
            if ( part != null && part.trim().length() > 0 )
            {
                if ( builder.length() > 0 )
                {
                    builder.append( " " );
                }
                builder.append( part.trim() );
            }
        }
        return builder.toString();
    }

    private boolean isUserFieldActive( UserFieldType type )
    {
        return userStoreConfig.getUserFieldConfig( type ) != null;
    }
}
