/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.rendering.portalfunctions;

import java.util.Locale;

import com.enonic.cms.core.SiteURLResolver;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.PortalInstanceKey;

/**
 * May 7, 2009
 */
public class PortalFunctionsContext
{
    private boolean encodeImageUrlParams;

    private Locale locale;

    private SitePath originalSitePath;

    private SiteEntity site;

    private MenuItemEntity menuItem;

    private PortalInstanceKey portalInstanceKey;

    private boolean encodeURIs;

    private boolean isRenderedInline;

    private SiteURLResolver siteURLResolver;

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public SitePath getOriginalSitePath()
    {
        return originalSitePath;
    }

    public void setOriginalSitePath( SitePath originalSitePath )
    {
        this.originalSitePath = originalSitePath;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public boolean isEncodeURIs()
    {
        return encodeURIs;
    }

    public void setEncodeURIs( boolean encodeURIs )
    {
        this.encodeURIs = encodeURIs;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public void setPortalInstanceKey( PortalInstanceKey portalInstanceKey )
    {
        this.portalInstanceKey = portalInstanceKey;
    }

    public boolean isRenderedInline()
    {
        return isRenderedInline;
    }

    public void setRenderedInline( boolean renderedInline )
    {
        isRenderedInline = renderedInline;
    }

    public SiteURLResolver getSiteURLResolver()
    {
        return siteURLResolver;
    }

    public void setSiteURLResolver( final SiteURLResolver siteURLResolver )
    {
        this.siteURLResolver = siteURLResolver;
    }

    public boolean encodeImageUrlParams()
    {
        return encodeImageUrlParams;
    }

    public void setEncodeImageUrlParams( boolean encodeImageUrlParams )
    {
        this.encodeImageUrlParams = encodeImageUrlParams;
    }
}
