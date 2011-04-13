/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.View;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.sql.model.datatypes.XMLType;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;

public class XDG
{
    private static final Logger LOG = LoggerFactory.getLogger( XDG.class.getName() );

    public static StringBuffer generateWhereSQL( StringBuffer sql, Column[] whereColumns )
    {
        if ( sql == null )
        {
            sql = new StringBuffer();
        }
        if ( whereColumns != null )
        {
            sql.append( " WHERE " );
            for ( int i = 0; i < whereColumns.length; i++ )
            {
                sql.append( whereColumns[i] );
                if ( whereColumns[i].isNullColumn() )
                {
                    if ( whereColumns[i].isNotColumn() )
                    {
                        sql.append( " IS NOT NULL" );
                    }
                    else
                    {
                        sql.append( " IS NULL" );
                    }
                }
                else
                {
                    sql.append( " = ?" );
                }
                if ( i < whereColumns.length - 1 )
                {
                    sql.append( " AND " );
                }
            }
        }
        return sql;
    }

    public static StringBuffer generateWhereInSQL( StringBuffer sql, String sqlStart, Column whereInColumn, int count )
    {
        if ( sqlStart == null )
        {
            sqlStart = " WHERE ";
        }
        if ( sql == null )
        {
            sql = new StringBuffer( sqlStart );
        }
        else
        {
            sql.append( sqlStart );
        }

        InClauseBuilder.buildAndAppendTemplateInClause( sql, whereInColumn.getName(), count );
        return sql;
    }

    private static StringBuffer generateSelectSQL( Column[] selectColumns, boolean distinct )
    {
        StringBuffer sql = new StringBuffer( "SELECT " );
        if ( distinct )
        {
            sql.append( "DISTINCT " );
        }
        if ( selectColumns != null && selectColumns.length > 0 )
        {
            for ( int i = 0; i < selectColumns.length; i++ )
            {
                sql.append( selectColumns[i] );
                if ( i < selectColumns.length - 1 )
                {
                    sql.append( ( ", " ) );
                }
            }
        }
        else
        {
            sql.append( "*" );
        }
        return sql;
    }

    /**
     * Generates a SELECT query which retrieves one columns from one table with a WHERE clause that adds and equals check and a question
     * mark (?) on the given where column.
     *
     * @param table        The table to select data from
     * @param selectColumn The desired data column of the table.
     * @param distinct     Whether to add the DISTINCT keyword to the SELECT clause or not.
     * @param whereColumn  The column in the table to add a where check against.
     * @return A StringBuffer containing the generated SQL.
     */
    public static StringBuffer generateSelectSQL( Table table, Column selectColumn, boolean distinct, Column whereColumn )
    {
        Column[] selectColumns = null;
        if ( selectColumn != null )
        {
            selectColumns = new Column[]{selectColumn};
        }
        Column[] whereColumns = null;
        if ( whereColumn != null )
        {
            whereColumns = new Column[]{whereColumn};
        }

        return generateSelectSQL( table, selectColumns, distinct, whereColumns );
    }

    public static StringBuffer generateSelectSQL( Table table, Column[] selectColumns, boolean distinct, Column[] whereColumns )
    {
        // Generate SQL
        StringBuffer sql = generateSelectSQL( selectColumns, distinct );

        sql.append( " FROM " );
        appendTable( sql, table );

        generateWhereSQL( sql, whereColumns );

        return sql;
    }

    private static void appendTable( StringBuffer sql, Table table )
    {
        if ( table instanceof View )
        {
            View view = (View) table;
            if ( view.hasReplacementSql() )
            {
                sql.append( view.getReplacementSql() );
            }
            else
            {
                sql.append( table );
            }
        }
        else
        {
            sql.append( table );
        }
    }

    public static StringBuffer generateUpdateSQL( Table table, Column setColumn, Column whereColumn )
    {
        Column[] setColumns;
        if ( setColumn != null )
        {
            setColumns = new Column[]{setColumn};
        }
        else
        {
            setColumns = null;
        }
        Column[] whereColumns;
        if ( whereColumn != null )
        {
            whereColumns = new Column[]{whereColumn};
        }
        else
        {
            whereColumns = null;
        }
        return generateUpdateSQL( table, setColumns, whereColumns, null );
    }

