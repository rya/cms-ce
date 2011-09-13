/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.portal.rendering.portalfunctions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.enonic.cms.business.portal.rendering.PageRendererContext;
import com.enonic.cms.business.portal.rendering.WindowRenderer;
import com.enonic.cms.business.portal.rendering.WindowRendererContext;
import com.enonic.cms.business.portal.rendering.WindowRendererFactory;
import com.enonic.cms.business.portal.rendering.tracing.RenderTrace;
import com.enonic.cms.business.portal.rendering.tracing.TraceMarkerHelper;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.portal.rendering.RenderedWindowResult;
import com.enonic.cms.domain.structure.page.WindowKey;

public class IsWindowEmptyFunction
{
    private WindowRendererFactory windowRendererFactory;

    public IsWindowEmptyFunction()
    {
    }

    public Boolean isWindowEmpty( final WindowKey windowKey, final String[] params )
    {
        // save current PortalFunctionsContext
        PortalFunctionsContext previousPortalFunctionContext = PortalFunctionsFactory.get().getContext();
        try
        {
            PageRendererContext pageRendererContext = PortalFunctionsFactory.get().getContext().getPageRendererContext();
            WindowRendererContext windowRenderContext = new WindowRendererContext();
            windowRenderContext.setContentFromRequest( pageRendererContext.getContentFromRequest() );
            windowRenderContext.setOverridingSitePropertyCreateUrlAsPath( pageRendererContext.getOverridingSitePropertyCreateUrlAsPath() );
            windowRenderContext.setDeviceClass( pageRendererContext.getDeviceClass() );
            windowRenderContext.setEncodeURIs( pageRendererContext.isEncodeURIs() );
            windowRenderContext.setForceNoCacheUsage( pageRendererContext.forceNoCacheUsage() );
            windowRenderContext.setHttpRequest( pageRendererContext.getHttpRequest() );
            windowRenderContext.setInvocationCache( previousPortalFunctionContext.getInvocationCache() );
            windowRenderContext.setLanguage( pageRendererContext.getLanguage() );
            windowRenderContext.setLocale( pageRendererContext.getLocale() );
            windowRenderContext.setMenuItem( pageRendererContext.getMenuItem() );
            windowRenderContext.setOriginalSitePath( pageRendererContext.getOriginalSitePath() );
            windowRenderContext.setPageRequestType( pageRendererContext.getPageRequestType() );
            windowRenderContext.setPageTemplate( pageRendererContext.getMenuItem().getPage().getTemplate() );
            windowRenderContext.setPreviewContext( pageRendererContext.getPreviewContext() );
            windowRenderContext.setProcessors( pageRendererContext.getProcessors() );
            windowRenderContext.setProfile( pageRendererContext.getProfile() );
            windowRenderContext.setRegionsInPage( pageRendererContext.getRegionsInPage() );
            windowRenderContext.setRenderedInline( true );
            windowRenderContext.setRenderer( pageRendererContext.getRenderer() );
            windowRenderContext.setTicketId( pageRendererContext.getTicketId() );
            windowRenderContext.setShoppingCart( pageRendererContext.getShoppingCart() );
            windowRenderContext.setSite( pageRendererContext.getSite() );
            windowRenderContext.setSitePath( pageRendererContext.getSitePath() );
            windowRenderContext.setVerticalSession( pageRendererContext.getVerticalSession() );
            windowRenderContext.setOriginalUrl( pageRendererContext.getOriginalUrl() );

            String windowContent = renderWindow( windowKey, params, windowRenderContext );

            return StringUtils.isBlank( windowContent );
        }
        finally
        {
            // restore previous PortalFunctionsContext
            PortalFunctionsFactory.get().setContext( previousPortalFunctionContext );
        }
    }

    private String renderWindow( final WindowKey windowKey, final String[] params, final WindowRendererContext context )
    {
        HashMap<String, String> map = createParamsMap( params );

        WindowRenderer windowRenderer = windowRendererFactory.createPortletRenderer( context );

        RequestParameters portletParams = new RequestParameters();

        for ( Map.Entry<String, String> entry : map.entrySet() )
        {
            portletParams.addParameterValue( entry.getKey(), entry.getValue() );
        }

        RenderedWindowResult renderedWindowResult = windowRenderer.renderWindowInline( windowKey, portletParams );

        if ( RenderTrace.isTraceOn() )
        {
            return TraceMarkerHelper.unwrapResultWithPortletMarker( renderedWindowResult );
        }
        else
        {
            return renderedWindowResult.getContent();
        }
    }

    private HashMap<String, String> createParamsMap( String[] params )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        if ( ( params != null ) && ( params.length > 0 ) )
        {
            for ( int i = 0; i < ( params.length / 2 ); i++ )
            {
                map.put( params[i * 2], params[i * 2 + 1] );
            }
        }
        return map;
    }

    public void setWindowRendererFactory( WindowRendererFactory windowRendererFactory )
    {
        this.windowRendererFactory = windowRendererFactory;
    }
}
