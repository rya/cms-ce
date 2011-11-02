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

import junit.framework.TestCase;

public abstract class AbstractParameterSerializerTest
    extends TestCase
{
    private final AbstractParameterSerializer serializer;

    public AbstractParameterSerializerTest( AbstractParameterSerializer serializer )
    {
        this.serializer = serializer;
    }

    public void testEmpty()
    {
        assertEquals( "", this.serializer.serialize( "" ) );
    }

    public void testSimple()
    {
        String v1 = this.serializer.serialize( "test" );
        String v2 = this.serializer.deserialize( v1 );

        assertEquals( "test", v2 );
    }

    public void testList()
    {
        List<String> l1 = new ArrayList<String>();
        l1.add( "value1" );
        l1.add( "value2" );

        String v1 = this.serializer.serializeList( l1 );
        List<String> l2 = this.serializer.deserializeList( v1 );

        assertEquals( l1, l2 );
    }

    private void assertEquals( List<String> l1, List<String> l2 )
    {
        assertEquals( l1.size(), l2.size() );
        for ( int i = 0; i < l1.size(); i++ )
        {
            assertEquals( l1.get( i ), l2.get( i ) );
        }
    }

    public void testMap()
    {
        Map<String, String> m1 = new HashMap<String, String>();
        m1.put( "key1", "value1" );
        m1.put( "key2", "value2" );

        String v1 = this.serializer.serializeMap( m1 );
        Map<String, String> m2 = this.serializer.deserializeMap( v1 );

        assertEquals( m1, m2 );
    }

    private void assertEquals( Map<String, String> m1, Map<String, String> m2 )
    {
        assertEquals( m1.size(), m2.size() );
        Iterator<Map.Entry<String, String>> i1 = m1.entrySet().iterator();
        Iterator<Map.Entry<String, String>> i2 = m2.entrySet().iterator();

        while ( i1.hasNext() && i2.hasNext() )
        {
            Map.Entry<String, String> e1 = i1.next();
            Map.Entry<String, String> e2 = i2.next();

            assertEquals( e1.getKey(), e2.getKey() );
            assertEquals( e1.getValue(), e2.getValue() );
        }
    }
}