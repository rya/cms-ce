/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.domain.NotFoundErrorType;
import com.enonic.cms.domain.StacktraceLoggingUnrequired;

public class AttachmentNotFoundException
    extends RuntimeException
    implements NotFoundErrorType, StacktraceLoggingUnrequired
{
    private String message;

    private Reason reason;

    public static AttachmentNotFoundException noAccess( BinaryDataKey binaryDataKey )
    {
        return new AttachmentNotFoundException( "Attachment not found: '" + binaryDataKey + "'", Reason.NO_ACCESS );
    }

    public static AttachmentNotFoundException notFound( String path )
    {
        return new AttachmentNotFoundException( "Attachment not found: '" + path + "'", Reason.NOT_FOUND );
    }

    public static AttachmentNotFoundException notFound( BinaryDataKey binaryDataKey )
    {
        return new AttachmentNotFoundException( "Attachment not found: '" + binaryDataKey + "'", Reason.NOT_FOUND );
    }

    public AttachmentNotFoundException( String message, Reason reason )
    {
        this.message = message;
        this.reason = reason;
    }

    public String getMessage()
    {
        return message;
    }

    public boolean reasonIsNoAccess()
    {
        return reason == AttachmentNotFoundException.Reason.NO_ACCESS;
    }

    public enum Reason
    {
        NOT_FOUND,
        NO_ACCESS
    }
}
