/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;


public class Image
    extends Field
{

    public Image( Element inputElem )
    {
        super( inputElem );
    }

    /*
    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;
        String key = dataElem.getAttribute( "key" );
        String text = XMLTool.getElementText( XMLTool.getElement( dataElem, "text" ) );

        if ( key != null && key.length() > 0 )
        {
            fields.put( name, key );
        }
        else
        {
            fields.put( name, null );
        }

        if ( text != null && text.length() > 0 )
        {
            fields.put( name + "text", text );
        }
    }
    */
}
