/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

public class PortalConfigurationException
    extends RuntimeException
{

    private String message;

    public PortalConfigurationException( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

}