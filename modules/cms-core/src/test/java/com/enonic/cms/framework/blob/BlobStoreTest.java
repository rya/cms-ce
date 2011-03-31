/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public abstract class BlobStoreTest
{
    private BlobStore store;

    @Before
    public void setUp()
        throws Exception
    {
        this.store = createBlobStore();
    }

    @Test
    public void testGetRecord()
    {
        final BlobRecord rec1 = this.store.getRecord( new BlobKey("unknown") );
        Assert.assertNull(rec1);

        final BlobRecord rec2 = addRecord("content");
        Assert.assertNotNull(rec2);

        final BlobRecord rec3 = this.store.getRecord( rec2.getKey() );
        Assert.assertNotNull(rec2);
        Assert.assertEquals(rec2.getKey(), rec3.getKey());
    }

    @Test
    public void testGetAllKeys()
    {
        final Iterable<BlobKey> keys1 = this.store.getAllKeys();
        Assert.assertNotNull(keys1);
        Assert.assertEquals(0, Iterables.size( keys1 ));

        final BlobKey key1 = addRecord("test1").getKey();
        final BlobKey key2 = addRecord("test2").getKey();
        addRecord("test1");

        final Iterable<BlobKey> keys2 = this.store.getAllKeys();
        Assert.assertNotNull(keys2);
        Assert.assertEquals(2, Iterables.size( keys2 ));

        final Set<BlobKey> set2 = Sets.newHashSet( keys2 );
        Assert.assertTrue(set2.contains( key1 ));
        Assert.assertTrue(set2.contains( key2 ));
    }

    @Test
    public void testDeleteRecord()
    {
        final BlobKey key1 = addRecord("test1").getKey();
        final BlobKey key2 = addRecord("test2").getKey();

        final Iterable<BlobKey> keys1 = this.store.getAllKeys();
        Assert.assertNotNull(keys1);
        Assert.assertEquals(2, Iterables.size( keys1 ));

        this.store.deleteRecord( key1 );

        final Iterable<BlobKey> keys2 = this.store.getAllKeys();
        Assert.assertNotNull(keys2);
        Assert.assertEquals(1, Iterables.size( keys2 ));
        Assert.assertEquals(key2, keys2.iterator().next());
    }

    private BlobRecord addRecord(final String content)
    {
        return this.store.addRecord( new ByteArrayInputStream(content.getBytes()) );
    }

    protected abstract BlobStore createBlobStore()
        throws Exception;
}
