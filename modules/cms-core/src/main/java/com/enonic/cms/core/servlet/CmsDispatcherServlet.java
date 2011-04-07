/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.enonic.cms.server.service.upgrade.UpgradeCheckerHelper;

import com.enonic.cms.domain.Attribute;

/**
 * This class implements a modification of the dispatcher servlet.
 */
public final class CmsDispatcherServlet
        extends DispatcherServlet
{
    private static final Logger LOG = LoggerFactory.getLogger( CmsDispatcherServlet.class );

    /**
     * Upgrade check parameter.
     */
    private final static String UPGRADE_CHECK_PARAM = "upgradeCheck";

    @Override
    public void init( ServletConfig config )
            throws ServletException
    {
        super.init( config );
        startContextIfNeeded();
    }

    protected void doService( HttpServletRequest req, HttpServletResponse res )
            throws Exception
    {
        startContextIfNeeded();
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

    /**
     * Check if upgrade is needed.
     */
    private boolean upgradeIsNeeded( HttpServletResponse res )
            throws Exception
    {
        return "true".equals( getInitParameter( UPGRADE_CHECK_PARAM ) ) && UpgradeCheckerHelper.checkUpgrade( getServletContext(), res );
    }

    private ApplicationContext startContextIfNeeded()
    {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext( getServletContext() );
        if ( appContext instanceof ConfigurableWebApplicationContext )
        {
            startContextIfNeeded( (ConfigurableWebApplicationContext) appContext );
        }
        return appContext;
    }

    private static void startContextIfNeeded( ConfigurableWebApplicationContext context )
    {
        if ( !context.isRunning() )
        {
            context.start();
        }
    }

}
