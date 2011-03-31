/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob;

public final class BlobStoreException
    extends RuntimeException
{
    public BlobStoreException( final String message )
    {
        super( message );
    }

    public BlobStoreException( final String message, final Throwable cause )
    {
        super( message, cause );
    }
}
