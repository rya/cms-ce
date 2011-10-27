/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavSessionProvider;
import org.apache.jackrabbit.webdav.WebdavRequest;
import org.apache.jackrabbit.webdav.server.AbstractWebdavServlet;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.upgrade.UpgradeCheckerHelper;

/**
 * This class implements the webdav servlet.
 */
public final class SimpleDavServlet
    extends AbstractWebdavServlet
{
    /**
     * Authenticate header value.
     */
    private final static String AUTHENTICATE_HEADER_VALUE = "Basic Realm=Enonic Webdav Server";

    /**
     * Session provider.
     */
    private DavSessionProvider sessionProvider;

    /**
     * Locator factory.
     */
    private DavLocatorFactory locatorFactory;

    /**
     * Resource factory.
     */
    private DavResourceFactory resourceFactory;

    /**
     * Initialize the servlet.
     */
    public void init( ServletConfig config )
        throws ServletException
    {
        DavConfiguration davConfig = getDavConfiguration( config.getServletContext() );
        setLocatorFactory( new DavLocatorFactoryImpl() );
        setDavSessionProvider( new DavSessionProviderImpl( davConfig.getSecurityService(), davConfig.getResourceAccessResolver() ) );
        setResourceFactory( new DavResourceFactoryImpl( davConfig.getFileResourceService() ) );
        super.init( config );
    }


    protected void service( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        if ( UpgradeCheckerHelper.checkUpgrade( getServletContext(), response ) )
        {
            return;
        }

        ServletRequestAccessor.setRequest( request );
        super.service( request, response );
    }

    /**
     * Return the dav configuration.
     */
    private DavConfiguration getDavConfiguration( ServletContext context )
        throws ServletException
    {
        WebApplicationContext appContext = WebApplicationContextUtils.getRequiredWebApplicationContext( context );
        String[] beanNames = appContext.getBeanNamesForType( DavConfiguration.class );
        if ( ( beanNames != null ) && ( beanNames.length > 0 ) )
        {
            return (DavConfiguration) appContext.getBean( beanNames[0] );
        }

        throw new ServletException( "No dav configuration set" );
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isPreconditionValid( WebdavRequest request, DavResource resource )
    {
        return !resource.exists() || request.matchesIfHeader( resource );
    }

    /**
     * {@inheritDoc}
     */
    public DavSessionProvider getDavSessionProvider()
    {
        return this.sessionProvider;
    }

    /**
     * {@inheritDoc}
     */
    public void setDavSessionProvider( DavSessionProvider sessionProvider )
    {
        this.sessionProvider = sessionProvider;
    }

    /**
     * {@inheritDoc}
     */
    public DavLocatorFactory getLocatorFactory()
    {
        return this.locatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void setLocatorFactory( DavLocatorFactory locatorFactory )
    {
        this.locatorFactory = locatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceFactory getResourceFactory()
    {
        return this.resourceFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void setResourceFactory( DavResourceFactory resourceFactory )
    {
        this.resourceFactory = resourceFactory;
    }

    /**
     * {@inheritDoc}
     */
    public String getAuthenticateHeaderValue()
    {
        return AUTHENTICATE_HEADER_VALUE;
    }
}
