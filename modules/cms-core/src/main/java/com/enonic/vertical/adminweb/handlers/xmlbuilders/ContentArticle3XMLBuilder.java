/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.util.StringTokenizer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;

import com.enonic.cms.core.security.user.User;

public class ContentArticle3XMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "contentdata_heading" );
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "heading" ) );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
    {

        // Heading
        Element tempElement = XMLTool.createElement( doc, contentdata, "heading", formItems.getString( "contentdata_heading" ) );

        // Teaser
        Element teaser = XMLTool.createElement( doc, contentdata, "teaser" );

        tempElement = XMLTool.createElement( doc, teaser, "text1" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "contentdata_teaser1", "" ) );

        tempElement = XMLTool.createElement( doc, teaser, "text2" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "contentdata_teaser2", "" ) );

        Element teaserImage = XMLTool.createElement( doc, teaser, "image" );

        if ( formItems.containsKey( "contentdata_teaser_image" ) )
        {
            teaserImage.setAttribute( "key", formItems.getString( "contentdata_teaser_image" ) );
        }

        // Body
        Element body = XMLTool.createElement( doc, contentdata, "body" );
        tempElement = XMLTool.createElement( doc, body, "text" );

        String docText = formItems.getString( "contentdata_body" );
        docText = StringUtil.replaceECC( docText );
        XMLTool.createCDATASection( doc, tempElement, docText );

        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "contentdata_body_image" ) )
        {
            String[] images = (String[]) formItems.get( "contentdata_body_image" );
            String[] text = (String[]) formItems.get( "contentdata_body_imagetext" );

            for ( int i = 1; i < images.length; i++ )
            {
                if ( images[i] == null || images[i].length() == 0 )
                {
                    continue;
                }

                Element bodyImage = XMLTool.createElement( doc, body, "image" );
                bodyImage.setAttribute( "key", images[i] );

                tempElement = XMLTool.createElement( doc, bodyImage, "text", text[i] );
            }
        }

        // Keywords
        Element keywords = XMLTool.createElement( doc, contentdata, "keywords" );
        if ( formItems.containsKey( "contentdata_keywords" ) )
        {
            StringTokenizer stringTok = new StringTokenizer( formItems.getString( "contentdata_keywords" ), " " );
            while ( stringTok.hasMoreElements() )
            {
                tempElement = XMLTool.createElement( doc, keywords, "keyword", (String) stringTok.nextElement() );
            }
        }

        // Authors
        Element authors = XMLTool.createElement( doc, contentdata, "authors" );
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "contentdata_author" ) )
        {
            String[] authorsArray = (String[]) formItems.get( "contentdata_author" );
            for ( int i = 0; i < authorsArray.length; i++ )
            {
                if ( authorsArray[i] != null && authorsArray[i].trim().length() != 0 )
                {
                    tempElement = XMLTool.createElement( doc, authors, "author", authorsArray[i] );
                }
            }
        }
        else
        {
            if ( formItems.containsKey( "contentdata_author" ) )
            {
                tempElement = XMLTool.createElement( doc, authors, "author", formItems.getString( "contentdata_author" ) );
            }
        }

        // Relatedlinks
        Element relatedlinks = XMLTool.createElement( doc, contentdata, "relatedlinks" );
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "contentdata_relatedlinks_url" ) )
        {
            String[] relatedURL = (String[]) formItems.get( "contentdata_relatedlinks_url" );
            String[] relatedDescription = (String[]) formItems.get( "contentdata_relatedlinks_description" );
            for ( int i = 0; i < relatedURL.length; i++ )
            {
                if ( relatedURL[i] != null && relatedURL[i].trim().length() != 0 )
                {
                    Element realtedlink = XMLTool.createElement( doc, relatedlinks, "relatedlink" );
                    tempElement = XMLTool.createElement( doc, realtedlink, "url", relatedURL[i] );
                    tempElement = XMLTool.createElement( doc, realtedlink, "description", relatedDescription[i] );
                }
            }
        }
        else
        {
            if ( formItems.containsKey( "contentdata_relatedlinks_url" ) )
            {
                Element realtedlink = XMLTool.createElement( doc, relatedlinks, "relatedlink" );
                tempElement = XMLTool.createElement( doc, realtedlink, "url", formItems.getString( "contentdata_relatedlinks_url" ) );
                tempElement = XMLTool.createElement( doc, realtedlink, "description",
                                                     formItems.getString( "contentdata_relatedlinks_description", "" ) );
            }
        }

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
            String filekey = formItems.getString( "contentdata_file", null );
            if ( filekey != null && filekey.length() > 0 )
            {
                Element file = XMLTool.createElement( doc, filesElement, "file" );
                file.setAttribute( "key", filekey );
            }
        }
    }

    public int[] getRelatedContentKeys( ExtendedMap formItems )
    {
        return AdminHandlerBaseServlet.getIntArrayFormItems( formItems, new String[]{"contentdata_file", "contentdata_body_image",
            "contentdata_teaser_image"} );
    }

}
