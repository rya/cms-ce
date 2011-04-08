/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.time.TimeService;

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

    private final static ThreadLocal<PortalRequestTrace> PORTAL_REQUEST_TRACE_THREAD_LOCAL = new ThreadLocal<PortalRequestTrace>();

    private final static ThreadLocal<AttachmentRequestTrace> ATTACHMENT_REQUEST_TRACE_THREAD_LOCAL = new ThreadLocal<AttachmentRequestTrace>();

    private final static ThreadLocal<BlobFetchingTrace> BLOB_FETCHING_TRACE_THREAD_LOCAL = new ThreadLocal<BlobFetchingTrace>();

    @PostConstruct
    public void init()
    {
        if ( enabled )
        {
            LOG.info( "Live Portal Tracing is enabled [historySize=" + historySize + ", longestSize=" + longestSize +
                              "]" );

            longestPortalPageRequests = new LongestPortalRequests( longestSize );
            longestPortalAttachmentRequests = new LongestPortalRequests( longestSize );
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
        ATTACHMENT_REQUEST_TRACE_THREAD_LOCAL.set( null );
        BLOB_FETCHING_TRACE_THREAD_LOCAL.set( null );
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

        PORTAL_REQUEST_TRACE_THREAD_LOCAL.set( null );
    }

    public PageRenderingTrace startPageRenderTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        PageRenderingTrace pageRenderTrace = new PageRenderingTrace( portalRequestTrace );
        pageRenderTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setPageRenderingTrace( pageRenderTrace );
        return pageRenderTrace;
    }

    public void stopTracing( PageRenderingTrace pageRenderTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( pageRenderTrace );

        pageRenderTrace.setStopTime( timeService.getNowAsDateTime() );
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
        return windowRenderingTrace;
    }

    public void stopTracing( WindowRenderingTrace windowRenderingTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( windowRenderingTrace );

        windowRenderingTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public AttachmentRequestTrace startAttachmentRequestTracing( PortalRequestTrace portalRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( portalRequestTrace );

        AttachmentRequestTrace newTrace = new AttachmentRequestTrace( portalRequestTrace );
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        portalRequestTrace.setAttachmentRequestTrace( newTrace );
        ATTACHMENT_REQUEST_TRACE_THREAD_LOCAL.set( newTrace );
        return newTrace;
    }

    public void stopTracing( AttachmentRequestTrace attachmentRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( attachmentRequestTrace );

        attachmentRequestTrace.setStopTime( timeService.getNowAsDateTime() );
    }

    public BlobFetchingTrace startBlobFetchTracing( AttachmentRequestTrace attachmentRequestTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( attachmentRequestTrace );

        BlobFetchingTrace newTrace = new BlobFetchingTrace( attachmentRequestTrace );
        newTrace.setStartTime( timeService.getNowAsDateTime() );
        attachmentRequestTrace.setBlobFetchingTrace( newTrace );
        BLOB_FETCHING_TRACE_THREAD_LOCAL.set( newTrace );
        return newTrace;
    }

    public void stopTracing( BlobFetchingTrace blobFetchingTrace )
    {
        checkEnabled();
        Preconditions.checkNotNull( blobFetchingTrace );

        blobFetchingTrace.setStopTime( timeService.getNowAsDateTime() );
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

    public List<PastPortalRequestTrace> getHistoryOfPortalRequests()
    {
        checkEnabled();

        return historyOfPortalRequests.getList();
    }

    public List<PastPortalRequestTrace> getHistorySince( long historyRecordNumber )
    {
        checkEnabled();

        return historyOfPortalRequests.getListSince( historyRecordNumber );
    }

    public PortalRequestTrace getCurrentPortalRequestTrace()
    {
        return PORTAL_REQUEST_TRACE_THREAD_LOCAL.get();
    }

    public AttachmentRequestTrace getCurrentAttachmentRequestTrace()
    {
        return ATTACHMENT_REQUEST_TRACE_THREAD_LOCAL.get();
    }

    public BlobFetchingTrace getCurrentBlobFetchingTrace()
    {
        return BLOB_FETCHING_TRACE_THREAD_LOCAL.get();
    }

    private void checkEnabled()
    {
        Preconditions.checkArgument( enabled, "Unexpected call when Live Portal Tracing is disabled" );
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
