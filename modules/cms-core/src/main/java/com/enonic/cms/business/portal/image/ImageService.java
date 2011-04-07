/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.image;

import com.enonic.cms.core.image.ImageRequest;
import com.enonic.cms.core.image.ImageResponse;

public interface ImageService
{
    public ImageResponse process( ImageRequest req );

    public Long getImageTimestamp( ImageRequest req );

    public boolean canAccess( ImageRequest req );
}
