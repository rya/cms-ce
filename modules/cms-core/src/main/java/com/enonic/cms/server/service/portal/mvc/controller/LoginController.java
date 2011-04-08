/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.portal.mvc.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.domain.command.LoginCommand;

import com.enonic.cms.business.portal.support.LoginPagePathResolverService;

import com.enonic.cms.domain.SitePath;

public class LoginController
    extends AbstractSiteCommandController
{

    private boolean useForward;

    private LoginPagePathResolverService loginPagePathResolverService;


    public void setLoginPagePathResolverService( LoginPagePathResolverService value )
    {
        this.loginPagePathResolverService = value;
    }

    public void setUseForward( boolean useForward )
    {
        this.useForward = useForward;
    }

    /**
     * Handles login and login page requests.
     *
     * @param errors any errors, these are ignored
     */
    protected ModelAndView handle( HttpServletRequest request, HttpServletResponse response, Object command, BindException errors,
                                   SitePath sitePath )
        throws Exception
    {
        LoginCommand loginCommand = (LoginCommand) command;

        SitePath sitePathAndParams;

        if ( loginCommand.hasLogin() )
        {
            sitePathAndParams = loginPagePathResolverService.resolvePathToUserServicesLoginPage( sitePath );

            return redirectAndForwardHelper.getModelAndView( request, sitePathAndParams, useForward );
        }
        else
        {
            sitePathAndParams = loginPagePathResolverService.resolvePathToDefaultPageInMenu( sitePath );

            return redirectAndForwardHelper.getForwardModelAndView( request, sitePathAndParams );
        }
    }
}
