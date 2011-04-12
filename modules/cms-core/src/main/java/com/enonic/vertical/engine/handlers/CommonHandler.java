/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.primitives.Ints;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.util.UUID;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.processors.ElementProcessor;
import com.enonic.vertical.engine.processors.ProcessElementException;

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

    public Object[][] getObjectArray( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Object[][] values = null;

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

            ArrayList<Object[]> rows = new ArrayList<Object[]>();

            while ( resultSet.next() )
            {
                int columnCount = resultSet.getMetaData().getColumnCount();
                Object[] columnValues = new Object[columnCount];

                for ( int columnCounter = 0; columnCounter < columnCount; columnCounter++ )
                {
                    columnValues[columnCounter] = resultSet.getObject( columnCounter + 1 );
                    if ( resultSet.wasNull() )
                    {
                        columnValues[columnCounter] = null;
                    }
                }

                rows.add( columnValues );
            }

            values = new Object[rows.size()][];
            for ( int i = 0; i < rows.size(); i++ )
            {
                values[i] = rows.get( i );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get object[][]: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return values;
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

    public int[] getIntArray( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        List<Integer> keys = new ArrayList<Integer>();

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

            while ( resultSet.next() )
            {
                keys.add( resultSet.getInt( 1 ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get integer array: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return Ints.toArray( keys );
    }

    public int[] getIntArray( String sql )
    {
        return getIntArray( sql, (Object[]) null );
    }

    public int[] getIntArray( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        List<Integer> keys = new ArrayList<Integer>();

        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( sql );

            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    if ( paramValues[i] instanceof Boolean )
                    {
                        preparedStmt.setBoolean( i + 1, (Boolean) paramValues[i] );
                    }
                    else
                    {
                        preparedStmt.setObject( i + 1, paramValues[i] );
                    }
                }
            }

            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                keys.add( resultSet.getInt( 1 ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get integer array: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return Ints.toArray( keys );
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

    public Object[] getObjects( String sql, int paramValue )
    {
        return getObjects( sql, new Integer( paramValue ) );
    }

    public Object[] getObjects( String sql, Object paramValue )
    {
        if ( paramValue == null )
        {
            return getObjects( sql, null );
        }
        else
        {
            return getObjects( sql, new Object[]{paramValue} );
        }
    }

    public Object[] getObjects( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Object[] objects;

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
                int columnCount = resultSet.getMetaData().getColumnCount();
                objects = new Object[columnCount];
                for ( int i = 1; i <= columnCount; i++ )
                {
                    objects[i - 1] = resultSet.getObject( i );
                }
            }
            else
            {
                objects = new Object[0];
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get string: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            objects = new Object[0];
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return objects;
    }

    public boolean hasRows( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean hasRows = false;

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
                hasRows = true;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to execute sql: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return hasRows;
    }

    public Object[] createEntities( CopyContext copyContext, Document doc, ElementProcessor[] elementProcessors )
        throws ProcessElementException
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        Object[] keys;

        Logger log = LOG;

        if ( log.isDebugEnabled() )
        {
            StringWriter writer = new StringWriter();
            XMLTool.printDocument( writer, doc, 2 );
            log.debug( writer.toString() );
        }

        // used by error messages:
        String currentTable = null;
        Element currentElement = null;

        try
        {
            Element rootElem = doc.getDocumentElement();

            Element[] dataElems;
            if ( rootElem.getTagName().equals( "data" ) )
            {
                // If data is root, there may be several different element types
                // here
                ArrayList<Element> dataElemsList = new ArrayList<Element>();

                Element[] parents = XMLTool.getElements( rootElem );

                // Traverse all parents and add all childrens to the
                // dataElemsList
                for ( Element parent : parents )
                {
                    String parentName = parent.getTagName();
                    Table table = db.getTableByParentName( parentName );

                    if ( table == null )
                    {
                        LOG.info( "Did not find table matching parent name {}", parentName );
                        continue;
                    }

                    String elementName = table.getElementName();

                    Element[] children = XMLTool.getElements( parent );

                    // Traverse all childrens
                    for ( Element aChildren : children )
                    {
                        // Check that this element has the correct name
                        if ( !aChildren.getTagName().equals( elementName ) )
                        {
                            LOG.info( "elementname {} did not match expected {}.", aChildren.getTagName(), elementName );
                            continue;
                        }
                        dataElemsList.add( aChildren );
                    }
                }
                dataElems = dataElemsList.toArray( new Element[dataElemsList.size()] );
            }
            else if ( db.getTableByElementName( rootElem.getTagName() ) != null )
            {
                // If the root matches the element name of a table, we have one
                // single entity
                dataElems = new Element[]{rootElem};
            }
            else if ( db.getTableByParentName( rootElem.getTagName() ) != null )
            {
                // If the root matches the parent name of a table, we have one
                // single entity, all it's
                // children are elements
                dataElems = XMLTool.getElements( rootElem );
            }
            else
            {
                String message = "Document root (%0) is not <data>, and does not match any parent or element names.";

                VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                StringUtil.expandString( message, rootElem.getTagName(), null ) );
                dataElems = null;
            }

            keys = new Object[dataElems.length];
            con = getConnection();
            for ( int i = 0; i < dataElems.length; i++ )
            {
                // primary key
                //  - get the key from xml, otherwise generate it
                //  - if key map is present and key exists, map old key to current key
                //  - update xml for use in processing with current key if old key or key missing
                Table table = db.getTableByElementName( dataElems[i].getTagName() );
                String keyStr = dataElems[i].getAttribute( "key" );
                if ( keyStr == null || keyStr.length() == 0 || copyContext != null )
                {
                    // Key must be generated

                    Column primaryKeyColumn = table.getPrimaryKeys()[0];
                    if ( primaryKeyColumn.getType() == Constants.COLUMN_INTEGER )
                    {
                        Integer newKey = getNextKey( table.getName() );
                        if ( copyContext != null )
                        {
                            copyContext.put( dataElems[i].getTagName(), Integer.parseInt( keyStr ), newKey );
                        }
                        keys[i] = newKey;
                    }
                    else
                    {
                        String newKey = UUID.generateValue();
                        if ( copyContext != null )
                        {
                            copyContext.put( dataElems[i].getTagName(), keyStr, newKey );
                        }
                        keys[i] = newKey;
                    }
                    dataElems[i].setAttribute( "key", String.valueOf( keys[i] ) );
                }
                else
                {
                    Column primaryKeyColumn = table.getPrimaryKeys()[0];
                    if ( primaryKeyColumn.getType() == Constants.COLUMN_INTEGER )
                    {
                        keys[i] = Integer.parseInt( keyStr );
                    }
                    else
                    {
                        keys[i] = keyStr;
                    }
                }

                // pre-process each element if one or more processors are present
                if ( elementProcessors != null && elementProcessors.length > 0 )
                {
                    for ( ElementProcessor elementProcessor : elementProcessors )
                    {
                        elementProcessor.process( dataElems[i] );
                    }
                }

                if ( LOG.isDebugEnabled() )
                {
                    LOG.debug("table={};key={}", table.getName(), keys[i]);
                }


                currentTable = table.toString();
                currentElement = dataElems[i];

                StringBuffer sql = XDG.generateInsertSQL( table, dataElems[i] );
                LOG.debug( StringUtil.expandString( "SQL: %0", sql, null ) );

                preparedStmt = con.prepareStatement( sql.toString() );

                XDG.setData( preparedStmt, table, dataElems[i], Constants.OPERATION_INSERT );

                int result = preparedStmt.executeUpdate();
                if ( result == 0 )
                {
                    VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class, "Failed to create entity." );
                }
                close( preparedStmt );
                preparedStmt = null;
            }

        }
        catch ( ParseException pe )
        {
            String message = "Failed to create: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, pe ), pe );
            keys = null;
        }
        catch ( TransformerException te )
        {
            String message = "Failed to create: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, te ), te );
            keys = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
            keys = null;
            if ( currentTable != null )
            {
                LOG.error( "Current table: {}", currentTable );
            }
            if ( currentElement != null )
            {
                LOG.error( "Current element: \n{}", XMLTool.elementToString( currentElement ) );
            }
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed to create: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, gke ), gke );
            keys = null;
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return keys;
    }


    public StringBuffer getPathString( Table table, Column keyColumn, Column parentKeyColumn, Column nameColumn, int key,
                                       String customRootName, boolean includeSpace )
    {

        Column[] selectColumns = new Column[]{parentKeyColumn, nameColumn};
        Column[] whereColumns = new Column[]{keyColumn};
        StringBuffer sql = XDG.generateSelectSQL( table, selectColumns, false, whereColumns );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        StringBuffer result = new StringBuffer();

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );

            while ( key >= 0 )
            {
                preparedStmt.setInt( 1, key );
                resultSet = preparedStmt.executeQuery();
                if ( resultSet.next() )
                {
                    String name;
                    key = resultSet.getInt( 1 );
                    if ( resultSet.wasNull() )
                    {
                        key = -1;
                        if ( customRootName == null )
                        {
                            name = resultSet.getString( 2 );
                        }
                        else
                        {
                            name = customRootName;
                        }
                    }
                    else
                    {
                        name = resultSet.getString( 2 );
                    }

                    if ( result.length() > 0 )
                    {
                        if ( includeSpace )
                        {
                            result.insert( 0, " / " );
                        }
                        else
                        {
                            result.insert( 0, "/" );
                        }
                        result.insert( 0, name );
                    }
                    else
                    {
                        result.append( name );
                    }
                }
                else
                {
                    key = -1;
                }
                close( resultSet );
                resultSet = null;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get path string: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            result.setLength( 0 );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
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
