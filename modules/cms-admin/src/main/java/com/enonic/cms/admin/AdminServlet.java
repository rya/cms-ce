/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public final class AdminServlet
    extends AbstractApplicationServlet
{
    @Override
    protected Application getNewApplication( final HttpServletRequest req )
        throws ServletException
    {
        ServletContext servletContext = req.getSession().getServletContext();

        WebApplicationContext springWebApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );

        AdminApplication adminApplication = springWebApplicationContext.getBean( AdminApplication.class );

        return adminApplication;
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
        throws ClassNotFoundException
    {
        return AdminApplication.class;
    }
}
