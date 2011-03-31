/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.spring.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * This class implements the command doc controller.
 */
public final class RedirectController
    extends AbstractController
{
    /**
     * Redirect url.
     */
    private String redirectUrl;

    /**
     * Set the redirect url.
     */
    public void setRedirectUrl( String redirectUrl )
    {
        this.redirectUrl = redirectUrl;
    }

    /**
     * Handle the request.
     */
    protected final ModelAndView handleRequestInternal( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        return new ModelAndView( "redirect:" + this.redirectUrl, null );
    }
}
