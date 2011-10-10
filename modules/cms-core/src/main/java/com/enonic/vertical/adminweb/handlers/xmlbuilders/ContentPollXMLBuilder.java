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

import com.enonic.cms.core.security.user.User;

public class ContentPollXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Title
        Element tempElement = XMLTool.createElement( doc, contentdata, "title", formItems.getString( "title" ) );

        // Body
        tempElement = XMLTool.createElement( doc, contentdata, "description" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "description", "" ) );

        // alternatives
        Element alternativesElem = XMLTool.createElement( doc, contentdata, "alternatives" );
        alternativesElem.setAttribute( "count", formItems.getString( "totalcount", "0" ) );
        if ( AdminHandlerBaseServlet.isArrayFormItem( formItems, "choice" ) )
        {
            String choice[] = (String[]) formItems.get( "choice" );
            String count[] = (String[]) formItems.get( "count" );
            for ( int i = 0; i < choice.length; i++ )
            {
                Element tmpElem = XMLTool.createElement( doc, alternativesElem, "alternative", choice[i] );
                tmpElem.setAttribute( "id", String.valueOf( i ) );
                if ( count[i].length() <= 0 )
                {
                    tmpElem.setAttribute( "count", "0" );
                }
                else
                {
                    tmpElem.setAttribute( "count", count[i] );
                }

            }
        }
        else
        {
            XMLTool.createElement( doc, alternativesElem, "alternative", formItems.getString( "choice" ) ).setAttribute( "count",
                                                                                                                         formItems.getString(
                                                                                                                             "count" ) );
        }

        String mChoice = formItems.getString( "multiplechoice", "" );
        if ( "on".equals( mChoice ) )
        {
            alternativesElem.setAttribute( "multiplechoice", "yes" );
        }
        else
        {
            alternativesElem.setAttribute( "multiplechoice", "no" );
        }
    }

}
