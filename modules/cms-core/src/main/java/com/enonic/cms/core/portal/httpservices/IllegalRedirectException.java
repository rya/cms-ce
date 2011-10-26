/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.portal.httpservices;

public class IllegalRedirectException
    extends RuntimeException
{
    public IllegalRedirectException( String message )
    {
        super( message );
    }
}
