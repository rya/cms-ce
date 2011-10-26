package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;

public class InstructionPostProcessingTrace
    implements Trace
{
    private Duration duration = new Duration();

    void setStartTime( DateTime startTime )
    {
        duration.setStartTime( startTime );
    }

    void setStopTime( DateTime stopTime )
    {
        duration.setStopTime( stopTime );
    }

    public Duration getDuration()
    {
        return duration;
    }

}
