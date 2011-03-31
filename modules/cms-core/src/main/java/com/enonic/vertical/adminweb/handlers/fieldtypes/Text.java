/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class Text
    extends Field
{
    String defaultValue = null;

    public Text( Element inputElem )
    {
        super( inputElem );

        Element defaultElem = XMLTool.getElement( inputElem, "default" );
        if ( defaultElem != null )
        {
            defaultValue = XMLTool.getElementText( defaultElem );
        }
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        String value;
        if ( dataNode instanceof Element )
        {
            value = XMLTool.getElementText( (Element) dataNode );
        }
        else
        {
            value = XMLTool.getNodeText( dataNode );
        }

        if ( defaultValue != null && defaultValue.length() > 0 && ( value == null || value.length() == 0 ) )
        {
            fields.put( name, defaultValue );
        }
        else
        {
            fields.put( name, value );
        }
    }

}
