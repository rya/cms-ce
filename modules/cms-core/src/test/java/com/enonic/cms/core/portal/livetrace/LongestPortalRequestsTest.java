package com.enonic.cms.core.portal.livetrace;


import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongestPortalRequestsTest
{
    private final static Random RANDOM_WHEEL = new SecureRandom();

    private AtomicLong atomicRequestNumber = new AtomicLong( 0 );

    @Test
    public void getList_returns_1_item_after_trace_is_added()
    {
        LongestPortalRequests requests = new LongestPortalRequests( 50 );

        PortalRequestTrace trace = createTrace( 1, "http://locahost:8080/site/0/home", 10 );
        requests.add( trace );

        List<PortalRequestTrace> actualList = requests.getList();
        assertSame( 1, actualList.size() );
        assertSame( trace, actualList.get( 0 ) );
    }

    @Test
    public void maxSize_is_retained()
    {
        LongestPortalRequests requests = new LongestPortalRequests( 3 );

        requests.add( createTrace( 1, "http://locahost:8080/site/0/home", 10 ) );
        requests.add( createTrace( 2, "http://locahost:8080/site/0/home", 10 ) );
        requests.add( createTrace( 3, "http://locahost:8080/site/0/home", 10 ) );
        requests.add( createTrace( 4, "http://locahost:8080/site/0/home", 10 ) );
        requests.add( createTrace( 5, "http://locahost:8080/site/0/home", 10 ) );

        List<PortalRequestTrace> actualList = requests.getList();
        assertSame( 3, actualList.size() );
    }

    @Test
    public void getList_returns_items_in_order_of_trace_with_longest_duration_on_top()
    {
        LongestPortalRequests requests = new LongestPortalRequests( 50 );

        PortalRequestTrace longestTrace = createTrace( 1, "http://locahost:8080/site/0/home", 100 );
        requests.add( longestTrace );
        PortalRequestTrace shortestTrace = createTrace( 2, "http://locahost:8080/site/0/home", 10 );
        requests.add( shortestTrace );
        PortalRequestTrace inTheMiddleTrace = createTrace( 3, "http://locahost:8080/site/0/home", 12 );
        requests.add( inTheMiddleTrace );

        List<PortalRequestTrace> actualList = requests.getList();
        assertEquals( 3, actualList.size() );
        assertSame( longestTrace, actualList.get( 0 ) );
        assertSame( inTheMiddleTrace, actualList.get( 1 ) );
        assertSame( shortestTrace, actualList.get( 2 ) );
    }

    @Test
    public void getList_returns_3_items_in_correct_order_even_two_traces_have_same_duration()
    {
        LongestPortalRequests requests = new LongestPortalRequests( 50 );

        PortalRequestTrace longestTrace = createTrace( 1, "http://locahost:8080/site/0/home", 100 );
        requests.add( longestTrace );
        PortalRequestTrace secondLongestTrace = createTrace( 2, "http://locahost:8080/site/0/home", 100 );
        requests.add( secondLongestTrace );
        PortalRequestTrace inTheMiddleTrace = createTrace( 3, "http://locahost:8080/site/0/home", 10 );
        requests.add( inTheMiddleTrace );

        List<PortalRequestTrace> actualList = requests.getList();
        assertEquals( 3, actualList.size() );
        assertSame( longestTrace, actualList.get( 0 ) );
        assertSame( secondLongestTrace, actualList.get( 1 ) );
        assertSame( inTheMiddleTrace, actualList.get( 2 ) );
    }

    @Test
    public void concurrent_100_threads_adding_random_requests()
    {
        long startTime = System.currentTimeMillis();

        final int numberOfThreadsToStart = 100;

        LongestPortalRequests requests = new LongestPortalRequests( 50 );

        List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfThreadsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( requests, random( 10, 100 ) ) );
        }

        final List<TraceInfoReaderSimulator> traceInfoReaderSimulators = new ArrayList<TraceInfoReaderSimulator>();
        for ( int i = 0; i < 100; i++ )
        {
            traceInfoReaderSimulators.add( new TraceInfoReaderSimulator( requests, 100 ) );
        }

        List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        threads.addAll( createThreadsForTraceInfoReaderSimulators( traceInfoReaderSimulators ) );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        System.out.println( "concurrent_100_threads_adding_random_requests, time: " + ( System.currentTimeMillis() - startTime ) );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertEquals( 50, requests.getList().size() );
        assertUniqueRequestNumbers( requests.getList() );
    }

    @Test
    public void concurrent_100_threads_adding_100_requests()
    {
        long startTime = System.currentTimeMillis();

        final int numberOfThreadsToStart = 100;

        LongestPortalRequests requests = new LongestPortalRequests( 50 );

        List<RequestSimulator> requestSimulators = new ArrayList<RequestSimulator>();
        for ( int i = 0; i < numberOfThreadsToStart; i++ )
        {
            requestSimulators.add( new RequestSimulator( requests, 100 ) );
        }

        final List<TraceInfoReaderSimulator> traceInfoReaderSimulators = new ArrayList<TraceInfoReaderSimulator>();
        for ( int i = 0; i < 100; i++ )
        {
            traceInfoReaderSimulators.add( new TraceInfoReaderSimulator( requests, 100 ) );
        }

        final List<Thread> threads = createThreadsForRequestSimulators( requestSimulators );
        threads.addAll( createThreadsForTraceInfoReaderSimulators( traceInfoReaderSimulators ) );
        startThreads( threads );
        waitForThreadsToFinish( threads );

        System.out.println( "concurrent_100_threads_adding_100_requests, time: " + ( System.currentTimeMillis() - startTime ) );

        assertNoExceptionsThrownForRequestSimulators( requestSimulators );
        assertEquals( 50, requests.getList().size() );
        assertUniqueRequestNumbers( requests.getList() );
    }

    private void assertNoExceptionsThrownForRequestSimulators( List<RequestSimulator> simulators )
    {
        for ( Simulator simulator : simulators )
        {
            assertTrue( "Simulator have thrown exception!", !simulator.exceptionThrown );
        }
    }

    private void assertUniqueRequestNumbers( List<PortalRequestTrace> traces )
    {
        final Set<Long> usedNumbers = new HashSet<Long>();
        for ( PortalRequestTrace trace : traces )
        {
            long currentCompletedNumber = trace.getCompletedNumber();
            assertTrue( currentCompletedNumber + "  duplicate completedNumber found!", !usedNumbers.contains( currentCompletedNumber ) );
            usedNumbers.add( currentCompletedNumber );
        }
    }

    private PortalRequestTrace createTrace( int requestNumber, String url, int durationInMilliseconds )
    {
        PortalRequestTrace trace = new PortalRequestTrace( requestNumber, url );
        DateTime startTime = new DateTime( 2010, 1, 1, 12, 0, 0, 0 );
        trace.setStartTime( startTime );
        trace.setStopTime( startTime.plusMillis( durationInMilliseconds ) );
        return trace;
    }

    private PortalRequestTrace createTrace()
    {
        long requestNumber = atomicRequestNumber.incrementAndGet();
        PortalRequestTrace trace = new PortalRequestTrace( requestNumber, "http://locahost:8080/site/0/home" );
        trace.setCompletedNumber( requestNumber );
        DateTime startTime = new DateTime( 2010, 1, 1, 12, 0, 0, 0 );
        trace.setStartTime( startTime );
        int durationInMilliseconds = random( 3, 999 );
        trace.setStopTime( startTime.plusMillis( durationInMilliseconds ) );
        return trace;
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

    private class RequestSimulator
        extends Simulator
        implements Runnable
    {
        private LongestPortalRequests requests;

        final int numberOfExecutions;

        RequestSimulator( LongestPortalRequests requests, int numberOfExecutions )
        {
            this.requests = requests;
            this.numberOfExecutions = numberOfExecutions;
        }

        @Override
        public void run()
        {
            try
            {
                for ( int i = 0; i < numberOfExecutions; i++ )
                {
                    PortalRequestTrace trace = createTrace();
                    requests.add( trace );
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
        private LongestPortalRequests requests;

        final int numberOfExecutions;

        private int[] sleepsBeforeReading;

        long lastCompletedRequestNumber = -1;

        TraceInfoReaderSimulator( LongestPortalRequests requests, int numberOfExecutions )
        {
            this.requests = requests;
            this.numberOfExecutions = numberOfExecutions;

            sleepsBeforeReading = new int[numberOfExecutions];
            for ( int i = 0; i < numberOfExecutions; i++ )
            {
                sleepsBeforeReading[i] = random( 3, 9 );
            }
        }

        @Override
        public void run()
        {
            try
            {
                for ( int i = 0; i < numberOfExecutions; i++ )
                {
                    List<PortalRequestTrace> listSince = requests.getList();
                    if ( listSince.size() > 0 )
                    {
                        lastCompletedRequestNumber = listSince.get( 0 ).getCompletedNumber();
                    }

                    //noinspection UnusedDeclaration
                    for ( PortalRequestTrace trace : listSince )
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
