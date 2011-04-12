/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.resource.ResourceKey;
import com.enonic.cms.core.structure.menuitem.MenuItemEntity;
import com.enonic.cms.core.structure.page.Regions;
import com.enonic.cms.core.structure.page.Window;
import com.enonic.cms.core.structure.page.template.PageTemplateEntity;
import com.enonic.cms.portal.*;
import org.jdom.Document;

import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.portal.datasource.processor.DataSourceProcessor;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.PortalInstanceKey;
import com.enonic.cms.portal.ShoppingCart;
import com.enonic.cms.portal.VerticalSession;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.structure.SiteEntity;
import com.enonic.cms.core.structure.SiteProperties;

/**
 * Apr 21, 2009
 */
public class DatasourceExecutorContext
{
    private DatasourcesType datasourcesType;

    private HttpServletRequest httpRequest;

    private SitePath originalSitePath;

    private RequestParameters requestParameters;

    private PageRequestType pageRequestType;

    private PortalInstanceKey portalInstanceKey;

    private SiteEntity site;

    private SiteProperties siteProperties;

    private MenuItemEntity menuItem;

    private ContentEntity contentFromRequest;

    private PageTemplateEntity pageTemplate;

    private Window window;

    private Regions regions;

    private Boolean isPortletWindowRenderedInline = null;

    private Document portletDocument;

    private ResourceKey[] cssKeys;

    private DataSourceProcessor[] processors;

    private PreviewContext previewContext;

    private InvocationCache datasourceServiceInvocationCache;

    private ShoppingCart shoppingCart;

    private String deviceClass;

    private VerticalSession verticalSession;

    private LanguageEntity language;

    private String profile;

    private UserEntity user;

    private Locale locale;

    private String defaultResultRootElementName;


    public DatasourcesType getDatasourcesType()
    {
        return datasourcesType;
    }

    public void setDatasourcesType( DatasourcesType datasourcesType )
    {
        this.datasourcesType = datasourcesType;
    }

    public SitePath getOriginalSitePath()
    {
        return originalSitePath;
    }

    public void setOriginalSitePath( SitePath originalSitePath )
    {
        this.originalSitePath = originalSitePath;
    }

    public RequestParameters getRequestParameters()
    {
        return requestParameters;
    }

    public void setRequestParameters( RequestParameters value )
    {
        this.requestParameters = value;
    }

    public PageRequestType getPageRequestType()
    {
        return pageRequestType;
    }

    public void setPageRequestType( PageRequestType value )
    {
        this.pageRequestType = value;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public SiteProperties getSiteProperties()
    {
        return siteProperties;
    }

    public MenuItemEntity getMenuItem()
    {
        return menuItem;
    }

    public ContentEntity getContentFromRequest()
    {
        return contentFromRequest;
    }

    public PageTemplateEntity getPageTemplate()
    {
        return pageTemplate;
    }

    public Window getWindow()
    {
        return window;
    }

    public void setWindow( Window window )
    {
        this.window = window;
    }

    public Regions getRegions()
    {
        return regions;
    }

    public void setRegions( Regions regions )
    {
        this.regions = regions;
    }

    public Document getPortletDocument()
    {
        return portletDocument;
    }

    public ResourceKey[] getCssKeys()
    {
        return cssKeys;
    }

    public DataSourceProcessor[] getProcessors()
    {
        return processors;
    }

    public boolean hasProcessors()
    {
        return processors != null && processors.length > 0;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }

    public boolean hasCssKeys()
    {
        return cssKeys != null && cssKeys.length > 0;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public void setSiteProperties( SiteProperties siteProperties )
    {
        this.siteProperties = siteProperties;
    }

    public void setMenuItem( MenuItemEntity menuItem )
    {
        this.menuItem = menuItem;
    }

    public void setContentFromRequest( ContentEntity contentFromRequest )
    {
        this.contentFromRequest = contentFromRequest;
    }

    public void setPageTemplate( PageTemplateEntity pageTemplate )
    {
        this.pageTemplate = pageTemplate;
    }

    public void setPortletDocument( Document value )
    {
        this.portletDocument = value;
    }

    public void setShoppingCart( ShoppingCart shoppingCart )
    {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getShoppingCart()
    {
        return shoppingCart;
    }

    public void setCssKeys( ResourceKey[] cssKeys )
    {
        this.cssKeys = cssKeys;
    }

    public void setProcessors( DataSourceProcessor[] processors )
    {
        this.processors = processors;
    }

    public InvocationCache getDatasourceServiceInvocationCache()
    {
        return datasourceServiceInvocationCache;
    }

    public void setDatasourceServiceInvocationCache( InvocationCache value )
    {
        this.datasourceServiceInvocationCache = value;
    }

    public String getDeviceClass()
    {
        return deviceClass;
    }

    public void setDeviceClass( String deviceClass )
    {
        this.deviceClass = deviceClass;
    }

    public VerticalSession getVerticalSession()
    {
        return verticalSession;
    }

    public void setVerticalSession( VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }

    public LanguageEntity getLanguage()
    {
        return language;
    }

    public void setLanguage( LanguageEntity language )
    {
        this.language = language;
    }

    public HttpServletRequest getHttpRequest()
    {
        return httpRequest;
    }

    public void setHttpRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile( String profile )
    {
        this.profile = profile;
    }

    public UserEntity getUser()
    {
        return user;
    }

    public void setUser( UserEntity user )
    {
        this.user = user;
    }

    public PortalInstanceKey getPortalInstanceKey()
    {
        return portalInstanceKey;
    }

    public void setPortalInstanceKey( PortalInstanceKey portalInstanceKey )
    {
        this.portalInstanceKey = portalInstanceKey;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale( Locale locale )
    {
        this.locale = locale;
    }

    public Boolean isPortletWindowRenderedInline()
    {
        return isPortletWindowRenderedInline;
    }

    public void setPortletWindowRenderedInline( Boolean value )
    {
        isPortletWindowRenderedInline = value;
    }

    public String getDefaultResultRootElementName()
    {
        return defaultResultRootElementName;
    }

    public void setDefaultResultRootElementName( final String value )
    {
        defaultResultRootElementName = value;
    }
}
