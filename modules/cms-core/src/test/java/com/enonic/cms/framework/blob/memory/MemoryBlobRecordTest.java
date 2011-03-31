/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.memory;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobRecordTest;

public class MemoryBlobRecordTest
    extends BlobRecordTest
{
    protected BlobRecord createRecord(final BlobKey key, final String data)
    {
        return new MemoryBlobRecord(key, data.getBytes());
    }
}
