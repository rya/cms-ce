/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.file;

import java.io.File;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobRecordTest;

public class FileBlobRecordTest
    extends BlobRecordTest
{
    protected BlobRecord createRecord(final BlobKey key, final String data)
        throws Exception
    {
        final File file = File.createTempFile( "blob", "dat" );
        file.deleteOnExit();
        
        Files.copy( ByteStreams.newInputStreamSupplier( data.getBytes() ), file);
        return new FileBlobRecord(key, file);
    }
}
