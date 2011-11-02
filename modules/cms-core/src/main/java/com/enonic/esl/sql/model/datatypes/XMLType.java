/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;

public class XMLType
    extends DataType
{
    private static final XMLType type = new XMLType();

    public int getSQLType()
    {
        return Types.BLOB;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        String data = null;
        try
        {
            byte[] bytes = resultSet.getBytes( columnIndex );
            if ( resultSet.wasNull() )
            {
                data = null;
            }
            else
            {
                data = new String( bytes, "UTF-8" );
            }
        }
        catch ( UnsupportedEncodingException uee )
        {
            uee.printStackTrace();
        }

        return data;
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        String data = null;
        try
        {
            byte[] bytes = resultSet.getBytes( columnIndex );
            if ( resultSet.wasNull() )
            {
                data = null;
            }
            else
            {
                data = new String( bytes, "UTF-8" );
            }
        }
        catch ( UnsupportedEncodingException uee )
        {
            uee.printStackTrace();
        }

        return data;
    }

    public Object getDataForXML( Object obj )
    {
        if ( obj == null )
        {
            return null;
        }
        else
        {
            return XMLTool.domparse( (String) obj );
        }
    }

    public String getDataString( Object obj )
    {
        return "null";
    }

    public Object getDataFromXML( Node node )
    {
        if ( node == null )
        {
            return null;
        }
        Document tmpDoc = XMLTool.createDocument();
        tmpDoc.appendChild( tmpDoc.importNode( node, true ) );
        return XMLTool.documentToBytes( tmpDoc, "UTF-8" );
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        byte[] dataBytes = (byte[]) obj;
        preparedStmt.setBytes( columnIndex, dataBytes );
    }

    public String getTypeString()
    {
        return "XML";
    }

    public static DataType getInstance()
    {
        return type;
    }
}
