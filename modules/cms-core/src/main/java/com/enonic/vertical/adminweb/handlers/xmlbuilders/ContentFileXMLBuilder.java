/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.xmlbuilders;

import java.util.StringTokenizer;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.AdminHandlerBaseServlet;
import com.enonic.vertical.adminweb.VerticalAdminException;

import com.enonic.cms.core.content.binary.BinaryData;
import com.enonic.cms.core.security.user.User;

public class ContentFileXMLBuilder
    extends ContentBaseXMLBuilder
    implements ContentXMLBuilder
{

    public String getTitleFormKey()
    {
        return "name";
    }

    public String getContentTitle( Element contentDataElem, int contentTypeKey )
    {
        return XMLTool.getElementText( XMLTool.getElement( contentDataElem, "name" ) );
    }

    public void buildContentTypeXML( User user, Document doc, Element contentdata, ExtendedMap formItems )
        throws VerticalAdminException
    {

        // Name
        Element tempElement = XMLTool.createElement( doc, contentdata, "name", formItems.getString( "name" ) );

        // Description
        tempElement = XMLTool.createElement( doc, contentdata, "description" );
        XMLTool.createCDATASection( doc, tempElement, formItems.getString( "description", "" ) );

        // Keywords
        Element keywords = XMLTool.createElement( doc, contentdata, "keywords" );
        if ( formItems.containsKey( "keywords" ) )
        {
            StringTokenizer stringTok = new StringTokenizer( formItems.getString( "keywords" ), " " );
            while ( stringTok.hasMoreElements() )
            {
                tempElement = XMLTool.createElement( doc, keywords, "keyword", (String) stringTok.nextElement() );
            }
        }

        // File size
        int fileSize = formItems.getInt( "filesize" );
        XMLTool.createElement( doc, contentdata, "filesize", String.valueOf( fileSize ) );

        tempElement = XMLTool.createElement( doc, contentdata, "binarydata" );
        tempElement.setAttribute( "key", (String) formItems.get( "binarydatakey" ) );
    }

    public int[] getDeleteBinaries( final ExtendedMap formItems )
        throws VerticalAdminException
    {
        final FileItem newfile = formItems.getFileItem( "newfile", null );
        if ( newfile != null )
        {
            final int versionKey = formItems.getInt( "versionkey" );
            return admin.getBinaryDataKeysByVersion( versionKey );
        }
        return null;
    }

    public BinaryData[] getBinaries( ExtendedMap formItems )
        throws VerticalAdminException
    {
        BinaryData[] binaryData = null;

        FileItem newfile = formItems.getFileItem( "newfile", null );
        if ( newfile != null )
        {
            binaryData = new BinaryData[1];
            binaryData[0] = AdminHandlerBaseServlet.createBinaryData( newfile );
            if ( formItems.containsKey( "oldbinarydatakey" ) )
            {
                binaryData[0].key = formItems.getInt( "oldbinarydatakey" );
            }
            binaryData[0].label = "source";

            // Add the binarydata keys to the form data
            formItems.put( "binarydatakey", "%0" );
            formItems.put( "newbinarydata", "true" );
            formItems.put( "filesize", binaryData[0].data.length );
        }
        else
        {
            // File was not updated
            binaryData = new BinaryData[0];
            if ( formItems.containsKey( "oldbinarydatakey" ) )
            {
                int oldKey = formItems.getInt( "oldbinarydatakey" );
                formItems.put( "binarydatakey", String.valueOf( oldKey ) );
            }
            formItems.put( "filesize", formItems.getInt( "oldfilesize", 0 ) );
        }
        return binaryData;
    }

}
