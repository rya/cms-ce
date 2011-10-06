/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.core.content.ContentKey;

/**
 * Nov 3, 2010
 */
public class AttachmentRequest
{
    private AttachmentNativeLinkKey attachmentNativeLinkKey;

    private BinaryDataKey binaryDataKey;

    public AttachmentRequest( AttachmentNativeLinkKey attachmentNativeLinkKey, BinaryDataKey binaryDataKey )
    {
        this.attachmentNativeLinkKey = attachmentNativeLinkKey;
        this.binaryDataKey = binaryDataKey;
    }

    public ContentKey getContentKey()
    {
        return attachmentNativeLinkKey.getContentKey();
    }

    public BinaryDataKey getBinaryDataKey()
    {
        return binaryDataKey;
    }
}
