/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Apr 16, 2009
 */
public class BatchedListTest
{

    @Test
    public void testGetNextBatch()
    {
        List sourceList = new ArrayList();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );

        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList( sourceList, 3 );
        assertArrayEquals( new Integer[]{1, 2, 3}, list.getNextBatch().toArray() );
        assertArrayEquals( new Integer[]{4, 5}, list.getNextBatch().toArray() );
        assertNull( list.getNextBatch() );
    }

    @Test
    public void testGetNextBatchWithEmptySourceList()
    {
        List sourceList = new ArrayList();

        BatchedList list = new BatchedList( sourceList, 3 );
        assertNotNull( list.getNextBatch() );
        assertNull( list.getNextBatch() );
    }


    @Test
    public void testGetNextBatchWithSizeLessThanBatchSize()
    {
        List sourceList = new ArrayList();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList( sourceList, 7 );
        assertArrayEquals( new Integer[]{1, 2, 3, 4, 5}, list.getNextBatch().toArray() );
        assertNull( list.getNextBatch() );
    }


    @Test
    public void testHasMoreBatches()
    {
        List sourceList = new ArrayList();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );

        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList( sourceList, 3 );

        // two batches should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the first batch
        list.getNextBatch();

        // one batch should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the last batch
        list.getNextBatch();

        // no more batches should be available
        assertFalse( list.hasMoreBatches() );
    }

    @Test
    public void testHasMoreBatchesWithSizeLessThanBatchSize()
    {
        List sourceList = new ArrayList();
        sourceList.add( 1 );
        sourceList.add( 2 );
        sourceList.add( 3 );
        sourceList.add( 4 );
        sourceList.add( 5 );

        BatchedList list = new BatchedList( sourceList, 10 );

        // one batch should be available
        assertTrue( list.hasMoreBatches() );

        // fetch the one and only batch
        list.getNextBatch();

        // more more batches available
        assertFalse( list.hasMoreBatches() );
    }

    @Test
    public void testhasMoreBatchesWithEmptySourceList()
    {
        List sourceList = new ArrayList();

        BatchedList list = new BatchedList( sourceList, 3 );
        assertTrue( list.hasMoreBatches() );
        list.getNextBatch();
        assertFalse( list.hasMoreBatches() );
    }
}
