/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import com.enonic.cms.core.security.user.QualifiedUsername;


public class ContentAccessException
    extends RuntimeException
{

    private ContentKey contentKey;

    private ContentAccessType accessType;

    public ContentAccessException( String message, QualifiedUsername user, ContentAccessType accessType, ContentKey contentKey )
    {
        super( buildMessage( message, user, accessType, contentKey ) );
        this.accessType = accessType;
        this.contentKey = contentKey;
    }

    public ContentAccessException( ContentKey contentKey, ContentAccessType accessType )
    {
        super( buildMessage( accessType, contentKey ) );
        this.accessType = accessType;
        this.contentKey = contentKey;
    }

    public ContentAccessType getAccessType()
    {
        return accessType;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    private static String buildMessage( ContentAccessType accessType, ContentKey contentKey )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( "User do not have access to " ).append( accessType ).append( " content: " ).append( contentKey );
        return msg.toString();
    }

    private static String buildMessage( String message, QualifiedUsername user, ContentAccessType categoryAccessType,
                                        ContentKey categoryKey )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( message );
        msg.append( " User " ).append( user ).append( " do not have " ).append( categoryAccessType );
        msg.append( " access on content " ).append( categoryKey );
        return msg.toString();
    }
}
