/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

import com.enonic.cms.core.NotFoundErrorType;
import com.enonic.cms.core.StacktraceLoggingUnrequired;
import com.enonic.cms.core.content.ContentKey;

public class ContentNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{

    private ContentKey contentKey;

    private String message;

    public ContentNotFoundException( ContentKey contentKey )
    {
        this.contentKey = contentKey;
        message = "Content not found, key = " + contentKey;
    }

    public ContentNotFoundException( ContentKey contentKey, String contentTitle )
    {
        this.contentKey = contentKey;
        message = "Content not found, key = " + contentKey + ", title = '" + contentTitle + "'";
    }

    public ContentNotFoundException( ContentKey contentKey, String contentTitle, String reason )
    {
        this.contentKey = contentKey;
        this.message = "Content not found, reason: '" + reason + "', " + "key = " + contentKey + ", title = '" + contentTitle + "'";
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public String getMessage()
    {
        return message;
    }
}
