/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.containers.MultiValueMap;
import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Constants;
import com.enonic.esl.sql.model.ForeignKeyColumn;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.sql.model.datatypes.DataType;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.util.UUID;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.Types;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.processors.ElementProcessor;
import com.enonic.vertical.engine.processors.ProcessElementException;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.util.TIntObjectHashMap;

import com.enonic.cms.domain.security.user.User;

public class CommonHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( CommonHandler.class.getName() );

    public final static int MSG_GENERAL = 0;

    private final static int FETCH_SIZE = 20;

    public int executeSQL( String sql, int paramValue )
    {
        return executeSQL( sql, new Object[]{paramValue} );
    }


    public int executeSQL( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        int result = 0;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );

            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    final int at = i + 1;
                    final Object value = paramValues[i];
                    if ( value instanceof String )
                    {
                        preparedStmt.setString( at, (String) value );
                    }
                    else
                    {
                        preparedStmt.setObject( at, value );
                    }
                }
            }
            result = preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to execute sql: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
        return result;
    }

    public int executeSQL( String sql, int[] paramValues )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        int result = 0;

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
            result = preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to execute sql: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
            result = 0;
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
        return result;
    }

    public int executeSQL( String sql, Integer[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        int result = 0;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );

            if ( paramValues != null )
            {
                for ( int i = 0; i < paramValues.length; i++ )
                {
                    if ( paramValues[i] == null )
                    {
                        preparedStmt.setNull( i + 1, java.sql.Types.INTEGER );
                    }
                    else
                    {
                        preparedStmt.setInt( i + 1, paramValues[i] );
                    }
                }
            }
            result = preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to execute sql: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
        return result;
    }

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

    public Date getTimestamp( String sql, int paramValue )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Date timestamp = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            preparedStmt.setInt( 1, paramValue );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                timestamp = resultSet.getTimestamp( 1 );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get date: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return timestamp;
    }

    public int getInt( String sql, int paramValue )
    {
        return getInt( sql, new int[]{paramValue} );
    }

    public Object[][] getObjectArray( String sql, Object[] paramValues )
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return values;
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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

    public boolean getBoolean( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean value;

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
                value = resultSet.getInt( 1 ) != 0;
                if ( resultSet.wasNull() )
                {
                    value = false;
                }
            }
            else
            {
                value = false;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get int: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
            value = false;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return value;
    }

    public byte[] getByteArray( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        byte[] byteArray = null;

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
                byteArray = resultSet.getBytes( 1 );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get byte array: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return byteArray;
    }

    public int[] getIntArray( String sql, int paramValue )
    {
        return getIntArray( sql, new int[]{paramValue} );
    }

    public int[] getIntArray( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntArrayList keys = new TIntArrayList();

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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return keys.toArray();
    }

    public TIntObjectHashMap getIntIntArrayMap( String sql, int paramValue )
    {
        return getIntIntArrayMap( sql, new int[]{paramValue} );
    }

    public TIntObjectHashMap getIntIntArrayMap( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntObjectHashMap keyMap = new TIntObjectHashMap();

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
                int key = resultSet.getInt( 1 );
                int value = resultSet.getInt( 2 );
                TIntArrayList values;
                if ( keyMap.contains( key ) )
                {
                    values = (TIntArrayList) keyMap.get( key );
                }
                else
                {
                    values = new TIntArrayList();
                    keyMap.put( key, values );
                }
                values.add( value );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get (integer -> integer array) map: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return keyMap;
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
        TIntArrayList keys = new TIntArrayList();

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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return keys.toArray();
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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

    public Date getTimestamp( Table table, Column selectColumn, boolean distinct, Column whereColumn, int paramValue )
    {
        String sql = XDG.generateSelectSQL( table, selectColumn, distinct, whereColumn ).toString();
        return getTimestamp( sql, paramValue );
    }

    public String[] getStringArray( String sql, int paramValue )
    {
        return getStringArray( sql, new int[]{paramValue} );
    }

    public String[] getStringArray( String sql, int[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        ArrayList<String> strings = new ArrayList<String>();
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
                strings.add( resultSet.getString( 1 ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get string: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return strings.toArray( new String[strings.size()] );
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

    public String[] getStrings( String sql, Object[] paramValues )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        String[] strings;

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
                strings = new String[columnCount];
                for ( int i = 1; i <= columnCount; i++ )
                {
                    strings[i - 1] = resultSet.getString( i );
                }
            }
            else
            {
                strings = new String[0];
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get string: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
            strings = new String[0];
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return strings;
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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

    public boolean hasRows( String sql )
    {
        return hasRows( sql, null );
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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
                        System.out.println( "Did not find table matching parent name " + parentName );
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
                            System.out.println( "elementname " + aChildren.getTagName() + " did not match expected " + elementName + "." );
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
                    String message = "table=%0;key=%1";
                    Object[] msgData = new Object[]{table.getName(), keys[i]};
                    LOG.debug(StringUtil.expandString( message,
                                                                                                        msgData ));
                }

                currentTable = table.toString();
                currentElement = dataElems[i];

                StringBuffer sql = XDG.generateInsertSQL( table, dataElems[i] );
                LOG.debug(StringUtil.expandString( "SQL: %0", sql,
                                                                                                    null ) );

                preparedStmt = con.prepareStatement( sql.toString() );

                XDG.setData( preparedStmt, table, dataElems[i], Constants.OPERATION_INSERT );

                int result = preparedStmt.executeUpdate();
                if ( result == 0 )
                {
                    String message = "Failed to create entity.";

                    VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
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
                System.err.println( "Current table: " + currentTable );
            }
            if ( currentElement != null )
            {
                System.err.println( "Current element: \n" + XMLTool.elementToString( currentElement ) );
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


    public void removeEntities( String[] keys, Table table )
        throws VerticalRemoveException
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        int index;
        try
        {
            con = getConnection();
            StringBuffer sql = XDG.generateRemoveWherePrimaryKeysSQL( table );
            preparedStmt = con.prepareStatement( sql.toString() );
            for ( index = 0; index < keys.length; index++ )
            {
                cascadeDelete( table, keys[index] );
                preparedStmt.setString( 1, keys[index] );
                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            int msgKey = MSG_GENERAL;
            String message = "Failed to remove entity/ies: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalRemoveException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updateEntities( Document doc, ElementProcessor[] elementProcessors )
        throws VerticalUpdateException, ProcessElementException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            Element rootElem = doc.getDocumentElement();
            Element[] dataElems;
            if ( db.getTableByElementName( rootElem.getTagName() ) != null )
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

                VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                StringUtil.expandString( message, rootElem.getTagName(), null ) );
                dataElems = null;
            }

            con = getConnection();
            for ( Element dataElem : dataElems )
            {
                String keyStr = dataElem.getAttribute( "key" );
                if ( keyStr == null || keyStr.length() == 0 )
                {
                    String message = "Update failed, missing key.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }
                else
                {
                    Table table = db.getTableByElementName( dataElem.getTagName() );
                    StringBuffer sql = XDG.generateUpdateSQL( table, dataElem );

                    // pre-process each element if one or more processors are present
                    if ( elementProcessors != null && elementProcessors.length > 0 )
                    {
                        for ( ElementProcessor elementProcessor : elementProcessors )
                        {
                            elementProcessor.process( dataElem );
                        }
                    }

                    preparedStmt = con.prepareStatement( sql.toString() );
                    XDG.setData( preparedStmt, table, dataElem, Constants.OPERATION_UPDATE );

                    int result = preparedStmt.executeUpdate();
                    if ( result == 0 )
                    {
                        String message = "Failed to update entity with key: %0";

                        VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                        StringUtil.expandString( message, keyStr, null ) );
                    }
                    close( preparedStmt );
                }
            }
        }
        catch ( ParseException pe )
        {
            String message = "Failed to update: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, pe ), pe );
        }
        catch ( TransformerException te )
        {
            String message = "Failed to update: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, te ), te );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }


    public Document getSingleData( int type, int key, ElementProcessor[] elementProcessors )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;
        Table table = Types.getTable( type );

        try
        {
            con = getConnection();

            StringBuffer sql = XDG.generateSelectWherePrimaryKeySQL( table );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, key );
            resultSet = preparedStmt.executeQuery();

            doc = XDG.resultSetToXML( table, resultSet, null, elementProcessors, null, -1 );
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;
    }

    public Document getSingleData( int type, String key, ElementProcessor[] elementProcessors )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;
        Table table = Types.getTable( type );

        try
        {
            con = getConnection();

            StringBuffer sql = XDG.generateSelectWherePrimaryKeySQL( table );
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setString( 1, key );
            resultSet = preparedStmt.executeQuery();

            doc = XDG.resultSetToXML( table, resultSet, null, elementProcessors, null, -1 );
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;
    }

    public Document getData( User user, int type, int[] keys )
    {
        Table table = Types.getTable( type );

        Column pkColumn = table.getPrimaryKeys()[0];

        MultiValueMap parameters = new MultiValueMap();
        if ( keys != null && keys.length > 0 )
        {
            for ( int key : keys )
            {
                parameters.put( pkColumn.getXPath(), new Integer( key ) );
            }
        }
        else
        {
            parameters.put( pkColumn.getXPath(), new Integer( -1 ) );
        }

        return getData( user, type, null, parameters, null, null, -1, -1, null, false, false, true );
    }

    public Document getData( User user, int type, Column[] selectColumns, MultiValueMap parameters, HashMap<String, String> accessParams,
                             ElementProcessor[] elementProcessors, int fromIndex, int count, String orderBy, boolean descending,
                             boolean includeAccessRights, boolean includeCount )
    {

        return getData( user, type, selectColumns, parameters, accessParams, elementProcessors, fromIndex, count, orderBy, descending,
                        includeAccessRights, includeCount, null );
    }

    private ResultSet getResultSet( PreparedStatement preparedStmt, List<DataType> dataTypes, List<String> paramValues, int fromIndex )
        throws SQLException
    {

        ResultSet resultSet;

        // Set parameter values
        for ( int i = 0; i < paramValues.size(); i++ )
        {
            if ( LOG.isDebugEnabled() )
            {
                Object[] msgData = new Object[3];
                msgData[0] = i;
                msgData[1] = paramValues.get( i );
                msgData[2] = paramValues.get( i ).getClass();
                LOG.debug(StringUtil.expandString(
                        "Parameter %0: %1 (%2)", msgData, null ) );
            }
            if ( dataTypes != null )
            {
                DataType dataType = dataTypes.get( i );
                dataType.setData( preparedStmt, i + 1, paramValues.get( i ) );
            }
            else
            {
                preparedStmt.setObject( i + 1, paramValues.get( i ) );
            }
        }
        resultSet = preparedStmt.executeQuery();

        if ( fromIndex != -1 )
        {
            int resultSetPosition = 0;
            boolean moreResults = true;

            // do manual skip
            // NOTE! resultSet.relative does not work correctly on PostgreSQL database
            while ( resultSetPosition < fromIndex && moreResults )
            {
                moreResults = resultSet.next();
                resultSetPosition++;
            }
        }

        return resultSet;
    }

    private PreparedStatement getPreparedStatement( List<DataType> dataTypes, List<String> paramValues, Connection con, User user, int type,
                                                    Column[] selectColumns, MultiValueMap parameters, HashMap<String, String> accessParams,
                                                    String orderBy, boolean descending )
        throws SQLException
    {
        PreparedStatement preparedStmt;
        Table table = Types.getTable( type );
        StringBuffer sql;
        if ( selectColumns == null )
        {
            sql = XDG.generateSelectSQL( table );
        }
        else
        {
            sql = XDG.generateSelectSQL( table, selectColumns, false, null );
        }

        if ( parameters != null && parameters.size() > 0 )
        {
            sql.append( " WHERE " );

            Iterator iter = parameters.keySet().iterator();
            for ( int paramCount = 0; iter.hasNext(); paramCount++ )
            {
                String xpath = iter.next().toString();

                if ( paramCount > 0 )
                {
                    sql.append( " AND " );
                }

                Column column = table.getColumnByXPath( xpath );
                List values = parameters.getValueList( xpath );

                if ( LOG.isDebugEnabled() )
                {
                    Object[] msgData = new Object[3];
                    msgData[0] = column;
                    msgData[1] = values;
                    msgData[2] = values.getClass();
                    LOG.debug(StringUtil.expandString( "%0: %1 (%2)",
                                                                                                        msgData, null )
                                                                                );
                }

                if ( values.size() == 0 )
                {
                    sql.append( column ).append( " IS NULL" );
                }
                else if ( values.size() == 1 )
                {
                    if ( XDG.OPERATOR_LIKE.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column ).append( XDG.OPERATOR_LIKE + " ?" );
                    }
                    else if ( XDG.OPERATOR_LESS.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column ).append( XDG.OPERATOR_LESS + " ?" );
                    }
                    else if ( XDG.OPERATOR_LESS_OR_EQUAL.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column ).append( XDG.OPERATOR_LESS_OR_EQUAL + " ?" );
                    }
                    else if ( XDG.OPERATOR_GREATER.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column ).append( XDG.OPERATOR_GREATER + " ?" );
                    }
                    else if ( XDG.OPERATOR_GREATER_OR_EQUAL.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column ).append( XDG.OPERATOR_GREATER_OR_EQUAL + " ?" );
                    }
                    else
                    {
                        sql.append( column ).append( " = ?" );
                    }
                    paramValues.add( column.getColumnValue( values.get( 0 ) ) );
                    dataTypes.add( column.getType() );
                }
                else if ( values.size() == 2 && XDG.OPERATOR_RANGE.equals( parameters.getAttribute( xpath ) ) )
                {
                    sql.append( column );
                    sql.append( XDG.OPERATOR_GREATER_OR_EQUAL );
                    sql.append( " ? AND " );
                    sql.append( column );
                    sql.append( XDG.OPERATOR_LESS );
                    sql.append( " ?" );
                    paramValues.add( column.getColumnValue( values.get( 0 ) ) );
                    dataTypes.add( column.getType() );
                    paramValues.add( column.getColumnValue( values.get( 1 ) ) );
                    dataTypes.add( column.getType() );
                }
                else
                {
                    sql.append( column ).append( " IN (" );
                    for ( int i = 0; i < values.size(); i++ )
                    {
                        sql.append( "?" );
                        if ( i < values.size() - 1 )
                        {
                            sql.append( ", " );
                        }
                        paramValues.add( column.getColumnValue( values.get( i ) ) );
                        dataTypes.add( column.getType() );
                    }
                    sql.append( ")" );
                }
            }
        }

        // Add security stuff
        if ( accessParams != null && accessParams.size() > 0 )
        {
            getSecurityHandler().appendAccessRightsSQL( user, type, sql, accessParams );
        }

        if ( orderBy != null )
        {
            sql.append( " ORDER BY " ).append( table.getColumnByXPath( orderBy ) );
            if ( descending )
            {
                sql.append( " DESC" );
            }
        }

        LOG.debug(StringUtil.expandString( "SQL: %0", sql.toString(),
                                                                                            null ) );
        preparedStmt = con.prepareStatement( sql.toString() );
        preparedStmt.setFetchSize( FETCH_SIZE );

        return preparedStmt;
    }

    public Document getData( User user, int type, Column[] selectColumns, MultiValueMap parameters, HashMap<String, String> accessParams,
                             ElementProcessor[] elementProcessors, int fromIndex, int count, String orderBy, boolean descending,
                             boolean includeAccessRights, boolean includeCount, Element parentElem )
    {

        LOG.debug(StringUtil.expandString( "Parameters: %0",
                                                                                            parameters, null ) );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;

        Table table = Types.getTable( type );

        try
        {
            con = getConnection();
            List<DataType> dataTypes = new ArrayList<DataType>();
            List<String> paramValues = new ArrayList<String>();
            preparedStmt = getPreparedStatement( dataTypes, paramValues, con, user, type, selectColumns, parameters, accessParams, orderBy,
                                                 descending );

            resultSet = getResultSet( preparedStmt, dataTypes, paramValues, fromIndex );
            doc = XDG.resultSetToXML( table, resultSet, parentElem, elementProcessors, null, count );
            count = XMLTool.getElements( doc.getDocumentElement() ).length;

            if ( includeCount )
            {
                int totalCount = getDataCount( user, type, parameters, accessParams );
                doc.getDocumentElement().setAttribute( "totalcount", String.valueOf( totalCount ) );
                doc.getDocumentElement().setAttribute( "count", String.valueOf( count ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        if ( includeAccessRights )
        {
            getSecurityHandler().appendAccessRights( user, doc, true, true );
        }

        return doc;
    }


    public int getDataCount( User user, int type, MultiValueMap parameters, HashMap<String, String> accessParams )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Table table = Types.getTable( type );
        int result;

        try
        {
            con = getConnection();

            StringBuffer sql = XDG.generateCountSQL( table );
            List<DataType> dataTypes = new ArrayList<DataType>();
            List<String> paramValues = new ArrayList<String>();

            if ( parameters != null && parameters.size() > 0 )
            {
                sql.append( " WHERE " );
                int paramCount = 0;
                for ( Object o : parameters.keySet() )
                {
                    String xpath = o.toString();
                    if ( paramCount > 0 )
                    {
                        sql.append( " AND " );
                    }

                    Column column = table.getColumnByXPath( xpath );
                    List values = parameters.getValueList( xpath );
                    if ( values.size() == 0 )
                    {
                        sql.append( column ).append( " IS NULL" );
                    }
                    else if ( values.size() == 1 )
                    {
                        if ( XDG.OPERATOR_LIKE.equals( parameters.getAttribute( xpath ) ) )
                        {
                            sql.append( column ).append( XDG.OPERATOR_LIKE + " ?" );
                        }
                        else if ( XDG.OPERATOR_LESS.equals( parameters.getAttribute( xpath ) ) )
                        {
                            sql.append( column ).append( XDG.OPERATOR_LESS + " ?" );
                        }
                        else if ( XDG.OPERATOR_LESS_OR_EQUAL.equals( parameters.getAttribute( xpath ) ) )
                        {
                            sql.append( column ).append( XDG.OPERATOR_LESS_OR_EQUAL + " ?" );
                        }
                        else if ( XDG.OPERATOR_GREATER.equals( parameters.getAttribute( xpath ) ) )
                        {
                            sql.append( column ).append( XDG.OPERATOR_GREATER + " ?" );
                        }
                        else if ( XDG.OPERATOR_GREATER_OR_EQUAL.equals( parameters.getAttribute( xpath ) ) )
                        {
                            sql.append( column ).append( XDG.OPERATOR_GREATER_OR_EQUAL + " ?" );
                        }
                        else
                        {
                            sql.append( column ).append( " = ?" );
                        }
                        paramValues.add( column.getColumnValue( values.get( 0 ) ) );
                        dataTypes.add( column.getType() );
                    }
                    else if ( values.size() == 2 && XDG.OPERATOR_RANGE.equals( parameters.getAttribute( xpath ) ) )
                    {
                        sql.append( column );
                        sql.append( XDG.OPERATOR_GREATER_OR_EQUAL );
                        sql.append( " ? AND " );
                        sql.append( column );
                        sql.append( XDG.OPERATOR_LESS );
                        sql.append( " ?" );
                        paramValues.add( column.getColumnValue( values.get( 0 ) ) );
                        dataTypes.add( column.getType() );
                        paramValues.add( column.getColumnValue( values.get( 1 ) ) );
                        dataTypes.add( column.getType() );
                    }
                    else if ( values.size() > 1 )
                    {
                        sql.append( column ).append( " IN (" );
                        for ( int i = 0; i < values.size(); i++ )
                        {
                            sql.append( "?" );
                            if ( i < values.size() - 1 )
                            {
                                sql.append( ", " );
                            }
                            paramValues.add( column.getColumnValue( values.get( i ) ) );
                            dataTypes.add( column.getType() );
                        }
                        sql.append( ")" );
                    }
                    paramCount++;
                }
            }

            // Add security stuff
            if ( accessParams != null && accessParams.size() > 0 )
            {
                getSecurityHandler().appendAccessRightsSQL( user, type, sql, accessParams );
            }

            preparedStmt = con.prepareStatement( sql.toString() );

            // Set parameter values
            for ( int i = 0; i < paramValues.size(); i++ )
            {
                DataType dataType = dataTypes.get( i );
                dataType.setData( preparedStmt, i + 1, paramValues.get( i ) );
            }

            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                result = resultSet.getInt( 1 );
            }
            else
            {
                String message = "Failed to count data.";
                LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                     null ) );
                result = 0;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to count data: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
            result = 0;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
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
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
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
                LOG.debug(StringUtil.expandString( message, rowCount,
                                                                                                    null ) );
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
                LOG.debug(
                        StringUtil.expandString( message, rowCount, null ) );
            }
        }
        finally
        {
            close( prepStmt );
            close( con );
        }
        return rowCount;
    }

    public Document getDocument( StringBuffer sql, int paramValue )
    {
        //InputStream in = getBinaryStream(sql.toString(), paramValue);
        byte[] bytes = getByteArray( sql.toString(), new Object[]{paramValue} );
        if ( bytes != null )
        {
            return XMLTool.domparse( new ByteArrayInputStream( bytes ) );
        }
        else
        {
            return null;
        }
    }

    public Document getDocument( Table table, int key )
    {
        StringBuffer sql = XDG.generateSelectSQL( table, table.getXMLColumn(), false, table.getPrimaryKeys()[0] );
        return getDocument( sql, key );
    }

    public Document getData( Table table, String sql, int paramValue, ElementProcessor[] elementProcessors )
    {
        return getData( table, sql, new int[]{paramValue}, elementProcessors );
    }

    public Document getData( Table table, String sql, int[] paramValues, ElementProcessor[] elementProcessors )
    {
        Document doc = null;
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

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
            doc = XDG.resultSetToXML( table, resultSet, null, elementProcessors, null, -1 );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get data: %t";
            LOG.error( StringUtil.expandString( message, (Object) null,
                                                                                                 sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return doc;
    }

    public void cascadeDelete( Table table, int key )
    {
        ForeignKeyColumn[] deleteForeignKeys = table.getReferencedKeys( true );
        if ( deleteForeignKeys != null && deleteForeignKeys.length > 0 )
        {
            for ( ForeignKeyColumn deleteForeignKey : deleteForeignKeys )
            {
                Table referrerTable = deleteForeignKey.getTable();
                StringBuffer sql = XDG.generateRemoveSQL( referrerTable, deleteForeignKey );
                executeSQL( sql.toString(), key );
            }
        }
    }

    public void cascadeDelete( Table table, String key )
    {
        ForeignKeyColumn[] deleteForeignKeys = table.getReferencedKeys( true );
        if ( deleteForeignKeys != null && deleteForeignKeys.length > 0 )
        {
            for ( ForeignKeyColumn deleteForeignKey : deleteForeignKeys )
            {
                Table referrerTable = deleteForeignKey.getTable();
                StringBuffer sql = XDG.generateRemoveSQL( referrerTable, deleteForeignKey );
                executeSQL( sql.toString(), new Object[]{key} );
            }
        }
    }


}
