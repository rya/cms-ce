/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public final class ClientHttpServletRequest
    implements HttpServletRequest
{

    public static final String DEFAULT_SCHEME = "http";

    public static final int DEFAULT_SERVER_PORT = 80;

    private String scheme = DEFAULT_SCHEME;

    private String serverName;

    private int serverPort = DEFAULT_SERVER_PORT;

    private final Map headers = new HashMap();

    private final Hashtable attributes = new Hashtable();

    private String requestURI;

    private HttpSession session;

    private String servletPath = "";

    private String contextPath = "";

    private Map parameters = new LinkedHashMap();

    public ClientHttpServletRequest( String serverName, int port, String uri, String contextPath )
    {
        assert ( serverName != null );
        assert ( port > 79 );
        assert ( uri != null );

        this.serverName = serverName;
        this.serverPort = port;
        if ( uri.startsWith( "/" ) )
        {
            this.requestURI = uri;
        }
        else
        {
            this.requestURI = "/" + uri;
        }

        if ( contextPath != null )
        {
            this.contextPath = contextPath;
        }

    }

    /**
     * Set a single value for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter( String name, String value )
    {
        setParameter( name, new String[]{value} );
    }

    /**
     * Set an array of values for the specified HTTP parameter.
     * <p>If there are already one or more values registered for the given
     * parameter name, they will be replaced.
     */
    public void setParameter( String name, String[] values )
    {
        assert ( name != null );
        this.parameters.put( name, values );
    }

    public void setParameters( Map params )
    {
        assert ( params != null );

        for ( Iterator it = params.keySet().iterator(); it.hasNext(); )
        {
            Object key = it.next();
            Object value = params.get( key );
            if ( value instanceof String )
            {
                this.setParameter( (String) key, (String) value );
            }
            else if ( value instanceof String[] )
            {
                this.setParameter( (String) key, (String[]) value );
            }
            else
            {
                throw new IllegalArgumentException(
                    "Parameter map value must be single value " + " or array of type [" + String.class.getName() + "]" );
            }
        }
    }

    public String getAuthType()
    {
        return null;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public Cookie[] getCookies()
    {

        return null;
    }

    public long getDateHeader( String name )
    {

        return 0;
    }

    public String getHeader( String name )
    {
        assert ( name != null );
        String[] arr = (String[]) this.headers.get( name );
        return ( arr != null && arr.length > 0 ? arr[0] : null );
    }

    public Enumeration getHeaderNames()
    {
        return Collections.enumeration( this.headers.keySet() );
    }

    public Enumeration getHeaders( String name )
    {
        assert ( name != null );
        String[] arr = (String[]) this.headers.get( name );
        return Collections.enumeration( Arrays.asList( arr ) );
    }

    public int getIntHeader( String name )
    {

        return 0;
    }

    public String getMethod()
    {

        return "GET";
    }

    public String getPathInfo()
    {

        return null;
    }

    public String getPathTranslated()
    {

        return null;
    }

    public String getQueryString()
    {
        StringBuffer queryString = new StringBuffer();
        Enumeration<String> keys = getParameterNames();
        for ( boolean appendAmp = false; keys.hasMoreElements(); appendAmp = true )
        {
            String key = keys.nextElement();
            String[] values = getParameterValues( key );
            if ( values.length > 1 )
            {
                for ( int cnt = 0; cnt < values.length; cnt++, appendAmp = true )
                {
                    if ( appendAmp )
                    {
                        queryString.append( '&' );
                    }
                    queryString.append( key );
                    queryString.append( '=' );
                    queryString.append( values[cnt] );
                }
            }
            else
            {
                if ( appendAmp )
                {
                    queryString.append( '&' );
                }
                queryString.append( key );
                queryString.append( '=' );
                queryString.append( getParameter( key ) );
            }
        }
        return queryString.toString();
    }

    public String getRemoteUser()
    {

        return null;
    }

    public String getRequestURI()
    {
        return requestURI;
    }

    public StringBuffer getRequestURL()
    {
        StringBuffer url = new StringBuffer( this.scheme );
        url.append( "://" ).append( this.serverName );

        if ( this.serverPort != 80 )
        {
            url.append( ':' );
            url.append( this.serverPort );
        }

        url.append( getRequestURI() );
        return url;
    }

    public String getRequestedSessionId()
    {

        return null;
    }

    public String getServletPath()
    {
        return servletPath;
    }

    public HttpSession getSession()
    {
        return getSession( true );
    }

    public HttpSession getSession( boolean create )
    {
        if ( this.session == null && create )
        {
            this.session = new ClientHttpSession();
        }
        return this.session;
    }

    public Principal getUserPrincipal()
    {

        return null;
    }

    public boolean isRequestedSessionIdFromCookie()
    {

        return false;
    }

    public boolean isRequestedSessionIdFromURL()
    {

        return false;
    }

    public boolean isRequestedSessionIdFromUrl()
    {

        return false;
    }

    public boolean isRequestedSessionIdValid()
    {

        return false;
    }

    public boolean isUserInRole( String role )
    {

        return false;
    }

    public Object getAttribute( String name )
    {
        return this.attributes.get( name );
    }

    public Enumeration getAttributeNames()
    {
        return this.attributes.keys();
    }

    public String getCharacterEncoding()
    {

        return null;
    }

    public int getContentLength()
    {

        return 0;
    }

    public String getContentType()
    {

        return null;
    }

    public ServletInputStream getInputStream()
        throws IOException
    {

        return null;
    }

    public String getLocalAddr()
    {

        return null;
    }

    public String getLocalName()
    {

        return null;
    }

    public int getLocalPort()
    {

        return 0;
    }

    public Locale getLocale()
    {

        return null;
    }

    public Enumeration getLocales()
    {

        return null;
    }

    public String getParameter( String name )
    {
        assert ( name != null );
        String[] arr = (String[]) this.parameters.get( name );
        return ( arr != null && arr.length > 0 ? arr[0] : null );
    }

    public Map getParameterMap()
    {
        return Collections.unmodifiableMap( this.parameters );
    }

    public Enumeration getParameterNames()
    {
        return Collections.enumeration( this.parameters.keySet() );
    }

    public String[] getParameterValues( String name )
    {
        assert ( name != null );
        return (String[]) this.parameters.get( name );
    }

    public String getProtocol()
    {

        return null;
    }

    public BufferedReader getReader()
        throws IOException
    {

        return null;
    }

    public String getRealPath( String path )
    {

        return null;
    }

    public String getRemoteAddr()
    {

        return null;
    }

    public String getRemoteHost()
    {

        return null;
    }

    public int getRemotePort()
    {

        return 0;
    }

    public RequestDispatcher getRequestDispatcher( String path )
    {

        return null;
    }

    public String getScheme()
    {

        return scheme;
    }

    public String getServerName()
    {

        return serverName;
    }

    public int getServerPort()
    {

        return serverPort;
    }

    public boolean isSecure()
    {

        return false;
    }

    public void removeAttribute( String name )
    {
        assert ( name != null );
        this.attributes.remove( name );
    }

    public void setAttribute( String name, Object o )
    {
        assert ( name != null );
        if ( o != null )
        {
            this.attributes.put( name, o );
        }
        else
        {
            removeAttribute( name );
        }
    }

    public void setCharacterEncoding( String env )
        throws UnsupportedEncodingException
    {

    }

}
