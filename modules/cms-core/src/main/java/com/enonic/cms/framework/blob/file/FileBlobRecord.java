/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStoreException;

final class FileBlobRecord
    extends BlobRecord
{
    private final File file;

    public FileBlobRecord( final BlobKey key, final File file )
    {
        super( key );
        this.file = file;
    }

    public long getLength()
    {
        return this.file.length();
    }

    public InputStream getStream()
        throws BlobStoreException
    {
        try
        {
            return new FileInputStream( this.file );
        }
        catch ( FileNotFoundException e )
        {
            throw new BlobStoreException( "Could not find blob [" + getKey().toString() + "]", e );
        }
    }
}
