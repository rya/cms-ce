/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.page;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;

import com.enonic.cms.domain.structure.portlet.PortletKey;


public abstract class PageRequestBase
    implements PageRequest
{
    /**
     * Browser/platform constants.
     */
    private final static String MSIE = "msie";

    private final static String OPERA = "opera";

    private final static String MOZILLA = "mozilla";

    private final static String WINDOWS = "windows";

    private final static String MAC = "mac";

    private final static String UNIX = "unix";

    private final static String UNKNOWN = "unknown";

    private boolean actionRequest;

    private PortletKey currentPortletKey;

    private Map<String, String[]> getLocalParameterMap()
    {
        Map<String, String[]> map = getParameterMap();
        Map<String, String[]> result = new HashMap<String, String[]>();

        for ( String key : map.keySet() )
        {
            result.put( key, map.get( key ) );
        }

        return result;
    }

    public final String[] getHeaderNames()
    {
        Set<String> keys = getHeaderMap().keySet();
        return keys.toArray( new String[keys.size()] );
    }

    public final String getHeader( String name )
    {
        return getHeaderMap().get( name );
    }

    public final String[] getParameterNames()
    {
        Set<String> names = getLocalParameterMap().keySet();
        return names.toArray( new String[names.size()] );
    }

    public final String getParameter( String name )
    {
        String[] values = getParameterValues( name );
        if ( ( values == null ) || ( values.length == 0 ) )
        {
            return null;
        }
        else
        {
            return values[0];
        }
    }

    public final String getParameter( String name, String def )
    {
        String value = getParameter( name );
        return value != null ? value : def;
    }

    public final String[] getParameterValues( String name )
    {
        return getLocalParameterMap().get( name );
    }

    public final String getClientType()
    {
        String userAgent = getHeader( "user-agent" );
        if ( userAgent == null )
        {
            return UNKNOWN;
        }

        userAgent = userAgent.toLowerCase();
        if ( userAgent.indexOf( MSIE ) != -1 )
        {
            return MSIE;
        }
        else if ( userAgent.indexOf( OPERA ) != -1 )
        {
            return OPERA;
        }
        else if ( userAgent.indexOf( MOZILLA ) != -1 )
        {
            return MOZILLA;
        }
        else
        {
            return UNKNOWN;
        }
    }

    public final String getClientPlatform()
    {
        String userAgent = getHeader( "user-agent" );
        if ( userAgent == null )
        {
            return UNKNOWN;
        }

        userAgent = userAgent.toLowerCase();
        if ( userAgent.indexOf( "mac" ) != -1 )
        {
            return MAC;
        }
        else if ( userAgent.indexOf( "x11" ) != -1 )
        {
            return UNIX;
        }
        else if ( userAgent.indexOf( "win" ) != -1 )
        {
            return WINDOWS;
        }
        else
        {
            return UNKNOWN;
        }
    }

    public final boolean isRenderRequest()
    {
        return !isActionRequest();
    }

    public final boolean isActionRequest()
    {
        return this.actionRequest;
    }

    public void setActionRequest( boolean actionRequest )
    {
        this.actionRequest = actionRequest;
    }

    public PortletKey getCurrentPortletKey()
    {
        return this.currentPortletKey;
    }

    public void setCurrentPortletKey( PortletKey value )
    {
        this.currentPortletKey = value;
    }

    public final Document getAsXml()
    {
        Element root = new Element( "request" );
        root.addContent( new Element( "type" ).setText( isActionRequest() ? "action" : "render" ) );
        root.addContent( new Element( "method" ).setText( getMethod() ) );
        root.addContent( new Element( "requestUri" ).setText( getRequestUri() ) );
        root.addContent( new Element( "locale" ).setText( getLocale() ) );
        root.addContent( new Element( "remoteAddr" ).setText( getRemoteAddr() ) );
        root.addContent( new Element( "remoteHost" ).setText( getRemoteHost() ) );
        root.addContent( new Element( "profile" ).setText( getProfile() ) );
        root.addContent( new Element( "clientType" ).setText( getClientType() ) );
        root.addContent( new Element( "clientPlatform" ).setText( getClientPlatform() ) );
        root.addContent( createHeadersXml() );
        root.addContent( createParametersXml() );
        return new Document( root );
    }

    private Element createHeadersXml()
    {
        Element root = new Element( "headers" );
        for ( String name : getHeaderNames() )
        {
            root.addContent( new Element( "header" ).setAttribute( "name", name ).setText( getHeader( name ) ) );
        }

        return root;
    }

    private Element createParametersXml()
    {
        Element root = new Element( "parameters" );
        for ( String name : getParameterNames() )
        {
            for ( String value : getParameterValues( name ) )
            {
                root.addContent( new Element( "parameter" ).setAttribute( "name", name ).setText( value ) );
            }
        }

        return root;
    }
}
