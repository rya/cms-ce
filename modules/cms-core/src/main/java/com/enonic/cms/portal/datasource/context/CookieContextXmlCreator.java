/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource.context;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.enonic.esl.net.URLUtil;

/**
 * Apr 21, 2009
 */
public class CookieContextXmlCreator
{
    public Element createCookieElement( HttpServletRequest request )
    {
        Element cookiesElem = new Element( "cookies" );

        if ( request == null )
        {
            return cookiesElem;
        }

        Cookie[] cookies = request.getCookies();
        if ( cookies != null )
        {
            for ( Cookie cookie : cookies )
            {
                Element cookieElem = new Element( "cookie" );
                cookieElem.setText( URLUtil.decode( cookie.getValue() ) );
                cookieElem.setAttribute( "name", URLUtil.decode( cookie.getName() ) );
                cookiesElem.addContent( cookieElem );
            }
        }

        return cookiesElem;
    }
}
