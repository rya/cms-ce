/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.View;
import com.enonic.esl.sql.model.datatypes.CDATAType;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.sql.model.datatypes.XMLType;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.processors.ElementProcessor;
import com.enonic.vertical.engine.processors.ProcessElementException;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;

public class XDG
{
    private static final Logger LOG = LoggerFactory.getLogger( XDG.class.getName() );

    public static final String OPERATOR_EQUAL = " = ";

    public static final String OPERATOR_NOT_EQUAL = " <> ";

    public static final String OPERATOR_GREATER = " > ";

    public static final String OPERATOR_GREATER_OR_EQUAL = " >= ";

    public static final String OPERATOR_LESS = " < ";

    public static final String OPERATOR_LESS_OR_EQUAL = " <= ";

    public static final String OPERATOR_ISNULL = " IS NULL";

    public static final String OPERATOR_LIKE = " LIKE ";

    public static final String OPERATOR_RANGE = " RANGE ";

    public static final String JOINTYPE_LEFT_JOIN = " LEFT JOIN ";

    public static final String JOINTYPE_JOIN = " JOIN ";

    private final static int IN_VALUE_TRESHOLD = 500;

    public static StringBuffer appendJoinSQL( StringBuffer sql, Column column, Table foreignTable, Column foreignColumn, String joinType )
    {
        if ( sql == null )
        {
            sql = new StringBuffer( joinType );
        }
        else
        {
            sql.append( joinType );
        }

        appendTable( sql, foreignTable );

        sql.append( " ON " );
        sql.append( column );
        sql.append( " = " );
        sql.append( foreignColumn );
        return sql;
    }

    public static StringBuffer appendJoinSQL( StringBuffer sql, Column[] columns, Table foreignTable, Column[] foreignColumns,
                                              String joinType )
    {
        if ( sql == null )
        {
            sql = new StringBuffer( joinType );
        }
        else
        {
            sql.append( joinType );
        }

        appendTable( sql, foreignTable );
        sql.append( " ON " );
        for ( int i = 0; i < columns.length; i++ )
        {
            if ( i > 0 )
            {
                sql.append( " AND " );
            }
            sql.append( columns[i] );
            sql.append( " = " );
            sql.append( foreignColumns[i] );
        }
        return sql;
    }

    public static StringBuffer appendJoinSQL( StringBuffer sql, Column column, Table foreignTable, Column foreignColumn )
    {
        return appendJoinSQL( sql, column, foreignTable, foreignColumn, JOINTYPE_JOIN );
    }

    public static StringBuffer appendJoinSQL( StringBuffer sql, ForeignKeyColumn column )
    {
        return appendJoinSQL( sql, column, column.getReferencedTable(), column.getReferencedColumn(), JOINTYPE_JOIN );
    }

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

    public static void appendOrderBySQL( StringBuffer sql, Column orderByColumn, boolean ascending )
    {
        sql.append( " ORDER BY " );
        sql.append( orderByColumn );
        if ( ascending )
        {
            sql.append( " ASC" );
        }
        else
        {
            sql.append( " DESC" );
        }
    }

    private static void appendAndSQL( StringBuffer sql )
    {
        String match = sql.toString().toUpperCase().trim();

        if ( match.endsWith( "WHERE" ) )
        {
            sql.append( " " );
        }
        else if ( match.matches( "^(.+) VIEW([0-9]+)$" ) )
        {
            sql.append( " WHERE " );
        }
        else if ( match.indexOf( " WHERE" ) == -1 )
        {
            sql.append( " WHERE " );
        }
        else if ( !match.endsWith( "AND" ) )
        {
            sql.append( " AND " );
        }
        else
        {
            sql.append( " " );
        }
    }

    public static void appendWhereSQL( StringBuffer sql, Column whereColumn, String operator, int value )
    {
        appendAndSQL( sql );
        sql.append( whereColumn );
        sql.append( operator );
        sql.append( value );
    }

    public static void appendWhereSQL( StringBuffer sql, Column whereColumn, String operator )
    {
        appendAndSQL( sql );
        sql.append( whereColumn );
        sql.append( operator );
    }

    /**
     * Adds a where clause to an existing SQL.  If the existing SQL already have a where clause, the new condition is ANDed at the end.
     *
     * @param sql         A StringBuffer containing the existing SQL, which is modified in the StringBuffer by this method.
     * @param whereColumn The column to check the value against.
     * @param operator    The operator for the check.
     * @param value       The value to check against.
     */
    public static void appendWhereSQL( StringBuffer sql, Column whereColumn, String operator, String value )
    {
        appendAndSQL( sql );
        sql.append( whereColumn );
        sql.append( operator );
        sql.append( "'" );
        sql.append( value );
        sql.append( "'" );
    }


