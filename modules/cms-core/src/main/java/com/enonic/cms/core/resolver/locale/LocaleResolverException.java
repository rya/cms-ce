/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resolver.locale;

/**
 * Created by rmy - Date: Apr 28, 2009
 */
public class LocaleResolverException
    extends RuntimeException
{
    public LocaleResolverException( String message, Throwable t )
    {
        super( message, t );
    }

    public LocaleResolverException( String message )
    {
        super( message );

    }


}
