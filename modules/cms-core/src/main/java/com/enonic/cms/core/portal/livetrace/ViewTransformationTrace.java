package com.enonic.cms.core.portal.livetrace;

import org.joda.time.DateTime;

public class ViewTransformationTrace
    implements Trace
{
    private Duration duration = new Duration();

    private String view;

    void setStartTime( DateTime startTime )
    {
        this.duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        this.duration.setStopTime( stopTime );
    }

    public Duration getDuration()
    {
        return this.duration;
    }

    public String getView()
    {
        return view;
    }

    void setView( String view )
    {
        this.view = view;
    }
}
