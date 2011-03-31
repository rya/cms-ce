/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HashMapUtil
{

    private HashMapUtil()
    {
        // Prohibit construction
    }

    /**
     * Listing of all items in a hashmap (useful for formItems
     * <p/>
     * HashMap map Input to iterate over and print items
     */
    public static void printItems( HashMap map )
    {
        for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); )
        {
            Map.Entry item = (Map.Entry) iter.next();

            Object value = item.getValue();
            if ( value instanceof String[] )
            {
                String[] list = (String[]) value;
                StringBuffer out = new StringBuffer();
                out.append( item.getKey() );
                out.append( " - [" );
                for ( int i = 0; i < list.length; i++ )
                {
                    Object listItem = list[i];
                    if ( listItem == null )
                    {
                        out.append( "NULL" );
                    }
                    else
                    {
                        out.append( listItem );
                    }

                    if ( i < list.length - 1 )
                    {
                        out.append( ", " );
                    }
                }
                out.append( "]" );
                System.out.println( out );
            }
            else
            {
                System.out.println( item.getKey() + " - " + item.getValue() );
            }
        }
    }

}