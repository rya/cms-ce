/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import java.io.Serializable;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.core.AbstractIntegerBasedKey;

public class SectionContentKey
    extends AbstractIntegerBasedKey
    implements Serializable
{

    public SectionContentKey( String key )
    {
        init( key );
    }

    public SectionContentKey( int key )
    {
        init( key );
    }

    public SectionContentKey( Integer key )
    {
        init( key );
    }

    @Override
    protected int minAllowedValue()
    {
        return -1;
    }

    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof SectionContentKey ) )
        {
            return false;
        }

        SectionContentKey that = (SectionContentKey) o;

        if ( intValue() != that.intValue() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 823, 263 ).append( intValue() ).toHashCode();
    }

    public static SectionContentKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new SectionContentKey( str );
    }
}
