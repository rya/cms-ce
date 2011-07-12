/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image;

import java.awt.image.BufferedImage;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.time.TimeService;
import com.enonic.cms.framework.util.ImageHelper;

import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.preview.PreviewService;
import com.enonic.cms.core.resolver.ContentAccessResolver;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.portal.image.cache.ImageCache;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

public final class ImageServiceImpl
    implements ImageService
{
    @Inject
    private ImageCache imageCache;

    private final ImageProcessor processor;

    private ContentDao contentDao;

    private UserDao userDao;

    private GroupDao groupDao;

    private BlobStore blobStore;

    private TimeService timeService;

    private PreviewService previewService;

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
        Preconditions.checkNotNull( imageRequest );
        String blobKey = getBlobKey( imageRequest );
        if ( blobKey == null )
        {
            return ImageResponse.notFound();
        }

        imageRequest.setBlobKey( blobKey );
        String eTag = calculateETag( imageRequest );
        if ( eTag.equals( imageRequest.getETag() ) )
        {
            return ImageResponse.notModified();
        }

        imageRequest.setETag( eTag );
        ImageResponse res = imageCache.get( imageRequest );
        if ( res != null )
        {
            res.setETag( imageRequest.getETag() );
            return res;
        }

        try
        {
            res = doProcess( imageRequest );
            res.setETag( imageRequest.getETag() );
        }
        catch ( Exception e )
        {
            throw new ImageProcessorException( "Failed to process image: " + e.getMessage(), e );
        }

        return res;
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

    private String calculateETag( ImageRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getBinaryDataKey() ).append( "-" );
        str.append( req.getParams().getQuality() ).append( "-" );
        str.append( req.getParams().getFilter() ).append( "-" );
        str.append( req.getParams().getBackgroundColor() ).append( "-" );
        str.append( req.getFormat() ).append( "-" );
        str.append( req.getBlobKey() );
        return "image_" + DigestUtils.shaHex( str.toString() );
    }

    @Inject
    public void setImageCache( ImageCache imageCache )
    {
        this.imageCache = imageCache;
    }

    @Inject
    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    @Inject
    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    @Inject
    public void setBlobStore( BlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Inject
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    @Inject
    public void setPreviewService( PreviewService previewService )
    {
        this.previewService = previewService;
    }

    @Inject
    public void setGroupDao( GroupDao groupDao )
    {
        this.groupDao = groupDao;
    }

}
