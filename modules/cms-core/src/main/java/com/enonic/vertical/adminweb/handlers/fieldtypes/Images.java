/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;


public class Images
    extends Field
{

    public Images( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;
        Element[] imageElems = XMLTool.getElements( dataElem, "image" );
        String[] imageKeys = new String[imageElems.length + 1];
        String[] imageTexts = new String[imageElems.length + 1];

        for ( int i = 0; i < imageElems.length; i++ )
        {
            String key = imageElems[i].getAttribute( "key" );
            String text = XMLTool.getElementText( XMLTool.getElement( imageElems[i], "text" ) );

            if ( key != null && key.length() > 0 )
            {
                imageKeys[i + 1] = key;
            }
            if ( text != null && text.length() > 0 )
            {
                imageTexts[i + 1] = text;
            }
        }

        fields.put( name, imageKeys );
        fields.put( name + "text", imageTexts );
    }

}
