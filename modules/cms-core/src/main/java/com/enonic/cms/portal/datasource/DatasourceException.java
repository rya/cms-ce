/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.datasource;

/**
 * Render exception.
 */
public final class DatasourceException
    extends RuntimeException
{
    /**
     * Construct the exception.
     */
    public DatasourceException( String message )
    {
        super( message );
    }

    /**
     * Construct the exception.
     */
    public DatasourceException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
