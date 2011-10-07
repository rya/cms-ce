/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.locks.Lock;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.time.TimeService;
import com.enonic.cms.framework.util.GenericConcurrencyLock;
import com.enonic.cms.framework.util.ImageHelper;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;
import com.enonic.cms.core.image.cache.ImageCache;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.business.portal.livetrace.ImageRequestTrace;
import com.enonic.cms.business.portal.livetrace.ImageRequestTracer;
import com.enonic.cms.business.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.business.preview.PreviewService;

public final class ImageServiceImpl
    implements ImageService
{
    private ImageCache imageCache;

    private final ImageProcessor processor;

    private ContentDao contentDao;

    private GroupDao groupDao;

    private UserDao userDao;

    private BlobStore blobStore;

    private TimeService timeService;

    private PreviewService previewService;

    @Autowired
    private LivePortalTraceService livePortalTraceService;

    private static GenericConcurrencyLock<String> concurrencyLock = GenericConcurrencyLock.create();

    public ImageServiceImpl()
    {
        this.processor = new ImageProcessor();
    }

    public boolean accessibleInPortal( ImageRequest req )
    {
        ImageRequestAccessResolver imageRequestAccessResolver =
            new ImageRequestAccessResolver( contentDao, new ContentAccessResolver( groupDao ) );
        imageRequestAccessResolver.imageRequester( userDao.findByKey( req.getRequester().getKey() ) );
        imageRequestAccessResolver.requireMainVersion();
        imageRequestAccessResolver.requireOnlineNow( timeService.getNowAsDateTime(), previewService );
        ImageRequestAccessResolver.Access access = imageRequestAccessResolver.isAccessible( req );
        return access == ImageRequestAccessResolver.Access.OK;
    }

    public ImageResponse process( ImageRequest imageRequest )
    {
        Preconditions.checkNotNull( imageRequest, "imageRequest cannot be null" );

        final ImageRequestTrace imageRequestTrace = livePortalTraceService.getCurrentImageRequestTrace();

        String blobKey = getBlobKey( imageRequest );
        if ( blobKey == null )
        {
            return ImageResponse.notFound();
        }

        imageRequest.setBlobKey( blobKey );

        final Lock locker = concurrencyLock.getLock( imageRequest.getCacheKey() );

        try
        {
            locker.lock();

            ImageResponse res = imageCache.get( imageRequest );
            if ( res != null )
            {
                ImageRequestTracer.traceImageResponse( imageRequestTrace, res );
                ImageRequestTracer.traceUsedCachedResult( imageRequestTrace, true );
                return res;
            }

            try
            {
                res = doProcess( imageRequest );
                ImageRequestTracer.traceImageResponse( imageRequestTrace, res );
                ImageRequestTracer.traceUsedCachedResult( imageRequestTrace, false );
                return res;
            }
            catch ( Exception e )
            {
                throw new ImageProcessorException( "Failed to process image: " + e.getMessage(), e );
            }
        }
        finally
        {
            locker.unlock();
        }
    }

    public Long getImageTimestamp( final ImageRequest req )
    {
        final UserKey userKey = req.getUserKey();
        if ( userKey != null )
        {
            final UserEntity user = this.userDao.findByKey( userKey.toString() );
            if ( user != null )
            {
                return user.getTimestamp().getMillis();
            }
            return null;
        }

        final BinaryDataEntity binaryData = new BinaryDataForImageRequestResolver( contentDao ).resolveBinaryData( req );
        if ( binaryData == null )
        {
            throw new ImageProcessorException( "Image not found", null );
        }

        return binaryData.getCreatedAt().getTime();
    }

    private String getBlobKey( ImageRequest req )
    {
        if ( req.getUserKey() != null )
        {
            return getBlobKeyForUser( req.getUserKey() );
        }

        BinaryDataEntity binaryData = new BinaryDataForImageRequestResolver( contentDao ).resolveBinaryData( req );
        if ( binaryData == null )
        {
            return null;
        }
        req.setBinaryDataKey( binaryData.getBinaryDataKey() );
        return binaryData.getBlobKey();
    }

    private String getBlobKeyForUser( UserKey key )
    {
        UserEntity entity = this.userDao.findByKey( key.toString() );
        if ( entity == null )
        {
            return null;
        }

        byte[] photo = entity.getPhoto();
        if ( photo == null )
        {
            return null;
        }

        return DigestUtils.shaHex( photo );
    }

    private byte[] fetchImage( ImageRequest req )
    {
        if ( req.getUserKey() != null )
        {
            UserEntity entity = this.userDao.findByKey( req.getUserKey().toString() );
            if ( entity == null )
            {
                return null;
            }

            return entity.getPhoto();
        }

        BlobRecord binary = this.blobStore.getRecord( new BlobKey( req.getBlobKey() ) );
        if ( binary == null )
        {
            return null;
        }

        return binary.getAsBytes();
    }

    private ImageResponse doProcess( ImageRequest req )
        throws Exception
    {
        byte[] bytes = fetchImage( req );
        BufferedImage image = ImageHelper.readImage( bytes );

        ImageResponse imageResponse = processor.process( req, image );
        imageCache.put( req, imageResponse );
        return imageResponse;
    }

    public void setImageCache( ImageCache imageCache )
    {
        this.imageCache = imageCache;
    }

    @Autowired
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Autowired
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Autowired
    public void setBlobStore( BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Autowired
    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }

    @Autowired
    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }
}