    public static StringBuffer generateUpdateSQL( Table table, Column[] setColumns, Column[] whereColumns, Set excludeColumns )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "UPDATE " );
        sql.append( table );
        sql.append( " SET " );

        Column[] columns;
        if ( setColumns == null || setColumns.length == 0 )
        {
            columns = table.getColumns();
        }
        else
        {
            columns = setColumns;
        }
        for ( int i = 0; i < columns.length; i++ )
        {
            // If this column is not a primary key and not a "created timestamp"
            if ( !( excludeColumns != null && excludeColumns.contains( columns[i] ) ) && !columns[i].isPrimaryKey() &&
                !( columns[i].getType() == Constants.COLUMN_CREATED_TIMESTAMP ) )
            {
                sql.append( columns[i] );
                sql.append( " = " );

                if ( columns[i].getType() == Constants.COLUMN_CURRENT_TIMESTAMP )
                {
                    sql.append( "@currentTimestamp@" );
                }
                else
                {
                    sql.append( "?" );
                }

                // If there are more columns
                if ( i < columns.length - 1 )
                {
                    sql.append( ( ", " ) );
                }
            }
        }

        if ( whereColumns == null || whereColumns.length == 0 )
        {
            generateWhereSQL( sql, table.getPrimaryKeys() );
        }
        else
        {
            generateWhereSQL( sql, whereColumns );
        }

        return sql;
    }

    public static StringBuffer generateInsertSQL( Table table )
    {
        Column[] columns = table.getColumns();
        return generateInsertSQL( table, columns );
    }

    public static StringBuffer generateInsertSQL( Table table, Column[] columns )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "INSERT INTO " );
        sql.append( table );
        sql.append( " (" );
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( i > 0 )
            {
                sql.append( "," );
            }
            sql.append( columns[i] );
        }
        sql.append( ") VALUES (" );
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( i > 0 )
            {
                sql.append( "," );
            }
            if ( columns[i].getType() == Constants.COLUMN_CREATED_TIMESTAMP || columns[i].getType() == Constants.COLUMN_CURRENT_TIMESTAMP )
            {
                sql.append( "@currentTimestamp@" );
            }
            else
            {
                sql.append( "?" );
            }
        }
        sql.append( ')' );

        return sql;
    }

    public static void setData( PreparedStatement preparedStmt, Table table, Element dataElem, int operation )
        throws SQLException, ParseException, TransformerException
    {
        setData( preparedStmt, table, dataElem, operation, null );
    }

    public static void setData( PreparedStatement preparedStmt, Table table, Element dataElem, int operation, Set excludeColumns )
        throws SQLException, ParseException, TransformerException
    {

        Column[] columns = table.getColumns();
        int pkPosition = columns.length;
        int dataPosition = 0;

        if ( LOG.isDebugEnabled() )
        {
            LOG.debug( StringUtil.expandString( "dataElem = %0", XMLTool.elementToString( dataElem ), null ) );
        }

        int excludedCounter = 0;

        for ( int i = 0; i < columns.length; i++ )
        {
            if ( ( columns[i].isPrimaryKey() && operation == Constants.OPERATION_UPDATE ) )
            {
                continue;
            }

            if ( excludeColumns != null && excludeColumns.contains( columns[i] ) )
            {
                excludedCounter++;
                pkPosition--;
                continue;
            }

            DataType type = columns[i].getType();
            String xpath = columns[i].getXPath();

            int index = table.getIndex( columns[i] ) - excludedCounter;

            if ( operation == Constants.OPERATION_UPDATE )
            {
                index -= table.getPrimaryKeys().length;
            }

            Node node = XMLTool.selectNode( dataElem, xpath );
            Object data = type.getDataFromXML( node );
            if ( data == null && ( type instanceof XMLType ) )
            {
                Document tmpDoc = XMLTool.createDocument();
                data = XMLTool.documentToBytes( tmpDoc, "UTF-8" );
            }

            if ( LOG.isDebugEnabled() )
            {
                LOG.debug( StringUtil.expandString( "i = %0", i, null ) );
                LOG.debug( StringUtil.expandString( "columns[i] = %0", columns[i], null ) );
                LOG.debug( StringUtil.expandString( "index = %0", index, null ) );
                LOG.debug( StringUtil.expandString( "dataPosition = %0", dataPosition, null ) );
                LOG.debug( StringUtil.expandString( "data = %0", data, null ) );
            }

            if ( data == null )
            {

                if ( type == Constants.COLUMN_CURRENT_TIMESTAMP || type == Constants.COLUMN_CREATED_TIMESTAMP )
                {
                    LOG.debug( "Timestamp is not set." );
                    // The current timestamp is inserted directly in the query, which means that
                    // the primary key position is one less than if we inserted the timestamp as
                    // a regular parameter
                    pkPosition--;
                    dataPosition++;
                }
                else if ( columns[i].getDefaultValue() != null )
                {
                    data = columns[i].getDefaultValue();
                    int columnIndex = index - dataPosition;
                    type.setData( preparedStmt, columnIndex, data );
                }
                else
                {
                    preparedStmt.setNull( index - dataPosition, type.getSQLType() );
                }
            }
            else
            {
                if ( operation == Constants.OPERATION_UPDATE && type == Constants.COLUMN_CREATED_TIMESTAMP )
                {
                    // Ignore created timestamp on update
                    LOG.debug( StringUtil.expandString( "Timestamp is ignored.", null, null ) );
                    pkPosition--;
                    dataPosition++;
                }
                else
                {
                    int columnIndex = index - dataPosition;
                    type.setData( preparedStmt, columnIndex, data );
                }
            }
        }

        // If we are doing an update operation, the primary keys are inserted last
        if ( operation == Constants.OPERATION_UPDATE )
        {
            Column[] primaryKeys = table.getPrimaryKeys();
            for ( int i = 0; i < primaryKeys.length; i++ )
            {
                DataType type = primaryKeys[i].getType();
                Node node = XMLTool.selectNode( dataElem, primaryKeys[i].getXPath() );
                Object data = type.getDataFromXML( node );
                type.setData( preparedStmt, pkPosition - primaryKeys.length + i + 1, data );
            }
        }
    }
}