package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class DurationTest
{
    @Test(expected = NullPointerException.class)
    public void setStopTime_throws_exception_when_startTime_is_null()
    {
        Duration duration = new Duration();
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 0, 1, 0 ) );
    }

    @Test
    public void hasStarted_returns_false_when_startTime_is_null()
    {
        Duration duration = new Duration();
        assertEquals( false, duration.hasStarted() );
    }

    @Test
    public void hasStarted_returns_true_when_startTime_is_not_null()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        assertEquals( true, duration.hasStarted() );
    }

    @Test
    public void hasStarted_returns_true_when_startTime_is_not_null_and_stopTime_is_not_null()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 1, 0, 0 ) );
        assertEquals( true, duration.hasStarted() );
    }

    @Test
    public void hasEnded_returns_false_when_startTime_is_null()
    {
        Duration duration = new Duration();
        assertEquals( false, duration.hasEnded() );
    }

    @Test
    public void hasEnded_returns_true_when_startTime_is_not_null_and_stopTime_is_not_null()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 1, 0, 0 ) );
        assertEquals( true, duration.hasEnded() );
    }

    @Test
    public void getStartTime_returns_startTime_when_startTime_is_set()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        assertEquals( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ), duration.getStartTime() );
    }

    @Test
    public void getStopTime_returns_stopTime_when_stopTime_is_set()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 1, 0, 0 ) );
        assertEquals( new DateTime( 2011, 11, 1, 12, 1, 0, 0 ), duration.getStopTime() );
    }

    @Test
    public void getAsMilliseconds_returns_1000_when_stop_minus_start_is_one_second()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 0, 1, 0 ) );
        assertEquals( 1000, duration.getAsMilliseconds() );
    }

    @Test
    public void getAsHRFormat_returns_1_s_when_stop_minus_start_is_one_second()
    {
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( 2011, 11, 1, 12, 0, 0, 0 ) );
        duration.setStopTime( new DateTime( 2011, 11, 1, 12, 0, 1, 0 ) );
        assertEquals( "1 s", duration.getAsHRFormat() );
    }

    @Test
    public void getAsHRFormat_returns_something_even_if_stopTime_is_not_set()
    {
        long currentTime = System.currentTimeMillis();
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( currentTime ) );
        assertTrue( duration.getAsHRFormat().length() > 0 );
    }

    @Test
    public void getAsMilliseconds_returns_something_even_if_stopTime_is_not_set()
    {
        long currentTime = System.currentTimeMillis();
        try
        {
            Thread.sleep( 500 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        Duration duration = new Duration();
        duration.setStartTime( new DateTime( currentTime ) );
        assertTrue( duration.getAsMilliseconds() >= 500 );
    }
}
