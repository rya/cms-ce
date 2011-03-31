/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.esl.util.StringUtil;

import com.enonic.cms.framework.util.HttpServletUtil;

import com.enonic.cms.core.service.AdminService;

import com.enonic.cms.domain.resource.ResourceFile;
import com.enonic.cms.domain.resource.ResourceKey;

public class ResourceDataServlet
    extends AbstractAdminwebServlet
{

    public void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {

        String keyStr = request.getParameter( "id" );
        if ( keyStr != null && keyStr.length() > 0 )
        {
            ResourceKey key = new ResourceKey( keyStr );

            AdminService admin = lookupAdminBean();
            ResourceFile res = resourceService.getResourceFile( key );

            if ( res != null )
            {
                HttpServletUtil.copyNoCloseOut( res.getDataAsInputStream(), response.getOutputStream() );
            }
            else
            {
                String msg = "Resource not found: %0";
                VerticalAdminLogger.warn( this.getClass(), 0, msg, keyStr, null );
                response.sendError( HttpServletResponse.SC_NOT_FOUND, StringUtil.expandString( msg, keyStr, null ) );
            }
        }
        else
        {
            String message = "Resource key not specified.";
            VerticalAdminLogger.warn( this.getClass(), 0, message, null );
            response.sendError( HttpServletResponse.SC_NOT_FOUND, message );
        }
    }
}