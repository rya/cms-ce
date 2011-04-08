/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.filters.Filter;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;

public class UnitHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( UnitHandler.class.getName() );

    // Unit SQL

    private final static String UNI_TABLE = "tUnit";

    private final static String UNI_SELECT = "SELECT uni_lKey,uni_lan_lKey,lan_sDescription,uni_sName,uni_sDescription," +
        "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp,cat_lKey,cat_sName" + " FROM tUnit" + " JOIN tLanguage ON uni_lan_lKey=lan_lKey" +
        " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" + " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL";

    private final static String UNI_SELECT_LANGUAGEKEY = "SELECT uni_lan_lKey FROM tUnit WHERE uni_lKey=?";

    private final static String UNI_SELECT_NAME =
        "SELECT uni_lKey, uni_sName, cat_sName, cat_lKey, lan_lKey, lan_sDescription, lan_sCode" + " FROM tUnit" +
            " JOIN tLanguage ON tLanguage.lan_lKey = tUnit.uni_lan_lKey" + " JOIN tCategory ON tCategory.cat_uni_lKey = tUnit.uni_lKey" +
            " WHERE (uni_bDeleted=0) AND cat_cat_lSuper IS NULL ";

    private final static String UNI_INSERT =
        "INSERT INTO tUnit" + " (uni_lKey,uni_lan_lKey,uni_sName,uni_sDescription," + "uni_lSuperKey,uni_bDeleted,uni_dteTimestamp)" +
            " VALUES (?,?,?,?,?,?," + "@currentTimestamp@" + ")";

    private final static String UNI_UPDATE =
        "UPDATE tUnit" + " SET uni_lan_lKey=?" + ",uni_sName=?" + ",uni_sDescription=?" + ",uni_lSuperKey=?" + ",uni_dteTimestamp=" +
            "@currentTimestamp@" + " WHERE uni_lKey=?";

    private final static String UNI_DELETE = "UPDATE tUnit SET uni_bDeleted = ?";

    private final static String UNI_WHERE_CLAUSE = " uni_lKey=?";

    private final static String UNI_DEFAULT_ORDER_BY = "name ASC";

    private final static String WHERE = " WHERE ";

    private static final Map<String, String> orderByMap;

    static
    {
        orderByMap = new HashMap<String, String>();

        orderByMap.put( "key", "uni_lKey" );
        orderByMap.put( "name", "uni_sName" );
        orderByMap.put( "description", "uni_sDescription" );
        orderByMap.put( "timestamp", "uni_dteTimeStamp" );
    }

    /**
     * @param xmlData String
     * @return int
     * @throws VerticalCreateException The exception description.
     */
    public int createUnit( String xmlData )
        throws VerticalCreateException
    {

        int key = -1;
        Document doc = XMLTool.domparse( xmlData, "unit" );
        Element root = doc.getDocumentElement();
        Map subelems = XMLTool.filterElements( root.getChildNodes() );

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( UNI_INSERT );

            // get the foreign keys
            // int sitekey = Integer.parseInt(root.getAttribute("sitekey"));
            int superkey = -1;
            String keyStr = root.getAttribute( "superkey" );
            if ( keyStr.length() > 0 )
            {
                superkey = Integer.parseInt( keyStr );
            }
            int languageKey = Integer.parseInt( root.getAttribute( "languagekey" ) );

            // attribute: key
            key = getNextKey( UNI_TABLE );
            preparedStmt.setInt( 1, key );

            // attribute: superkey
            if ( superkey >= 0 )
            {
                preparedStmt.setInt( 5, superkey );
            }
            else
            {
                preparedStmt.setNull( 5, Types.INTEGER );
            }

            // attribute: languagekey
            preparedStmt.setInt( 2, languageKey );

            // element: name
            Element subelem = (Element) subelems.get( "name" );
            String name = XMLTool.getElementText( subelem );
            preparedStmt.setString( 3, name );

            // element: description
            subelem = (Element) subelems.get( "description" );
            if ( subelem != null )
            {
                String description = XMLTool.getElementText( subelem );

                if ( description == null )
                {
                    preparedStmt.setNull( 4, Types.VARCHAR );
                }
                else
                {
                    StringReader sr = new StringReader( description );
                    preparedStmt.setCharacterStream( 4, sr, description.length() );
                }
            }
            else
            {
                preparedStmt.setNull( 4, Types.VARCHAR );
            }

            // mark as not deleted
            preparedStmt.setBoolean( 6, false );

            // element: timestamp (using the database timestamp at update)
            /* no code */

            // add the unit
            preparedStmt.executeUpdate();

            // Set content types
            Element[] contentTypeElems = XMLTool.getElements( XMLTool.getElement( root, "contenttypes" ) );
            int[] contentTypeKeys = new int[contentTypeElems.length];
            for ( int j = 0; j < contentTypeElems.length; j++ )
            {
                contentTypeKeys[j] = Integer.parseInt( contentTypeElems[j].getAttribute( "key" ) );
            }
            setUnitContentTypes( key, contentTypeKeys );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "Failed to create units: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, vue ), vue );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create units: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a unit key: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, nfe ), nfe );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed to generate unit key: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                            StringUtil.expandString( message, (Object) null, gke ), gke );
        }

        finally
        {
            close( preparedStmt );
            close( con );
        }

        return key;
    }

    /**
     * @param orderBy String
     * @return String
     */
    private String generateOrderBySql( String orderBy )
    {

        if ( orderBy == null )
        {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer( orderBy, "," );
        StringBuffer orderBySql = new StringBuffer( " ORDER BY " );
        int count = 0;

        while ( tokenizer.hasMoreTokens() )
        {
            String token = tokenizer.nextToken().trim();
            int spaceIdx = token.indexOf( ' ' );
            String attribute, ordering;
            if ( spaceIdx > 0 )
            {
                attribute = token.substring( 0, spaceIdx );
                ordering = token.substring( spaceIdx + 1, token.length() ).toUpperCase();
                if ( !"ASC".equals( ordering ) && !"DESC".equals( ordering ) )
                {
                    ordering = "ASC";
                }
            }
            else
            {
                attribute = token;
                ordering = "ASC";
            }

            String column = orderByMap.get( attribute );
            if ( column != null )
            {
                if ( ++count > 1 )
                {
                    orderBySql.append( ',' );
                }
                orderBySql.append( column );
                orderBySql.append( ' ' );
                orderBySql.append( ordering );
            }
        }

        if ( count > 0 )
        {
            return orderBySql.toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * @param unitKey int
     * @return String
     */
    public String getUnit( int unitKey )
    {

        StringBuffer sql = new StringBuffer( UNI_SELECT );
        sql.append( " AND" );
        sql.append( UNI_WHERE_CLAUSE );
        int[] paramValue = {unitKey};

        return getUnit( sql.toString(), null, paramValue );
    }

    /**
     * @param sql        String
     * @param orderBy    String
     * @param paramValue int[]
     * @return String
     */
    private String getUnit( String sql, String orderBy, int[] paramValue )
    {

        Document doc = getUnitDOM( sql, orderBy, paramValue );
        return XMLTool.documentToString( doc );
    }

    private Document getUnitDOM( String sql, String orderBy, int[] paramValue )
    {

        if ( orderBy == null )
        {
            orderBy = UNI_DEFAULT_ORDER_BY;
        }

        String orderBySql = generateOrderBySql( orderBy );
        if ( orderBySql != null )
        {
            sql += orderBySql;
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = null;

        try
        {
            doc = XMLTool.createDocument( "units" );
            Element root = doc.getDocumentElement();

            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            int length = ( paramValue != null ? paramValue.length : 0 );
            for ( int i = 0; i < length; i++ )
            {
                preparedStmt.setInt( i + 1, paramValue[i] );
            }
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                int unitkey = resultSet.getInt( "uni_lKey" );
                // int sitekey = resultSet.getInt("uni_sit_lKey");

                Element elem = XMLTool.createElement( doc, root, "unit" );
                elem.setAttribute( "key", Integer.toString( unitkey ) );
                // elem.setAttribute("sitekey", Integer.toString(sitekey));
                String superkey = resultSet.getString( "uni_lSuperKey" );
                if ( !resultSet.wasNull() )
                {
                    elem.setAttribute( "superkey", superkey );
                }
                elem.setAttribute( "languagekey", resultSet.getString( "uni_lan_lKey" ) );
                elem.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                elem.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                elem.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );

                // sub-elements
                XMLTool.createElement( doc, elem, "name", resultSet.getString( "uni_sName" ) );
                String description = resultSet.getString( "uni_sDescription" );
                if ( !resultSet.wasNull() )
                {
                    XMLTool.createElement( doc, elem, "description", description );
                }

                Timestamp timestamp = resultSet.getTimestamp( "uni_dteTimestamp" );
                XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( timestamp, true ) );

                Element ctyElem = XMLTool.createElement( doc, elem, "contenttypes" );
                int[] contentTypeKeys = getUnitContentTypes( unitkey );
                for ( int contentTypeKey : contentTypeKeys )
                {
                    XMLTool.createElement( doc, ctyElem, "contenttype" ).setAttribute( "key", String.valueOf( contentTypeKey ) );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get units: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;
    }

    public String getUnitName( int unitKey )
    {

        String unitName = null;

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            StringBuffer sql = new StringBuffer( UNI_SELECT_NAME );
            sql.append( " AND" );
            sql.append( UNI_WHERE_CLAUSE );
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, unitKey );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                unitName = resultSet.getString( "uni_sName" );
            }

            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get unit's name: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return unitName;
    }

    private String getUnitNamesXML( String orderBy, Filter filter )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc;

        try
        {
            doc = XMLTool.createDocument( "unitnames" );
            Element root = doc.getDocumentElement();

            StringBuffer sql = new StringBuffer( UNI_SELECT_NAME );

            if ( orderBy == null )
            {
                orderBy = UNI_DEFAULT_ORDER_BY;
            }

            String orderBySql = generateOrderBySql( orderBy );
            if ( orderBySql != null )
            {
                sql.append( orderBySql );
            }

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                if ( filter != null && filter.filter( baseEngine, resultSet ) )
                {
                    continue;
                }

                String siteName = resultSet.getString( "uni_sName" );
                Element unitname = XMLTool.createElement( doc, root, "unitname", siteName );
                unitname.setAttribute( "key", resultSet.getString( "uni_lKey" ) );
                unitname.setAttribute( "categoryname", resultSet.getString( "cat_sName" ) );
                unitname.setAttribute( "categorykey", resultSet.getString( "cat_lKey" ) );
                unitname.setAttribute( "languagekey", resultSet.getString( "lan_lKey" ) );
                unitname.setAttribute( "language", resultSet.getString( "lan_sDescription" ) );
                unitname.setAttribute( "languagecode", resultSet.getString( "lan_sCode" ) );
            }

            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get unit names: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            doc = null;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return XMLTool.documentToString( doc );
    }

    public String getUnits( String orderBy )
    {
        StringBuffer sql = new StringBuffer( UNI_SELECT );
        return getUnit( sql.toString(), orderBy, null );
    }

    public String getUnits()
    {
        return getUnits( null );
    }

    public void removeUnit( int unitKey )
        throws VerticalRemoveException
    {

        try
        {
            String unit = getUnit( unitKey );
            String categoryKey = JDOMUtil.evaluateSingleXPathValueAsString( "/units/unit/@categorykey", JDOMUtil.parseDocument( unit ) );
            CategoryEntity categoryEntity = categoryDao.findByKey( new CategoryKey( Integer.valueOf( categoryKey ) ) );

            List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( categoryEntity );

            if ( categoryEntity.hasChildren() || contentKeys.size() > 0 )
            {
                throw new VerticalRemoveException(
                    "Unable to remove unit with key " + unitKey + " : Category " + categoryEntity.getName() + " (" +
                        categoryEntity.getKey().toString() + ") contains sub-categories and/or content" );
            }
        }
        catch ( JDOMException e )
        {
            throw new VerticalRemoveException( e );
        }
        catch ( IOException e )
        {
            throw new VerticalRemoveException( e );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;

        getCommonHandler().cascadeDelete( db.tUnit, unitKey );

        try
        {
            StringBuffer sql = new StringBuffer( UNI_DELETE );
            sql.append( WHERE );
            sql.append( UNI_WHERE_CLAUSE );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setBoolean( 1, true );
            preparedStmt.setInt( 2, unitKey );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove unit: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalRemoveException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updateUnit( String xmlData )
        throws VerticalUpdateException
    {

        Document doc = XMLTool.domparse( xmlData, "unit" );
        updateUnit( doc );
    }

    public void updateUnit( Document unitDoc )
        throws VerticalUpdateException
    {

        // XML DOM
        Element root = unitDoc.getDocumentElement();

        // get the unit's sub-elements
        Map subelems = XMLTool.filterElements( root.getChildNodes() );

        // connection variables
        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            // get the keys
            int unitkey = Integer.parseInt( root.getAttribute( "key" ) );
            // int sitekey = Integer.parseInt(root.getAttribute("sitekey"));
            int superkey = -1;
            String key = root.getAttribute( "superkey" );
            if ( key.length() > 0 )
            {
                superkey = Integer.parseInt( key );
            }
            int languageKey = Integer.parseInt( root.getAttribute( "languagekey" ) );

            con = getConnection();
            preparedStmt = con.prepareStatement( UNI_UPDATE );

            // attribute: key
            preparedStmt.setInt( 5, unitkey );

            // attribute: superkey
            if ( superkey >= 0 )
            {
                preparedStmt.setInt( 4, superkey );
            }
            else
            {
                preparedStmt.setNull( 4, Types.INTEGER );
            }

            // attribute: languagekey
            preparedStmt.setInt( 1, languageKey );

            // element: name
            Element subelem = (Element) subelems.get( "name" );
            String name = XMLTool.getElementText( subelem );
            preparedStmt.setString( 2, name );

            // element: description
            subelem = (Element) subelems.get( "description" );
            if ( subelem != null )
            {
                String description = XMLTool.getElementText( subelem );
                if ( description == null )
                {
                    preparedStmt.setNull( 3, Types.VARCHAR );
                }
                else
                {
                    StringReader sr = new StringReader( description );
                    preparedStmt.setCharacterStream( 3, sr, description.length() );
                }
            }
            else
            {
                preparedStmt.setNull( 3, Types.VARCHAR );
            }

            // element: timestamp (using the database timestamp at update)
            /* no code */

            // update the unit
            preparedStmt.executeUpdate();

            // Set content types
            Element[] contentTypeElems = XMLTool.getElements( XMLTool.getElement( root, "contenttypes" ) );
            int[] contentTypeKeys = new int[contentTypeElems.length];
            for ( int i = 0; i < contentTypeElems.length; i++ )
            {
                contentTypeKeys[i] = Integer.parseInt( contentTypeElems[i].getAttribute( "key" ) );
            }
            setUnitContentTypes( unitkey, contentTypeKeys );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update unit: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a unit key: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, nfe ), nfe );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void setUnitContentTypes( int unitKey, int[] contentTypeKeys )
        throws VerticalUpdateException
    {
        // First remove the old content type settings
        StringBuffer sql = XDG.generateRemoveSQL( db.tUnitContentType, db.tUnitContentType.uct_uni_lKey );
        getCommonHandler().executeSQL( sql.toString(), unitKey );

        // Set the new content types
        if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
        {
            for ( int contentTypeKey : contentTypeKeys )
            {
                sql = XDG.generateInsertSQL( db.tUnitContentType );
                getCommonHandler().executeSQL( sql.toString(), new int[]{unitKey, contentTypeKey} );
            }
        }
    }

    public int[] getUnitContentTypes( int unitKey )
    {
        // First remove the old content type settings
        StringBuffer sql =
            XDG.generateSelectSQL( db.tUnitContentType, db.tUnitContentType.uct_cty_lKey, true, db.tUnitContentType.uct_uni_lKey );
        return getCommonHandler().getIntArray( sql.toString(), new int[]{unitKey} );
    }

    public int getUnitLanguageKey( int unitKey )
    {

        int key = -1;
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( UNI_SELECT_LANGUAGEKEY );
            preparedStmt.setInt( 1, unitKey );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                key = resultSet.getInt( "uni_lan_lKey" );
            }
        }
        catch ( SQLException sqle )
        {
            String MESSAGE_00 = "Failed to get unit's language key: %t";
            LOG.error( StringUtil.expandString( MESSAGE_00, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return key;
    }

    public String getUnitNamesXML( Filter filter )
    {
        return getUnitNamesXML( null, filter );
    }

}
