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

public class HistoryOfPortalRequestsTest
{
    private final static Random RANDOM_WHEEL = new SecureRandom();

    private AtomicLong atomicRequestNumber = new AtomicLong( 0 );

    @Test
    public void size_returns_1_after_one_trace_is_added()
    {
        HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

        PortalRequestTrace trace = createTrace( 1, "http://locahost:8080/site/0/home" );
        requests.add( trace );
        assertEquals( 1, requests.getSize() );
    }

    @Test
    public void getList_returns_1_item_after_trace_is_added()
    {
        HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

        PortalRequestTrace trace = createTrace( 1, "http://locahost:8080/site/0/home" );
        requests.add( trace );

        List<PortalRequestTrace> actualList = requests.getList();
        assertSame( 1, actualList.size() );
        assertSame( trace, actualList.get( 0 ) );
    }

    @Test
    public void getList_returns_items_in_opposite_order_as_they_where_inserted()
    {
        HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

        PortalRequestTrace trace1 = createTrace( 1, "http://locahost:8080/site/0/home" );
        requests.add( trace1 );
        PortalRequestTrace trace2 = createTrace( 2, "http://locahost:8080/site/0/home" );
        requests.add( trace2 );

        List<PortalRequestTrace> actualList = requests.getList();
        assertEquals( 2, actualList.size() );
        assertSame( trace2, actualList.get( 0 ) );
        assertSame( trace1, actualList.get( 1 ) );
    }

    @Test
    public void getListSince_returns_items_in_opposite_order_as_they_where_inserted()
    {
        HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

        PortalRequestTrace trace1 = new PortalRequestTrace( 1, "http://locahost:8080/site/0/home" );
        requests.add( trace1 );
        PortalRequestTrace trace2 = new PortalRequestTrace( 2, "http://locahost:8080/site/0/home" );
        requests.add( trace2 );

        List<PortalRequestTrace> actualList = requests.getListSince( 0 );
        assertEquals( 2, actualList.size() );
        assertSame( trace2, actualList.get( 0 ) );
        assertSame( trace1, actualList.get( 1 ) );
    }

    @Test
    public void concurrent_100_threads_adding_random_requests()
    {
        long startTime = System.currentTimeMillis();

        final int numberOfThreadsToStart = 100;

        final HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

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
        assertEquals( 1000, requests.getSize() );
        assertUniqueRequestNumbers( requests.getList() );
    }

    @Test
    public void concurrent_100_threads_adding_100_requests()
    {
        long startTime = System.currentTimeMillis();

        final int numberOfThreadsToStart = 100;

        final HistoryOfPortalRequests requests = new HistoryOfPortalRequests( 1000 );

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
        assertEquals( 1000, requests.getSize() );
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
            assertTrue( currentCompletedNumber + "  duplicate requestNumber found!", !usedNumbers.contains( currentCompletedNumber ) );
            usedNumbers.add( currentCompletedNumber );
        }
    }

    private PortalRequestTrace createTrace( long requestNumber, String url )
    {
        PortalRequestTrace trace = new PortalRequestTrace( requestNumber, url );
        trace.setCompletedNumber( requestNumber );
        return trace;
    }

    private PortalRequestTrace createTrace()
    {
        long requestNumber = atomicRequestNumber.incrementAndGet();
        PortalRequestTrace trace = new PortalRequestTrace( requestNumber, "http://locahost:8080/site/0/home" );
        trace.setCompletedNumber( requestNumber );
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
        private HistoryOfPortalRequests requests;

        final int numberOfExecutions;

        RequestSimulator( HistoryOfPortalRequests requests, int numberOfExecutions )
        {
            this.requests = requests;
            this.numberOfExecutions = numberOfExecutions;
        }

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
        private HistoryOfPortalRequests requests;

        final int numberOfExecutions;

        private int[] sleepsBeforeReading;

        long lastCompletedRequestNumber = -1;

        TraceInfoReaderSimulator( HistoryOfPortalRequests requests, int numberOfExecutions )
        {
            this.requests = requests;
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

                    requests.getSize();
                    List<PortalRequestTrace> listSince = requests.getListSince( lastCompletedRequestNumber );
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
