/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.imports;


public class InvalidImportDataException
    extends RuntimeException
{
    public InvalidImportDataException( String message )
    {
        super( message );
    }
}
