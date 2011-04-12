/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.blob;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.gc.UsedBlobKeyFinder;

import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.VirtualFileDao;
import com.enonic.cms.store.vfs.db.VirtualFileEntity;

import com.enonic.cms.core.content.binary.BinaryDataEntity;

public final class DbUsedBlobKeyFinder
    implements UsedBlobKeyFinder
{
    private BinaryDataDao binaryDataDao;

    private VirtualFileDao virtualFileDao;

    public Set<BlobKey> findKeys()
        throws Exception
    {
        final HashSet<BlobKey> keys = Sets.newHashSet();

        findFromVirtualFile( keys );
        findFromBinaryData( keys );

        return keys;
    }

    private void findFromVirtualFile( final Set<BlobKey> keys )
    {
        for ( final VirtualFileEntity entity : this.virtualFileDao.findAll() )
        {
            final String key = entity.getBlobKey();
            if ( key != null )
            {
                keys.add( new BlobKey( key ) );
            }
        }
    }

    private void findFromBinaryData( final Set<BlobKey> keys )
    {
        for ( final BinaryDataEntity entity : this.binaryDataDao.findAll() )
        {
            final String key = entity.getBlobKey();
            if ( key != null )
            {
                keys.add( new BlobKey( key ) );
            }
        }
    }

    public void setBinaryDataDao( final BinaryDataDao dao )
    {
        this.binaryDataDao = dao;
    }

    public void setVirtualFileDao( final VirtualFileDao dao )
    {
        this.virtualFileDao = dao;
    }
}
