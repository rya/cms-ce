/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.net.URL;

import com.enonic.cms.domain.Attribute;
import com.enonic.cms.domain.SitePath;
import com.enonic.cms.portal.httpservices.IllegalRedirectException;
import com.enonic.cms.portal.httpservices.UserServicesException;

public class UserServicesRedirectUrlResolver
{

    public String resolveRedirectUrlToPage( HttpServletRequest request, String redirect, MultiValueMap queryParams )
    {
        if ( redirect == null || redirect.length() == 0 )
        {
            String referer = request.getHeader( "referer" );
            if ( referer != null && !referer.equals( "" ) )
            {
                return appendParams( referer, queryParams );
            }
            return appendParams( "/", queryParams );
        }

        if ( redirect.contains( "://" ) )
        {
            return appendParams( redirect, queryParams );
        }

        if ( redirect.startsWith( "/" ) )
        {
            return appendParams( redirect, queryParams );
        }

        throw new IllegalRedirectException( redirect );
    }

    public String resolveRedirectUrlToErrorPage( HttpServletRequest request, ExtendedMap formItems, int[] codes, MultiValueMap queryParams )
    {

        if ( queryParams == null )
        {
            queryParams = new MultiValueMap();
        }

        // Check for a fatal exception
        for ( int code : codes )
        {
            if ( code >= 500 )
            {
                throw new UserServicesException( code );
            }
        }

        SitePath originalSitePath = (SitePath) request.getAttribute( Attribute.ORIGINAL_SITEPATH );
        String handler = UserServicesParameterResolver.resolveHandlerFromSitePath( originalSitePath );
        String operation = UserServicesParameterResolver.resolveOperationFromSitePath( originalSitePath );

        // set error paramater on query string
        StringBuffer errorKeyBuilder = new StringBuffer( "error_" );
        if ( handler != null && operation != null )
        {
            errorKeyBuilder.append( handler );
            errorKeyBuilder.append( '_' );
            errorKeyBuilder.append( operation );
        }
        else
        {
            errorKeyBuilder.append( "userservices" );
        }

        String errorKey = errorKeyBuilder.toString();
        queryParams.remove( errorKey );
        for ( int code : codes )
        {
            queryParams.put( errorKey, String.valueOf( code ) );
        }

        String baseUrlString = request.getHeader( "referer" );
        if ( baseUrlString == null )
        {
            baseUrlString = formItems.getString( "redirecterror", null );
        }
        if ( baseUrlString == null )
        {
            throw new UserServicesException( codes[0] );
        }

        // remove old error-parameters
        URL url = new URL( baseUrlString );
        removeErrorParameters( url );

        // add query parameters to url
        url.addParameters( queryParams );

        return url.toString();
    }

    private void removeErrorParameters( URL url )
    {
        Iterator paramIterator = url.parameterIterator();
        while ( paramIterator.hasNext() )
        {
            URL.Parameter param = (URL.Parameter) paramIterator.next();
            if ( param.getKey().indexOf( "error" ) >= 0 )
            {
                paramIterator.remove();
            }
        }
    }


    private String appendParams( String urlString, MultiValueMap queryParams )
    {
        URL url = new URL( urlString );
        if ( queryParams != null && queryParams.size() > 0 )
        {
            for ( Object key : queryParams.keySet() )
            {
                for ( Object o : ( queryParams.getValueList( key ) ) )
                {
                    url.addParameter( key.toString(), o.toString() );
                }
            }
        }

        return url.toString();
    }
}
