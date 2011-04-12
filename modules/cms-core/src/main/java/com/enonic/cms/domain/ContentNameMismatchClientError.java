/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import com.enonic.cms.core.content.ContentKey;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/20/11
 * Time: 10:51 AM
 */
public class ContentNameMismatchClientError
    extends ClientError
{
    private ContentKey contentKey;

    private String requestedContentName;

    public ContentNameMismatchClientError( int statusCode, String message, Throwable cause, ContentKey contentKey,
                                           String requestedContentName )
    {
        super( statusCode, message, cause );
        this.contentKey = contentKey;
        this.requestedContentName = requestedContentName;
    }

    public ContentKey getContentKey()
    {
        return contentKey;
    }

    public String getRequestedContentName()
    {
        return requestedContentName;
    }
}
