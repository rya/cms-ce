/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.image;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.core.image.ImageRequest;

public class ImageRequestTest
{
    @Test
    public void testFormatOverride()
    {
        ImageRequest req = new ImageRequest();
        req.setFormat( "jpeg" );
        Assert.assertEquals( "jpeg", req.getFormat() );

        req.getParams().setFormat( "png" );
        Assert.assertEquals( "png", req.getFormat() );
    }

    @Test
    public void testLocation()
    {
        ImageRequest req = new ImageRequest();
        req.setContentKey( "99" );
        req.setFormat( "jpeg" );
        Assert.assertEquals( "99.jpeg", req.getLocation() );

        req = new ImageRequest();
        req.setContentKey( "99" );
        req.setLabel( "source" );
        req.setFormat( "jpeg" );
        Assert.assertEquals( "99/label/source.jpeg", req.getLocation() );

        req = new ImageRequest();
        req.setContentKey( "99" );
        req.setBinaryDataKey( "66" );
        req.setFormat( "jpeg" );
        Assert.assertEquals( "99/binary/66.jpeg", req.getLocation() );
    }
}
