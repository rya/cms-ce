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

public class BooleanType
    extends DataType
{
    private static final BooleanType type = new BooleanType();

    public int getSQLType()
    {
        return Types.INTEGER;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        boolean value = resultSet.getBoolean( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return ( value ? Boolean.TRUE : Boolean.FALSE );
        }
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        boolean value = resultSet.getBoolean( columnIndex );
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
            return ( (Boolean) obj ).toString();
        }
    }

    public String getDataString( Object obj )
    {
        if ( obj == null )
        {
            return "null";
        }
        else if ( obj instanceof Boolean )
        {
            Boolean b = (Boolean) obj;
            if ( b.booleanValue() )
            {
                return "Boolean.TRUE";
            }
            else
            {
                return "Boolean.FALSE";
            }
        }
        else
        {
            return getDataString( new Boolean( obj.toString() ) );
        }
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        Boolean value;
        if ( obj instanceof String )
        {
            try
            {
                Integer i = Integer.valueOf( (String) obj );
                if ( i.intValue() == 0 )
                {
                    value = Boolean.FALSE;
                }
                else
                {
                    value = Boolean.TRUE;
                }
            }
            catch ( NumberFormatException e )
            {
                value = Boolean.valueOf( (String) obj );
            }
        }
        else if ( obj instanceof Integer )
        {
            Integer i = (Integer) obj;
            if ( i.intValue() == 0 )
            {
                value = Boolean.FALSE;
            }
            else
            {
                value = Boolean.TRUE;
            }
        }
        else
        {
            value = (Boolean) obj;
        }
        preparedStmt.setInt( columnIndex, value.booleanValue() ? 1 : 0 );
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
            return Boolean.valueOf( text );
        }
    }

    public String getSQLValue( Object xpathValue )
    {
        if ( "true".equals( xpathValue ) )
        {
            return "1";
        }
        else
        {
            return "0";
        }
    }

    public String getTypeString()
    {
        return "BOOLEAN";
    }

    public static DataType getInstance()
    {
        return type;
    }

}