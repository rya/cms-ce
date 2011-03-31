/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.image.cache;

import com.enonic.cms.business.image.ImageRequest;
import com.enonic.cms.business.image.ImageResponse;

public interface ImageCache
{
    public ImageResponse get( ImageRequest req );

    public void put( ImageRequest req, ImageResponse res );
}
