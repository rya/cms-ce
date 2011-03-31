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

public class ContentNewsletterXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getContentTitle( ExtendedMap formItems )
    {
        return formItems.getString( "contentdata_subject" );
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "subject" ) );
    }

    @Override
    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {
        super.buildContentTypeXML( user, doc, contentdata, formItems );
    }
}
