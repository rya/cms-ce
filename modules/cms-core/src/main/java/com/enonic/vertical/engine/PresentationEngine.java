/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import javax.inject.Inject;

import com.enonic.cms.store.dao.*;
import org.joda.time.DateTime;

import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.ContentAccessType;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;

import com.enonic.cms.domain.SiteKey;

public class PresentationEngine
{
    @Inject
    protected ContentBinaryDataDao contentBinaryDataDao;

    @Inject
    protected SecurityService securityService;

    @Inject
    protected ContentDao contentDao;

    @Inject
    protected BinaryDataDao binaryDataDao;

    @Inject
    private SiteDao siteDao;

    @Inject
    private UserDao userDao;

    @Inject
    private BinaryAccessResolver binaryAccessResolver;

    public BinaryData getBinaryData( User user, int binaryDataKey, long timestamp )
    {

        if ( user == null )
        {
            user =  this.userDao.findBuiltInAnonymousUser();
        }

        UserEntity newUser = securityService.getUser( user );
        BinaryDataEntity binaryData = binaryDataDao.findByKey( new BinaryDataKey( binaryDataKey ) );
        if ( !binaryAccessResolver.hasReadAndIsAccessibleOnline( binaryData, newUser, new DateTime() ) )
        {
            return null;
        }

        ContentBinaryDataEntity contentBinaryData = contentBinaryDataDao.findByBinaryKey( binaryData.getKey() );
        ContentVersionEntity contentVersion = contentBinaryData.getContentVersion();
        ContentEntity content = contentVersion.getContent();

        // fast check if anonymous have read
        UserEntity anonymousUser = securityService.getUser( securityService.getAnonymousUserKey() );
        boolean anonAccess = content.hasAccessRightSet( anonymousUser.getUserGroup(), ContentAccessType.READ );
        return getBinaryData( contentBinaryData, anonAccess, timestamp );
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
            BlobStoreObject blob = this.binaryDataDao.getBlob( contentBinaryData.getBinaryData() );
            binaryData.data = blob.getData();
        }

        return binaryData;
    }

    public boolean hasErrorPage( int menuKey )
    {
        int result;
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            result = -1;
        }
        else
        {
            result = entity.getErrorPage().getKey();
        }
        return result >= 0;
    }

    public int getErrorPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    public int getLoginPage( int menuKey )
    {
        SiteEntity entity = siteDao.findByKey( menuKey );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }

    public int getBinaryDataKey( int contentKey, String label )
    {
        ContentEntity content = contentDao.findByKey( new ContentKey( contentKey ) );
        if ( content != null )
        {
            BinaryDataEntity binaryData = content.getMainVersion().getSingleBinaryData( label );
            if ( binaryData != null )
            {
                return binaryData.getKey();
            }
        }
        return -1;
    }

    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }
}
