/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.image.param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractParameterSerializer
    implements ParameterSerializer
{
    private final static String SEPARATOR = "/";

    private final static String SEPARATOR_ENC = "&#47;";

    public final String serializeMap( Map<String, String> map )
    {
        ArrayList<String> list = new ArrayList<String>();
        for ( Map.Entry<String, String> entry : map.entrySet() )
        {
            list.add( entry.getKey() );
            list.add( entry.getValue() );
        }

        return serializeList( list );
    }

    public final Map<String, String> deserializeMap( String value )
    {
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<String> list = deserializeList( value ).iterator();

        while ( list.hasNext() )
        {
            String key = list.next();
            if ( list.hasNext() )
            {
                map.put( key, list.next() );
            }
            else
            {
                map.put( key, "" );
            }
        }

        return map;
    }

    public final String serializeList( List<String> value )
    {
        StringBuffer str = new StringBuffer();
        for ( int i = 0; i < value.size(); i++ )
        {
            if ( i > 0 )
            {
                str.append( SEPARATOR );
            }

            str.append( encodeValue( value.get( i ) ) );
        }

        return serialize( str.toString() );
    }

    public final List<String> deserializeList( String value )
    {
        value = deserialize( value );
        ArrayList<String> list = new ArrayList<String>();
        for ( String current : value.split( SEPARATOR ) )
        {
            list.add( decodeValue( current ) );
        }

        return list;
    }

    private String encodeValue( String value )
    {
        return value.replaceAll( SEPARATOR, SEPARATOR_ENC );
    }

    private String decodeValue( String value )
    {
        return value.replaceAll( SEPARATOR_ENC, SEPARATOR );
    }
}
