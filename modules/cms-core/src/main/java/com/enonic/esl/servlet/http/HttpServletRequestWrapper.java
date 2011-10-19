/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.servlet.http;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper
    extends javax.servlet.http.HttpServletRequestWrapper
{

//    private HttpServletRequestWrapper request;

    private String servletPath;

    /**
     * A local set with values which overrides the parameters in the <code>HttpServletRequest</code> that is wrapped.  Whether the
     * <code>HttpServletRequest</code> values are just overwritten or completely ignored, depends on the setting in the <code>inherit</code>
     * value.
     */
    private Map<String, String[]> queryParams;

    /**
     * Determines if the request parameters are taken into account when looking for a parameter. If this value is <code>true</code>, and no
     * parameter value can be found in the wrapper, the request object is searched for the same parameter.  If this value is
     * <code>false</code>, then the request object is ignored completely as far as parameters are concerned, but is still used for all other
     * methods.
     */
    private boolean inherit = false;

    // simulated variables
    //private String contextPath;                ex: /vertical
    //private String queryString;                ex: foo=bar&x=y
    //private String requestURI;                 ex: /vertical/page
    //private String requestURL;                 ex: http://somehost.com/vertical/page

    public HttpServletRequestWrapper( HttpServletRequest request )
    {
        this( request, null );
    }

    public HttpServletRequestWrapper( HttpServletRequest request, Map<String, String[]> params )
    {
        this( request, params, false );
    }

    public HttpServletRequestWrapper( HttpServletRequest request, Map<String, String[]> params, boolean i )
    {
        super( request );

        queryParams = new HashMap<String, String[]>();
        inherit = i;

        if ( params != null )
        {
            Iterator<Map.Entry<String, String[]>> itr = params.entrySet().iterator();

            while ( itr.hasNext() )
            {
                Map.Entry<String, String[]> entry = itr.next();
                queryParams.put( entry.getKey(), makeStringArray( entry.getValue() ) );
            }
        }

        // If this wrapper wraps and already wrapped request, replace the old wrapper, to avoid extra layers:
        if ( inherit && ( request instanceof HttpServletRequestWrapper ) )
        {
            HttpServletRequestWrapper dynamicReq = (HttpServletRequestWrapper) request;

            Map<String, String[]> reqParams = dynamicReq.getWrapperParameterMap();

            if ( reqParams != null )
            {
                Iterator<Map.Entry<String, String[]>> itr = reqParams.entrySet().iterator();

                while ( itr.hasNext() )
                {
                    Map.Entry<String, String[]> entry = itr.next();

                    String name = entry.getKey();
                    String[] oldValues = entry.getValue();

                    String[] curValues = queryParams.get( name );

                    if ( curValues == null )
                    {
                        queryParams.put( name, oldValues );
                    }
                    else
                    {
                        String[] newValues = new String[oldValues.length + curValues.length];
                        System.arraycopy( oldValues, 0, newValues, 0, oldValues.length );
                        System.arraycopy( curValues, 0, newValues, oldValues.length, curValues.length );
                        queryParams.put( name, newValues );
                    }
                }
            }

            // After pulling out all the parameters of the wrapper object, it can be discarded, so we don't have a wrapper around a wrapper.
            setRequest( dynamicReq.getRequest() );
        }
    }

    private static String[] makeStringArray( Object value )
    {
        if ( value == null )
        {
            // Don't really know when this occurs, but it has occured on Enonic and Helsebiblioteket
            return new String[0];
        }
        else if ( value instanceof String[] )
        {
            return (String[]) value;
        }
        else
        {
            return new String[]{value.toString()};
        }
    }

    /**
     * @return A map of all parameters stored locally in this wrapper.
     */
    public Map<String, String[]> getWrapperParameterMap()
    {
        return queryParams;
    }

    /**
     * A provided method with this wrapper implementation, allowing the user set a parameter.
     * <p/>
     * If the request already contains a value with the same name, it is kept, although it will no longer be visible.  However if the
     * wrapper contains a value with the same name, this new value will override the existing one.
     *
     * @param name  Parameter name.
     * @param value Parameter value.
     */
    public void setParameter( String name, String value )
    {
        queryParams.put( name, new String[]{value} );
    }

    /**
     * A provided method with this wrapper implementation, allowing the user to set multiple parameter values.
     * <p/>
     * If the request already contains a value with the same name, it is kept, although it will no longer be visible.  However if the
     * wrapper contains a value with the same name, these new values will override the existing one.
     *
     * @param name   Parameter name.
     * @param values Parameter values.
     */
    public void setParameterValues( String name, String[] values )
    {
        queryParams.put( name, values );
    }

    public Enumeration<String> getParameterNames()
    {
        Set<String> names = new HashSet<String>();

        if ( inherit )
        {
            Enumeration<?> enumer = super.getParameterNames();
            while ( enumer.hasMoreElements() )
            {
                names.add( enumer.nextElement().toString() );
            }
        }

        names.addAll( queryParams.keySet() );

        return Collections.enumeration( names );
    }

    public String[] getParameterValues( String name )
    {
        String[] values = queryParams.get( name );
        if ( inherit && ( values == null ) )
        {
            return super.getParameterValues( name );
        }
        return values;
    }

    /**
     * Creates and returns a new <code>Map</code> with all the valid parameters.  These may or may not include the values in the
     * <code>request</code>, depending on the setting of the <code>inherit</code> parameter on this class.
     */
    public Map<String, String[]> getParameterMap()
    {
        String s;

        Map<String, String[]> map = new HashMap<String, String[]>();
        Enumeration<String> enumer = getParameterNames();

        // No need to check the inherit parameter, since the getParameterNames() and getParameterValues() methods do it.
        while ( enumer.hasMoreElements() )
        {
            s = enumer.nextElement();
            map.put( s, getParameterValues( s ) );
        }

        return map;
    }

    /**
     * Returns the value of the parameter as a <code>String</code>, or <code>null</code> if the parameter does not exist. Depending on the
     * <code>inherit</code> setting for this instance, the parameter will be searched for only in the wrapper, or also in the wrapped
     * request.
     * <p/>
     * If the parameter has multiple values, it will return one of the values at random, so you should only use this method when you are
     * sure the parameter has only one value.
     */
    public String getParameter( String name )
    {
        String[] values = queryParams.get( name );

        if ( inherit && ( values == null ) )
        {
            return super.getParameter( name );
        }

        if ( ( values != null ) && ( values.length > 0 ) )
        {
            return values[0];
        }
        else
        {
            return null;
        }
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

    public String getRequestURI()
    {
        String requestURI;
        if ( servletPath != null )
        {
            if ( servletPath.length() == 0 || servletPath.charAt( 0 ) != '/' )
            {
                requestURI = getContextPath() + '/' + servletPath;
            }
            else
            {
                requestURI = getContextPath() + servletPath;
            }
        }
        else
        {
//            requestURI = request.getRequestURI();
            requestURI = super.getRequestURI();
        }

        return requestURI;
    }

    public StringBuffer getRequestURL()
    {
        StringBuffer requestURL = new StringBuffer( "http" );

        if ( super.isSecure() )
        {
//        if (request.isSecure()) {
            requestURL.append( "s://" );
        }
        else
        {
            requestURL.append( "://" );
        }
//            requestURL.append(request.getServerName());
//            int origServerPort = request.getServerPort();
        requestURL.append( super.getServerName() );
        int origServerPort = super.getServerPort();
        if ( origServerPort != 80 )
        {
            requestURL.append( ':' );
            requestURL.append( origServerPort );
        }
        requestURL.append( getRequestURI() );

        return requestURL;
    }

    public String getServletPath()
    {
        if ( servletPath != null )
        {
            if ( servletPath.length() > 0 && servletPath.charAt( 0 ) == '/' )
            {
                return servletPath;
            }
            else
            {
                return '/' + servletPath;
            }
        }
        else
        {
            return super.getServletPath();
        }
    }

    /**
     * @param string
     */
    public void setServletPath( String string )
    {
        servletPath = string;
    }

    /**
     * @return
     * @deprecated This method is provided for backwards compatibility.  The old <code>paramsMasked</code> parameter is the exact opposite
     *             of the new <code>inherit</code> parameter, so calls to this method should be replace by <code>isInherit()</code>,
     *             negating the result.
     */
    public boolean isParamsMasked()
    {
        return !inherit;
    }

    /**
     * This method
     *
     * @param b
     * @deprecated This method is provided for backwards compatibility.  The old <code>paramsMasked</code> parameter is the exact opposite
     *             of the new <code>inherit</code> parameter, so calls to this method should be replaced by calls to
     *             <code>setInherit()</code>, with the parameter negated.
     */
    public void setParamsMasked( boolean b )
    {
        inherit = !b;
    }

    public boolean isInherit()
    {
        return inherit;
    }

    /**
     * Defines whether the parameters in the request is included in the result set, when working with parameters.
     *
     * @param b <code>true</code>, includes the parameters from the request, <code>false</code> does not.
     * @deprecated This method should not be used.  The parameter should only be set in the constructor, and not be changed after that.
     */
    public void setInherit( boolean b )
    {
        inherit = b;
    }

//    /**
//     * @see javax.servlet.ServletRequestWrapper#getRequest()
//     */
//    public ServletRequest getRequest() {
//        return this.request;
//    }
//
//    /**
//     * @see javax.servlet.ServletRequestWrapper#setRequest(javax.servlet.ServletRequest)
//     */
//    public void setRequest(HttpServletRequestWrapper req) {
//        request = req;
//    }
}