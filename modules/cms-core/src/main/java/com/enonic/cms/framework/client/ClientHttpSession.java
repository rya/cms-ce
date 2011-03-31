/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.client;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class ClientHttpSession
    implements HttpSession
{

    public static final String SESSION_COOKIE_NAME = "JSESSION";

    private static int nextId = 1;

    private final String id;

    private final Hashtable attributes = new Hashtable();

    public ClientHttpSession()
    {
        this( null );
    }

    private ClientHttpSession( String id )
    {
        this.id = ( id != null ? id : Integer.toString( nextId++ ) );
    }

    public Object getAttribute( String name )
    {

        return null;
    }

    public Enumeration getAttributeNames()
    {

        return null;
    }

    public long getCreationTime()
    {

        return 0;
    }

    public String getId()
    {

        return id;
    }

    public long getLastAccessedTime()
    {

        return 0;
    }

    public int getMaxInactiveInterval()
    {

        return 0;
    }

    public ServletContext getServletContext()
    {

        return null;
    }

    public HttpSessionContext getSessionContext()
    {

        return null;
    }

    public Object getValue( String name )
    {

        return null;
    }

    public String[] getValueNames()
    {

        return null;
    }

    public void invalidate()
    {

    }

    public boolean isNew()
    {

        return false;
    }

    public void putValue( String name, Object value )
    {

    }

    public void removeAttribute( String name )
    {
        this.attributes.remove( name );
    }

    public void removeValue( String name )
    {

    }

    public void setAttribute( String name, Object value )
    {
        if ( value != null )
        {
            this.attributes.put( name, value );
        }
        else
        {
            removeAttribute( name );
        }
    }

    public void setMaxInactiveInterval( int interval )
    {

    }

}
