/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.server.service.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.DispatcherServlet;
import com.enonic.cms.core.Attribute;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.upgrade.UpgradeCheckerHelper;

/**
 * This class implements a modification of the dispatcher servlet.
 */
public final class CmsDispatcherServlet
    extends DispatcherServlet
{
    private static final Logger LOG = LoggerFactory.getLogger( CmsDispatcherServlet.class );

    private final static List<HttpMethod> ALLOWED_HTTP_METHODS =
        Arrays.asList( HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS );

    /**
     * Upgrade check parameter.
     */
    private final static String UPGRADE_CHECK_PARAM = "upgradeCheck";

    @Override
    protected void doOptions( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        response.setHeader( "Allow", StringUtils.join( ALLOWED_HTTP_METHODS, "," ) );
        response.setStatus( HttpServletResponse.SC_OK );
    }

    protected void doService( HttpServletRequest req, HttpServletResponse res )
        throws Exception
    {

        HttpMethod requestMethod = HttpMethod.valueOf( req.getMethod() );

        if ( !ALLOWED_HTTP_METHODS.contains( requestMethod ) )
        {
            res.sendError( HttpServletResponse.SC_METHOD_NOT_ALLOWED );
            return;
        }

        if ( upgradeIsNeeded( res ) )
        {
            return;
        }

        ServletRequestAccessor.setRequest( req );
        // resolve and set original url if not set
        if ( req.getAttribute( Attribute.ORIGINAL_URL ) == null )
        {
            final String originalUrl = OriginalUrlResolver.get().resolveOriginalUrl( req );
            req.setAttribute( Attribute.ORIGINAL_URL, originalUrl );
        }

        if ( LOG.isDebugEnabled() )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "Dispatching request to URL: " + req.getRequestURL() );
            LOG.debug( msg.toString() );
        }

        super.doService( req, res );
    }

    private boolean upgradeIsNeeded( HttpServletResponse res )
        throws Exception
    {
        return "true".equals( getInitParameter( UPGRADE_CHECK_PARAM ) ) && UpgradeCheckerHelper.checkUpgrade(
                getServletContext(), res );
    }
}
