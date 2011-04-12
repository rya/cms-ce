/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;


public class BinaryDataEntityDao
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

    public BinaryDataEntity findByContentKey( ContentKey contentKey, String label )
    {
        ContentEntity content = get( ContentEntity.class, contentKey );
        if ( content == null )
        {
            return null;
        }

        return findByContentVersion( content.getMainVersion(), label );
    }

    public BinaryDataEntity findByContentVersionKey( ContentVersionKey contentVersionKey, String label )
    {
        ContentVersionEntity version = get( ContentVersionEntity.class, contentVersionKey );
        return findByContentVersion( version, label );
    }

    private BinaryDataEntity findByContentVersion( ContentVersionEntity contentVersion, String label )
    {
        if ( contentVersion == null )
        {
            return null;
        }

        if ( ( label == null ) || label.trim().equals( "" ) )
        {
            label = "source";
        }

        for ( ContentBinaryDataEntity entity : contentVersion.getContentBinaryData() )
        {
            String binLabel = entity.getLabel();
            if ( binLabel == null )
            {
                binLabel = "";
            }

            if ( binLabel.equalsIgnoreCase( label ) )
            {
                return entity.getBinaryData();
            }
        }

        return null;
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

    public BlobStoreObject getBlob( BinaryDataEntity entity )
    {
        if ( entity != null )
        {
            return this.blobStore.get( entity.getBlobKey() );
        }

        return null;
    }

    public void setBlob( BinaryDataKey key, BlobStoreObject blob )
    {
        setBlob( findByKey( key ), blob );
    }

    public void setBlob( BinaryDataEntity entity, BlobStoreObject blob )
    {
        if ( entity != null )
        {
            this.blobStore.put( blob );
            entity.setBlobKey( blob.getId() );
        }
    }

    public List<BinaryDataEntity> findAll()
    {
        return findByNamedQuery( BinaryDataEntity.class, "BinaryDataEntity.getAll" );
    }
}
