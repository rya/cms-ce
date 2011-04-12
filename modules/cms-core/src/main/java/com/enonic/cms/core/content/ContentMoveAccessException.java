/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;


public class ContentMoveAccessException
    extends RuntimeException
{
    private ContentKey contentKey;

    public ContentMoveAccessException( ContentKey contentKey )
    {
        super( buildMessage( contentKey ) );
        this.contentKey = contentKey;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    private static String buildMessage( ContentKey contentKey )
    {
        StringBuffer msg = new StringBuffer();
        msg.append( "User do not have access to move content: " ).append( contentKey );
        return msg.toString();
    }
}
