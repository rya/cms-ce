/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.livetrace;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import com.enonic.cms.core.time.TimeService;

/**
 * Oct 6, 2010
 */
public class LivePortalTraceServiceImpl
    implements LivePortalTraceService
{
    private static final Logger LOG = LoggerFactory.getLogger( LivePortalTraceServiceImpl.class );

    private static AtomicLong requestCounter = new AtomicLong();

    private TimeService timeService;

    private boolean enabled = false;

    private int historySize;

    private int longestSize;

    private CurrentPortalRequests currentPortalRequests = new CurrentPortalRequests();

    private HistoryOfPortalRequests historyOfPortalRequests;

    private LongestPortalRequests longestPortalPageRequests;

    private LongestPortalRequests longestPortalAttachmentRequests;

    private LongestPortalRequests longestPortalImageRequests;

    private final static ThreadLocal<PortalRequestTrace> PORTAL_REQUEST_TRACE_THREAD_LOCAL = new ThreadLocal<PortalRequestTrace>();

    private final static ThreadLocal<PageRenderingTrace> PAGE_RENDERING_TRACE_THREAD_LOCAL = new ThreadLocal<PageRenderingTrace>();

    private final static ThreadLocal<DatasourceExecutionTrace> DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL =
        new ThreadLocal<DatasourceExecutionTrace>();

    private final static ThreadLocal<ClientMethodExecutionTrace> CLIENT_METHOD_EXECUTION_TRACE_THREAD_LOCAL =
        new ThreadLocal<ClientMethodExecutionTrace>();

    private final static ThreadLocal<WindowRenderingTrace> WINDOW_RENDERING_TRACE_THREAD_LOCAL = new ThreadLocal<WindowRenderingTrace>();

    private final static ThreadLocal<ImageRequestTrace> IMAGE_REQUEST_TRACE_THREAD_LOCAL = new ThreadLocal<ImageRequestTrace>();

    @PostConstruct
    public void init()
    {
        if ( enabled )
        {
            LOG.info( "Live Portal Tracing is enabled [historySize=" + historySize + ", longestSize=" + longestSize + "]" );

            longestPortalPageRequests = new LongestPortalRequests( longestSize );
            longestPortalAttachmentRequests = new LongestPortalRequests( longestSize );
            longestPortalImageRequests = new LongestPortalRequests( longestSize );
            historyOfPortalRequests = new HistoryOfPortalRequests( historySize );
        }
        else
        {
            LOG.info( "Live Portal Tracing is not enabled" );
        }
    }

    public boolean tracingEnabled()
    {
        return enabled;
    }

    public PortalRequestTrace startPortalRequestTracing( String url )
    {
        checkEnabled();

        final long requestNumber = requestCounter.incrementAndGet();
        PortalRequestTrace portalRequestTrace = new PortalRequestTrace( requestNumber, url );
        currentPortalRequests.add( portalRequestTrace );

        portalRequestTrace.setStartTime( timeService.getNowAsDateTime() );

        PORTAL_REQUEST_TRACE_THREAD_LOCAL.set( portalRequestTrace );
        PAGE_RENDERING_TRACE_THREAD_LOCAL.set( null );
        WINDOW_RENDERING_TRACE_THREAD_LOCAL.set( null );
        DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.set( null );
        CLIENT_METHOD_EXECUTION_TRACE_THREAD_LOCAL.set( null );
        return portalRequestTrace;
    }

    public void stopTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        portalRequestTrace.setStopTime( timeService.getNowAsDateTime() );
        currentPortalRequests.remove( portalRequestTrace );

        historyOfPortalRequests.add( portalRequestTrace );

        if ( portalRequestTrace.hasPageRenderingTrace() || portalRequestTrace.hasWindowRenderingTrace() )
        {
            longestPortalPageRequests.add( portalRequestTrace );
        }
        else if ( portalRequestTrace.hasAttachmentRequsetTrace() )
        {
            longestPortalAttachmentRequests.add( portalRequestTrace );
        }
        else if ( portalRequestTrace.hasImageRequestTrace() )
        {
            longestPortalImageRequests.add( portalRequestTrace );
        }

        PORTAL_REQUEST_TRACE_THREAD_LOCAL.set( null );
    }

    public PageRenderingTrace startPageRenderTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        PageRenderingTrace pageRenderTrace = new PageRenderingTrace( portalRequestTrace );
        pageRenderTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setPageRenderingTrace( pageRenderTrace );

        PAGE_RENDERING_TRACE_THREAD_LOCAL.set( pageRenderTrace );

        return pageRenderTrace;
    }

    public void stopTracing( PageRenderingTrace pageRenderTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( pageRenderTrace );

        pageRenderTrace.setStopTime( timeService.getNowAsDateTime() );

        PAGE_RENDERING_TRACE_THREAD_LOCAL.set( null );
    }

    public WindowRenderingTrace startWindowRenderTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        WindowRenderingTrace windowRenderingTrace;
        if ( portalRequestTrace.hasPageRenderingTrace() )
        {
            windowRenderingTrace = new WindowRenderingTrace( portalRequestTrace, portalRequestTrace.getPageRenderingTrace() );
            portalRequestTrace.getPageRenderingTrace().addWindowRenderingTrace( windowRenderingTrace );
        }
        else
        {
            windowRenderingTrace = new WindowRenderingTrace( portalRequestTrace );
            portalRequestTrace.setWindowRenderingTrace( windowRenderingTrace );
        }
        windowRenderingTrace.setStartTime( timeService.getNowAsDateTime() );

        WINDOW_RENDERING_TRACE_THREAD_LOCAL.set( windowRenderingTrace );

        return windowRenderingTrace;
    }

    public DatasourceExecutionTrace startPageTemplateDatasourceExecutionTracing( String datasourceMethodName )
    {
        checkEnabled();

        PageRenderingTrace pageRenderingTrace = PAGE_RENDERING_TRACE_THREAD_LOCAL.get();
        if ( pageRenderingTrace == null )
        {
            return null;
        }

        DatasourceExecutionTrace datasourceExecutionTrace = new DatasourceExecutionTrace( datasourceMethodName );
        datasourceExecutionTrace.setStartTime( timeService.getNowAsDateTime() );

        pageRenderingTrace.addDatasourceExecutionTrace( datasourceExecutionTrace );

        DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.set( datasourceExecutionTrace );

        return datasourceExecutionTrace;
    }

    public DatasourceExecutionTrace startPortletDatasourceExecutionTracing( String datasourceMethodName )
    {
        checkEnabled();
        WindowRenderingTrace windowRenderingTrace = WINDOW_RENDERING_TRACE_THREAD_LOCAL.get();
        if ( windowRenderingTrace == null )
        {
            return null;
        }

        DatasourceExecutionTrace datasourceExecutionTrace = new DatasourceExecutionTrace( datasourceMethodName );
        datasourceExecutionTrace.setStartTime( timeService.getNowAsDateTime() );

        windowRenderingTrace.addDatasourceExecutionTrace( datasourceExecutionTrace );

        DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.set( datasourceExecutionTrace );

        return datasourceExecutionTrace;
    }

    public ClientMethodExecutionTrace startClientMethodExecutionTracing( String methodName )
    {
        checkEnabled();
        Preconditions.checkNotNull( methodName );

        DatasourceExecutionTrace currentDatasourceExecutionTrace = DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.get();
        if ( currentDatasourceExecutionTrace == null )
        {
            return null;
        }

        ClientMethodExecutionTrace trace = new ClientMethodExecutionTrace();
        trace.setMethodName( methodName );
        trace.setStartTime( timeService.getNowAsDateTime() );
        currentDatasourceExecutionTrace.addClientMethodExecutionTrace( trace );

        CLIENT_METHOD_EXECUTION_TRACE_THREAD_LOCAL.set( trace );

        return trace;
    }

    public ContentIndexQueryTrace startContentIndexQueryTracing()
    {
        checkEnabled();
        ContentIndexQuerier currentQuerier = getCurrentContentIndexQuerier();
        if ( currentQuerier != null )
        {
            ContentIndexQueryTrace trace = new ContentIndexQueryTrace();
            trace.setStartTime( timeService.getNowAsDateTime() );
            currentQuerier.addContentIndexQueryTrace( trace );
            return trace;
        }
        else
        {
            return null;
        }
    }

    public ViewTransformationTrace startViewTransformationTracing()
    {
        checkEnabled();

        final ViewTransformationTrace trace = new ViewTransformationTrace();
        trace.setStartTime( timeService.getNowAsDateTime() );

        final WindowRenderingTrace windowRenderingTrace = WINDOW_RENDERING_TRACE_THREAD_LOCAL.get();
        if ( windowRenderingTrace != null )
        {
            windowRenderingTrace.setViewTransformationTrace( trace );
        }
        else
        {
            final PageRenderingTrace pageRenderingTrace = PAGE_RENDERING_TRACE_THREAD_LOCAL.get();
            if ( pageRenderingTrace != null )
            {
                pageRenderingTrace.setViewTransformationTrace( trace );
            }
            else
            {
                return null;
            }
        }

        return trace;
    }

    public InstructionPostProcessingTrace startInstructionPostProcessingTracingForWindow()
    {
        checkEnabled();
        WindowRenderingTrace windowRenderingTrace = WINDOW_RENDERING_TRACE_THREAD_LOCAL.get();
        if ( windowRenderingTrace == null )
        {
            return null;
        }

        InstructionPostProcessingTrace instructionPostProcessingTrace = new InstructionPostProcessingTrace();
        instructionPostProcessingTrace.setStartTime( timeService.getNowAsDateTime() );
        windowRenderingTrace.setInstructionPostProcessingTrace( instructionPostProcessingTrace );
        return instructionPostProcessingTrace;
    }

    public InstructionPostProcessingTrace startInstructionPostProcessingTracingForPage()
    {
        checkEnabled();
        PageRenderingTrace pageRenderingTrace = PAGE_RENDERING_TRACE_THREAD_LOCAL.get();
        if ( pageRenderingTrace == null )
        {
            return null;
        }

        InstructionPostProcessingTrace instructionPostProcessingTrace = new InstructionPostProcessingTrace();
        instructionPostProcessingTrace.setStartTime( timeService.getNowAsDateTime() );
        pageRenderingTrace.setInstructionPostProcessingTrace( instructionPostProcessingTrace );
        return instructionPostProcessingTrace;
    }

    public void stopTracing( WindowRenderingTrace windowRenderingTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( windowRenderingTrace );

        windowRenderingTrace.setStopTime( timeService.getNowAsDateTime() );

        WINDOW_RENDERING_TRACE_THREAD_LOCAL.set( null );
    }

    public AttachmentRequestTrace startAttachmentRequestTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        AttachmentRequestTrace newTrace = new AttachmentRequestTrace( portalRequestTrace );
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setAttachmentRequestTrace( newTrace );
        return newTrace;
    }

    public ImageRequestTrace startImageRequestTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        ImageRequestTrace newTrace = new ImageRequestTrace( portalRequestTrace );
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setImageRequestTrace( newTrace );
        IMAGE_REQUEST_TRACE_THREAD_LOCAL.set( newTrace );
        return newTrace;
    }

    public void stopTracing( AttachmentRequestTrace attachmentRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( attachmentRequestTrace );

        attachmentRequestTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public void stopTracing( DatasourceExecutionTrace datasourceExecutionTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( datasourceExecutionTrace );

        datasourceExecutionTrace.setStopTime( timeService.getNowAsDateTime() );

        DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.set( null );
    }

    public void stopTracing( ClientMethodExecutionTrace clientMethodExecutionTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( clientMethodExecutionTrace );

        clientMethodExecutionTrace.setStopTime( timeService.getNowAsDateTime() );

        CLIENT_METHOD_EXECUTION_TRACE_THREAD_LOCAL.set( null );
    }

    public void stopTracing( ViewTransformationTrace trace )
    {
        checkEnabled();
        Preconditions.checkNotNull( trace );

        trace.setStopTime( timeService.getNowAsDateTime() );
    }

    public void stopTracing( ContentIndexQueryTrace contentIndexQueryTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( contentIndexQueryTrace );

        contentIndexQueryTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public void stopTracing( InstructionPostProcessingTrace instructionPostProcessingTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( instructionPostProcessingTrace );

        if ( WINDOW_RENDERING_TRACE_THREAD_LOCAL.get() == null && PAGE_RENDERING_TRACE_THREAD_LOCAL != null )
        {
            int windowsTotalPeriod =
                PAGE_RENDERING_TRACE_THREAD_LOCAL.get().getWindowRenderingTracesAsTraces().getTotalPeriodInMilliseconds();
            final long now = timeService.getNowAsDateTime().getMillis();
            final long startTime = instructionPostProcessingTrace.getStartTime().getMillis();
            final long duration = ( now - startTime ) - windowsTotalPeriod;
            instructionPostProcessingTrace.setDurationInMilliseconds( duration );
        }
        else if ( WINDOW_RENDERING_TRACE_THREAD_LOCAL.get() != null )
        {
            final long now = timeService.getNowAsDateTime().getMillis();
            final long startTime = instructionPostProcessingTrace.getStartTime().getMillis();
            final long duration = now - startTime;
            instructionPostProcessingTrace.setDurationInMilliseconds( duration );
        }
    }

    public void stopTracing( ImageRequestTrace imageRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( imageRequestTrace );

        imageRequestTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public int getNumberOfPortalRequestTracesInProgress()
    {
        checkEnabled();
        return currentPortalRequests.getSize();
    }

    public List<PortalRequestTrace> getCurrentPortalRequestTraces()
    {
        checkEnabled();

        return currentPortalRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalPageRequestTraces()
    {
        checkEnabled();

        return longestPortalPageRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalAttachmentRequestTraces()
    {
        checkEnabled();

        return longestPortalAttachmentRequests.getList();
    }

    public List<PortalRequestTrace> getLongestTimePortalImageRequestTraces()
    {
        checkEnabled();

        return longestPortalImageRequests.getList();
    }

    public List<PortalRequestTrace> getHistorySince( long historyRecordNumber )
    {
        checkEnabled();

        return historyOfPortalRequests.getListSince( historyRecordNumber );
    }

    public PortalRequestTrace getCurrentPortalRequestTrace()
    {
        return PORTAL_REQUEST_TRACE_THREAD_LOCAL.get();
    }

    public DatasourceExecutionTrace getCurrentDatasourceExecutionTrace()
    {
        return DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.get();
    }

    public ImageRequestTrace getCurrentImageRequestTrace()
    {
        return IMAGE_REQUEST_TRACE_THREAD_LOCAL.get();
    }

    public void clearLongestPageRequestsTraces()
    {
        longestPortalPageRequests.clear();
    }

    public void clearLongestAttachmentRequestTraces()
    {
        longestPortalAttachmentRequests.clear();
    }

    public void clearLongestImageRequestTraces()
    {
        longestPortalImageRequests.clear();
    }

    private void checkEnabled()
    {
        Preconditions.checkArgument( enabled, "Unexpected call when Live Portal Tracing is disabled" );
    }

    private ContentIndexQuerier getCurrentContentIndexQuerier()
    {
        final ClientMethodExecutionTrace currentClientTrace = CLIENT_METHOD_EXECUTION_TRACE_THREAD_LOCAL.get();
        if ( currentClientTrace != null )
        {
            return currentClientTrace;
        }
        return DATASOURCE_EXECUTION_TRACE_THREAD_LOCAL.get();
    }

    @Autowired
    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setEnabled( String enabled )
    {
        this.enabled = Boolean.valueOf( enabled );
    }

    public void setHistorySize( int value )
    {
        this.historySize = value;
    }

    public void setLongestSize( int value )
    {
        this.longestSize = value;
    }
}
