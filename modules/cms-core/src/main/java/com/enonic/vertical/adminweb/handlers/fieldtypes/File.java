/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;


public class File
    extends Field
{

    public File( Element inputElem )
    {
        super( inputElem );
    }

    /*
    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;

        Element fileElem = XMLTool.getElement( dataElem, "file" );
        if ( fileElem != null )
        {
            String key = fileElem.getAttribute( "key" );
            if ( key != null && key.length() > 0 )
            {
                fields.put( name, key );
            }
            else
            {
                fields.put( name, null );
            }
        }
    }
    */
}
