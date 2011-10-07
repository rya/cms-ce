/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.structure.page.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.enonic.cms.domain.InvalidKeyException;

/**
 *
 */
public class PageTemplateKey
    implements Serializable
{
    private int intValue;

    private String stringValue;


    public PageTemplateKey( String key )
        throws InvalidKeyException
    {
        try
        {
            init( Integer.parseInt( key ) );
        }
        catch ( NumberFormatException e )
        {
            throw new InvalidKeyException( key, this.getClass() );
        }
    }

    public PageTemplateKey( int key )
    {
        init( key );
    }

    private void init( int value )
    {
        this.intValue = value;
        this.stringValue = String.valueOf( value );
    }

    public int toInt()
    {
        return intValue;
    }


    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PageTemplateKey ) )
        {
            return false;
        }

        PageTemplateKey that = (PageTemplateKey) o;

        if ( intValue != that.intValue )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return new HashCodeBuilder( 95, 187 ).append( intValue ).toHashCode();
    }

    public String toString()
    {
        return stringValue;
    }

    public static PageTemplateKey parse( String str )
    {

        if ( str == null )
        {
            return null;
        }

        return new PageTemplateKey( str );
    }

    public static Collection<PageTemplateKey> converToList( int[] array )
    {

        if ( ( array == null ) || ( array.length == 0 ) )
        {
            return new ArrayList<PageTemplateKey>();
        }

        Collection<PageTemplateKey> list = new ArrayList<PageTemplateKey>( array.length );
        for ( int value : array )
        {
            list.add( new PageTemplateKey( value ) );

        }
        return list;
    }

    public static String convertToCommaSeparatedString( Collection<PageTemplateKey> keys )
    {
        StringBuffer s = new StringBuffer();
        for ( Iterator<PageTemplateKey> it = keys.iterator(); it.hasNext(); )
        {
            PageTemplateKey pageTemplateKey = it.next();
            s.append( pageTemplateKey );
            if ( it.hasNext() )
            {
                s.append( "," );
            }
        }
        return s.toString();
    }
}
