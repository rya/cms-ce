/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.memory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;

final class MemoryBlobRecord
    extends BlobRecord
{
    private final byte[] data;

    public MemoryBlobRecord( final BlobKey key, final byte[] data )
    {
        super( key );
        this.data = data;
    }

    public long getLength()
    {
        return this.data.length;
    }

    public InputStream getStream()
    {
        return new ByteArrayInputStream( this.data );
    }
}
