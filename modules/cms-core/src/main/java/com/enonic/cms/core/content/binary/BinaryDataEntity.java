/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.binary;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class BinaryDataEntity
    implements Serializable
{
    private BinaryDataKey key;

    private String name;

    private int size;

    private Date createdAt;

    private String blobKey;

    public int getKey()
    {
        return key.toInt();
    }

    public BinaryDataKey getBinaryDataKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public int getSize()
    {
        return size;
    }

    public Date getCreatedAt()
    {
        return createdAt;
    }

    public void setKey( int value )
    {
        this.key = new BinaryDataKey( value );
    }

    public void setBinaryDataKey( BinaryDataKey value )
    {
        this.key = value;
    }

    public void setName( String value )
    {
        this.name = value;
    }

    public void setSize( int value )
    {
        this.size = value;
    }

    public void setCreatedAt( Date value )
    {
        this.createdAt = value;
    }

    public String getBlobKey()
    {
        return blobKey;
    }

    public void setBlobKey( String blobKey )
    {
        this.blobKey = blobKey;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof BinaryDataEntity ) )
        {
            return false;
        }

        BinaryDataEntity that = (BinaryDataEntity) o;

        if ( getBinaryDataKey() != null ? !getBinaryDataKey().equals( that.getBinaryDataKey() ) : that.getBinaryDataKey() != null )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 471, 325 ).append( key ).toHashCode();
    }
}
