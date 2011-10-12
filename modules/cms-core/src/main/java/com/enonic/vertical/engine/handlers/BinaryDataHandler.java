/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;

import com.enonic.cms.framework.blob.BlobRecord;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentEntity;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class BinaryDataHandler
{
    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private BinaryDataDao binaryDataDao;

    @Autowired
    private ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    private ContentDao contentDao;

    public BinaryData getBinaryData( int binaryKey )
    {
        ContentBinaryDataEntity entity = contentBinaryDataDao.findByBinaryKey( binaryKey );
        if ( entity == null )
        {
            return null;
        }

        return getBinaryData( entity, false, -1 );
    }

    private BinaryData getBinaryData( ContentBinaryDataEntity contentBinaryData, boolean anonAccess, long timestamp )
    {
        BinaryData binaryData = new BinaryData();
        binaryData.key = contentBinaryData.getBinaryData().getKey();
        binaryData.contentKey = contentBinaryData.getContentVersion().getContent().getKey().toInt();
        binaryData.setSafeFileName( contentBinaryData.getBinaryData().getName() );
        binaryData.timestamp = contentBinaryData.getBinaryData().getCreatedAt();
        binaryData.anonymousAccess = anonAccess;

        if ( binaryData.timestamp.getTime() > timestamp )
        {
            BlobRecord blob = this.binaryDataDao.getBlob( contentBinaryData.getBinaryData() );
            binaryData.data = blob.getAsBytes();
        }

        return binaryData;
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
        if ( content != null )
        {
            BinaryDataEntity binaryData = content.getMainVersion().getBinaryData( label );
            if ( binaryData != null )
            {
                return binaryData.getKey();
            }
        }
        return -1;
    }

    public int[] getBinaryDataKeysByVersion( int versionKey )
    {
        final ContentVersionEntity contentVersion = this.contentVersionDao.findByKey(new ContentVersionKey(versionKey));
        final Set<BinaryDataKey> binaryDataKeys = contentVersion.getContentBinaryDataKeys();

        int count = 0;
        final int[] result = new int[binaryDataKeys.size()];
        for (final BinaryDataKey key : binaryDataKeys) {
            result[count++] = key.toInt();
        }

        return result;
    }
}


