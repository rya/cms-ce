/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

/**
 * Feb 6, 2010
 */
public class InvalidImportConfigException
    extends InvalidContentTypeConfigException
{
    public InvalidImportConfigException( String message )
    {
        super( message );
    }

    public InvalidImportConfigException( String name, String message )
    {
        super( buildMessage( name, message ) );
    }

    private static String buildMessage( String name, String message )
    {
        return "Import config '" + name + "' is invalid: " + message;
    }
}
