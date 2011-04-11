/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal.processor;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;

import com.enonic.cms.core.preview.PreviewContext;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.domain.portal.ContentPath;
import com.enonic.cms.domain.portal.PageRequestType;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;

/**
 * Sep 28, 2009
 */
public class PageRequestProcessorContext
{
    private HttpServletRequest httpRequest;

    private PageRequestType pageRequestType;

    private DateTime requestTime;

    private UserEntity requester;

    private SitePath sitePath;

    private SiteEntity site;

    private MenuItemEntity menuItem;

    private LanguageEntity overridingLanguage;

    private ContentPath contentPath;

    private PreviewContext previewContext;

    public HttpServletRequest getHttpRequest()
    {
        return httpRequest;
    }

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public PageRequestType getPageRequestType()
    {
        return pageRequestType;
    }

    public void setPageRequestType( PageRequestType pageRequestType )
    {
        this.pageRequestType = pageRequestType;
    }

    public DateTime getRequestTime()
    {
        return requestTime;
    }

    public void setRequestTime( DateTime requestTime )
    {
        this.requestTime = requestTime;
    }

    public UserEntity getRequester()
    {
        return requester;
    }

    public void setRequester( UserEntity requester )
    {
        this.requester = requester;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public void setSitePath( SitePath sitePath )
    {
        this.sitePath = sitePath;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public LanguageEntity getOverridingLanguage()
    {
        return overridingLanguage;
    }

    public void setOverridingLanguage( LanguageEntity overridingLanguage )
    {
        this.overridingLanguage = overridingLanguage;
    }

    public ContentPath getContentPath()
    {
        return contentPath;
    }

    public void setContentPath( ContentPath contentPath )
    {
        this.contentPath = contentPath;
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }
}
