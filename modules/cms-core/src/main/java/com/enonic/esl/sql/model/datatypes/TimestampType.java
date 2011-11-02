/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Node;

import com.enonic.esl.xml.XMLTool;

public class TimestampType
    extends DataType
{

    /**
     * Format used for storing dates.
     */
    public static final DateFormat STORE_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );

    private static final TimestampType type = new TimestampType();

    public int getSQLType()
    {
        return Types.TIMESTAMP;
    }

    public Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        Timestamp timestamp = resultSet.getTimestamp( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return new Date( timestamp.getTime() );
        }
    }

    public String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        Timestamp timestamp = resultSet.getTimestamp( columnIndex );
        if ( resultSet.wasNull() )
        {
            return null;
        }
        else
        {
            return STORE_DATE_FORMAT.format( new Date( timestamp.getTime() ) );
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
            return STORE_DATE_FORMAT.format( (Date) obj );
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
            return STORE_DATE_FORMAT.format( (Date) obj );
        }
    }

    public Object getDataFromXML( Node node )
    {
        String text = XMLTool.getNodeText( node );

        if ( text == null || text.length() == 0 )
        {
            return null;
        }

        Date date = null;
        try
        {
            date = STORE_DATE_FORMAT.parse( text );
        }
        catch ( ParseException pe )
        {
            pe.printStackTrace();
        }

        return date;
    }

    public void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException
    {
        Date value;
        if ( obj instanceof String )
        {
            try
            {
                value = STORE_DATE_FORMAT.parse( (String) obj );
            }
            catch ( ParseException pe )
            {
                throw new IllegalArgumentException( "Invalid date: " + obj );
            }
        }
        else
        {
            value = (Date) obj;
        }
        preparedStmt.setTimestamp( columnIndex, new Timestamp( value.getTime() ) );
    }

    /**
     * @see com.enonic.esl.sql.model.datatypes.DataType#getSQLValue(java.lang.Object)
     */
    public String getSQLValue( Object xpathValue )
    {
        if ( xpathValue instanceof Date )
        {
            return STORE_DATE_FORMAT.format( (Date) xpathValue );
        }
        else
        {
            return xpathValue.toString();
        }
    }

    public String getTypeString()
    {
        return "TIMESTAMP";
    }

    public static DataType getInstance()
    {
        return type;
    }

}