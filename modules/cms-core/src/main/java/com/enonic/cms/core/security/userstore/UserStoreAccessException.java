/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore;

import com.enonic.cms.core.security.QualifiedName;

/**
 * Jul 9, 2009
 */
public class UserStoreAccessException
    extends RuntimeException
{
    private UserStoreAccessType type;

    private QualifiedName user;

    public UserStoreAccessException( UserStoreAccessType type, QualifiedName user, Object object )
    {
        super( buildMessage( type, user, object, null ) );
        this.type = type;
        this.user = user;
    }

    public UserStoreAccessException( UserStoreAccessType type, QualifiedName user, Object object, String detailMessage )
    {
        super( buildMessage( type, user, object, detailMessage ) );
        this.type = type;
        this.user = user;
    }

    private static String buildMessage( UserStoreAccessType type, QualifiedName user, Object object, String detailMessage )
    {
        StringBuffer s = new StringBuffer();
        s.append( "User '" ).append( user ).append( "' do not have access to " ).append( type );
        s.append( " '" ).append( object ).append( "'" );

        if ( detailMessage != null )
        {
            s.append( ": " ).append( detailMessage );
        }

        return s.toString();
    }

    public UserStoreAccessType getType()
    {
        return type;
    }

    public QualifiedName getUser()
    {
        return user;
    }
}