    public static void appendWhereSQL( StringBuffer sql, Column whereColumn1, String operator, Column whereColumn2 )
    {
        appendAndSQL( sql );
        sql.append( whereColumn1 );
        sql.append( operator );
        sql.append( whereColumn2 );
    }

    public static void appendWhereInSQL( StringBuffer sql, Column whereInColumn, int count )
    {
        if ( count > 0 )
        {
            appendAndSQL( sql );

            InClauseBuilder.buildAndAppendTemplateInClause( sql, whereInColumn.getName(), count );

        }
    }

    private static List<int[]> chunkKeys( int[] keys )
    {
        int pos = 0;
        int left = keys.length;
        List<int[]> list = new ArrayList<int[]>();

        while ( left > 0 )
        {
            int size = Math.min( left, IN_VALUE_TRESHOLD );
            int[] chunk = new int[size];
            System.arraycopy( keys, pos, chunk, 0, size );
            list.add( chunk );
            left = left - size;
            pos = pos + size;
        }

        return list;
    }

    // This method does not append and, and does not chunk up the values

    private static void appendInSQL( StringBuffer sql, Column whereInColumn, int[] values )
    {

        List<Integer> valuesList = new ArrayList<Integer>( values.length );
        for ( int value : values )
        {
            valuesList.add( value );
        }
        InClauseBuilder inclause = new InClauseBuilder<Integer>( whereInColumn.getName(), valuesList )
        {
            public void appendValue( StringBuffer sql, Integer value )
            {
                sql.append( value );
            }
        };
        inclause.appendTo( sql );
    }

    public static void appendWhereInSQL( StringBuffer sql, Column whereInColumn, int[] values )
    {
        if ( values != null && values.length > 0 )
        {
            appendAndSQL( sql );

            if ( values.length < IN_VALUE_TRESHOLD )
            {
                appendInSQL( sql, whereInColumn, values );
            }
            else
            {
                sql.append( "(" );
                // We need to chunk up the values because oracle sucks
                List<int[]> list = chunkKeys( values );

                for ( Iterator<int[]> i = list.iterator(); i.hasNext(); )
                {
                    int[] chunk = i.next();
                    appendInSQL( sql, whereInColumn, chunk );

                    if ( i.hasNext() )
                    {
                        sql.append( " OR " );
                    }
                }

                sql.append( ")" );
            }

        }
    }

    public static void appendWhereInSQL( StringBuffer sql, Column whereInColumn, String[] values )
    {
        if ( values != null && values.length > 0 )
        {
            appendAndSQL( sql );

            List<String> valuesList = new ArrayList<String>( values.length );
            for ( String value : values )
            {
                valuesList.add( value );
            }
            InClauseBuilder inclause = new InClauseBuilder<String>( whereInColumn.getName(), valuesList )
            {
                public void appendValue( StringBuffer sql, String value )
                {
                    sql.append( "'" ).append( value ).append( "'" );
                }
            };
            inclause.appendTo( sql );
        }
    }

    public static StringBuffer generateWhereSQL( StringBuffer sql, Column whereColumn )
    {
        if ( whereColumn != null )
        {
            return generateWhereSQL( sql, new Column[]{whereColumn} );
        }
        else
        {
            return sql;
        }
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

    public static StringBuffer generateFKJoinSQL( Table table, ForeignKeyColumn fkColumn, Column[] selectColumns, Column[] whereColumns )
    {

        StringBuffer sql = generateSelectSQL( table, selectColumns, false, null );
        appendJoinSQL( sql, fkColumn );
        generateWhereSQL( sql, whereColumns );

        return sql;
    }

    public static StringBuffer generateFKJoinSQL( Table table1, Table table2, Column[] selectColumns, Column[] whereColumns )
    {

        StringBuffer sql = generateSelectSQL( table1, selectColumns, false, null );
        appendJoinSQL( sql, table1.getForeignKey( table2 ) );
        generateWhereSQL( sql, whereColumns );

        return sql;
    }

    public static StringBuffer generateSelectSQL( Table table )
    {
        return generateSelectSQL( table, (Column[]) null, false, null );
    }

    public static StringBuffer generateSelectWherePrimaryKeySQL( Table table )
    {
        StringBuffer sql = generateSelectSQL( table, (Column[]) null, false, null );
        generateWhereSQL( sql, table.getPrimaryKeys() );
        return sql;
    }

    public static StringBuffer generateSelectSQL( Table table, Column whereColumn )
    {
        return generateSelectSQL( table, (Column[]) null, false, new Column[]{whereColumn} );
    }

    public static StringBuffer generateSelectSQL( Table table, Column selectColumn, boolean distinct, Column[] whereColumns )
    {
        return generateSelectSQL( table, new Column[]{selectColumn}, distinct, whereColumns );
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

    public static StringBuffer generateCountSQL( Table table )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "SELECT count(*) FROM " );
        appendTable( sql, table );

        return sql;
    }

