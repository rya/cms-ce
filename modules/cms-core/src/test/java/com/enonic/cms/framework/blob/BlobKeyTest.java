/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob;

import org.junit.Assert;
import org.junit.Test;

public class BlobKeyTest
{
    @Test
    public void testStringBased()
    {
        final BlobKey key = new BlobKey("1234");
        Assert.assertEquals("1234", key.toString());
    }

    @Test
    public void testBytesBased()
    {
        final BlobKey key = new BlobKey(new byte[] { 0x01, 0x02, 0x03, 0x04 });
        Assert.assertEquals("01020304", key.toString());
    }

    @Test
    public void testEquals()
    {
        final BlobKey key1 = new BlobKey("1234");
        final BlobKey key2 = new BlobKey("abcd");
        final BlobKey key3 = new BlobKey("1234");

        Assert.assertTrue(key1.equals( key1 ));
        Assert.assertTrue(key1.equals( key3 ));
        Assert.assertFalse(key1.equals( key2 ));
    }
}
