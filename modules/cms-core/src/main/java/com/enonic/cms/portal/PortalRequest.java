/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

import java.util.Map;

import com.enonic.cms.core.preview.PreviewContext;

import com.enonic.cms.domain.SitePath;

/**
 * May 6, 2009
 */
public class PortalRequest
    extends AbstractBasePortalRequest
{

    private SitePath sitePath;

    private Map<String, Object> requestParams;

    private VerticalSession verticalSession;

    private ShoppingCart shoppingCart;

    private int overridingLanguage = -1;

    private String profile;

    private boolean encodeURIs;

    private String httpReferer;

    private String originalUrl;

    private PreviewContext previewContext = PreviewContext.NO_PREVIEW;


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

    public VerticalSession getVerticalSession()
    {
        return verticalSession;
    }

    public void setVerticalSession( VerticalSession verticalSession )
    {
        this.verticalSession = verticalSession;
    }

    public ShoppingCart getShoppingCart()
    {
        return shoppingCart;
    }

    public void setShoppingCart( ShoppingCart shoppingCart )
    {
        this.shoppingCart = shoppingCart;
    }

    public int getOverridingLanguage()
    {
        return overridingLanguage;
    }

    public void setOverridingLanguage( final int value )
    {
        this.overridingLanguage = value;
    }

    public void setProfile( final String profile )
    {
        this.profile = profile;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setEncodeURIs( final boolean encodeURIs )
    {
        this.encodeURIs = encodeURIs;
    }

    public boolean isEncodeURIs()
    {
        return encodeURIs;
    }

    public String getHttpReferer()
    {
        return httpReferer;
    }

    public void setHttpReferer( String httpReferer )
    {
        this.httpReferer = httpReferer;
    }

    public void setOriginalUrl( String originalUrl )
    {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl()
    {
        return originalUrl;
    }

    public PreviewContext getPreviewContext()
    {
        return previewContext;
    }

    public void setPreviewContext( PreviewContext previewContext )
    {
        this.previewContext = previewContext;
    }
}
