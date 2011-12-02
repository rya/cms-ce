package com.enonic.cms.core.portal.livetrace;


import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleDurationTest
{
    @Test
    public void getAsMilliseconds_returns_1000_when_stop_minus_start_is_one_second()
    {
        SimpleDuration duration = new SimpleDuration();
        duration.setDurationInMilliseconds( 1000 );
        assertEquals( 1000, duration.getAsMilliseconds() );
    }

    @Test
    public void getAsHRFormat_returns_correct_when_duration_is_one_second()
    {
        SimpleDuration duration = new SimpleDuration();
        duration.setDurationInMilliseconds( 1000 );
        assertEquals( "1 s", duration.getAsHRFormat() );
    }

    @Test
    public void getAsHRFormat_returns_correct_when_duration_is_one_second_and_one_millisecond()
    {
        SimpleDuration duration = new SimpleDuration();
        duration.setDurationInMilliseconds( 1001 );
        assertEquals( "1 s 1 ms", duration.getAsHRFormat() );
    }

    @Test
    public void getAsHRFormat_returns_correct_when_duration_is_one_minute_one_second_and_one_millisecond()
    {
        SimpleDuration duration = new SimpleDuration();
        duration.setDurationInMilliseconds( ( 60000 + 1000 + 1 ) );
        assertEquals( "1 m 1 s 1 ms", duration.getAsHRFormat() );
    }

    @Test
    public void getAsHRFormat_returns_correct_when_duration_is_one_hour_and_one_minute_one_second_and_one_millisecond()
    {
        SimpleDuration duration = new SimpleDuration();
        duration.setDurationInMilliseconds( ( ( 60 * 60000 ) + 60000 + 1000 + 1 ) );
        assertEquals( "1 h 1 m 1 s 1 ms", duration.getAsHRFormat() );
    }
}
