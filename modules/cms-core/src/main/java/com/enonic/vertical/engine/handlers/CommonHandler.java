/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.engine.XDG;

public class CommonHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( CommonHandler.class.getName() );


    public int getInt( String sql, Object paramValue )
    {
        if ( paramValue != null )
        {
            return getInt( sql, new Object[]{paramValue} );
        }
        else
        {
            return getInt( sql, (Object[]) null );
        }
    }

    public int getInt( String sql, int paramValue )
    {
        return getInt( sql, new int[]{paramValue} );
    }

    public int getInt( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        int value;

        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( sql );

            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    if ( paramValues[i] instanceof String )
                    {
                        preparedStmt.setString( i + 1, paramValues[i].toString() );
                    }
                    else
                    {
                        preparedStmt.setObject( i + 1, paramValues[i] );
                    }
                }
            }

            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                value = resultSet.getInt( 1 );
                if ( resultSet.wasNull() )
                {
                    value = -1;
                }
            }
            else
            {
                value = -1;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get int: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            value = -1;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return value;
    }

    public int getInt( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        int value;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    preparedStmt.setInt( i + 1, paramValues[i] );
                }
            }
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                value = resultSet.getInt( 1 );
                if ( resultSet.wasNull() )
                {
                    value = -1;
                }
            }
            else
            {
                value = -1;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get int: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            value = -1;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return value;
    }

    public String getString( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        String string;

        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( sql );

            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    preparedStmt.setObject( i + 1, paramValues[i] );
                }
            }

            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                ResultSetMetaData metaData = resultSet.getMetaData();
                String columnName = metaData.getColumnName( 1 );
                Table table = db.getTableByColumnName( columnName );
                if ( table != null )
                {
                    Column column = table.getColumn( metaData.getColumnName( 1 ) );
                    DataType dataType = column.getType();
                    string = dataType.getDataAsString( resultSet, 1 );
                }
                else
                {
                    string = resultSet.getString( 1 );
                    if ( resultSet.wasNull() )
                    {
                        string = null;
                    }
                }
            }
            else
            {
                string = null;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get string: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            string = null;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return string;
    }

    public String getString( String sql, int paramValue )
    {
        return getString( sql, new Object[]{paramValue} );
    }

    public int updateInt( Table table, Column setColumn, int setValue, Column whereColumn, Object paramValue )
        throws SQLException
    {

        Connection con = null;
        PreparedStatement prepStmt = null;
        StringBuffer sql = XDG.generateUpdateSQL( table, setColumn, whereColumn );
        int rowCount = 0;
        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql.toString() );
            prepStmt.setInt( 1, setValue );
            prepStmt.setObject( 2, paramValue );
            rowCount = prepStmt.executeUpdate();
            if ( LOG.isDebugEnabled() )
            {
                String message = "%0 rows updated.";
                LOG.debug( StringUtil.expandString( message, rowCount, null ) );
            }
        }
        finally
        {
            close( prepStmt );
            close( con );
        }
        return rowCount;
    }

    public int update( String sql, Object[] values )
        throws SQLException
    {

        Connection con = null;
        PreparedStatement prepStmt = null;
        int rowCount = 0;
        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql );
            for ( int i = 0; i < values.length; i++ )
            {
                prepStmt.setObject( i + 1, values[i] );
            }
            rowCount = prepStmt.executeUpdate();
            if ( LOG.isDebugEnabled() )
            {
                String message = "%0 row(s) updated.";
                LOG.debug( StringUtil.expandString( message, rowCount, null ) );
            }
        }
        finally
        {
            close( prepStmt );
            close( con );
        }
        return rowCount;
    }


}
