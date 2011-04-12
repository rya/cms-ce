/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.page.Regions;

/**
 * Sep 28, 2009
 */
public class PageRequestProcessorResult
{
    private ContentEntity contentFromRequest;

    private PageTemplateEntity pageTemplate;

    private LanguageEntity language;

    private UserEntity runAsUser;

    private Locale locale;

    private String deviceClass;

    private Regions regionsInPage;

    private HttpServletRequest httpRequest;

    private SitePath sitePath;

    private SitePath redirectToSitePath;

    public ContentEntity getContentFromRequest()
    {
        return contentFromRequest;
    }

    public void setContentFromRequest( ContentEntity contentFromRequest )
    {
        this.contentFromRequest = contentFromRequest;
    }

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public UserEntity getRunAsUser()
    {
        return runAsUser;
    }

    public void setRunAsUser( UserEntity runAsUser )
    {
        this.runAsUser = runAsUser;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public String getDeviceClass()
    {
        return deviceClass;
    }

    public void setDeviceClass( String deviceClass )
    {
        this.deviceClass = deviceClass;
    }

    public Regions getRegionsInPage()
    {
        return regionsInPage;
    }

    public void setRegionsInPage( Regions regionsInPage )
    {
        this.regionsInPage = regionsInPage;
    }

    public SitePath getRedirectToSitePath()
    {
        return redirectToSitePath;
    }

    public void setRedirectToSitePath( SitePath redirectToSitePath )
    {
        this.redirectToSitePath = redirectToSitePath;
    }

    public HttpServletRequest getHttpRequest()
    {
        return httpRequest;
    }

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public SitePath getSitePath()
    {
        return sitePath;
    }

    public void setSitePath( SitePath sitePath )
    {
        this.sitePath = sitePath;
    }
}
