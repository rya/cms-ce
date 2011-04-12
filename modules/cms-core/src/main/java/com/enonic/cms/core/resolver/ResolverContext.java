/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Created by rmy - Date: Aug 24, 2009
 */
public class ResolverContext
{

    HttpServletRequest request;

    SiteEntity site;

    MenuItemEntity menuItem;

    LanguageEntity language;

    UserEntity user;

    private ResolverContext()
    {
    }

    public ResolverContext( HttpServletRequest request, SiteEntity site )
    {
        this.request = request;
        this.site = site;
    }

    public ResolverContext( HttpServletRequest request, SiteEntity site, MenuItemEntity menuItem, LanguageEntity language )
    {
        this.request = request;
        this.site = site;
        this.menuItem = menuItem;
        this.language = language;
    }

    public HttpServletRequest getRequest()
    {
        return request;
    }

    public void setRequest( HttpServletRequest request )
    {
        this.request = request;
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

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }
}
