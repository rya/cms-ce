/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.security.userstore.config;

import org.jdom.Element;

import com.enonic.cms.framework.util.JDOMUtil;

public class InvalidUserStoreConfigException
    extends RuntimeException
{
    public InvalidUserStoreConfigException( final String message )
    {
        super( message );
    }


    public InvalidUserStoreConfigException( final String message, final Element el )
    {
        super( message + ".\r\nConfig XML:\r\n" + parseElement( el ) );
    }

    private static String parseElement( final Element el )
    {
        if ( el == null )
        {
            return "";
        }
        return JDOMUtil.printElement( el );
    }
}