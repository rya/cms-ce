/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.resource;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;


public class ResourceKey
    implements Serializable
{

    private String key;

    public ResourceKey( String key )
    {
        if ( key == null )
        {
            throw new IllegalArgumentException( "Given key cannot be null" );
        }
        if ( key.trim().length() == 0 )
        {
            throw new IllegalArgumentException( "Given key cannot be empty" );
        }

        this.key = key.trim();
    }

    public boolean isPublic()
    {
        return key.startsWith( "/_public" ) || key.startsWith( "_public" );
    }

    public boolean equals( Object o )
    {

        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        ResourceKey that = (ResourceKey) o;

        return key.equals( that.toString() );
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 335, 653 ).append( key ).toHashCode();
    }

    public String toString()
    {
        return key;
    }

    public boolean startsWith( String prefix )
    {
        return key.startsWith( prefix );
    }

    public static ResourceKey parse( String key )
    {
        if ( key == null || key.trim().length() == 0 )
        {
            return null;
        }
        else
        {
            return new ResourceKey( key );
        }
    }
}
