/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.httpservices;

public class IllegalRedirectException
    extends RuntimeException
{
    public IllegalRedirectException( String message )
    {
        super( message );
    }
}
