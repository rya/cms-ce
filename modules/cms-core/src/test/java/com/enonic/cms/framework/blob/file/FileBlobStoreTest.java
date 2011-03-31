/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.file;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import com.google.common.io.Files;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.BlobStoreTest;

public class FileBlobStoreTest
    extends BlobStoreTest
{
    private File tmpDir;

    protected BlobStore createBlobStore()
        throws Exception
    {
        this.tmpDir = Files.createTempDir();
        final FileBlobStore store = new FileBlobStore();
        store.setDirectory( this.tmpDir );
        return store;
    }

    @After
    public void tearDown()
        throws Exception
    {
        FileUtils.deleteDirectory( this.tmpDir );
    }
}
