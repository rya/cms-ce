/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import com.enonic.cms.portal.PortalAccessService;

/**
 * Sep 28, 2009
 */
public abstract class AbstractBasePortalRequestProcessor
{
    protected PortalAccessService portalAccessService;

    public void setPortalAccessService( PortalAccessService value )
    {
        this.portalAccessService = value;
    }
}
