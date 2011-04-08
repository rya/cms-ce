/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.systemfunctions;

import com.enonic.cms.portal.rendering.WindowRendererContext;

/**
 * Apr 28, 2009
 */
public class PortalSystemFunctionsContext
{
    private WindowRendererContext windowRendererContext;


    public void setPortletRendererContext( WindowRendererContext windowRendererContext )
    {
        this.windowRendererContext = windowRendererContext;
    }

    public WindowRendererContext getPortletRendererContext()
    {
        return windowRendererContext;
    }
}
