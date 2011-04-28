/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
        final ServletContext servletContext = req.getSession().getServletContext();

        final WebApplicationContext springWebApplicationContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext( servletContext );

        return springWebApplicationContext.getBean( getApplicationClass() );
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
    {
        return AdminApplication.class;
    }
}
