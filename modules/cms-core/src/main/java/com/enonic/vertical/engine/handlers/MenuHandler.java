/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import com.enonic.esl.sql.JDBCUtil;
import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.adminweb.VerticalAdminLogger;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuGetterSettings;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.VerticalCopyException;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineException;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.criteria.MenuCriteria;
import com.enonic.vertical.engine.criteria.MenuItemCriteria;
import com.enonic.vertical.event.MenuHandlerEvent;
import com.enonic.vertical.event.MenuHandlerListener;
import com.enonic.vertical.event.VerticalEventMulticaster;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.resource.ResourceKey;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserSpecification;
import com.enonic.cms.domain.structure.RunAsType;
import com.enonic.cms.domain.structure.SiteData;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemSpecification;
import com.enonic.cms.domain.structure.menuitem.MenuItemType;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;

public final class MenuHandler
    extends BaseHandler
{
    /**
     * Menu and menu item access rights.
     */
    private final static int AC_READ = 0x01;

    private final static int AC_ANONREAD = 0x02;

    private final static int AC_CREATE = 0x04;

    private final static int AC_UPDATE = 0x08;

    private final static int AC_ADD = 0x10;

    private final static int AC_PUBLISH = 0x20;

    private final static int AC_DELETE = 0x40;

    private final static int AC_ADMIN = 0x80;

    private VerticalEventMulticaster multicaster = new VerticalEventMulticaster();

    public static final String ELEMENT_NAME_MENU_NAME = "menu-name";

    public static final String ELEMENT_NAME_DISPLAY_NAME = "displayname";

    private static final String COLUMN_NAME_DISPLAY_NAME = "mei_sDisplayName";

    private static final String COLUMN_NAME_ALTERNATIVE_NAME = "mei_sSubTitle";

    private static final String ELEMENT_NAME_MENUITEM_NAME = "name";

    public synchronized void addListener( MenuHandlerListener mhl )
    {
        multicaster.add( mhl );
    }

    public Document getMenuItem( User user, int key, boolean withParents, boolean complete, boolean includePageConfig )
    {
        return getMenuItem( user, key, withParents, complete, includePageConfig, false );
    }

    final static private String MENU_TABLE = "tMenu";

    final static private String MENU_ITEM_TABLE = "tMenuItem";

    final static private String MENU_COLS =
        "MEN_LKEY, MEN_DTETIMESTAMP, MEN_MEI_FIRSTPAGE, MEN_MEI_LOGINPAGE, MEN_MEI_ERRORPAGE, MEN_PAT_LKEY, MEN_SNAME, " +
            "MEN_XMLDATA, MEN_LAN_LKEY, LAN_SDESCRIPTION, LAN_SCODE, MEN_SSTATISTICSURL, " + "MEN_USR_HRUNAS, " +
            "USR_SFULLNAME AS USR_SRUNASNAME, USR_SUID AS USR_SRUNASUID, USR_BISDELETED AS USR_BRUNASDELETED";

    final static private String MENU_SELECT =
        "SELECT " + MENU_COLS + " FROM " + MENU_TABLE + " JOIN tLanguage ON " + MENU_TABLE + ".men_lan_lKey = tLanguage.lan_lKey " +
            "LEFT JOIN tUser ON " + MENU_TABLE + ".men_usr_hRunAs = tUser.usr_hKey";

    final static private String MENU_SELECT_BY_KEY =
        "SELECT " + MENU_COLS + " FROM " + MENU_TABLE + " JOIN tLanguage ON tMenu.men_lan_lKey = tLanguage.lan_lKey " +
            "LEFT JOIN tUser ON " + MENU_TABLE + ".men_usr_hRunAs = tUser.usr_hKey" + " WHERE men_lKey = ?";

    final static private String MENU_SELECT_NAME = "SELECT men_sName FROM " + MENU_TABLE + " WHERE men_lKey = ?";

    final static private String MENU_DELETE_WITH_KEY = "DELETE FROM " + MENU_TABLE + " WHERE men_lKey = ?";

    final static private String MENU_PREPARE_DELETE =
        "UPDATE  " + MENU_TABLE + " SET men_mei_firstPage = NULL," + " men_mei_loginPage = NULL," + " men_mei_errorPage = NULL," +
            " men_pat_lKey = NULL" + " WHERE men_lKey = ?";

    final static private String MENU_INSERT = "INSERT INTO " + MENU_TABLE +
        " (men_lKey, men_dteTimestamp, men_mei_firstPage, men_mei_loginPage, men_mei_errorPage, men_pat_lKey, " +
        "men_sName, men_xmlData, men_lan_lKey, men_sStatisticsURL, " + "men_usr_hRunAs)" +
        " VALUES (?, @currentTimestamp@, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    final static private String MENUDATA_UPDATE =
        "UPDATE " + MENU_TABLE + " SET men_sName = ?, men_xmlData = ?," + " men_dteTimestamp = @currentTimestamp@," + " men_lan_lKey = ?," +
            " men_sStatisticsURL = ?," + " men_usr_hRunAs = ? WHERE men_lKey = ?";

    final static private String MENU_UPDATE =
        "UPDATE " + MENU_TABLE + " SET men_mei_firstPage = ? , men_mei_loginPage = ? , men_mei_errorPage = ? ," + " men_pat_lKey = ?  ," +
            " men_dteTimestamp = @currentTimestamp@ ," + " men_xmlData = ? ," + " men_lan_lKey = ? ," + " men_sStatisticsURL = ?,  " +
            " men_usr_hRunAs = ? WHERE men_lKey = ?";

    final static private String MENU_ITEM_COLS =
        "mei_lKey, mei_bHidden, mei_mid_lKey, mei_usr_hOwner, mei_usr_hModifier, mei_lOrder, mei_men_lkey, mei_lParent, mei_sName, mei_sDescription, " +
            "mei_sKeywords, " + COLUMN_NAME_ALTERNATIVE_NAME +
            ", mei_xmlData, mei_pag_lKey, mei_sURL, mei_burlopennewwin, lan_lKey, lan_sCode, " +
            "lan_sDescription, mei_dteTimestamp, mei_lRunAs, " + COLUMN_NAME_DISPLAY_NAME;

    final static private String MENU_SELECT_ITEMS =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN " + MENU_TABLE + " ON " + MENU_ITEM_TABLE +
            ".mei_men_lKey = " + MENU_TABLE + ".men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE +
            ".mei_lan_lKey = tLanguage.lan_lKey ";

    final static private String MENU_SELECT_ITEMS_BY_PARENT = MENU_SELECT_ITEMS + "WHERE mei_lParent = ? ";

    final static private String MENU_SELECT_ITEM_KEYS_BY_PARENT = "SELECT mei_lKey FROM tMenuItem WHERE mei_lParent = ? ";

    final static private String MENU_SELECT_ITEMS_BY_MENU_KEY_WO_PARENT =
        MENU_SELECT_ITEMS + "WHERE mei_men_lKey = ? AND mei_lParent IS NULL ";

    final static private String ORDER_BY = " ORDER BY mei_lOrder ";

    final static private String MENU_SELECT_KEYS_BY_MENU_KEY_WO_PARENT =
        "SELECT mei_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_men_lKey = ? AND mei_lParent IS NULL";

    final static private String MENU_SELECT_KEYS_BY_MENU_KEY_AND_PARENT =
        "SELECT mei_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_men_lKey = ? AND mei_lParent = ?";

    final static private String MENU_ITEM_DELETE_WITH_KEY = "DELETE FROM " + MENU_ITEM_TABLE + " WHERE mei_lKey = ?";

    final static private String MENU_ITEM_UPDATE =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_sName = ?," + " mei_lParent = ?," + " mei_lOrder = ?," + " mei_dteTimestamp = " +
            "@currentTimestamp@" + ", " + COLUMN_NAME_ALTERNATIVE_NAME + " = ?," + " mei_bHidden = ?," + " mei_sDescription = ?," +
            " mei_sKeywords = ?," + " mei_lan_lKey = ?," + " mei_usr_hOwner = ?," + " mei_usr_hModifier = ?," + " mei_lRunAs = ?," +
            COLUMN_NAME_DISPLAY_NAME + " = ?," + " mei_xmlData = " + "?" + " WHERE mei_lKey = ?";

    final static private String MENU_ITEM_UPDATE_NO_DATA =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_sName = ?," + " mei_lParent = ?," + " mei_lOrder = ?," + " mei_dteTimestamp = " +
            "@currentTimestamp@" + ", " + COLUMN_NAME_ALTERNATIVE_NAME + " = ?," + " mei_bHidden = ?," + " mei_sDescription = ?," +
            " mei_sKeywords = ?," + " mei_lan_lKey = ?," + " mei_usr_hOwner = ?," + " mei_usr_hModifier = ?, mei_lRunAs = ?" + ", " +
            COLUMN_NAME_DISPLAY_NAME + " = ? " + " WHERE mei_lKey = ?";

    final static private String MENU_ITEM_INSERT =
        "INSERT INTO " + MENU_ITEM_TABLE + " (mei_lKey, mei_sName, mei_men_lKey, mei_mid_lKey, " +
            " mei_lParent, mei_lOrder, mei_dteTimestamp," + " " + COLUMN_NAME_ALTERNATIVE_NAME +
            ", mei_bHidden, mei_sDescription, mei_usr_hOwner," +
            " mei_usr_hModifier, mei_xmlData, mei_sKeywords, mei_lan_lKey, mei_bSection, mei_lRunAs, " + COLUMN_NAME_DISPLAY_NAME + ") " +
            " VALUES (?, ?, ?, ?, ?, ?, " + "@currentTimestamp@" + ", ?, ?, ?, ?, ?, " + "?" + ", ?, ?, 0, ?, ?)";

    final static private String MENU_ITEM_SELECT =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN tMenu ON tMenu.men_lKey = " + MENU_ITEM_TABLE +
            ".mei_men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE + ".mei_lan_lKey = tLanguage.lan_lKey";

    final static private String MENU_ITEM_SELECT_BY_KEY =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN tMenu ON tMenu.men_lKey = " + MENU_ITEM_TABLE +
            ".mei_men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE + ".mei_lan_lKey = tLanguage.lan_lKey" + " WHERE mei_lKey = ?";

//	final static private String MENU_ITEM_SELECT_NAME_BY_KEY =
//		"SELECT mei_sName FROM " + MENU_ITEM_TABLE + " WHERE mei_lKey = ?";

    final static private String MENU_ITEM_URL_UPDATE =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_sURL = ?, mei_burlopennewwin = ? WHERE mei_lKey = ?";

    final static private String MENU_ITEMS_BY_CONTENTOBJECT = "SELECT mei_lKey, mei_pag_lKey FROM tMenuItem WHERE mei_pag_lKey in " +
        "(SELECT pco_pag_lKey FROM tPageConObj WHERE pco_cob_lKey = ?)";

    final static private String MENU_ITEMS_BY_PAGETEMPLATES =
        "SELECT mei_lKey, mei_pag_lKey FROM tMenuItem WHERE mei_pag_lKey in " + "(SELECT pag_lKey FROM tPage WHERE pag_pat_lKey IN (";

    final static private String MENU_ITEM_PAGE_UPDATE_KEY =
        "UPDATE " + MENU_ITEM_TABLE + " SET mei_pag_lKey = ?, mei_mid_lKey = ? WHERE mei_lKey = ?";

    final static String MENU_ITEM_SELECT_CHILDREN = "SELECT mei_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_lParent = ?";

    final static private String MENU_GET_KEY_BY_MENU_ITEM = "SELECT mei_men_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_lKey = ?";

    static private Hashtable<String, Integer> menuItemTypes;

    private void buildDocumentTypeXML( Element menuitemElem, Element documentElem )
    {

        if ( documentElem != null )
        {
            if ( verticalProperties.isStoreXHTMLOn() )
            {
                Node n = documentElem.getFirstChild();
                if ( n != null && n.getNodeType() == Node.CDATA_SECTION_NODE )
                {
                    int menuItemKey = Integer.parseInt( menuitemElem.getAttribute( "key" ) );
                    String menuItemName = XMLTool.getElementText( XMLTool.getElement( menuitemElem, "name" ) );
                    Document doc = menuitemElem.getOwnerDocument();
                    String docString = XMLTool.getElementText( documentElem );
                    documentElem.removeChild( n );
                    XMLTool.createXHTMLNodes( doc, documentElem, docString, true );
                    String menuKey = menuitemElem.getAttribute( "menukey" );
                    VerticalAdminLogger.error( this.getClass(), 0,
                                               "Received invalid XML from database, menukey=" + menuKey + ", menuitem key=" + menuItemKey +
                                                   ", name=" + menuItemName + ". Running Tidy..", null );
                }
                documentElem.setAttribute( "mode", "xhtml" );
            }
            else
            {
                Node n = documentElem.getFirstChild();
                if ( n == null )
                {
                    Document doc = menuitemElem.getOwnerDocument();
                    XMLTool.createCDATASection( doc, menuitemElem, "Scratch document." );
                }
                else if ( n.getNodeType() != Node.CDATA_SECTION_NODE )
                {
                    Document doc = menuitemElem.getOwnerDocument();
                    String docString = XMLTool.serialize( documentElem );
                    XMLTool.createCDATASection( doc, documentElem, docString );
                    VerticalEngineLogger.debug( this.getClass(), 0, "Expected CDATA, found XML. Serialized it.", null );
                }
            }
        }
    }

    private boolean buildMenuItemsXML( User user, ResultSet result, Document doc, Element menuItemsElement, int levels, int tagItem,
                                       boolean complete, boolean includePageConfig, boolean includeHidden, boolean includeTypeSpecificXML,
                                       boolean tagItems )
        throws SQLException
    {
        ArrayList<Element> menuItems = new ArrayList<Element>();
        while ( result.next() )
        {
            Element tmpElem = buildMenuItemXML( doc, menuItemsElement, result, tagItem, complete, includePageConfig, includeHidden,
                                                includeTypeSpecificXML, tagItems, levels );
            if ( tmpElem != null )
            {
                menuItems.add( tmpElem );
            }
        }

        // ok.. that was one more level.
        // decrement the counter.
        --levels;

        if ( menuItems.size() == 0 )
        {
            return false;
        }

        //// build xml for children:
        Iterator<Element> iter = menuItems.iterator();
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            //// build sql statement
            String sql = getSecurityHandler().appendMenuItemSQL( user, MENU_SELECT_ITEMS_BY_PARENT, null ) + ORDER_BY;
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );

            while ( iter.hasNext() )
            {
                Element menuItemElement = iter.next();

                // create menuitems element
                menuItemsElement = XMLTool.createElement( doc, menuItemElement, "menuitems" );
                menuItemsElement.setAttribute( "istop", "no" );

                String tmp = menuItemElement.getAttribute( "key" );
                int parentKey = Integer.parseInt( tmp );

                //// execute sql statement
                preparedStmt.setInt( 1, parentKey );
                resultSet = preparedStmt.executeQuery();

                buildMenuItemsXML( user, resultSet, doc, menuItemsElement, levels, tagItem, complete, includePageConfig, includeHidden,
                                   includeTypeSpecificXML, tagItems );
            }
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return true;
    }

    private boolean hasChild( int parentKey, int key, boolean recursive )
        throws SQLException
    {
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        TIntArrayList keyArray = new TIntArrayList();
        Connection con = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( MENU_SELECT_ITEM_KEYS_BY_PARENT );
            preparedStmt.setInt( 1, parentKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                int currentKey = resultSet.getInt( 1 );
                if ( currentKey == key )
                {
                    return true;
                }
                else
                {
                    keyArray.add( currentKey );
                }
            }

            int arraySize = keyArray.size();
            if ( arraySize == 0 )
            {
                return false;
            }

            if ( recursive )
            {
                for ( int i = 0; i < arraySize; ++i )
                {
                    if ( hasChild( keyArray.get( i ), key, recursive ) )
                    {
                        return true;
                    }
                }
            }
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return false;
    }

    private void tagParents( Element menuItemsElement )
    {
        Node tmpNode = menuItemsElement.getParentNode();
        while ( tmpNode != null && tmpNode.getNodeType() == Node.ELEMENT_NODE && !( (Element) tmpNode ).getTagName().equals( "menu" ) )
        {
            ( (Element) tmpNode ).setAttribute( "path", "true" );
            tmpNode = tmpNode.getParentNode().getParentNode();
        }
    }

    private Element buildMenuItemXML( Document doc, Element menuItemsElement, ResultSet result, int tagItem, boolean complete,
                                      boolean includePageConfig, boolean includeHidden, boolean includeTypeSpecificXML, boolean tagItems,
                                      int levels )
        throws SQLException
    {

        int key = result.getInt( "mei_lKey" );

        // check if menuitem is hidden:
        int hiddenInt = result.getInt( "mei_bHidden" );
        boolean hidden = result.wasNull() || hiddenInt == 1;

        // propagate upwards in the XML and tag parents:
        if ( key == tagItem )
        {
            tagParents( menuItemsElement );
        }

        // simply return null if we don't want to include
        // hidden menuitems:
        if ( ( !includeHidden && hidden ) || levels == 0 )
        {
            // special case: if includeHidden is false, we must
            // check to see if the menuitem that is to be tagged
            // is a child of this menuitem
            if ( tagItems )
            {
                if ( tagItem != -1 && tagItem != key )
                {
                    if ( hasChild( key, tagItem, true ) )
                    {
                        tagParents( menuItemsElement );

                        if ( hidden )
                        {
                            Node n = menuItemsElement.getParentNode();
                            if ( n.getNodeType() == Node.ELEMENT_NODE )
                            {
                                ( (Element) n ).setAttribute( "active", "true" );
                            }
                        }
                    }
                }
                else if ( tagItem == key )
                {
                    Node n = menuItemsElement.getParentNode();
                    if ( n.getNodeType() == Node.ELEMENT_NODE )
                    {
                        ( (Element) n ).setAttribute( "active", "true" );
                    }
                }
            }
            return null;
        }

        ////// + build xml for menu item
        Element menuItemElement = XMLTool.createElement( doc, menuItemsElement, "menuitem" );
        menuItemElement.setAttribute( "key", String.valueOf( key ) );

        // tag the menuitem?
        if ( tagItem == key && !hidden )
        {
            menuItemElement.setAttribute( "path", "true" );
            menuItemElement.setAttribute( "active", "true" );
        }

        // attribute: owner
        menuItemElement.setAttribute( "owner", result.getString( "mei_usr_hOwner" ) );

        // attribute: modifier
        menuItemElement.setAttribute( "modifier", result.getString( "mei_usr_hModifier" ) );

        // attribute: order
        menuItemElement.setAttribute( "order", result.getString( "mei_lOrder" ) );

        // Add timestamp attribute
        menuItemElement.setAttribute( "timestamp", CalendarUtil.formatTimestamp( result.getTimestamp( "mei_dteTimestamp" ) ) );

        // attribute: language
        int lanKey = result.getInt( "lan_lKey" );
        String lanCode = result.getString( "lan_sCode" );
        String lanDesc = result.getString( "lan_sDescription" );
        if ( lanDesc != null )
        {
            menuItemElement.setAttribute( "languagekey", String.valueOf( lanKey ) );
            menuItemElement.setAttribute( "languagecode", lanCode );
            menuItemElement.setAttribute( "language", lanDesc );
        }

        // attribute menykey:
        int menuKey = result.getInt( "mei_men_lkey" );
        if ( !result.wasNull() )
        {
            menuItemElement.setAttribute( "menukey", String.valueOf( menuKey ) );
        }

        // attribute parent:
        int parentKey = result.getInt( "mei_lParent" );
        if ( !result.wasNull() )
        {
            menuItemElement.setAttribute( "parent", String.valueOf( parentKey ) );
        }

        // element: name
        XMLTool.createElement( doc, menuItemElement, "name", result.getString( "mei_sName" ) );

        // display-name
        XMLTool.createElement( doc, menuItemElement, ELEMENT_NAME_DISPLAY_NAME, result.getString( COLUMN_NAME_DISPLAY_NAME ) );

        // short-name:
        String tmp = result.getString( COLUMN_NAME_ALTERNATIVE_NAME );
        if ( !result.wasNull() && tmp.length() > 0 )
        {
            XMLTool.createElement( doc, menuItemElement, ELEMENT_NAME_MENU_NAME, tmp );
        }

        menuItemElement.setAttribute( "runAs", RunAsType.get( result.getInt( "mei_lRunAs" ) ).toString() );

        // description:
        String desc = result.getString( "mei_sDescription" );
        if ( !result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "description", desc );
        }
        else
        {
            XMLTool.createElement( doc, menuItemElement, "description" );
        }

        // keywords:
        String keywords = result.getString( "mei_sKeywords" );
        if ( !result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "keywords", keywords );
        }
        else
        {
            XMLTool.createElement( doc, menuItemElement, "keywords" );
        }

        // visibility:
        if ( !hidden )
        {
            menuItemElement.setAttribute( "visible", "yes" );
        }
        else
        {
            menuItemElement.setAttribute( "visible", "no" );
        }

        // contentkey
        int contentKey = getMenuItemContentKey( key );
        if ( contentKey != -1 )
        {
            menuItemElement.setAttribute( "contentkey", String.valueOf( contentKey ) );
        }

        // element menuitemdata:
        InputStream is = result.getBinaryStream( "mei_xmlData" );
        Element documentElem;
        if ( result.wasNull() )
        {
            XMLTool.createElement( doc, menuItemElement, "parameters" );
            documentElem = XMLTool.createElement( doc, "document" );
            if ( complete )
            {
                XMLTool.createElement( doc, menuItemElement, "data" );
            }
        }
        else
        {
            Document dataDoc = XMLTool.domparse( is );
            Element dataElem = (Element) doc.importNode( dataDoc.getDocumentElement(), true );
            Element parametersElem = XMLTool.getElement( dataElem, "parameters" );
            dataElem.removeChild( parametersElem );
            menuItemElement.appendChild( parametersElem );
            if ( complete )
            {
                documentElem = XMLTool.getElement( dataElem, "document" );
                if ( documentElem != null )
                {
                    dataElem.removeChild( documentElem );
                    menuItemElement.appendChild( documentElem );
                }
                menuItemElement.appendChild( dataElem );
            }
            else
            {
                documentElem = XMLTool.createElement( doc, "document" );
            }
        }

        // attribute: menu item type
        MenuItemType menuItemType = MenuItemType.get( result.getInt( "mei_mid_lKey" ) );
        menuItemElement.setAttribute( "type", menuItemType.getName() );

        if ( includeTypeSpecificXML )
        {
            // build type-specific XML:
            switch ( menuItemType )
            {
                case PAGE:
                    buildPageTypeXML( result, doc, menuItemElement, complete && includePageConfig );
                    break;

                case URL:
                    buildURLTypeXML( result, doc, menuItemElement );
                    break;

                case CONTENT:
                    MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( key ) );
                    if ( sectionKey != null )
                    {
                        buildSectionTypeXML( key, menuItemElement );
                    }
                    buildDocumentTypeXML( menuItemElement, documentElem );
                    buildPageTypeXML( result, doc, menuItemElement, complete && includePageConfig );
                    break;

                case LABEL:
                    break;
                case SECTION:
                    buildSectionTypeXML( key, menuItemElement );
                    break;
                case SHORTCUT:
                    buildShortcutTypeXML( key, menuItemElement );
                    break;
            }
        }

        return menuItemElement;
    }

    private void buildPageTypeXML( ResultSet result, Document doc, Element menuItemElement, boolean includePageConfig )
        throws SQLException
    {

        int pKey = result.getInt( "mei_pag_lKey" );

        if ( includePageConfig )
        {
            Document pageDoc = getPageHandler().getPage( pKey, includePageConfig ).getAsDOMDocument();
            Element rootElem = pageDoc.getDocumentElement();
            Element pageElem = XMLTool.getElement( rootElem, "page" );
            menuItemElement.appendChild( menuItemElement.getOwnerDocument().importNode( pageElem, true ) );
        }
        else
        {
            Element pageElem = XMLTool.createElement( doc, menuItemElement, "page" );
            pageElem.setAttribute( "key", String.valueOf( pKey ) );

            // extract pagetemplate key
            PageTemplateKey ptKey = getPageHandler().getPageTemplateKey( pKey );
            pageElem.setAttribute( "pagetemplatekey", ptKey.toString() );
            PageTemplateType pageTemplateType = getPageTemplateHandler().getPageTemplateType( ptKey );
            pageElem.setAttribute( "pagetemplatetype", String.valueOf( pageTemplateType.getKey() ) );
        }
    }

    protected void buildSectionTypeXML( int menuItemKey, Element menuItemElement )
    {
        Document sectionDoc = getSectionHandler().getSectionByMenuItem( menuItemKey );
        XMLTool.mergeDocuments( menuItemElement, sectionDoc, true );
    }

    protected void buildShortcutTypeXML( int menuItemKey, Element menuItemElement )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lKey );
        Object[] columnValues = getCommonHandler().getObjects( sql.toString(), menuItemKey );
        if ( columnValues.length > 0 )
        {
            Element shortcutElem = XMLTool.createElement( menuItemElement.getOwnerDocument(), menuItemElement, "shortcut" );
            Integer shortcut = (Integer) columnValues[19];
            shortcutElem.setAttribute( "key", String.valueOf( shortcut ) );
            final String menuItemName = getMenuItemName( shortcut );
            shortcutElem.setAttribute( "name", menuItemName );
            Integer forward = (Integer) columnValues[20];
            if ( forward == 0 )
            {
                shortcutElem.setAttribute( "forward", "false" );
            }
            else
            {
                shortcutElem.setAttribute( "forward", "true" );
            }

        }
    }

    protected void buildURLTypeXML( ResultSet result, Document doc, Element menuItemElement )
    {

        try
        {
            String url = result.getString( "mei_sURL" );
            Element urlElement = XMLTool.createElement( doc, menuItemElement, "url", url );

            // attribute: newWindow
            int newWindow = result.getInt( "mei_burlopennewwin" );
            String attrValue;
            if ( newWindow == 0 )
            {
                attrValue = "no";
            }
            else
            {
                attrValue = "yes";
            }
            urlElement.setAttribute( "newwindow", attrValue );

        }
        catch ( SQLException sqle )
        {
            System.err.println( "[MenuHandler_DB2Impl:Error] SQL exception." );
        }
    }

    public int createMenu( User user, String xmlData )
        throws VerticalCreateException, VerticalSecurityException
    {
        Document doc = XMLTool.domparse( xmlData, "menu" );
        return createMenu( user, null, doc, true ).toInt();
    }

    private SiteKey createMenu( User user, CopyContext copyContext, Document doc, boolean useOldKey )
        throws VerticalCreateException, VerticalSecurityException
    {

        Element rootElement = doc.getDocumentElement();

        // Get site key:
        String tmp;

        // Prepare insertion into the database
        SiteKey siteKey = null;
        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();

            tmp = rootElement.getAttribute( "key" );
            if ( !useOldKey || tmp == null || tmp.length() == 0 )
            {
                // generate key:
                siteKey = new SiteKey( getNextKey( MENU_TABLE ) );
            }
            else
            {
                siteKey = new SiteKey( tmp );
            }

            preparedStmt = con.prepareStatement( MENU_INSERT );

            if ( copyContext != null )
            {
                copyContext.putMenuKey( Integer.parseInt( tmp ), siteKey.toInt() );
            }
            preparedStmt.setInt( 1, siteKey.toInt() );

            // name:
            Element tmpElement = XMLTool.getElement( rootElement, "name" );
            String name = null;
            if ( tmpElement != null )
            {
                name = XMLTool.getElementText( tmpElement );
            }
            if ( name != null )
            {
                preparedStmt.setString( 6, name );
            }
            else
            {
                preparedStmt.setNull( 6, Types.VARCHAR );
            }

            // firstpage:
            tmpElement = XMLTool.getElement( rootElement, "firstpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 2, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 2, Types.INTEGER );
            }

            // loginpage:
            tmpElement = XMLTool.getElement( rootElement, "loginpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 3, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 3, Types.INTEGER );
            }

            // errorpage:
            tmpElement = XMLTool.getElement( rootElement, "errorpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 4, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 4, Types.INTEGER );
            }

            // default pagetemplate:
            tmpElement = XMLTool.getElement( rootElement, "defaultpagetemplate" );
            if ( tmpElement != null )
            {
                tmp = tmpElement.getAttribute( "pagetemplatekey" );
                if ( tmp != null && tmp.length() > 0 )
                {
                    preparedStmt.setInt( 5, Integer.parseInt( tmp ) );
                }
                else
                {
                    preparedStmt.setNull( 5, Types.INTEGER );
                }
            }
            else
            {
                preparedStmt.setNull( 5, Types.INTEGER );
            }

            SiteData siteData = new SiteData();

            // DeviceClassResolver:
            tmpElement = XMLTool.getElement( rootElement, "deviceclassresolver" );
            if ( tmpElement != null )
            {
                String deviceClassResolverUrl = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( deviceClassResolverUrl ) )
                {
                    siteData.setDeviceClassResolver( new ResourceKey( deviceClassResolverUrl ) );
                }
            }

            // Default localization resource:
            tmpElement = XMLTool.getElement( rootElement, "defaultlocalizationresource" );
            if ( tmpElement != null )
            {
                String defaultLocalizationResource = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( defaultLocalizationResource ) )
                {
                    siteData.setDeviceClassResolver( new ResourceKey( defaultLocalizationResource ) );
                }
            }

            // locale resolver
            tmpElement = XMLTool.getElement( rootElement, "localeresolver" );
            if ( tmpElement != null )
            {
                String localeResolver = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( localeResolver ) )
                {
                    siteData.setLocaleResolver( new ResourceKey( localeResolver ) );
                }
            }

            // Path to public home:
            String pathToPublicHome = rootElement.getAttribute( "path-to-public-home-resources" );
            if ( StringUtils.isNotEmpty( pathToPublicHome ) )
            {
                siteData.setPathToPublicResources( new ResourceKey( pathToPublicHome ) );
            }

            // Path to home:
            String pathToHome = rootElement.getAttribute( "path-to-home-resources" );
            if ( StringUtils.isNotEmpty( pathToHome ) )
            {
                siteData.setPathToResources( new ResourceKey( pathToHome ) );
            }

            tmpElement = XMLTool.getElement( rootElement, "menudata" );
            if ( tmpElement != null )
            {
                parseAndAddMenudataToSiteData( tmpElement, siteData );
            }

            final byte[] xmldata = siteData.getAsBytes();
            preparedStmt.setBinaryStream( 7, new ByteArrayInputStream( xmldata ), xmldata.length );

            // language key:
            preparedStmt.setInt( 8, Integer.parseInt( rootElement.getAttribute( "languagekey" ) ) );

            Element detailsElement = XMLTool.getElement( rootElement, "details" );

            // Statistics URL:
            tmpElement = XMLTool.getElement( detailsElement, "statistics" );
            if ( tmpElement != null )
            {
                String statisticsURL = XMLTool.getElementText( tmpElement );
                if ( statisticsURL != null )
                {
                    preparedStmt.setString( 9, statisticsURL );
                }
                else
                {
                    preparedStmt.setNull( 9, Types.VARCHAR );
                }
            }
            else
            {
                preparedStmt.setNull( 9, Types.VARCHAR );
            }

            // Run As User:
            String runAsUserGroupKey = rootElement.getAttribute( "runas" );
            if ( StringUtils.isNotEmpty( runAsUserGroupKey ) )
            {
                UserKey userKey = getUserKeyFromGroupKey( runAsUserGroupKey );
                preparedStmt.setString( 10, userKey != null ? userKey.toString() : null );
            }
            else
            {
                preparedStmt.setNull( 10, Types.VARCHAR );
            }

            // insert the data:
            preparedStmt.executeUpdate();

            // create default menu access rights
            GroupHandler groupHandler = getGroupHandler();
            String groupKey = groupHandler.getAdminGroupKey();

            Document tmpDoc = XMLTool.createDocument( "accessrights" );
            Element root = tmpDoc.getDocumentElement();
            root.setAttribute( "type", String.valueOf( AccessRight.MENUITEM_DEFAULT ) );
            root.setAttribute( "key", String.valueOf( siteKey ) );

            Element accessrightElem = XMLTool.createElement( tmpDoc, root, "accessright" );
            accessrightElem.setAttribute( "groupkey", groupKey );
            accessrightElem.setAttribute( "grouptype", GroupType.ADMINS.toInteger().toString() );

            accessrightElem.setAttribute( "read", "true" );
            accessrightElem.setAttribute( "create", "true" );
            accessrightElem.setAttribute( "update", "true" );
            accessrightElem.setAttribute( "delete", "true" );
            accessrightElem.setAttribute( "publish", "true" );
            accessrightElem.setAttribute( "add", "true" );
            accessrightElem.setAttribute( "administrate", "true" );

            getSecurityHandler().updateAccessRights( user, tmpDoc );

            //////// Create menuitems:
            Element itemsElement = XMLTool.getElement( rootElement, "menuitems" );
            if ( itemsElement != null )
            {
                Element[] itemElems = XMLTool.getElements( itemsElement );

                for ( int i = 0; i < itemElems.length; i++ )
                {
                    createMenuItem( user, copyContext, itemElems[i], siteKey, i, null, useOldKey );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String msg = "Failed to create menu: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, msg, sqle );
        }
        catch ( VerticalKeyException gke )
        {
            String msg = "Unable to generate key for table %0.";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, msg, MENU_TABLE, gke );
        }
        catch ( VerticalUpdateException e )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 10, "Error creating default access rights: %t", e );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return siteKey;
    }

    public int createMenuItem( User user, String xmlData )
        throws VerticalCreateException, VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData );
        Element menuItemsElement = doc.getDocumentElement();
        Element menuItemElement = XMLTool.getElement( menuItemsElement, "menuitem" );

        // get parent key
        MenuItemKey parentKey = null;
        String tmp = menuItemElement.getAttribute( "parent" );
        if ( tmp != null && tmp.length() > 0 )
        {
            parentKey = new MenuItemKey( tmp );
        }

        SiteKey siteKey = null;
        tmp = menuItemElement.getAttribute( "menukey" );
        if ( tmp != null && tmp.length() > 0 )
        {
            siteKey = new SiteKey( tmp );
        }

        int order;
        tmp = menuItemElement.getAttribute( "order" );
        if ( tmp != null && tmp.length() > 0 )
        {
            order = Integer.parseInt( tmp );
        }
        else
        {
            order = getNextOrder( siteKey == null ? -1 : siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt() );
        }

        return createMenuItem( user, null, menuItemElement, siteKey, order, parentKey, false );
    }

    public int getNextOrder( int menuKey, int parentKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lOrder, false, db.tMenuItem.mei_men_lKey );
        if ( parentKey != -1 )
        {
            XDG.appendWhereSQL( sql, db.tMenuItem.mei_lParent, XDG.OPERATOR_EQUAL, parentKey );
        }

        XDG.appendOrderBySQL( sql, db.tMenuItem.mei_lOrder, false );

        int highestOrder = getCommonHandler().getInt( sql.toString(), menuKey );

        if ( highestOrder == -1 )
        {
            return 0;
        }
        else if ( highestOrder < Integer.MAX_VALUE )
        {
            return highestOrder + 1;
        }
        else
        {
            return Integer.MAX_VALUE;
        }
    }

    private int createMenuItem( User user, CopyContext copyContext, Element menuItemElement, SiteKey siteKey, int order,
                                MenuItemKey parentKey, boolean useOldKey )
        throws VerticalCreateException, VerticalSecurityException
    {

        // security check:
        if ( !getSecurityHandler().validateMenuItemCreate( user, siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt() ) )
        {
            VerticalEngineLogger.errorSecurity( this.getClass(), 10, "Not allowed to create menuitem in this position.", null );
        }

        String menuItemName = XMLTool.getElementText( XMLTool.getElement( menuItemElement, ELEMENT_NAME_MENUITEM_NAME ) );

        if ( StringUtils.isEmpty( menuItemName ) )
        {
            menuItemName = generateMenuItemName( menuItemElement );
        }

        menuItemName = ensureUniqueMenuItemName( siteKey, parentKey, menuItemName, null );

        // check whether name is unique for this parent
        if ( menuItemNameExists( siteKey, parentKey, menuItemName, null ) )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 20, "Menu item name already exists on this level: %0",
                                              new Object[]{menuItemName}, null );
        }

        Element tmp_element;
        Hashtable<String, Integer> menuItemTypes = getMenuItemTypesAsHashtable();

        // Get menuitem type:
        String miType = menuItemElement.getAttribute( "type" );
        Integer type = menuItemTypes.get( miType );
        if ( type == null )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 20, "Invalid menu item type %0.", new Object[]{type}, null );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        MenuItemKey menuItemKey = null;

        try
        {
            con = getConnection();

            // key
            String keyStr = menuItemElement.getAttribute( "key" );
            if ( !useOldKey || keyStr == null || keyStr.length() == 0 )
            {
                try
                {
                    menuItemKey = new MenuItemKey( getNextKey( MENU_ITEM_TABLE ) );
                }
                catch ( VerticalKeyException e )
                {
                    VerticalEngineLogger.errorCreate( this.getClass(), 30, "Error generating key for tMenuItem.", e );
                }
            }
            else
            {
                menuItemKey = new MenuItemKey( keyStr );
            }
            if ( copyContext != null )
            {
                copyContext.putMenuItemKey( Integer.parseInt( keyStr ), menuItemKey.toInt() );
            }

            String tmp;

            preparedStmt = con.prepareStatement( MENU_ITEM_INSERT );

            preparedStmt.setInt( 1, menuItemKey.toInt() );

            // element: name
            validateMenuItemName( menuItemName );
            preparedStmt.setString( 2, menuItemName );

            // menu key:
            preparedStmt.setInt( 3, siteKey.toInt() );

            // attribute: menu item type
            preparedStmt.setInt( 4, type );

            // parent
            if ( parentKey == null )
            {
                preparedStmt.setNull( 5, Types.INTEGER );
            }
            else
            {
                preparedStmt.setInt( 5, parentKey.toInt() );
            }

            // order:
            preparedStmt.setInt( 6, order );

            // pre-fetch data element
            Element dataElem = XMLTool.getElement( menuItemElement, "data" );

            // element: parameters
            tmp_element = XMLTool.getElement( menuItemElement, "parameters" );
            if ( tmp_element != null )
            {
                dataElem.appendChild( tmp_element );
            }

            // alternative name:
            tmp_element = XMLTool.getElement( menuItemElement, ELEMENT_NAME_MENU_NAME );
            if ( tmp_element != null )
            {
                tmp = XMLTool.getElementText( tmp_element );
                preparedStmt.setString( 7, tmp );
            }
            else
            {
                preparedStmt.setNull( 7, Types.VARCHAR );
            }

            // visibility:
            tmp = menuItemElement.getAttribute( "visible" );
            if ( "no".equals( tmp ) )
            {
                preparedStmt.setInt( 8, 1 );
            }
            else
            {
                preparedStmt.setInt( 8, 0 );
            }

            // description:
            tmp_element = XMLTool.getElement( menuItemElement, "description" );
            String data = XMLTool.getElementText( tmp_element );
            if ( data != null )
            {
                StringReader reader = new StringReader( data );
                preparedStmt.setCharacterStream( 9, reader, data.length() );
            }
            else
            {
                preparedStmt.setNull( 9, Types.VARCHAR );
            }

            if ( type == 4 )
            {
                Element docElem = XMLTool.getElement( menuItemElement, "document" );

                if ( docElem != null )
                {
                    dataElem.appendChild( docElem );
                }
            }

            // attribute: owner/modifier
            String ownerKey = menuItemElement.getAttribute( "owner" );
            preparedStmt.setString( 10, ownerKey );
            preparedStmt.setString( 11, ownerKey );

            // data
            if ( dataElem != null )
            {
                Document dataDoc = XMLTool.createDocument();
                dataDoc.appendChild( dataDoc.importNode( dataElem, true ) );

                byte[] bytes = XMLTool.documentToBytes( dataDoc, "UTF-8" );
                preparedStmt.setBinaryStream( 12, new ByteArrayInputStream( bytes ), bytes.length );
            }
            else
            {
                preparedStmt.setNull( 12, Types.BLOB );
            }

            // keywords
            tmp_element = XMLTool.getElement( menuItemElement, "keywords" );
            String keywords = XMLTool.getElementText( tmp_element );
            if ( keywords == null || keywords.length() == 0 )
            {
                preparedStmt.setNull( 13, Types.VARCHAR );
            }
            else
            {
                StringReader keywordReader = new StringReader( keywords );
                preparedStmt.setCharacterStream( 13, keywordReader, keywords.length() );
            }

            // language
            String lanKey = menuItemElement.getAttribute( "languagekey" );
            if ( ( lanKey != null ) && ( lanKey.length() > 0 ) )
            {
                preparedStmt.setInt( 14, Integer.parseInt( lanKey ) );
            }
            else
            {
                preparedStmt.setNull( 14, Types.INTEGER );
            }

            RunAsType runAs = RunAsType.INHERIT;
            String runAsStr = menuItemElement.getAttribute( "runAs" );
            if ( StringUtils.isNotEmpty( runAsStr ) )
            {
                runAs = RunAsType.valueOf( runAsStr );
            }
            preparedStmt.setInt( 15, runAs.getKey() );

            // Display-name
            String displayName = getElementValue( menuItemElement, ELEMENT_NAME_DISPLAY_NAME );
            preparedStmt.setString( 16, displayName );

            // execute statement:
            preparedStmt.executeUpdate();

            // Create type specific data.
            switch ( type )
            {
                case 1:
                    // page
                    createPage( con, menuItemElement, type, menuItemKey );
                    break;

                case 2:
                    // URL
                    createOrUpdateURL( con, menuItemElement, menuItemKey );
                    break;

                case 4:
                    // document: nothing
                    // page
                    Element pageElem = XMLTool.getElement( menuItemElement, "page" );
                    PageTemplateKey pageTemplateKey = new PageTemplateKey( pageElem.getAttribute( "pagetemplatekey" ) );
                    PageTemplateType pageTemplateType = getPageTemplateHandler().getPageTemplateType( pageTemplateKey );
                    if ( pageTemplateType == PageTemplateType.SECTIONPAGE || pageTemplateType == PageTemplateType.NEWSLETTER )
                    {
                        createSection( menuItemElement, menuItemKey );
                    }
                    createPage( con, menuItemElement, type, menuItemKey );
                    break;

                case 5:
                    // label
                    break;

                case 6:
                    // section
                    createSection( menuItemElement, menuItemKey );
                    break;

                case 7:
                    // shortcut
                    createOrOverrideShortcut( menuItemElement, menuItemKey );
                    break;

                default:
                    VerticalEngineLogger.errorCreate( this.getClass(), 70, "Unknown menuitem type: %0", new Object[]{type}, null );
            }

            // set contentkey if present
            String contentKeyStr = menuItemElement.getAttribute( "contentkey" );
            if ( contentKeyStr.length() == 0 )
            {
                contentKeyStr = "-1";
            }
            setMenuItemContentKey( menuItemKey, Integer.parseInt( contentKeyStr ) );

            // fire event
            if ( multicaster.hasListeners() && copyContext == null )
            {
                MenuHandlerEvent e = new MenuHandlerEvent( user, siteKey.toInt(), menuItemKey.toInt(), menuItemName, this );
                multicaster.createdMenuItem( e );
            }

            UserSpecification userSpecification = new UserSpecification();
            userSpecification.setDeletedState( UserSpecification.DeletedState.ANY );
            userSpecification.setKey( new UserKey( ownerKey ) );
            UserEntity owner = userDao.findSingleBySpecification( userSpecification );
            String ownerGroupKey = null;
            if ( owner.getUserGroup() != null )
            {
                ownerGroupKey = owner.getUserGroup().getGroupKey().toString();
            }

            getSecurityHandler().inheritMenuItemAccessRights( siteKey.toInt(), parentKey == null ? -1 : parentKey.toInt(),
                                                              menuItemKey.toInt(), ownerGroupKey );

            // Create other
            Element menuItemsElement = XMLTool.getElement( menuItemElement, "menuitems" );
            if ( menuItemsElement != null )
            {
                Element[] elems = XMLTool.getElements( menuItemsElement );
                for ( int i = 0; i < elems.length; i++ )
                {
                    createMenuItem( user, copyContext, elems[i], siteKey, i, menuItemKey, useOldKey );
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 40, "A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return menuItemKey.toInt();
    }

    private String generateMenuItemName( Element menuItemElement )
    {
        String menuItemName;

        String suggestedName = getElementValue( menuItemElement, ELEMENT_NAME_MENU_NAME );

        if ( StringUtils.isBlank( suggestedName ) )
        {
            suggestedName = getElementValue( menuItemElement, ELEMENT_NAME_DISPLAY_NAME );
        }

        menuItemName = PrettyPathNameCreator.generatePrettyPathName( suggestedName );

        return menuItemName;
    }

    private String ensureUniqueMenuItemName( SiteKey siteKey, MenuItemKey parentKey, String menuItemName, MenuItemKey existingKey )
    {
        int i = 0;

        String baseName = menuItemName;

        while ( true )
        {
            if ( !menuItemNameExists( siteKey, parentKey, menuItemName, existingKey ) )
            {
                return menuItemName;
            }
            else
            {
                i++;
                menuItemName = baseName + "(" + i + ")";
            }

            Assert.isTrue( i < 100, "Not able to generate menuitem-name within 100 attempts to create unique" );
        }
    }


    private String getElementValue( Element menuItemElement, String elementName )
    {
        Element tmp_element;
        String tmp;
        tmp_element = XMLTool.getElement( menuItemElement, elementName );
        tmp = XMLTool.getElementText( tmp_element );
        return tmp;
    }

    public void createOrUpdateURL( Connection con, Element elem, MenuItemKey menuItemKey )
        throws VerticalCreateException
    {

        Element urlElement = XMLTool.getElement( elem, "url" );
        String tmp = urlElement.getAttribute( "newwindow" );
        int bNewWindow = -1;
        if ( "yes".equals( tmp ) )
        {
            bNewWindow = 1;
        }
        else if ( "no".equals( tmp ) )
        {
            bNewWindow = 0;
        }
        else
        {
            String msg = "Please specify 'yes' or 'no' in 'newwindow' attribute.";
            VerticalEngineLogger.errorCreate( this.getClass(), 10, msg, null );
        }

        String url = XMLTool.getElementText( urlElement );

        PreparedStatement preparedStmt = null;
        try
        {
            preparedStmt = con.prepareStatement( MENU_ITEM_URL_UPDATE );

            preparedStmt.setString( 1, url );
            preparedStmt.setInt( 2, bNewWindow );
            preparedStmt.setInt( 3, menuItemKey.toInt() );

            preparedStmt.executeUpdate();
        }
        catch ( VerticalKeyException e )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 30, "Error generating key: %t", e );
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 40, "A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public void updateSection( Element menuItemElem, int sectionKey )
        throws VerticalCreateException, VerticalSecurityException
    {
        Element sectionElem = XMLTool.getElement( menuItemElem, "section" );
        boolean ordered = "true".equals( sectionElem.getAttribute( "ordered" ) );

        Element[] contentTypeElems = XMLTool.selectElements( sectionElem, "contenttypes/contenttype" );
        int[] contentTypes = new int[contentTypeElems.length];
        for ( int i = 0; i < contentTypeElems.length; i++ )
        {
            contentTypes[i] = Integer.parseInt( contentTypeElems[i].getAttribute( "key" ) );
        }

        getSectionHandler().updateSection( sectionKey, ordered, contentTypes );
    }

    public void setMenuItemContentTypes( int menuItemKey, int[] contentTypeKeys )
    {
        try
        {
            MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( menuItemKey ) );
            if ( sectionKey != null )
            {
                getSectionHandler().setContentTypesForSection( sectionKey.toInt(), contentTypeKeys );
            }
        }
        catch ( VerticalCreateException vce )
        {
            String message = "Failed to create section contenttype filter: %t";
            VerticalEngineLogger.error( this.getClass(), 1, message, null );
        }
    }

    private void createSection( Element menuItemElem, MenuItemKey menuItemKey )
        throws VerticalCreateException, VerticalSecurityException
    {
        Element sectionElem = XMLTool.getElement( menuItemElem, "section" );
        boolean ordered = "true".equals( sectionElem.getAttribute( "ordered" ) );

        Element[] contentTypeElems = XMLTool.selectElements( sectionElem, "contenttypes/contenttype" );
        int[] contentTypes = new int[contentTypeElems.length];
        for ( int i = 0; i < contentTypeElems.length; i++ )
        {
            contentTypes[i] = Integer.parseInt( contentTypeElems[i].getAttribute( "key" ) );
        }

        getSectionHandler().createSection( menuItemKey.toInt(), ordered, contentTypes );
    }

    private void createOrOverrideShortcut( Element shortcutDestinationMenuItem, MenuItemKey shortcutMenuItem )
        throws VerticalCreateException
    {
        Element shortcutElem = XMLTool.getElement( shortcutDestinationMenuItem, "shortcut" );
        int shortcut = Integer.parseInt( shortcutElem.getAttribute( "key" ) );
        boolean forward = Boolean.valueOf( shortcutElem.getAttribute( "forward" ) );
        StringBuffer sql =
            XDG.generateUpdateSQL( db.tMenuItem, new Column[]{db.tMenuItem.mei_mei_lShortcut, db.tMenuItem.mei_bShortcutForward},
                                   new Column[]{db.tMenuItem.mei_lKey}, null );
        Connection con = null;
        PreparedStatement prepStmt = null;
        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql.toString() );
            prepStmt.setInt( 1, shortcut );
            prepStmt.setBoolean( 2, forward );
            prepStmt.setInt( 3, shortcutMenuItem.toInt() );
            prepStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create menu item shortcut: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( con );
            close( prepStmt );
        }
    }

    private void removeShortcut( int menuItemKey )
        throws VerticalRemoveException
    {
        StringBuffer sql =
            XDG.generateUpdateSQL( db.tMenuItem, new Column[]{db.tMenuItem.mei_mei_lShortcut, db.tMenuItem.mei_bShortcutForward},
                                   new Column[]{db.tMenuItem.mei_lKey}, null );
        Connection con = null;
        PreparedStatement prepStmt = null;
        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( sql.toString() );
            prepStmt.setNull( 1, Types.INTEGER );
            prepStmt.setNull( 2, Types.INTEGER );
            prepStmt.setInt( 3, menuItemKey );
            prepStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create menu item shortcut: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( con );
            close( prepStmt );
        }
    }


    public void createPage( Connection con, Element elem, int type, MenuItemKey menuItemKey )
        throws VerticalCreateException
    {

        Element pageElem = XMLTool.getElement( elem, "page" );
        int pKey;

        Document pageDoc = XMLTool.createDocument();
        pageDoc.appendChild( pageDoc.importNode( pageElem, true ) );
        pKey = getPageHandler().createPage( XMLTool.documentToString( pageDoc ) );

        PreparedStatement preparedStmt = null;
        try
        {
            preparedStmt = con.prepareStatement( MENU_ITEM_PAGE_UPDATE_KEY );
            preparedStmt.setInt( 1, pKey );
            preparedStmt.setInt( 2, type );
            preparedStmt.setInt( 3, menuItemKey.toInt() );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 10, "A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public int getErrorPage( int key )
    {
        SiteEntity entity = siteDao.findByKey( key );
        if ( ( entity == null ) || ( entity.getErrorPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getErrorPage().getKey();
        }
    }

    public int getLoginPage( int key )
    {
        SiteEntity entity = siteDao.findByKey( key );
        if ( ( entity == null ) || ( entity.getLoginPage() == null ) )
        {
            return -1;
        }
        else
        {
            return entity.getLoginPage().getKey();
        }
    }

    private Document getMenu( User user, int menuKey, int levels, int tagItem, boolean complete, boolean includePageConfig,
                              boolean includeHidden )
    {

        Document doc = XMLTool.createDocument( "menus" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet result = null;
        try
        {
            con = getConnection();

            Element menuElement = getMenuData( doc.getDocumentElement(), menuKey );

            String sql = MENU_SELECT_ITEMS_BY_MENU_KEY_WO_PARENT;
            sql = getSecurityHandler().appendMenuItemSQL( user, sql );
            sql += ORDER_BY;

            preparedStmt = con.prepareStatement( sql );
            preparedStmt.setInt( 1, menuKey );

            result = preparedStmt.executeQuery();

            Element menuItemsElement = XMLTool.createElement( doc, menuElement, "menuitems" );
            menuItemsElement.setAttribute( "istop", "yes" );

            // Make sure levels is set correct:
            if ( levels == 0 )
            {
                levels = -1;
            }

            // Build menu items
            buildMenuItemsXML( user, result, doc, menuItemsElement, levels, tagItem, complete, includePageConfig, includeHidden, true,
                               true );
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( result );
            close( preparedStmt );
            close( con );
        }

        return doc;
    }

    public Document getMenu( User user, int menuKey, boolean complete, boolean includePageConfig )
    {
        return getMenu( user, menuKey, -1, -1, complete, includePageConfig, true );
    }

    public String getMenuName( int menuKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        String name;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( MENU_SELECT_NAME );
            preparedStmt.setInt( 1, menuKey );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                name = resultSet.getString( "men_sName" );
            }
            else
            {
                name = null;
            }

        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get menu name: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
            name = null;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return name;
    }

    private Element getMenuData( Element rootElement, int menuId )
    {
        Document doc = rootElement.getOwnerDocument();
        Element menuElement = XMLTool.createElement( doc, rootElement, "menu" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet result = null;
        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( MENU_SELECT_BY_KEY );
            preparedStmt.setInt( 1, menuId );
            result = preparedStmt.executeQuery();
            if ( result.next() )
            {
                int languageKey = result.getInt( "men_lan_lKey" );

                menuElement.setAttribute( "key", String.valueOf( menuId ) );
                menuElement.setAttribute( "languagekey", String.valueOf( languageKey ) );
                menuElement.setAttribute( "languagecode", result.getString( "lan_sCode" ) );
                menuElement.setAttribute( "language", result.getString( "lan_sDescription" ) );
                String name = result.getString( "men_sName" );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, menuElement, "name", name );
                }
                else
                {
                    XMLTool.createElement( doc, menuElement, "name" );
                }

                // firstpage:
                int frontpageKey = result.getInt( "men_mei_firstPage" );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, menuElement, "firstpage" ).setAttribute( "key", String.valueOf( frontpageKey ) );
                }
                else
                {
                    XMLTool.createElement( doc, menuElement, "firstpage" );
                }

                // loginpage:
                int loginpageKey = result.getInt( "men_mei_loginPage" );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, menuElement, "loginpage" ).setAttribute( "key", String.valueOf( loginpageKey ) );
                }
                else
                {
                    XMLTool.createElement( doc, menuElement, "loginpage" );
                }

                // errorpage:
                int errorpageKey = result.getInt( "men_mei_errorPage" );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, menuElement, "errorpage" ).setAttribute( "key", String.valueOf( errorpageKey ) );
                }
                else
                {
                    XMLTool.createElement( doc, menuElement, "errorpage" );
                }

                // Page template:
                int defaultPagetemplate = result.getInt( "men_pat_lKey" );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, menuElement, "defaultpagetemplate" ).setAttribute( "pagetemplatekey",
                                                                                                   String.valueOf( defaultPagetemplate ) );
                }

                // XML data:
                InputStream is = result.getBinaryStream( "men_xmlData" );
                if ( !result.wasNull() )
                {
                    Document tmpDoc = XMLTool.domparse( is );
                    menuElement.appendChild( doc.importNode( tmpDoc.getDocumentElement(), true ) );
                }

                // Menu details.
                Element detailsElement = XMLTool.createElement( doc, menuElement, "details" );

                //Statistics URL:
                String statisticsURL = result.getString( db.tMenu.men_sStatisticsURL.getName() );
                if ( !result.wasNull() )
                {
                    XMLTool.createElement( doc, detailsElement, db.tMenu.men_sStatisticsURL.getXPath() ).setTextContent( statisticsURL );
                }
                else
                {
                    XMLTool.createElement( doc, detailsElement, db.tMenu.men_sStatisticsURL.getXPath() );
                }

                /* Missing fields. New fields are only implemented in SiteXmlCreator. */
            }
            else
            {
                String message = "No menu found for menu ID: %0";
                VerticalEngineLogger.error( this.getClass(), 10, message, menuId, null );
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.error( this.getClass(), 20, "A database error occurred: %t", sqle );
        }
        finally
        {
            close( result );
            close( preparedStmt );
            close( con );
        }

        return menuElement;
    }

    public Document getMenuItem( User user, int key, boolean withParents )
    {
        return getMenuItem( user, key, withParents, false, false );
    }


    public int getMenuKeyByMenuItem( int menuItemKey )
    {
        Connection con = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        int menuKey = -1;

        try
        {
            con = getConnection();
            prepStmt = con.prepareStatement( MENU_GET_KEY_BY_MENU_ITEM );
            prepStmt.setInt( 1, menuItemKey );
            resultSet = prepStmt.executeQuery();
            if ( resultSet.next() )
            {
                menuKey = resultSet.getInt( 1 );
            }
        }
        catch ( SQLException ignored )
        {
        }
        finally
        {
            close( resultSet );
            close( prepStmt );
            close( con );
        }

        return menuKey;
    }

    protected Document getMenuItem( User user, int key, boolean withParents, boolean complete, boolean includePageConfig,
                                    boolean withChildren )
    {

        Document doc;
        Element rootElement;
        doc = XMLTool.createDocument( "menuitems" );
        rootElement = doc.getDocumentElement();

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( getSecurityHandler().appendMenuItemSQL( user, MENU_ITEM_SELECT_BY_KEY ) );
            preparedStmt.setInt( 1, key );
            resultSet = preparedStmt.executeQuery();

            if ( !withChildren )
            {
                if ( resultSet.next() )
                {
                    buildMenuItemXML( doc, rootElement, resultSet, -1, complete, includePageConfig, true, true, true, -1 );
                }

                // include parents?
                if ( withParents )
                {
                    // yep. call getMenuItemDOM recursivly.
                    Element menuItemElement = (Element) doc.getDocumentElement().getFirstChild();
                    if ( menuItemElement.hasAttribute( "parent" ) )
                    {
                        int parentKey = Integer.valueOf( menuItemElement.getAttribute( "parent" ) );
                        while ( parentKey >= 0 )
                        {
                            // get the parent:
                            doc = getMenuItem( user, parentKey, false, false, false );

                            // move the child inside the parent:
                            rootElement = doc.getDocumentElement();
                            Element parentElement = (Element) rootElement.getFirstChild();
                            if ( parentElement != null )
                            {
                                Element menuItemsElement = XMLTool.createElement( doc, parentElement, "menuitems" );
                                menuItemsElement.appendChild( doc.importNode( menuItemElement, true ) );
                                menuItemElement = parentElement;

                                if ( menuItemElement.hasAttribute( "parent" ) )
                                {
                                    parentKey = Integer.valueOf( menuItemElement.getAttribute( "parent" ) );
                                }
                                else
                                {
                                    parentKey = -1;
                                }
                            }
                            else
                            {
                                parentKey = -1;
                            }
                        }
                    }
                }
            }
            else
            {
                buildMenuItemsXML( user, resultSet, doc, rootElement, -1, -1, complete, includePageConfig, true, true, true );
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.error( this.getClass(), 30, "SQL error.", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;

    }

    protected Document getMenuItem( User user, int key, int tagItem, boolean withParents, boolean complete, boolean includePageConfig,
                                    boolean withChildren, boolean includeHidden, boolean includeTypeSpecificXML, boolean tagItems,
                                    int levels )
    {

        Document doc = XMLTool.createDocument( "menuitems" );
        Element rootElement = doc.getDocumentElement();

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            String sql = getSecurityHandler().appendMenuItemSQL( user, MENU_ITEM_SELECT_BY_KEY, null );
            preparedStmt = con.prepareStatement( sql );

            preparedStmt.setInt( 1, key );
            resultSet = preparedStmt.executeQuery();

            if ( !withChildren )
            {
                if ( resultSet.next() )
                {
                    buildMenuItemXML( doc, rootElement, resultSet, tagItem, complete, includePageConfig, includeHidden,
                                      includeTypeSpecificXML, tagItems, levels );
                }

                // include parents?
                if ( withParents )
                {
                    // yep. call getMenuItemDOM recursivly.
                    Element menuItemElement = (Element) doc.getDocumentElement().getFirstChild();
                    String tmp = menuItemElement.getAttribute( "parent" );

                    while ( tmp != null && tmp.length() > 0 )
                    {
                        // get the parent:
                        doc = getMenuItem( user, Integer.parseInt( tmp ), false, false, false );

                        // move the child inside the parent:
                        rootElement = doc.getDocumentElement();
                        Element parentElement = (Element) rootElement.getFirstChild();
                        Element menuItemsElement = XMLTool.createElement( doc, parentElement, "menuitems" );
                        menuItemsElement.appendChild( doc.importNode( menuItemElement, true ) );
                        menuItemElement = parentElement;

                        tmp = menuItemElement.getAttribute( "parent" );
                    }
                }
            }
            else
            {
                buildMenuItemsXML( user, resultSet, doc, rootElement, levels, tagItem, complete, includePageConfig, includeHidden,
                                   includeTypeSpecificXML, tagItems );
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.error( this.getClass(), 30, "SQL error.", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;

    }

    protected ArrayList<Integer> getMenuItemKeys( int menuKey, int parent )
    {
        ArrayList<Integer> keys = new ArrayList<Integer>();

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet result = null;
        try
        {
            con = getConnection();
            if ( parent == -1 )
            {
                preparedStmt = con.prepareStatement( MENU_SELECT_KEYS_BY_MENU_KEY_WO_PARENT );
                preparedStmt.setInt( 1, menuKey );
            }
            else
            {
                preparedStmt = con.prepareStatement( MENU_SELECT_KEYS_BY_MENU_KEY_AND_PARENT );
                preparedStmt.setInt( 1, menuKey );
                preparedStmt.setInt( 2, parent );
            }

            result = preparedStmt.executeQuery();
            while ( result.next() )
            {
                int key = result.getInt( "mei_lKey" );
                keys.add( key );
            }

        }
        catch ( SQLException sqle )
        {
            System.err.println( "[MenuHandler_DB2Impl:Error] sql exception." );
        }
        finally
        {
            close( result );
            close( preparedStmt );
            close( con );
        }

        return keys;
    }

    public int getParentMenuItemKey( int menuItemKey )
    {
        CommonHandler commonHandler = getCommonHandler();
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lParent, false, db.tMenuItem.mei_lKey );
        return commonHandler.getInt( sql.toString(), menuItemKey );
    }

    public String getMenuItemName( int menuItemKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_sName, false, db.tMenuItem.mei_lKey );
        return getCommonHandler().getString( sql.toString(), menuItemKey );
    }

    private Hashtable<String, Integer> getMenuItemTypesAsHashtable()
    {
        if ( menuItemTypes == null )
        {
            menuItemTypes = new Hashtable<String, Integer>();
            for ( MenuItemType menuItemType : MenuItemType.values() )
            {
                menuItemTypes.put( menuItemType.getName(), menuItemType.getKey() );
            }
        }
        return menuItemTypes;

    }

    public void removeMenu( User user, int key )
        throws com.enonic.vertical.engine.VerticalRemoveException, VerticalSecurityException
    {

        // remove the darn thing
        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();

            // remove sections
            int[] sectionKeys = getSectionHandler().getSectionKeysByMenu( key );
            for ( int sectionKey : sectionKeys )
            {
                getSectionHandler().removeSection( sectionKey, true );
            }

            getCommonHandler().cascadeDelete( db.tMenu, key );

            // remove accessrights:
            preparedStmt = con.prepareStatement( "DELETE FROM tDefaultMenuAR WHERE dma_men_lkey = ?" );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();
            preparedStmt.close();

            // remove the menu (prepare delete):
            preparedStmt = con.prepareStatement( MENU_PREPARE_DELETE );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();
            preparedStmt.close();

            int[] pageKeys = getPageHandler().getPageKeysByMenu( con, key );

            // remove the menuitems:
            ArrayList<Integer> menuItemKeys = getMenuItemKeys( key, -1 );
            if ( menuItemKeys != null && menuItemKeys.size() > 0 )
            {
                for ( Integer menuItemKey : menuItemKeys )
                {
                    removeMenuItem( user, menuItemKey );
                }
            }

            getPageHandler().removePages( con, pageKeys );
            getPageTemplateHandler().removePageTemplatesByMenu( key );
            getContentObjectHandler().removeContentObjectsByMenu( con, key );

            // remove the menu:
            preparedStmt = con.prepareStatement( MENU_DELETE_WITH_KEY );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, sqle );
        }
        catch ( VerticalRemoveException vre )
        {
            String message = "Failed to remove menu: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 0, message, vre );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private MenuItemType getMenuItemType( int menuItemKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_mid_lkey, false, (Column[]) null );
        XDG.appendWhereSQL( sql, db.tMenuItem.mei_lKey, XDG.OPERATOR_EQUAL, menuItemKey );
        int menuItemTypeKey = getCommonHandler().getInt( sql.toString(), (Object[]) null );
        return MenuItemType.get( menuItemTypeKey );
    }

    public void removeMenuItem( User user, int menuItemKey )
        throws VerticalRemoveException, VerticalSecurityException
    {

        MenuItemEntity menuItemToRemove = menuItemDao.findByKey( menuItemKey );

        List<MenuItemEntity> shortcuttingMenuItems = getShortcuttingMenuItems( menuItemToRemove );

        if ( !shortcuttingMenuItems.isEmpty() )
        {
            throw new VerticalRemoveException( "Could not remove menuItem since its referred to by shortcut" );
        }

        if ( isFrontPage( menuItemToRemove ) )
        {
            throw new VerticalRemoveException( "This menu item can not be removed, it is the site front page." );
        }
        if ( isLoginPage( menuItemToRemove ) )
        {
            throw new VerticalRemoveException( "This menu item can not be removed, it is the site login page." );
        }
        if ( isErrorPage( menuItemToRemove ) )
        {
            throw new VerticalRemoveException( "This menu item can not be removed, it is the site error page." );
        }

        MenuItemType type = getMenuItemType( menuItemKey );

        // security check:
        SecurityHandler securityHandler = getSecurityHandler();
        if ( !securityHandler.validateMenuItemRemove( user, menuItemKey ) )
        {
            VerticalEngineLogger.errorSecurity( this.getClass(), 10, "Not allowed to remove menuitem: %0", new Object[]{menuItemKey},
                                                null );
        }

        getCommonHandler().cascadeDelete( db.tMenuItem, menuItemKey );

        Connection con = null;

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            // get menu item name and menu key (used by event handling)
            String name = getMenuItemName( menuItemKey );
            int menuKey = getMenuKeyByMenuItem( menuItemKey );

            // recursivly remove children
            preparedStmt = con.prepareStatement( MENU_ITEM_SELECT_CHILDREN );
            preparedStmt.setInt( 1, menuItemKey );
            resultSet = preparedStmt.executeQuery();
            List<Integer> menuItemsChildList = new LinkedList<Integer>();
            while ( resultSet.next() )
            {
                menuItemsChildList.add( resultSet.getInt( 1 ) );
            }
            resultSet.close();
            resultSet = null;
            preparedStmt.close();
            preparedStmt = null;

            for ( Integer childKey : menuItemsChildList )
            {
                //String childType = (String) menuItemsMap.get(childKey);
                removeMenuItem( user, childKey );
            }

            securityHandler.removeAccessRights( con, menuItemKey, AccessRight.MENUITEM );
            if ( type == MenuItemType.PAGE )
            {
                removePageFromMenuItem( con, menuItemKey );
            }
            else if ( type == MenuItemType.CONTENT )
            {
                removePageFromMenuItem( con, menuItemKey );
            }

            // remove any sections
            MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( new MenuItemKey( menuItemKey ) );
            if ( sectionKey != null )
            {
                getSectionHandler().removeSection( sectionKey.toInt() );
            }

            preparedStmt = con.prepareStatement( MENU_ITEM_DELETE_WITH_KEY );
            preparedStmt.setInt( 1, menuItemKey );
            preparedStmt.executeUpdate();

            if ( multicaster.hasListeners() )
            {
                MenuHandlerEvent e = new MenuHandlerEvent( user, menuKey, menuItemKey, name, this );
                multicaster.removedMenuItem( e );
            }
        }
        catch ( SQLException sqle )
        {
            String MESSAGE_00 = "A database error occurred while removing menuitem: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 20, MESSAGE_00, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    private boolean isErrorPage( MenuItemEntity menuItem )
    {
        SiteEntity site = menuItem.getSite();
        return menuItem.equals( site.getErrorPage() );
    }

    private boolean isLoginPage( MenuItemEntity menuItem )
    {
        SiteEntity site = menuItem.getSite();
        return menuItem.equals( site.getLoginPage() );
    }

    private boolean isFrontPage( MenuItemEntity menuItem )
    {
        SiteEntity site = menuItem.getSite();
        return menuItem.equals( site.getFrontPage() );
    }

    private List<MenuItemEntity> getShortcuttingMenuItems( MenuItemEntity menuItem )
    {

        MenuItemSpecification shortcutsToMenuItemsSpec = new MenuItemSpecification();
        shortcutsToMenuItemsSpec.setMenuItemShortcut( menuItem );

        return menuItemDao.findBySpecification( shortcutsToMenuItemsSpec );
    }

    protected void removePageFromMenuItem( Connection con, int key )
        throws SQLException, VerticalRemoveException
    {

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        int pagKey = -1;

        try
        {
            // Get the page key:
            preparedStmt = con.prepareStatement( "SELECT mei_pag_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_lKey = ?" );
            preparedStmt.setInt( 1, key );
            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                pagKey = resultSet.getInt( 1 );
            }
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        try
        {
            // Remove the coupling between the menuitem and the page:
            preparedStmt = con.prepareStatement( "UPDATE " + MENU_ITEM_TABLE + " SET mei_pag_lKey = ? WHERE mei_lKey = ?" );
            preparedStmt.setNull( 1, Types.INTEGER );
            preparedStmt.setInt( 2, key );
            preparedStmt.executeUpdate();
        }
        finally
        {
            close( preparedStmt );
        }

        // Remove the page from the page table:
        if ( pagKey != -1 )
        {
            getPageHandler().removePage( pagKey );
        }
    }

    protected void setURLToNull( Connection con, MenuItemKey mikey )
        throws SQLException
    {
        PreparedStatement preparedStmt = null;

        try
        {
            preparedStmt = con.prepareStatement( "UPDATE " + MENU_ITEM_TABLE + " SET mei_sURL = ? WHERE mei_lKey = ?" );
            preparedStmt.setNull( 1, Types.VARCHAR );
            preparedStmt.setInt( 2, mikey.toInt() );
            preparedStmt.executeUpdate();
            preparedStmt.close();
        }
        finally
        {
            close( preparedStmt );
        }
    }

    private void updateMenu( User user, CopyContext copyContext, Document doc, boolean useOldKeys )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Element root_elem = doc.getDocumentElement();

        // get menu key:
        String tmp = root_elem.getAttribute( "key" );
        SiteKey menuKey = new SiteKey( tmp );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();

            // Update the main menu table:
            preparedStmt = con.prepareStatement( MENU_UPDATE );

            // firstpage:
            Element tmpElement = XMLTool.getElement( root_elem, "firstpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 1, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 1, Types.INTEGER );
            }

            // loginpage:
            tmpElement = XMLTool.getElement( root_elem, "loginpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 2, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 2, Types.INTEGER );
            }

            // errorpage:
            tmpElement = XMLTool.getElement( root_elem, "errorpage" );
            tmp = tmpElement.getAttribute( "key" );
            if ( tmp != null && tmp.length() > 0 )
            {
                preparedStmt.setInt( 3, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 3, Types.INTEGER );
            }

            // default pagetemplate:
            tmpElement = XMLTool.getElement( root_elem, "defaultpagetemplate" );
            if ( tmpElement != null )
            {
                tmp = tmpElement.getAttribute( "pagetemplatekey" );
                preparedStmt.setInt( 4, Integer.parseInt( tmp ) );
            }
            else
            {
                preparedStmt.setNull( 4, Types.INTEGER );
            }

            SiteData siteData = new SiteData();

            // DeviceClassResolver:
            tmpElement = XMLTool.getElement( root_elem, "deviceclassresolver" );
            if ( tmpElement != null )
            {
                String deviceClassResolverUrl = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( deviceClassResolverUrl ) )
                {
                    siteData.setDeviceClassResolver( new ResourceKey( deviceClassResolverUrl ) );
                }
            }

            // default localization resource:
            tmpElement = XMLTool.getElement( root_elem, "defaultlocalizationresource" );
            if ( tmpElement != null )
            {
                String defaultLocalizationResource = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( defaultLocalizationResource ) )
                {
                    siteData.setDefaultLocalizationResource( new ResourceKey( defaultLocalizationResource ) );
                }
            }

            // locale resolver:
            tmpElement = XMLTool.getElement( root_elem, "localeresolver" );
            if ( tmpElement != null )
            {
                String localeResolver = tmpElement.getAttribute( "key" );
                if ( StringUtils.isNotEmpty( localeResolver ) )
                {
                    siteData.setLocaleResolver( new ResourceKey( localeResolver ) );
                }
            }

            // Path to public home:
            String pathToPublicHome = root_elem.getAttribute( "pathtopublichome" );
            if ( StringUtils.isNotEmpty( pathToPublicHome ) )
            {
                siteData.setPathToPublicResources( new ResourceKey( pathToPublicHome ) );
            }

            // Path to home:
            String pathToHome = root_elem.getAttribute( "pathtohome" );
            if ( StringUtils.isNotEmpty( pathToHome ) )
            {
                siteData.setPathToResources( new ResourceKey( pathToHome ) );
            }

            tmpElement = XMLTool.getElement( root_elem, "menudata" );
            if ( tmpElement != null )
            {
                parseAndAddMenudataToSiteData( tmpElement, siteData );
            }

            final byte[] xmldata = siteData.getAsBytes();
            preparedStmt.setBinaryStream( 5, new ByteArrayInputStream( xmldata ), xmldata.length );

            // language key:
            preparedStmt.setInt( 6, Integer.parseInt( root_elem.getAttribute( "languagekey" ) ) );

            Element detailsElement = XMLTool.getElement( root_elem, "detials" );

            // Statistics URL:
            tmpElement = XMLTool.getElement( detailsElement, "statistics" );
            if ( tmpElement != null )
            {
                String name = XMLTool.getElementText( tmpElement );
                if ( name != null )
                {
                    preparedStmt.setString( 7, name );
                }
                else
                {
                    preparedStmt.setNull( 7, Types.VARCHAR );
                }
            }
            else
            {
                preparedStmt.setNull( 7, Types.VARCHAR );
            }

            // Run As User:
            String runAsUserGroupKey = root_elem.getAttribute( "runas" );
            if ( StringUtils.isNotEmpty( runAsUserGroupKey ) )
            {
                UserKey userKey = getUserKeyFromGroupKey( runAsUserGroupKey );
                preparedStmt.setString( 8, userKey != null ? userKey.toString() : null );
            }
            else
            {
                preparedStmt.setNull( 8, Types.VARCHAR );
            }

            // menu key:
            preparedStmt.setInt( 9, menuKey.toInt() );
            preparedStmt.executeUpdate();
            preparedStmt.close();
            preparedStmt = null;

            // Update the individual menuitems (recursivly):
            try
            {
                Element[] elems = XMLTool.getElements( XMLTool.getElement( root_elem, "menuitems" ) );
                for ( int i = 0; i < elems.length; i++ )
                {
                    String curDeleted = elems[i].getAttribute( "deleted" );
                    if ( !"deleted".equals( curDeleted ) )
                    {
                        String curKey = elems[i].getAttribute( "key" );
                        if ( curKey == null || curKey.length() == 0 || !useOldKeys )
                        {
                            createMenuItem( user, copyContext, elems[i], menuKey, i, null, useOldKeys );
                        }
                        else
                        {
                            updateMenuItem( user, elems[i], menuKey, i, null, true );
                        }
                    }
                }
            }
            catch ( VerticalCreateException vce )
            {
                VerticalEngineLogger.errorUpdate( this.getClass(), 2, "Failed to create new menuitem: %t", vce );
            }

            // get all deleted menuitems:
            String xpath = "//menuitem[@deleted = 'deleted']";

            try
            {
                // Search for the xpath:
                NodeList list = XMLTool.selectNodes( doc.getDocumentElement(), xpath );

                // Loop through the results.
                for (int i = 0; i < list.getLength(); i++) {
                    Element n = (Element)list.item( i );
                    tmp = n.getAttribute( "key" );
                    removeMenuItem( user, Integer.parseInt( tmp ) );
                }

            }
            catch ( VerticalRemoveException vre )
            {
                VerticalEngineLogger.errorUpdate( this.getClass(), 3, "Failed to remove menuitem: %t", vre );
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 5, "A database error occurred: %t", sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private void parseAndAddMenudataToSiteData( Element menuDataElement, SiteData siteData )
    {
        Element pageTypesEl = XMLTool.getElement( menuDataElement, "pagetypes" );
        Element[] allowElements = XMLTool.getElements( pageTypesEl, "allow" );
        for ( Element allowEl : allowElements )
        {
            final String pageType = allowEl.getAttribute( "type" );
            siteData.addPageType( pageType );
        }

        Element defaultCss = XMLTool.getElement( menuDataElement, "defaultcss" );
        if ( defaultCss != null )
        {
            String defaultCssKey = defaultCss.getAttribute( "key" );

            if ( StringUtils.isNotEmpty( defaultCssKey ) )
            {
                siteData.setDefaultCssKey( new ResourceKey( defaultCssKey ) );
            }
        }
    }

    private UserKey getUserKeyFromGroupKey( String groupKey )
    {
        UserSpecification userSpec = new UserSpecification();
        userSpec.setDeletedState( UserSpecification.DeletedState.ANY );
        userSpec.setUserGroupKey( new GroupKey( groupKey ) );
        UserEntity userEntity = userDao.findSingleBySpecification( userSpec );

        if ( userEntity == null )
        {
            return null;
        }

        return userEntity.getKey();
    }

    public void updateMenuData( Document doc )
        throws VerticalUpdateException, VerticalSecurityException
    {
        Element root_elem = doc.getDocumentElement();

        // get menu key:
        String tmp = root_elem.getAttribute( "key" );
        int menuKey = Integer.parseInt( tmp );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();

            // Update the main menu table:
            preparedStmt = con.prepareStatement( MENUDATA_UPDATE );

            // name:
            Element tmpElement = XMLTool.getElement( root_elem, "name" );
            String name = null;
            if ( tmpElement != null )
            {
                name = XMLTool.getElementText( tmpElement );
            }
            if ( name != null && name.length() > 0 )
            {
                StringReader sreader = new StringReader( name );
                preparedStmt.setCharacterStream( 1, sreader, name.length() );
            }
            else
            {
                preparedStmt.setNull( 1, Types.VARCHAR );
            }

            // language:
            preparedStmt.setInt( 3, Integer.parseInt( root_elem.getAttribute( "languagekey" ) ) );

            Element detailsElement = XMLTool.getElement( root_elem, "details" );

            // Statistics URL:
            tmpElement = XMLTool.getElement( detailsElement, "statistics" );
            if ( tmpElement != null )
            {
                String statisticsURL = XMLTool.getElementText( tmpElement );
                if ( statisticsURL != null )
                {
                    preparedStmt.setString( 4, statisticsURL );
                }
                else
                {
                    preparedStmt.setNull( 4, Types.VARCHAR );
                }
            }
            else
            {
                preparedStmt.setNull( 4, Types.VARCHAR );
            }

            SiteData siteData = new SiteData();

            // DeviceClassResolver:
            tmpElement = XMLTool.getElement( root_elem, "deviceclassresolver" );
            if ( tmpElement != null )
            {
                String deviceClassResolverUrl = tmpElement.getAttribute( "key" );
                if ( deviceClassResolverUrl != null )
                {
                    siteData.setDeviceClassResolver( new ResourceKey( deviceClassResolverUrl ) );
                }
            }

            // Localization default resource:
            tmpElement = XMLTool.getElement( root_elem, "defaultlocalizationresource" );
            if ( tmpElement != null )
            {
                String defaultLocalizationResourceUrl = tmpElement.getAttribute( "key" );
                if ( defaultLocalizationResourceUrl != null )
                {
                    siteData.setDefaultLocalizationResource( new ResourceKey( defaultLocalizationResourceUrl ) );
                }
            }

            // Locale resolver
            tmpElement = XMLTool.getElement( root_elem, "localeresolver" );
            if ( tmpElement != null )
            {
                String localeResolver = tmpElement.getAttribute( "key" );
                if ( localeResolver != null )
                {
                    siteData.setLocaleResolver( new ResourceKey( localeResolver ) );
                }
            }

            // Path to public home:
            String pathToPublicHome = root_elem.getAttribute( "pathtopublichome" );
            if ( pathToPublicHome != null && pathToPublicHome.length() > 0 )
            {
                siteData.setPathToPublicResources( new ResourceKey( pathToPublicHome ) );
            }

            // Path to home:
            String pathToHome = root_elem.getAttribute( "pathtohome" );
            if ( StringUtils.isNotEmpty( pathToHome ) )
            {
                siteData.setPathToResources( new ResourceKey( pathToHome ) );
            }

            // menu data:
            tmpElement = XMLTool.getElement( root_elem, "menudata" );
            if ( tmpElement != null )
            {
                parseAndAddMenudataToSiteData( tmpElement, siteData );
            }

            final byte[] xmldata = siteData.getAsBytes();
            preparedStmt.setBinaryStream( 2, new ByteArrayInputStream( xmldata ), xmldata.length );

            // Run As User:
            String runAsUserGroupKey = root_elem.getAttribute( "runas" );
            if ( runAsUserGroupKey != null && runAsUserGroupKey.length() > 0 )
            {
                UserKey userKey = getUserKeyFromGroupKey( runAsUserGroupKey );
                preparedStmt.setString( 5, userKey != null ? userKey.toString() : null );
            }
            else
            {
                preparedStmt.setNull( 5, Types.VARCHAR );
            }

            // menu key:
            preparedStmt.setInt( 6, menuKey );
            preparedStmt.executeUpdate();
            preparedStmt.close();
            preparedStmt = null;
        }
        catch ( SQLException sqle )
        {
            String message = "SQL error: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void updateMenuItem( User user, String xmlData )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Document doc = XMLTool.domparse( xmlData );
        Element rootElement = doc.getDocumentElement();
        Element menuItemElement = XMLTool.getElement( rootElement, "menuitem" );

        SiteKey siteKey = null;
        String tmp = menuItemElement.getAttribute( "menukey" );
        if ( tmp != null && tmp.length() > 0 )
        {
            siteKey = new SiteKey( tmp );
        }

        int order = 0;
        tmp = menuItemElement.getAttribute( "order" );
        if ( tmp != null && tmp.length() > 0 )
        {
            order = Integer.parseInt( tmp );
        }

        MenuItemKey parent = null;
        tmp = menuItemElement.getAttribute( "parent" );
        if ( tmp != null && tmp.length() > 0 )
        {
            parent = new MenuItemKey( tmp );
        }

        try
        {
            updateMenuItem( user, menuItemElement, siteKey, order, parent, false );
        }
        catch ( VerticalCreateException e )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 10, "Wrapped exception: %t", e );
        }
    }

    private void updateMenuItem( User user, Element menuitem_elem, SiteKey siteKey, int order, MenuItemKey parent, boolean skipNameCheck )
        throws VerticalUpdateException, VerticalCreateException, VerticalSecurityException
    {

        MenuItemKey key = new MenuItemKey( menuitem_elem.getAttribute( "key" ) );

        Element menuItemNameElement = XMLTool.getElement( menuitem_elem, ELEMENT_NAME_MENUITEM_NAME );

        String menuItemName = XMLTool.getElementText( menuItemNameElement );

        // If no menuItemName given, it should be generated. This is already done in the MenuHandlerServlet, but enshure this for other ways in aswell
        if ( StringUtils.isEmpty( menuItemName ) )
        {
            menuItemName = generateMenuItemName( menuitem_elem );
            menuItemNameElement.setTextContent( menuItemName );
        }

        String uniqueMenuItemName = ensureUniqueMenuItemName( siteKey, parent, menuItemName, key );

        if ( !uniqueMenuItemName.equals( menuItemName ) )
        {
            menuItemName = uniqueMenuItemName;
            menuItemNameElement.setTextContent( menuItemName );
        }

        if ( !skipNameCheck && menuItemNameExists( siteKey, parent, menuItemName, key ) )
        {
            VerticalEngineLogger.errorCreate( this.getClass(), 20, "Menu item name already exists on this level: %0",
                                              new Object[]{menuItemName}, null );
        }

        Connection con = null;
        try
        {
            con = getConnection();

            // menu item types:
            Hashtable<String, Integer> itemTypes = getMenuItemTypesAsHashtable();

            // security check:
            if ( !getSecurityHandler().validateMenuItemUpdate( user, key.toInt() ) )
            {
                VerticalEngineLogger.errorSecurity( this.getClass(), 10, "Not allowed to update menuitem: %0.", new Object[]{key}, null );
            }

            // get type:
            String str_type = menuitem_elem.getAttribute( "type" );
            int type = itemTypes.get( str_type );

            // has it changed type?
            boolean typeChanged = false;
            String str_typeChanged = menuitem_elem.getAttribute( "typechanged" );
            if ( str_typeChanged != null && str_typeChanged.length() != 0 )
            {
                typeChanged = true;

                // the type has changed. delete it and re-create it with the same key.
                int old_type = itemTypes.get( str_typeChanged );

                try
                {
                    switch ( old_type )
                    {
                        case 1:
                            // page
                            removePageFromMenuItem( con, key.toInt() );
                            break;
                        case 2:
                            // URL
                            setURLToNull( con, key );
                            break;
                        case 4:
                            // page
                            removePageFromMenuItem( con, key.toInt() );
                            break;
                        case 7:
                            // page
                            if ( type != 7 )
                            {
                                removeShortcut( key.toInt() );
                            }
                            break;
                    }
                }
                catch ( VerticalRemoveException e )
                {
                    VerticalEngineLogger.errorUpdate( this.getClass(), 30, "Error removing page: %t", e );
                }
                setMenuItemType( key.toInt(), type );
            }

            // create the new menu item type or update existing:
            boolean modified = "modified".equals( menuitem_elem.getAttribute( "modified" ) );
            if ( modified )
            {
                PreparedStatement preparedStmt = null;

                boolean hasSection = false;

                try
                {
                    switch ( type )
                    {
                        case 1:
                            Element pageElem = XMLTool.getElement( menuitem_elem, "page" );
                            String str_pageKey = pageElem.getAttribute( "key" );
                            if ( str_pageKey != null && str_pageKey.length() > 0 && !typeChanged )
                            {
                                updatePage( con, menuitem_elem, type, key );
                            }
                            else
                            {
                                createPage( con, menuitem_elem, type, key );
                            }
                            break;

                        case 2: // create a new URL-thingie
                            createOrUpdateURL( con, menuitem_elem, key );
                            break;

                        case 4: // document
                            // does it have a section?
                            Element sectionElem = XMLTool.getElement( menuitem_elem, "section" );
                            if ( sectionElem != null )
                            {
                                hasSection = true;
                                MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( key );
                                if ( sectionKey != null )
                                {
                                    updateSection( menuitem_elem, sectionKey.toInt() );
                                }
                                else
                                {
                                    createSection( menuitem_elem, key );
                                }
                            }

                            // page
                            pageElem = XMLTool.getElement( menuitem_elem, "page" );
                            str_pageKey = pageElem.getAttribute( "key" );
                            if ( str_pageKey != null && str_pageKey.length() > 0 && !typeChanged )
                            {
                                updatePage( con, menuitem_elem, type, key );
                            }
                            else
                            {
                                createPage( con, menuitem_elem, type, key );
                            }
                            break;

                        case 5: // Label:
                            break;

                        case 6: // section
                            hasSection = true;
                            MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( key );
                            if ( sectionKey != null )
                            {
                                updateSection( menuitem_elem, sectionKey.toInt() );
                            }
                            else
                            {
                                createSection( menuitem_elem, key );
                            }
                            break;

                        case 7: // Shortcut:
                            createOrOverrideShortcut( menuitem_elem, key );
                            break;
                    }
                }
                finally
                {
                    close( preparedStmt );
                }

                // remove section if necessary
                MenuItemKey sectionKey = getSectionHandler().getSectionKeyByMenuItem( key );
                if ( sectionKey != null && !hasSection )
                {
                    try
                    {
                        getSectionHandler().removeSection( sectionKey.toInt() );
                    }
                    catch ( VerticalRemoveException vre )
                    {
                        VerticalEngineLogger.errorUpdate( this.getClass(), 50, "Failed to remove section: %t", vre );
                    }
                }

                updateMenuItemData( user, menuitem_elem, type, parent, order );
            }
            else
            {
                updateMenuItemData( user, menuitem_elem, type, parent, order );
            }

            // set contentkey if present
            String contentKeyStr = menuitem_elem.getAttribute( "contentkey" );
            if ( contentKeyStr.length() == 0 )
            {
                contentKeyStr = "-1";
            }
            setMenuItemContentKey( key, Integer.parseInt( contentKeyStr ) );

            Element menuitems = XMLTool.getElement( menuitem_elem, "menuitems" );
            if ( menuitems != null )
            {
                Node[] items = XMLTool.filterNodes( menuitems.getChildNodes(), Node.ELEMENT_NODE );
                for ( int i = 0; i < items.length; i++ )
                {
                    Element curElement = (Element) items[i];
                    String deleted = curElement.getAttribute( "deleted" );
                    if ( !"deleted".equals( deleted ) )
                    {
                        String curKey = curElement.getAttribute( "key" );
                        if ( curKey == null || curKey.length() == 0 )
                        {
                            createMenuItem( user, null, curElement, siteKey, i, key, false );
                        }
                        else
                        {
                            updateMenuItem( user, curElement, siteKey, i, key, true );
                        }
                    }
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 50, "A database error occurred: %t", e );
        }
        finally
        {
            close( con );
        }
    }

    public void setMenuItemContentKey( MenuItemKey menuItemKey, int contentKey )
    {
        // first delete the contentkey for this menu item
        StringBuffer sql = XDG.generateRemoveSQL( db.tMenuItemContent, db.tMenuItemContent.mic_mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), menuItemKey.toInt() );

        // now insert the new value
        if ( contentKey != -1 )
        {
            sql = XDG.generateInsertSQL( db.tMenuItemContent );
            getCommonHandler().executeSQL( sql.toString(), new int[]{menuItemKey.toInt(), contentKey} );
        }
    }

    public int getMenuItemContentKey( int menuItemKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tMenuItemContent, db.tMenuItemContent.mic_con_lKey, false, db.tMenuItemContent.mic_mei_lKey );
        return getCommonHandler().getInt( sql.toString(), menuItemKey );
    }

    private void updateMenuItemData( User user, Element menuItemElem, int type, MenuItemKey parent, int order )
        throws VerticalUpdateException
    {

        String tmp;
        boolean modified = "modified".equals( menuItemElem.getAttribute( "modified" ) );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();

            // update the main table:
            int psCounter = 1;
            if ( modified )
            {
                preparedStmt = con.prepareStatement( MENU_ITEM_UPDATE );
            }
            else
            {
                preparedStmt = con.prepareStatement( MENU_ITEM_UPDATE_NO_DATA );
            }
            //}

            // name
            Element tmp_elem = XMLTool.getElement( menuItemElem, ELEMENT_NAME_MENUITEM_NAME );
            String name = XMLTool.getElementText( tmp_elem );

            validateMenuItemName( name );

            StringReader sreader = new StringReader( name );
            preparedStmt.setCharacterStream( psCounter++, sreader, name.length() );

            if ( parent != null )
            {
                preparedStmt.setInt( psCounter++, parent.toInt() );
            }
            else
            {
                preparedStmt.setNull( psCounter++, Types.INTEGER );
            }

            // order
            preparedStmt.setInt( psCounter++, order );

            Element dataElem;
            if ( modified )
            {
                dataElem = XMLTool.getElement( menuItemElem, "data" );

                // parameters
                tmp_elem = XMLTool.getElement( menuItemElem, "parameters" );
                if ( tmp_elem != null )
                {
                    dataElem.appendChild( tmp_elem );
                }
            }
            else
            {
                dataElem = null;
            }

            // alternative name:
            tmp_elem = XMLTool.getElement( menuItemElem, ELEMENT_NAME_MENU_NAME );
            if ( tmp_elem != null )
            {
                tmp = XMLTool.getElementText( tmp_elem );
                preparedStmt.setString( psCounter++, tmp );
            }
            else
            {
                preparedStmt.setNull( psCounter++, Types.VARCHAR );
            }

            // visibility:
            tmp = menuItemElem.getAttribute( "visible" );
            if ( "no".equals( tmp ) )
            {
                preparedStmt.setInt( psCounter++, 1 );
            }
            else
            {
                preparedStmt.setInt( psCounter++, 0 );
            }

            // description:
            tmp_elem = XMLTool.getElement( menuItemElem, "description" );
            String data = XMLTool.getElementText( tmp_elem );
            if ( data == null )
            {
                data = "";
            }
            StringReader reader = new StringReader( data );
            preparedStmt.setCharacterStream( psCounter++, reader, data.length() );

            // keywords
            tmp_elem = XMLTool.getElement( menuItemElem, "keywords" );
            String keywords = XMLTool.getElementText( tmp_elem );
            if ( keywords == null || keywords.length() == 0 )
            {
                preparedStmt.setNull( psCounter++, Types.VARCHAR );
            }
            else
            {
                StringReader keywordReader = new StringReader( keywords );
                preparedStmt.setCharacterStream( psCounter++, keywordReader, keywords.length() );
            }

            // language
            String lanKey = menuItemElem.getAttribute( "languagekey" );
            if ( ( lanKey != null ) && ( lanKey.length() > 0 ) )
            {
                preparedStmt.setInt( psCounter++, Integer.parseInt( lanKey ) );
            }
            else
            {
                preparedStmt.setNull( psCounter++, Types.INTEGER );
            }

            // get menuitem key:
            tmp = menuItemElem.getAttribute( "key" );
            int key = Integer.parseInt( tmp );

            // owner
            String ownerKey = menuItemElem.getAttribute( "owner" );

            // modifier
            String modifierKey = menuItemElem.getAttribute( "modifier" );

            if ( modified && type == 4 )
            {
                //byte[] document = null;
                Element docElem = XMLTool.getElement( menuItemElem, "document" );
                dataElem.appendChild( docElem );
            }
            preparedStmt.setString( psCounter++, ownerKey );
            preparedStmt.setString( psCounter++, modifierKey );

            RunAsType runAs = RunAsType.INHERIT;
            String runAsStr = menuItemElem.getAttribute( "runAs" );
            if ( StringUtils.isNotEmpty( runAsStr ) )
            {
                runAs = RunAsType.valueOf( runAsStr );
            }
            preparedStmt.setInt( psCounter++, runAs.getKey() );

            // displayname:
            tmp_elem = XMLTool.getElement( menuItemElem, ELEMENT_NAME_DISPLAY_NAME );

            if ( tmp_elem == null )
            {
                throw new IllegalArgumentException( "Displayname must be set" );
            }
            else
            {
                tmp = XMLTool.getElementText( tmp_elem );
                preparedStmt.setString( psCounter++, tmp );
            }

            // data
            if ( modified )
            {
                Document dataDoc = XMLTool.createDocument();
                dataDoc.appendChild( dataDoc.importNode( dataElem, true ) );

                byte[] bytes = XMLTool.documentToBytes( dataDoc, "UTF-8" );
                preparedStmt.setBinaryStream( psCounter++, new ByteArrayInputStream( bytes ), bytes.length );
            }

            preparedStmt.setInt( psCounter, key );

            preparedStmt.executeUpdate();

            if ( multicaster.hasListeners() )
            {
                int menuKey = getMenuKeyByMenuItem( key );
                MenuHandlerEvent e = new MenuHandlerEvent( user, menuKey, key, name, this );
                multicaster.updatedMenuItem( e );
            }
        }
        catch ( SQLException sqle )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 20, "SQL error while updating menuitem data.", sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private void validateMenuItemName( String name )
    {
        if ( name == null || name.trim().length() == 0 )
        {
            throw new IllegalArgumentException( "Name must be set: '" + name + "'" );
        }
        if ( name.endsWith( " " ) )
        {
            throw new IllegalArgumentException( "Name cannot end with a space: '" + name + "'" );
        }
        if ( name.startsWith( " " ) )
        {
            throw new IllegalArgumentException( "Name cannot start with a space: '" + name + "'" );
        }
        if ( name.indexOf( "  " ) >= 0 )
        {
            throw new IllegalArgumentException( "Name cannot contain double spaces: '" + name + "'" );
        }
    }

    public void moveMenuItem( User user, Element[] menuItemElems, int menuItemKey, int fromMenuKey, int fromParentKey, int toMenuKey,
                              int toParentKey )
    {
        try
        {
            // Sjekk om brukeren har rettighet til å flytte..
            // Brukeren må ha administrate rettighet dit han skal flytte til
            boolean isEA = user.isEnterpriseAdmin();

            if ( fromParentKey == -1 )
            {
                MenuAccessRight fromParentUserright = getSecurityHandler().getMenuAccessRight( user, fromMenuKey );
                if ( !fromParentUserright.getAdministrate() && !isEA )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to move menuitem[key=%0] from parent[key=%1] to parent[key=%2]. You need administrate rights on the parent to the menuitem you are moving.",
                                                        new Object[]{menuItemKey, fromParentKey, toParentKey}, null );
                }
            }
            else
            {
                MenuItemAccessRight fromParentUserright =
                    getSecurityHandler().getMenuItemAccessRight( user, new MenuItemKey( fromParentKey ) );
                if ( !fromParentUserright.getAdministrate() && !isEA )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to move menuitem[key=%0] from parent[key=%1] to parent[key=%2]. You need administrate rights on the parent to the menuitem you are moving.",
                                                        new Object[]{menuItemKey, fromParentKey, toParentKey}, null );
                }
            }
            if ( toParentKey == -1 )
            {
                MenuAccessRight toParentUserright = getSecurityHandler().getMenuAccessRight( user, toMenuKey );
                if ( !toParentUserright.getAdministrate() && !isEA )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to move menuitem[key=%0] from parent[key=%1] to parent[key=%2]. You need administrative rights on the menuitems you are moving into.",
                                                        new Object[]{menuItemKey, fromParentKey, toParentKey}, null );
                }
            }
            else
            {
                MenuItemAccessRight toParentUserright = getSecurityHandler().getMenuItemAccessRight( user, new MenuItemKey( toParentKey ) );
                if ( !toParentUserright.getAdministrate() && !isEA )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to move menuitem[key=%0] from parent[key=%1] to parent[key=%2]. You need administrative rights on the menuitems you are moving into.",
                                                        new Object[]{menuItemKey, fromParentKey, toParentKey}, null );
                }
            }

            // check whether name is unique for this parent
            String name = getMenuItemName( menuItemKey );
            if ( menuItemNameExists( new SiteKey( toMenuKey ), toParentKey == -1 ? null : new MenuItemKey( toParentKey ), name, null ) )
            {
                VerticalEngineLogger.errorCreate( this.getClass(), 20, "Menu item name already exists on this level: %0",
                                                  new Object[]{name}, null );
            }

            setMenuItemParent( menuItemKey, toParentKey );
            shiftMenuItems( user, menuItemElems, toMenuKey, toParentKey );
        }
        catch ( VerticalSecurityException e )
        {
            String message = "Failed to move menuitem: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, e );
        }
    }

    public void shiftMenuItems( User user, Element[] menuItemElems, int menuKey, int parentMenuItemKey )
    {

        try
        {

            // Sjekk om brukeren har rettighet til å flytte..
            // Brukeren må ha administrate rettighet dit han skal flytte til
            boolean hasEAPowers = memberOfResolver.hasEnterpriseAdminPowers( user.getKey() );

            if ( parentMenuItemKey == -1 )
            {
                MenuAccessRight fromParentUserright = getSecurityHandler().getMenuAccessRight( user, menuKey );
                if ( !fromParentUserright.getAdministrate() && !( hasEAPowers ) )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to shift menuitems at root in menu[key=%1]. You need administrate rights on the parent to the menuitem you are moving.",
                                                        new Object[]{menuKey}, null );
                }
            }
            else
            {
                MenuItemAccessRight fromParentUserright =
                    getSecurityHandler().getMenuItemAccessRight( user, new MenuItemKey( parentMenuItemKey ) );
                if ( !fromParentUserright.getAdministrate() && !( hasEAPowers ) )
                {
                    VerticalEngineLogger.errorSecurity( this.getClass(), 0,
                                                        "Not allowed to shift menuitems under parent[key=%0]. You need administrate rights on the parent to the menuitem you are moving.",
                                                        new Object[]{parentMenuItemKey}, null );
                }
            }

            for ( int i = 0; i < menuItemElems.length; i++ )
            {
                int menuItemKey = Integer.parseInt( menuItemElems[i].getAttribute( "key" ) );
                setMenuItemOrder( menuItemKey, i );
            }
        }
        catch ( VerticalSecurityException e )
        {
            String message = "Failed to shift menuitems: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, e );
        }
    }

    public void updatePage( Connection con, Element elem, int type, MenuItemKey key )
        throws VerticalUpdateException
    {

        Element pageElem = XMLTool.getElement( elem, "page" );
        int pKey = -1;

        Document pageDoc = XMLTool.createDocument();
        pageDoc.appendChild( pageDoc.importNode( pageElem, true ) );

        String tmp = pageElem.getAttribute( "key" );
        if ( tmp != null && tmp.length() > 0 )
        {
            pKey = Integer.parseInt( tmp );
            try
            {
                getPageHandler().updatePage( XMLTool.documentToString( pageDoc ) );
            }
            catch ( VerticalUpdateException vue )
            {
                VerticalEngineLogger.errorUpdate( this.getClass(), 10, "A database error occurred: %t", vue );
            }
        }
        else
        {
            String MESSAGE_10 = "No page key found in XML.";
            VerticalEngineLogger.errorUpdate( this.getClass(), 20, MESSAGE_10, null );
        }

        PreparedStatement preparedStmt = null;
        try
        {
            preparedStmt = con.prepareStatement( MENU_ITEM_PAGE_UPDATE_KEY );
            preparedStmt.setInt( 1, pKey );
            preparedStmt.setInt( 2, type );
            preparedStmt.setInt( 3, key.toInt() );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 30, "A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    private void copyMenu( CopyContext copyContext, Element menuElem )
        throws VerticalCopyException, VerticalCreateException, VerticalUpdateException, VerticalSecurityException
    {

        if ( menuElem != null )
        {
            User user = copyContext.getUser();
            int oldMenuKey = Integer.parseInt( menuElem.getAttribute( "key" ) );

            //if (copyContext.getOldSiteKey() != copyContext.getNewSiteKey())
            //  menuElem.setAttribute("sitekey", String.valueOf(copyContext.getNewSiteKey()));
            //else {
            Element nameElem = XMLTool.getElement( menuElem, "name" );
            Text text = (Text) nameElem.getFirstChild();
            text.setData( text.getData() + " (copy)" );
            //}

            Element elem = XMLTool.getElement( menuElem, "firstpage" );
            String oldFirstPageKey = elem.getAttribute( "key" );
            elem.removeAttribute( "key" );

            elem = XMLTool.getElement( menuElem, "loginpage" );
            String oldLoginPageKey = elem.getAttribute( "key" );
            elem.removeAttribute( "key" );

            elem = XMLTool.getElement( menuElem, "errorpage" );
            String oldErrorPageKey = elem.getAttribute( "key" );
            elem.removeAttribute( "key" );

            String oldDefaultPageTemplateKey = null;
            elem = XMLTool.getElement( menuElem, "defaultpagetemplate" );
            if ( elem != null )
            {
                oldDefaultPageTemplateKey = elem.getAttribute( "pagetemplatekey" );
                elem.removeAttribute( "pagetemplatekey" );
            }

            Document newDoc = XMLTool.createDocument();
            Element newMenuElem = (Element) newDoc.importNode( menuElem, true );
            newDoc.appendChild( newMenuElem );
            Element menuitemsElem = XMLTool.getElement( newMenuElem, "menuitems" );
            newMenuElem.removeChild( menuitemsElem );
            SiteKey menuKey = createMenu( user, copyContext, newDoc, false );

            // copy content objects and page templates
            ContentObjectHandler contentObjectHandler = getContentObjectHandler();
            contentObjectHandler.copyContentObjects( oldMenuKey, copyContext );
            PageTemplateHandler pageTemplateHandler = getPageTemplateHandler();
            pageTemplateHandler.copyPageTemplates( oldMenuKey, copyContext );

            Document doc = getMenu( user, menuKey.toInt(), true, true );
            Element docElem = doc.getDocumentElement();
            newMenuElem = (Element) docElem.getFirstChild();
            doc.replaceChild( newMenuElem, docElem );
            Element newMenuitemsElem = (Element) doc.importNode( menuitemsElem, true );
            menuitemsElem = XMLTool.getElement( newMenuElem, "menuitems" );
            newMenuElem.replaceChild( newMenuitemsElem, menuitemsElem );

            // prepare copy of menu items
            prepareCopy( newMenuitemsElem, copyContext );
            updateMenu( user, copyContext, doc, false );

            if ( oldFirstPageKey != null && oldFirstPageKey.length() > 0 )
            {
                elem = XMLTool.createElementIfNotPresent( doc, newMenuElem, "firstpage" );
                elem.setAttribute( "key", String.valueOf( copyContext.getMenuItemKey( Integer.parseInt( oldFirstPageKey ) ) ) );
            }

            if ( oldLoginPageKey != null && oldLoginPageKey.length() > 0 )
            {
                elem = XMLTool.createElementIfNotPresent( doc, newMenuElem, "loginpage" );
                elem.setAttribute( "key", String.valueOf( copyContext.getMenuItemKey( Integer.parseInt( oldLoginPageKey ) ) ) );
            }

            if ( oldErrorPageKey != null && oldErrorPageKey.length() > 0 )
            {
                elem = XMLTool.createElementIfNotPresent( doc, newMenuElem, "errorpage" );
                elem.setAttribute( "key", String.valueOf( copyContext.getMenuItemKey( Integer.parseInt( oldErrorPageKey ) ) ) );
            }

            if ( oldDefaultPageTemplateKey != null && oldDefaultPageTemplateKey.length() > 0 )
            {
                elem = XMLTool.createElement( doc, newMenuElem, "defaultpagetemplate" );
                elem.setAttribute( "pagetemplatekey",
                                   String.valueOf( copyContext.getPageTemplateKey( Integer.parseInt( oldDefaultPageTemplateKey ) ) ) );
            }

            if ( copyContext.isIncludeContents() )
            {
                menuitemsElem = XMLTool.getElement( newMenuElem, "menuitems" );
                prepareUpdate( menuitemsElem );
            }

            // update default css
            Element menudataElem = XMLTool.getElement( newMenuElem, "menudata" );
            Element defaultcssElem = XMLTool.getElement( menudataElem, "defaultcss" );
            if ( defaultcssElem != null )
            {
                String cssKey = defaultcssElem.getAttribute( "key" );
                if ( cssKey != null && cssKey.length() > 0 )
                {
                    defaultcssElem.setAttribute( "key", cssKey );
                }
            }

            updateMenu( user, copyContext, doc, true );

            // post-process content objects and page templates
            contentObjectHandler.copyContentObjectsPostOp( oldMenuKey, copyContext );
            pageTemplateHandler.copyPageTemplatesPostOp( oldMenuKey, copyContext );
        }
    }

    public void copyMenu( User user, int menuKey, boolean includeContents )
        throws VerticalCopyException
    {

        try
        {
            Document doc = getMenu( user, menuKey, true, true );
            Element menuElem = XMLTool.getFirstElement( doc.getDocumentElement() );
            if ( menuElem != null )
            {
                //int siteKey = Integer.parseInt(menuElem.getAttribute("sitekey"));

                CopyContext copyContext = new CopyContext();
                copyContext.setUser( user );
                //copyContext.setOldSiteKey(siteKey);
                //copyContext.setNewSiteKey(siteKey);
                copyContext.setIncludeContents( includeContents );

                copyMenu( copyContext, menuElem );
            }
        }
        catch ( VerticalEngineException vee )
        {
            String MESSAGE_00 = "Failed to copy pages: %0";
            Object[] msgData = new Object[]{vee.getMessage()};
            VerticalEngineLogger.errorCopy( this.getClass(), 10, MESSAGE_00, msgData, vee );
        }
    }

    private void prepareCopy( Element menuitemsElem, CopyContext copyContext )
    {

        //int newSiteKey = copyContext.getNewSiteKey();
        boolean includeContents = copyContext.isIncludeContents();

        Element[] menuitemElems = XMLTool.getElements( menuitemsElem );
        for ( Element menuitemElem : menuitemElems )
        {

            String type = menuitemElem.getAttribute( "type" );
            // content is document
            if ( "content".equals( type ) )
            {

                Element pageElem = XMLTool.getElement( menuitemElem, "page" );
                pageElem.removeAttribute( "key" );
                //pageElem.setAttribute("sitekey", Integer.toString(newSiteKey));
                String oldPageTemplateKey = pageElem.getAttribute( "pagetemplatekey" );
                pageElem.setAttribute( "pagetemplatekey",
                                       String.valueOf( copyContext.getPageTemplateKey( Integer.parseInt( oldPageTemplateKey ) ) ) );

                Element contentobjectsElem = XMLTool.getElement( pageElem, "contentobjects" );
                Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );
                for ( Element contentobjectElem : contentobjectElems )
                {
                    String oldContentObjectKey = contentobjectElem.getAttribute( "conobjkey" );
                    contentobjectElem.setAttribute( "conobjkey", String.valueOf(
                        copyContext.getContentObjectKey( Integer.parseInt( oldContentObjectKey ) ) ) );
                }

                Element documentElem = XMLTool.getElement( menuitemElem, "document" );
                if ( !includeContents )
                {
                    XMLTool.removeChildNodes( documentElem, false );
                    XMLTool.createCDATASection( documentElem.getOwnerDocument(), documentElem, "Scratch document" );
                }
            }
            else if ( "page".equals( type ) )
            {

                Element pageElem = XMLTool.getElement( menuitemElem, "page" );
                pageElem.removeAttribute( "key" );
                //pageElem.setAttribute("sitekey", Integer.toString(newSiteKey));
                String oldPageTemplateKey = pageElem.getAttribute( "pagetemplatekey" );
                pageElem.setAttribute( "pagetemplatekey",
                                       String.valueOf( copyContext.getPageTemplateKey( Integer.parseInt( oldPageTemplateKey ) ) ) );

                Element contentobjectsElem = XMLTool.getElement( pageElem, "contentobjects" );
                Element[] contentobjectElems = XMLTool.getElements( contentobjectsElem );
                for ( Element contentobjectElem : contentobjectElems )
                {
                    String oldContentObjectKey = contentobjectElem.getAttribute( "conobjkey" );
                    contentobjectElem.setAttribute( "conobjkey", String.valueOf(
                        copyContext.getContentObjectKey( Integer.parseInt( oldContentObjectKey ) ) ) );
                }
            }

            Element elem = XMLTool.getElement( menuitemElem, "menuitems" );
            prepareCopy( elem, copyContext );
        }
    }

    private void prepareUpdate( Element menuitemsElem )
    {

        Element[] menuitemElems = XMLTool.getElements( menuitemsElem );
        for ( Element menuitemElem : menuitemElems )
        {

            String type = menuitemElem.getAttribute( "type" );
            // content is document
            if ( "content".equals( type ) )
            {
                XMLTool.getElement( menuitemElem, "document" );
            }

            Element elem = XMLTool.getElement( menuitemElem, "menuitems" );
            prepareUpdate( elem );
        }
    }

    public Document getMenuItemsByContentObject( User user, int cobKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        Document doc = XMLTool.createDocument( "menuitems" );

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( MENU_ITEMS_BY_CONTENTOBJECT );
            preparedStmt.setInt( 1, cobKey );
            resultSet = preparedStmt.executeQuery();

            Element rootElem = doc.getDocumentElement();
            while ( resultSet.next() )
            {
                int meiKey = resultSet.getInt( "mei_lKey" );
                Document menuItemDoc = getMenuItem( user, meiKey, -1, false, false, false, false, true, false, false, 1 );
                Element tmpElem = (Element) menuItemDoc.getDocumentElement().getFirstChild();
                if ( tmpElem != null )
                {  // Fix for nullpointerexception discovered on BergHansen, user did not have access to menuitem
                    rootElem.appendChild( doc.importNode( tmpElem, true ) );
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.warn( this.getClass(), 10, "A database error occurred. XML may be incomplete.", e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;
    }

    public Document getMenuItemsByPageTemplates( User user, int[] pageTemplateKeys )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        Document doc = XMLTool.createDocument( "menuitems" );

        if ( pageTemplateKeys != null && pageTemplateKeys.length > 0 )
        {
            try
            {
                StringBuffer sql = new StringBuffer( MENU_ITEMS_BY_PAGETEMPLATES );
                for ( int i = 0; i < pageTemplateKeys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        sql.append( ',' );
                    }
                    sql.append( pageTemplateKeys[i] );
                }
                sql.append( "))" );

                con = getConnection();
                preparedStmt = con.prepareStatement( sql.toString() );
                resultSet = preparedStmt.executeQuery();

                Element rootElem = doc.getDocumentElement();
                while ( resultSet.next() )
                {
                    int meiKey = resultSet.getInt( "mei_lKey" );

                    Document menuItemDoc = getMenuItem( user, meiKey, -1, false, false, false, false, true, false, false, 1 );

                    Element tmpElem = (Element) menuItemDoc.getDocumentElement().getFirstChild();
                    if ( tmpElem != null )
                    {
                        rootElem.appendChild( doc.importNode( tmpElem, true ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                VerticalEngineLogger.warn( this.getClass(), 10, "A database error occurred. XML may be incomplete.", e );
            }
            finally
            {
                close( resultSet );
                close( preparedStmt );
                close( con );
            }
        }

        return doc;
    }

    /**
     * Builds a menu tree, typically used to present the menu in the menu ;)
     *
     * @param user           The current user
     * @param getterSettings If given - build tree from these settings
     * @return A DOM document containing the menu tree
     */
    public Document getMenusForAdmin( User user, MenuGetterSettings getterSettings )
    {

        List<Integer> paramValues = new ArrayList<Integer>( 2 );
        StringBuffer sqlMenus = new StringBuffer( MENU_SELECT );

        if ( getterSettings.hasMenuKeys() )
        {
            int[] menuKeys = getterSettings.getMenuKeys();
            sqlMenus.append( " WHERE " );
            sqlMenus.append( "men_lKey IN (" );
            for ( int i = 0; i < menuKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sqlMenus.append( ',' );
                }
                sqlMenus.append( menuKeys[i] );
            }
            sqlMenus.append( ')' );
        }

        // Selekterer bare menyer som brukeren har create eller administrate p�
        if ( user != null )
        {
            MenuCriteria criteria = getterSettings.getMenuCriteria();
            getSecurityHandler().appendMenuSQL( user, sqlMenus, criteria );
        }

        Hashtable<String, Element> hashtable_menus = new Hashtable<String, Element>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Document doc = XMLTool.createDocument( "menus" );
        try
        {
            con = getConnection();

            statement = con.prepareStatement( sqlMenus.toString() );
            JDBCUtil.addValuesToPreparedStatement( statement, paramValues );

            try
            {

                // Først selekterer vi ut menus...
                resultSet = statement.executeQuery();

                while ( resultSet.next() )
                {

                    int curMenuKey = resultSet.getInt( "men_lKey" );

                    Element element_Menu = getMenuData( doc.getDocumentElement(), curMenuKey );
                    Element accessRights = XMLTool.createElement( doc, element_Menu, "accessrights" );
                    getSecurityHandler().appendAccessRightsOnDefaultMenuItem( user, curMenuKey, accessRights, true );

                    XMLTool.createElement( doc, element_Menu, "menuitems" );
                    // Lagrer referansen til kategori-elementet for raskt oppslag til senere bruk
                    hashtable_menus.put( String.valueOf( curMenuKey ), element_Menu );
                }
            }
            finally
            {
                close( resultSet );
                resultSet = null;
                close( statement );
                statement = null;
            }

            MenuItemCriteria menuItemCriteria = getterSettings.getMenuItemCriteria();
            if ( !menuItemCriteria.getDisableMenuItemLoadingForUnspecified() || menuItemCriteria.getMenuKey() >= 0 )
            {
                // Legger så til menuitems til menyene
                Hashtable<String, Element> hashtable_MenuItems = appendMenuItemsBySettings( user, doc, hashtable_menus, getterSettings );

                // Hvis brukeren bare vil ha et fullt tree fra gitte menuItemKey,
                // så må vi fjerne alle søsken-noder til foreldre-nodene
                if ( getterSettings.hasOnlyChildrenFromThisMenuItemKey() )
                {
                    Element curMenuItem = hashtable_MenuItems.get( getterSettings.getMenuItemKeyAsInteger().toString() );

                    while ( curMenuItem != null )
                    {

                        XMLTool.removeAllSiblings( curMenuItem );

                        // Move to first parent node (menuitems)
                        curMenuItem = (Element) curMenuItem.getParentNode();
                        // Exit loop if we have reached the top
                        if ( "menus".equals( curMenuItem.getNodeName() ) )
                        {
                            break;
                        }

                        // Move to next parent node (menuitem/menu)
                        if ( curMenuItem != null )
                        {
                            curMenuItem = (Element) curMenuItem.getParentNode();
                        }
                    }
                }

                if ( getterSettings.hasTagParentsOfThisMenuItem() )
                {

                    Element menuItem = hashtable_MenuItems.get( getterSettings.getTagParentsOfThisMenuItemKeyAsInteger().toString() );
                    Node parent = menuItem.getParentNode();
                    String tagName = getterSettings.getTagParentsOfThisMenuItemTagName();
                    String tagValue = getterSettings.getTagParentsOfThisMenuItemTagValue();
                    while ( parent != null )
                    {
                        if ( parent instanceof Element )
                        {
                            ( (Element) parent ).setAttribute( tagName, tagValue );
                        }
                        parent = parent.getParentNode();
                    }
                }
            }

            if ( !getterSettings.hasMenuKeys() )
            {
                String sqlMenuItems;
                sqlMenuItems = "SELECT DISTINCT mei_men_lKey FROM tMenuItem JOIN tMenu on tMenuItem.mei_men_lKey = tMenu.men_lKey ";
                sqlMenuItems = getSecurityHandler().appendMenuItemSQL( user, sqlMenuItems, menuItemCriteria );

                statement = con.prepareStatement( sqlMenuItems );
                resultSet = statement.executeQuery();
                while ( resultSet.next() )
                {
                    String menuKey = resultSet.getString( "mei_men_lKey" );
                    if ( !hashtable_menus.containsKey( menuKey ) )
                    {
                        Element menuElement = getMenuData( doc.getDocumentElement(), Integer.parseInt( menuKey ) );

                        hashtable_menus.put( menuKey, menuElement );
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get the menus: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
            doc = XMLTool.createDocument( "categories" );
        }
        finally
        {
            close( resultSet );
            close( statement );
            close( con );
        }

        return doc;
    }

    private Hashtable<String, Element> appendMenuItemsBySettings( User user, Document doc, Hashtable<String, Element> hashtable_menus,
                                                                  MenuGetterSettings getterSettings )
    {

        Hashtable<String, Element> hashtable_MenuItems = new Hashtable<String, Element>();

        List<Integer> paramValues = new ArrayList<Integer>( 2 );
        StringBuffer sqlMenuItems = new StringBuffer( MENU_ITEM_SELECT );

        // menuKey
        MenuItemCriteria criteria = getterSettings.getMenuItemCriteria();
        if ( getterSettings.hasMenuKeys() || criteria.hasMenuKey() )
        {
            if ( getterSettings.hasMenuKeys() )
            {
                int[] menuKeys = getterSettings.getMenuKeys();
                sqlMenuItems.append( " AND men_lKey IN (" );
                for ( int i = 0; i < menuKeys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        sqlMenuItems.append( ',' );
                    }
                    sqlMenuItems.append( menuKeys[i] );
                }

                sqlMenuItems.append( ')' );
            }
            else
            {
                sqlMenuItems.append( " AND mei_men_lKey = ?" );
                paramValues.add( criteria.getMenuKeyAsInteger() );
            }
        }
        // only root menuitems
        if ( getterSettings.getOnlyRootMenuItems() )
        {
            sqlMenuItems.append( " AND mei_lParent IS NULL" );
        }

        if ( user != null )
        {
            getSecurityHandler().appendMenuItemSQL( user, sqlMenuItems, criteria );
        }

        sqlMenuItems.append( ORDER_BY );

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        Element element_AdminReadMenuItems = doc.getDocumentElement();
        try
        {
            con = getConnection();

            statement = con.prepareStatement( sqlMenuItems.toString() );

            int i = 1;
            for ( Iterator<Integer> iter = paramValues.iterator(); iter.hasNext(); i++ )
            {
                Object paramValue = iter.next();
                statement.setObject( i, paramValue );
            }

            try
            {
                // Hender ut menuitems
                resultSet = statement.executeQuery();
                while ( resultSet.next() )
                {

                    int curMenuItemKey = resultSet.getInt( "mei_lKey" );

                    Element menuItem =
                        buildMenuItemXML( doc, element_AdminReadMenuItems, resultSet, -1, false, false, true, true, false, 1 );
                    Element accessRights = XMLTool.createElement( doc, menuItem, "accessrights" );
                    getSecurityHandler().appendAccessRightsOnMenuItem( user, curMenuItemKey, accessRights, true );

                    XMLTool.createElement( doc, menuItem, "menuitems" );
                    // Lagrer referansen til kategori-elementet for raskt oppslag til senere bruk
                    hashtable_MenuItems.put( String.valueOf( curMenuItemKey ), menuItem );
                }
            }
            finally
            {
                close( resultSet );
                resultSet = null;
                close( statement );
                statement = null;
            }

            // Går igjennom menuitems og bygger opp trestrukturen
            Element curAdminReadMenuItem = (Element) element_AdminReadMenuItems.getFirstChild();
            while ( curAdminReadMenuItem != null )
            {

                String parentKey = curAdminReadMenuItem.getAttribute( "parent" );

                Element nextElement = (Element) curAdminReadMenuItem.getNextSibling();

                // Forelder node finnes ikke fra før
                if ( "menuitem".equals( curAdminReadMenuItem.getNodeName() ) )
                {
                    insertParentMenuItem( user, MENU_ITEM_SELECT_BY_KEY, doc, element_AdminReadMenuItems, hashtable_menus,
                                          hashtable_MenuItems, curAdminReadMenuItem, parentKey );
                }

                curAdminReadMenuItem = nextElement;
            }

        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get menuitems: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( resultSet );
            close( statement );
            close( con );
        }

        return hashtable_MenuItems;
    }

    private void insertParentMenuItem( User user, String sqlSelectByKey, Document doc, Element element_MenuItems,
                                       Hashtable<String, Element> hash_Menus, Hashtable<String, Element> hash_MenuItems,
                                       Element childMenuItem, String parentMenuItemKey )
        throws SQLException
    {

        // Sjekker om vi har nådd root-nivå
        if ( parentMenuItemKey.trim().length() == 0 )
        {

            // Vi må finne menu og legge menuitemen inn i den
            String menuKey = childMenuItem.getAttribute( "menukey" );
            Element element_Menu = ( hash_Menus != null ? hash_Menus.get( menuKey ) : null );
            Element menuitems;
            if ( element_Menu == null )
            {
                element_Menu = getMenuData( doc.getDocumentElement(), Integer.parseInt( menuKey ) );
                Element accessRights = XMLTool.createElement( doc, element_Menu, "accessrights" );
                getSecurityHandler().appendAccessRightsOnDefaultMenuItem( user, Integer.parseInt( menuKey ), accessRights, true );
                menuitems = XMLTool.createElement( doc, element_Menu, "menuitems" );
                menuitems.setAttribute( "istop", "yes" );
                if ( hash_Menus != null )
                {
                    hash_Menus.put( menuKey, element_Menu );
                }
            }
            else
            {
                menuitems = XMLTool.getElement( element_Menu, "menuitems" );
            }

            XMLTool.moveNode( childMenuItem, element_MenuItems, menuitems );

            return;
        }

        // Henter referanse til foreldernode (blir null hvis ikke finnes)
        Element parentMenuItem = ( hash_MenuItems != null ? hash_MenuItems.get( parentMenuItemKey ) : null );

        // Vi har lastet foreldernode fra f�r, bare legge barnet under denne
        if ( parentMenuItem != null )
        {
            Element menuitems = XMLTool.getElement( parentMenuItem, "menuitems" );
            XMLTool.moveNode( childMenuItem, element_MenuItems, menuitems );
            return;
        }

        // Vi har ikke lastet foreldrenode fra f�r, last foreldrenode fra databasen
        parentMenuItem = buildMenuItem( user, sqlSelectByKey, Integer.parseInt( parentMenuItemKey ), element_MenuItems );

        Element menuItems = XMLTool.createElement( doc, parentMenuItem, "menuitems" );
        XMLTool.moveNode( childMenuItem, element_MenuItems, menuItems );
        //
        // Lagrer referansen til menuitem-elementet for raskt oppslag til senere bruk
        if ( hash_MenuItems != null )
        {
            hash_MenuItems.put( parentMenuItemKey, parentMenuItem );
        }

        String parent = parentMenuItem.getAttribute( "parent" );
        insertParentMenuItem( user, sqlSelectByKey, doc, element_MenuItems, hash_Menus, hash_MenuItems, parentMenuItem, parent );
    }

    /**
     * Builds and appends the xml for a given menuitem into a given menuitems element.
     *
     * @param user           The current user
     * @param sqlSelectByKey The sql string (that selects the one menuitem)
     * @param menuItemKey    The key to the menuitem to build
     * @param menuItems      The element to append the menuitem Element into
     * @return The menuitem element
     * @throws SQLException When the menu item cannot be found or a more serious database error happens.
     */
    private Element buildMenuItem( User user, String sqlSelectByKey, int menuItemKey, Element menuItems )
        throws SQLException
    {

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Connection con = null;
        try
        {
            con = getConnection();
            statement = con.prepareStatement( sqlSelectByKey );
            statement.setInt( 1, menuItemKey );
            resultSet = statement.executeQuery();
            resultSet.next();

            Document document = menuItems.getOwnerDocument();
            Element menuItem = buildMenuItemXML( document, menuItems, resultSet, -1, false, false, true, true, true, 1 );
            Element accessRights = XMLTool.createElement( document, menuItem, "accessrights" );
            getSecurityHandler().appendAccessRightsOnMenuItem( user, menuItemKey, accessRights, true );

            return menuItem;
        }
        finally
        {
            close( resultSet );
            close( statement );
            close( con );
        }
    }

    public StringBuffer getPathString( int menuItemKey, boolean includeMenu, boolean includeSpace )
    {
        CommonHandler commonHandler = getCommonHandler();
        StringBuffer path =
            commonHandler.getPathString( db.tMenuItem, db.tMenuItem.mei_lKey, db.tMenuItem.mei_lParent, db.tMenuItem.mei_sName, menuItemKey,
                                         null, includeSpace );
        if ( includeMenu )
        {
            if ( includeSpace )
            {
                path.insert( 0, " / " );
            }
            else
            {
                path.insert( 0, "/" );
            }
            path.insert( 0, getMenuName( getMenuKeyByMenuItem( menuItemKey ) ) );
        }
        return path;
    }

    public ResourceKey getDefaultCSSByMenu( int menuKey )
    {
        Document menuDoc = getCommonHandler().getDocument( db.tMenu, menuKey );
        if ( menuDoc == null )
        {
            return null;
        }

        Element defaultCSSElem = XMLTool.getElement( menuDoc.getDocumentElement(), "defaultcss" );
        if ( defaultCSSElem == null )
        {
            return null;
        }

        final String keyStr = defaultCSSElem.getAttribute( "key" );
        if ( keyStr == null || keyStr.length() == 0 )
        {
            return null;
        }
        return new ResourceKey( keyStr );
    }

//    public int getMenuItemLevel( int menuItemKey )
//    {
//        int level = 0;
//        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_lParent, false, db.tMenuItem.mei_lKey );
//        int parentKey = getCommonHandler().getInt( sql.toString(), menuItemKey );
//        while ( parentKey != -1 )
//        {
//            level++;
//            parentKey = getCommonHandler().getInt( sql.toString(), parentKey );
//        }
//        return level;
//    }

    public void setMenuItemOrder( int menuItemKey, int order )
    {
        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, db.tMenuItem.mei_lOrder, db.tMenuItem.mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), new int[]{order, menuItemKey} );
    }

    public void setMenuItemType( int menuItemKey, int type )
    {
        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, db.tMenuItem.mei_mid_lkey, db.tMenuItem.mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), new int[]{type, menuItemKey} );
    }

    public void setMenuItemParent( int menuItemKey, int parentKey )
    {
        Integer parentKeyInt = parentKey;
        if ( parentKey == -1 )
        {
            parentKeyInt = null;
        }

        StringBuffer sql = XDG.generateUpdateSQL( db.tMenuItem, db.tMenuItem.mei_lParent, db.tMenuItem.mei_lKey );
        getCommonHandler().executeSQL( sql.toString(), new Integer[]{parentKeyInt, menuItemKey} );
    }

    public Document getAdminMenu( User user, int[] menuKeys, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        Connection conn = null;
        Document doc = XMLTool.createDocument( "menus" );

        if ( menuKeys == null )
        {
            menuKeys = new int[0];
        }

        User anonUser = getUserHandler().getAnonymousUser();
        if ( user == null )
        {
            user = anonUser;
        }

        String anonGroupKey = anonUser.getUserGroupKey().toString();
        boolean adminRights = getSecurityHandler().isEnterpriseAdmin( user );
        String[] groupKeys = getGroupHandler().getAllGroupMembershipsForUser( user );

        try
        {
            conn = getConnection();
            HashMap<Integer, Element> menuMap =
                findAdminMenuElements( doc, conn, groupKeys, anonGroupKey, adminRights, includeReadOnlyAccessRight );
            HashMap<Integer, List<Element>> menuItemMap = new HashMap<Integer, List<Element>>();

            for ( int menuKey : menuKeys )
            {
                menuItemMap.put( menuKey, findAdminMenuItemElements( doc, conn, menuKey, groupKeys, anonGroupKey, adminRights ) );
            }

            composeAdminMenu( doc, menuMap, menuItemMap, menuItemTypes, includeReadOnlyAccessRight );
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.error( this.getClass(), 0, "Failed to get admin menu", e );
        }
        finally
        {
            close( conn );
        }

        return doc;
    }

    private void composeAdminMenu( Document doc, HashMap<Integer, Element> menuMap, HashMap<Integer, List<Element>> menuItemMap,
                                   String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        // Add menu elements
        Element root = doc.getDocumentElement();
        for ( Element element : menuMap.values() )
        {
            root.appendChild( element );
        }

        // Add menu item elements
        for ( Object o : menuItemMap.entrySet() )
        {
            Map.Entry entry = (Map.Entry) o;
            Integer menuKey = (Integer) entry.getKey();
            List menuItems = (List) entry.getValue();

            Element currentMenu = menuMap.get( menuKey );
            if ( currentMenu != null )
            {
                for ( Object menuItem : menuItems )
                {
                    currentMenu.appendChild( (Element) menuItem );
                }
            }
        }

        // Filter the menus
        Element[] menuElements = XMLTool.getElements( root );
        for ( Element menuElement : menuElements )
        {
            filterAdminMenuItemElements( menuElement, menuItemTypes, includeReadOnlyAccessRight );
        }
    }

    private void filterAdminMenuItemElements( Element root, String[] menuItemTypes, boolean includeReadOnlyAccessRight )
    {
        boolean adminAccess = "true".equals( root.getAttribute( "useradministrate" ) );
        Element[] children = XMLTool.getElements( root );
        for ( Element aChildren : children )
        {
            if ( !showAdminMenuItemElement( aChildren, adminAccess, menuItemTypes, includeReadOnlyAccessRight ) )
            {
                root.removeChild( aChildren );
            }
            else
            {
                filterAdminMenuItemElements( aChildren, menuItemTypes, includeReadOnlyAccessRight );
            }
        }
    }

    private boolean showAdminMenuItemElement( Element root, boolean parentAdmin, String[] menuItemTypes,
                                              boolean includeReadOnlyAccessRight )
    {
        boolean typeFiltered;
        if ( menuItemTypes != null && menuItemTypes.length > 0 )
        {
            String type = root.getAttribute( "type" );
            typeFiltered = Arrays.binarySearch( menuItemTypes, type ) >= 0;
        }
        else
        {
            typeFiltered = true;
        }

        boolean addAccess = "true".equals( root.getAttribute( "useradd" ) );
        boolean publishAccess = "true".equals( root.getAttribute( "userpublish" ) );
        boolean createAccess = "true".equals( root.getAttribute( "usercreate" ) );
        boolean updateAccess = "true".equals( root.getAttribute( "userupdate" ) );
        boolean adminAccess = "true".equals( root.getAttribute( "useradministrate" ) );

        boolean hasAnyReadAccess = "true".equals( root.getAttribute( "anonread" ) ) || "true".equals( root.getAttribute( "userread" ) );
        boolean readAccess = includeReadOnlyAccessRight && hasAnyReadAccess;

        boolean hasAccess = parentAdmin || adminAccess || createAccess || updateAccess || publishAccess || addAccess || readAccess;

        if ( hasAccess && typeFiltered )
        {
            return true;
        }
        else if ( hasAccess && !typeFiltered )
        {
            Element[] children = XMLTool.getElements( root );
            for ( Element aChildren : children )
            {
                if ( showAdminMenuItemElement( aChildren, true, menuItemTypes, includeReadOnlyAccessRight ) )
                {
                    return true;
                }
            }
            return false;
        }
        else
        {
            // hasAccess == false
            Element[] children = XMLTool.getElements( root );
            for ( Element aChildren : children )
            {
                if ( showAdminMenuItemElement( aChildren, parentAdmin, menuItemTypes, includeReadOnlyAccessRight ) )
                {
                    return true;
                }
            }
            return false;
        }
    }

    private HashMap<Integer, Element> findAdminMenuElements( Document doc, Connection conn, String[] groupKeys, String anonGroupKey,
                                                             boolean adminRights, boolean includeReadOnlyAccessRight )
        throws SQLException
    {
        // Find access rights
        HashMap<Integer, Integer> accessRights = findAdminMenuAccessRights( conn, groupKeys, anonGroupKey, adminRights );

        // Composte the sql
        StringBuffer sql = new StringBuffer( "SELECT " );
        sql.append( this.db.tMenu.men_lKey.getName() ).append( ", " );
        sql.append( this.db.tMenu.men_sName.getName() ).append( ", " );
        sql.append( this.db.tLanguage.lan_sCode.getName() ).append( ", " );

        sql.append( this.db.tMenu.men_mei_firstPage.getName() ).append( ", " );
        sql.append( this.db.tMenu.men_mei_loginPage.getName() ).append( ", " );
        sql.append( this.db.tMenu.men_mei_errorPage.getName() ).append( ", " );
        sql.append( this.db.tMenu.men_pat_lKey.getName() ).append( ", " );
        sql.append( this.db.tMenu.men_xmlData.getName() );

        sql.append( " FROM " ).append( this.db.tMenu.getName() );
        sql.append( " LEFT JOIN " ).append( this.db.tLanguage.getName() );
        sql.append( " ON " ).append( this.db.tMenu.men_lan_lKey.getName() ).append( " = " ).append( this.db.tLanguage.lan_lKey.getName() );

        if ( !adminRights )
        {
            sql.append( " WHERE (" );

            StringBuffer grpSql = new StringBuffer();
            grpSql.append( " IN (" );
            for ( int i = 0; i < groupKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    grpSql.append( ", " );
                }

                grpSql.append( "'" ).append( groupKeys[i] ).append( "'" );
            }
            grpSql.append( ")" );

            sql.append( "EXISTS (SELECT * FROM " ).append( this.db.tDefaultMenuAR.getName() );
            sql.append( " WHERE " ).append( this.db.tDefaultMenuAR.dma_men_lKey.getName() );
            sql.append( " = " ).append( this.db.tMenu.men_lKey.getName() ).append( " AND " );
            sql.append( this.db.tDefaultMenuAR.dma_grp_hKey.getName() ).append( grpSql ).append( " AND (" );
            sql.append( this.db.tDefaultMenuAR.dma_bAdd.getName() ).append( " = 1 OR " );
            sql.append( this.db.tDefaultMenuAR.dma_bAdministrate.getName() ).append( " = 1 OR " );
            sql.append( this.db.tDefaultMenuAR.dma_bCreate.getName() ).append( " = 1 OR " );
            sql.append( this.db.tDefaultMenuAR.dma_bUpdate.getName() ).append( " = 1 OR " );

            if ( includeReadOnlyAccessRight )
            {
                sql.append( this.db.tDefaultMenuAR.dma_bRead.getName() ).append( " = 1 OR " );
            }

            sql.append( this.db.tDefaultMenuAR.dma_bDelete.getName() ).append( " = 1)) OR " );

            sql.append( "EXISTS (SELECT * FROM " ).append( this.db.tMenuItemAR.getName() );
            sql.append( " WHERE " ).append( this.db.tMenuItemAR.mia_mei_lKey.getName() ).append( " IN (" );
            sql.append( "SELECT " ).append( this.db.tMenuItem.mei_lKey.getName() ).append( " FROM " );
            sql.append( this.db.tMenuItem.getName() ).append( " WHERE " );
            sql.append( this.db.tMenuItem.mei_men_lKey.getName() ).append( " = " ).append( this.db.tMenu.men_lKey.getName() );
            sql.append( ") AND " ).append( this.db.tMenuItemAR.mia_grp_hKey.getName() ).append( grpSql ).append( " AND (" );
            sql.append( this.db.tMenuItemAR.mia_bAdd.getName() ).append( " = 1 OR " );
            sql.append( this.db.tMenuItemAR.mia_bAdministrate.getName() ).append( " = 1 OR " );
            sql.append( this.db.tMenuItemAR.mia_bCreate.getName() ).append( " = 1 OR " );
            sql.append( this.db.tMenuItemAR.mia_bUpdate.getName() ).append( " = 1 OR " );

            if ( includeReadOnlyAccessRight )
            {
                sql.append( this.db.tMenuItemAR.mia_bRead.getName() ).append( " = 1 OR " );
            }

            sql.append( this.db.tMenuItemAR.mia_bDelete.getName() ).append( " = 1))" );

            sql.append( ")" );
        }

        // Execute the sql
        HashMap<Integer, Element> elements = new HashMap<Integer, Element>();
        PreparedStatement stmt = null;
        ResultSet result = null;

        try
        {
            stmt = conn.prepareStatement( sql.toString() );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Integer menKey = result.getInt( 1 );
                String name = result.getString( 2 );
                String languageCode = result.getString( 3 );
                Element elem = doc.createElement( "menu" );
                elem.setAttribute( "key", menKey.toString() );
                elem.setAttribute( "name", name );
                appendMenuAccessRights( elem, accessRights.get( menKey ) );
                elem.setAttribute( "language", languageCode );

                int firstPage = result.getInt( db.tMenu.men_mei_firstPage.getName() );
                if ( !result.wasNull() )
                {
                    elem.setAttribute( "firstpage", String.valueOf( firstPage ) );
                }
                int loginPage = result.getInt( db.tMenu.men_mei_loginPage.getName() );
                if ( !result.wasNull() )
                {
                    elem.setAttribute( "loginpage", String.valueOf( loginPage ) );
                }
                int errorPage = result.getInt( db.tMenu.men_mei_errorPage.getName() );
                if ( !result.wasNull() )
                {
                    elem.setAttribute( "errorpage", String.valueOf( errorPage ) );
                }

                int defaultPageTemplate = result.getInt( db.tMenu.men_pat_lKey.getName() );
                if ( !result.wasNull() )
                {
                    elem.setAttribute( "defaultpagetemplate", String.valueOf( defaultPageTemplate ) );
                }

                InputStream is = result.getBinaryStream( "men_xmlData" );
                if ( !result.wasNull() )
                {
                    Document menuDataDoc = XMLTool.domparse( is );

                    boolean allowUrl = XMLTool.selectNode( menuDataDoc.getDocumentElement(), "pagetypes/allow[@type = 'url']" ) != null;
                    boolean allowLabel = XMLTool.selectNode( menuDataDoc.getDocumentElement(), "pagetypes/allow[@type = 'label']" ) != null;
                    boolean allowSection =
                        XMLTool.selectNode( menuDataDoc.getDocumentElement(), "pagetypes/allow[@type = 'section']" ) != null;
                    elem.setAttribute( "allowurl", String.valueOf( allowUrl ) );
                    elem.setAttribute( "allowlabel", String.valueOf( allowLabel ) );
                    elem.setAttribute( "allowsection", String.valueOf( allowSection ) );

                    Element defaultCSSElem = XMLTool.getElement( menuDataDoc.getDocumentElement(), "defaultcss" );
                    if ( defaultCSSElem != null )
                    {
                        String defaultCssKey = defaultCSSElem.getAttribute( "key" );
                        if ( StringUtils.isNotEmpty( defaultCssKey ) )
                        {
                            ResourceKey resourceKey = new ResourceKey( defaultCSSElem.getAttribute( "key" ) );
                            elem.setAttribute( "defaultcss", resourceKey.toString() );
                            elem.setAttribute( "defaultcssexists", resourceDao.getResourceFile( resourceKey ) != null ? "true" : "false" );
                        }
                    }
                }

                elements.put( menKey, elem );
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }

        return elements;
    }

    private void appendMenuAccessRights( Element elem, Integer accessFlag )
    {
        int flag = accessFlag != null ? accessFlag : 0;
        elem.setAttribute( "anonread", String.valueOf( ( flag & AC_ANONREAD ) == AC_ANONREAD ) );
        elem.setAttribute( "userread", String.valueOf( ( flag & AC_READ ) == AC_READ ) );
        elem.setAttribute( "useradd", String.valueOf( ( flag & AC_ADD ) == AC_ADD ) );
        elem.setAttribute( "userpublish", String.valueOf( ( flag & AC_PUBLISH ) == AC_PUBLISH ) );
        elem.setAttribute( "usercreate", String.valueOf( ( flag & AC_CREATE ) == AC_CREATE ) );
        elem.setAttribute( "userupdate", String.valueOf( ( flag & AC_UPDATE ) == AC_UPDATE ) );
        elem.setAttribute( "userdelete", String.valueOf( ( flag & AC_DELETE ) == AC_DELETE ) );
        elem.setAttribute( "useradministrate", String.valueOf( ( flag & AC_ADMIN ) == AC_ADMIN ) );
    }

    private List<Element> findAdminMenuItemElements( Document doc, Connection conn, int menuKey, String[] groupKeys, String anonGroupKey,
                                                     boolean adminRights )
        throws SQLException
    {
        // Find access rights
        HashMap<Integer, Integer> accessRights = findAdminMenuItemAccessRights( conn, menuKey, groupKeys, anonGroupKey, adminRights );

        // Composte the sql
        StringBuffer sql = new StringBuffer( "SELECT " );
        sql.append( this.db.tMenuItem.mei_lKey.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_lParent.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_sName.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_sSubTitle.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_bHidden.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_lOrder.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_mid_lkey.getName() ).append( ", " );
        sql.append( this.db.tPageTemplate.pat_lType.getName() ).append( ", " );
        sql.append( this.db.tPageTemplate.pat_sName.getName() ).append( ", " );
        sql.append( this.db.tMenuItem.mei_sDisplayName.getName() );
        sql.append( " FROM " ).append( this.db.tMenuItem.getName() );
        sql.append( " LEFT JOIN " ).append( this.db.tPage.getName() );
        sql.append( " ON " ).append( this.db.tMenuItem.mei_pag_lKey.getName() ).append( " = " ).append( this.db.tPage.pag_lKey.getName() );
        sql.append( " LEFT JOIN " ).append( this.db.tPageTemplate.getName() );
        sql.append( " ON " ).append( this.db.tPage.pag_pat_lKey.getName() ).append( " = " ).append(
            this.db.tPageTemplate.pat_lKey.getName() );
        sql.append( " WHERE " ).append( this.db.tMenuItem.mei_men_lKey.getName() );
        sql.append( " = " ).append( String.valueOf( menuKey ) );

        // Execute the sql
        ArrayList<Element> list = new ArrayList<Element>();
        HashMap<Integer, Element> elements = new HashMap<Integer, Element>();
        HashMap<Integer, Integer> keyParentMap = new HashMap<Integer, Integer>();
        PreparedStatement stmt = null;
        ResultSet result = null;

        try
        {
            stmt = conn.prepareStatement( sql.toString() );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Integer itemKey = result.getInt( 1 );
                Number parentKey = (Number) result.getObject( 2 );
                String name = result.getString( 3 );
                String alternativeName = result.getString( db.tMenuItem.mei_sSubTitle.getName() );
                boolean hidden = ( result.getInt( 5 ) == 1 || result.wasNull() );
                int order = result.getInt( 6 );
                MenuItemType menuItemType = MenuItemType.get( result.getInt( 7 ) );
                Number value = (Number) result.getObject( 8 );
                PageTemplateType pageTemplateType = value != null ? PageTemplateType.get( value.intValue() ) : null;
                String pageTemplateName = result.getString( db.tPageTemplate.pat_sName.getName() );
                String displayName = result.getString( db.tMenuItem.mei_sDisplayName.getName() );

                Element elem = doc.createElement( "menuitem" );
                elem.setAttribute( "key", itemKey.toString() );
                elem.setAttribute( "name", StringUtil.getXMLSafeString( name ) );
                if ( alternativeName != null && alternativeName.length() > 0 )
                {
                    elem.setAttribute( "alternativename", StringUtil.getXMLSafeString( alternativeName ) );
                }
                if ( StringUtils.isNotBlank( displayName ) )
                {
                    elem.setAttribute( "displayname", StringUtil.getXMLSafeString( displayName ) );
                }
                elem.setAttribute( "order", String.valueOf( order ) );
                elem.setAttribute( "visible", String.valueOf( !hidden ) );
                if ( pageTemplateName != null )
                {
                    elem.setAttribute( "pagetemplatename", pageTemplateName );
                }
                appendMenuAccessRights( elem, accessRights.get( itemKey ) );

                elem.setAttribute( "path", getMenuItemPath( itemKey ) );

                String type = null;

                if ( pageTemplateType != null )
                {
                    type = pageTemplateType.getName();
                }
                else
                {
                    if ( menuItemType == MenuItemType.URL )
                    {
                        type = "url";
                    }
                    else if ( menuItemType == MenuItemType.CONTENT )
                    {
                        type = "content";
                    }
                    else if ( menuItemType == MenuItemType.LABEL )
                    {
                        type = "label";
                    }
                    else if ( menuItemType == MenuItemType.SECTION )
                    {
                        type = "section";
                    }
                    else if ( menuItemType == MenuItemType.SHORTCUT )
                    {
                        type = "shortcut";
                    }
                }

                elem.setAttribute( "type", type );
                elements.put( itemKey, elem );

                if ( parentKey != null )
                {
                    keyParentMap.put( itemKey, parentKey.intValue() );
                }
                else
                {
                    list.add( elem );
                }
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }

        // Nest the elements
        for ( Object o : keyParentMap.entrySet() )
        {
            Map.Entry entry = (Map.Entry) o;
            Integer itemKey = (Integer) entry.getKey();
            Integer parentKey = (Integer) entry.getValue();

            Element parentElem = elements.get( parentKey );
            Element itemElem = elements.get( itemKey );

            if ( ( parentElem != null ) && ( itemElem != null ) )
            {
                parentElem.appendChild( itemElem );
            }
        }

        return list;
    }

    private HashMap<Integer, Integer> findAdminMenuAccessRights( Connection conn, String[] groupKeys, String anonGroupKey,
                                                                 boolean adminRights )
        throws SQLException
    {
        // Composte the sql
        StringBuffer sql = new StringBuffer( "SELECT " );
        sql.append( this.db.tDefaultMenuAR.dma_grp_hKey.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_men_lKey.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bRead.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bAdd.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bCreate.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bUpdate.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bPublish.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bDelete.getName() ).append( ", " );
        sql.append( this.db.tDefaultMenuAR.dma_bAdministrate.getName() );
        sql.append( " FROM " ).append( this.db.tDefaultMenuAR.getName() );
        sql.append( " WHERE " ).append( this.db.tDefaultMenuAR.dma_men_lKey.getName() );
        sql.append( " IN (SELECT " ).append( this.db.tMenu.men_lKey.getName() );
        sql.append( " FROM " ).append( this.db.tMenu.getName() ).append( ")" );

        if ( !adminRights )
        {
            sql.append( " AND " ).append( this.db.tDefaultMenuAR.dma_grp_hKey.getName() ).append( " IN (" );
            for ( int i = 0; i < groupKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ", " );
                }

                sql.append( "'" ).append( groupKeys[i] ).append( "'" );
            }
            sql.append( ")" );
        }

        // Execute the sql
        return queryAdminMenuAccessRights( conn, sql.toString(), anonGroupKey, adminRights );
    }

    private HashMap<Integer, Integer> findAdminMenuItemAccessRights( Connection conn, int menuKey, String[] groupKeys, String anonGroupKey,
                                                                     boolean adminRights )
        throws SQLException
    {
        // Composte the sql
        StringBuffer sql = new StringBuffer( "SELECT " );
        sql.append( this.db.tMenuItemAR.mia_grp_hKey.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_mei_lKey.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bRead.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bAdd.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bPublish.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bCreate.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bUpdate.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bDelete.getName() ).append( ", " );
        sql.append( this.db.tMenuItemAR.mia_bAdministrate.getName() );
        sql.append( " FROM " ).append( this.db.tMenuItemAR.getName() );
        sql.append( " WHERE " ).append( this.db.tMenuItemAR.mia_mei_lKey.getName() );
        sql.append( " IN (SELECT " ).append( this.db.tMenuItem.mei_lKey.getName() );
        sql.append( " FROM " ).append( this.db.tMenuItem.getName() ).append( " WHERE " );
        sql.append( this.db.tMenuItem.mei_men_lKey.getName() ).append( " = " ).append( String.valueOf( menuKey ) ).append( ")" );

        if ( !adminRights )
        {
            sql.append( " AND " ).append( this.db.tMenuItemAR.mia_grp_hKey.getName() ).append( " IN (" );
            for ( int i = 0; i < groupKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    sql.append( ", " );
                }

                sql.append( "'" ).append( groupKeys[i] ).append( "'" );
            }
            sql.append( ")" );
        }

        // Execute the sql
        return queryAdminMenuAccessRights( conn, sql.toString(), anonGroupKey, adminRights );
    }

    private HashMap<Integer, Integer> queryAdminMenuAccessRights( Connection conn, String sql, String anonGroupKey, boolean adminRights )
        throws SQLException
    {
        HashMap<Integer, Integer> rightsMap = new HashMap<Integer, Integer>();
        PreparedStatement stmt = null;
        ResultSet result = null;

        try
        {
            stmt = conn.prepareStatement( sql );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                String grpKey = result.getString( 1 );
                Integer menKey = result.getInt( 2 );
                boolean readAccess = result.getInt( 3 ) == 1;
                boolean addAccess = result.getInt( 4 ) == 1;
                boolean publishAccess = result.getInt( 5 ) == 1;
                boolean createAccess = result.getInt( 6 ) == 1;
                boolean updateAccess = result.getInt( 7 ) == 1;
                boolean deleteAccess = result.getInt( 8 ) == 1;
                boolean adminAccess = result.getInt( 9 ) == 1;
                int accessFlag = 0;

                if ( rightsMap.containsKey( menKey ) )
                {
                    accessFlag = rightsMap.get( menKey );
                }

                if ( grpKey.equals( anonGroupKey ) && readAccess )
                {
                    accessFlag |= AC_ANONREAD;
                }

                if ( readAccess || adminRights )
                {
                    accessFlag |= AC_READ;
                }

                if ( addAccess || adminRights )
                {
                    accessFlag |= AC_ADD;
                }

                if ( publishAccess || adminRights )
                {
                    accessFlag |= AC_PUBLISH;
                }

                if ( createAccess || adminRights )
                {
                    accessFlag |= AC_CREATE;
                }

                if ( updateAccess || adminRights )
                {
                    accessFlag |= AC_UPDATE;
                }

                if ( deleteAccess || adminRights )
                {
                    accessFlag |= AC_DELETE;
                }

                if ( adminAccess || adminRights )
                {
                    accessFlag |= AC_ADMIN;
                }

                rightsMap.put( menKey, accessFlag );
            }
        }
        finally
        {
            close( result );
            close( stmt );
        }

        return rightsMap;
    }

    public String getMenuItemPath( int menuItemKey )
    {
        final MenuItemEntity entity = menuItemDao.findByKey( menuItemKey );
        return entity != null ? entity.getPathAsString() : null;
    }

    public void updateMenuDetails( int menuKey, int frontPageKey, int loginPageKey, int errorPageKey, int defaultPageTemplateKey )
    {
        Integer frontPageKeyInt = frontPageKey != -1 ? frontPageKey : null;
        Integer loginPageKeyInt = loginPageKey != -1 ? loginPageKey : null;
        Integer errorPageKeyInt = errorPageKey != -1 ? errorPageKey : null;
        Integer defaultPageTemplateKeyInt = defaultPageTemplateKey != -1 ? defaultPageTemplateKey : null;

        StringBuffer sql = XDG.generateUpdateSQL( db.tMenu, new Column[]{db.tMenu.men_mei_firstPage, db.tMenu.men_mei_loginPage,
            db.tMenu.men_mei_errorPage, db.tMenu.men_pat_lKey}, new Column[]{db.tMenu.men_lKey}, null );
        getCommonHandler().executeSQL( sql.toString(),
                                       new Integer[]{frontPageKeyInt, loginPageKeyInt, errorPageKeyInt, defaultPageTemplateKeyInt,
                                           menuKey} );
    }

    public boolean menuItemNameExists( SiteKey siteKey, MenuItemKey parentKey, String newNameOfMenuItem, MenuItemKey excludeKey )
    {
        if ( parentKey != null )
        {
            MenuItemEntity parent = menuItemDao.findByKey( parentKey );
            final MenuItemEntity childByName = parent.getChildByName( newNameOfMenuItem );
            if ( childByName == null )
            {
                return false;
            }
            else
            {
                if ( childByName.getMenuItemKey().equals( excludeKey ) )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
        else
        {
            SiteEntity site = siteDao.findByKey( siteKey );
            final MenuItemEntity childByName = site.getChild( newNameOfMenuItem );
            if ( childByName == null )
            {
                return false;
            }
            else
            {
                if ( childByName.getMenuItemKey().equals( excludeKey ) )
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
        }
    }

    /**
     * Return a map of top level menus with name.
     *
     * @return A map with the keys of the top level menus as keys, and their names as the corresponding value.
     * @throws SQLException If a database error occurs.
     */
    public Map<Integer, String> getMenuMap()
        throws SQLException
    {
        HashMap<Integer, String> menuMap = new HashMap<Integer, String>();
        Connection conn = null;
        ResultSet result = null;
        PreparedStatement stmt = null;
        String sql = "SELECT " + this.db.tMenu.men_lKey.getName() + ", " + this.db.tMenu.men_sName + " FROM " + this.db.tMenu.getName();

        try
        {
            conn = getConnection();
            stmt = conn.prepareStatement( sql );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                menuMap.put( result.getInt( 1 ), result.getString( 2 ) );
            }
        }
        finally
        {
            close( result );
            close( stmt );
            close( conn );
        }

        return menuMap;
    }
}
