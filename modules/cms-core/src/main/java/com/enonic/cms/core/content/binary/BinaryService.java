/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import com.enonic.cms.framework.blob.BlobRecord;

public interface BinaryService
{
    public BlobRecord fetchBinary( BinaryDataKey binaryDataKey );
}
