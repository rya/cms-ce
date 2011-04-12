/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.menuitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.AbstractIntegerBasedKey;

public class MenuItemKey
    extends AbstractIntegerBasedKey
{

    public MenuItemKey( String key )
    {
        init( key );
    }

    public MenuItemKey( int key )
    {
        init( key );
    }

    public MenuItemKey( Integer key )
    {
        init( key );
    }

    @Override
    protected int minAllowedValue()
    {
        return -1;
    }

    @Override
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

        MenuItemKey that = (MenuItemKey) o;

        if ( intValue != that.intValue() )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 653, 213 ).append( intValue ).toHashCode();
    }

    public static MenuItemKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new MenuItemKey( str );
    }

    public static Collection<MenuItemKey> converToList( int[] array )
    {

        if ( ( array == null ) || ( array.length == 0 ) )
        {
            return new ArrayList<MenuItemKey>();
        }

        Collection<MenuItemKey> list = new ArrayList<MenuItemKey>( array.length );
        for ( int value : array )
        {
            list.add( new MenuItemKey( value ) );

        }
        return list;
    }

    public static String convertToCommaSeparatedString( Collection<MenuItemKey> keys )
    {
        StringBuffer s = new StringBuffer();
        for ( Iterator<MenuItemKey> it = keys.iterator(); it.hasNext(); )
        {
            MenuItemKey menuItemKey = it.next();
            s.append( menuItemKey );
            if ( it.hasNext() )
            {
                s.append( "," );
            }
        }
        return s.toString();
    }
}
