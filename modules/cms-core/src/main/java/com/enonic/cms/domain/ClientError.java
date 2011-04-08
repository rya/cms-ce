/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

public class ClientError
    extends AbstractBaseError
{

    public ClientError( int statusCode, String message, Throwable cause )
    {
        super( statusCode, message, cause );
    }
}
