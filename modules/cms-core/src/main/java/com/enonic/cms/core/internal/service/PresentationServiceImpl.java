/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.internal.service;

import com.enonic.vertical.engine.PresentationEngine;

import com.enonic.cms.core.service.PresentationService;

import com.enonic.cms.domain.SiteKey;

public class PresentationServiceImpl
    implements PresentationService
{

    private PresentationEngine presentationEngine;

    public void setPresentationEngine( PresentationEngine value )
    {
        presentationEngine = value;
    }

    /**
     * Get error page key for a menu.
     *
     * @param menuKey menu key
     * @return error page key
     */
    public int getErrorPage( int menuKey )
    {
        return presentationEngine.getErrorPage( menuKey );
    }

    public boolean hasErrorPage( int menuKey )
    {
        return presentationEngine.hasErrorPage( menuKey );
    }

    public int getLoginPage( int menuKey )
    {
        return presentationEngine.getLoginPage( menuKey );
    }

    public String getPathString( int type, int key, boolean includeRoot )
    {
        return presentationEngine.getPathString( type, key, includeRoot );
    }

    public boolean siteExists( SiteKey siteKey )
    {
        return presentationEngine.siteExists( siteKey );
    }

}
