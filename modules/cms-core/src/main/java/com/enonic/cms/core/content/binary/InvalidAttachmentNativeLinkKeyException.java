/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.domain.StacktraceLoggingUnrequired;

/**
 * Feb 16, 2010
 */
public class InvalidAttachmentNativeLinkKeyException
    extends RuntimeException
    implements StacktraceLoggingUnrequired
{
    public InvalidAttachmentNativeLinkKeyException( String key, String reason )
    {
        super( buildMessage( key, reason ) );
    }

    private static String buildMessage( String key, String reason )
    {
        return "Invalid Attachment Key '" + key + "': " + reason;
    }
}
