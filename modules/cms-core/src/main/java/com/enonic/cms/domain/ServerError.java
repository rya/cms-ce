/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

public class ServerError
    extends AbstractBaseError
{

    public ServerError( int statusCode, String message, Throwable cause )
    {
        super( statusCode, message, cause );
    }

    public ServerError( int statusCode, String message )
    {
        super( statusCode, message );
    }
}
