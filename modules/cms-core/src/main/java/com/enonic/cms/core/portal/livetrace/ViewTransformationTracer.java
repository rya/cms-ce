package com.enonic.cms.core.portal.livetrace;


public class ViewTransformationTracer
{
    public static ViewTransformationTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        return livePortalTraceService.startViewTransformationTracing();
    }

    public static void stopTracing( final ViewTransformationTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceView( final String name, final ViewTransformationTrace trace )
    {
        if ( trace != null )
        {
            trace.setView( name );
        }
    }
}
