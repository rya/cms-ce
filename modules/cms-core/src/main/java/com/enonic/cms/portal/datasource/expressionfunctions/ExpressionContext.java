/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.expressionfunctions;

import java.util.Locale;

import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.portal.PortalInstanceKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;

public class ExpressionContext
{
    private SiteEntity site;

    private MenuItemEntity menuItem;

    private ContentEntity contentFromRequest;

    private UserEntity user;

    private PortalInstanceKey portalInstanceKey;

    private Locale locale;

    private String deviceClass;

    private Boolean portletWindowRenderedInline;

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public ContentEntity getContentFromRequest()
    {
        return contentFromRequest;
    }

    public void setContentFromRequest( ContentEntity contentFromRequest )
    {
        this.contentFromRequest = contentFromRequest;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public void setPortalInstanceKey( PortalInstanceKey portalInstanceKey )
    {
        this.portalInstanceKey = portalInstanceKey;
    }

    public void setLocale( final Locale locale )
    {
        this.locale = locale;
    }

    public void setDeviceClass( final String deviceClass )
    {
        this.deviceClass = deviceClass;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public String getDeviceClass()
    {
        return deviceClass;
    }

    public void setPortletWindowRenderedInline( final Boolean portletWindowRenderedInline )
    {
        this.portletWindowRenderedInline = portletWindowRenderedInline;
    }

    public Boolean isPortletWindowRenderedInline()
    {
        return portletWindowRenderedInline;
    }
}
