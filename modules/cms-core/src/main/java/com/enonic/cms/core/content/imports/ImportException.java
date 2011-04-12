/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

public class ImportException
    extends RuntimeException
{
    public ImportException( String message )
    {
        super( message );
    }

    public ImportException( Throwable cause )
    {
        super( cause.getMessage(), cause );
    }

    public ImportException( String message, Throwable cause )
    {
        super( message, cause );
    }
}