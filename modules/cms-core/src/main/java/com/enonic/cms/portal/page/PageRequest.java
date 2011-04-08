/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.page;

import java.util.Map;

import org.jdom.Document;

import com.enonic.cms.domain.structure.portlet.PortletKey;

/**
 * This interface defines the page request.
 */
public interface PageRequest
{
    /**
     * Return the method.
     */
    public String getMethod();

    /**
     * Return the session id.
     */
    public String getSessionId();

    /**
     * Return the locale.
     */
    public String getLocale();

    /**
     * Return the request URI.
     */
    public String getRequestUri();

    /**
     * Return the remote host.
     */
    public String getRemoteHost();

    /**
     * Return the remote address.
     */
    public String getRemoteAddr();

    /**
     * Return the profile.
     */
    public String getProfile();

    /**
     * Return the client type.
     */
    public String getClientType();

    /**
     * Return the client platform.
     */
    public String getClientPlatform();

    /**
     * Return true if render request.
     */
    public boolean isRenderRequest();

    /**
     * Return true if action request.
     */
    public boolean isActionRequest();

    /**
     * Set true if this is an action request.
     */
    public void setActionRequest( boolean actionRequest );

    /**
     * Return the header names.
     */
    public String[] getHeaderNames();

    /**
     * Return the header value.
     */
    public String getHeader( String name );

    /**
     * Return the parameter names.
     */
    public String[] getParameterNames();

    /**
     * Return the parameter value.
     */
    public String getParameter( String name );

    /**
     * Return the parameter value.
     */
    public String getParameter( String name, String def );

    /**
     * Return the parameter values.
     */
    public String[] getParameterValues( String name );

    /**
     * Return the header map.
     */
    public Map<String, String> getHeaderMap();

    /**
     * Return the parameter map.
     */
    public Map<String, String[]> getParameterMap();

    /**
     * Return the current object key.
     */
    public PortletKey getCurrentPortletKey();

    /**
     * Set the current object key.
     */
    public void setCurrentPortletKey( PortletKey portletKey );

    /**
     * Return as xml.
     */
    public Document getAsXml();

}
