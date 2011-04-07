/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.servlet;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;


public class OriginalUrlResolver
{

    private static final Logger LOG = LoggerFactory.getLogger( OriginalUrlResolver.class );

    private UrlPathHelper urlPathHelper;

    private static OriginalUrlResolver instance;

    private static int instances;

    public OriginalUrlResolver()
    {
        instance = this;
        if ( ++instances > 1 )
        {
            String message = this.getClass().getName() + " not allowed to be instantiated more than once";
            LOG.error( message );
            throw new IllegalStateException( message );
        }

        urlPathHelper = new UrlPathHelper();
        urlPathHelper.setUrlDecode( true );
        urlPathHelper.setDefaultEncoding( "UTF-8" );
    }

    public static OriginalUrlResolver get()
    {
        return instance;
    }

    public String resolveOriginalUrl( HttpServletRequest req )
    {

        StringBuffer url = new StringBuffer();
        url.append( req.getScheme() ).append( "://" );
        url.append( req.getServerName() );
        int serverPort = req.getServerPort();
        if ( serverPort != 80 )
        {
            url.append( ":" ).append( serverPort );
        }

        String originatingUri = urlPathHelper.getOriginatingRequestUri( req );
        url.append( originatingUri );

        final String originatingQueryString = urlPathHelper.getOriginatingQueryString( req );
        if ( originatingQueryString != null )
        {
            url.append( "?" ).append( originatingQueryString );
        }

        return url.toString();
    }
}
