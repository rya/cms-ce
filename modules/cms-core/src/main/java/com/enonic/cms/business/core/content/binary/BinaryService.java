/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.core.content.binary;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.domain.content.binary.BinaryDataKey;

public interface BinaryService
{
    public BlobRecord fetchBinary( BinaryDataKey binaryDataKey );
}
