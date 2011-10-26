/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;
import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.core.security.user.User;


/**
 * This class implements the abstract tool controller.
 */
public abstract class AbstractToolController
    extends AdminHandlerBaseServlet
    implements Controller
{
    private ViewResolver viewResolver;

    public ModelAndView handleRequest( HttpServletRequest request, HttpServletResponse response )
        throws Exception
    {

        performTask( request, response );
        return null;
    }

    /**
     * Do handle request.
     */
    protected abstract void doHandleRequest( HttpServletRequest req, HttpServletResponse res, ExtendedMap formItems );

    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
    {

        doHandleRequest( request, response, formItems );
    }

    protected void process(HttpServletRequest request, HttpServletResponse response, final HashMap<String, Object> model, final String templateName)
    {
        try
        {
            final View view = this.viewResolver.resolveViewName(templateName, Locale.getDefault());
            view.render(model, request, response);
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Could not find template: " + templateName, e );
        }
    }

    public void setViewResolver( final ViewResolver viewResolver )
    {
        this.viewResolver = viewResolver;
    }
}
