/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class MultipleChoice
    extends Field
{

    public MultipleChoice( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;
        Element textElem = XMLTool.getElement( dataElem, "text" );
        if ( textElem != null )
        {
            fields.put( name, XMLTool.getElementText( textElem ) );
        }
        Element[] alternativeElems = XMLTool.getElements( dataElem, "alternative" );
        String[] alternativeTexts = new String[alternativeElems.length];
        String[] alternativeValues = new String[alternativeElems.length];
        for ( int i = 0; i < alternativeElems.length; i++ )
        {
            alternativeTexts[i] = XMLTool.getElementText( alternativeElems[i] );
            alternativeValues[i] = alternativeElems[i].getAttribute( "correct" );
        }
        fields.put( name + "_alternative", alternativeTexts );
        fields.put( name + "_checkbox_values", alternativeValues );
    }

}
