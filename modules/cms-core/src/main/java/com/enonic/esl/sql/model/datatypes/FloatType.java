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

public class FloatType
    extends DataType
{
    private static final FloatType type = new FloatType();

    public int getSQLType()
    {
        return Types.FLOAT;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        float value = resultSet.getFloat( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return new Float( value );
        }
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        float value = resultSet.getFloat( columnIndex );
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
            return ( (Float) obj ).toString();
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
            return ( (Float) obj ).toString();
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
            return new Float( Float.parseFloat( text ) );
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        Float value = (Float) obj;
        if ( obj instanceof String )
        {
            value = Float.valueOf( (String) obj );
        }
        else
        {
            value = (Float) obj;
        }
        preparedStmt.setFloat( columnIndex, (Float)value.floatValue() );
    }

    public String getTypeString()
    {
        return "FLOAT";
    }

    public static DataType getInstance()
    {
        return type;
    }

}