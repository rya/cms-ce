/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.framework.cache.CacheFacade;
import com.enonic.cms.framework.cache.CacheManager;

import com.enonic.cms.core.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.core.portal.livetrace.PastPortalRequestTrace;
import com.enonic.cms.core.portal.livetrace.PortalRequestTrace;

/**
 * This class implements the connection info controller.
 */
public final class LivePortalTraceController
    extends AbstractToolController
{

    private LivePortalTraceService livePortalTraceService;

    private CacheManager cacheManager;

    private SessionFactory sessionFactory;


    protected void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems )
    {
        if ( "POST".equalsIgnoreCase( req.getMethod() ) )
        {
            final String command = req.getParameter( "command" );

            if ( "clear-longestpagerequests".equals( command ) )
            {
                livePortalTraceService.clearLongestPageRequestsTraces();
            }
            else if ( "clear-longestattachmentrequests".equals( command ) )
            {
                livePortalTraceService.clearLongestAttachmentRequestTraces();
            }
            else if ( "clear-longestimagerequests".equals( command ) )
            {
                livePortalTraceService.clearLongestImageRequestTraces();
            }

            res.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
        else
        {
            final String systemInfo = req.getParameter( "system-info" );
            final String window = req.getParameter( "window" );
            final String history = req.getParameter( "history" );

            final HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );

            if ( StringUtils.isNotBlank( systemInfo ) )
            {
                appendSystemInfoToModel( model );
                process( req, res, model, "livePortalTrace_system_info" );
                res.setHeader( "Content-Type", "application/json; charset=UTF-8" );
            }
            else if ( "current".equals( window ) )
            {
                List<PortalRequestTrace> currentPortalRequestTraces = livePortalTraceService.getCurrentPortalRequestTraces();
                model.put( "currentTraces", currentPortalRequestTraces );
                process( req, res, model, "livePortalTraceWindow_current" );
                res.setHeader( "Content-Type", "text/html; charset=UTF-8" );
            }
            else if ( "longestpagerequests".equals( window ) )
            {
                List<PortalRequestTrace> longestTimePortalRequestTraces = livePortalTraceService.getLongestTimePortalPageRequestTraces();
                model.put( "longestTraces", longestTimePortalRequestTraces );
                process( req, res, model, "livePortalTraceWindow_longest" );
                res.setHeader( "Content-Type", "text/html; charset=UTF-8" );
            }
            else if ( "longestattachmentrequests".equals( window ) )
            {
                List<PortalRequestTrace> longestTimePortalRequestTraces =
                    livePortalTraceService.getLongestTimePortalAttachmentRequestTraces();
                model.put( "longestTraces", longestTimePortalRequestTraces );
                process( req, res, model, "livePortalTraceWindow_longest" );
                res.setHeader( "Content-Type", "text/html; charset=UTF-8" );
            }
            else if ( "longestimagerequests".equals( window ) )
            {
                List<PortalRequestTrace> longestTimePortalRequestTraces = livePortalTraceService.getLongestTimePortalImageRequestTraces();
                model.put( "longestTraces", longestTimePortalRequestTraces );
                process( req, res, model, "livePortalTraceWindow_longest" );
                res.setHeader( "Content-Type", "text/html; charset=UTF-8" );
            }
            else if ( history != null )
            {
                String recordsSinceIdStr = req.getParameter( "records-since-id" );
                Long recordsSinceId = Long.valueOf( recordsSinceIdStr );

                List<PastPortalRequestTrace> pastPortalRequestTraces = livePortalTraceService.getHistorySince( recordsSinceId );
                model.put( "pastPortalRequestTraces", pastPortalRequestTraces );

                Long lastHistoryRecordNumber = recordsSinceId;
                if ( pastPortalRequestTraces.size() > 0 )
                {
                    lastHistoryRecordNumber = pastPortalRequestTraces.get( 0 ).getHistoryRecordNumber();
                }
                model.put( "lastHistoryRecordNumber", lastHistoryRecordNumber );

                res.setHeader( "Content-Type", "application/json" );
                process( req, res, model, "livePortalTrace_history_trace" );
            }
            else
            {
                model.put( "livePortalTraceEnabled", isLivePortalTraceEnabled() ? 1 : 0 );
                process( req, res, model, "livePortalTracePage" );
                res.setHeader( "Content-Type", "text/html; charset=UTF-8" );
            }
        }
    }

    private Map<String, Object> appendSystemInfoToModel( final Map<String, Object> model )
    {
        model.put( "portalRequestTracesInProgress", livePortalTraceService.getNumberOfPortalRequestTracesInProgress() );
        final CacheFacade entityCache = cacheManager.getCache( "entity" );
        model.put( "entityCacheCount", entityCache != null ? entityCache.getCount() : 0 );
        model.put( "entityCacheHitCount", entityCache != null ? entityCache.getHitCount() : 0 );
        model.put( "entityCacheMissCount", entityCache != null ? entityCache.getMissCount() : 0 );
        model.put( "entityCacheCapacityCount", entityCache != null ? entityCache.getMemoryCapacity() : 0 );

        final CacheFacade pageCache = cacheManager.getCache( "page" );

        model.put( "pageCacheCount", pageCache != null ? pageCache.getCount() : 0 );
        model.put( "pageCacheHitCount", pageCache != null ? pageCache.getHitCount() : 0 );
        model.put( "pageCacheMissCount", pageCache != null ? pageCache.getMissCount() : 0 );
        model.put( "pageCacheCapacityCount", pageCache != null ? pageCache.getMemoryCapacity() : 0 );

        final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        final MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        model.put( "javaHeapMemoryUsageInit", heapMemoryUsage.getInit() );
        model.put( "javaHeapMemoryUsageUsed", heapMemoryUsage.getUsed() );
        model.put( "javaHeapMemoryUsageCommitted", heapMemoryUsage.getCommitted() );
        model.put( "javaHeapMemoryUsageMax", heapMemoryUsage.getMax() );
        final MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        model.put( "javaNonHeapMemoryUsageInit", nonHeapMemoryUsage.getInit() );
        model.put( "javaNonHeapMemoryUsageUsed", nonHeapMemoryUsage.getUsed() );
        model.put( "javaNonHeapMemoryUsageCommitted", nonHeapMemoryUsage.getCommitted() );
        model.put( "javaNonHeapMemoryUsageMax", nonHeapMemoryUsage.getMax() );

        final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        model.put( "javaThreadCount", threadMXBean.getThreadCount() );
        model.put( "javaThreadPeakCount", threadMXBean.getPeakThreadCount() );

        final Statistics statistics = sessionFactory.getStatistics();
        if ( statistics != null )
        {
            model.put( "hibernateConnectionCount", statistics.getConnectCount() );
            model.put( "hibernateQueryCacheHitCount", statistics.getQueryCacheHitCount() );
            model.put( "hibernateCollectionFetchCount", statistics.getCollectionFetchCount() );
            model.put( "hibernateCollectionLoadCount", statistics.getCollectionLoadCount() );
        }
        return model;
    }

    private boolean isLivePortalTraceEnabled()
    {
        return livePortalTraceService.tracingEnabled();
    }

    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }

    public void setCacheManager( CacheManager cacheManager )
    {
        this.cacheManager = cacheManager;
    }

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
}
