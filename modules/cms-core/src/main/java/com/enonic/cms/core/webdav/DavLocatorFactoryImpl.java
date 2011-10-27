/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.core.vhost.VirtualHostHelper;

/**
 * This class implements the locator factory.
 */
public final class DavLocatorFactoryImpl
    implements DavLocatorFactory
{
    /**
     * Dav prefix.
     */
    private final static String PREFIX = "/dav";

    /**
     * {@inheritDoc}
     */
    public DavResourceLocator createResourceLocator( String prefix, String href )
    {
        StringBuffer buff = new StringBuffer();

        if ( href == null )
        {
            href = "";
        }

        if ( prefix != null && prefix.length() > 0 )
        {
            buff.append( prefix );
            if ( href.startsWith( prefix ) )
            {
                href = href.substring( prefix.length() );
            }
        }

        if ( href.startsWith( PREFIX ) )
        {
            href = href.substring( PREFIX.length() );
        }

        if ( "".equals( href ) )
        {
            href = "/";
        }

        buff.append( getBasePath() );
        return new DavResourceLocatorImpl( buff.toString(), Text.unescape( href ), this );
    }

    /**
     * Return the base path.
     */
    private String getBasePath()
    {
        String basePath = VirtualHostHelper.getBasePath( ServletRequestAccessor.getRequest() );
        return basePath != null ? basePath : PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceLocator createResourceLocator( String prefix, String workspacePath, String resourcePath )
    {
        return createResourceLocator( prefix, workspacePath, resourcePath, true );
    }

    /**
     * {@inheritDoc}
     */
    public DavResourceLocator createResourceLocator( String prefix, String workspacePath, String path, boolean isResourcePath )
    {
        return new DavResourceLocatorImpl( prefix, path, this );
    }
}
