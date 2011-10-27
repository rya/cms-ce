/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.cache;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;

public interface ImageCache
{
    public ImageResponse get( ImageRequest req );

    public void put( ImageRequest req, ImageResponse res );
}
