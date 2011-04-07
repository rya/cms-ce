/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.AttachmentRequest;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.security.user.User;

public interface BinaryService
{
    public BinaryDataEntity getBinaryDataForPortal( User user, AttachmentRequest attachmentRequest );

    public BinaryDataEntity getBinaryDataForAdmin( User user, BinaryDataKey binaryDataKey );

    public BlobRecord fetchBinary( BinaryDataKey binaryDataKey );

    public BinaryDataKey resolveBinaryDataKey( ContentKey contentKey, String label, ContentVersionKey contentVersionKey );
}
