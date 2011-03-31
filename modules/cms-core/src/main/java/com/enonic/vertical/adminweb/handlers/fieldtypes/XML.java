/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class XML
    extends Field
{

    public XML( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;

        if ( dataElem.hasChildNodes() )
        {
            String value = XMLTool.serialize( dataElem, true, "UTF-8" );
            fields.put( name, value );
        }
        else
        {
            fields.put( name, null );
        }
    }

}
