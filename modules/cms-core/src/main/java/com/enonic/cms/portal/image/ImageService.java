/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.image;

public interface ImageService
{
    public ImageResponse process( ImageRequest req );

    public Long getImageTimestamp( ImageRequest req );

    public boolean canAccess( ImageRequest req );
}
