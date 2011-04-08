/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.livetrace;

import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.page.Window;
import com.enonic.cms.domain.structure.portlet.PortletEntity;

/**
 * Nov 25, 2010
 */
public class WindowRenderingTracer
{
    public static WindowRenderingTrace startTracing( final LivePortalTraceService livePortalTraceService )
    {
        final PortalRequestTrace portalRequestTrace = livePortalTraceService.getCurrentPortalRequestTrace();

        if ( portalRequestTrace != null )
        {
            return livePortalTraceService.startWindowRenderTracing( portalRequestTrace );
        }
        else
        {
            return null;
        }
    }

    public static void stopTracing( final WindowRenderingTrace trace, final LivePortalTraceService livePortalTraceService )
    {
        if ( trace != null )
        {
            livePortalTraceService.stopTracing( trace );
        }
    }

    public static void traceRequestedWindow( final WindowRenderingTrace trace, final Window window )
    {
        if ( trace != null && window != null )
        {
            final PortletEntity portlet = window.getPortlet();
            if ( portlet != null )
            {
                trace.setPortletName( portlet.getName() );
            }
        }
    }

    public static void traceRenderer( final WindowRenderingTrace trace, final UserEntity renderer )
    {
        if ( trace != null && renderer != null )
        {
            trace.setRenderer( renderer.getQualifiedName() );
        }
    }

    public static void traceUsedCachedResult( final WindowRenderingTrace trace, boolean value )
    {
        if ( trace != null )
        {
            trace.setUsedCachedResult( value );
        }
    }
}
