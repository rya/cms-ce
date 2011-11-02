/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.esl.sql.model.datatypes;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.w3c.dom.Node;

public abstract class DataType
    implements Serializable
{
    public abstract int getSQLType();

    public abstract String getTypeString();

    public abstract Object getData( ResultSet resultSet, int columnIndex )
        throws SQLException;

    public abstract String getDataAsString( ResultSet resultSet, int columnIndex )
        throws SQLException;

    public Object getDataForXML( ResultSet resultSet, int columnIndex )
        throws SQLException
    {
        return getDataForXML( getData( resultSet, columnIndex ) );
    }

    public abstract Object getDataForXML( Object obj );

    public abstract Object getDataFromXML( Node node );

    public abstract String getDataString( Object obj );

    // This method should never be called with null as object!

    public abstract void setData( PreparedStatement preparedStmt, int columnIndex, Object obj )
        throws SQLException;

    public String getSQLValue( Object xpathValue )
    {
        return xpathValue.toString();
    }

    public String toString()
    {
        return getTypeString() + " - SQL Type: " + getSQLType();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if ( obj == null || ( obj instanceof DataType ) == false )
        {
            return false;
        }
        DataType other = (DataType) obj;
        return getTypeString().equals( other.getTypeString() );
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getTypeString().hashCode();
    }
}