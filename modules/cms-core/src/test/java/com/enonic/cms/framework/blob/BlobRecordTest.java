/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.ByteStreams;

public abstract class BlobRecordTest
{
    @Test
    public void testCreate()
        throws Exception
    {
        final BlobKey key = new BlobKey("1");
        final BlobRecord record = createRecord( key, "1" );

        Assert.assertEquals( key, record.getKey() );
        Assert.assertEquals( 1, record.getLength() );
        Assert.assertEquals( "1", new String(record.getAsBytes()) );
        assertStream("1", record.getStream());
    }

    private void assertStream(final String value, final InputStream in)
        throws Exception
    {
        Assert.assertNotNull(in);
        Assert.assertEquals(value, new String(ByteStreams.toByteArray( in )));
        in.close();
    }

    protected abstract BlobRecord createRecord(final BlobKey key, final String data)
        throws Exception;
}
