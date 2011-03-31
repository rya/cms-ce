/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb.handlers.fieldtypes;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalProperties;


public abstract class Field
{
    private String xPath;

    private String name;

    private Map relationMap = null;

    private static final FileUploadBase fileUpload;

    static
    {
        fileUpload = new DiskFileUpload();
        fileUpload.setHeaderEncoding( "UTF-8" );
        fileUpload.setSizeMax( VerticalProperties.getVerticalProperties().getMultiPartRequestMaxSize() );
    }

    public Field( Element inputElem )
    {
        xPath = XMLTool.getElementText( XMLTool.getElement( inputElem, "xpath" ) );
        if ( xPath.startsWith( "contentdata/" ) )
        {
            xPath = xPath.substring( "contentdata/".length() );
        }
        name = inputElem.getAttribute( "name" );
    }

    public void XMLToMultiValueMap( String name, Node dataNode, MultiValueMap fields, int groupCounter )
    {
        String value;
        if ( dataNode instanceof Element )
        {
            value = XMLTool.getElementText( (Element) dataNode );
        }
        else
        {
            value = XMLTool.getNodeText( dataNode );
        }

        fields.put( name, value );
    }

    public void XMLToMultiValueMap( String name, MultiValueMap fields )
    {
        fields.put( name, (Object) null );
    }

    public final String getXPath()
    {
        return xPath;
    }

    public final void setXPath( String xPath )
    {
        this.xPath = xPath;
    }

    public final String getName()
    {
        return name;
    }

    public void setData( Element elem, String data )
    {
        XMLTool.createTextNode( elem.getOwnerDocument(), elem, data );
    }

    public String getPrimaryFormKey()
    {
        return name;
    }

    public String[] getFormKeys()
    {
        return new String[]{name};
    }

    public FileItem createFileItem( String fileName, byte[] data )
    {
        FileItem binary = null;
        try
        {
            binary = fileUpload.getFileItemFactory().createItem( name, null, false, fileName );
            binary.getOutputStream().write( data );
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( ioe );
        }
        return binary;
    }

    public Map getRelationMap()
    {
        return relationMap;
    }

    public void setRelationMap( Map relationMap )
    {
        this.relationMap = relationMap;
    }

}
