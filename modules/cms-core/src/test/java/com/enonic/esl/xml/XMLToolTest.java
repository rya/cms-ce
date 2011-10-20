/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.xml;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

public class XMLToolTest
{
    private Element addChild( Element parent, int order )
    {
        Element elem = XMLTool.createElement( parent, "elem" );
        elem.setAttribute( "order", String.valueOf( order ) );
        return elem;
    }

    @Test
    public void testSortChildElements()
        throws Exception
    {
        Document doc = XMLTool.createDocument( "root" );
        Element root = doc.getDocumentElement();

        Element elem1 = addChild( root, 1 );
        Element elem2 = addChild( root, 3 );
        Element elem3 = addChild( elem1, 4 );
        Element elem4 = addChild( elem3, 1 );
        Element elem5 = addChild( elem3, 7 );

        //XMLTool.printDocument(doc);

        XMLTool.sortChildElements( root, "order", true, true );

        //XMLTool.printDocument(doc);

        assertEquals( 0, XMLTool.getElementIndex( elem2 ) );
        assertEquals( 1, XMLTool.getElementIndex( elem1 ) );
        assertEquals( 0, XMLTool.getElementIndex( elem3 ) );
        assertEquals( 0, XMLTool.getElementIndex( elem5 ) );
        assertEquals( 1, XMLTool.getElementIndex( elem4 ) );
    }
}
