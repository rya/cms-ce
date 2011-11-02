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

public class IntegerType
    extends DataType
{
    private static final IntegerType type = new IntegerType();

    public int getSQLType()
    {
        return Types.INTEGER;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        int value = resultSet.getInt( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return new Integer( value );
        }
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        int value = resultSet.getInt( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return String.valueOf( value );
        }
    }

    public Object getDataForXML( Object obj )
    {
        if ( obj == null )
        {
            return null;
        }
        else
        {
            return ( (Integer) obj ).toString();
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
            return ( (Integer) obj ).toString();
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
            return new Integer( Integer.parseInt( text ) );
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        Integer value;
        if ( obj instanceof String )
        {
            value = Integer.valueOf( (String) obj );
        }
        else
        {
            value = (Integer) obj;
        }
        preparedStmt.setInt( columnIndex, value.intValue() );
    }

    public String getTypeString()
    {
        return "INTEGER";
    }

    public static DataType getInstance()
    {
        return type;
    }

}