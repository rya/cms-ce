/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;

public class ShortXMLType
    extends XMLType
{
    private static final ShortXMLType type = new ShortXMLType();

    public int getSQLType()
    {
        return Types.VARCHAR;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        return resultSet.getString( columnIndex );
    }

    public String getTypeString()
    {
        return "SHORTXML";
    }

    public static DataType getInstance()
    {
        return type;
    }

    public boolean isBlobType()
    {
        return false;
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        String dataString = (String) obj;
        preparedStmt.setString( columnIndex, dataString);
    }

    public Object getDataFromXML( Node node )
    {
        Document tmpDoc = XMLTool.createDocument();
        tmpDoc.appendChild( tmpDoc.importNode( node, true ) );
        return XMLTool.documentToString( tmpDoc );
    }
}