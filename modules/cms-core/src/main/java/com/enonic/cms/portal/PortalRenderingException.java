/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal;

/**
 * Apr 28, 2009
 */
public class PortalRenderingException
    extends RuntimeException
{
    public PortalRenderingException( String message )
    {
        super( message );
    }

    public PortalRenderingException( String s, Throwable throwable )
    {
        super( s, throwable );
    }
}
