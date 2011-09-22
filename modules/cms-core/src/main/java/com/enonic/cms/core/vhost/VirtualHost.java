/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.vhost;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.style.ToStringCreator;
import org.springframework.web.util.UrlPathHelper;

/**
 * This class implements the holder of virtual host information.
 */
public final class VirtualHost
    implements Comparable<VirtualHost>
{
    /**
     * Static reusable url path helper.
     */
    private final static UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    static
    {
        URL_PATH_HELPER.setUrlDecode( false );
    }

    /**
     * Separator.
     */
    private final static char SERVER_SEPARATOR = '/';

    /**
     * Server name.
     */
    private String serverName;

    /**
     * Source path.
     */
    private String sourceContext;

    /**
     * Target path.
     */
    private String targetPath;

    /**
     * Construct the virtual host.
     */
    public VirtualHost( String pattern, String targetPath )
        throws InvalidVirtualHostPatternException
    {

        this.serverName = getServerNameFromPattern( pattern );
        if ( this.serverName == null || this.serverName.trim().length() == 0 )
        {
            throw new InvalidVirtualHostPatternException( pattern, "Missing server name" );
        }
        this.sourceContext = resolveSourceContextFromPattern( pattern );
        this.targetPath = normalizePath( targetPath );
    }

    /**
     * Return the pattern.
     */
    public String getPattern()
    {
        if ( this.sourceContext.equals( "" ) )
        {
            return this.serverName + SERVER_SEPARATOR + this.sourceContext;
        }
        else
        {
            return this.serverName;
        }
    }

    /**
     * Return the server name.
     */
    public String getServerName()
    {
        return this.serverName;
    }

    /**
     * Return the source context.
     */
    public String getSourceContext()
    {
        return this.sourceContext;
    }

    /**
     * Return the source path.
     */
    public String getSourcePath()
    {
        return this.sourceContext.equals( "" ) ? "/" : "/" + this.sourceContext;
    }

    /**
     * Return the source path.
     */
    public String getFullSourcePath( HttpServletRequest req )
    {
        String contextPath = req.getContextPath();
        if ( !this.sourceContext.equals( "" ) )
        {
            return contextPath + "/" + this.sourceContext;
        }
        else
        {
            return contextPath;
        }
    }

    /**
     * Return the target path.
     */
    public String getTargetPath()
    {
        return this.targetPath;
    }

    /**
     * Return the target path.
     */
    public String getFullTargetPath( HttpServletRequest req )
    {
        String path = getRealRequestPathInfo( req );
        String sourcePath = getSourcePath();
        if ( !sourcePath.equals( "/" ) && path.startsWith( sourcePath ) )
        {
            path = path.substring( sourcePath.length() );
        }

        return this.targetPath + path;
    }

    /**
     * Return true if it matches.
     */
    public boolean matches( HttpServletRequest req )
    {
        return matchesServerName( req ) && matchesSourcePath( req );
    }

    /**
     * Return true if it matches the server name.
     */
    private boolean matchesServerName( HttpServletRequest req )
    {
        String serverName = req.getServerName();
        return ( serverName != null ) && this.serverName.equals( serverName );
    }

    /**
     * Return true if it matches the source path.
     */
    private boolean matchesSourcePath( HttpServletRequest req )
    {
        String path = getRealRequestPath( req );
        String sourcePath = getFullSourcePath( req );
        return sourcePath.equals( "/" ) || path.equals( sourcePath ) || path.startsWith( sourcePath + "/" );
    }

    /**
     * Return the real path.
     */
    private String getRealRequestPath( HttpServletRequest req )
    {
        return req.getContextPath() + getRealRequestPathInfo( req );
    }

    /**
     * Return the real path.
     */
    private String getRealRequestPathInfo( HttpServletRequest req )
    {
        String pathInfo = URL_PATH_HELPER.getPathWithinApplication( req );
        return pathInfo != null ? pathInfo : "";
    }

    /**
     * Compare to other virtual host.
     */
    public int compareTo( VirtualHost other )
    {
        int compared = compareServerName( other.serverName );
        if ( compared == 0 )
        {
            return compareSourceContext( other.sourceContext );
        }
        else
        {
            return compared;
        }
    }

    /**
     * Compare to other virtual host.
     */
    private int compareServerName( String hostName )
    {
        return this.serverName.compareTo( hostName );
    }

    /**
     * Compare to other virtual host.
     */
    private int compareSourceContext( String sourceContext )
    {
        int compared = sourceContext.length() - this.sourceContext.length();
        if ( compared == 0 )
        {
            return this.sourceContext.compareTo( sourceContext );
        }
        else
        {
            return compared;
        }
    }

    /**
     * Return true if equals.
     */
    public boolean equals( Object other )
    {
        return ( other instanceof VirtualHost ) && ( compareTo( (VirtualHost) other ) == 0 );
    }

    /**
     * Normalize the server name.
     */
    private static String getServerNameFromPattern( String pattern )
    {
        pattern = pattern.trim();
        String serverName = pattern;
        int pos = pattern.indexOf( SERVER_SEPARATOR );
        if ( pos > -1 )
        {
            serverName = pattern.substring( 0, pos );
        }

        return removeTrailingDots( serverName );
    }

    private static String removeTrailingDots( String str )
    {
        while ( str.endsWith( "." ) )
        {
            str = str.substring( 0, str.length() - 1 );

        }

        return str;
    }

    private static String resolveSourceContextFromPattern( String pattern )
    {

        int pos = pattern.indexOf( SERVER_SEPARATOR );
        if ( pos > -1 )
        {
            return pattern.substring( pos + 1 ).trim();
        }
        else
        {
            return "";
        }
    }

    private static String normalizePath( String path )
    {
        if ( path.equals( "" ) )
        {
            return "/";
        }
        else if ( path.equals( "/" ) )
        {
            return path;
        }
        else
        {
            if ( path.endsWith( "/" ) )
            {
                path = path.substring( 0, path.length() - 1 );
            }

            if ( !path.startsWith( "/" ) )
            {
                path = "/" + path;
            }

            return path;
        }
    }

    public String toString()
    {
        ToStringCreator creator = new ToStringCreator( this );
        creator.append( "serverName", this.serverName );
        creator.append( "sourceContext", this.sourceContext );
        creator.append( "targetPath", this.targetPath );
        return creator.toString();
    }
}
