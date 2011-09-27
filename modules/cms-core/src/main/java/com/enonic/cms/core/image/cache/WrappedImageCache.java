/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.cache;

import com.enonic.cms.framework.cache.CacheFacade;

public final class WrappedImageCache
    extends AbstractImageCache
{
    private final CacheFacade cache;

    public WrappedImageCache( CacheFacade cache )
    {
        this.cache = cache;
    }

    protected byte[] get( String key )
    {
        return (byte[]) this.cache.get( null, key );
    }

    protected void put( String key, byte[] data )
    {
        this.cache.put( null, key, data );
    }
}
