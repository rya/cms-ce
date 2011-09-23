/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

/**
 * Created by rmy - Date: May 4, 2009
 */
public class LocaleParsingException
    extends RuntimeException
{

    public LocaleParsingException( String message, Throwable t )
    {
        super( message, t );
    }

    public LocaleParsingException( String message )
    {
        super( message );

    }
}
