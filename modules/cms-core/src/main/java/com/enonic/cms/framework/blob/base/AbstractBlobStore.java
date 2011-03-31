/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.base;

import java.io.ByteArrayInputStream;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.BlobStoreException;
import com.enonic.cms.framework.blob.BlobStoreObject;

public abstract class AbstractBlobStore
    implements BlobStore
{
    public final BlobStoreObject get( final String id )
        throws BlobStoreException
    {
        final BlobRecord record = getRecord( new BlobKey( id ) );
        if ( record == null )
        {
            return BlobStoreObject.EMPTY;
        }
        else
        {
            return new BlobStoreObject( record.getKey().toString(), record.getAsBytes() );
        }
    }

    public final void put( final BlobStoreObject data )
        throws BlobStoreException
    {
        addRecord( new ByteArrayInputStream( data.getData() ) );
    }
}
