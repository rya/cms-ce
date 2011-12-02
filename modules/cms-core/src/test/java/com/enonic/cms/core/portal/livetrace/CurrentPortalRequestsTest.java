package com.enonic.cms.core.portal.livetrace;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import static org.junit.Assert.*;

public class CurrentPortalRequestsTest
{
    private final static Random RANDOM_WHEEL = new SecureRandom();

    private AtomicLong atomicRequestNumber = new AtomicLong( 0 );

    @Test
    public void size_returns_1_after_one_trace_is_added()
    {
        CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        PortalRequestTrace trace = new PortalRequestTrace( 1, "http://locahost:8080/site/0/home" );
        currentPortalRequests.add( trace );
        assertEquals( 1, currentPortalRequests.getSize() );
    }

    @Test
    public void getList_returns_1_item_after_trace_is_added()
    {
        CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        PortalRequestTrace trace = new PortalRequestTrace( 1, "http://locahost:8080/site/0/home" );
        currentPortalRequests.add( trace );

        List<PortalRequestTrace> actualList = currentPortalRequests.getList();
        assertSame( 1, actualList.size() );
        assertSame( trace, actualList.get( 0 ) );
    }

    @Test
    public void getList_returns_items_in_order_as_they_where_inserted()
    {
        CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        PortalRequestTrace trace1 = new PortalRequestTrace( 1, "http://locahost:8080/site/0/home" );
        currentPortalRequests.add( trace1 );

        PortalRequestTrace trace2 = new PortalRequestTrace( 1, "http://locahost:8080/site/0/home" );
        currentPortalRequests.add( trace2 );

        List<PortalRequestTrace> actualList = currentPortalRequests.getList();
        assertSame( 2, actualList.size() );
        assertSame( trace1, actualList.get( 0 ) );
        assertSame( trace2, actualList.get( 1 ) );
    }

