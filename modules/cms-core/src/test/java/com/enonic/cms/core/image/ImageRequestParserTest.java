/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image;

import java.util.HashMap;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.junit.Assert;
import org.junit.Test;

import com.enonic.cms.core.security.user.UserKey;

public class ImageRequestParserTest
{
    @Test
    public void testBinaryKeyUrl()
    {
        ImageRequest req = parse( "_image/66/binary/80.jpeg", null, "somefilter", "100", "0x000000" );
        Assert.assertNotNull( req );
        Assert.assertEquals( new BinaryDataKey( 80 ), req.getBinaryDataKey() );
        Assert.assertEquals( new ContentKey( 66 ), req.getContentKey() );
        Assert.assertEquals( "source", req.getLabel() );
        Assert.assertEquals( "jpeg", req.getFormat() );
        Assert.assertEquals( "somefilter", req.getParams().getFilter() );
        Assert.assertEquals( 100, req.getParams().getQuality() );
        Assert.assertEquals( 0, req.getParams().getBackgroundColor() );
    }

    @Test
    public void testContentKeyUrl()
    {
        ImageRequest req = parse( "_image/66.jpeg", null, "somefilter", "100", "0x0000FF" );
        Assert.assertNotNull( req );
        Assert.assertEquals( new ContentKey( 66 ), req.getContentKey() );
        Assert.assertEquals( "source", req.getLabel() );
        Assert.assertNull( req.getBinaryDataKey() );
        Assert.assertEquals( "jpeg", req.getFormat() );
        Assert.assertEquals( "somefilter", req.getParams().getFilter() );
        Assert.assertEquals( 100, req.getParams().getQuality() );
        Assert.assertEquals( 255, req.getParams().getBackgroundColor() );
    }

    @Test
    public void testContentKeyLabelUrl()
    {
        ImageRequest req = parse( "_image/66/label/full.jpeg", null, "somefilter", "100", "0x0000FF" );
        Assert.assertNotNull( req );
        Assert.assertEquals( new ContentKey( 66 ), req.getContentKey() );
        Assert.assertEquals( "full", req.getLabel() );
        Assert.assertNull( req.getBinaryDataKey() );
        Assert.assertEquals( "jpeg", req.getFormat() );
        Assert.assertEquals( "somefilter", req.getParams().getFilter() );
        Assert.assertEquals( 100, req.getParams().getQuality() );
        Assert.assertEquals( 255, req.getParams().getBackgroundColor() );
    }

    @Test
    public void testUserKeyUrl()
    {
        ImageRequest req = parse( "_image/user/66.jpeg", null, "somefilter", "100", "0x0000FF" );
        Assert.assertNotNull( req );
        Assert.assertEquals( new UserKey( "66" ), req.getUserKey() );
        Assert.assertNull( req.getLabel() );
        Assert.assertNull( req.getBinaryDataKey() );
        Assert.assertEquals( "jpeg", req.getFormat() );
        Assert.assertEquals( "somefilter", req.getParams().getFilter() );
        Assert.assertEquals( 100, req.getParams().getQuality() );
        Assert.assertEquals( 255, req.getParams().getBackgroundColor() );
    }

    @Test
    public void testOverrideFormat()
    {
        ImageRequest req = parse( "_image/66/label/full.jpeg", "png", "somefilter", "100", "0x0000FF" );
        Assert.assertNotNull( req );
        Assert.assertEquals( new ContentKey( 66 ), req.getContentKey() );
        Assert.assertEquals( "full", req.getLabel() );
        Assert.assertNull( req.getBinaryDataKey() );
        Assert.assertEquals( "png", req.getFormat() );
        Assert.assertEquals( "somefilter", req.getParams().getFilter() );
        Assert.assertEquals( 100, req.getParams().getQuality() );
        Assert.assertEquals( 255, req.getParams().getBackgroundColor() );
    }

    private ImageRequest parse( String path, String format, String filter, String quality, String background )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( format != null )
        {
            map.put( "_format", format );
        }

        if ( filter != null )
        {
            map.put( "_filter", filter );
        }

        if ( quality != null )
        {
            map.put( "_quality", quality );
        }

        if ( background != null )
        {
            map.put( "_background", background );
        }

        ImageRequestParser parser = new ImageRequestParser();
        return parser.parse( path, map, false );
    }
}
