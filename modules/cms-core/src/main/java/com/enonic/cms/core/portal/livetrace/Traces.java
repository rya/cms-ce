package com.enonic.cms.core.portal.livetrace;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


public class Traces<T extends Trace>
    implements Iterable<T>
{
    private static final PeriodFormatter hoursMinutesMillis =
        new PeriodFormatterBuilder().appendHours().appendSuffix( " h ", " h " ).appendMinutes().appendSuffix( " m ",
                                                                                                              " m " ).appendSeconds().appendSuffix(
            " s ", " s " ).appendMillis().appendSuffix( " ms", " ms" ).toFormatter();

    private List<T> list = new ArrayList<T>();

    private int totalPeriodTimeInMilliseconds = 0;

    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    public void add( T trace )
    {
        synchronized ( list )
        {
            list.add( trace );
            computeTotalPeriod();
        }
    }

    public boolean hasTraces()
    {
        return !list.isEmpty();
    }

    public List<T> getList()
    {
        return list;
    }

    public int getTotalPeriodInMilliseconds()
    {
        synchronized ( list )
        {
            computeTotalPeriod();
            return totalPeriodTimeInMilliseconds;
        }
    }

    public String getTotalPeriodInHRFormat()
    {
        synchronized ( list )
        {
            computeTotalPeriod();
            Period period = new Period( totalPeriodTimeInMilliseconds );
            return hoursMinutesMillis.print( period );
        }
    }

    private void computeTotalPeriod()
    {
        totalPeriodTimeInMilliseconds = 0;

        for ( Trace trace : list )
        {
            totalPeriodTimeInMilliseconds += trace.getDuration().getAsMilliseconds();
        }
    }
}
