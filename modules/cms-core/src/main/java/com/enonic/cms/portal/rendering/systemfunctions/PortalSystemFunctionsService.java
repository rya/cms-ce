/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.systemfunctions;

/**
 * Apr 28, 2009
 */
public interface PortalSystemFunctionsService
{
    String RENDER_WINDOW = "renderWindow";

    void setContext( PortalSystemFunctionsContext value );

    String renderWindow( String portletKey, String encodedParamMap );
}
