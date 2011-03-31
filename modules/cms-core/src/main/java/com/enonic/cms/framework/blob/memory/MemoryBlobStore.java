/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.memory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import com.google.common.collect.Maps;
import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStoreHelper;
import com.enonic.cms.framework.blob.base.AbstractBlobStore;

public final class MemoryBlobStore
    extends AbstractBlobStore
{
    private final Map<BlobKey, MemoryBlobRecord> store;

    public MemoryBlobStore()
    {
        this.store = Maps.newConcurrentMap();
    }

    public BlobRecord getRecord( final BlobKey key )
    {
        return this.store.get( key );
    }

    public BlobRecord addRecord( final InputStream in )
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final BlobKey key = BlobStoreHelper.createKey( in, out );

        MemoryBlobRecord record = this.store.get( key );
        if ( record != null )
        {
            return record;
        }

        record = new MemoryBlobRecord( key, out.toByteArray() );
        this.store.put( key, record );
        return record;
    }

    public Iterable<BlobKey> getAllKeys()
    {
        return this.store.keySet();
    }

    public boolean deleteRecord( BlobKey key )
    {
        return this.store.remove( key ) != null;
    }
}
