/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.httpservices;

public class UserServicesException
    extends RuntimeException
{

    private String message;

    public UserServicesException( int errorCode )
    {
        this.message = "Error in userservices, error code: " + errorCode;
    }

    public String getMessage()
    {
        return message;
    }

}
