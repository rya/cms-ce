/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Node;

public class BinaryType
    extends DataType
{
    private static final BinaryType type = new BinaryType();

    public int getSQLType()
    {
        return Types.LONGVARBINARY;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        return resultSet.getBytes( columnIndex );
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        return null;
    }

    public Object getDataForXML( Object obj )
    {
        return null;
    }

    public Object getDataFromXML( Node node )
    {
        // This should never be called
        return null;
    }

    public String getDataString( Object obj )
    {
        return "null";
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
    {
        // This should never be called
    }

    public String getTypeString()
    {
        return "BINARY";
    }

    public static DataType getInstance()
    {
        return type;
    }

}