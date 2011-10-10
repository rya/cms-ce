/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem.section;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;

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

        if ( intValue != that.intValue() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 823, 263 ).append( intValue ).toHashCode();
    }

    public static SectionContentKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new SectionContentKey( str );
    }

    public static Collection<SectionContentKey> converToList( int[] array )
    {

        if ( ( array == null ) || ( array.length == 0 ) )
        {
            return new ArrayList<SectionContentKey>();
        }

        Collection<SectionContentKey> list = new ArrayList<SectionContentKey>( array.length );
        for ( int value : array )
        {
            list.add( new SectionContentKey( value ) );

        }
        return list;
    }

    public static String convertToCommaSeparatedString( Collection<SectionContentKey> keys )
    {
        StringBuffer s = new StringBuffer();
        for ( Iterator<SectionContentKey> it = keys.iterator(); it.hasNext(); )
        {
            SectionContentKey menuItemKey = it.next();
            s.append( menuItemKey );
            if ( it.hasNext() )
            {
                s.append( "," );
            }
        }
        return s.toString();
    }
}
