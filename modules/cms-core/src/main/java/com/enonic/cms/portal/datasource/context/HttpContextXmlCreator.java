/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.enonic.esl.util.StringUtil;

/**
 * Apr 21, 2009
 */
public class HttpContextXmlCreator
{
    public Element createHttpElement( HttpServletRequest request )
    {
        Element httpEl = new Element( "http" );

        if ( request != null )
        {
            httpEl.setAttribute( "action", request.getMethod() );
            httpEl.addContent( new Element( "user-agent" ).setText( request.getHeader( "user-agent" ) ) );
            httpEl.addContent( new Element( "client-ip" ).setText( request.getRemoteAddr() ) );
            httpEl.addContent( new Element( "referer" ).setText( request.getHeader( "referer" ) ) );

            // accept
            Element acceptElem = new Element( "accept" );
            httpEl.addContent( acceptElem );

            // language
            String acceptLanguage = request.getHeader( "accept-language" );
            if ( acceptLanguage != null )
            {
                String[] languages = StringUtil.splitString( acceptLanguage, "," );
                for ( String languageStr : languages )
                {
                    if ( languageStr.indexOf( ";" ) > 0 )
                    {
                        Element langElem = new Element( "language" );
                        langElem.setText( languageStr.substring( 0, languageStr.indexOf( ";" ) ) );
                        langElem.setAttribute( "q", languageStr.substring( languageStr.indexOf( ";" ) + 3 ) );
                        acceptElem.addContent( langElem );
                    }
                    else
                    {
                        acceptElem.addContent( new Element( "language" ).setText( languageStr ) );
                    }
                }
            }
        }
        return httpEl;
    }
}
