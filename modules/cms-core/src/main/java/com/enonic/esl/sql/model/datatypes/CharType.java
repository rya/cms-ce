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

import com.enonic.esl.xml.XMLTool;

public class CharType
    extends DataType
{
    private static final CharType type = new CharType();

    public int getSQLType()
    {
        return Types.CHAR;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        String string = resultSet.getString( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return string;
        }
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
        String text = XMLTool.getNodeText( node );

        if ( text == null || text.length() == 0 )
        {
            return null;
        }
        else
        {
            return text;
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        String value = (String) obj;
        preparedStmt.setString( columnIndex, value );
    }

    public String getTypeString()
    {
        return "CHAR";
    }

    public static DataType getInstance()
    {
        return type;
    }

}