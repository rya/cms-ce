/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;

public class ContentLeadsXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "contentdata_name" );
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "subject" ) );
    }

}
