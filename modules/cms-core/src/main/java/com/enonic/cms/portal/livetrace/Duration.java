/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import com.google.common.base.Preconditions;

/**
 * Oct 11, 2010
 */
public class Duration
{
    private DateTime startTime;

    private DateTime stopTime;

    private long executionTimeInMilliseconds;

    private Period dateTimeDuration;

    private String executionTimeAsHRFormat;

    private static final PeriodFormatter hoursMinutesMillis =
        new PeriodFormatterBuilder().appendHours().appendSuffix( " h ", " h " ).appendMinutes().appendSuffix( " m ",
                                                                                                              " m " ).appendSeconds().appendSuffix(
            " s ", " s " ).appendMillis().appendSuffix( " ms", " ms" ).toFormatter();

    public boolean hasStarted()
    {
        return startTime != null;
    }

    public DateTime getStartTime()
    {
        return startTime;
    }

    public Date getStartTimeAsDate()
    {
        if ( startTime == null )
        {
            return null;
        }
        return startTime.toDate();
    }

    void setStartTime( DateTime time )
    {
        this.startTime = time;
    }

    public boolean hasEnded()
    {
        return stopTime != null;
    }

    public DateTime getStopTime()
    {
        return stopTime;
    }

    public Date getStopTimeAsDate()
    {
        if ( stopTime == null )
        {
            return null;
        }
        return stopTime.toDate();
    }

    void setStopTime( DateTime stopTime )
    {
        Preconditions.checkNotNull( stopTime );
        this.stopTime = stopTime;
        this.executionTimeInMilliseconds = stopTime.getMillis() - startTime.getMillis();
        this.dateTimeDuration = new Period( startTime, stopTime );
        this.executionTimeAsHRFormat = hoursMinutesMillis.print( dateTimeDuration );
    }

    public long getExecutionTimeInMilliseconds()
    {
        if ( hasEnded() )
        {
            return executionTimeInMilliseconds;
        }
        else if ( startTime != null )
        {
            return new DateTime().getMillis() - startTime.getMillis();
        }
        else
        {
            return 0L;
        }
    }

    public String getExecutionTimeAsHRFormat()
    {
        if ( hasEnded() )
        {
            return executionTimeAsHRFormat;
        }
        else if ( startTime != null )
        {
            return hoursMinutesMillis.print( new Period( startTime, new DateTime() ) );
        }
        else
        {
            return "0";
        }
    }

    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "startTime: " ).append( startTime ).append( "\n" );
        s.append( "stopTime: " ).append( stopTime ).append( "\n" );
        s.append( "executionTimeInMilliseconds: " ).append( executionTimeInMilliseconds ).append( "\n" );
        return s.toString();
    }
}
