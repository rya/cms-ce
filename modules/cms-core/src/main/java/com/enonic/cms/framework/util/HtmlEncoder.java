/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;


public class HtmlEncoder
{


    public static String encode( String message )
    {

        if ( message == null )
        {
            return null;
        }

        message = message.replaceAll( "<", "&lt;" );
        message = message.replaceAll( ">", "&gt;" );

        return message;
    }

}
