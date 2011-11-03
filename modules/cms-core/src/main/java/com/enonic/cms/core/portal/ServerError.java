/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal;

public class ServerError
    extends AbstractBaseError
{

    public ServerError( int statusCode, String message, Throwable cause )
    {
        super( statusCode, message, cause );
    }
}
