/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.core.security.user.User;

public class ContentDocumentXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "contentdata_title" );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Title
        Element tempElement = XMLTool.createElement( doc, contentdata, "title", formItems.getString( "contentdata_title" ) );

        // Teaser
        Element teaser = XMLTool.createElement( doc, contentdata, "teaser" );

        tempElement = XMLTool.createElement( doc, teaser, "text" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "contentdata_teaser", "" ) );

        // Body
        Element body = XMLTool.createElement( doc, contentdata, "body" );
        tempElement = XMLTool.createElement( doc, body, "text" );

        XMLTool.createCDATASection( doc, tempElement, (String) formItems.get( "contentdata_body" ) );
    }

}