    public static StringBuffer generateSelectWhereInSQL( Table table, Column[] selectColumns, boolean distinct, Column whereInColumn,
                                                         int count )
    {
        // Generate SQL

        StringBuffer sql = generateSelectSQL( table, selectColumns, distinct, null );
        generateWhereInSQL( sql, null, whereInColumn, count );
        return sql;
    }

    public static StringBuffer generateSelectWhereInSQL( Table table, Column selectColumn, boolean distinct, Column whereInColumn,
                                                         int count )
    {

        Column[] selectColumns = null;
        if ( selectColumn != null )
        {
            selectColumns = new Column[]{selectColumn};
        }
        return generateSelectWhereInSQL( table, selectColumns, distinct, whereInColumn, count );
    }

    public static Document resultSetToXML( Table table, ResultSet resultSet, Element parentElem, ElementProcessor[] elementProcessors,
                                           String sortAttribute, int totalCount )
        throws SQLException
    {

        ResultSetMetaData metaData = resultSet.getMetaData();

        Document doc;
        if ( parentElem == null )
        {
            doc = XMLTool.createDocument( table.getParentName() );
            parentElem = doc.getDocumentElement();
        }
        else
        {
            doc = parentElem.getOwnerDocument();
        }

        int count = 0;
        if ( totalCount == -1 )
        {
            totalCount = Integer.MAX_VALUE;
        }
        while ( count < totalCount && resultSet.next() )
        {
            count++;
            Element elem = null;
            if ( sortAttribute != null )
            {
                Column sortColumn = table.getColumn( "@" + sortAttribute );
                if ( sortColumn != null )
                {
                    String sortValue = resultSet.getString( sortColumn.getName() );
                    if ( !resultSet.wasNull() )
                    {
                        elem = XMLTool.createElement( doc, parentElem, table.getElementName(), null, sortAttribute, sortValue );
                    }
                }
            }

            if ( elem == null )
            {
                elem = XMLTool.createElement( doc, parentElem, table.getElementName() );
            }

            int columnCount = metaData.getColumnCount();
            for ( int i = 1; i <= columnCount; i++ )
            {
                String columnName = metaData.getColumnLabel( i );

                Column column = table.getColumn( columnName );
                if ( column == null )
                {
                    String message = "column not in xml: %0, tablename: %1";
                    Object[] msgData = {columnName, table};

                    VerticalRuntimeException.error( XDG.class, VerticalEngineRuntimeException.class,
                                                    StringUtil.expandString( message, msgData, null ) );
                }
                String xpath = column.getXPath();

                if ( xpath == null )
                {
                    continue;
                }

                DataType type = column.getType();

                Object data = type.getDataForXML( resultSet, i );

                if ( !resultSet.wasNull() && !( data.toString().length() == 0 ) )
                {
                    setXPathValue( elem, xpath, data, type );
                }
            }
            if ( elementProcessors != null )
            {
                try
                {
                    for ( ElementProcessor elementProcessor : elementProcessors )
                    {
                        if ( elementProcessor != null )
                        {
                            elementProcessor.process( elem );
                        }
                    }
                }
                catch ( ProcessElementException pee )
                {
                    String message = "Failed to run element processors: %t";
                    LOG.error( StringUtil.expandString( message, (Object) null, pee ), pee );
                }
            }
        }

        return doc;
    }

