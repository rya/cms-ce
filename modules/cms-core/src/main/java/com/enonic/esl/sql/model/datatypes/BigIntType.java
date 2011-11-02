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

public class BigIntType
    extends DataType
{
    private static final BigIntType type = new BigIntType();

    public int getSQLType()
    {
        return Types.BIGINT;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        long value = resultSet.getLong( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return new Long( value );
        }
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        long value = resultSet.getLong( columnIndex );
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
            return ( (Long) obj ).toString();
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
            return ( (Long) obj ).toString();
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
            return new Long( Long.parseLong( text ) );
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        Long value;
        if ( obj instanceof String )
        {
            value = Long.valueOf( (String) obj );
        }
        else
        {
            value = (Long) obj;
        }
        preparedStmt.setLong( columnIndex, value.longValue() );
    }

    public String getTypeString()
    {
        return "BIGINT";
    }

    public static DataType getInstance()
    {
        return type;
    }

}
