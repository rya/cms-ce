/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.sql.model.Table;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.util.TStringArrayList;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.VerticalRuntimeException;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.ContentAccessRight;
import com.enonic.vertical.engine.MenuAccessRight;
import com.enonic.vertical.engine.MenuItemAccessRight;
import com.enonic.vertical.engine.SectionCriteria;
import com.enonic.vertical.engine.Types;
import com.enonic.vertical.engine.VerticalCreateException;
import com.enonic.vertical.engine.VerticalEngineRuntimeException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.criteria.MenuCriteria;
import com.enonic.vertical.engine.criteria.MenuItemCriteria;
import com.enonic.vertical.engine.dbmodel.CatAccessRightView;
import com.enonic.vertical.engine.dbmodel.ConAccessRightView;
import com.enonic.vertical.engine.dbmodel.ContentView;
import com.enonic.vertical.engine.dbmodel.MenuARView;
import com.enonic.vertical.engine.dbmodel.MenuItemARView;
import com.enonic.vertical.engine.dbmodel.SectionContentView;
import com.enonic.vertical.event.VerticalEventListener;

import com.enonic.cms.framework.util.UUIDGenerator;

import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.QualifiedUsername;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

final public class SecurityHandler
    extends BaseHandler
    implements VerticalEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SecurityHandler.class.getName() );


    // constants -----------------------------------------------------------------------------------

    private final static String GROUP_TABLE = "tGroup";

    private final static String USER_TABLE = "tUser";

    private final static String MENUITEMAR_TABLE = "tMenuItemAR";

    private final static String DEFAULTMENUAR_TABLE = "tDefaultMenuAR";

    private final static String CAR_TABLE = "tCatAccessRight";

    private final static String COA_TABLE = "tConAccessRight2";

    private final static String MENUITEMAR_INSERT = "INSERT INTO " + MENUITEMAR_TABLE +
        " (mia_mei_lkey, mia_grp_hkey, mia_bRead, mia_bCreate, mia_bPublish, mia_bAdministrate, mia_bUpdate, mia_bDelete, mia_bAdd)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";

    private final static String DEFAULTMENUAR_INSERT = "INSERT INTO " + DEFAULTMENUAR_TABLE +
        " (dma_men_lkey, dma_grp_hkey, dma_bRead, dma_bCreate, dma_bDelete, dma_bPublish, dma_bAdministrate, dma_bUpdate, dma_bAdd)" +
        " VALUES (?,?,?,?,?,?,?,?,?)";

    private final static String DEFAULTMENUAR_GET_ALL = "SELECT dma_grp_hKey, grp_hKey, grp_lType, usr_sUID, usr_sFullName, " +
        "grp_sName, dma_bRead, dma_bCreate, dma_bPublish, dma_bAdministrate, dma_bUpdate, dma_bDelete, dma_bAdd, usr_hkey" + " FROM " +
        DEFAULTMENUAR_TABLE + " LEFT JOIN " + GROUP_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + DEFAULTMENUAR_TABLE + ".dma_grp_hKey " +
        " LEFT JOIN " + USER_TABLE + " ON " + USER_TABLE + ".usr_grp_hKey = " + GROUP_TABLE + ".grp_hKey " + " WHERE dma_men_lKey = ?";

    private final static String DEFAULTMENUAR_REMOVE_ALL = "DELETE FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ?";

    private final static String DEFAULTMENUAR_COLS =
        "dma_men_lKey, dma_grp_hKey, dma_bRead, dma_bCreate, dma_bDelete," + "dma_bPublish, dma_bAdministrate, dma_bUpdate, dma_bAdd";

    private final static String DEFAULTMENUAR_GET_FOR_GROUPS =
        "SELECT " + DEFAULTMENUAR_COLS + " FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ? AND " + " dma_grp_hKey IN ";

    private final static String MENUITEMAR_COLS =
        "mia_mei_lKey, mia_grp_hKey, mia_bRead, mia_bCreate, mia_bPublish," + "mia_bAdministrate, mia_bUpdate, mia_bDelete, mia_bAdd";

    private final static String MENUITEMAR_GET_FOR_GROUPS =
        "SELECT " + MENUITEMAR_COLS + " FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ? AND " + " mia_grp_hKey IN ";

    private final static String MENUITEMAR_GET_ALL = "SELECT mia_mei_lKey, mia_grp_hKey, mia_bRead, mia_bCreate, mia_bPublish, " +
        "mia_bAdministrate, mia_bUpdate, mia_bDelete, mia_bAdd, grp_hKey, grp_lType, grp_sName, usr_sUID, usr_sFullName, usr_hkey" + " FROM " +
        MENUITEMAR_TABLE + " LEFT JOIN " + GROUP_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + MENUITEMAR_TABLE + ".mia_grp_hKey " +
        " LEFT JOIN " + USER_TABLE + " ON " + USER_TABLE + ".usr_grp_hKey = " + GROUP_TABLE + ".grp_hKey " + " WHERE mia_mei_lKey = ?";

    private final static String USERSTORE_GET_NAME =
        "SELECT dom_sName FROM tDomain, " + USER_TABLE + " WHERE usr_dom_lKey = dom_lKey AND usr_hkey = ? ";

    private final static String MENUITEMAR_REMOVE_ALL = "DELETE FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?";

    private final static String DEFAULTMENUAR_SECURITY_FILTER_1 =
        " AND EXISTS (SELECT dma_men_lKey FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = men_lKey" + " AND dma_grp_hKey IN (";

    private final static String MENUITEMAR_SECURITY_FILTER_1 =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lKey" + " AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_SECURITY_FILTER_ON_PARENT =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lParent" + " AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_UPDATE_1 = " SELECT DISTINCT * FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?" +
        " AND (mia_bUpdate = 1 or mia_bAdministrate = 1) AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_UPDATE_2 = ")";

    private final static String MENUITEMAR_VALIDATE_CREATE_TOPLEVEL_1 =
        " SELECT DISTINCT * FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ?" +
            " AND (dma_bCreate = 1 OR dma_bAdministrate = 1) AND dma_grp_hKey IN (";

    private final static String DEFAULTMENUAR_VALIDATE_AR_UPDATE_1 =
        " SELECT DISTINCT * FROM " + DEFAULTMENUAR_TABLE + " WHERE dma_men_lKey = ?" + " AND dma_bAdministrate = 1 AND dma_grp_hKey IN (";

    private final static String DEFAULTMENUAR_VALIDATE_AR_UPDATE_2 = ")";

    private final static String MENUITEMAR_VALIDATE_CREATE_1 = " SELECT DISTINCT * FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?" +
        " AND (mia_bCreate = 1 OR mia_bAdministrate = 1) AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_CREATE_2 = ")";

    private final static String MENUITEMAR_VALIDATE_REMOVE_1 = " SELECT DISTINCT * FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?" +
        " AND (mia_bDelete != 0 OR mia_bAdministrate != 0) AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_REMOVE_2 = ")";

    private final static String MENUITEMAR_VALIDATE_AR_UPDATE_1 =
        " SELECT DISTINCT * FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = ?" + " AND mia_bAdministrate != 0 AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_VALIDATE_AR_UPDATE_2 = ")";

    private final static String MIA_WHERE_CLAUSE_MEI = " mia_mei_lKey = ?";

    private final static String MIA_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN (";

    private final static String MENUITEMAR_SELECT =
        "SELECT mia_mei_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, mia_bRead, mia_bCreate," +
            " mia_bPublish, mia_bAdministrate, mia_bUpdate, mia_bDelete, mia_bAdd FROM " + MenuItemARView.getInstance().getReplacementSql();

    private final static String DMA_WHERE_CLAUSE_MEN = " dma_men_lKey = ?";

    private final static String DMA_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN ";

    private final static String MENUAR_SELECT =
        "SELECT dma_men_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, dma_bRead, dma_bCreate," +
            " dma_bPublish, dma_bAdministrate, dma_bUpdate, dma_bDelete FROM " + MenuARView.getInstance().getReplacementSql();

    private final static String CAR_INSERT = "INSERT INTO " + CAR_TABLE + " VALUES (?,?,?,?,?,?,?)";

    private final static String CAR_DELETE_ALL = "DELETE FROM " + CAR_TABLE + " WHERE car_cat_lKey = ?";

    private final static String CAR_SELECT =
        "SELECT car_cat_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, car_bRead, car_bCreate," +
            " car_bPublish, car_bAdministrate, car_bAdminRead FROM " + CatAccessRightView.getInstance().getReplacementSql();

    private final static String CAR_WHERE_CLAUSE_CAT = " car_cat_lKey = ?";

    private final static String CAR_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN ";

    private final static String CAR_WHERE_CLAUSE_CREATE = " car_bAdministrate = 1";

    private final static String CAR_WHERE_CLAUSE_REMOVE = " car_bAdministrate = 1";

    private final static String CAR_WHERE_CLAUSE_UPDATE = " car_bAdministrate = 1";

    private final static String CAR_WHERE_CLAUSE_ADMINREAD = " car_bAdminRead = 1";

    private final static String CAR_WHERE_CLAUSE_PUBLISH = " car_bPublish = 1";

    private final static String CAR_WHERE_CLAUSE_SECURITY_FILTER =
        " EXISTS (SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = cat_lKey" + " AND car_grp_hKey IN (%groups))";

    private final static String CAR_WHERE_CLAUSE_SECURITY_FILTER_RIGHTS =
        " EXISTS (SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = cat_lKey" + " AND car_grp_hKey IN (%groups)" +
            " %filterRights )";

    private final static String CAR_SECURITY_FILTER_PUBLISH =
        "SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = ? AND car_bPublish = 1 " + " AND car_grp_hKey IN (%0)";

    private final static String CAR_SECURITY_FILTER_ADMIN =
        "SELECT car_grp_hKey FROM " + CAR_TABLE + " WHERE car_cat_lKey = ? AND car_bAdministrate = 1 " + " AND car_grp_hKey IN (%0)";

    private final static String COA_INSERT =
        "INSERT INTO " + COA_TABLE + " (coa_con_lkey, coa_grp_hKey, coa_bRead, coa_bUpdate, coa_bDelete, coa_sKey) VALUES (?,?,?,?,?,?)";

    private final static String COA_DELETE_ALL = "DELETE FROM " + COA_TABLE + " WHERE coa_con_lKey = ?";

    private final static String COA_SELECT =
        "SELECT coa_con_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, coa_bRead," +
            " coa_bUpdate, coa_bDelete FROM " + ConAccessRightView.getInstance().getReplacementSql();

    private final static String COA_WHERE_CLAUSE_CON = " coa_con_lKey = ?";

    private final static String COA_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN ";

    private final static String COA_WHERE_CLAUSE_SECURITY_FILTER =
        " EXISTS (SELECT coa_grp_hKey FROM " + COA_TABLE + " WHERE coa_con_lKey = con_lKey" + " AND coa_grp_hKey IN (%0))";

    // methods --------------------------------------------------------------------------------------


    public void appendAccessRights( User user, Document doc, boolean includeAccessRights, boolean includeUserRights )
    {
        Element rootElement = doc.getDocumentElement();

        if ( rootElement.getTagName().equals( "menus" ) )
        {
            Node[] menus = XMLTool.filterNodes( rootElement.getChildNodes(), Node.ELEMENT_NODE );

            for ( Node menu : menus )
            {
                Element menuElement = (Element) menu;
                appendDefaultMenuItemAccessRights( user, doc, menuElement, includeAccessRights, includeUserRights );
            }
        }
        else if ( rootElement.getTagName().equals( "menuitems" ) )
        {
            appendMenuItemAccessRights( user, doc, XMLTool.filterNodes( rootElement.getChildNodes(), Node.ELEMENT_NODE ), null, null,
                                        includeAccessRights, includeUserRights );
        }
        else if ( rootElement.getTagName().equals( "categories" ) )
        {
            Element[] categoryElems = XMLTool.getElements( rootElement );
            appendCategoryAccessRights( user, categoryElems, null, includeAccessRights, includeUserRights );
        }
        else if ( rootElement.getTagName().equals( "contents" ) )
        {
            appendContentAccessRights( user, rootElement, includeAccessRights, includeUserRights );
        }
        else if ( rootElement.getTagName().equals( "sections" ) )
        //appendSectionAccessRights(user, null, rootElement, includeAccessRights, includeUserRights);
        {
            appendMenuItemAccessRights( user, doc, XMLTool.filterNodes( rootElement.getChildNodes(), Node.ELEMENT_NODE ), null, null,
                                        includeAccessRights, includeUserRights );
        }
    }

    private void appendDefaultMenuItemAccessRights( User user, Document doc, Element menuElement, boolean includeAccessRights,
                                                    boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( DEFAULTMENUAR_GET_ALL );

            appendDefaultMenuItemAccessRight( user, Integer.parseInt( menuElement.getAttribute( "key" ) ),
                                              XMLTool.createElement( doc, menuElement, "accessrights" ), preparedStmt, includeAccessRights,
                                              includeUserRights );

            Element menuItemsElement = XMLTool.getElement( menuElement, "menuitems" );
            if ( menuItemsElement != null )
            {
                appendMenuItemAccessRights( user, doc, XMLTool.filterNodes( menuItemsElement.getChildNodes(), Node.ELEMENT_NODE ), con,
                                            null, includeAccessRights, includeUserRights );
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private void appendMenuItemAccessRights( User user, Document doc, Node[] menuItems, Connection _con, PreparedStatement _preparedStmt,
                                             boolean includeAccessRights, boolean includeUserRights )
    {

        Connection con = _con;
        PreparedStatement preparedStmt = _preparedStmt;
        try
        {
            if ( preparedStmt == null )
            {
                if ( _con == null )
                {
                    con = getConnection();
                }
                preparedStmt = con.prepareStatement( MENUITEMAR_GET_ALL );
            }

            // loop through all menuitems
            Element menuItemElement;
            for ( Node menuItem : menuItems )
            {
                menuItemElement = (Element) menuItem;
                int menuItemKey = Integer.parseInt( menuItemElement.getAttribute( "key" ) );

                boolean section = false;
                // section hack #1: this method can be called on old section documents
                if ( menuItemElement.getNodeName().equals( "section" ) )
                {
                    section = true;
                    menuItemKey = getSectionHandler().getMenuItemKeyBySection( menuItemKey ).toInt();
                }

                // append accessrights for this menuitem
                Element accessRightsElement = XMLTool.createElement( doc, menuItemElement, "accessrights" );
                appendMenuItemAccessRight( user, menuItemKey, accessRightsElement, preparedStmt, includeAccessRights, includeUserRights );

                // for each menuitem, we must also loop through its children
                Element childElement = XMLTool.getElement( menuItemElement, "menuitems" );
                if ( section )
                {
                    // section hack #2: children are <sections> here
                    childElement = XMLTool.getElement( menuItemElement, "sections" );
                }
                if ( childElement != null )
                {
                    appendMenuItemAccessRights( user, doc, XMLTool.filterNodes( childElement.getChildNodes(), Node.ELEMENT_NODE ), con,
                                                preparedStmt, includeAccessRights, includeUserRights );
                }
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            // we only close the prepared statement if we are at the
            // top of the recursion (i.e. the _preparedStmt parameter
            // is null.
            if ( _preparedStmt == null )
            {
                close( preparedStmt );
            }

            if ( _con == null )
            {
                close( con );
            }
        }
    }

    private void appendCategoryAccessRights( User user, Element[] categoryElems, PreparedStatement _preparedStmt,
                                             boolean includeAccessRights, boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );

            if ( _preparedStmt == null )
            {
                con = getConnection();
                preparedStmt = con.prepareStatement( sql.toString() );
            }
            else
            {
                con = _preparedStmt.getConnection();
                preparedStmt = con.prepareStatement( sql.toString() );
            }

            // loop through all categories
            for ( Element categoryElem : categoryElems )
            {
                CategoryKey categoryKey = new CategoryKey( categoryElem.getAttribute( "key" ) );

                // append accessrights for this category
                Document doc = categoryElem.getOwnerDocument();
                Element accessrightsElement = XMLTool.createElement( doc, categoryElem, "accessrights" );
                appendCategoryAccessRight( user, accessrightsElement, categoryKey, preparedStmt, includeAccessRights, includeUserRights );

                // for each category, we must also loop through its children
                Element categoriesElement = XMLTool.getElement( categoryElem, "categories" );
                if ( categoriesElement != null )
                {
                    appendCategoryAccessRights( user, XMLTool.getElements( categoriesElement ), preparedStmt, includeAccessRights,
                                                includeUserRights );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get access rights for a category: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            if ( _preparedStmt == null )
            {
                close( preparedStmt );
                close( con );
            }
        }
    }

    private void appendContentAccessRights( User user, Element contentsElem, boolean includeAccessRights, boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            StringBuffer sql = new StringBuffer( COA_SELECT );
            sql.append( " WHERE" );
            sql.append( COA_WHERE_CLAUSE_CON );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );

            // loop through all content
            Element[] contentElems = XMLTool.getElements( contentsElem, "content" );
            for ( Element contentElem1 : contentElems )
            {
                int contentKey = Integer.parseInt( contentElem1.getAttribute( "key" ) );

                // append accessrights for this category
                Document doc = contentElem1.getOwnerDocument();
                Element accessrightsElement = XMLTool.createElement( doc, contentElem1, "accessrights" );
                appendContentAccessRight( user, accessrightsElement, contentKey, preparedStmt, includeAccessRights, includeUserRights );
            }
            Element relatedcontentsElem = XMLTool.getElement( contentsElem, "relatedcontents" );
            contentElems = XMLTool.getElements( relatedcontentsElem );
            for ( Element contentElem : contentElems )
            {
                int contentKey = Integer.parseInt( contentElem.getAttribute( "key" ) );

                // append accessrights for this category
                Document doc = contentElem.getOwnerDocument();
                Element accessrightsElement = XMLTool.createElement( doc, contentElem, "accessrights" );
                appendContentAccessRight( user, accessrightsElement, contentKey, preparedStmt, includeAccessRights, includeUserRights );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get access rights for contents: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public void appendContentSQL( User user, StringBuffer sql, boolean adminReadOnly )
    {

        String[] groups = getGroupHandler().getAllGroupMembershipsForUser( user );

        if ( isEnterpriseAdmin( user, groups ) )
        {
            return;
        }

        ContentView contentView = ContentView.getInstance();

        // Allow if publish on category (NB! Assuming that administrate also has publish set)
        StringBuffer categoryPublishSQL =
            XDG.generateSelectSQL( db.tCatAccessRight, db.tCatAccessRight.car_grp_hKey, false, (Column) null );
        XDG.appendWhereSQL( categoryPublishSQL, db.tCatAccessRight.car_cat_lKey, XDG.OPERATOR_EQUAL, contentView.cat_lKey );
        XDG.appendWhereSQL( categoryPublishSQL, db.tCatAccessRight.car_bPublish, XDG.OPERATOR_EQUAL, 1 );
        XDG.appendWhereInSQL( categoryPublishSQL, db.tCatAccessRight.car_grp_hKey, groups );

        sql.append( " AND (EXISTS (" );
        sql.append( categoryPublishSQL );
        sql.append( ")" );

        // ..or read on content
        StringBuffer contentReadSQL = XDG.generateSelectSQL( db.tConAccessRight2, db.tConAccessRight2.coa_grp_hKey, false, (Column) null );
        XDG.appendWhereSQL( contentReadSQL, db.tConAccessRight2.coa_con_lKey, XDG.OPERATOR_EQUAL, contentView.con_lKey );
        XDG.appendWhereInSQL( contentReadSQL, db.tConAccessRight2.coa_grp_hKey, groups );

        sql.append( " OR (EXISTS (" );
        sql.append( contentReadSQL );
        sql.append( ")" );

        if ( adminReadOnly )
        {
            // Check tCatAccessRights
            StringBuffer categoryAdminReadSQL =
                XDG.generateSelectSQL( db.tCatAccessRight, db.tCatAccessRight.car_grp_hKey, false, (Column) null );
            XDG.appendWhereSQL( categoryAdminReadSQL, db.tCatAccessRight.car_cat_lKey, XDG.OPERATOR_EQUAL, contentView.cat_lKey );
            XDG.appendWhereSQL( categoryAdminReadSQL, db.tCatAccessRight.car_bAdminRead, XDG.OPERATOR_EQUAL, 1 );
            XDG.appendWhereInSQL( categoryAdminReadSQL, db.tCatAccessRight.car_grp_hKey, groups );

            sql.append( " AND EXISTS (" );
            sql.append( categoryAdminReadSQL );
            sql.append( ")" );
        }
        sql.append( "))" );
    }

    public String appendContentSQL( User user, CategoryKey categoryKey, String sql )
    {
        return appendContentSQL( user, new int[]{categoryKey.toInt()}, sql );
    }

    public String appendContentSQL( User user, int[] categoryKeys, String sql )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return sql;
        }

        GroupHandler groupHandler = getGroupHandler();

        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        // allow if user is a member of the enterprise admin group
        if ( Arrays.binarySearch( groups, epGroup ) >= 0 )
        {
            return sql;
        }

        StringBuffer newSQL = new StringBuffer( sql );
        newSQL.append( " AND ((" );
        newSQL.append( COA_WHERE_CLAUSE_SECURITY_FILTER );
        StringBuffer temp = new StringBuffer( groups.length * 2 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                temp.append( "," );
            }
            temp.append( "'" );
            temp.append( groups[i] );
            temp.append( "'" );
        }
        newSQL.append( ")" );
        if ( categoryKeys != null && categoryKeys.length > 0 )
        {
            for ( int categoryKey : categoryKeys )
            {
                CategoryAccessRight categoryAccessRight = getCategoryAccessRight( null, user, new CategoryKey( categoryKey ) );
                if ( categoryAccessRight.getPublish() || categoryAccessRight.getAdministrate() )
                {
                    newSQL.append( " OR cat_lKey = " );
                    newSQL.append( categoryKey );
                }
            }
        }
        newSQL.append( ")" );

        return StringUtil.expandString( newSQL.toString(), temp );
    }

    public void createAccessRights( Element elem )
        throws VerticalCreateException
    {
        createAccessRights( null, elem );
    }

    private void createAccessRights( Connection _con, Element accessrightsElement )
        throws VerticalCreateException
    {

        Connection con = _con;
        PreparedStatement preparedStmt = null;
        String tmp;

        try
        {
            if ( con == null )
            {
                con = getConnection();
            }

            int key = Integer.parseInt( accessrightsElement.getAttribute( "key" ) );
            int type = Integer.parseInt( accessrightsElement.getAttribute( "type" ) );

            // prepare the appropriate SQL statement:
            switch ( type )
            {
                case AccessRight.MENUITEM:
                    preparedStmt = con.prepareStatement( MENUITEMAR_INSERT );
                    break;

                case AccessRight.MENUITEM_DEFAULT:
                    preparedStmt = con.prepareStatement( DEFAULTMENUAR_INSERT );
                    break;

                case AccessRight.CATEGORY:
                    preparedStmt = con.prepareStatement( CAR_INSERT );
                    break;

                case AccessRight.CONTENT:
                    preparedStmt = con.prepareStatement( COA_INSERT );
                    break;

                default:
                    String message = "Accessright type not supported: %0";

                    VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                    StringUtil.expandString( message, type, null ) );
                    break;
            }

            // set key
            preparedStmt.setInt( 1, key );

            Element[] elements = XMLTool.getElements( accessrightsElement, "accessright" );
            for ( int i = 0; i < elements.length; ++i )
            {

                // group key (all)
                String groupKey = elements[i].getAttribute( "groupkey" );
                preparedStmt.setString( 2, groupKey );

                // read (all)
                tmp = elements[i].getAttribute( "read" );
                if ( "true".equals( tmp ) )
                {
                    preparedStmt.setInt( 3, 1 );
                }
                else
                {
                    preparedStmt.setInt( 3, 0 );
                }

                switch ( type )
                {
                    case AccessRight.MENUITEM:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 8, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 8, 0 );
                        }

                        tmp = elements[i].getAttribute( "add" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 9, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 9, 0 );
                        }

                        break;
                    case AccessRight.MENUITEM_DEFAULT:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 8, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 8, 0 );
                        }

                        tmp = elements[i].getAttribute( "add" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 9, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 9, 0 );
                        }

                        break;

                    case AccessRight.CATEGORY:
                        tmp = elements[i].getAttribute( "create" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        tmp = elements[i].getAttribute( "adminread" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 7, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 7, 0 );
                        }

                        break;

                    case AccessRight.CONTENT:
                        tmp = elements[i].getAttribute( "update" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "delete" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        preparedStmt.setString( 6, UUIDGenerator.randomUUID() );
                        break;

                    case AccessRight.SECTION:
                        tmp = elements[i].getAttribute( "read" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 3, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 3, 0 );
                        }

                        tmp = elements[i].getAttribute( "publish" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 4, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 4, 0 );
                        }

                        tmp = elements[i].getAttribute( "approve" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 5, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 5, 0 );
                        }

                        tmp = elements[i].getAttribute( "administrate" );
                        if ( "true".equals( tmp ) )
                        {
                            preparedStmt.setInt( 6, 1 );
                        }
                        else
                        {
                            preparedStmt.setInt( 6, 0 );
                        }

                        break;

                    default:
                        String message = "Accessright type not supported: %0";

                        VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
                                                        StringUtil.expandString( message, type, null ) );
                        break;
                }

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create access rights: %t";

            VerticalRuntimeException.error( this.getClass(), VerticalCreateException.class,
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

    public void removeAccessRights( Connection con, int key, int type )
        throws VerticalRemoveException
    {

        PreparedStatement preparedStmt = null;
        try
        {
            String sql = null;
            switch ( type )
            {
                case AccessRight.MENUITEM:
                    sql = MENUITEMAR_REMOVE_ALL;
                    break;

                case AccessRight.CATEGORY:
                    sql = CAR_DELETE_ALL;
                    break;

                case AccessRight.CONTENT:
                    sql = COA_DELETE_ALL;
                    break;

                default:
                    String message = "Accessright type not supported: %0";

                    VerticalRuntimeException.error( this.getClass(), VerticalRemoveException.class,
                                                    StringUtil.expandString( message, type, null ) );
            }

            preparedStmt = con.prepareStatement( sql );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();

        }
        catch ( SQLException e )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalRemoveException.class,
                                            StringUtil.expandString( "A database error occurred: %t", (Object) null,
                                                                     e ), e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public Document getAccessRights( User user, int type, int key, boolean includeUserright )
    {

        Document doc = XMLTool.createDocument( "accessrights" );
        Element rootElement = doc.getDocumentElement();
        rootElement.setAttribute( "type", String.valueOf( type ) );
        rootElement.setAttribute( "key", String.valueOf( key ) );

        switch ( type )
        {
            case AccessRight.MENUITEM:
                appendAccessRightsOnMenuItem( user, key, rootElement, includeUserright );
                break;

            case AccessRight.MENUITEM_DEFAULT:
                appendAccessRightsOnDefaultMenuItem( user, key, rootElement, includeUserright );
                break;

            case AccessRight.CATEGORY:
                appendAccessRightsOnCategory( user, new CategoryKey( key ), rootElement, true, includeUserright );
                break;

            case AccessRight.CONTENT:
                getContentAccessRights( user, key, rootElement, includeUserright );
                break;

            default:
                LOG.error( StringUtil.expandString( "Accessright type not supported: %0", new Object[]{type}, null ) );
        }

        return doc;
    }

    public Document getDefaultAccessRights( User user, int type, int key )
    {

        Document doc = XMLTool.createDocument( "accessrights" );
        Element accessRights = doc.getDocumentElement();
        accessRights.setAttribute( "type", String.valueOf( type ) );

        switch ( type )
        {
            case AccessRight.MENUITEM:
                appendAccessRightsOnMenuItem( user, key, accessRights, true );
                break;

            case AccessRight.CATEGORY:
                if ( key >= 0 )
                {
                    appendAccessRightsOnCategory( user, new CategoryKey( key ), accessRights, true, true );
                }
                else
                {
                    appendDefaultAccessRightOnCategory( accessRights );
                }
                break;

            case AccessRight.CONTENT:
                appendDefaultAccessRightsOnContent( user, new CategoryKey( key ), accessRights );
                break;

            default:
                LOG.error( StringUtil.expandString( "Accessright type not supported: %0", new Object[]{type}, null ) );
        }

        return doc;
    }

    public void appendAccessRightsOnDefaultMenuItem( User user, int key, Element accessRights, boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( DEFAULTMENUAR_GET_ALL );
            appendDefaultMenuItemAccessRight( user, key, accessRights, preparedStmt, true, includeUserRights );
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public void appendAccessRightsOnMenuItem( User user, int key, Element accessRights, boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( MENUITEMAR_GET_ALL );
            appendMenuItemAccessRight( user, key, accessRights, preparedStmt, true, includeUserRights );
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public void appendAccessRightsOnCategory( User user, CategoryKey categoryKey, Element accessRights, boolean includeAccessRights,
                                              boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            appendCategoryAccessRight( user, accessRights, categoryKey, preparedStmt, includeAccessRights, includeUserRights );
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    private void getContentAccessRights( User user, int key, Element rootElement, boolean includeUserRights )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            StringBuffer sql = new StringBuffer( COA_SELECT );
            sql.append( " WHERE" );
            sql.append( COA_WHERE_CLAUSE_CON );
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            appendContentAccessRight( user, rootElement, key, preparedStmt, true, includeUserRights );
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    private void appendDefaultAccessRightsOnContent( User user, CategoryKey categoryKey, Element rootElement )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();

            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );

            preparedStmt = con.prepareStatement( sql.toString() );

            appendDefaultContentAccessRight( user, rootElement, categoryKey, preparedStmt );
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    private void appendDefaultMenuItemAccessRight( User user, int key, Element rootElement, PreparedStatement preparedStmt,
                                                   boolean includeAccessRights, boolean includeUserRights )
        throws SQLException
    {
        Document doc = rootElement.getOwnerDocument();

        ResultSet resultSet = null;

        try
        {
            if ( includeAccessRights )
            {
                preparedStmt.setInt( 1, key );
                resultSet = preparedStmt.executeQuery();
                while ( resultSet.next() )
                {
                    Element accessRight = XMLTool.createElement( doc, rootElement, "accessright" );
                    accessRight.setAttribute( "groupkey", resultSet.getString( "grp_hKey" ) );

                    GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                    accessRight.setAttribute( "grouptype", groupType.toInteger().toString() );

                    if ( groupType == GroupType.USER )
                    {
                        addUserMenuItemAccessRightsForUser( resultSet, accessRight );
                    }
                    else if ( ( groupType == GroupType.GLOBAL_GROUP ) || ( groupType == GroupType.USERSTORE_GROUP ) )
                    {
                        accessRight.setAttribute( "groupname", resultSet.getString( "grp_sName" ) );
                    }
                    else
                    {
                        accessRight.setAttribute( "groupname", groupType.getName() );
                    }

                    if ( resultSet.getBoolean( "dma_bRead" ) )
                    {
                        accessRight.setAttribute( "read", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bCreate" ) )
                    {
                        accessRight.setAttribute( "create", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bPublish" ) )
                    {
                        accessRight.setAttribute( "publish", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bAdministrate" ) )
                    {
                        accessRight.setAttribute( "administrate", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bUpdate" ) )
                    {
                        accessRight.setAttribute( "update", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bDelete" ) )
                    {
                        accessRight.setAttribute( "delete", "true" );
                    }

                    if ( resultSet.getBoolean( "dma_bAdd" ) )
                    {
                        accessRight.setAttribute( "add", "true" );
                    }

                }
            }

            if ( includeUserRights )
            {
                // append maximum user rights (skip this if user is enterprise administrator)
                appendMaximumDefaultMenuItemRights( preparedStmt.getConnection(), user, doc, rootElement, key );
            }
        }
        finally
        {
            close( resultSet );
        }
    }

    private void appendMenuItemAccessRight( User user, int key, Element accessrightsElement, PreparedStatement preparedStmt,
                                            boolean includeAccessRights, boolean includeUserRights )
        throws SQLException
    {
        Document doc = accessrightsElement.getOwnerDocument();

        ResultSet resultSet = null;

        try
        {
            if ( includeAccessRights )
            {
                preparedStmt.setInt( 1, key );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    Element menuItemAccessRight = XMLTool.createElement( doc, accessrightsElement, "accessright" );
                    menuItemAccessRight.setAttribute( "groupkey", resultSet.getString( "grp_hKey" ) );

                    GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                    menuItemAccessRight.setAttribute( "grouptype", groupType.toInteger().toString() );

                    if ( groupType == GroupType.USER )
                    {
                        addUserMenuItemAccessRightsForUser( resultSet, menuItemAccessRight );
                    }
                    else if ( ( groupType == GroupType.GLOBAL_GROUP ) || ( groupType == GroupType.USERSTORE_GROUP ) )
                    {
                        menuItemAccessRight.setAttribute( "groupname", resultSet.getString( "grp_sName" ) );
                    }
                    else
                    {
                        menuItemAccessRight.setAttribute( "groupname", groupType.getName() );
                    }

                    if ( resultSet.getBoolean( "mia_bRead" ) )
                    {
                        menuItemAccessRight.setAttribute( "read", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bCreate" ) )
                    {
                        menuItemAccessRight.setAttribute( "create", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bPublish" ) )
                    {
                        menuItemAccessRight.setAttribute( "publish", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bAdministrate" ) )
                    {
                        menuItemAccessRight.setAttribute( "administrate", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bUpdate" ) )
                    {
                        menuItemAccessRight.setAttribute( "update", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bDelete" ) )
                    {
                        menuItemAccessRight.setAttribute( "delete", "true" );
                    }

                    if ( resultSet.getBoolean( "mia_bAdd" ) )
                    {
                        menuItemAccessRight.setAttribute( "add", "true" );
                    }

                }
            }

            if ( includeUserRights )
            {
                // append maximum user rights (skip this if user is enterprise administrator)
                appendMaximumMenuItemRights( preparedStmt.getConnection(), user, doc, accessrightsElement, key );
            }
        }
        finally
        {
            close( resultSet );
        }
    }

    private void addUserMenuItemAccessRightsForUser( ResultSet resultSet, Element menuItemAccessRight )
        throws SQLException
    {
        String uid = resultSet.getString( "usr_sUID" );
        String userKey = resultSet.getString( "usr_hkey" );

        QualifiedUsername qualifiedName;
        if ( "anonymous".equals( uid ) || "admin".equals( uid ) )
        {
            qualifiedName = new QualifiedUsername( uid );
        }
        else
        {
            String userStoreName = getUserStoreName( userKey );
            qualifiedName = new QualifiedUsername( userStoreName, uid );
        }

        menuItemAccessRight.setAttribute( "uid", uid );
        menuItemAccessRight.setAttribute( "fullname", resultSet.getString( "usr_sFullName" ) );
        menuItemAccessRight.setAttribute( "qualifiedName", qualifiedName.toString() );
    }

    private String getUserStoreName( String userKey )
    {
        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        try
        {
            con = getConnection();

            preparedStmt = con.prepareStatement( USERSTORE_GET_NAME );
            preparedStmt.setString( 1, userKey );

            resultSet = preparedStmt.executeQuery();
            if ( resultSet.next() )
            {
                return resultSet.getString( "dom_sName" );
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return "Error: unknown userstore";
    }

    private void appendCategoryAccessRight( User user, Element category, CategoryKey categoryKey, PreparedStatement preparedStmt,
                                            boolean includeAccessRights, boolean includeUserRights )
        throws SQLException
    {

        Document doc = category.getOwnerDocument();
        ResultSet resultSet = null;

        try
        {
            if ( includeAccessRights )
            {
                preparedStmt.setInt( 1, categoryKey.toInt() );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    Element accessrightElem = XMLTool.createElement( doc, category, "accessright" );
                    accessrightElem.setAttribute( "groupkey", resultSet.getString( "grp_hKey" ) );

                    GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                    accessrightElem.setAttribute( "grouptype", groupType.toInteger().toString() );

                    if ( groupType == GroupType.USER )
                    {
                        addUserMenuItemAccessRightsForUser( resultSet, accessrightElem );
                    }
                    else if ( ( groupType == GroupType.GLOBAL_GROUP ) || ( groupType == GroupType.USERSTORE_GROUP ) )
                    {
                        accessrightElem.setAttribute( "groupname", resultSet.getString( "grp_sName" ) );
                    }
                    else
                    {
                        accessrightElem.setAttribute( "groupname", groupType.getName() );
                    }

                    if ( resultSet.getBoolean( "car_bRead" ) )
                    {
                        accessrightElem.setAttribute( "read", "true" );
                    }

                    if ( resultSet.getBoolean( "car_bCreate" ) )
                    {
                        accessrightElem.setAttribute( "create", "true" );
                    }

                    if ( resultSet.getBoolean( "car_bPublish" ) )
                    {
                        accessrightElem.setAttribute( "publish", "true" );
                    }

                    if ( resultSet.getBoolean( "car_bAdministrate" ) )
                    {
                        accessrightElem.setAttribute( "administrate", "true" );
                    }

                    if ( resultSet.getBoolean( "car_bAdminRead" ) )
                    {
                        accessrightElem.setAttribute( "adminread", "true" );
                    }
                }
            }

            if ( includeUserRights )
            {
                // append maximum user rights (skip this if user is enterprise administrator)
                appendMaximumCategoryRights( preparedStmt.getConnection(), user, category, categoryKey );
            }
        }
        finally
        {
            close( resultSet );
        }
    }

    private void appendDefaultAccessRightOnCategory( Element rootElement )
    {

        GroupHandler groupHandler = getGroupHandler();
        String groupKey = groupHandler.getAdminGroupKey();

        Document doc = rootElement.getOwnerDocument();
        Element accessrightElem = XMLTool.createElement( doc, rootElement, "accessright" );
        accessrightElem.setAttribute( "groupkey", groupKey );
        accessrightElem.setAttribute( "grouptype", GroupType.ADMINS.toInteger().toString() );
        accessrightElem.setAttribute( "groupname", GroupType.ADMINS.getName() );

        accessrightElem.setAttribute( "adminread", "true" );
        accessrightElem.setAttribute( "read", "true" );
        accessrightElem.setAttribute( "create", "true" );
        accessrightElem.setAttribute( "publish", "true" );
        accessrightElem.setAttribute( "administrate", "true" );
    }

    private void appendContentAccessRight( User user, Element rootElement, int key, PreparedStatement preparedStmt,
                                           boolean includeAccessRights, boolean includeUserRights )
        throws SQLException
    {

        Document doc = rootElement.getOwnerDocument();
        ResultSet resultSet = null;
        rootElement.setAttribute( "type", String.valueOf( AccessRight.CONTENT ) );

        try
        {
            if ( includeAccessRights )
            {
                preparedStmt.setInt( 1, key );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    Element accessrightElem = XMLTool.createElement( doc, rootElement, "accessright" );
                    accessrightElem.setAttribute( "groupkey", resultSet.getString( "grp_hKey" ) );

                    GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                    accessrightElem.setAttribute( "grouptype", groupType.toInteger().toString() );

                    if ( groupType == GroupType.USER )
                    {
                        addUserMenuItemAccessRightsForUser( resultSet, accessrightElem );
                    }
                    else if ( ( groupType == GroupType.GLOBAL_GROUP ) || ( groupType == GroupType.USERSTORE_GROUP ) )
                    {
                        accessrightElem.setAttribute( "groupname", resultSet.getString( "grp_sName" ) );
                    }
                    else
                    {
                        accessrightElem.setAttribute( "groupname", groupType.getName() );
                    }

                    if ( resultSet.getBoolean( "coa_bRead" ) )
                    {
                        accessrightElem.setAttribute( "read", "true" );
                    }

                    if ( resultSet.getBoolean( "coa_bUpdate" ) )
                    {
                        accessrightElem.setAttribute( "update", "true" );
                    }

                    if ( resultSet.getBoolean( "coa_bDelete" ) )
                    {
                        accessrightElem.setAttribute( "delete", "true" );
                    }
                }
            }

            if ( includeUserRights )
            {
                appendMaximumContentRights( preparedStmt.getConnection(), user, rootElement, key );
            }
        }
        finally
        {
            close( resultSet );
        }
    }

    private void appendDefaultContentAccessRight( User user, Element rootElement, CategoryKey categoryKey, PreparedStatement preparedStmt )
        throws SQLException
    {

        Document doc = rootElement.getOwnerDocument();
        ResultSet resultSet = null;

        try
        {
            if ( user != null && ( !user.isEnterpriseAdmin() && !user.isAnonymous() ) )
            {
                Element accessrightElem = XMLTool.createElement( doc, rootElement, "accessright" );
                accessrightElem.setAttribute( "groupkey", user.getUserGroupKey().toString() );
                accessrightElem.setAttribute( "grouptype", GroupType.USER.toInteger().toString() );
                accessrightElem.setAttribute( "uid", user.getName() );
                accessrightElem.setAttribute( "fullname", user.getDisplayName() );
                accessrightElem.setAttribute( "read", "true" );
                accessrightElem.setAttribute( "update", "true" );
                accessrightElem.setAttribute( "delete", "true" );
            }

            preparedStmt.setInt( 1, categoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();
            while ( resultSet.next() )
            {
                GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                if ( groupType == GroupType.USER )
                {
                    String uid = resultSet.getString( "usr_sUID" );
                    if ( user != null && uid.equals( user.getName() ) )
                    {
                        continue;
                    }
                }

                Element accessrightElem = XMLTool.createElement( doc, rootElement, "accessright" );
                accessrightElem.setAttribute( "groupkey", resultSet.getString( "grp_hKey" ) );
                accessrightElem.setAttribute( "grouptype", groupType.toInteger().toString() );

                if ( groupType == GroupType.USER )
                {
                    String uid = resultSet.getString( "usr_sUID" );
                    accessrightElem.setAttribute( "uid", uid );
                    accessrightElem.setAttribute( "fullname", resultSet.getString( "usr_sFullName" ) );
                }
                else if ( ( groupType == GroupType.GLOBAL_GROUP ) || ( groupType == GroupType.USERSTORE_GROUP ) )
                {
                    accessrightElem.setAttribute( "groupname", resultSet.getString( "grp_sName" ) );
                }
                else
                {
                    accessrightElem.setAttribute( "groupname", groupType.getName() );
                }

                boolean categoryRead = resultSet.getBoolean( "car_bRead" );
                boolean categoryAdminBrowse = resultSet.getBoolean( "car_bAdminread" );
                //boolean categoryCreate = resultSet.getBoolean( "car_bCreate" );
                boolean categoryPublish = resultSet.getBoolean( "car_bPublish" );
                boolean categoryAdministrate = resultSet.getBoolean( "car_bAdministrate" );

                boolean contentRead = categoryRead || categoryAdminBrowse || categoryPublish || categoryAdministrate;
                boolean contentUpdate = categoryPublish || categoryAdministrate;
                boolean contentDelete = categoryPublish || categoryAdministrate;

                accessrightElem.setAttribute( "read", Boolean.toString( contentRead ) );
                accessrightElem.setAttribute( "update", Boolean.toString( contentUpdate ) );
                accessrightElem.setAttribute( "delete", Boolean.toString( contentDelete ) );
            }
        }
        finally
        {
            close( resultSet );
        }
    }

    public CategoryAccessRight getCategoryAccessRight( User user, CategoryKey categoryKey )
    {

        return getCategoryAccessRight( null, user, categoryKey );
    }

    private CategoryAccessRight getCategoryAccessRight( Connection _con, User user, CategoryKey categoryKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();
        CategoryAccessRight categoryAccessRight = new CategoryAccessRight( categoryKey );

        // if enterprise administrator, return full rights
        String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        if ( user.isEnterpriseAdmin() || Arrays.binarySearch( groups, eaGroup ) >= 0 )
        {
            categoryAccessRight.setRead( true );
            categoryAccessRight.setCreate( true );
            categoryAccessRight.setPublish( true );
            categoryAccessRight.setAdministrate( true );
            categoryAccessRight.setAdminRead( true );
            return categoryAccessRight;
        }

        // [read, create, publish, administrate, adminread]
        int READ = 0, CREATE = 1, PUBLISH = 2, ADMIN = 3, ADMINREAD = 4;
        boolean[] rights = new boolean[5];

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

            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );
            sql.append( " AND" );
            sql.append( CAR_WHERE_CLAUSE_GROUP_IN );

            // generate list of groupkeys
            sql.append( " (" );
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i != 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            // get accessrights for the groups
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, categoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                rights[READ] |= resultSet.getBoolean( "car_bRead" );
                rights[CREATE] |= resultSet.getBoolean( "car_bCreate" );
                rights[PUBLISH] |= resultSet.getBoolean( "car_bPublish" );
                rights[ADMINREAD] |= resultSet.getBoolean( "car_bAdminRead" );
                if ( resultSet.getBoolean( "car_bAdministrate" ) )
                {
                    rights[ADMIN] = true;
                    break;
                }
            }

            categoryAccessRight.setRead( rights[READ] );
            categoryAccessRight.setCreate( rights[CREATE] );
            categoryAccessRight.setPublish( rights[PUBLISH] );
            categoryAccessRight.setAdministrate( rights[ADMIN] );
            categoryAccessRight.setAdminRead( rights[ADMINREAD] );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get maximum category access right: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            if ( _con == null )
            {
                close( con );
            }
        }

        return categoryAccessRight;
    }

    public MenuAccessRight getMenuAccessRight( User user, int menuKey )
    {

        GroupHandler groupHandler = getGroupHandler();

        String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        if ( user.isEnterpriseAdmin() || Arrays.binarySearch( groups, eaGroup ) >= 0 )
        {
            MenuAccessRight menuAccessRight = new MenuAccessRight( menuKey );
            menuAccessRight.setRead( true );
            menuAccessRight.setCreate( true );
            menuAccessRight.setPublish( true );
            menuAccessRight.setUpdate( true );
            menuAccessRight.setDelete( true );
            menuAccessRight.setAdministrate( true );
            return menuAccessRight;
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        MenuAccessRight menuAccessRight = new MenuAccessRight( menuKey );

        // [read, create, publish, administrate, update, delete]
        int READ = 0, CREATE = 1, PUBLISH = 2, ADMIN = 3, UPDATE = 4, DELETE = 5;
        boolean[] rights = new boolean[6];

        try
        {
            con = getConnection();

            StringBuffer sql = new StringBuffer( MENUAR_SELECT );
            sql.append( " WHERE" );
            sql.append( DMA_WHERE_CLAUSE_MEN );
            sql.append( " AND" );
            sql.append( DMA_WHERE_CLAUSE_GROUP_IN );

            String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );
            sql.append( " (" );
            for ( int i = 0; i < groupKeys.length; ++i )
            {
                if ( i != 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groupKeys[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            // get accessrights for the groups
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, menuKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                rights[READ] |= resultSet.getBoolean( "dma_bRead" );
                rights[CREATE] |= resultSet.getBoolean( "dma_bCreate" );
                rights[PUBLISH] |= resultSet.getBoolean( "dma_bPublish" );
                rights[UPDATE] |= resultSet.getBoolean( "dma_bUpdate" );
                rights[DELETE] |= resultSet.getBoolean( "dma_bDelete" );

                if ( resultSet.getBoolean( "dma_bAdministrate" ) )
                {
                    rights[ADMIN] = true;
                    break;
                }
            }
            resultSet.close();
            preparedStmt.close();

            menuAccessRight.setRead( rights[READ] );
            menuAccessRight.setCreate( rights[CREATE] );
            menuAccessRight.setPublish( rights[PUBLISH] );
            menuAccessRight.setUpdate( rights[UPDATE] );
            menuAccessRight.setDelete( rights[DELETE] );
            menuAccessRight.setAdministrate( rights[ADMIN] );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get maximum menu access right: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return menuAccessRight;
    }

    public MenuItemAccessRight getMenuItemAccessRight( User user, MenuItemKey menuItemKey )
    {
        return getMenuItemAccessRight( null, user, menuItemKey );
    }

    private MenuItemAccessRight getMenuItemAccessRight( Connection _con, User user, MenuItemKey menuItemKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();
        MenuItemAccessRight menuItemAccessRight = new MenuItemAccessRight( menuItemKey );

        String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        if ( user.isEnterpriseAdmin() || Arrays.binarySearch( groups, eaGroup ) >= 0 )
        {
            menuItemAccessRight.setRead( true );
            menuItemAccessRight.setCreate( true );
            menuItemAccessRight.setPublish( true );
            menuItemAccessRight.setUpdate( true );
            menuItemAccessRight.setDelete( true );
            menuItemAccessRight.setAdministrate( true );
            menuItemAccessRight.setAdd( true );
            return menuItemAccessRight;
        }

        // [read, create, publish, administrate, update, delete, adminread]
        int READ = 0, CREATE = 1, PUBLISH = 2, ADMIN = 3, UPDATE = 4, DELETE = 5, ADD = 6;
        boolean[] rights = new boolean[7];

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

            StringBuffer sql = new StringBuffer( MENUITEMAR_SELECT );
            sql.append( " WHERE" );
            sql.append( MIA_WHERE_CLAUSE_MEI );
            sql.append( " AND" );
            sql.append( MIA_WHERE_CLAUSE_GROUP_IN );

            String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );
            for ( int i = 0; i < groupKeys.length; ++i )
            {
                if ( i != 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groupKeys[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            // get accessrights for the groups
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, menuItemKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                rights[READ] |= resultSet.getBoolean( "mia_bRead" );
                rights[CREATE] |= resultSet.getBoolean( "mia_bCreate" );
                rights[PUBLISH] |= resultSet.getBoolean( "mia_bPublish" );
                rights[UPDATE] |= resultSet.getBoolean( "mia_bUpdate" );
                rights[DELETE] |= resultSet.getBoolean( "mia_bDelete" );
                rights[ADD] |= resultSet.getBoolean( "mia_bAdd" );

                if ( resultSet.getBoolean( "mia_bAdministrate" ) )
                {
                    rights[ADMIN] = true;
                    break;
                }
            }
            resultSet.close();
            preparedStmt.close();

            menuItemAccessRight.setRead( rights[READ] );
            menuItemAccessRight.setCreate( rights[CREATE] );
            menuItemAccessRight.setPublish( rights[PUBLISH] );
            menuItemAccessRight.setUpdate( rights[UPDATE] );
            menuItemAccessRight.setDelete( rights[DELETE] );
            menuItemAccessRight.setAdministrate( rights[ADMIN] );
            menuItemAccessRight.setAdd( rights[ADD] );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get maximum menuitem access right: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            if ( _con == null )
            {
                close( con );
            }
        }

        return menuItemAccessRight;
    }

    public ContentAccessRight getContentAccessRight( User user, int contentKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();
        ContentAccessRight contentAccessRight = new ContentAccessRight( contentKey );

        // if enterprise administrator, return full rights
        String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );
        if ( user.isEnterpriseAdmin() || Arrays.binarySearch( groups, eaGroup ) >= 0 )
        {
            contentAccessRight.setRead( true );
            contentAccessRight.setUpdate( true );
            contentAccessRight.setDelete( true );
            return contentAccessRight;
        }

        // if user has publish right on category, return full rights
        CategoryKey categoryKey = getContentHandler().getCategoryKey( contentKey );
        CategoryAccessRight categoryAccessRight = getCategoryAccessRight( user, categoryKey );
        if ( categoryAccessRight.getPublish() )
        {
            contentAccessRight.setRead( true );
            contentAccessRight.setUpdate( true );
            contentAccessRight.setDelete( true );
            return contentAccessRight;
        }

        // [read, create, publish, administrate, adminread]
        int READ = 0, UPDATE = 1, DELETE = 2;
        boolean[] rights = new boolean[3];
        try
        {
            con = getConnection();
            StringBuffer sql = new StringBuffer( COA_SELECT );
            sql.append( " WHERE" );
            sql.append( COA_WHERE_CLAUSE_CON );
            sql.append( " AND" );
            sql.append( COA_WHERE_CLAUSE_GROUP_IN );

            // generate list of groupkeys
            sql.append( " (" );
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i != 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            // get accessrights for the groups
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, contentKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {
                rights[READ] |= resultSet.getBoolean( "coa_bRead" );
                rights[UPDATE] |= resultSet.getBoolean( "coa_bUpdate" );
                rights[DELETE] |= resultSet.getBoolean( "coa_bDelete" );
            }
            close( resultSet );
            close( preparedStmt );

            contentAccessRight.setRead( rights[READ] );
            contentAccessRight.setUpdate( rights[UPDATE] );
            contentAccessRight.setDelete( rights[DELETE] );
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get maximum category access right: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return contentAccessRight;
    }

    private void appendMaximumMenuItemRights( Connection con, User user, Document doc, Element rootElement, int mikey )
        throws SQLException
    {

        Element userRightElement = XMLTool.createElement( doc, rootElement, "userright" );

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();

        // [read, create, publish, administrate, update, delete, add]
        int[] rights;

        try
        {
            StringBuffer sql = new StringBuffer();
            String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );
            Arrays.sort( groupKeys );
            String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();

            if ( user != null && ( user.isEnterpriseAdmin() || Arrays.binarySearch( groupKeys, eaGroup ) >= 0 ) )
            {
                rights = new int[]{1, 1, 1, 1, 1, 1, 1};
            }
            else
            {
                rights = new int[]{0, 0, 0, 0, 0, 0, 0};

                sql.append( " (" );
                for ( int i = 0; i < groupKeys.length; ++i )
                {
                    if ( i != 0 )
                    {
                        sql.append( "," );
                    }

                    sql.append( "'" );
                    sql.append( groupKeys[i] );
                    sql.append( "'" );
                }
                sql.append( ")" );

                // get accessrights for the groups
                preparedStmt = con.prepareStatement( MENUITEMAR_GET_FOR_GROUPS + sql.toString() );
                preparedStmt.setInt( 1, mikey );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    if ( resultSet.getBoolean( "mia_bRead" ) )
                    {
                        rights[0] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bCreate" ) )
                    {
                        rights[1] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bPublish" ) )
                    {
                        rights[2] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bUpdate" ) )
                    {
                        rights[4] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bDelete" ) )
                    {
                        rights[5] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bAdd" ) )
                    {
                        rights[6] = 1;
                    }

                    if ( resultSet.getBoolean( "mia_bAdministrate" ) )
                    {
                        rights[0] = 1;
                        rights[1] = 1;
                        rights[2] = 1;
                        rights[3] = 1;
                        rights[4] = 1;
                        rights[5] = 1;
                        rights[6] = 1;
                        break;
                    }
                }
                resultSet.close();
                preparedStmt.close();
            }

            if ( rights[0] == 1 )
            {
                userRightElement.setAttribute( "read", "true" );
            }
            else
            {
                userRightElement.setAttribute( "read", "false" );
            }

            if ( rights[1] == 1 )
            {
                userRightElement.setAttribute( "create", "true" );
            }
            else
            {
                userRightElement.setAttribute( "create", "false" );
            }

            if ( rights[2] == 1 )
            {
                userRightElement.setAttribute( "publish", "true" );
            }
            else
            {
                userRightElement.setAttribute( "publish", "false" );
            }

            if ( rights[3] == 1 )
            {
                userRightElement.setAttribute( "administrate", "true" );
            }
            else
            {
                userRightElement.setAttribute( "administrate", "false" );
            }

            if ( rights[4] == 1 )
            {
                userRightElement.setAttribute( "update", "true" );
            }
            else
            {
                userRightElement.setAttribute( "update", "false" );
            }

            if ( rights[5] == 1 )
            {
                userRightElement.setAttribute( "delete", "true" );
            }
            else
            {
                userRightElement.setAttribute( "delete", "false" );
            }

            if ( rights[6] == 1 )
            {
                userRightElement.setAttribute( "add", "true" );
            }
            else
            {
                userRightElement.setAttribute( "add", "false" );
            }
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }
    }

    private void appendMaximumDefaultMenuItemRights( Connection con, User user, Document doc, Element rootElement, int mkey )
        throws SQLException
    {

        Element userRightElement = XMLTool.createElement( doc, rootElement, "userright" );

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        GroupHandler groupHandler = getGroupHandler();

        // [read, create, publish, administrate, update, delete, add]
        int[] rights;

        try
        {
            StringBuffer sql = new StringBuffer();
            String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );
            Arrays.sort( groupKeys );
            String eaGroup = groupHandler.getEnterpriseAdministratorGroupKey();

            if ( user != null && ( user.isEnterpriseAdmin() || Arrays.binarySearch( groupKeys, eaGroup ) >= 0 ) )
            {
                rights = new int[]{1, 1, 1, 1, 1, 1, 1};
            }
            else
            {
                rights = new int[]{0, 0, 0, 0, 0, 0, 0};

                sql.append( " (" );
                for ( int i = 0; i < groupKeys.length; ++i )
                {
                    if ( i != 0 )
                    {
                        sql.append( "," );
                    }

                    sql.append( "'" );
                    sql.append( groupKeys[i] );
                    sql.append( "'" );
                }
                sql.append( ")" );

                // get accessrights for the groups
                preparedStmt = con.prepareStatement( DEFAULTMENUAR_GET_FOR_GROUPS + sql.toString() );
                preparedStmt.setInt( 1, mkey );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    if ( resultSet.getBoolean( "dma_bRead" ) )
                    {
                        rights[0] = 1;
                    }

                    if ( resultSet.getBoolean( "dma_bCreate" ) )
                    {
                        rights[1] = 1;
                    }

                    if ( resultSet.getBoolean( "dma_bPublish" ) )
                    {
                        rights[2] = 1;
                    }

                    if ( resultSet.getBoolean( "dma_bAdministrate" ) )
                    {
                        rights[0] = 1;
                        rights[1] = 1;
                        rights[2] = 1;
                        rights[3] = 1;
                        rights[4] = 1;
                        rights[5] = 1;
                        rights[6] = 1;
                        break;
                    }

                    if ( resultSet.getBoolean( "dma_bUpdate" ) )
                    {
                        rights[4] = 1;
                    }

                    if ( resultSet.getBoolean( "dma_bDelete" ) )
                    {
                        rights[5] = 1;
                    }

                    if ( resultSet.getBoolean( "dma_bAdd" ) )
                    {
                        rights[6] = 1;
                    }
                }
                resultSet.close();
                preparedStmt.close();
            }

            if ( rights[0] == 1 )
            {
                userRightElement.setAttribute( "read", "true" );
            }
            else
            {
                userRightElement.setAttribute( "read", "false" );
            }

            if ( rights[1] == 1 )
            {
                userRightElement.setAttribute( "create", "true" );
            }
            else
            {
                userRightElement.setAttribute( "create", "false" );
            }

            if ( rights[2] == 1 )
            {
                userRightElement.setAttribute( "publish", "true" );
            }
            else
            {
                userRightElement.setAttribute( "publish", "false" );
            }

            if ( rights[3] == 1 )
            {
                userRightElement.setAttribute( "administrate", "true" );
            }
            else
            {
                userRightElement.setAttribute( "administrate", "false" );
            }

            if ( rights[4] == 1 )
            {
                userRightElement.setAttribute( "update", "true" );
            }
            else
            {
                userRightElement.setAttribute( "update", "false" );
            }

            if ( rights[5] == 1 )
            {
                userRightElement.setAttribute( "delete", "true" );
            }
            else
            {
                userRightElement.setAttribute( "delete", "false" );
            }

            if ( rights[6] == 1 )
            {
                userRightElement.setAttribute( "add", "true" );
            }
            else
            {
                userRightElement.setAttribute( "add", "false" );
            }

        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }
    }

    private void appendMaximumCategoryRights( Connection con, User user, Element rootElement, CategoryKey categoryKey )
        throws SQLException
    {

        Document doc = rootElement.getOwnerDocument();
        Element userRightElement = XMLTool.createElement( doc, rootElement, "userright" );

        CategoryAccessRight categoryAccessRight = getCategoryAccessRight( con, user, categoryKey );

        if ( categoryAccessRight.getAdminRead() )
        {
            userRightElement.setAttribute( "adminread", "true" );
        }
        else
        {
            userRightElement.setAttribute( "adminread", "false" );
        }

        if ( categoryAccessRight.getRead() )
        {
            userRightElement.setAttribute( "read", "true" );
        }
        else
        {
            userRightElement.setAttribute( "read", "false" );
        }

        if ( categoryAccessRight.getCreate() )
        {
            userRightElement.setAttribute( "create", "true" );
        }
        else
        {
            userRightElement.setAttribute( "create", "false" );
        }

        if ( categoryAccessRight.getPublish() )
        {
            userRightElement.setAttribute( "publish", "true" );
        }
        else
        {
            userRightElement.setAttribute( "publish", "false" );
        }

        if ( categoryAccessRight.getAdministrate() )
        {
            userRightElement.setAttribute( "administrate", "true" );
            userRightElement.setAttribute( "publish", "true" );
            userRightElement.setAttribute( "create", "true" );
            userRightElement.setAttribute( "read", "true" );
            userRightElement.setAttribute( "adminread", "true" );
        }
        else
        {
            userRightElement.setAttribute( "administrate", "false" );
        }

    }

    private void appendMaximumContentRights( Connection con, User user, Element rootElement, int contentKey )
        throws SQLException
    {

        Document doc = rootElement.getOwnerDocument();
        Element userRightElement = XMLTool.createElement( doc, rootElement, "userright" );

        CategoryKey categoryKey = getContentHandler().getCategoryKey( contentKey );
        CategoryAccessRight categoryAccessRight = getCategoryAccessRight( user, categoryKey );

        if ( categoryAccessRight.getPublish() )
        {
            userRightElement.setAttribute( "read", "true" );
            userRightElement.setAttribute( "update", "true" );
            userRightElement.setAttribute( "delete", "true" );
            userRightElement.setAttribute( "categorypublish", "true" );
            userRightElement.setAttribute( "categorycreate", "true" );
        }
        else
        {
            PreparedStatement preparedStmt = null;
            ResultSet resultSet = null;
            GroupHandler groupHandler = getGroupHandler();

            // [read, update, delete]
            int READ = 0, UPDATE = 1, DELETE = 2;
            boolean[] rights = new boolean[3];

            try
            {
                StringBuffer sql = new StringBuffer( COA_SELECT );
                sql.append( " WHERE" );
                sql.append( COA_WHERE_CLAUSE_CON );
                sql.append( " AND" );
                sql.append( COA_WHERE_CLAUSE_GROUP_IN );

                String[] groupKeys = groupHandler.getAllGroupMembershipsForUser( user );

                sql.append( " (" );
                for ( int i = 0; i < groupKeys.length; ++i )
                {
                    if ( i != 0 )
                    {
                        sql.append( "," );
                    }

                    sql.append( "'" );
                    sql.append( groupKeys[i] );
                    sql.append( "'" );
                }
                sql.append( ")" );

                // get accessrights for the groups
                preparedStmt = con.prepareStatement( sql.toString() );
                preparedStmt.setInt( 1, contentKey );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    rights[READ] |= resultSet.getBoolean( "coa_bRead" );
                    rights[UPDATE] |= resultSet.getBoolean( "coa_bUpdate" );
                    rights[DELETE] |= resultSet.getBoolean( "coa_bDelete" );
                }
                resultSet.close();
                preparedStmt.close();

                if ( rights[READ] )
                {
                    userRightElement.setAttribute( "read", "true" );
                }
                else
                {
                    userRightElement.setAttribute( "read", "false" );
                }
                if ( rights[UPDATE] )
                {
                    userRightElement.setAttribute( "update", "true" );
                }
                else
                {
                    userRightElement.setAttribute( "update", "false" );
                }
                if ( rights[DELETE] )
                {
                    userRightElement.setAttribute( "delete", "true" );
                }
                else
                {
                    userRightElement.setAttribute( "delete", "false" );
                }

                if ( categoryAccessRight.getCreate() )
                {
                    userRightElement.setAttribute( "categorycreate", "true" );
                }
            }
            finally
            {
                close( resultSet );
                close( preparedStmt );
            }
        }
    }

    public boolean validateMenuItemCreate( User user, int menuKey, int parentKey )
    {

        GroupHandler groupHandler = getGroupHandler();

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        TStringArrayList autoAllowGroups = new TStringArrayList();
        autoAllowGroups.add( groupHandler.getEnterpriseAdministratorGroupKey() );

        autoAllowGroups.add( groupHandler.getAdminGroupKey() );

        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );

        StringBuffer sql;
        if ( parentKey == -1 )
        {
            sql = new StringBuffer( MENUITEMAR_VALIDATE_CREATE_TOPLEVEL_1 );
        }
        else
        {
            sql = new StringBuffer( MENUITEMAR_VALIDATE_CREATE_1 );
        }

        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                sql.append( "," );
            }

            // allow if user is a member of the enterprise admin group
            if ( autoAllowGroups.contains( groups[i] ) )
            {
                return true;
            }

            sql.append( "'" );
            sql.append( groups[i] );
            sql.append( "'" );
        }
        sql.append( MENUITEMAR_VALIDATE_CREATE_2 );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            if ( parentKey == -1 )
            {
                preparedStmt.setInt( 1, menuKey );
            }
            else
            {
                preparedStmt.setInt( 1, parentKey );
            }
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                result = true;
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public boolean validateCategoryCreate( User user, CategoryKey superCategoryKey )
    {

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        if ( isSiteAdmin( user, groups ) )
        {
            return true;
        }

        if ( superCategoryKey != null )
        {
            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );
            sql.append( " AND" );
            sql.append( CAR_WHERE_CLAUSE_CREATE );
            sql.append( " AND" );
            sql.append( CAR_WHERE_CLAUSE_GROUP_IN );
            sql.append( "(" );
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i > 0 )
                {
                    sql.append( "," );
                }
                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
            }
            sql.append( ")" );

            Connection con = null;
            PreparedStatement preparedStmt = null;
            ResultSet resultSet = null;
            boolean result = false;

            try
            {
                con = getConnection();
                preparedStmt = con.prepareStatement( sql.toString() );
                preparedStmt.setInt( 1, superCategoryKey.toInt() );
                resultSet = preparedStmt.executeQuery();

                if ( resultSet.next() )
                {
                    result = true;
                }
            }
            catch ( SQLException sqle )
            {
                String message = "Failed to validate category create: %t";
                LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
            }
            finally
            {
                close( resultSet );
                close( preparedStmt );
                close( con );
            }

            return result;
        }
        else
        {
            return false;
        }
    }

    public boolean validateCategoryRemove( User user, CategoryKey superCategoryKey )
    {

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        if ( isSiteAdmin( user, groups ) )
        {
            return true;
        }

        StringBuffer sql = new StringBuffer( CAR_SELECT );
        sql.append( " WHERE" );
        sql.append( CAR_WHERE_CLAUSE_CAT );
        sql.append( " AND" );
        sql.append( CAR_WHERE_CLAUSE_REMOVE );
        sql.append( " AND" );
        sql.append( CAR_WHERE_CLAUSE_GROUP_IN );
        sql.append( "(" );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                sql.append( "," );
            }
            sql.append( "'" );
            sql.append( groups[i] );
            sql.append( "'" );
        }
        sql.append( ")" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, superCategoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                result = true;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to validate category create: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public boolean validateCategoryUpdate( User user, CategoryKey categoryKey )
    {

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        if ( isSiteAdmin( user, groups ) )
        {
            return true;
        }

        StringBuffer sql = new StringBuffer( CAR_SELECT );
        sql.append( " WHERE" );
        sql.append( CAR_WHERE_CLAUSE_CAT );
        sql.append( " AND" );
        sql.append( CAR_WHERE_CLAUSE_UPDATE );
        sql.append( " AND" );
        sql.append( CAR_WHERE_CLAUSE_GROUP_IN );
        sql.append( "(" );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                sql.append( "," );
            }
            sql.append( "'" );
            sql.append( groups[i] );
            sql.append( "'" );
        }
        sql.append( ")" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, categoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                result = true;
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to validate category create: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public boolean validateMenuItemUpdate( User user, int menuItemKey )
    {

        GroupHandler groupHandler = getGroupHandler();

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        TStringArrayList autoAllowGroups = new TStringArrayList();

        autoAllowGroups.add( groupHandler.getAdminGroupKey() );
        autoAllowGroups.add( groupHandler.getEnterpriseAdministratorGroupKey() );

        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );

        StringBuffer sql = new StringBuffer( MENUITEMAR_VALIDATE_UPDATE_1 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                sql.append( "," );
            }

            if ( autoAllowGroups.contains( groups[i] ) )
            {
                return true;
            }

            sql.append( "'" );
            sql.append( groups[i] );
            sql.append( "'" );
        }
        sql.append( MENUITEMAR_VALIDATE_UPDATE_2 );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, menuItemKey );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                result = true;
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public boolean validateMenuItemRemove( User user, int menuItemKey )
    {

        GroupHandler groupHandler = getGroupHandler();

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        TStringArrayList autoAllowGroups = new TStringArrayList();

        autoAllowGroups.add( groupHandler.getAdminGroupKey() );
        autoAllowGroups.add( groupHandler.getEnterpriseAdministratorGroupKey() );

        String[] groups = getGroupHandler().getAllGroupMembershipsForUser( user );

        StringBuffer sql = new StringBuffer( MENUITEMAR_VALIDATE_REMOVE_1 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                sql.append( "," );
            }

            if ( autoAllowGroups.contains( groups[i] ) )
            {
                return true;
            }

            sql.append( "'" );
            sql.append( groups[i] );
            sql.append( "'" );
        }
        sql.append( MENUITEMAR_VALIDATE_REMOVE_2 );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, menuItemKey );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                result = true;
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public void inheritMenuItemAccessRights( int menuKey, int parentKey, int miKey, String ownerGroupKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            if ( parentKey == -1 )
            {
                preparedStmt = con.prepareStatement( DEFAULTMENUAR_GET_ALL );
                preparedStmt.setInt( 1, menuKey );
            }
            else
            {
                preparedStmt = con.prepareStatement( MENUITEMAR_GET_ALL );
                preparedStmt.setInt( 1, parentKey );
            }
            resultSet = preparedStmt.executeQuery();

            ArrayList<ArrayList<Comparable<?>>> rightsList = new ArrayList<ArrayList<Comparable<?>>>();
            while ( resultSet.next() )
            {
                ArrayList<Comparable<?>> rights = new ArrayList<Comparable<?>>();

                if ( parentKey == -1 )
                {
                    String curgroupkey = resultSet.getString( "dma_grp_hKey" );
                    int read = resultSet.getInt( "dma_bRead" );
                    int create = resultSet.getInt( "dma_bCreate" );
                    int publish = resultSet.getInt( "dma_bPublish" );
                    int administrate = resultSet.getInt( "dma_bAdministrate" );
                    int update = resultSet.getInt( "dma_bUpdate" );
                    int delete = resultSet.getInt( "dma_bDelete" );
                    int add = resultSet.getInt( "dma_bAdd" );

                    // Special case when user is owner:
                    if ( curgroupkey.equals( ownerGroupKey ) )
                    {
                        update = 1;
                        delete = 1;
                        create = 1;
                    }
                    rights.add( curgroupkey );
                    rights.add( read );
                    rights.add( create );
                    rights.add( publish );
                    rights.add( administrate );
                    rights.add( update );
                    rights.add( delete );
                    rights.add( add );
                }
                else
                {
                    String curgroupkey = resultSet.getString( "mia_grp_hKey" );
                    int read = resultSet.getInt( "mia_bRead" );
                    int create = resultSet.getInt( "mia_bCreate" );
                    int publish = resultSet.getInt( "mia_bPublish" );
                    int administrate = resultSet.getInt( "mia_bAdministrate" );
                    int update = resultSet.getInt( "mia_bUpdate" );
                    int delete = resultSet.getInt( "mia_bDelete" );
                    int add = resultSet.getInt( "mia_bAdd" );

                    // Special case when user is owner:
                    if ( curgroupkey.equals( ownerGroupKey ) )
                    {
                        update = 1;
                        delete = 1;
                        create = 1;
                        add = 1;
                    }

                    rights.add( curgroupkey );
                    rights.add( read );
                    rights.add( create );
                    rights.add( publish );
                    rights.add( administrate );
                    rights.add( update );
                    rights.add( delete );
                    rights.add( add );
                }

                rightsList.add( rights );
            }
            close( resultSet );
            close( preparedStmt );

            preparedStmt = con.prepareStatement( MENUITEMAR_INSERT );
            for ( int i = 0; i < rightsList.size(); ++i )
            {
                preparedStmt.setInt( 1, miKey );

                ArrayList<Comparable<?>> rights = rightsList.get( i );
                for ( int j = 0; j < rights.size(); ++j )
                {
                    preparedStmt.setObject( j + 2, rights.get( j ) );
                }

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public void inheritCategoryAccessRights( int superCategoryKey, CategoryKey categoryKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            StringBuffer sql = new StringBuffer( CAR_SELECT );
            sql.append( " WHERE" );
            sql.append( CAR_WHERE_CLAUSE_CAT );

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, superCategoryKey );
            resultSet = preparedStmt.executeQuery();

            ArrayList<ArrayList<Comparable<?>>> rightsList = new ArrayList<ArrayList<Comparable<?>>>();
            while ( resultSet.next() )
            {
                ArrayList<Comparable<?>> rights = new ArrayList<Comparable<?>>();
                rights.add( resultSet.getString( "grp_hKey" ) );
                rights.add( resultSet.getInt( "car_bRead" ) );
                rights.add( resultSet.getInt( "car_bCreate" ) );
                rights.add( resultSet.getInt( "car_bPublish" ) );
                rights.add( resultSet.getInt( "car_bAdministrate" ) );
                rights.add( resultSet.getInt( "car_bAdminRead" ) );

                rightsList.add( rights );
            }
            close( resultSet );
            close( preparedStmt );

            preparedStmt = con.prepareStatement( CAR_INSERT );
            for ( int i = 0; i < rightsList.size(); ++i )
            {
                preparedStmt.setInt( 1, categoryKey.toInt() );

                ArrayList<Comparable<?>> rights = rightsList.get( i );
                for ( int j = 0; j < rights.size(); ++j )
                {
                    preparedStmt.setObject( j + 2, rights.get( j ) );
                }

                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to inherit category access rights: %t";
            LOG.error( StringUtil.expandString( message, (Object) null, sqle ), sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public void updateAccessRights( User user, Document doc )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        Element rootElement = doc.getDocumentElement();
        int type = Integer.parseInt( rootElement.getAttribute( "type" ) );
        int key = Integer.parseInt( rootElement.getAttribute( "key" ) );

        if ( !validateAccessRightsUpdate( user, type, key ) )
        {
            VerticalRuntimeException.error( this.getClass(), VerticalSecurityException.class,
                                            StringUtil.expandString( "Access denied.", (Object) null, null ) );
        }

        try
        {
            con = getConnection();

            String sql = null;
            switch ( type )
            {
                case AccessRight.MENUITEM:
                    sql = MENUITEMAR_REMOVE_ALL;
                    break;

                case AccessRight.MENUITEM_DEFAULT:
                    sql = DEFAULTMENUAR_REMOVE_ALL;
                    break;

                case AccessRight.CATEGORY:
                    sql = CAR_DELETE_ALL;
                    break;

                case AccessRight.CONTENT:
                    sql = COA_DELETE_ALL;
                    break;

                default:
                    String message = "Accessright type not supported: %0";

                    VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                                    StringUtil.expandString( message, type, null ) );
            }

            preparedStmt = con.prepareStatement( sql );
            preparedStmt.setInt( 1, key );
            preparedStmt.executeUpdate();

            createAccessRights( con, rootElement );
        }
        catch ( SQLException e )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( "A database error occurred: %t", (Object) null,
                                                                     e ), e );
        }
        catch ( VerticalCreateException e )
        {

            VerticalRuntimeException.error( this.getClass(), VerticalUpdateException.class,
                                            StringUtil.expandString( "Error creating accessrights: %t", (Object) null,
                                                                     e ), e );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    private boolean validateAccessRightsUpdate( User user, int type, int key )
    {

        GroupHandler groupHandler = getGroupHandler();

        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        // array containing groups that should always
        // pass security check:
        TStringArrayList autoAllowGroups = new TStringArrayList();
        autoAllowGroups.add( groupHandler.getEnterpriseAdministratorGroupKey() );

        String[] groups = getGroupHandler().getAllGroupMembershipsForUser( user );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean result = false;

        try
        {
            // set the correct sql:
            String sql1 = null;
            String sql2 = null;
            switch ( type )
            {
                case AccessRight.MENUITEM:
                {
                    sql1 = MENUITEMAR_VALIDATE_AR_UPDATE_1;
                    sql2 = MENUITEMAR_VALIDATE_AR_UPDATE_2;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.MENUITEM_DEFAULT:
                {
                    sql1 = DEFAULTMENUAR_VALIDATE_AR_UPDATE_1;
                    sql2 = DEFAULTMENUAR_VALIDATE_AR_UPDATE_2;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.CATEGORY:
                {
                    sql1 = CAR_SECURITY_FILTER_ADMIN;

                    // add site administrators group to the list of approved groups:
                    autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                    break;
                }

                case AccessRight.CONTENT:
                {
                    String userKey = user.getKey().toString();
                    UserKey ownerKey = getContentHandler().getOwnerKey( key );
                    String anonymousUserKey = getUserHandler().getAnonymousUser().getKey().toString();
                    if ( userKey.equals( ownerKey.toString() ) && userKey.equals( anonymousUserKey ) == false )
                    {
                        result = true;
                    }
                    else
                    {
                        sql1 = CAR_SECURITY_FILTER_PUBLISH;

                        // add site administrators group to the list of approved groups:
                        autoAllowGroups.add( groupHandler.getAdminGroupKey() );

                        // get the content's category key
                        key = getContentHandler().getCategoryKey( key ).toInt();
                    }
                    break;
                }

                default:
                {
                    // unknown access rights type, throw runtime exception

                    VerticalRuntimeException.error( this.getClass(), VerticalEngineRuntimeException.class,
                                                    StringUtil.expandString( "Access denied.", (Object) null, null )
                                                     );
                    result = false;
                }
            }

            if ( !result )
            {
                con = getConnection();

                if ( type == AccessRight.MENUITEM_DEFAULT || type == AccessRight.MENUITEM )
                {
                    StringBuffer sql = new StringBuffer( sql1 );
                    for ( int i = 0; i < groups.length; ++i )
                    {
                        if ( i != 0 )
                        {
                            sql.append( "," );
                        }

                        if ( autoAllowGroups.contains( groups[i] ) )
                        {
                            result = true;
                            break;
                        }

                        sql.append( "'" );
                        sql.append( groups[i] );
                        sql.append( "'" );
                    }
                    if ( !result )
                    {
                        sql.append( sql2 );

                        //con = getConnection();
                        preparedStmt = con.prepareStatement( sql.toString() );
                        preparedStmt.setInt( 1, key );
                        resultSet = preparedStmt.executeQuery();

                        if ( resultSet.next() )
                        {
                            result = true;
                        }
                    }
                }
                else if ( type == AccessRight.CATEGORY || type == AccessRight.CONTENT || type == AccessRight.SECTION )
                {
                    StringBuffer temp = new StringBuffer( groups.length * 2 );
                    for ( int i = 0; i < groups.length; i++ )
                    {
                        if ( i > 0 )
                        {
                            temp.append( "," );
                        }
                        if ( autoAllowGroups.contains( groups[i] ) )
                        {
                            result = true;
                            break;
                        }
                        temp.append( "'" );
                        temp.append( groups[i] );
                        temp.append( "'" );
                    }
                    if ( !result )
                    {
                        sql1 = StringUtil.expandString( sql1, temp );

                        //con = getConnection();
                        preparedStmt = con.prepareStatement( sql1 );
                        preparedStmt.setInt( 1, key );
                        resultSet = preparedStmt.executeQuery();

                        if ( resultSet.next() )
                        {
                            result = true;
                        }
                    }
                }
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return result;
    }

    public String appendCategorySQL( User user, String sql, boolean adminread, boolean publish )
    {

        StringBuffer buf = new StringBuffer( sql );

        appendCategorySQL( user, buf, adminread, publish );

        return buf.toString();
    }

    public void appendCategorySQL( User user, StringBuffer sql, boolean adminread, boolean publish )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        if ( isSiteAdmin( user, groups ) )
        {
            return;
        }

        StringBuffer newSQL = sql;
        newSQL.append( " AND" );
        StringBuffer sqlFilterRights = new StringBuffer( "" );
        if ( adminread || publish )
        {
            newSQL.append( CAR_WHERE_CLAUSE_SECURITY_FILTER_RIGHTS );
            if ( adminread )
            {
                sqlFilterRights.append( " AND" );
                sqlFilterRights.append( CAR_WHERE_CLAUSE_ADMINREAD );
            }
            if ( publish )
            {
                sqlFilterRights.append( " AND" );
                sqlFilterRights.append( CAR_WHERE_CLAUSE_PUBLISH );
            }
        }
        else
        {
            newSQL.append( CAR_WHERE_CLAUSE_SECURITY_FILTER );
        }
        StringBuffer sb_groups = new StringBuffer( groups.length * 2 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                sb_groups.append( "," );
            }
            sb_groups.append( "'" );
            sb_groups.append( groups[i] );
            sb_groups.append( "'" );
        }

        StringUtil.replaceString( newSQL, "%groups", sb_groups.toString() );
        StringUtil.replaceString( newSQL, "%filterRights", sqlFilterRights.toString() );
    }

    public void appendSectionSQL( User user, StringBuffer sql, SectionCriteria criteria )
    {

        boolean publishRight = criteria.isPublishRight();
        boolean approveRight = criteria.isApproveRight();
        boolean administrateRight = criteria.isAdminRight();

        HashMap<String, String> accessRightsMap = new HashMap<String, String>();
        if ( publishRight )
        {
            accessRightsMap.put( "accessrights/userright/@publish", "true" );
        }
        if ( approveRight )
        {
            accessRightsMap.put( "accessrights/userright/@approve", "true" );
        }
        if ( administrateRight )
        {
            accessRightsMap.put( "accessrights/userright/@administrate", "true" );
        }

        appendAccessRightsSQL( user, Types.SECTION, sql, accessRightsMap );

    }

    public String appendMenuItemSQL( User user, String sql )
    {

        return appendMenuItemSQL( user, sql, null );
    }

    public String appendMenuItemSQL( User user, String sql, MenuItemCriteria criteria )
    {

        StringBuffer bufferSQL = new StringBuffer( sql );
        appendMenuItemSQL( user, bufferSQL, criteria );
        return bufferSQL.toString();
    }

    public void appendMenuItemSQL( User user, StringBuffer sql, MenuItemCriteria criteria )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        // Return if we shall not apply security
        if ( criteria != null && criteria.applySecurity() == false )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();

        StringBuffer newSQL = sql;

        // find all groups that the user is a member of
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );

        // use standard sql if user is a member of
        // the enterprise admin group
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) )
            {
                return;
            }
        }

        newSQL.append( " AND ( " );

        // appending group clause
        newSQL.append( MENUITEMAR_SECURITY_FILTER_1 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                newSQL.append( "," );
            }
            newSQL.append( "'" );
            newSQL.append( groups[i] );
            newSQL.append( "'" );
        }
        newSQL.append( ")" );

        // appending rights criteria
        if ( criteria != null )
        {
            if ( criteria.getType() != Criteria.NONE )
            {
                StringBuffer rightsSQL = new StringBuffer();
                String binding = criteria.getBinding();
                if ( criteria.includeUpdate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bUpdate = " );
                    rightsSQL.append( criteria.getUpdateAsInt() );
                }
                if ( criteria.includeDelete() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bDelete = " );
                    rightsSQL.append( criteria.getDeleteAsInt() );
                }
                if ( criteria.includeCreate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bCreate = " );
                    rightsSQL.append( criteria.getCreateAsInt() );
                }
                if ( criteria.includeAdministrate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bAdministrate = " );
                    rightsSQL.append( criteria.getAdministrateAsInt() );
                }
                if ( criteria.includeAdd() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bAdd = " );
                    rightsSQL.append( criteria.getAddAsInt() );
                }
                if ( criteria.includePublish() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " mia_bPublish = " );
                    rightsSQL.append( criteria.getPublishAsInt() );
                }

                if ( rightsSQL.length() > 0 )
                {
                    newSQL.append( " AND (" );
                    newSQL.append( rightsSQL );
                    newSQL.append( " )" );
                }
            }
        }

        newSQL.append( ")" );

        // Støtte for å selektere ut menuitem som har en parent med en eller annen rettighet..
        if ( criteria != null && criteria.hasParentCriteria() )
        {
            MenuItemCriteria parentCriteria = criteria.getParentCriteria();
            newSQL.append( " OR " + MENUITEMAR_SECURITY_FILTER_ON_PARENT );
            // appending group clause
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i != 0 )
                {
                    newSQL.append( "," );
                }
                newSQL.append( "'" );
                newSQL.append( groups[i] );
                newSQL.append( "'" );
            }
            newSQL.append( ")" );

            StringBuffer rightsSQL = new StringBuffer();
            String binding = parentCriteria.getBinding();
            if ( parentCriteria.includeUpdate() )
            {
                if ( rightsSQL.length() > 0 )
                {
                    rightsSQL.append( binding );
                }
                rightsSQL.append( " mia_bUpdate=" );
                rightsSQL.append( parentCriteria.getUpdateAsInt() );
            }
            if ( parentCriteria.includeDelete() )
            {
                if ( rightsSQL.length() > 0 )
                {
                    rightsSQL.append( binding );
                }
                rightsSQL.append( " mia_bDelete=" );
                rightsSQL.append( parentCriteria.getDeleteAsInt() );
            }
            if ( parentCriteria.includeCreate() )
            {
                if ( rightsSQL.length() > 0 )
                {
                    rightsSQL.append( binding );
                }
                rightsSQL.append( " mia_bCreate=" );
                rightsSQL.append( parentCriteria.getCreateAsInt() );
            }
            if ( parentCriteria.includeAdministrate() )
            {
                if ( rightsSQL.length() > 0 )
                {
                    rightsSQL.append( binding );
                }
                rightsSQL.append( " mia_bAdministrate=" );
                rightsSQL.append( parentCriteria.getAdministrateAsInt() );
            }

            if ( rightsSQL.length() > 0 )
            {
                newSQL.append( " AND (" );
                newSQL.append( rightsSQL );
                newSQL.append( " )" );
            }

            newSQL.append( ")" );
        }

        newSQL.append( ")" );

        //return newSQL;
    }

    public void appendMenuSQL( User user, StringBuffer sql, MenuCriteria criteria )
    {

        // Return if we shall not apply security
        if ( criteria != null && criteria.applySecurity() == false )
        {
            return;
        }

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();

        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();

        StringBuffer newSQL = sql;

        // find all groups that the user is a member of
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        for ( int i = 0; i < groups.length; ++i )
        {
            // use standard sql if user is a member of
            // the enterprise admin group
            if ( groups[i].equals( epGroup ) )
            {
                return;
            }
        }

        newSQL.append( DEFAULTMENUAR_SECURITY_FILTER_1 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i != 0 )
            {
                newSQL.append( "," );
            }

            newSQL.append( "'" );
            newSQL.append( groups[i] );
            newSQL.append( "'" );
        }
        newSQL.append( ")" );

        if ( criteria != null )
        {
            if ( criteria.getType() != Criteria.NONE )
            {
                StringBuffer rightsSQL = new StringBuffer();
                String binding = criteria.getBinding();
                if ( criteria.includeUpdate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " dma_bUpdate=" );
                    rightsSQL.append( criteria.getUpdateAsInt() );
                }
                if ( criteria.includeDelete() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " dma_bDelete=" );
                    rightsSQL.append( criteria.getDeleteAsInt() );
                }
                if ( criteria.includeCreate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " dma_bCreate=" );
                    rightsSQL.append( criteria.getCreateAsInt() );
                }
                if ( criteria.includeAdministrate() )
                {
                    if ( rightsSQL.length() > 0 )
                    {
                        rightsSQL.append( binding );
                    }
                    rightsSQL.append( " dma_bAdministrate=" );
                    rightsSQL.append( criteria.getAdministrateAsInt() );
                }

                if ( rightsSQL.length() > 0 )
                {
                    newSQL.append( " AND (" );
                    newSQL.append( rightsSQL );
                    newSQL.append( " )" );
                }
            }
        }

        newSQL.append( ")" );
    }

    public boolean isEnterpriseAdmin( User user, String[] allGroupMemberships )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        String epGroup = getGroupHandler().getEnterpriseAdministratorGroupKey();

        for ( int i = 0; i < allGroupMemberships.length; ++i )
        {
            if ( allGroupMemberships[i].equals( epGroup ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isEnterpriseAdmin( User user )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();

        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isSiteAdmin( User user, String[] groups )
    {
        if ( user.isEnterpriseAdmin() )
        {
            return true;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String saGroup = groupHandler.getAdminGroupKey();

        for ( int i = 0; i < groups.length; ++i )
        {
            if ( groups[i].equals( epGroup ) || groups[i].equals( saGroup ) )
            {
                return true;
            }
        }

        return false;
    }

    public boolean isSiteAdmin( User user )
    {
        String[] groups = getGroupHandler().getAllGroupMembershipsForUser( user );
        return isSiteAdmin( user, groups );
    }

    public Document getUsersWithPublishRight( CategoryKey categoryKey )
    {
        StringBuffer sql =
            XDG.generateSelectSQL( db.tCatAccessRight, db.tCatAccessRight.car_grp_hKey, true, db.tCatAccessRight.car_cat_lKey );
        XDG.appendWhereSQL( sql, db.tCatAccessRight.car_bPublish, XDG.OPERATOR_EQUAL, 1 );
        String[] groups = getCommonHandler().getStringArray( sql.toString(), categoryKey.toInt() );
        String[] members = getGroupHandler().getGroupMembers( groups, null, true );
        return getUserHandler().getUsersByGroupKeys( ArrayUtil.concat( groups, members, true ) );
    }

    public void appendAccessRightsSQL( User user, int type, StringBuffer sql, HashMap<String, String> accessRightsXPaths )
    {

        if ( user != null && user.isEnterpriseAdmin() )
        {
            return;
        }

        GroupHandler groupHandler = getGroupHandler();
        String epGroup = groupHandler.getEnterpriseAdministratorGroupKey();
        String[] groups = groupHandler.getAllGroupMembershipsForUser( user );
        Arrays.sort( groups );

        // allow if user is a member of the enterprise admin group
        if ( Arrays.binarySearch( groups, epGroup ) >= 0 )
        {
            return;
        }

        Table tabAccessRight = null;
        Column colAccessRightGroup = null;
        Column colAccessRightKey = null;
        Column colLocalKey = null;
        String accessRight_prefix = null;

        switch ( type )
        {
            case Types.SECTION:
                colLocalKey = SectionContentView.getInstance().mei_lKey;
                tabAccessRight = db.tMenuItemAR;
                colAccessRightGroup = db.tMenuItemAR.mia_grp_hKey;
                colAccessRightKey = db.tMenuItemAR.mia_mei_lKey;
                accessRight_prefix = "mia_";
                break;
            case Types.SECTIONCONTENT:
                colLocalKey = SectionContentView.getInstance().mei_lKey;
                tabAccessRight = db.tMenuItemAR;
                colAccessRightGroup = db.tMenuItemAR.mia_grp_hKey;
                colAccessRightKey = db.tMenuItemAR.mia_mei_lKey;
                accessRight_prefix = "mia_";
                break;
            case Types.CONTENTVIEW:
            case Types.CONTENTVERSIONVIEW:
                // This only checks accessright on the category
                colLocalKey = ContentView.getInstance().cat_lKey;
                tabAccessRight = db.tCatAccessRight;
                colAccessRightGroup = db.tCatAccessRight.car_grp_hKey;
                colAccessRightKey = db.tCatAccessRight.car_cat_lKey;
                accessRight_prefix = "car_";
                break;
            default:

                VerticalRuntimeException.error( this.getClass(), VerticalEngineRuntimeException.class,
                                                StringUtil.expandString(
                                                        "Access rights not implemented for type: " + type, null, null )
                                                 );
                break;
        }

        if ( sql.toString().toLowerCase().indexOf( "where" ) < 0 )
        {
            sql.append( " WHERE" );
        }
        else
        {
            sql.append( " AND" );
        }

        StringBuffer sqlFilterRights = new StringBuffer();
        sql.append( " EXISTS (SELECT " );
        sql.append( colAccessRightGroup );
        sql.append( " FROM " );
        sql.append( tabAccessRight );
        sql.append( " WHERE " );
        sql.append( colAccessRightKey );
        sql.append( " = " );
        sql.append( colLocalKey );
        sql.append( " AND " );
        sql.append( colAccessRightGroup );
        sql.append( " IN (%groups) %filterRights" );

        // Check what types of accessrights to append
        for ( String accessRight : accessRightsXPaths.keySet() )
        {
            sqlFilterRights.append( " AND " );

            Column colAccessRight = null;

            // Access rights for content only checks the category
            if ( type == Types.CONTENTVIEW && !accessRight.startsWith( "category" ) )
            {

                VerticalRuntimeException.error( this.getClass(), VerticalEngineRuntimeException.class,
                                                StringUtil.expandString(
                                                        "Access right " + accessRight + " not supported for type: " +
                                                                type, null, null ) );
            }

            if ( accessRight.endsWith( "administrate" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bAdministrate" );
            }
            else if ( accessRight.endsWith( "create" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bCreate" );
            }
            else if ( accessRight.endsWith( "delete" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bDelete" );
            }
            else if ( accessRight.endsWith( "publish" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bPublish" );
            }
            else if ( accessRight.endsWith( "read" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bRead" );
            }
            else if ( accessRight.endsWith( "update" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bUpdate" );
            }
            else if ( accessRight.endsWith( "adminread" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bAdminRead" );
            }
            else if ( accessRight.endsWith( "approve" ) )
            {
                colAccessRight = tabAccessRight.getColumn( accessRight_prefix + "bApprove" );
            }
            else
            {

                VerticalRuntimeException.error( this.getClass(), VerticalEngineRuntimeException.class,
                                                StringUtil.expandString(
                                                        "Access right " + accessRight + " not supported for type: " +
                                                                type, null, null ) );
            }
            sqlFilterRights.append( colAccessRight );
            sqlFilterRights.append( " = " );
            sqlFilterRights.append( colAccessRight.getColumnValue( accessRightsXPaths.get( accessRight ) ) );
        }

        sql.append( ")" );

        StringBuffer sb_groups = new StringBuffer( groups.length * 2 );
        for ( int i = 0; i < groups.length; ++i )
        {
            if ( i > 0 )
            {
                sb_groups.append( "," );
            }
            sb_groups.append( "'" );
            sb_groups.append( groups[i] );
            sb_groups.append( "'" );
        }
        StringUtil.replaceString( sql, "%groups", sb_groups.toString() );
        StringUtil.replaceString( sql, "%filterRights", sqlFilterRights.toString() );
    }

    public boolean validateSectionApprove( User user, int sectionKey )
    {
        // approve = publish on menuitem
        MenuItemKey menuItemKey = getSectionHandler().getMenuItemKeyBySection( sectionKey );
        MenuItemAccessRight mia = getSecurityHandler().getMenuItemAccessRight( user, menuItemKey );
        return mia.getPublish();
    }

    public boolean validateContentAddToSection( User user, int sectionKey )
    {
        // add = add on menuitem
        MenuItemKey menuItemKey = getSectionHandler().getMenuItemKeyBySection( sectionKey );
        MenuItemAccessRight mia = getSecurityHandler().getMenuItemAccessRight( user, menuItemKey );
        return mia.getAdd() || mia.getPublish();
    }


}
