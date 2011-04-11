/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalException;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalUpdateException;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.structure.RunAsType;

public final class ContentObjectHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentObjectHandler.class.getName() );


    /**
     * Content object select sql.
     */
    private static final String COB_GET =
        "SELECT cob_lKey, cob_men_lKey, cob_sStyle, cob_sBorder, cob_sName, cob_xmlData, cob_dteTimestamp, cob_lRunAs" +
            " FROM tContentObject";

    private static final String COB_WHERE_MEN = " cob_men_lKey=?";

    private static final String COB_REMOVE_BY_MENU = "DELETE FROM tContentObject WHERE cob_men_lKey = ?";

    private static final String COB_TABLE = "tContentObject";

    private static final String COB_CREATE =
        "INSERT INTO tContentObject (cob_lKey,cob_men_lKey,cob_sStyle,cob_sBorder,cob_sName,cob_xmlData,cob_dteTimestamp,cob_lRunAs)" +
            " VALUES (?,?,?,?,?,?,@currentTimestamp@,?)";

    private static final Pattern CATEGORY_KEYLIST_PATTERN = Pattern.compile( "(\\d+)(,(\\d+))*" );

    /**
     * Update content object sql.
     */
    private static final String COB_UPDATE =
        "UPDATE tContentObject SET " + "cob_men_lKey = ?, " + "cob_sStyle = ?, " + "cob_sBorder = ?, " + "cob_sName = ?, " +
            "cob_xmlData = ?, " + "cob_dteTimestamp = @currentTimestamp@, " + "cob_lRunAs = ? " + " WHERE cob_lKey = ?";


    public int[] createContentObject( CopyContext copyContext, Document doc, boolean useOldKey )
        throws VerticalCreateException
    {

        Element docElem = doc.getDocumentElement();
        Element[] contentobjectElems;
        if ( "contentobject".equals( docElem.getTagName() ) )
        {
            contentobjectElems = new Element[]{docElem};
        }
        else
        {
            contentobjectElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        int pos = 0;
        String tmpStr = null;
        TIntArrayList newKeys = new TIntArrayList();

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( COB_CREATE );

            for ( Element root : contentobjectElems )
            {
                Map subelems = XMLTool.filterElements( root.getChildNodes() );

                int key, menuKey = -1;
                String styleSheetKey = "", borderStyleSheetKey = "";

                pos = 0;
                String keyStr = root.getAttribute( "key" );
                if ( !useOldKey || tmpStr == null || tmpStr.length() == 0 )
                {
                    key = getNextKey( COB_TABLE );
                }
                else
                {
                    key = Integer.parseInt( tmpStr );
                }
                if ( copyContext != null )
                {
                    copyContext.putContentObjectKey( Integer.parseInt( keyStr ), key );
                }
                newKeys.add( key );

                pos++;
                // was sitekey

                pos++;
                tmpStr = root.getAttribute( "menukey" );
                if ( tmpStr != null && tmpStr.length() > 0 )
                {
                    menuKey = Integer.parseInt( tmpStr );
                }
                else
                {
                    String message = "No menu key specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                pos++;
                Element subelem = (Element) subelems.get( "objectstylesheet" );
                if ( subelem != null )
                {
                    tmpStr = subelem.getAttribute( "key" );
                    if ( tmpStr != null && tmpStr.length() > 0 )
                    {
                        styleSheetKey = tmpStr;
                    }
                    else
                    {
                        String message = "No object stylesheet key specified.";

                        VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                        StringUtil.expandString( message, (Object) null, null ) );
                    }
                }
                else
                {
                    String message = "No object stylesheet specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                pos++;
                subelem = (Element) subelems.get( "borderstylesheet" );
                if ( subelem != null )
                {
                    tmpStr = subelem.getAttribute( "key" );
                    if ( tmpStr != null && tmpStr.length() > 0 )
                    {
                        borderStyleSheetKey = tmpStr;
                    }
                }

                String name = null;
                byte[] contentobjectdata;

                // element: name
                subelem = (Element) subelems.get( "name" );
                if ( subelem != null )
                {
                    name = XMLTool.getElementText( subelem );
                    if ( name == null || name.length() == 0 )
                    {
                        String message = "Empty stylesheet name.";

                        VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                        StringUtil.expandString( message, (Object) null, null ) );
                    }
                }
                else
                {
                    String message = "No stylesheet name specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                // element: contentobjectdata (optional)
                subelem = (Element) subelems.get( "contentobjectdata" );
                if ( subelem != null )
                {
                    Document codDoc = XMLTool.createDocument();
                    codDoc.appendChild( codDoc.importNode( subelem, true ) );
                    contentobjectdata = XMLTool.documentToBytes( codDoc, "UTF-8" );
                }
                else
                {
                    contentobjectdata = null;
                }

                preparedStmt.setInt( 1, key );
                preparedStmt.setInt( 2, menuKey );
                preparedStmt.setString( 3, styleSheetKey );
                if ( borderStyleSheetKey.length() > 0 )
                {
                    preparedStmt.setString( 4, borderStyleSheetKey );
                }
                else
                {
                    preparedStmt.setNull( 4, Types.VARCHAR );
                }
                preparedStmt.setString( 5, name );
                if ( contentobjectdata != null )
                {
                    preparedStmt.setBinaryStream( 6, new ByteArrayInputStream( contentobjectdata ), contentobjectdata.length );
                }
                else
                {
                    preparedStmt.setNull( 6, Types.VARBINARY );
                }

                RunAsType runAs = RunAsType.INHERIT;
                String runAsStr = root.getAttribute( "runAs" );
                if ( StringUtils.isNotEmpty( runAsStr ) )
                {
                    runAs = RunAsType.valueOf( runAsStr );
                }
                preparedStmt.setInt( 7, runAs.getKey() );

                // create content object
                int result = preparedStmt.executeUpdate();
                if ( result <= 0 )
                {
                    String message = "Failed to create content object, no content object created.";

                    VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }
            }

            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create content object(s): %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse %0: %1";
            Object[] msgData;
            switch ( pos )
            {
                case 1:
                    msgData = new Object[]{"site key", tmpStr};
                    break;
                case 2:
                    msgData = new Object[]{"menu key", tmpStr};
                    break;
                case 3:
                    msgData = new Object[]{"object stylesheet key", tmpStr};
                    break;
                case 4:
                    msgData = new Object[]{"border stylesheet key", tmpStr};
                    break;
                default:
                    msgData = new Object[]{"content object key", tmpStr};
            }

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, msgData, nfe ), nfe );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed to generate content object key";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, gke ), gke );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return newKeys.toArray();
    }

    private Document getContentObject( String sql, int[] paramValue )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Document doc = XMLTool.createDocument( "contentobjects" );

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            int length = ( paramValue != null ? paramValue.length : 0 );
            for ( int i = 0; i < length; i++ )
            {
                preparedStmt.setInt( i + 1, paramValue[i] );
            }

            resultSet = preparedStmt.executeQuery();
            Element root = doc.getDocumentElement();
            while ( resultSet.next() )
            {

                // pre-fetch content object data
                Document contentdata = XMLTool.domparse( resultSet.getBinaryStream( "cob_xmlData" ) );

                Element elem = XMLTool.createElement( doc, root, "contentobject" );
                elem.setAttribute( "key", resultSet.getString( "cob_lKey" ) );
                elem.setAttribute( "menukey", resultSet.getString( "cob_men_lKey" ) );

                String style = resultSet.getString( "cob_sStyle" );
                Element subelem = XMLTool.createElement( doc, elem, "objectstylesheet", style );
                subelem.setAttribute( "key", style );
                boolean styleExist = false;
                if ( style != null && style.length() > 0 )
                {
                    styleExist = null != resourceDao.getResourceFile( new ResourceKey( style ) );
                }
                subelem.setAttribute( "exists", styleExist ? "true" : "false" );
                resultSet.getInt( "cob_men_lKey" );
                if ( resultSet.wasNull() )
                {
                    subelem.setAttribute( "shared", "true" );
                }

                String border = resultSet.getString( "cob_sBorder" );
                if ( !resultSet.wasNull() )
                {
                    subelem = XMLTool.createElement( doc, elem, "borderstylesheet", border );
                    subelem.setAttribute( "key", border );
                    boolean borderExist = false;
                    if ( border != null && border.length() > 0 )
                    {
                        borderExist = null != resourceDao.getResourceFile( new ResourceKey( border ) );
                    }
                    subelem.setAttribute( "exists", borderExist ? "true" : "false" );
                    resultSet.getInt( "cob_men_lKey" );
                    if ( resultSet.wasNull() )
                    {
                        subelem.setAttribute( "shared", "true" );
                    }
                }

                // element: name
                XMLTool.createElement( doc, elem, "name", resultSet.getString( "cob_sName" ) );

                // element: contentobjectdata
                Node xmldata_root = doc.importNode( contentdata.getDocumentElement(), true );
                elem.appendChild( xmldata_root );

                Timestamp timestamp = resultSet.getTimestamp( "cob_dteTimestamp" );
                XMLTool.createElement( doc, elem, "timestamp", CalendarUtil.formatTimestamp( timestamp, true ) );

                elem.setAttribute( "runAs", RunAsType.get( resultSet.getInt( "cob_lRunAs" ) ).toString() );
            }

            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get content object(s): %t";
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

    public Document getContentObjectsByMenu( int menuKey )
    {
        StringBuffer sql = new StringBuffer( COB_GET );
        sql.append( " WHERE" );
        sql.append( COB_WHERE_MEN );
        int[] paramValue = {menuKey};

        return getContentObject( sql.toString(), paramValue );
    }

    public void removeContentObjectsByMenu( Connection _con, int menuKey )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            if ( _con == null )
            {
                con = getConnection();
            }
            else
            {
                con = _con;
            }
            preparedStmt = con.prepareStatement( COB_REMOVE_BY_MENU );
            preparedStmt.setInt( 1, menuKey );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove contents: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            if ( _con == null )
            {
                close( con );
            }
        }
    }

    public void updateContentObject( Document doc )
        throws VerticalUpdateException
    {
        Element docElem = doc.getDocumentElement();
        Element[] contentobjectElems;
        if ( "contentobject".equals( docElem.getTagName() ) )
        {
            contentobjectElems = new Element[]{docElem};
        }
        else
        {
            contentobjectElems = XMLTool.getElements( doc.getDocumentElement() );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        int pos = 0;
        String tmpStr = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( COB_UPDATE );

            for ( Element root : contentobjectElems )
            {
                Map subelems = XMLTool.filterElements( root.getChildNodes() );

                int key = -1, menuKey = -1;
                ResourceKey styleSheetKey = null, borderStyleSheetKey = null;

                pos = 0;
                tmpStr = root.getAttribute( "key" );
                if ( tmpStr != null && tmpStr.length() > 0 )
                {
                    key = Integer.parseInt( tmpStr );
                }
                else
                {
                    String message = "No content object key specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null )  );
                }

                pos++;
                // was sitekey

                pos++;
                tmpStr = root.getAttribute( "menukey" );
                if ( tmpStr != null && tmpStr.length() > 0 )
                {
                    menuKey = Integer.parseInt( tmpStr );
                }
                else
                {
                    String message = "No menu key specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                pos++;
                Element subelem = (Element) subelems.get( "objectstylesheet" );
                if ( subelem != null )
                {
                    tmpStr = subelem.getAttribute( "key" );
                    if ( tmpStr != null && tmpStr.length() > 0 )
                    {
                        styleSheetKey = new ResourceKey( tmpStr );
                    }
                    else
                    {
                        String message = "No object stylesheet key specified.";

                        VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                        StringUtil.expandString( message, (Object) null, null ) );
                    }
                }
                else
                {
                    String message = "No object stylesheet specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                pos++;
                subelem = (Element) subelems.get( "borderstylesheet" );
                if ( subelem != null )
                {
                    tmpStr = subelem.getAttribute( "key" );
                    if ( tmpStr != null && tmpStr.length() > 0 )
                    {
                        borderStyleSheetKey = new ResourceKey( tmpStr );
                    }
                }

                // element: name
                String name = null;
                subelem = (Element) subelems.get( "name" );
                if ( subelem != null )
                {
                    name = XMLTool.getElementText( subelem );
                    if ( name == null || name.length() == 0 )
                    {
                        String message = "Empty stylesheet name.";

                        VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                        StringUtil.expandString( message, (Object) null, null ) );
                    }
                }
                else
                {
                    String message = "No stylesheet name specified.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }

                // element: contentobjectdata (optional)
                byte[] contentobjectdata;
                subelem = (Element) subelems.get( "contentobjectdata" );
                if ( subelem != null )
                {
                    Document codDoc = XMLTool.createDocument();
                    codDoc.appendChild( codDoc.importNode( subelem, true ) );
                    contentobjectdata = XMLTool.documentToBytes( codDoc, "UTF-8" );
                }
                else
                {
                    contentobjectdata = null;
                }

                preparedStmt.setInt( 7, key );
                preparedStmt.setInt( 1, menuKey );
                if ( styleSheetKey != null )
                {
                    preparedStmt.setString( 2, styleSheetKey.toString() );
                }
                else
                {
                    preparedStmt.setNull( 2, Types.VARCHAR );
                }
                if ( borderStyleSheetKey != null )
                {
                    preparedStmt.setString( 3, borderStyleSheetKey.toString() );
                }
                else
                {
                    preparedStmt.setNull( 3, Types.VARCHAR );
                }
                preparedStmt.setCharacterStream( 4, new StringReader( name ), name.length() );
                if ( contentobjectdata != null )
                {
                    preparedStmt.setBinaryStream( 5, new ByteArrayInputStream( contentobjectdata ), contentobjectdata.length );
                }
                else
                {
                    preparedStmt.setNull( 5, Types.VARCHAR );
                }

                RunAsType runAs = RunAsType.INHERIT;
                String runAsStr = root.getAttribute( "runAs" );
                if ( StringUtils.isNotEmpty( runAsStr ) )
                {
                    runAs = RunAsType.valueOf( runAsStr );
                }
                preparedStmt.setInt( 6, runAs.getKey() );

                // update content object
                int result = preparedStmt.executeUpdate();
                if ( result <= 0 )
                {
                    String message = "Failed to update content object, no content object updated.";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, (Object) null, null ) );
                }
            }

            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update content object(s): %t";

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse %0: %1";
            Object[] msgData;
            switch ( pos )
            {
                case 1:
                    msgData = new Object[]{"site key", tmpStr};
                    break;
                case 2:
                    msgData = new Object[]{"menu key", tmpStr};
                    break;
                case 3:
                    msgData = new Object[]{"object stylesheet key", tmpStr};
                    break;
                case 4:
                    msgData = new Object[]{"border stylesheet key", tmpStr};
                    break;
                default:
                    msgData = new Object[]{"content object key", tmpStr};
            }
            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( message, msgData, nfe ), nfe );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void copyContentObjects( int oldMenuKey, CopyContext copyContext )
        throws VerticalCopyException
    {
        int newMenuKey = copyContext.getMenuKey( oldMenuKey );

        try
        {
            Document doc = getContentObjectsByMenu( oldMenuKey );
            Element[] contentobjectElems = XMLTool.getElements( doc.getDocumentElement() );
            for ( Element contentobjectElem : contentobjectElems )
            {
                contentobjectElem.setAttribute( "menukey", Integer.toString( newMenuKey ) );

                // objectstylesheet
                Element tempElem = XMLTool.getElement( contentobjectElem, "objectstylesheet" );
                String oldStyleSheetKey = tempElem.getAttribute( "key" );
                if ( oldStyleSheetKey != null && oldStyleSheetKey.length() > 0 )
                {
                    tempElem.setAttribute( "key", oldStyleSheetKey );
                }

                // borderstylesheet
                tempElem = XMLTool.getElement( contentobjectElem, "borderstylesheet" );
                if ( tempElem != null )
                {
                    oldStyleSheetKey = tempElem.getAttribute( "key" );
                    if ( oldStyleSheetKey != null && oldStyleSheetKey.length() > 0 )
                    {
                        tempElem.setAttribute( "key", oldStyleSheetKey );
                    }
                }

                // document
                Node documentNode = XMLTool.selectNode( contentobjectElem, "contentobjectdata/document" );
                if ( !copyContext.isIncludeContents() )
                {
                    XMLTool.removeChildNodes( (Element) documentNode, false );
                }
            }

            createContentObject( copyContext, doc, false );
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to copy content objects: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCopyException.class,
                                            StringUtil.expandString( message, (Object) null, vce ), vce );
        }
    }

    public void copyContentObjectsPostOp( int oldMenuKey, CopyContext copyContext )
        throws VerticalCopyException
    {

        boolean includeContents = copyContext.isIncludeContents();
        int newMenuKey = copyContext.getMenuKey( oldMenuKey );

        try
        {
            Document doc = getContentObjectsByMenu( newMenuKey );
            Element[] contentobjectElems = XMLTool.getElements( doc.getDocumentElement() );

            for ( Element contentobjectElem : contentobjectElems )
            {
                Element codElem = XMLTool.getElement( contentobjectElem, "contentobjectdata" );

                // datasource
                NodeList parameterList = XMLTool.selectNodes( codElem, "datasources/datasource/parameters/parameter" );
                for ( int k = 0; k < parameterList.getLength(); k++ )
                {
                    Element parameterElem = (Element) parameterList.item( k );
                    String name = parameterElem.getAttribute( "name" );
                    if ( "cat".equalsIgnoreCase( name ) )
                    {
                        Text text = (Text) parameterElem.getFirstChild();
                        if ( text != null )
                        {
                            String oldCategoryKeys = text.getData();

                            Matcher m = CATEGORY_KEYLIST_PATTERN.matcher( oldCategoryKeys );
                            if ( m.matches() )
                            {
                                StringBuffer data = translateCategoryKeys( copyContext, oldCategoryKeys );
                                text.setData( data.toString() );
                            }
                        }
                    }
                    else if ( "menu".equalsIgnoreCase( name ) )
                    {
                        Text text = (Text) parameterElem.getFirstChild();
                        if ( text != null )
                        {
                            int oldKey = Integer.parseInt( text.getData() );
                            int newKey = copyContext.getMenuKey( oldKey );
                            if ( newKey >= 0 )
                            {
                                text.setData( String.valueOf( newKey ) );
                            }
                            else
                            {
                                text.setData( String.valueOf( oldKey ) );
                            }
                        }
                    }
                }

                // stylesheet parameters
                NodeList stylesheetparamList = XMLTool.selectNodes( codElem, "stylesheetparams/stylesheetparam" );
                for ( int k = 0; k < stylesheetparamList.getLength(); k++ )
                {
                    Element stylesheetparamElem = (Element) stylesheetparamList.item( k );
                    String type = stylesheetparamElem.getAttribute( "type" );
                    if ( "page".equals( type ) )
                    {
                        Text text = (Text) stylesheetparamElem.getFirstChild();
                        if ( text != null )
                        {
                            String oldMenuItemKey = text.getData();
                            if ( oldMenuItemKey != null && oldMenuItemKey.length() > 0 )
                            {
                                int newMenuItemKey = copyContext.getMenuItemKey( Integer.parseInt( oldMenuItemKey ) );
                                if ( newMenuItemKey >= 0 )
                                {
                                    text.setData( String.valueOf( newMenuItemKey ) );
                                }
                                else
                                {
                                    XMLTool.removeChildNodes( stylesheetparamElem, true );
                                }
                            }
                            else
                            {
                                XMLTool.removeChildNodes( stylesheetparamElem, true );
                            }
                        }
                    }
                }

                // border parameters
                NodeList borderparamList = XMLTool.selectNodes( codElem, "borderparams/borderparam" );
                for ( int k = 0; k < borderparamList.getLength(); k++ )
                {
                    Element borderparamElem = (Element) borderparamList.item( k );
                    String type = borderparamElem.getAttribute( "type" );
                    if ( "page".equals( type ) )
                    {
                        Text text = (Text) borderparamElem.getFirstChild();
                        if ( text != null )
                        {
                            String oldMenuItemKey = text.getData();
                            if ( oldMenuItemKey != null && oldMenuItemKey.length() > 0 )
                            {
                                int newMenuItemKey = copyContext.getMenuItemKey( Integer.parseInt( oldMenuItemKey ) );
                                if ( newMenuItemKey >= 0 )
                                {
                                    text.setData( String.valueOf( newMenuItemKey ) );
                                }
                                else
                                {
                                    XMLTool.removeChildNodes( borderparamElem, true );
                                }
                            }
                            else
                            {
                                XMLTool.removeChildNodes( borderparamElem, true );
                            }
                        }
                    }
                }
            }

            updateContentObject( doc );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "Failed to copy content objects (post operation): %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCopyException.class,
                                            StringUtil.expandString( message, (Object) null, vue ), vue );
        }
    }

    private StringBuffer translateCategoryKeys( CopyContext copyContext, String oldCategoryKeys )
    {
        StringTokenizer st = new StringTokenizer( oldCategoryKeys, " ," );
        StringBuffer data = new StringBuffer( oldCategoryKeys.length() );
        while ( st.hasMoreTokens() )
        {
            int oldCategoryKey = Integer.parseInt( st.nextToken() );
            if ( data.length() > 0 )
            {
                data.append( ',' );
            }
            int newCategoryKey = copyContext.getCategoryKey( oldCategoryKey );
            if ( newCategoryKey >= 0 )
            {
                data.append( newCategoryKey );
            }
            else
            {
                data.append( oldCategoryKey );
            }
        }
        return data;
    }

}
