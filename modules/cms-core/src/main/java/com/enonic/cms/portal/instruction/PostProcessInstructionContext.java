/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.instruction;

import javax.servlet.http.HttpServletRequest;

import com.enonic.cms.core.SiteURLResolver;
import com.enonic.cms.core.preview.PreviewContext;
import com.enonic.cms.portal.rendering.WindowRendererContext;

import com.enonic.cms.domain.structure.SiteEntity;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Nov 25, 2009
 * Time: 1:35:15 PM
 */
public class PostProcessInstructionContext
{
    private WindowRendererContext windowRendererContext;

    private PreviewContext previewContext;

    private HttpServletRequest httpRequest;

    private SiteEntity site;

    private boolean encodeImageUrlParams;

    private boolean inContextOfWindow;

    private SiteURLResolver siteURLResolverEnableHtmlEscaping;

    private SiteURLResolver siteURLResolverDisableHtmlEscaping;

    public WindowRendererContext getWindowRendererContext()
    {
        return windowRendererContext;
    }

    public void setWindowRendererContext( WindowRendererContext windowRendererContext )
    {
        this.windowRendererContext = windowRendererContext;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }

    public HttpServletRequest getHttpRequest()
    {
        return httpRequest;
    }

    public void setHttpRequest( HttpServletRequest request )
    {
        this.httpRequest = request;
    }

    public SiteEntity getSite()
    {
        return site;
    }

    public void setSite( SiteEntity site )
    {
        this.site = site;
    }

    public boolean doEncodeImageUrlParams()
    {
        return encodeImageUrlParams;
    }

    public void setEncodeImageUrlParams( boolean encodeImageUrlParams )
    {
        this.encodeImageUrlParams = encodeImageUrlParams;
    }

    public boolean isInContextOfWindow()
    {
        return inContextOfWindow;
    }

    public void setInContextOfWindow( boolean inContextOfWindow )
    {
        this.inContextOfWindow = inContextOfWindow;
    }

    public void setSiteURLResolverEnableHtmlEscaping( SiteURLResolver value )
    {
        this.siteURLResolverEnableHtmlEscaping = value;
    }

    public SiteURLResolver getSiteURLResolverEnabledHtmlEscaping()
    {
        return siteURLResolverEnableHtmlEscaping;
    }

    public SiteURLResolver getSiteURLResolverDisableHtmlEscaping()
    {
        return siteURLResolverDisableHtmlEscaping;
    }

    public void setSiteURLResolverDisableHtmlEscaping( SiteURLResolver siteURLResolverDisableHtmlEscaping )
    {
        this.siteURLResolverDisableHtmlEscaping = siteURLResolverDisableHtmlEscaping;
    }
}
