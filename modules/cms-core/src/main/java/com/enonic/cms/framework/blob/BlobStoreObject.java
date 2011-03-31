/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.blob;

import java.io.Serializable;

public final class BlobStoreObject
    implements Serializable
{
    public final static BlobStoreObject EMPTY = new BlobStoreObject( null );

    private final String id;

    private final byte[] data;

    public BlobStoreObject( byte[] data )
    {
        this( null, data );
    }

    public BlobStoreObject( String id, byte[] data )
    {
        this.data = data != null ? data : new byte[0];
        this.id = id != null ? id : BlobStoreHelper.createKey( this.data ).toString();
    }

    public String getId()
    {
        return this.id;
    }

    public int getSize()
    {
        return this.data.length;
    }

    public byte[] getData()
    {
        return this.data;
    }

    public boolean equals( Object o )
    {
        return ( o instanceof BlobStoreObject ) && equals( (BlobStoreObject) o );
    }

    private boolean equals( BlobStoreObject o )
    {
        return this.id.equals( o.id );
    }
}
