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

public class ContentProductXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "contentdata_productname" );
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "name" ) );
    }

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {
        return AdminHandlerBaseServlet.getIntArrayFormItems( formItems, new String[]{"contentdata_file", "contentdata_body_image",
            "contentdata_teaser_image"} );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Productname
        Element tempElement = XMLTool.createElement( doc, contentdata, "name", formItems.getString( "contentdata_productname" ) );

        // Productnumber
        XMLTool.createElement( doc, contentdata, "number", formItems.getString( "contentdata_productnumber", "" ) );
        // price
        XMLTool.createElement( doc, contentdata, "price", formItems.getString( "contentdata_price", "" ) );
        // Teaser
        Element teaser = XMLTool.createElement( doc, contentdata, "teaser" );
        tempElement = XMLTool.createElement( doc, teaser, "text" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "contentdata_teaser", "" ) );

        // teaser image
        Element teaserImage = XMLTool.createElement( doc, teaser, "teaser_image" );

        if ( formItems.containsKey( "contentdata_teaser_image" ) )
        {
            tempElement = XMLTool.createElement( doc, teaserImage, "binarydata" );
            tempElement.setAttribute( "key", formItems.getString( "contentdata_teaser_image" ) );
            if ( formItems.containsKey( "contentdata_teaser_imagewidth" ) && formItems.containsKey( "contentdata_teaser_imageheight" ) )
            {
                XMLTool.createElement( doc, teaserImage, "width", formItems.getString( "contentdata_teaser_imagewidth" ) );
                XMLTool.createElement( doc, teaserImage, "height", formItems.getString( "contentdata_teaser_imageheight" ) );
            }
        }

        // Body
        Element body = XMLTool.createElement( doc, contentdata, "body" );
        tempElement = XMLTool.createElement( doc, body, "text" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "contentdata_body", "" ) );

        // body image
        Element bodyImage = XMLTool.createElement( doc, body, "image" );

        if ( formItems.containsKey( "contentdata_body_image" ) )
        {
            tempElement = XMLTool.createElement( doc, bodyImage, "binarydata" );
            tempElement.setAttribute( "key", formItems.getString( "contentdata_body_image" ) );
            if ( formItems.containsKey( "contentdata_body_imagewidth" ) && formItems.containsKey( "contentdata_body_imageheight" ) )
            {
                XMLTool.createElement( doc, bodyImage, "width", formItems.getString( "contentdata_body_imagewidth" ) );
                XMLTool.createElement( doc, bodyImage, "height", formItems.getString( "contentdata_body_imageheight" ) );
            }

            if ( formItems.containsKey( "contentdata_body_imagetext" ) )
            {
                tempElement = XMLTool.createElement( doc, bodyImage, "text", formItems.getString( "contentdata_body_imagetext", "" ) );
            }
        }

        // Instock
        XMLTool.createElement( doc, contentdata, "instock", formItems.getString( "contentdata_instock", "" ) );
        // Expected delivery
        XMLTool.createElement( doc, contentdata, "expecteddelivery", formItems.getString( "contentdata_expecteddelivery", "" ) );

        // Files:
        Element filesElement = XMLTool.createElement( doc, contentdata, "files" );
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "contentdata_file" ) )
        {
            String[] files = (String[]) formItems.get( "contentdata_file" );

            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i] != null && files[i].length() > 0 )
                {
                    Element file = XMLTool.createElement( doc, filesElement, "file" );
                    file.setAttribute( "key", files[i] );
                }
            }
        }
        else
        {
            String filekey = formItems.getString( "contentdata_file", "" );
            if ( filekey != null && filekey.length() > 0 )
            {
                Element file = XMLTool.createElement( doc, filesElement, "file" );
                file.setAttribute( "key", filekey );
            }
        }

    }

}
