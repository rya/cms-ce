/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

public final class FileResourceData
{
    private byte[] bytes;

    public long getSize()
    {
        return this.bytes != null ? this.bytes.length : 0;
    }

    public byte[] getAsBytes()
    {
        return this.bytes;
    }

    public void setAsBytes( byte[] bytes )
    {
        this.bytes = bytes;
    }

    public String getAsString()
    {
        try
        {
            return new String( this.bytes, "UTF-8" );
        }
        catch ( Exception e )
        {
            return new String( this.bytes );
        }
    }

    public void setAsString( String str )
    {
        try
        {
            this.bytes = str.getBytes( "UTF-8" );
        }
        catch ( Exception e )
        {
            this.bytes = str.getBytes();
        }
    }

    public static FileResourceData create( byte[] bytes )
    {
        FileResourceData data = new FileResourceData();
        data.setAsBytes( bytes );
        return data;
    }

    public static FileResourceData create( String str )
    {
        FileResourceData data = new FileResourceData();
        data.setAsString( str );
        return data;
    }
}
