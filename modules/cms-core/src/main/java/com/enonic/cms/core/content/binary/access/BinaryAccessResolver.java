/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary.access;

import java.util.List;

import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.security.user.UserEntity;


public class BinaryAccessResolver
{
    @Autowired
    private ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    private GroupDao groupDao;

    public boolean hasReadAndIsAccessibleOnline( BinaryDataEntity binaryData, UserEntity user, DateTime atTime )
    {
        List<ContentBinaryDataEntity> contentBinaryData = contentBinaryDataDao.findAllByBinaryKey( binaryData.getKey() );
        if ( contentBinaryData == null || contentBinaryData.size() == 0 )
        {
            return false;
        }

        final ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );

        for ( ContentBinaryDataEntity currentContentBinaryData : contentBinaryData )
        {
            ContentVersionEntity contentVersion = currentContentBinaryData.getContentVersion();
            if ( contentVersion != null )
            {
                ContentEntity content = contentVersion.getContent();
                boolean isOnline = content.isOnline( atTime );
                ContentVersionEntity mainVersion = content.getMainVersion();
                if ( isOnline && mainVersion.equals( contentVersion ) )
                {
                    if ( contentAccessResolver.hasReadContentAccess( user, content ) )
                    {
                        return true;
                    }
                }
            }

        }

        // No main version of the content refers to this binary.
        return false;
    }

    public boolean hasReadAccess( BinaryDataEntity binaryData, UserEntity user )
    {
        final ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );

        List<ContentBinaryDataEntity> contentBinaryData = contentBinaryDataDao.findAllByBinaryKey( binaryData.getKey() );
        if ( contentBinaryData == null || contentBinaryData.size() == 0 )
        {
            return false;
        }

        for ( ContentBinaryDataEntity currentContentBinaryData : contentBinaryData )
        {
            ContentVersionEntity contentVersion = currentContentBinaryData.getContentVersion();
            if ( contentVersion != null )
            {
                ContentEntity content = contentVersion.getContent();
                ContentVersionEntity mainVersion = content.getMainVersion();
                if ( mainVersion.equals( contentVersion ) )
                {
                    if ( contentAccessResolver.hasReadContentAccess( user, content ) )
                    {
                        return true;
                    }
                }
            }

        }

        // No main version of the content refers to this binary.
        return false;
    }
}
