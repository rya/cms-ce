/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.domain.security.user.User;

public class ContentPersonXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "lastname" );
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "lastname" ) );
    }

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {
        return AdminHandlerBaseServlet.getIntArrayFormItem( formItems, "person_image" );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Name
        XMLTool.createElement( doc, contentdata, "firstname", formItems.getString( "firstname", "" ) );
        XMLTool.createElement( doc, contentdata, "lastname", formItems.getString( "lastname", "" ) );

        XMLTool.createElement( doc, contentdata, "title", formItems.getString( "title", "" ) );
        XMLTool.createElement( doc, contentdata, "telephone", formItems.getString( "telephone", "" ) );
        XMLTool.createElement( doc, contentdata, "mobile", formItems.getString( "mobile", "" ) );
        XMLTool.createElement( doc, contentdata, "fax", formItems.getString( "fax", "" ) );
        XMLTool.createElement( doc, contentdata, "mail", formItems.getString( "mail", "" ) );

        // Description
        Element desc = XMLTool.createElement( doc, contentdata, "description" );
        XMLTool.createCDATASection( doc, desc, formItems.getString( "description", "" ) );

        // Image
        Element personImage = XMLTool.createElement( doc, contentdata, "image" );

        if ( formItems.containsKey( "person_image" ) )
        {
            personImage.setAttribute( "key", formItems.getString( "person_image" ) );
            if ( formItems.containsKey( "person_imagewidth" ) && formItems.containsKey( "person_imageheight" ) )
            {
                XMLTool.createElement( doc, personImage, "width", formItems.getString( "person_imagewidth" ) );
                XMLTool.createElement( doc, personImage, "height", formItems.getString( "person_imageheight" ) );
            }
        }

    }

}