    @Test
    public void concurrent_100_threads_adding_random_requests()
    {
        final int numberOfThreadsToStart = 100;

        final CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfThreadsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( currentPortalRequests, random( 10, 100 ), false ) );
        }

        List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertEquals( countTotalNumberOfExecutions( requestSimulators ), currentPortalRequests.getSize() );
        assertUniqueRequestNumbers( currentPortalRequests.getList() );
    }

    @Test
    public void concurrent_100_threads_adding_100_requests()
    {
        long startTime = System.currentTimeMillis();

        final int numberOfThreadsToStart = 100;

        final CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfThreadsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( currentPortalRequests, 100, false ) );
        }

        List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        System.out.println( "concurrent_100_threads_adding_100_requests, time: " + ( System.currentTimeMillis() - startTime ) );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertEquals( countTotalNumberOfExecutions( requestSimulators ), currentPortalRequests.getSize() );
        assertUniqueRequestNumbers( currentPortalRequests.getList() );
    }

    @Test
    public void concurrent_100_threads_adding_100_requests_and_removing_them()
    {
        long startTime = System.currentTimeMillis();

        final CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();
        final int numberOfRequestSimulatorsToStart = 100;

        List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfRequestSimulatorsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( currentPortalRequests, random( 10, 100 ), true ) );
        }
        List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        System.out.println(
            "concurrent_100_threads_adding_100_requests_and_removing_them, time: " + ( System.currentTimeMillis() - startTime ) );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertEquals( 0, currentPortalRequests.getSize() );
        assertUniqueRequestNumbers( currentPortalRequests.getList() );
    }

    @Test
    public void concurrent_100_threads_adding_random_requests_and_removing_them()
    {
        long startTime = System.currentTimeMillis();

        final CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

        final int numberOfRequestSimulatorsToStart = 100;
        final List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfRequestSimulatorsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( currentPortalRequests, 100, true ) );
        }

        final List<TraceInfoReaderSimulator> traceInfoReaderSimulators = new ArrayList<TraceInfoReaderSimulator>();
        for ( int i = 0; i < 100; i++ )
        {
            traceInfoReaderSimulators.add( new TraceInfoReaderSimulator( currentPortalRequests, 100 ) );
        }

        List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        threads.addAll( createThreadsForTraceInfoReaderSimulators( traceInfoReaderSimulators ) );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertNoExceptionsThrownForTraceInfoReaderSimulators( traceInfoReaderSimulators );
        assertEquals( 0, currentPortalRequests.getSize() );
        assertUniqueRequestNumbers( currentPortalRequests.getList() );
    }

    private void assertNoExceptionsThrownForRequestSimulators( List<RequestSimulator> simulators )
    {
        for ( Simulator simulator : simulators )
        {
            assertTrue( "Simulator have thrown exception!", !simulator.exceptionThrown );
        }
    }

    private void assertNoExceptionsThrownForTraceInfoReaderSimulators( List<TraceInfoReaderSimulator> simulators )
    {
        for ( Simulator simulator : simulators )
        {
            assertTrue( "Simulator have thrown exception!", !simulator.exceptionThrown );
        }
    }

    private void assertUniqueRequestNumbers( List<PortalRequestTrace> traces )
    {
        final Set<Long> usedRequestNumbers = new HashSet<Long>();
        for ( PortalRequestTrace trace : traces )
        {
            long currentRequestNumber = trace.getRequestNumber();
            assertTrue( currentRequestNumber + "  duplicate requestNumber found!", !usedRequestNumbers.contains( currentRequestNumber ) );
            usedRequestNumbers.add( currentRequestNumber );
        }
    }

    private PortalRequestTrace createTrace()
    {
        long requestNumber = atomicRequestNumber.incrementAndGet();
        return new PortalRequestTrace( requestNumber, "http://locahost:8080/site/0/home" );
    }

    private static int random( int low, int high )
    {
        return RANDOM_WHEEL.nextInt( high - low + 1 ) + low;
    }

    private List<Thread> createThreadsForRequestSimulators( List<RequestSimulator> simulators )
    {
        List<Thread> threads = new ArrayList<Thread>();
        for ( RequestSimulator requestSimulator : simulators )
        {
            threads.add( new Thread( requestSimulator ) );
        }

        return threads;
    }

    private List<Thread> createThreadsForTraceInfoReaderSimulators( List<TraceInfoReaderSimulator> simulators )
    {
        List<Thread> threads = new ArrayList<Thread>();
        for ( TraceInfoReaderSimulator requestSimulator : simulators )
        {
            threads.add( new Thread( requestSimulator ) );
        }

        return threads;
    }

    private void startThreads( Collection<Thread> threads )
    {
        for ( Thread thread : threads )
        {
            thread.start();
        }
    }

    private void waitForThreadsToFinish( Collection<Thread> threads )
    {
        for ( Thread thread : threads )
        {
            try
            {
                thread.join();
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
                fail();
            }
        }
    }

    private int countTotalNumberOfExecutions( Iterable<RequestSimulator> it )
    {
        int sum = 0;
        for ( RequestSimulator t : it )
        {
            sum += t.numberOfExecutions;
        }
        return sum;
    }

    private class RequestSimulator
        extends Simulator
        implements Runnable
    {
        private CurrentPortalRequests currentPortalRequests;

        final int numberOfExecutions;

        private boolean remove = false;

        private int[] sleepsBeforeRemoving;

        RequestSimulator( CurrentPortalRequests currentPortalRequests, int numberOfExecutions, boolean remove )
        {
            this.currentPortalRequests = currentPortalRequests;
            this.numberOfExecutions = numberOfExecutions;
            this.remove = remove;

            if ( remove )
            {
                sleepsBeforeRemoving = new int[numberOfExecutions];
                for ( int i = 0; i < numberOfExecutions; i++ )
                {
                    sleepsBeforeRemoving[i] = random( 3, 9 );
                }
            }
        }

        public void run()
        {
            try
            {
                for ( int i = 0; i < numberOfExecutions; i++ )
                {
                    PortalRequestTrace trace = createTrace();
                    currentPortalRequests.add( trace );

                    if ( remove )
                    {
                        Thread.sleep( sleepsBeforeRemoving[i] );
                        currentPortalRequests.remove( trace );
                    }
                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                exceptionThrown = true;
            }
        }
    }

    private class TraceInfoReaderSimulator
        extends Simulator
        implements Runnable
    {
        private CurrentPortalRequests currentPortalRequests;

        final int numberOfExecutions;

        private int[] sleepsBeforeReading;

        TraceInfoReaderSimulator( CurrentPortalRequests currentPortalRequests, int numberOfExecutions )
        {
            this.currentPortalRequests = currentPortalRequests;
            this.numberOfExecutions = numberOfExecutions;

            sleepsBeforeReading = new int[numberOfExecutions];
            for ( int i = 0; i < numberOfExecutions; i++ )
            {
                sleepsBeforeReading[i] = random( 3, 9 );
            }
        }

        public void run()
        {
            try
            {
                for ( int i = 0; i < numberOfExecutions; i++ )
                {

                    currentPortalRequests.getSize();
                    //noinspection UnusedDeclaration
                    for ( PortalRequestTrace trace : currentPortalRequests.getList() )
                    {
                        // just simulating iteration
                    }

                    Thread.sleep( sleepsBeforeReading[i] );


                }
            }
            catch ( Exception e )
            {
                e.printStackTrace();
                exceptionThrown = true;
            }
        }
    }

    private class Simulator
    {
        boolean exceptionThrown = false;
    }
}
