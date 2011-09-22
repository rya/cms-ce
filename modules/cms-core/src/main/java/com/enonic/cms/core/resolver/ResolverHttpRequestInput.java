/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by rmy - Date: Apr 15, 2009
 */
public class ResolverHttpRequestInput
{

    private String userAgent;

    private String referrer;

    private String ip;

    private String uri;

    private String protocol;

    private String virtualHost;

    private Integer port;

    private String resourcePath;

    private Map<String, String> parameters = new LinkedHashMap<String, String>();

    private Map<String, String> acceptLanguages = new LinkedHashMap<String, String>();

    private Map<String, String> cookies = new LinkedHashMap<String, String>();

    private Map<String, String> httpHeaders = new LinkedHashMap<String, String>();


    public void addAcceptLanguage( String acceptLanguage, String quality )
    {
        acceptLanguages.put( acceptLanguage, quality );
    }

    public void addCookie( String cookieName, String cookieValue )
    {
        cookies.put( cookieName, cookieValue );
    }

    public void addHttpHeader( String headerName, String headerValue )
    {
        httpHeaders.put( headerName, headerValue );
    }

    public void addParameter( String parameterName, String parameterValue )
    {
        parameters.put( parameterName, parameterValue );
    }

    public String getUserAgent()
    {
        return userAgent;
    }

    public void setUserAgent( String userAgent )
    {
        this.userAgent = userAgent;
    }

    public String getReferrer()
    {
        return referrer;
    }

    public void setReferrer( String referrer )
    {
        this.referrer = referrer;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp( String ip )
    {
        this.ip = ip;
    }

    public String getUri()
    {
        return uri;
    }

    public void setUri( String uri )
    {
        this.uri = uri;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol( String protocol )
    {
        this.protocol = protocol;
    }

    public String getVirtualHost()
    {
        return virtualHost;
    }

    public void setVirtualHost( String virtualHost )
    {
        this.virtualHost = virtualHost;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort( Integer port )
    {
        this.port = port;
    }

    public String getResourcePath()
    {
        return resourcePath;
    }

    public void setResourcePath( String resourcePath )
    {
        this.resourcePath = resourcePath;
    }

    public Map<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters( Map<String, String> parameters )
    {
        this.parameters = parameters;
    }

    public Map<String, String> getAcceptLanguages()
    {
        return acceptLanguages;
    }

    public Map<String, String> getCookies()
    {
        return cookies;
    }

    public Map<String, String> getHttpHeaders()
    {
        return httpHeaders;
    }

    public void setAcceptLanguages( Map<String, String> acceptLanguages )
    {
        this.acceptLanguages = acceptLanguages;
    }

    public void setCookies( Map<String, String> cookies )
    {
        this.cookies = cookies;
    }

    public void setHttpHeaders( Map<String, String> httpHeaders )
    {
        this.httpHeaders = httpHeaders;
    }
}
