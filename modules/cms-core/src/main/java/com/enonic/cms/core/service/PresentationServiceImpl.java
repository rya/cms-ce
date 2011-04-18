/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.ContentAccessType;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.service.PresentationService;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.SiteDao;
import com.enonic.cms.store.dao.UserDao;

public class PresentationServiceImpl
    implements PresentationService
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

    /**
     * Returns only binary data that is newer than the timestamp specified. Only binary data for this site and for a
     * user that is allowed to see it are returned.
     *
     * @param user          the user accessing the binary data
     * @param binaryDataKey key to the binary data
     * @param menuKey       the menu key
     * @param url           used by the log handler to record which url loaded the binary
     * @param referrer      used by the log handler to record which referrer loaded the binary
     * @param timestamp     the timestamp to check against
     * @param updateLog     if true, update the log
     * @return binary data object with or without binary data depending on the timestamp
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public BinaryData getBinaryData( User user, int binaryDataKey, int menuKey, String url, String referrer, long timestamp,
                                     boolean updateLog )
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

    /**
     * Get error page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
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

    public String getPathString( int type, int key, boolean includeRoot )
    {
        // TODO: Implement method using Hibernate
        throw new IllegalStateException("Method not implemented");
    }

    public boolean siteExists( SiteKey siteKey )
    {
        SiteEntity site = siteDao.findByKey( siteKey.toInt() );
        return ( site != null );
    }


}
