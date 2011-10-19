/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class HtmlArea
    extends Field
{
    boolean cData;

    public HtmlArea( Element inputElem )
    {
        super( inputElem );
        cData = "cdata".equals( inputElem.getAttribute( "mode" ) );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        if ( cData )
        {
            super.XMLToMultiValueMap( name, dataNode, fields, groupCounter );
        }
        else
        {
            Element dataElem = (Element) dataNode;
            String value = XMLTool.serialize( dataElem, false );
            fields.put( name, value );
        }
    }

}
