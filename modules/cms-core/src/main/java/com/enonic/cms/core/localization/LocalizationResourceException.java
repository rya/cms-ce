/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.localization;

/**
 * Created by rmy - Date: Apr 24, 2009
 */
public class LocalizationResourceException
    extends RuntimeException
{

    public LocalizationResourceException( String message, Throwable t )
    {

        super( message, t );
    }

    public LocalizationResourceException( String message )
    {
        super( message );
    }

}
