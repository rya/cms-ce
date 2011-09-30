/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.enonic.esl.xml.XMLTool;

public class CDATAType
    extends DataType
{
    private static final CDATAType type = new CDATAType();

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
        return (String) getData( resultSet, columnIndex );
    }

    public Object getDataForXML( Object obj )
    {
        if ( obj == null )
        {
            return null;
        }
        else
        {
            return obj.toString();
        }
    }

    public String getDataString( Object obj )
    {
        if ( obj == null )
        {
            return "null";
        }
        else
        {
            return obj.toString();
        }
    }

    public Object getDataFromXML( Node node )
    {
        try
        {
            if ( node.getNodeType() == Node.TEXT_NODE )
            {
                return ( (Text) node ).getData().getBytes( "UTF-8" );
            }
            else if ( node.getNodeType() == Node.ELEMENT_NODE )
            {
                return XMLTool.getElementText( (Element) node ).getBytes( "UTF-8" );
            }
            else
            {
                return null;
            }
        }
        catch ( UnsupportedEncodingException uee )
        {
            uee.printStackTrace();
            return null;
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        byte[] dataBytes = (byte[]) obj;
        preparedStmt.setBinaryStream( columnIndex, new ByteArrayInputStream( dataBytes ), dataBytes.length );
    }

    public Class getJavaType()
    {
        return String.class;
    }

    public String getTypeString()
    {
        return "CDATA";
    }

    public boolean isBlobType()
    {
        return true;
    }

    public static DataType getInstance()
    {
        return type;
    }
}