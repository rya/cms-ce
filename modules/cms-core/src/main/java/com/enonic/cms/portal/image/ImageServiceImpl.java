/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image;

import java.awt.image.BufferedImage;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.util.ImageHelper;

import com.enonic.cms.core.content.binary.BinaryService;
import com.enonic.cms.core.content.binary.access.BinaryAccessResolver;
import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.cache.ImageCache;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.image.ImageResponse;

import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;

public final class ImageServiceImpl
    implements ImageService
{
    private ImageCache imageCache;

    private BinaryService binaryService;

    private final ImageProcessor processor;

    private BinaryAccessResolver binaryAccessResolver;

    private BinaryDataDao binaryDataDao;

    private ContentDao contentDao;

    private UserDao userDao;

    private BlobStore blobStore;

    public ImageServiceImpl()
    {
        this.processor = new ImageProcessor();
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

    public Long getImageTimestamp( ImageRequest req )
    {

        UserKey userKey = req.getUserKey();

        if ( userKey != null )
        {
            UserEntity user = this.userDao.findByKey( userKey.toString() );
            if ( user != null )
            {
                return user.getTimestamp().getMillis();
            }
            return null;
        }

        BinaryDataKey binaryDataKey = resolveBinaryDataKey( req );

        if ( binaryDataKey == null )
        {
            throw new ImageProcessorException( "Image not found", null );
        }

        BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataKey );

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

        BinaryDataKey binaryDataKey = resolveBinaryDataKey( req );
        if ( binaryDataKey == null )
        {
            return null;
        }
        req.setBinaryDataKey( binaryDataKey );

        BinaryDataEntity entity = getBinaryDataEntity( req );
        if ( entity == null )
        {
            return null;
        }

        if ( req.serveOfflineContent() && requestedContentDeleted( req ) )
        {
            return null;
        }

        boolean serveOnlyOnlineContent = !req.serveOfflineContent();

        if ( serveOnlyOnlineContent && requestedNotContentOnline( req ) )
        {
            return null;
        }

        return entity.getBlobKey();
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

    private BinaryDataKey resolveBinaryDataKey( ImageRequest req )
    {
        BinaryDataKey binaryDataKey = req.getBinaryDataKey();

        if ( binaryDataKey != null )
        {
            return binaryDataKey;
        }

        if ( ( req.getContentKey() != null ) && ( req.getLabel() != null ) )
        {
            return binaryService.resolveBinaryDataKey( req.getContentKey(), req.getLabel(), req.getContentVersionKey() );
        }
        else
        {
            return null;
        }
    }

    private BinaryDataEntity getBinaryDataEntity( final ImageRequest imageRequest )
    {
        final BinaryDataEntity binaryData = binaryDataDao.findByKey( imageRequest.getBinaryDataKey() );
        if ( binaryData == null )
        {
            return null;
        }

        final UserEntity requester = userDao.findByKey( imageRequest.getRequester().getKey() );
        return binaryAccessResolver.hasReadAccess( binaryData, requester ) ? binaryData : null;
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

    private boolean requestedNotContentOnline( ImageRequest req )
    {
        ContentEntity content = contentDao.findByKey( req.getContentKey() );
        return !content.isOnline( req.getRequestDateTime().toDate() );
    }

    private boolean requestedContentDeleted( ImageRequest req )
    {
        ContentEntity content = contentDao.findByKey( req.getContentKey() );
        return content.isDeleted();
    }

    public void setImageCache( ImageCache imageCache )
    {
        this.imageCache = imageCache;
    }

    public void setBinaryService( BinaryService binaryService )
    {
        this.binaryService = binaryService;
    }

    public void setBinaryAccessResolver( BinaryAccessResolver binaryAccessResolver )
    {
        this.binaryAccessResolver = binaryAccessResolver;
    }

    public void setBinaryDataDao( BinaryDataDao binaryDataDao )
    {
        this.binaryDataDao = binaryDataDao;
    }

    public void setContentDao( ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }

    public void setUserDao( UserDao userDao )
    {
        this.userDao = userDao;
    }

    public void setBlobStore( BlobStore blobStore )
    {
        this.blobStore = blobStore;
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

    public boolean canAccess( ImageRequest req )
    {
        return getBlobKey( req ) != null;
    }
}
