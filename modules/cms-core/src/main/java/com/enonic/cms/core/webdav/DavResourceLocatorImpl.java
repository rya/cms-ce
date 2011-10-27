/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.webdav;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;

/**
 * This class implements the resource locator.
 */
public final class DavResourceLocatorImpl
    implements DavResourceLocator
{
    /**
     * Prefix.
     */
    private final String prefix;

    /**
     * Resource path.
     */
    private final String resourcePath;

    /**
     * Locator factory.
     */
    private final DavLocatorFactory factory;

    /**
     * Href.
     */
    private final String href;

    /**
     * Resource locator.
     */
    public DavResourceLocatorImpl( String prefix, String resourcePath, DavLocatorFactory factory )
    {
        this.prefix = prefix;
        this.factory = factory;

        if ( resourcePath.endsWith( "/" ) && !"/".equals( resourcePath ) )
        {
            resourcePath = resourcePath.substring( 0, resourcePath.length() - 1 );
        }

        this.resourcePath = resourcePath;
        this.href = this.prefix + Text.escapePath( this.resourcePath );
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefix()
    {
        return this.prefix;
    }

    /**
     * {@inheritDoc}
     */
    public String getResourcePath()
    {
        return this.resourcePath;
    }

    /**
     * {@inheritDoc}
     */
    public String getWorkspacePath()
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public String getWorkspaceName()
    {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSameWorkspace( DavResourceLocator locator )
    {
        return isSameWorkspace( locator.getWorkspaceName() );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSameWorkspace( String workspaceName )
    {
        return getWorkspaceName().equals( workspaceName );
    }

    /**
     * {@inheritDoc}
     */
    public String getHref( boolean isCollection )
    {
        String suffix = ( isCollection && !isRootLocation() ) ? "/" : "";
        return this.href + suffix;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRootLocation()
    {
        return "/".equals( this.resourcePath );
    }

    /**
     * {@inheritDoc}
     */
    public DavLocatorFactory getFactory()
    {
        return this.factory;
    }

    /**
     * {@inheritDoc}
     */
    public String getRepositoryPath()
    {
        return getResourcePath();
    }

    /**
     * Return the hash code.
     */
    public int hashCode()
    {
        return this.href.hashCode();
    }

    /**
     * Return true if equals.
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof DavResourceLocator )
        {
            DavResourceLocator other = (DavResourceLocator) obj;
            return hashCode() == other.hashCode();
        }

        return false;
    }
}
