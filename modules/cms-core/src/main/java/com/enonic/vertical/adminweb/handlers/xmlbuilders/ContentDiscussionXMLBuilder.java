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

import com.enonic.cms.domain.security.user.User;

public class ContentDiscussionXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        if ( formItems.containsKey( "parentkey" ) )
        {
            contentdata.setAttribute( "parentkey", formItems.getString( "parentkey" ) );
            contentdata.setAttribute( "top", "false" );
        }
        else
        {
            contentdata.setAttribute( "top", "true" );
        }

        if ( formItems.containsKey( "topkey" ) )
        {
            contentdata.setAttribute( "topkey", formItems.getString( "topkey" ) );
        }

        // Author
        XMLTool.createElement( doc, contentdata, "author", formItems.getString( "author" ) );

        // E-mail
        if ( formItems.containsKey( "email" ) )
        {
            XMLTool.createElement( doc, contentdata, "email", formItems.getString( "email" ) );
        }

        // Title
        XMLTool.createElement( doc, contentdata, "title", formItems.getString( "title" ) );

        // Body
        Element bodyElement = XMLTool.createElement( doc, contentdata, "body" );
        XMLTool.createCDATASection( doc, bodyElement, formItems.getString( "body", "" ) );

    }

}
