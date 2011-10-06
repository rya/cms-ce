/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata;

public class InvalidContentDataException
    extends RuntimeException
{
    public InvalidContentDataException( final String message )
    {
        super( message );
    }

    public InvalidContentDataException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
