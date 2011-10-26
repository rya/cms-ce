package com.enonic.cms.core.portal.livetrace;


public class InstructionPostProcessingTracer
{
    public static InstructionPostProcessingTrace startTracingForWindow( LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        return livePortalTraceService.startInstructionPostProcessingTracingForWindow();
    }

    public static InstructionPostProcessingTrace startTracingForPage( LivePortalTraceService livePortalTraceService )
    {
        if ( !livePortalTraceService.tracingEnabled() )
        {
            return null;
        }

        return livePortalTraceService.startInstructionPostProcessingTracingForPage();
    }

    public static void stopTracing( InstructionPostProcessingTrace trace, LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null && livePortalTraceService != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }
}
