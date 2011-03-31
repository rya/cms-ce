/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob.gc;

import java.io.ByteArrayInputStream;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import com.enonic.cms.framework.blob.BlobKey;
import com.enonic.cms.framework.blob.BlobStore;
import com.enonic.cms.framework.blob.memory.MemoryBlobStore;

public class GarbageCollectorTest
{
    private BlobStore store;
    private BlobKey key1;
    private BlobKey key2;
    private BlobKey key3;

    @Before
    public void setUp()
    {
        this.store = new MemoryBlobStore();
        this.key1 = this.store.addRecord( new ByteArrayInputStream("test1".getBytes()) ).getKey();
        this.key2 = this.store.addRecord( new ByteArrayInputStream("test2".getBytes()) ).getKey();
        this.key3 = this.store.addRecord( new ByteArrayInputStream("test3".getBytes()) ).getKey();
    }

    @Test
    public void testGarbageNone()
        throws Exception
    {
        // Check if size is 3
        assertBlobStoreSize(3);

        // Run garbage collector
        final GarbageCollector collector = new GarbageCollector();
        collector.setFinder( createFinder( this.key1, this.key2, this.key3 ) );
        collector.setStore( this.store );
        collector.process();

        // Check contents of blob store
        assertBlobStoreContents(this.key1, this.key2, this.key3);
    }

    @Test
    public void testGarbageOne()
        throws Exception
    {
        // Check if size is 3
        assertBlobStoreSize(3);

        // Run garbage collector
        final GarbageCollector collector = new GarbageCollector();
        collector.setFinder( createFinder( this.key1, this.key2 ) );
        collector.setStore( this.store );
        collector.process();

        // Check contents of blob store
        assertBlobStoreContents(this.key1, this.key2);
    }

    private UsedBlobKeyFinder createFinder(final BlobKey... keys)
        throws Exception
    {
        final UsedBlobKeyFinder finder = Mockito.mock( UsedBlobKeyFinder.class );
        Mockito.when( finder.findKeys() ).thenReturn( Sets.newHashSet( keys ));
        return finder;
    }

    private void assertBlobStoreSize(final int size)
    {
        Assert.assertEquals( size, Iterables.size( this.store.getAllKeys() ) );
    }

    private Set<BlobKey> getAllBlobKeys()
    {
        return Sets.newHashSet( this.store.getAllKeys() );
    }

    private void assertBlobStoreContents(final BlobKey... keys)
    {
        final Set<BlobKey> current = getAllBlobKeys();
        Assert.assertEquals(keys.length, current.size());

        for (final BlobKey key : keys) {
            Assert.assertTrue(current.contains( key ));
        }
    }
}
