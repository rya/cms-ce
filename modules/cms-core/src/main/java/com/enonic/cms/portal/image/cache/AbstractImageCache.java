/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image.cache;

import com.enonic.cms.portal.image.ImageRequest;
import com.enonic.cms.portal.image.ImageResponse;

public abstract class AbstractImageCache
    implements ImageCache
{
    public final ImageResponse get( ImageRequest req )
    {
        byte[] data = get( req.getCacheKey() );
        if ( data == null )
        {
            return null;
        }

        return new ImageResponse( req.getName(), data, req.getFormat() );
    }

    public final void put( ImageRequest req, ImageResponse res )
    {
        put( req.getCacheKey(), res.getData() );
    }

    protected abstract byte[] get( String key );

    protected abstract void put( String key, byte[] data );
}
