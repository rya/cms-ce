/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.util.Base64Util;
import com.enonic.esl.xml.XMLTool;


public class UploadFile
    extends Field
{

    public UploadFile( Element inputElem )
    {
        super( inputElem );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        Element dataElem = (Element) dataNode;
        Element binaryDataElem = XMLTool.getElement( dataElem, "binarydata" );
        if ( binaryDataElem != null )
        {
            String key = binaryDataElem.getAttribute( "key" );
            if ( key != null && key.length() > 0 )
            {
                fields.put( name, key );
            }
            else
            {
                fields.put( name, null );
            }
        }
        else
        {
            String dataStr = XMLTool.getElementText( dataElem );
            if ( dataStr != null && dataStr.length() > 0 )
            {
                String fileName = dataElem.getAttribute( "name" );
                if ( fileName == null || fileName.length() == 0 )
                {
                    fileName = "uploadfile";
                }

                byte[] data = Base64Util.decode( dataStr );
                FileItem binary = createFileItem( fileName, data );
                fields.put( "f_" + name, binary );
                fields.put( "filename_" + name, fileName );
            }
        }
    }

}
