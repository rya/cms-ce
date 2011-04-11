/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;

public class StandardCacheConcurrencyTest
    extends TestCase
{
    private static final Logger LOG = LoggerFactory.getLogger( StandardCacheConcurrencyTest.class.getName() );

    private int keyCounter = 0;

    private StandardCache cache;


    Random wheel = new Random( 149L );

    public void testConcurrencyWhenRemoveGroup()
        throws InterruptedException
    {

        cache = new StandardCache( 10000000 );

        CacheUsageSimulator putEntrySim = new CacheUsageSimulator( cache, 10, 1000 )
        {
            public void doCacheOperation( StandardCache cache )
            {
                cache.put( getNewCacheEntry( "a" ) );
            }
        };
        CacheUsageSimulator removeGroupSim = new CacheUsageSimulator( cache, 1000, 10 )
        {
            public void doCacheOperation( StandardCache cache )
            {
                cache.removeGroup( "b" );
            }
        };

        putEntrySim.start();
        removeGroupSim.start();

        removeGroupSim.join();
        putEntrySim.join();

        assertExpectNull( putEntrySim.exception );
        assertExpectNull( removeGroupSim.exception );
    }

    public void testConcurrencyWhenGet()
        throws InterruptedException
    {

        cache = new StandardCache( 5 );

        cache.put( createCacheEntry( "A", "1", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "B", "2", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "C", "3", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "D", "4", Long.MAX_VALUE ) );
        cache.put( createCacheEntry( "E", "5", Long.MAX_VALUE ) );

        CacheUsageSimulator iterationTriggerSim = new CacheUsageSimulator( cache, 10, 1000 )
        {

            public void doCacheOperation( StandardCache cache )
            {

                // call remove group with any value to trigger iteration over keys
                cache.removeGroup( "dummy" );
            }
        };
        CacheUsageSimulator getterSim = new CacheUsageSimulator( cache, 1000, 10 )
        {

            public void doCacheOperation( StandardCache cache )
            {

                CacheEntry entry = cache.get( "B" );
                assertNotNull( entry );
                assertEquals( "B", entry.getKey().toString() );
                entry = cache.get( "A" );
                assertNotNull( entry );
                assertEquals( "A", entry.getKey().toString() );
                entry = cache.get( "E" );
                assertNotNull( entry );
                assertEquals( "E", entry.getKey().toString() );
                entry = cache.get( "D" );
                assertNotNull( entry );
                assertEquals( "D", entry.getKey().toString() );
                entry = cache.get( "C" );
                assertNotNull( entry );
                assertEquals( "C", entry.getKey().toString() );

            }
        };

        iterationTriggerSim.start();
        getterSim.start();

        getterSim.join();
        iterationTriggerSim.join();

        assertEquals( 5, cache.numberOfEntries() );
        assertExpectNull( iterationTriggerSim.exception );
        assertExpectNull( getterSim.exception );
    }

    private void assertExpectNull( Exception e )
    {

        if ( e != null )
        {

            // print exception
            LOG.info( "Exception found:" , e);
            fail( "Concurrency test failed. Expected no exception, got: " + e.getMessage() );

        }
    }

    private long getRandomSleepTime( int maxTime )
    {
        return 1 + wheel.nextInt( maxTime );
    }

    @SuppressWarnings({"unchecked"})
    private CacheEntry createCacheEntry( String key, Object value, long timeToLive )
    {

        return new CacheEntry( key, value, timeToLive );
    }

    private CacheEntry getNewCacheEntry( String group )
    {
        final String newKey = group + ":" + String.valueOf( keyCounter++ );
        return createCacheEntry( newKey, "value." + newKey, Long.MAX_VALUE );
    }


    private abstract class CacheUsageSimulator
        extends Thread
    {

        private int roundsToSimulate = 0;

        private StandardCache cache;

        private int sleepTime;

        public Exception exception;

        public CacheUsageSimulator( StandardCache cache, int roundsToSimulate, int sleepTime )
        {
            this.cache = cache;
            this.roundsToSimulate = roundsToSimulate;
            this.sleepTime = sleepTime;
        }


        public void run()
        {

            int rounds = 1;

            while ( rounds < roundsToSimulate )
            {

                try
                {
                    doCacheOperation( cache );
                }
                catch ( Exception e )
                {
                    exception = e;
                    break;
                }

                rounds++;

                try
                {
                    Thread.sleep( getRandomSleepTime( sleepTime ) );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        }

        public abstract void doCacheOperation( StandardCache cache );
    }
}