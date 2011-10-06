/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.index.util;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.ReadableDateTime;

import junit.framework.TestCase;

public class QueryFunctionsTest
    extends TestCase
{

    public void testNow()
    {
        DateTime before = new DateTime().minusHours( 1 );
        DateTime now = QueryFunctions.now();
        DateTime after = new DateTime().plusHours( 1 );
        assertTrue( "Comparing before with now gave", before.isBefore( now ) );
        assertTrue( "Comparing after with now gave", after.isAfter( now ) );
    }

    public void testToday()
    {
        DateTime before = new DateTime().minusDays( 1 );
        ReadableDateTime today = QueryFunctions.today();
        DateTime after = new DateTime().plusDays( 1 );

        assertTrue( "Comparing before with now gave: ", before.isBefore( today ) );
        assertTrue( "Comparing after with now gave: ", after.isAfter( today ) );
        assertEquals( "The hour field should be 0", 0, today.getHourOfDay() );
        assertEquals( "The minute field should be 0", 0, today.getMinuteOfHour() );
        assertEquals( "The second field should be 0", 0, today.getSecondOfMinute() );
        assertEquals( "The milliseconds field should be 0", 0, today.getMillisOfSecond() );
    }

    public void testCompleteDateTime()
    {
        ReadableDateTime xmasEve = QueryFunctions.date( "2008-12-24 16:30:15,454" );
        assertEquals( "Wrong year", 2008, xmasEve.getYear() );
        assertEquals( "Wrong month", 12, xmasEve.getMonthOfYear() );
        assertEquals( "Wrong day", 24, xmasEve.getDayOfMonth() );
        assertEquals( "Wrong hour", 16, xmasEve.getHourOfDay() );
        assertEquals( "Wrong minute", 30, xmasEve.getMinuteOfHour() );
        assertEquals( "Wrong seconds", 15, xmasEve.getSecondOfMinute() );
        assertEquals( "Wrong millisecs", 0, xmasEve.getMillisOfSecond() );
    }

    public void testDate()
    {
        ReadableDateTime xmasEve = QueryFunctions.date( "2008-12-24" );
        assertTrue( "Wrong implementing class", xmasEve instanceof DateMidnight );
        assertEquals( "Wrong year", 2008, xmasEve.getYear() );
        assertEquals( "Wrong month", 12, xmasEve.getMonthOfYear() );
        assertEquals( "Wrong day", 24, xmasEve.getDayOfMonth() );
        assertEquals( "Wrong hour", 0, xmasEve.getHourOfDay() );
        assertEquals( "Wrong minute", 0, xmasEve.getMinuteOfHour() );
        assertEquals( "Wrong seconds", 0, xmasEve.getSecondOfMinute() );
        assertEquals( "Wrong millisecs", 0, xmasEve.getMillisOfSecond() );
    }

    public void testDateWithoutSeconds()
    {
        ReadableDateTime xmasEve = QueryFunctions.date( "2008-12-24 16:30" );
        assertEquals( "Wrong year", 2008, xmasEve.getYear() );
        assertEquals( "Wrong month", 12, xmasEve.getMonthOfYear() );
        assertEquals( "Wrong day", 24, xmasEve.getDayOfMonth() );
        assertEquals( "Wrong hour", 16, xmasEve.getHourOfDay() );
        assertEquals( "Wrong minute", 30, xmasEve.getMinuteOfHour() );
        assertEquals( "Wrong seconds", 0, xmasEve.getSecondOfMinute() );
        assertEquals( "Wrong millisecs", 0, xmasEve.getMillisOfSecond() );
    }
}
