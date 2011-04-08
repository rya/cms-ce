/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.enonic.cms.business.SiteURLResolver;

import com.enonic.cms.domain.RequestParameters;
import com.enonic.cms.domain.SitePath;

/**
 * Apr 21, 2009
 */
public class QueryStringContextXmlCreator
{
    private SiteURLResolver siteURLResolver;

    public QueryStringContextXmlCreator( SiteURLResolver siteURLResolver )
    {
        this.siteURLResolver = siteURLResolver;
    }

    public Element createQueryStringElement( HttpServletRequest request, SitePath originalSitePath, RequestParameters requestParameters )
    {
        Element queryStringElem = new Element( "querystring" );

        if ( request != null )
        {
            String server = request.getHeader( "host" );
            if ( server == null || "".equals( server ) )
            {
                // Not all browsers provide the host in the header
                server = request.getServerName();
                final int httpPort = 80;
                if ( request.getServerPort() != httpPort )
                {
                    server += ":" + request.getServerPort();
                }
            }

            queryStringElem.setAttribute( "server", server );

            String servletPath = buildServletPathForVerticalContextQueryString( request, originalSitePath );
            queryStringElem.setAttribute( "servletpath", servletPath );

            String url = buildUrlForVerticalContextQueryString( request, originalSitePath );
            queryStringElem.setAttribute( "url", url );
        }

        // Add parameters
        for ( RequestParameters.Param param : requestParameters.getParameters() )
        {
            if ( !param.isEmpty() && !param.getName().startsWith( "VERTICAL" ) && !param.getName().startsWith( "vertical" ) )
            {
                String[] values = param.getValues();

                for ( String value : values )
                {
                    Element parameterEl = new Element( "parameter" ).setText( value );
                    parameterEl.setAttribute( "name", param.getName() );
                    queryStringElem.addContent( parameterEl );
                }
            }
        }

        return queryStringElem;
    }

    private String buildServletPathForVerticalContextQueryString( HttpServletRequest request, SitePath originalSitePath )
    {
        return siteURLResolver.createPathWithinContextPath( request, originalSitePath, false );
    }

    private String buildUrlForVerticalContextQueryString( HttpServletRequest request, SitePath originalSitePath )
    {
        return siteURLResolver.createUrl( request, originalSitePath, true );
    }
}
