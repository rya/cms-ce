/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.cache;

import org.apache.commons.codec.digest.DigestUtils;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;

public abstract class AbstractImageCache
    implements ImageCache
{
    public final ImageResponse get( ImageRequest req )
    {
        byte[] data = get( getCacheKey( req ) );
        if ( data == null )
        {
            return null;
        }

        return new ImageResponse( req.getName(), data, req.getFormat() );
    }

    public final void put( ImageRequest req, ImageResponse res )
    {
        put( getCacheKey( req ), res.getData() );
    }

    protected abstract byte[] get( String key );

    protected abstract void put( String key, byte[] data );

    private String getCacheKey( ImageRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getBlobKey() ).append( "-" );
        str.append( req.getParams().getQuality() ).append( "-" );
        str.append( req.getParams().getFilter() ).append( "-" );
        str.append( req.getParams().getBackgroundColor() );
        return DigestUtils.shaHex( str.toString() ) + "." + req.getFormat();
    }
}
