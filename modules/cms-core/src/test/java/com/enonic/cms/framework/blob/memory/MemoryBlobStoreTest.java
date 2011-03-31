/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.memory;

import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.BlobStoreTest;

public class MemoryBlobStoreTest
    extends BlobStoreTest
{
    protected BlobStore createBlobStore()
        throws Exception
    {
        return new MemoryBlobStore();
    }
}
