/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

import com.enonic.cms.admin.spring.events.ApplicationCreatedEvent;
import com.enonic.cms.admin.spring.events.ApplicationEventListener;

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

        Application application = springWebApplicationContext.getBean( getApplicationClass() );

        /* will eagerly create vaadin beans - this will fail */
        //springWebApplicationContext.publishEvent( new ApplicationEvent("core"){} );

        Map<String,ApplicationEventListener> listeners = springWebApplicationContext.getBeansOfType( ApplicationEventListener.class );
        for (ApplicationEventListener event : listeners.values()) {
            event.onApplicationEvent( new ApplicationCreatedEvent() );
        }


        return application;
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
    {
        return AdminApplication.class;
    }
}
