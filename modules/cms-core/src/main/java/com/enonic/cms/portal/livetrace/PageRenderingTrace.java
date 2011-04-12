/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 * Oct 6, 2010
 */
public class PageRenderingTrace
{
    private PortalRequestTrace portalRequestTrace;

    private Duration duration = new Duration();

    private QualifiedUsername renderer;

    private boolean usedCachedResult = false;

    private List<WindowRenderingTrace> windowRenderingTraces = new ArrayList<WindowRenderingTrace>();

    PageRenderingTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    public PortalRequestTrace getPortalRequestTrace()
    {
        return portalRequestTrace;
    }

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

    public QualifiedUsername getRenderer()
    {
        return renderer;
    }

    public void setRenderer( QualifiedUsername renderer )
    {
        this.renderer = renderer;
    }

    public boolean isUsedCachedResult()
    {
        return usedCachedResult;
    }

    public void setUsedCachedResult( boolean value )
    {
        this.usedCachedResult = value;
    }

    void addWindowRenderingTrace( WindowRenderingTrace trace )
    {
        windowRenderingTraces.add( trace );
    }

    public boolean hasWindowRenderingTraces()
    {
        return !windowRenderingTraces.isEmpty();
    }

    public List<WindowRenderingTrace> getWindowRenderingTraces()
    {
        return windowRenderingTraces;
    }
}
