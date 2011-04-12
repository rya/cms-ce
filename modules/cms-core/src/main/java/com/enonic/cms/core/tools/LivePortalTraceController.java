/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.portal.livetrace.LivePortalTraceService;
import com.enonic.cms.portal.livetrace.PastPortalRequestTrace;
import com.enonic.cms.portal.livetrace.PortalRequestTrace;

/**
 * This class implements the connection info controller.
 */
@Controller
@RequestMapping("/tools/liveportaltrace")
public final class LivePortalTraceController
    extends AbstractToolController
{

    private LivePortalTraceService livePortalTraceService;

    /**
     * Handle the request.
     */
    protected ModelAndView doHandleRequest( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {

        String window = req.getParameter( "window" );
        String history = req.getParameter( "history" );

        if ( "current".equals( window ) )
        {
            List<PortalRequestTrace> currentPortalRequestTraces = livePortalTraceService.getCurrentPortalRequestTraces();
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "currentTraces", currentPortalRequestTraces );
            return new ModelAndView( "livePortalTraceWindow_current", model );
        }
        else if ( "longestpagerequests".equals( window ) )
        {
            List<PortalRequestTrace> longestTimePortalRequestTraces = livePortalTraceService.getLongestTimePortalPageRequestTraces();
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "longestTraces", longestTimePortalRequestTraces );
            return new ModelAndView( "livePortalTraceWindow_longest", model );
        }
        else if ( "longestattachmentrequests".equals( window ) )
        {
            List<PortalRequestTrace> longestTimePortalRequestTraces = livePortalTraceService.getLongestTimePortalAttachmentRequestTraces();
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "longestTraces", longestTimePortalRequestTraces );
            return new ModelAndView( "livePortalTraceWindow_longest", model );
        }
        else if ( history != null )
        {
            String recordsSinceIdStr = req.getParameter( "records-since-id" );
            Long recordsSinceId = Long.valueOf( recordsSinceIdStr );

            List<PastPortalRequestTrace> pastPortalRequestTraces = livePortalTraceService.getHistorySince( recordsSinceId );
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "pastPortalRequestTraces", pastPortalRequestTraces );

            Long lastHistoryRecordNumber = recordsSinceId;
            if ( pastPortalRequestTraces.size() > 0 )
            {
                lastHistoryRecordNumber = pastPortalRequestTraces.get( 0 ).getHistoryRecordNumber();
            }
            model.put( "lastHistoryRecordNumber", lastHistoryRecordNumber );

            return new ModelAndView( "livePortalTrace_history_trace", model );
        }
        else
        {
            HashMap<String, Object> model = new HashMap<String, Object>();
            model.put( "livePortalTraceEnabled", isLivePortalTraceEnabled() ? 1 : 0 );
            return new ModelAndView( "livePortalTracePage", model );
        }
    }

    private boolean isLivePortalTraceEnabled()
    {
        return livePortalTraceService.tracingEnabled();
    }

    @Autowired
    public void setLivePortalTraceService( LivePortalTraceService livePortalTraceService )
    {
        this.livePortalTraceService = livePortalTraceService;
    }
}
