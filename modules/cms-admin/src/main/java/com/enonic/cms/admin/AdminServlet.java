/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.admin;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public final class AdminServlet
    extends AbstractApplicationServlet
{
    @Override
    protected Application getNewApplication( final HttpServletRequest req )
        throws ServletException
    {
        return null;
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
        throws ClassNotFoundException
    {
        return null;  
    }
}
