/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import com.enonic.cms.domain.security.user.UserEntity;

/**
 * Nov 25, 2010
 */
public class PageRenderingTracer
{
    public static PageRenderingTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        final PortalRequestTrace portalRequestTrace = livePortalTraceService.getCurrentPortalRequestTrace();

        if ( portalRequestTrace != null )
        {
            return livePortalTraceService.startPageRenderTracing( portalRequestTrace );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( final PageRenderingTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceRequester( final PageRenderingTrace trace, final UserEntity renderer )
    {
        if ( trace != null && renderer != null )
        {
            trace.setRenderer( renderer.getQualifiedName() );
        }
    }

    public static void traceUsedCachedResult( final PageRenderingTrace trace, boolean value )
    {
        if ( trace != null )
        {
            trace.setUsedCachedResult( value );
        }
    }
}
