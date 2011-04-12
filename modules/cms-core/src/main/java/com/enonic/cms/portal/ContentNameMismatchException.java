/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: 1/20/11
 * Time: 8:02 AM
 */
public class ContentNameMismatchException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{
    private ContentKey contentKey;

    private String requestedContentName;

    public ContentNameMismatchException( ContentKey contentKey, String requestedContentName )
    {
        super( "Content with key: " + contentKey + " and name: " + requestedContentName + " not found" );

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
