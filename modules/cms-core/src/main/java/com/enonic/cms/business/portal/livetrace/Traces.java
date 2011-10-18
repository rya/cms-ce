package com.enonic.cms.business.portal.livetrace;


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

    private int totalExecutionTimeInMilliseconds = 0;

    @Override
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    public void add( T trace )
    {
        list.add( trace );
        computeTotalPeriod();
    }

    public boolean hasTraces()
    {
        return !list.isEmpty();
    }

    public List<T> getList()
    {
        return list;
    }

    public String getTotalPeriodInHRFormat()
    {
        return computeTotalPeriod();
    }

    private String computeTotalPeriod()
    {
        totalExecutionTimeInMilliseconds = 0;

        for ( Trace trace : list )
        {
            totalExecutionTimeInMilliseconds += trace.getDuration().getExecutionTimeInMilliseconds();
        }

        Period period = new Period( totalExecutionTimeInMilliseconds );
        return hoursMinutesMillis.print( period );
    }
}
