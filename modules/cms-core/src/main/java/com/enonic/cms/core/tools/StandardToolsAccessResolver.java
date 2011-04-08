/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.tools;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * This class implements the standard tools access resolver.
 */
@Component
public final class StandardToolsAccessResolver
    implements ToolsAccessResolver, InitializingBean
{
    /**
     * Wildcard.
     */
    private final static String WILDCARD = "*";

    /**
     * Allowed hosts.
     */
    private String allowedHosts;

    /**
     * Allowed hosts.
     */
    private HashSet<String> allowedHostSet;

    /**
     * After set properties.
     */
    public void afterPropertiesSet()
        throws Exception
    {
        this.allowedHostSet = new HashSet<String>();
        if ( this.allowedHosts != null )
        {
            for ( String s : this.allowedHosts.split( "," ) )
            {
                s = s.trim();
                if ( s.length() > 0 )
                {
                    // From config file
                    this.allowedHostSet.add( s );

                    // Resolve host name
                    String hostName = resolveHostName( s );
                    this.allowedHostSet.add( hostName );

                    // Resolve all host addresses
                    List<String> hostAddresses = resolveHostAddresses( s );
                    for ( String hostAddress : hostAddresses )
                    {
                        this.allowedHostSet.add( hostAddress );
                    }
                }
            }
        }
        else
        {
            this.allowedHostSet.add( WILDCARD );
        }
    }

    /**
     * Set allowed hosts.
     */
    @Value("${cms.tools.allowHosts}")
    public void setAllowedHosts( String allowedHosts )
    {
        this.allowedHosts = allowedHosts;
    }

    /**
     * Check if access.
     */
    public boolean hasAccess( HttpServletRequest req )
    {
        String remoteHostName = resolveRemoteHostName( req );
        for ( String allowedHost : this.allowedHostSet )
        {
            if ( hasAccess( remoteHostName, allowedHost ) )
            {
                return true;
            }
        }
        List<String> remoteAddresses = resolveRemoteAddresses( req );
        for ( String remoteAddress : remoteAddresses )
        {
            for ( String allowedHost : this.allowedHostSet )
            {
                if ( hasAccess( remoteAddress, allowedHost ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if access.
     */
    private boolean hasAccess( String remoteHost, String allowedHost )
    {
        return allowedHost.equals( WILDCARD ) || allowedHost.equalsIgnoreCase( remoteHost );
    }

    /**
     * Return the error message.
     */
    public String getErrorMessage( HttpServletRequest req )
    {
        String hostName = resolveRemoteHostName( req );

        StringBuffer msg = new StringBuffer();
        msg.append( "Access restricted for host '" );
        msg.append( hostName );
        msg.append( "'" );

        List<String> addresses = resolveRemoteAddresses( req );
        for ( int i = 0; i < addresses.size(); i++ )
        {
            String address = addresses.get( i );
            if ( !hostName.equalsIgnoreCase( address ) )
            {
                if ( i == 0 )
                {
                    msg.append( " (" );
                }
                else
                {
                    msg.append( ", " );
                }

                msg.append( address );

                if ( i == ( addresses.size() - 1 ) )
                {
                    msg.append( ")" );
                }
            }
        }

        return msg.toString();
    }

    /**
     * Resolve the host.
     */
    private String resolveRemoteHostName( HttpServletRequest req )
    {
        return resolveHostName( req.getRemoteHost() );
    }

    private String resolveHostName( String host )
    {
        try
        {
            return InetAddress.getByName( host ).getHostName();
        }
        catch ( Exception e )
        {
            return host;
        }
    }

    private List<String> resolveRemoteAddresses( HttpServletRequest req )
    {
        return resolveHostAddresses( req.getRemoteAddr() );
    }

    private List<String> resolveHostAddresses( String host )
    {
        List<String> addresses = new ArrayList<String>();
        try
        {
            for ( InetAddress address : InetAddress.getAllByName( host ) )
            {
                addresses.add( address.getHostAddress() );
            }
        }
        catch ( Exception e )
        {
        }
        return addresses;
    }
}
