/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import java.util.Map;

import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;

/**
 * Sep 29, 2009
 */
public class DirectiveRequestProcessorContext
{
    private UserEntity requester;

    private MenuItemEntity menuItem;

    private SitePath originalSitePath;

    private SitePath sitePath;

    private Map<String, Object> requestParams;

    public UserEntity getRequester()
    {
        return requester;
    }

    public void setRequester( UserEntity requester )
    {
        this.requester = requester;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public SitePath getOriginalSitePath()
    {
        return originalSitePath;
    }

    public void setOriginalSitePath( SitePath originalSitePath )
    {
        this.originalSitePath = originalSitePath;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public void setSitePath( SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public Map<String, Object> getRequestParams()
    {
        return requestParams;
    }

    public void setRequestParams( Map<String, Object> requestParams )
    {
        this.requestParams = requestParams;
    }
}
