/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.systemfunctions;

import java.util.Map;

import com.enonic.cms.framework.io.ParameterSerializer;
import com.enonic.cms.framework.io.SimpleParameterSerializer;

import com.enonic.cms.portal.rendering.RenderedWindowResult;
import com.enonic.cms.portal.rendering.WindowRenderer;
import com.enonic.cms.portal.rendering.WindowRendererFactory;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.core.structure.page.WindowKey;

/**
 * Apr 28, 2009
 */
public class PortalSystemFunctionsServiceImpl
    implements PortalSystemFunctionsService
{
    private final static ParameterSerializer SERIALIZER = new SimpleParameterSerializer();

    private WindowRendererFactory windowRendererFactory;

    private final ThreadLocal<PortalSystemFunctionsContext> portalSystemFunctionsContext = new ThreadLocal<PortalSystemFunctionsContext>();


    public String renderWindow( String portletWindowKeyStr, String encodedParamMap )
    {
        WindowKey windowKey = new WindowKey( portletWindowKeyStr );

        PortalSystemFunctionsContext context = getContext();

        WindowRenderer windowRenderer = windowRendererFactory.createPortletRenderer( context.getPortletRendererContext() );

        RequestParameters extraParams = new RequestParameters();
        if ( encodedParamMap != null )
        {
            for ( Map.Entry<String, String> entry : SERIALIZER.deserializeMap( encodedParamMap ).entrySet() )
            {
                extraParams.addParameterValue( entry.getKey(), entry.getValue() );
            }
        }

        RenderedWindowResult renderedWindowResult = windowRenderer.renderWindowInline( windowKey, extraParams );
        return renderedWindowResult.getContent();
    }

    public PortalSystemFunctionsContext getContext()
    {
        return portalSystemFunctionsContext.get();
    }

    public void setContext( PortalSystemFunctionsContext value )
    {
        portalSystemFunctionsContext.set( value );
    }

    public void setWindowRendererFactory( WindowRendererFactory value )
    {
        this.windowRendererFactory = value;
    }
}
