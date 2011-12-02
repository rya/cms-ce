/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;

import org.joda.time.DateTime;

import com.enonic.cms.core.security.user.QualifiedUsername;

/**
 * Oct 6, 2010
 */
public class WindowRenderingTrace
    implements Trace
{
    private PortalRequestTrace portalRequestTrace;

    private PageRenderingTrace pageRenderingTrace;

    private String portletName;

    private Duration duration = new Duration();

    private QualifiedUsername renderer;

    private boolean cacheable = false;

    private boolean usedCachedResult = false;

    private long concurrencyBlockStartTime = 0;

    private long concurrencyBlockingTime = 0;

    private Traces<DatasourceExecutionTrace> datasourceExecutionTraces = new Traces<DatasourceExecutionTrace>();

    private ViewTransformationTrace viewTransformationTrace;

    private InstructionPostProcessingTrace instructionPostProcessingTrace;

    WindowRenderingTrace( PortalRequestTrace portalRequestTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
    }

    WindowRenderingTrace( PortalRequestTrace portalRequestTrace, PageRenderingTrace pageRenderingTrace )
    {
        this.portalRequestTrace = portalRequestTrace;
        this.pageRenderingTrace = pageRenderingTrace;
    }

    public String getPortletName()
    {
        return portletName;
    }

    public void setPortletName( String portletName )
    {
        this.portletName = portletName;
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

    public boolean isCacheable()
    {
        return cacheable;
    }

    void setCacheable( boolean cacheable )
    {
        this.cacheable = cacheable;
    }

    public boolean isUsedCachedResult()
    {
        return usedCachedResult;
    }

    public void setUsedCachedResult( boolean value )
    {
        this.usedCachedResult = value;
    }

    public boolean isConcurrencyBlocked()
    {
        return concurrencyBlockingTime > CONCURRENCY_BLOCK_THRESHOLD;
    }

    public long getConcurrencyBlockingTime()
    {
        return isConcurrencyBlocked() ? concurrencyBlockingTime : 0;
    }

    void startConcurrencyBlockTimer()
    {
        concurrencyBlockStartTime = System.currentTimeMillis();
    }

    void stopConcurrencyBlockTimer()
    {
        this.concurrencyBlockingTime = System.currentTimeMillis() - concurrencyBlockStartTime;
    }

    public void addDatasourceExecutionTrace( DatasourceExecutionTrace trace )
    {
        this.datasourceExecutionTraces.add( trace );
    }

    public boolean hasDatasourceExecutionTraces()
    {
        return datasourceExecutionTraces.hasTraces();
    }

    public String getDurationOfDatasourceExecutionTracesInHRFormat()
    {
        return datasourceExecutionTraces.getTotalPeriodInHRFormat();
    }

    public List<DatasourceExecutionTrace> getDatasourceExecutionTraces()
    {
        return datasourceExecutionTraces.getList();
    }

    public boolean hasViewTransformationTrace()
    {
        return viewTransformationTrace != null;
    }

    public ViewTransformationTrace getViewTransformationTrace()
    {
        return viewTransformationTrace;
    }

    void setViewTransformationTrace( ViewTransformationTrace viewTransformationTrace )
    {
        this.viewTransformationTrace = viewTransformationTrace;
    }

    public boolean hasInstructionPostProcessingTrace()
    {
        return instructionPostProcessingTrace != null;
    }

    public InstructionPostProcessingTrace getInstructionPostProcessingTrace()
    {
        return instructionPostProcessingTrace;
    }

    void setInstructionPostProcessingTrace( InstructionPostProcessingTrace instructionPostProcessingTrace )
    {
        this.instructionPostProcessingTrace = instructionPostProcessingTrace;
    }
}