    private static Element setXPathValue( Element parentElement, String xpath, Object value, DataType type )
    {
        String[] xpathSplit = StringUtil.splitString( xpath, '/' );
        Element tmpElem = parentElement;

        for ( int i = 0; i < xpathSplit.length; i++ )
        {
            String current = xpathSplit[i];

            if ( current.startsWith( "@" ) )
            {
                tmpElem.setAttribute( current.substring( 1 ), value.toString() );
            }
            //else if (current.equals(".")) {
            // Do nothing
            //}
            else if ( XMLTool.getElement( parentElement, current ) != null )
            {
                tmpElem = XMLTool.getElement( parentElement, current );
                if ( i == xpathSplit.length - 1 )
                {
                    if ( value instanceof Document )
                    // This should really never happen, means that an element is present where we want to add xml
                    {
                        tmpElem.appendChild( tmpElem.getOwnerDocument().importNode( ( (Document) value ).getDocumentElement(), true ) );
                    }
                    else if ( type instanceof CDATAType )
                    {
                        XMLTool.createCDATASection( tmpElem.getOwnerDocument(), tmpElem, value.toString() );
                    }
                    else
                    {
                        XMLTool.createTextNode( tmpElem.getOwnerDocument(), tmpElem, value.toString() );
                    }
                }
            }
            else
            {
                if ( i == xpathSplit.length - 1 )
                {
                    if ( value instanceof Document )
                    {
                        tmpElem.appendChild( tmpElem.getOwnerDocument().importNode( ( (Document) value ).getDocumentElement(), true ) );
                    }
                    else if ( type instanceof CDATAType )
                    {
                        tmpElem = XMLTool.createElement( tmpElem.getOwnerDocument(), tmpElem, current );
                        XMLTool.createCDATASection( tmpElem.getOwnerDocument(), tmpElem, value.toString() );
                    }
                    else
                    {
                        tmpElem = XMLTool.createElement( tmpElem.getOwnerDocument(), tmpElem, current, value.toString() );
                    }
                }
                else
                {
                    tmpElem = XMLTool.createElement( tmpElem.getOwnerDocument(), tmpElem, current );
                }
            }
        }
        return tmpElem;
    }

    public static StringBuffer generateRemoveWherePrimaryKeysSQL( Table table )
    {
        return generateRemoveSQL( table, table.getPrimaryKeys() );
    }

    public static StringBuffer generateRemoveSQL( Table table, Column whereColumn )
    {
        return generateRemoveSQL( table, new Column[]{whereColumn} );
    }

    public static StringBuffer generateRemoveSQL( Table table, Column[] whereColumns )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "DELETE FROM " );
        sql.append( table );
        generateWhereSQL( sql, whereColumns );
        return sql;
    }

    public static StringBuffer generateRemoveSQL( Table table, Column whereInColumn, int count )
    {
        StringBuffer sql;

        if ( table.getDeletedColumn() != null )
        {
            sql = new StringBuffer( "UPDATE " );
            sql.append( table );
            sql.append( " SET " );
            sql.append( table.getDeletedColumn() );
            sql.append( "= 1" );
        }
        else
        {
            sql = new StringBuffer( "DELETE FROM " );
            sql.append( table );
        }

        generateWhereInSQL( sql, null, whereInColumn, count );
        return sql;
    }

    public static StringBuffer generateInsertSQL( Table table, Element elem )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "INSERT INTO " );
        sql.append( table );
        sql.append( " (" );

        StringBuffer params = new StringBuffer();

        Column[] columns = table.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {
            sql.append( columns[i].getName() );

            if ( columns[i].getType() == Constants.COLUMN_CURRENT_TIMESTAMP || columns[i].getType() == Constants.COLUMN_CREATED_TIMESTAMP )
            {
                String text = XMLTool.getElementText( elem, columns[i].getXPath() );
                if ( text == null || text.length() == 0 )
                {
                    params.append( "@currentTimestamp@" );
                }
                else
                {
                    params.append( "?" );
                }
            }
            else
            {
                params.append( "?" );
            }

            // If there are more nodes
            if ( i < columns.length - 1 )
            {
                sql.append( ( ", " ) );
                params.append( ", " );
            }
        }
        sql.append( ") VALUES (" );
        sql.append( params );
        sql.append( ")" );

        return sql;
    }

    public static StringBuffer generateUpdateSQL( Table table )
    {
        return generateUpdateSQL( table, null, null, null );
    }

    public static StringBuffer generateUpdateSQL( Table table, Element elem )
    {
        // Generate SQL
        StringBuffer sql = new StringBuffer( "UPDATE " );
        sql.append( table );
        sql.append( " SET " );

        Column[] columns = table.getColumns();
        for ( int i = 0; i < columns.length; i++ )
        {

            if ( columns[i].isPrimaryKey() || columns[i].getType() == Constants.COLUMN_CREATED_TIMESTAMP )
            {
                continue;
            }
            else if ( columns[i].getType() == Constants.COLUMN_CURRENT_TIMESTAMP )
            {
                sql.append( columns[i].getName() );
                sql.append( " = " );
                String text = XMLTool.getElementText( elem, columns[i].getXPath() );
                if ( text == null || text.length() == 0 )
                {
                    sql.append( "@currentTimestamp@" );
                }
                else
                {
                    sql.append( "?" );
                }
            }
            else
            {
                sql.append( columns[i].getName() );
                sql.append( " = ?" );
            }

            // If there are more nodes
            if ( i < columns.length - 1 )
            {
                sql.append( ", " );
            }
        }

        generateWhereSQL( sql, table.getPrimaryKeys() );

        return sql;
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