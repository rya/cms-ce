package com.enonic.cms.core.portal.livetrace;

import java.util.List;

import org.joda.time.DateTime;

public class ClientMethodExecutionTrace
    implements Trace, ContentIndexQuerier
{
    private Duration duration = new Duration();

    private String methodName;

    private Traces<ContentIndexQueryTrace> contentIndexQueryTraces = new Traces<ContentIndexQueryTrace>();

    void setStartTime( DateTime startTime )
    {
        duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        duration.setStopTime( stopTime );
    }

    public String getMethodName()
    {
        return methodName;
    }

    void setMethodName( String methodName )
    {
        this.methodName = methodName;
    }

    public Duration getDuration()
    {
        return duration;
    }

    @Override
    public void addContentIndexQueryTrace( ContentIndexQueryTrace trace )
    {
        contentIndexQueryTraces.add( trace );
    }

    public boolean hasContentIndexQueryTraces()
    {
        return contentIndexQueryTraces.hasTraces();
    }

    public String getDurationOfContentIndexQueryTracesInHRFormat()
    {
        return contentIndexQueryTraces.getTotalPeriodInHRFormat();
    }

    public List<ContentIndexQueryTrace> getContentIndexQueryTraces()
    {
        return contentIndexQueryTraces.getList();
    }
}
