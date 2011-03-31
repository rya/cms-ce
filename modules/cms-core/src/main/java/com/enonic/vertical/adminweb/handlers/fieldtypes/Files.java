/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class Files
    extends Field
{

    public Files( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;

        Element[] fileElems = XMLTool.getElements( dataElem, "file" );
        String[] fileKeys = new String[fileElems.length];

        for ( int i = 0; i < fileElems.length; i++ )
        {
            String key = fileElems[i].getAttribute( "key" );
            if ( key != null && key.length() > 0 )
            {
                fileKeys[i] = key;
            }
        }

        fields.put( name, fileKeys );
    }

}
