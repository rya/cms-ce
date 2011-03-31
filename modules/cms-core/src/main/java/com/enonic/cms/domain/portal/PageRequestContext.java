/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 9, 2010
 * Time: 8:45:43 AM
 */
public class PageRequestContext
{
    private SitePath sitePath;

    private ContentPath resolvedContentPath;

    private MenuItemEntity requestedMenuItem;

    private PageRequestType pageRequestType;

    public ContentPath getContentPath()
    {
        return resolvedContentPath;
    }

    public boolean hasPathToContent()
    {
        if ( getContentPath() != null )
        {
            return true;
        }

        return false;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public void setSitePath( SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public ContentPath getResolvedContentPath()
    {
        return resolvedContentPath;
    }

    public void setResolvedContentPath( ContentPath resolvedContentPath )
    {
        this.resolvedContentPath = resolvedContentPath;
    }

    public MenuItemEntity getRequestedMenuItem()
    {
        return requestedMenuItem;
    }

    public void setRequestedMenuItem( MenuItemEntity requestedMenuItem )
    {
        this.requestedMenuItem = requestedMenuItem;
    }

    public PageRequestType getPageRequestType()
    {
        return pageRequestType;
    }

    public void setPageRequestType( PageRequestType pageRequestType )
    {
        this.pageRequestType = pageRequestType;
    }
}

