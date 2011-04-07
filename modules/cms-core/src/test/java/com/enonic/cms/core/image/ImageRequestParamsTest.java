/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.core.image.ImageRequestParams;

public class ImageRequestParamsTest
{
    @Test
    public void testParams()
    {
        ImageRequestParams params = new ImageRequestParams();
        params.setFilter( "myfilter" );
        params.setQuality( 100 );
        params.setBackgroundColor( 0xFFFFFF );
        params.setFormat( "png" );

        Assert.assertEquals( "myfilter", params.getFilter() );
        Assert.assertEquals( 100, params.getQuality() );
        Assert.assertEquals( "100", params.getQualityAsString() );
        Assert.assertEquals( 0xFFFFFF, params.getBackgroundColor() );
        Assert.assertEquals( "0xffffff", params.getBackgroundColorAsString() );
        Assert.assertEquals( "png", params.getFormat() );
        Assert.assertEquals( "676e702f66666666666678302f3030312f7265746c6966796d", params.getEncoded() );
    }

    @Test
    public void testEncodedParams()
    {
        ImageRequestParams params = new ImageRequestParams();
        params.setEncoded( "676e702f66666666666678302f3030312f7265746c6966796d" );

        Assert.assertEquals( "myfilter", params.getFilter() );
        Assert.assertEquals( 100, params.getQuality() );
        Assert.assertEquals( "100", params.getQualityAsString() );
        Assert.assertEquals( 0xFFFFFF, params.getBackgroundColor() );
        Assert.assertEquals( "0xffffff", params.getBackgroundColorAsString() );
        Assert.assertEquals( "png", params.getFormat() );
    }

    @Test
    public void testNullParams()
    {
        ImageRequestParams params = new ImageRequestParams();
        params.setFormat( null );

        Assert.assertNull( params.getFormat() );
        Assert.assertEquals( "2f66666666666678302f35382f", params.getEncoded() );
    }
}
