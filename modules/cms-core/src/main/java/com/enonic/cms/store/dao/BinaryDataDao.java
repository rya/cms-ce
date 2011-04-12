/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.store.dao;

import java.util.List;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionKey;


public interface BinaryDataDao
    extends EntityDao<BinaryDataEntity>
{
    List<BinaryDataEntity> findAll();

    BinaryDataEntity findByKey( BinaryDataKey key );

    long countReferences( BinaryDataEntity binaryData );

    BinaryDataEntity findByContentKey( ContentKey contentKey, String label );

    BinaryDataEntity findByContentVersionKey( ContentVersionKey contentVersionKey, String label );

    BlobRecord getBlob( BinaryDataKey key );

    BlobStoreObject getBlob( BinaryDataEntity entity );

    void setBlob( BinaryDataKey key, BlobStoreObject blob );

    void setBlob( BinaryDataEntity entity, BlobStoreObject blob );
}
