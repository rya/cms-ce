/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.service;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DataSourceServiceCompabilityKeeper
{
    public static void fixCategoriesCompability( Node root )
    {
        if ( root instanceof Element )
        {
            fixCategoriesCompabilityElement( (Element) root );
        }

        NodeList list = root.getChildNodes();
        for ( int i = 0; i < list.getLength(); i++ )
        {
            fixCategoriesCompability( list.item( i ) );
        }
    }

    private static void fixCategoriesCompabilityElement( Element root )
    {
        if ( root.getNodeName().equals( "category" ) )
        {
            NodeList list = root.getElementsByTagName( "title" );
            Element title = (Element) list.item( 0 );

            String name = title.getChildNodes().item( 0 ).getNodeValue();
            root.setAttribute( "name", name );

            root.removeChild( title );

            String superKey = root.getAttribute( "superkey" );
            root.removeAttribute( "superkey" );

            if ( ( superKey != null ) && ( superKey.length() > 0 ) )
            {
                root.setAttribute( "supercategorykey", superKey );
            }
        }
    }
}
