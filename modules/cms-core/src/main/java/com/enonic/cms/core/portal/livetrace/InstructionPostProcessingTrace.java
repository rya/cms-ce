package com.enonic.cms.core.portal.livetrace;


import org.joda.time.DateTime;

public class InstructionPostProcessingTrace
    implements Trace
{
    private SimpleDuration duration = new SimpleDuration();

    private DateTime startTime = null;

    void setStartTime( DateTime startTime )
    {
        this.startTime = startTime;
    }

    DateTime getStartTime()
    {
        return startTime;
    }

    void setDurationInMilliseconds( long milliseconds )
    {
        duration.setDurationInMilliseconds( milliseconds );
    }

    public SimpleDuration getDuration()
    {
        return duration;
    }

}
