/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.processor;

import com.enonic.cms.domain.SitePath;

/**
 * Sep 29, 2009
 */
public class DirectiveRequestProcessorResult
{
    private String redirectToAbsoluteURL;

    private SitePath redirectToSitePath;

    private SitePath forwardToSitePath;

    public static DirectiveRequestProcessorResult createRedirectToAbsoluteURL( String absoluteURL )
    {
        DirectiveRequestProcessorResult result = new DirectiveRequestProcessorResult();
        result.setRedirectToAbsoluteURL( absoluteURL );
        return result;
    }

    public static DirectiveRequestProcessorResult createRedirectToSitePath( SitePath sitePath )
    {
        DirectiveRequestProcessorResult result = new DirectiveRequestProcessorResult();
        result.setRedirectToSitePath( sitePath );
        return result;
    }

    public static DirectiveRequestProcessorResult createForwardToSitePath( SitePath sitePath )
    {
        DirectiveRequestProcessorResult result = new DirectiveRequestProcessorResult();
        result.setForwardToSitePath( sitePath );
        return result;
    }

    public String getRedirectToAbsoluteURL()
    {
        return redirectToAbsoluteURL;
    }

    public void setRedirectToAbsoluteURL( String redirectToAbsoluteURL )
    {
        this.redirectToAbsoluteURL = redirectToAbsoluteURL;
    }

    public SitePath getRedirectToSitePath()
    {
        return redirectToSitePath;
    }

    public void setRedirectToSitePath( SitePath redirectToSitePath )
    {
        this.redirectToSitePath = redirectToSitePath;
    }

    public SitePath getForwardToSitePath()
    {
        return forwardToSitePath;
    }

    public void setForwardToSitePath( SitePath forwardToSitePath )
    {
        this.forwardToSitePath = forwardToSitePath;
    }
}
