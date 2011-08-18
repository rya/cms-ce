/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.tools;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.w3c.dom.Document;

import freemarker.template.Configuration;
import freemarker.template.Template;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.AdminHelper;
import com.enonic.vertical.adminweb.VerticalAdminException;
import com.enonic.vertical.engine.VerticalEngineException;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.domain.security.user.User;


/**
 * This class implements the abstract tool controller.
 */
public abstract class AbstractToolController
    extends AdminHandlerBaseServlet
    implements Controller
{
    private Configuration freemarkerConfiguration;

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

    /**
     * Return the base path.
     */
    protected String createBaseUrl( HttpServletRequest req )
    {
        StringBuffer str = new StringBuffer();
        str.append( req.getScheme() ).append( "://" ).append( req.getServerName() );

        if ( req.getServerPort() != 80 )
        {
            str.append( ":" ).append( req.getServerPort() );
        }

        str.append( req.getContextPath() );
        return str.toString();
    }

    /**
     * Redirect to self.
     */
    protected void redirectToSelf( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {
        redirectToReferer( req, res );
    }


    public void handlerCustom( HttpServletRequest request, HttpServletResponse response, HttpSession session, AdminService admin,
                               ExtendedMap formItems, String operation, ExtendedMap parameters, User user, Document verticalDoc )
        throws VerticalAdminException, VerticalEngineException
    {

        doHandleRequest( request, response, formItems );
    }

    protected void process( HttpServletResponse response, final HashMap<String, Object> model, final String templateName )
    {

        try
        {
            Template template = getTemplate( templateName );

            template.process( model, response.getWriter() );

        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Could not find template: " + templateName );
        }
    }

    private Template getTemplate( String templateName )
        throws IOException
    {
        if ( !StringUtils.endsWith( templateName, ".ftl" ) )
        {
            templateName = templateName + ".ftl";
        }

        Template template = freemarkerConfiguration.getTemplate( templateName );

        if ( template == null )
        {
            throw new RuntimeException( "Template not found: " + templateName );
        }
        return template;
    }

    public void redirectToReferer( HttpServletRequest request, HttpServletResponse response )
        throws VerticalAdminException
    {
        String redirect = AdminHelper.getAdminPath( request, true ) + "/";

        URL redirectURL = new URL( redirect );

        redirectClientToURL( redirectURL, response );
    }

    public void setFreemarkerConfiguration( Configuration freemarkerConfiguration )
    {
        this.freemarkerConfiguration = freemarkerConfiguration;
    }


}
