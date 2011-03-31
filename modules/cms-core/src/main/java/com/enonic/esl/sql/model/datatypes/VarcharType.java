/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;

public class VarcharType
    extends DataType
{
    private static final VarcharType type = new VarcharType();

    public int getSQLType()
    {
        return Types.VARCHAR;
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
        preparedStmt.setCharacterStream( columnIndex, new StringReader( value ), value.length() );
    }

    public Class getJavaType()
    {
        return String.class;
    }

    public String getTypeString()
    {
        return "VARCHAR";
    }

    public static DataType getInstance()
    {
        return type;
    }

}