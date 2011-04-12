/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.event.MenuHandlerListener;
import com.enonic.vertical.event.VerticalEventMulticaster;

import com.enonic.cms.framework.util.TIntArrayList;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.structure.RunAsType;
import com.enonic.cms.domain.structure.SiteEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemType;
import com.enonic.cms.domain.structure.page.template.PageTemplateKey;
import com.enonic.cms.domain.structure.page.template.PageTemplateType;

public final class MenuHandler
    extends BaseHandler
{
    private static final Logger LOG = LoggerFactory.getLogger( MenuHandler.class.getName() );

    private VerticalEventMulticaster multicaster = new VerticalEventMulticaster();

    public static final String ELEMENT_NAME_MENU_NAME = "menu-name";

    public static final String ELEMENT_NAME_DISPLAY_NAME = "displayname";

    private static final String COLUMN_NAME_DISPLAY_NAME = "mei_sDisplayName";

    private static final String COLUMN_NAME_ALTERNATIVE_NAME = "mei_sSubTitle";

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

    final static private String MENU_SELECT_NAME = "SELECT men_sName FROM " + MENU_TABLE + " WHERE men_lKey = ?";

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

    final static private String ORDER_BY = " ORDER BY mei_lOrder ";

    final static private String MENU_ITEM_SELECT_BY_KEY =
        "SELECT " + MENU_ITEM_COLS + " FROM " + MENU_ITEM_TABLE + " LEFT JOIN tMenu ON tMenu.men_lKey = " + MENU_ITEM_TABLE +
            ".mei_men_lKey " + " LEFT JOIN tLanguage ON " + MENU_ITEM_TABLE + ".mei_lan_lKey = tLanguage.lan_lKey" + " WHERE mei_lKey = ?";

    final static private String MENU_GET_KEY_BY_MENU_ITEM = "SELECT mei_men_lKey FROM " + MENU_ITEM_TABLE + " WHERE mei_lKey = ?";

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
                    LOG.error( StringUtil.expandString(
                            "Received invalid XML from database, menukey=" + menuKey + ", menuitem key=" + menuItemKey +
                                    ", name=" + menuItemName + ". Running Tidy..", (Object) null, null ) );
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
                    LOG.debug( StringUtil.expandString( "Expected CDATA, found XML. Serialized it.", null, null ) );
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
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
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
            LOG.error( StringUtil.expandString( "SQL error.", (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return doc;

    }

    public String getMenuItemName( int menuItemKey )
    {
        StringBuffer sql = XDG.generateSelectSQL( db.tMenuItem, db.tMenuItem.mei_sName, false, db.tMenuItem.mei_lKey );
        return getCommonHandler().getString( sql.toString(), menuItemKey );
    }

    public int getMenuItemContentKey( int menuItemKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tMenuItemContent, db.tMenuItemContent.mic_con_lKey, false, db.tMenuItemContent.mic_mei_lKey );
        return getCommonHandler().getInt( sql.toString(), menuItemKey );
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

}
