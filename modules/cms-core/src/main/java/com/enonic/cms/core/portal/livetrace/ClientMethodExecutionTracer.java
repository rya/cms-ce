package com.enonic.cms.core.portal.livetrace;

public class ClientMethodExecutionTracer
{
    public static ClientMethodExecutionTrace startTracing( final String methodName, final LivePortalTraceService livePortalTraceService )
    {
        if ( livePortalTraceService.tracingEnabled() )
        {
            return livePortalTraceService.startClientMethodExecutionTracing( methodName );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( ClientMethodExecutionTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }
}
