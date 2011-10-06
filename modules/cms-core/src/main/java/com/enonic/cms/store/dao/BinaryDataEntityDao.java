/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.springframework.stereotype.Repository;

@Repository("binaryDataDao")
public final class BinaryDataEntityDao
    extends AbstractBaseEntityDao<BinaryDataEntity>
    implements BinaryDataDao
{
    @Autowired
    @Qualifier("blobStore")
    private BlobStore blobStore;

    public BinaryDataEntity findByKey( BinaryDataKey key )
    {
        return get( BinaryDataEntity.class, key );
    }

    public long countReferences( BinaryDataEntity binaryData )
    {
        return findSingleByNamedQuery( Long.class, "BinaryDataEntity.countReferences", "binaryDataKey", binaryData.getBinaryDataKey() );
    }

    public BlobRecord getBlob( BinaryDataKey key )
    {
        final BinaryDataEntity entity = findByKey( key );
        if ( entity == null )
        {
            return null;
        }

        return this.blobStore.getRecord( new BlobKey( entity.getBlobKey() ) );
    }

    public BlobRecord getBlob( BinaryDataEntity entity )
    {
        if ( entity != null )
        {
            return this.blobStore.getRecord( new BlobKey( entity.getBlobKey() ) );
        }

        return null;
    }

    public void setBlob( BinaryDataEntity entity, BlobRecord blob )
    {
        if ( entity != null )
        {
            this.blobStore.addRecord( blob.getStream() );
            entity.setBlobKey( blob.getKey().toString() );
        }
    }

    public List<BinaryDataEntity> findAll()
    {
        return findByNamedQuery( BinaryDataEntity.class, "BinaryDataEntity.getAll" );
    }
}
