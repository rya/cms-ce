/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.core.security.user.User;

public class ContentCatalogXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Name
        Element tempElement = XMLTool.createElement( doc, contentdata, "title", formItems.getString( "title", "" ) );

        // URL
        tempElement = XMLTool.createElement( doc, contentdata, "url", formItems.getString( "url", "" ) );

        // Description
        tempElement = XMLTool.createElement( doc, contentdata, "description" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "description", "" ) );

        // Country
        tempElement = XMLTool.createElement( doc, contentdata, "country", formItems.getString( "country", "" ) );

        // Keywords
        Element keywords = XMLTool.createElement( doc, contentdata, "keywords" );
        if ( formItems.containsKey( "contentdata_keywords" ) )
        {
            StringTokenizer stringTok = new StringTokenizer( (String) formItems.get( "contentdata_keywords" ), " " );
            while ( stringTok.hasMoreElements() )
            {
                tempElement = XMLTool.createElement( doc, keywords, "keyword", (String) stringTok.nextElement() );
            }
        }

        // Author
        Element author = XMLTool.createElement( doc, contentdata, "author" );
        tempElement = XMLTool.createElement( doc, author, "name", formItems.getString( "author_name", "" ) );
        tempElement = XMLTool.createElement( doc, author, "phone", formItems.getString( "author_phone", "" ) );
        tempElement = XMLTool.createElement( doc, author, "email", formItems.getString( "author_email", "" ) );
        tempElement = XMLTool.createElement( doc, author, "org", formItems.getString( "author_org", "" ) );

    }

}
