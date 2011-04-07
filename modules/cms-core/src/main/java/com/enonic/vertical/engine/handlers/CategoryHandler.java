/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.SelectString;
import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.RelationAggregator;
import com.enonic.esl.util.RelationNode;
import com.enonic.esl.util.RelationTree;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalKeyException;
import com.enonic.vertical.engine.VerticalRemoveException;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.VerticalUpdateException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.dbmodel.CategoryTable;
import com.enonic.vertical.engine.dbmodel.CategoryView;
import com.enonic.vertical.engine.dbmodel.ConAccessRight2Table;
import com.enonic.vertical.engine.dbmodel.ContentPublishedView;

import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.category.CategoryXmlCreator;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.store.dao.CategoryDao;

import com.enonic.cms.domain.CalendarUtil;
import com.enonic.cms.domain.content.category.CategoryAccessRightsAccumulated;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.category.CategoryStatistics;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;

public class CategoryHandler
    extends BaseHandler
{

    private final static String CON_TABLE = "tContent";

    private final static String CAT_TABLE = "tCategory";

    private final static String CAT_INSERT =
        "INSERT INTO " + CAT_TABLE + " (cat_lKey,cat_uni_lKey,cat_cty_lKey,cat_cat_lSuper,cat_usr_hOwner,cat_dteCreated,cat_bDeleted," +
            "cat_sName,cat_sDescription,cat_usr_hModifier,cat_dteTimestamp,cat_bautoapprove)" + " VALUES (?,?,?,?,?,@currentTimestamp@" +
            ",0,?,?,?," + "@currentTimestamp@,?" + ")";

    private final static String CAT_UPDATE =
        "UPDATE " + CAT_TABLE + " SET cat_uni_lKey = ?" + ",cat_cty_lKey = ?" + ",cat_cat_lSuper = ?" + ",cat_usr_hOwner = ?" +
            ",cat_dteCreated = ?" + ",cat_sName = ?" + ",cat_sDescription = ?" + ",cat_usr_hModifier = ?" + ",cat_dteTimestamp = " +
            "@currentTimestamp@" + ",cat_bAutoApprove = ?" + " WHERE cat_lKey = ?";

    private final static String CAT_DELETE = "UPDATE " + CAT_TABLE + " SET cat_bDeleted = 1 WHERE cat_lKey=?";

    private final static String CAT_SELECT_COUNT = "SELECT count(cat_lKey) FROM " + CAT_TABLE;

    private final static String CAT_SELECT_CON_COUNT =
        "SELECT cat_lKey," + " count(con_lKey) AS cat_con_lCount" + " FROM " + CAT_TABLE + " LEFT JOIN " + CON_TABLE +
            " ON con_lKey IS NULL OR con_cat_lKey = cat_lKey";

    private final static String CAT_SELECT_KEY = "SELECT cat_lKey FROM " + CAT_TABLE;

    private final static String CAT_WHERE_CLAUSE_CAT_NOT_DELETED = " cat_bDeleted = 0";

    private final static String CAT_WHERE_CLAUSE_CON_NOT_DELETED_OR_NULL = " (con_bDeleted IS NULL OR con_bDeleted = 0)";

    private final static String CAT_WHERE_CLAUSE_MANY = " cat_lKey IN (%0)";

    private final static String CAT_WHERE_CLAUSE_SCA_ONE = " cat_cat_lSuper = ?";

    private final static String CAT_WHERE_CLAUSE_CTY = " cat_cty_lKey = ?";

    private final static String CAT_WHERE_CLAUSE_UNI = " cat_uni_lKey = ?";

    private final static String CAT_GROUP_BY_CAT = " GROUP BY cat_lKey";

    private final static String CAT_SET_UNITKEY = "UPDATE tCategory SET cat_uni_lKey = ? WHERE cat_lKey = ?";

    private final static String CAT_SET_UNITKEY_AND_SUPERKEY =
        "UPDATE tCategory SET cat_uni_lKey = ?, cat_cat_lSuper = ? WHERE cat_lKey = ?";

    // SQL objects:

    private SelectString catSelectName;

    private SelectString catSelectSca;

    private CategoryStatisticsHelper categoryStatisticsHelper;

    @Autowired
    private CategoryDao categoryDao;

    public void init()
    {
        super.init();

        categoryStatisticsHelper = new CategoryStatisticsHelper( this );

        // initialize SQL objects:
        catSelectName = new SelectString();
        String q = catSelectName.addTableToFrom( CAT_TABLE, true );
        catSelectName.addColumnToSelect( q + ".cat_lKey" );
        catSelectName.addColumnToSelect( q + ".cat_uni_lKey" );
        catSelectName.addColumnToSelect( q + ".cat_cty_lKey" );
        catSelectName.addColumnToSelect( q + ".cat_cat_lSuper" );
        catSelectName.addColumnToSelect( q + ".cat_sName" );

        catSelectSca = new SelectString();
        q = catSelectSca.addTableToFrom( CAT_TABLE, true );
        catSelectSca.addColumnToSelect( q + ".cat_cat_lSuper" );
    }

    private int[] createCategory( User olduser, CopyContext copyContext, Document categoryDoc, boolean useOldKey )
        throws VerticalSecurityException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        TIntArrayList newKeys = null;

        try
        {
            Document subCategoriesDoc = XMLTool.createDocument( "categories" );
            Element subCategoriesElem = subCategoriesDoc.getDocumentElement();
            int subCategoryCount = 0;

            Element docElem = categoryDoc.getDocumentElement();
            Element[] categoryElems;
            if ( "category".equals( docElem.getTagName() ) )
            {
                categoryElems = new Element[]{docElem};
            }
            else
            {
                categoryElems = XMLTool.getElements( docElem );
            }

            con = getConnection();
            preparedStmt = con.prepareStatement( CAT_INSERT );

            newKeys = new TIntArrayList();
            for ( Element root : categoryElems )
            {
                // key
                int key;
                String keyStr = root.getAttribute( "key" );
                if ( !useOldKey || keyStr == null || keyStr.length() == 0 )
                {
                    key = getNextKey( db.tCategory.toString() );
                }
                else
                {
                    key = Integer.parseInt( keyStr );
                }
                if ( copyContext != null )
                {
                    int oldCategoryKey = Integer.parseInt( keyStr );
                    copyContext.putCategoryKey( oldCategoryKey, key );
                }
                newKeys.add( key );

                // get the foreign keys
                int unitkey = -1;
                keyStr = root.getAttribute( "unitkey" );
                if ( keyStr.length() > 0 )
                {
                    unitkey = Integer.parseInt( keyStr );
                }
                int ctyKey = -1;
                keyStr = root.getAttribute( "contenttypekey" );
                if ( keyStr.length() > 0 )
                {
                    ctyKey = Integer.parseInt( keyStr );
                }
                int scakey = -1;
                keyStr = root.getAttribute( "supercategorykey" );
                if ( keyStr.length() > 0 )
                {
                    scakey = Integer.parseInt( keyStr );
                }

                preparedStmt.setInt( 1, key );
                if ( unitkey >= 0 )
                {
                    preparedStmt.setInt( 2, unitkey );
                }
                else
                {
                    preparedStmt.setNull( 2, Types.INTEGER );
                }
                if ( ctyKey >= 0 )
                {
                    preparedStmt.setInt( 3, ctyKey );
                }
                else
                {
                    preparedStmt.setNull( 3, Types.INTEGER );
                }
                if ( scakey >= 0 )
                {
                    preparedStmt.setInt( 4, scakey );
                }
                else
                {
                    preparedStmt.setNull( 4, Types.INTEGER );
                }

                // get the contentcategory's sub-elements
                Map subelems = XMLTool.filterElements( root.getChildNodes() );

                preparedStmt.setString( 5, olduser.getKey().toString() );

                // element: created (using the database timestamp at creation)
                /* no code */

                // name
                String name = root.getAttribute( "name" );
                preparedStmt.setCharacterStream( 6, new StringReader( name ), name.length() );

                // element: description (optional)
                Element subelem = (Element) subelems.get( "description" );
                if ( subelem != null )
                {
                    String description = XMLTool.getElementText( subelem );

                    if ( description == null || description.length() == 0 )
                    {
                        preparedStmt.setNull( 7, Types.VARCHAR );
                    }
                    else
                    {
                        preparedStmt.setCharacterStream( 7, new StringReader( description ), description.length() );
                    }
                }
                else
                {
                    preparedStmt.setNull( 7, Types.VARCHAR );
                }

                preparedStmt.setString( 8, olduser.getKey().toString() );

                // element: timestamp (using the database timestamp at creation)
                /* no code */

                preparedStmt.setInt( 9, root.getAttribute( "autoApprove" ).equals( "true" ) ? 1 : 0 );

                // add the content category
                int result = preparedStmt.executeUpdate();

                if ( result <= 0 )
                {
                    String message = "Failed to create category.";
                    SQLWarning warning = preparedStmt.getWarnings();
                    if ( warning != null )
                    {
                        message += " (ErrorCode=%0, %1, SQLSTATE=%2)";
                        Object[] msgData = new Object[3];
                        msgData[0] = warning.getErrorCode();
                        msgData[1] = warning.getMessage();
                        msgData[2] = warning.getSQLState();
                        VerticalEngineLogger.errorCreate( this.getClass(), 0, message, msgData, null );
                    }
                    else
                    {
                        message += " (No warning)";
                        VerticalEngineLogger.errorCreate( this.getClass(), 0, message, null );
                    }
                }

                // set category access rights
                SecurityHandler securityHandler = getSecurityHandler();
                if ( scakey >= 0 )
                {
                    securityHandler.inheritCategoryAccessRights( scakey, new CategoryKey( key ) );
                }
                else
                {
                    GroupHandler groupHandler = getGroupHandler();
                    Document accessrightsDoc = XMLTool.createDocument( "accessrights" );
                    Element elem = accessrightsDoc.getDocumentElement();
                    elem.setAttribute( "key", String.valueOf( key ) );
                    elem.setAttribute( "type", String.valueOf( AccessRight.CATEGORY ) );
                    elem = XMLTool.createElement( accessrightsDoc, elem, "accessright" );
                    String siteGroupKey = groupHandler.getAdminGroupKey();
                    elem.setAttribute( "groupkey", siteGroupKey );
                    elem.setAttribute( "read", "true" );
                    elem.setAttribute( "create", "true" );
                    elem.setAttribute( "publish", "true" );
                    elem.setAttribute( "administrate", "true" );
                    elem.setAttribute( "adminread", "true" );
                    securityHandler.updateAccessRights( olduser, accessrightsDoc );
                }
                Element accessrightsElem = (Element) subelems.get( "accessrights" );
                if ( accessrightsElem != null )
                {
                    accessrightsElem.setAttribute( "key", String.valueOf( key ) );
                    Document tempDoc = XMLTool.createDocument();
                    tempDoc.appendChild( tempDoc.importNode( accessrightsElem, true ) );
                    securityHandler.updateAccessRights( olduser, tempDoc );
                }

                Element[] elems = XMLTool.getElements( XMLTool.getElement( root, "categories" ) );
                subCategoryCount += elems.length;
                for ( Element elem : elems )
                {
                    subCategoriesElem.appendChild( subCategoriesDoc.importNode( elem, true ) );
                }
            }

            // create sub categories
            if ( subCategoryCount > 0 )
            {
                newKeys.add( createCategory( olduser, copyContext, subCategoriesDoc, useOldKey ) );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to create category: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, sqle );
        }
        catch ( VerticalKeyException gke )
        {
            String message = "Failed to create category: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, gke );
        }
        catch ( VerticalUpdateException vue )
        {
            String message = "Failed to set default categories for top category: %t";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, vue );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }

        return newKeys.toArray();
    }

    public int createCategory( User olduser, Document doc )
        throws VerticalSecurityException
    {

        int[] key = createCategory( olduser, null, doc, true );
        if ( key == null || key.length == 0 )
        {
            String message = "Failed to create content category. No category key returned.";
            VerticalEngineLogger.errorCreate( this.getClass(), 1, message, null );
        }

        return key[0];
    }

    public int createCategory( User olduser, CategoryKey superCategoryKey, String name )
        throws VerticalSecurityException
    {
        Document doc = getCategory( olduser, superCategoryKey );
        Element categoryElem = XMLTool.getFirstElement( doc.getDocumentElement() );
        categoryElem.setAttribute( "supercategorykey", categoryElem.getAttribute( "key" ) );
        categoryElem.setAttribute( "name", name );
        categoryElem.removeAttribute( "key" );
        categoryElem.removeAttribute( "created" );
        categoryElem.removeAttribute( "timestamp" );
        Element descriptionElem = XMLTool.getElement( categoryElem, "description" );
        if ( descriptionElem != null )
        {
            categoryElem.removeChild( descriptionElem );
        }
        Element ownerElem = XMLTool.getElement( categoryElem, "owner" );
        categoryElem.removeChild( ownerElem );
        Element modifierElem = XMLTool.getElement( categoryElem, "modifier" );
        categoryElem.removeChild( modifierElem );

        return createCategory( olduser, doc );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        CategoryView view = CategoryView.getInstance();
        StringBuffer sql = XDG.generateSelectSQL( view, view.cat_lKey, false, (Column[]) null );
        XDG.appendWhereSQL( sql, view.cat_cat_lSuper, XDG.OPERATOR_EQUAL, superCategoryKey );
        XDG.appendWhereSQL( sql, view.cat_sName, XDG.OPERATOR_EQUAL, name );
        CommonHandler commonHandler = getCommonHandler();
        return commonHandler.getInt( sql.toString(), (int[]) null );
    }

    public int[] getCategoryKeysBySuperCategory( Connection con, CategoryKey superCategoryKey, boolean recursive )
    {

        return getCategoryKeysBySuperCategories( con, new int[]{superCategoryKey.toInt()}, recursive );
    }

    public int[] getCategoryKeysBySuperCategories( Connection __con, int[] superCategoryKeys, boolean recursive )
    {

        if ( superCategoryKeys == null || superCategoryKeys.length == 0 )
        {
            return new int[0];
        }

        TIntArrayList categoryKeys = new TIntArrayList();
        for ( int i = 0; i < superCategoryKeys.length; i++ )
        {
            categoryKeys.add( getSubCategoriesByParent( new CategoryKey( superCategoryKeys[i] ), recursive ).toArray() );
        }

        return categoryKeys.toArray();
    }

    public TIntArrayList getSubCategoriesByParent( CategoryKey parentKey, boolean recursive )
    {
        TIntArrayList categoryKeys = new TIntArrayList();
        CategoryEntity parent = categoryDao.findByKey( parentKey );
        if ( parent == null )
        {
            return categoryKeys;
        }
        for ( CategoryKey categoryKey : parent.getChildrenKeys() )
        {
            categoryKeys.add( categoryKey.toInt() );
            if ( recursive )
            {
                categoryKeys.add( getSubCategoriesByParent( categoryKey, true ).toArray() );
            }
        }
        return categoryKeys;
    }

    public Document getCategory( User olduser, CategoryKey categoryKey )
    {
        CategoryXmlCreator xmlCreator = new CategoryXmlCreator();
        UserEntity user = securityService.getUser( olduser );

        if ( user == null )
        {
            return xmlCreator.createEmptyCategoriesDocument( "No user given" ).getAsDOMDocument();
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );
        xmlCreator.setCategoryAccessResolver( new CategoryAccessResolver( groupDao ) );
        xmlCreator.setUser( user );
        xmlCreator.setIncludeAccessRightsInfo( false );
        xmlCreator.setIncludeDisabledInfo( false );
        return xmlCreator.createCategory( category ).getAsDOMDocument();
    }

    public Document getCategoryNameDoc( CategoryKey categoryKey )
    {
        CategoryXmlCreator xmlCreator = new CategoryXmlCreator();
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        return xmlCreator.createCategoryNames( category ).getAsDOMDocument();
    }

    public String getCategoryName( CategoryKey categoryKey )
    {

        String name = "";
        if ( categoryKey != null )
        {
            CategoryEntity category = categoryDao.findByKey( categoryKey );
            if ( category != null )
            {
                name = category.getName();
            }
        }
        return name;
    }

    /**
     * @see CategoryHandler#getContentCount(Connection, CategoryKey, boolean)
     */
    public int getContentCount( Connection con, CategoryKey categoryKey, boolean recursive )
    {

        if ( categoryKey == null )
        {
            return 0;
        }
        return getContentCount( con, new int[]{categoryKey.toInt()}, recursive );
    }

    private int getContentCount( Connection _con, int[] categoryKeys, boolean recursive )
    {

        if ( categoryKeys == null || categoryKeys.length == 0 )
        {
            return 0;
        }

        Connection con = null;
        PreparedStatement preparedStmt1 = null, preparedStmt2 = null;
        ResultSet resultSet1 = null, resultSet2 = null;
        int contentCount = 0;

        try
        {
            StringBuffer sql = new StringBuffer( CAT_SELECT_CON_COUNT );
            StringBuffer tempSql = new StringBuffer();
            for ( int i = 0; i < categoryKeys.length; i++ )
            {
                if ( i > 0 )
                {
                    tempSql.append( ',' );
                }
                tempSql.append( Integer.toString( categoryKeys[i] ) );
            }
            sql.append( " WHERE" );
            sql.append( CAT_WHERE_CLAUSE_CAT_NOT_DELETED );
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_CON_NOT_DELETED_OR_NULL );
            sql.append( " AND" );
            sql.append( StringUtil.expandString( CAT_WHERE_CLAUSE_MANY, tempSql ) );
            sql.append( CAT_GROUP_BY_CAT );

            if ( _con == null )
            {
                con = getConnection();
            }
            else
            {
                con = _con;
            }
            preparedStmt1 = con.prepareStatement( sql.toString() );
            resultSet1 = preparedStmt1.executeQuery();

            while ( resultSet1.next() )
            {
                contentCount += resultSet1.getInt( "cat_con_lCount" );
                int catKey = resultSet1.getInt( "cat_lKey" );

                if ( recursive )
                {
                    sql.setLength( 0 );
                    sql.append( CAT_SELECT_KEY );
                    sql.append( " WHERE" );
                    sql.append( CAT_WHERE_CLAUSE_SCA_ONE );
                    preparedStmt2 = con.prepareStatement( sql.toString() );
                    preparedStmt2.setInt( 1, catKey );
                    resultSet2 = preparedStmt2.executeQuery();

                    TIntArrayList catKeys = new TIntArrayList();
                    while ( resultSet2.next() )
                    {
                        catKeys.add( resultSet2.getInt( "cat_lKey" ) );
                    }

                    resultSet2.close();
                    resultSet2 = null;
                    preparedStmt2.close();
                    preparedStmt2 = null;

                    if ( catKeys.size() > 0 )
                    {
                        contentCount += getContentCount( con, catKeys.toArray(), recursive );
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to calculate categories' content counts: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( resultSet1 );
            close( resultSet2 );
            close( preparedStmt1 );
            close( preparedStmt2 );
            if ( _con == null )
            {
                close( con );
            }
        }

        return contentCount;
    }

    public int getContentTypeKey( CategoryKey categoryKey )
    {
        int contentTypeKey = -1;
        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category != null && category.getContentType() != null )
        {
            contentTypeKey = category.getContentType().getKey();
        }
        return contentTypeKey;
    }

    public Document getSuperCategoryNames( CategoryKey categoryKey, boolean withContentCount, boolean includeCategory )
    {
        Map<CategoryEntity, Integer> contentCountMap = new HashMap<CategoryEntity, Integer>();
        List<CategoryEntity> categories = new ArrayList<CategoryEntity>();

        CategoryXmlCreator xmlCreator = new CategoryXmlCreator();

        if ( categoryKey == null )
        {
            return xmlCreator.createEmptyCategoryNamesDocument( "No categorykey given" ).getAsDOMDocument();
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );

        if ( category == null )
        {
            return xmlCreator.createEmptyCategoryNamesDocument( "No category found" ).getAsDOMDocument();
        }

        CategoryEntity currCategory;

        if ( includeCategory )
        {
            currCategory = category;
        }
        else
        {
            currCategory = category.getParent();
        }

        while ( currCategory != null )
        {
            categories.add( currCategory );

            if ( withContentCount )
            {
                contentCountMap.put( category, getContentCount( null, category.getKey(), true ) );
            }

            currCategory = currCategory.getParent();
        }

        // Reverse list to get the root first in result
        Collections.reverse( categories );

        XMLDocument newDoc = xmlCreator.createCategoryNames( categories, contentCountMap );

        return newDoc.getAsDOMDocument();
    }

    public int getUnitKey( CategoryKey categoryKey )
    {

        if ( categoryKey == null )
        {
            return -1;
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category == null || category.getUnitExcludeDeleted() == null )
        {
            return -1;
        }

        return category.getUnitExcludeDeleted().getKey();
    }

    /**
     * @see CategoryHandler#hasSubCategories(CategoryKey)
     */
    public boolean hasSubCategories( CategoryKey categoryKey )
    {
        return hasSubCategories( null, categoryKey, null, false );
    }

    private boolean hasSubCategories( User olduser, CategoryKey categoryKey, int[] contentTypeKeys, boolean adminRead )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        boolean subCategories;

        try
        {
            StringBuffer sql = new StringBuffer( CAT_SELECT_COUNT );
            sql.append( " WHERE" );
            sql.append( CAT_WHERE_CLAUSE_CAT_NOT_DELETED );
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_SCA_ONE );
            if ( contentTypeKeys != null && contentTypeKeys.length > 0 )
            {
                sql.append( " AND cat_cty_lKey IN (" );
                for ( int i = 0; i < contentTypeKeys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        sql.append( ", " );
                    }
                    sql.append( contentTypeKeys[i] );
                }
                sql.append( ")" );
            }
            if ( adminRead )
            {
                getSecurityHandler().appendCategorySQL( olduser, sql, true, false );
            }

            if ( VerticalEngineLogger.isDebugEnabled( this.getClass() ) )
            {
                VerticalEngineLogger.debug( this.getClass(), 0, "SQL: %0", sql, null );
                VerticalEngineLogger.debug( this.getClass(), 0, "categoryKey = %0", categoryKey, null );
                if ( contentTypeKeys != null )
                {
                    StringBuffer sb = new StringBuffer( contentTypeKeys.length + 1 );
                    sb.append( '[' );
                    for ( int i = 0; i < contentTypeKeys.length; i++ )
                    {
                        if ( i > 0 )
                        {
                            sb.append( ',' );
                        }
                        sb.append( contentTypeKeys[i] );
                    }
                    sb.append( ']' );
                    VerticalEngineLogger.debug( this.getClass(), 0, "contentTypeKeys = %0", sb, null );
                }
                else
                {
                    VerticalEngineLogger.debug( this.getClass(), 0, "contentTypeKeys = null", null );
                }
            }

            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, categoryKey.toInt() );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                int count = resultSet.getInt( 1 );
                subCategories = ( count > 0 );
            }
            else
            {
                subCategories = false;
                String message = "Failed to count sub-categories. No result given";
                VerticalEngineLogger.error( this.getClass(), 0, message, null );
            }

            if ( !subCategories )
            {
                int keys[] = getCategoryKeysBySuperCategory( con, categoryKey, false );
                for ( int i = 0; !subCategories && i < keys.length; i++ )
                {
                    subCategories = hasSubCategories( olduser, new CategoryKey( keys[i] ), contentTypeKeys, adminRead );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to count sub-categories: %t";
            VerticalEngineLogger.error( this.getClass(), 1, message, sqle );
            subCategories = false;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }

        return subCategories;
    }

    /**
     * @see CategoryHandler#removeCategory(Connection, CategoryKey)
     */
    public void removeCategory( Connection _con, CategoryKey categoryKey )
        throws VerticalRemoveException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        if ( getContentCount( null, categoryKey, true ) > 0 || hasSubCategories( categoryKey ) )
        {
            throw new VerticalRemoveException(
                "Unable to remove category with key " + categoryKey.toString() + " : Category contains sub-categories and/or content" );
        }

        getCommonHandler().cascadeDelete( db.tCategory, categoryKey.toInt() );

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
            preparedStmt = con.prepareStatement( CAT_DELETE );
            preparedStmt.setInt( 1, categoryKey.toInt() );
            preparedStmt.executeUpdate();
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to remove category: %t";
            VerticalEngineLogger.errorRemove( this.getClass(), 1, message, sqle );
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

    public void updateCategory( Connection _con, User olduser, Document doc )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        Element root = doc.getDocumentElement();
        Map subElems = XMLTool.filterElements( root.getChildNodes() );

        int categoryKey = -1;
        try
        {
            // get the keys
            categoryKey = Integer.parseInt( root.getAttribute( "key" ) );
            int unitKey = -1;
            String keyStr = root.getAttribute( "unitkey" );
            if ( keyStr.length() > 0 )
            {
                unitKey = Integer.parseInt( keyStr );
            }
            int contentTypeKey = -1;
            keyStr = root.getAttribute( "contenttypekey" );
            if ( keyStr.length() > 0 )
            {
                contentTypeKey = Integer.parseInt( keyStr );
            }
            int superCategorykey = -1;
            String key = root.getAttribute( "supercategorykey" );
            if ( key.length() > 0 )
            {
                superCategorykey = Integer.parseInt( key );
            }

            if ( _con == null )
            {
                con = getConnection();
            }
            else
            {
                con = _con;
            }
            preparedStmt = con.prepareStatement( CAT_UPDATE );

            // attribute: key
            preparedStmt.setInt( 10, categoryKey );

            // attribute: unitkey
            if ( unitKey >= 0 )
            {
                preparedStmt.setInt( 1, unitKey );
            }
            else
            {
                preparedStmt.setNull( 1, Types.INTEGER );
            }

            // attribute: contenttypekey
            if ( contentTypeKey >= 0 )
            {
                preparedStmt.setInt( 2, contentTypeKey );
            }
            else
            {
                preparedStmt.setNull( 2, Types.INTEGER );
            }

            // attribute: supercategorykey
            if ( superCategorykey >= 0 )
            {
                preparedStmt.setInt( 3, superCategorykey );
            }
            else
            {
                preparedStmt.setNull( 3, Types.INTEGER );
            }

            // attribute: created
            Date createdDate = parseDate( root.getAttribute( "created" ) );
            preparedStmt.setTimestamp( 5, new Timestamp( createdDate.getTime() ) );

            // attribute: name
            String name = root.getAttribute( "name" );
            preparedStmt.setCharacterStream( 6, new StringReader( name ), name.length() );

            // element: owner
            Element subElem = (Element) subElems.get( "owner" );
            String ownerKey = subElem.getAttribute( "key" );
            preparedStmt.setString( 4, ownerKey );

            // element: description (optional)
            subElem = (Element) subElems.get( "description" );
            if ( subElem != null )
            {
                String description = XMLTool.getElementText( subElem );

                if ( description == null )
                {
                    preparedStmt.setNull( 7, Types.VARCHAR );
                }
                else
                {
                    StringReader sr = new StringReader( description );
                    preparedStmt.setCharacterStream( 7, sr, description.length() );
                }
            }
            else
            {
                preparedStmt.setNull( 7, Types.VARCHAR );
            }

            preparedStmt.setString( 8, olduser.getKey().toString() );

            // element: timestamp (using the database timestamp at creation)
            /* no code */

            preparedStmt.setInt( 9, root.getAttribute( "autoApprove" ).equals( "true" ) ? 1 : 0 );

            // update the content category
            int result = preparedStmt.executeUpdate();
            preparedStmt.close();
            preparedStmt = null;
            if ( result == 0 )
            {
                String message = "Failed to update category.";
                SQLWarning warning = preparedStmt.getWarnings();
                if ( warning != null )
                {
                    message += " (ErrorCode=%0, %1, SQLSTATE=%2)";
                    Object[] msgData = new Object[3];
                    msgData[0] = new Integer( warning.getErrorCode() );
                    msgData[1] = warning.getMessage();
                    msgData[2] = warning.getSQLState();
                    VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, msgData, null );
                }
                else
                {
                    message += " No warning.";
                    VerticalEngineLogger.errorUpdate( this.getClass(), 0, message, null );
                }
            }

            // set category access rights
            SecurityHandler securityHandler = getSecurityHandler();
            Element accessrightsElem = (Element) subElems.get( "accessrights" );
            if ( accessrightsElem != null )
            {
                Document tempDoc = XMLTool.createDocument();
                tempDoc.appendChild( tempDoc.importNode( accessrightsElem, true ) );
                securityHandler.updateAccessRights( olduser, tempDoc );
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to update category: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 1, message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 2, message, nfe );
        }
        catch ( ParseException pe )
        {
            String message = "Failed to parse the created date field: %t";
            VerticalEngineLogger.errorUpdate( this.getClass(), 2, message, pe );
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

    public CategoryKey getParentCategoryKey( CategoryKey categoryKey )
    {
        if ( categoryKey == null )
        {
            return null;
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category != null && category.getParent() != null )
        {
            return category.getParent().getKey();
        }
        return null;
    }

    public boolean hasContent( Connection con, CategoryKey categoryKey )
    {

        return getContentCount( con, categoryKey, true ) > 0;
    }

    public boolean isSubCategory( CategoryKey categoryKey, CategoryKey subCategoryKey )
    {
        CategoryKey parentKey = getParentCategoryKey( subCategoryKey );
        return parentKey != null && ( parentKey.equals( categoryKey ) || isSubCategory( categoryKey, parentKey ) );
    }

    public void moveCategory( User olduser, CategoryKey catKey, CategoryKey superCatKey )
        throws VerticalUpdateException, VerticalSecurityException
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();

            if ( !getSecurityHandler().validateCategoryUpdate( olduser, categoryDao.findByKey( catKey ).getParent().getKey() ) )
            {
                VerticalEngineLogger.errorSecurity( this.getClass(), 10, "User is not allowed to move the category.", null );
            }

            if ( !getSecurityHandler().validateCategoryUpdate( olduser, superCatKey ) )
            {
                VerticalEngineLogger.errorSecurity( this.getClass(), 10, "User is not allowed to update the new parent category.", null );
            }

            int[] children = getCategoryKeysBySuperCategory( con, catKey, true );

            // Get new unit key
            int unitKey = getUnitKey( superCatKey );
            preparedStmt = con.prepareStatement( CAT_SET_UNITKEY_AND_SUPERKEY );
            if ( unitKey == -1 )
            {
                preparedStmt.setNull( 1, Types.INTEGER );
            }
            else
            {
                preparedStmt.setInt( 1, unitKey );
            }
            preparedStmt.setInt( 2, superCatKey.toInt() );
            preparedStmt.setInt( 3, catKey.toInt() );
            preparedStmt.executeUpdate();

            // Update unit keys for all children
            for ( int child : children )
            {
                preparedStmt = con.prepareStatement( CAT_SET_UNITKEY );
                preparedStmt.setInt( 1, unitKey );
                preparedStmt.setInt( 2, child );
                preparedStmt.executeUpdate();
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.errorUpdate( this.getClass(), 10, "A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
            close( con );
        }
    }

    public List<CategoryKey> getSubCategories( CategoryKey categoryKey )
    {
        return categoryDao.findByKey( categoryKey ).getChildrenKeys();
    }

    public boolean hasCategoriesWithRights( Connection _con, User olduser, int unitKey, int contentTypeKey )
    {

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;

        List<Integer> paramValues = new ArrayList<Integer>( 2 );
        StringBuffer sql = new StringBuffer();
        sql.append( "SELECT COUNT(*) FROM " );
        sql.append( CAT_TABLE );
        sql.append( " WHERE" );
        sql.append( CAT_WHERE_CLAUSE_CAT_NOT_DELETED );
        if ( unitKey >= 0 )
        {
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_UNI );
            paramValues.add( new Integer( unitKey ) );
        }
        if ( contentTypeKey >= 0 )
        {
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_CTY );
            paramValues.add( new Integer( contentTypeKey ) );
        }
        String sqlString = sql.toString();
        if ( olduser != null )
        {

            sqlString = getSecurityHandler().appendCategorySQL( olduser, sqlString, true, false );
        }

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

            statement = con.prepareStatement( sqlString );
            int i = 1;
            for ( Iterator<Integer> iter = paramValues.iterator(); iter.hasNext(); i++ )
            {
                Object paramValue = iter.next();
                statement.setObject( i, paramValue );
            }
            resultSet = statement.executeQuery();
            if ( resultSet.next() )
            {
                count = resultSet.getInt( 1 );
            }

        }
        catch ( SQLException sqle )
        {
            String message = "Failed to count categories: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( resultSet );
            close( statement );
            if ( _con == null )
            {
                close( con );
            }
        }

        return ( count > 0 );
    }

    public void getMenu( User olduser, Element root, Criteria criteria, boolean includeSubtrees )
    {
        CategoryCriteria categoryCriteria = (CategoryCriteria) criteria;
        SecurityHandler securityHandler = getSecurityHandler();
        UserHandler userHandler = getUserHandler();

        VerticalEngineLogger.debug( this.getClass(), 0, "Criteria: %0", categoryCriteria, null );

        List<Integer> paramValues = new ArrayList<Integer>();
        StringBuffer sqlCategories = new StringBuffer( "SELECT cat_lKey, cat_uni_lKey, cat_cat_lSuper, cat_sName, cat_cty_lKey FROM " );
        sqlCategories.append( CategoryView.getInstance().getReplacementSql() );
        StringBuffer sqlCategoriesWHERE = new StringBuffer( " WHERE " );

        if ( categoryCriteria.hasUnitKey() )
        {
            sqlCategoriesWHERE.append( CAT_WHERE_CLAUSE_UNI );
            paramValues.add( categoryCriteria.getUnitKeyAsInteger() );
            sqlCategoriesWHERE.append( " AND " );
        }
        // If a category key is specified, get only this (and parents)
        if ( categoryCriteria.getCategoryKey() != -1 )
        {
            sqlCategoriesWHERE.append( " cat_lKey = ?" );
            paramValues.add( categoryCriteria.getCategoryKey() );
            sqlCategoriesWHERE.append( " AND " );
        }
        else if ( categoryCriteria.getCategoryKeys() != null )
        {
            sqlCategoriesWHERE.append( " cat_lKey IN (" );
            List<Integer> categoryKeys = categoryCriteria.getCategoryKeys();
            for ( int i = 0; i < categoryKeys.size(); i++ )
            {
                sqlCategoriesWHERE.append( "?" );
                paramValues.add( categoryKeys.get( i ) );
                if ( i < categoryKeys.size() - 1 )
                {
                    sqlCategoriesWHERE.append( ", " );
                }
            }
            sqlCategoriesWHERE.append( ") AND " );
        }

        TIntArrayList contentTypes = new TIntArrayList();
        if ( categoryCriteria.getContentTypes() != null )
        {
            List contentTypesList = categoryCriteria.getContentTypes();
            StringBuffer str_contentTypes = new StringBuffer();
            for ( int i = 0; i < contentTypesList.size(); i++ )
            {
                if ( str_contentTypes.length() > 0 )
                {
                    str_contentTypes.append( " OR " );
                }
                Integer curContentType = (Integer) contentTypesList.get( i );
                contentTypes.add( curContentType );
                str_contentTypes.append( CAT_WHERE_CLAUSE_CTY );
                paramValues.add( curContentType );
            }
            sqlCategoriesWHERE.append( " ( " );
            sqlCategoriesWHERE.append( str_contentTypes );
            sqlCategoriesWHERE.append( " ) " );
            sqlCategoriesWHERE.append( " AND " );
        }

        if ( sqlCategoriesWHERE.length() == 7 )
        {
            sqlCategoriesWHERE.append( " 1 = 1" );
        }
        else
        {
            sqlCategoriesWHERE.setLength( sqlCategoriesWHERE.length() - 5 );
        }

        if ( olduser != null )
        {
            getSecurityHandler().appendCategorySQL( olduser, sqlCategoriesWHERE, true, false );
        }

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        Document doc = root.getOwnerDocument();
        if ( categoryCriteria.useRootElement() )
        {
            root = XMLTool.createElement( doc, root, "categories" );
        }
        if ( categoryCriteria.useDisableAttribute() )
        {
            root.setAttribute( "disable", "true" );
        }

        try
        {
            con = getConnection();

            /*if ((categoryCriteria.getCategoryKey() != -1)
                   || (categoryCriteria.getCategoryKeys() != null)) {*/
            String sql = sqlCategories.toString() + sqlCategoriesWHERE.toString();
            VerticalEngineLogger.debug( this.getClass(), 0, "SQL (1): %0", sql, null );
            preparedStmt = con.prepareStatement( sql );

            int i = 1;
            for ( Iterator<Integer> iter = paramValues.iterator(); iter.hasNext(); i++ )
            {
                Object paramValue = iter.next();
                preparedStmt.setObject( i, paramValue );
            }

            HashMap<Integer, Integer> processedKeys = new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> missingKeys = new HashMap<Integer, Integer>();

            resultSet = preparedStmt.executeQuery();
            CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
            while ( resultSet.next() )
            {
                String categoryName = resultSet.getString( "cat_sName" );
                Element catElem = XMLTool.createElement( doc, root, "category", null, "name", categoryName );
                catElem.setAttribute( "name", categoryName );

                // unit key
                int unitKey = resultSet.getInt( "cat_uni_lKey" );
                if ( !resultSet.wasNull() )
                {
                    catElem.setAttribute( "unitkey", Integer.toString( unitKey ) );
                }

                // category key
                CategoryKey categoryKey = new CategoryKey( resultSet.getInt( "cat_lKey" ) );
                Integer catKey = categoryKey.toInt();
                catElem.setAttribute( "key", catKey.toString() );

                // supercategory key
                int superCategoryKey = resultSet.getInt( "cat_cat_lSuper" );
                Integer superCatKey = null;
                if ( !resultSet.wasNull() )
                {
                    superCatKey = superCategoryKey;
                    catElem.setAttribute( "superkey", superCatKey.toString() );
                }
                else if ( categoryCriteria.hasUnitKey() && unitKey == categoryCriteria.getUnitKey() )
                {
                    // If this is the current unit category, it should be open
                    catElem.setAttribute( "expanded", "true" );
                }

                int ctyKey = resultSet.getInt( "cat_cty_lKey" );
                if ( !resultSet.wasNull() )
                {
                    catElem.setAttribute( "contenttypekey", Integer.toString( ctyKey ) );

                    // Set handler
                    String handlerClass = getContentHandler().getContentHandlerClassForContentType( ctyKey );
                    catElem.setAttribute( "handler", handlerClass );
                }

                if ( categoryCriteria.useDisableAttribute() &&
                    ( contentTypes.size() > 0 && !contentTypes.contains( ctyKey ) || resultSet.wasNull() ) )
                {
                    catElem.setAttribute( "disable", "true" );
                }

                // Does anonymous have access?
                User anonUser = userHandler.getAnonymousUser();
                final CategoryEntity category = categoryDao.findByKey( categoryKey );
                CategoryAccessRightsAccumulated categoryAccessRightsAccumulated =
                    categoryAccessResolver.getAccumulatedAccessRights( securityService.getUser( anonUser ), category );
                final boolean anonReadAcccess = categoryAccessRightsAccumulated.isRead();
                catElem.setAttribute( "anonaccess", Boolean.valueOf( anonReadAcccess ).toString() );

                if ( superCatKey != null && !processedKeys.containsKey( superCatKey ) )
                {
                    missingKeys.put( superCatKey, superCatKey );
                }

                if ( missingKeys.containsKey( catKey ) )
                {
                    missingKeys.remove( catKey );
                }

                processedKeys.put( catKey, catKey );
            }

            // Find all missing parent categories
            while ( missingKeys.size() > 0 )
            {
                StringBuffer sqlCategoriesWHEREPK = new StringBuffer( " WHERE cat_lKey IN (" );

                Iterator<Integer> iter = missingKeys.values().iterator();
                while ( iter.hasNext() )
                {
                    Integer missingKey = iter.next();
                    sqlCategoriesWHEREPK.append( missingKey );
                    if ( iter.hasNext() )
                    {
                        sqlCategoriesWHEREPK.append( ", " );
                    }
                    else
                    {
                        sqlCategoriesWHEREPK.append( ")" );
                    }
                    missingKeys.remove( missingKey );
                    iter = missingKeys.values().iterator();
                }
                sql = sqlCategories.toString() + sqlCategoriesWHEREPK.toString();
                VerticalEngineLogger.debug( this.getClass(), 0, "SQL (2): %0", sql, null );
                preparedStmt = con.prepareStatement( sql );

                resultSet = preparedStmt.executeQuery();
                while ( resultSet.next() )
                {
                    String categoryName = resultSet.getString( "cat_sName" );
                    Element catElem = XMLTool.createElement( doc, root, "category", null, "name", categoryName );

                    // category key
                    CategoryKey categoryKey = new CategoryKey( resultSet.getInt( "cat_lKey" ) );
                    Integer catKey = categoryKey.toInt();
                    catElem.setAttribute( "key", catKey.toString() );

                    // unit key
                    int unitKey = resultSet.getInt( "cat_uni_lKey" );
                    if ( !resultSet.wasNull() )
                    {
                        catElem.setAttribute( "unitkey", Integer.toString( unitKey ) );
                    }

                    // supercategory key
                    int superCategoryKey = resultSet.getInt( "cat_cat_lSuper" );
                    Integer superCatKey = null;
                    new Integer( superCategoryKey );
                    if ( !resultSet.wasNull() )
                    {
                        superCatKey = superCategoryKey;
                        catElem.setAttribute( "superkey", superCatKey.toString() );
                    }
                    else if ( categoryCriteria.hasUnitKey() && unitKey == categoryCriteria.getUnitKey() )
                    {
                        // If this is the current unit category, it should be open
                        catElem.setAttribute( "expanded", "true" );
                    }

                    // category name
                    catElem.setAttribute( "name", categoryName );

                    int ctyKey = resultSet.getInt( "cat_cty_lKey" );
                    if ( !resultSet.wasNull() )
                    {
                        catElem.setAttribute( "contenttypekey", Integer.toString( ctyKey ) );

                        // Set handler
                        String handlerClass = getContentHandler().getContentHandlerClassForContentType( ctyKey );
                        catElem.setAttribute( "handler", handlerClass );
                    }

                    // Does anonymous have access?
                    User anonUser = userHandler.getAnonymousUser();
                    final CategoryEntity category = categoryDao.findByKey( categoryKey );
                    CategoryAccessRightsAccumulated categoryAccessRightsAccumulated =
                        categoryAccessResolver.getAccumulatedAccessRights( securityService.getUser( anonUser ), category );
                    final boolean anonReadAcccess = categoryAccessRightsAccumulated.isRead();
                    catElem.setAttribute( "anonaccess", Boolean.valueOf( anonReadAcccess ).toString() );

                    // disable attribute
                    int cty = resultSet.getInt( "cat_cty_lKey" );
                    // Set disabled if it does not have the correct content type

                    if ( !resultSet.wasNull() )
                    {
                        // Set handler
                        String handlerClass = getContentHandler().getContentHandlerClassForContentType( ctyKey );
                        catElem.setAttribute( "handler", handlerClass );
                    }

                    if ( categoryCriteria.useDisableAttribute() &&
                        ( contentTypes.size() > 0 && !contentTypes.contains( cty ) || resultSet.wasNull() ) )
                    {
                        catElem.setAttribute( "disable", "true" );
                    }

                    if ( superCatKey != null && !processedKeys.containsKey( superCatKey ) )
                    {
                        missingKeys.put( superCatKey, superCatKey );
                    }

                    if ( missingKeys.containsKey( catKey ) )
                    {
                        missingKeys.remove( catKey );
                    }
                }
            }

            XMLTool.makeTree( XMLTool.getElements( root, "category" ) );
            //}

            if ( categoryCriteria.includeAllUnits() )
            {
                // Find all other units
                if ( categoryCriteria.hasUnitKey() )
                {
                    sqlCategoriesWHERE = new StringBuffer( " WHERE cat_uni_lKey != " + categoryCriteria.getUnitKey() + " AND " );
                }
                else
                {
                    sqlCategoriesWHERE = new StringBuffer( " WHERE " );
                }
                sqlCategoriesWHERE.append( "cat_cat_lSuper IS NULL" );
                Element[] categoryElems = XMLTool.getElements( root );
                if ( categoryElems.length > 0 )
                {
                    sqlCategoriesWHERE.append( " AND cat_lKey NOT IN (" );
                    for ( int j = 0; j < categoryElems.length; j++ )
                    {
                        if ( j > 0 )
                        {
                            sqlCategoriesWHERE.append( ',' );
                        }
                        sqlCategoriesWHERE.append( categoryElems[j].getAttribute( "key" ) );
                    }
                    sqlCategoriesWHERE.append( ')' );
                }

                /*String*/
                sql = sqlCategories.toString() + sqlCategoriesWHERE.toString();
                VerticalEngineLogger.debug( this.getClass(), 0, "SQL (3): %0", sql, null );
                preparedStmt = con.prepareStatement( sql );
                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    CategoryKey categoryKey = new CategoryKey( resultSet.getInt( "cat_lKey" ) );
                    int thisUnitKey = resultSet.getInt( "cat_uni_lKey" );
                    String categoryName = resultSet.getString( "cat_sName" );

                    boolean hasChildren = hasSubCategories( olduser, categoryKey, contentTypes.toArray(), true );
                    if ( hasChildren )
                    {
                        Element catElem = XMLTool.createElement( doc, root, "category", null, "name", categoryName );
                        catElem.setAttribute( "key", categoryKey.toString() );
                        catElem.setAttribute( "unitkey", Integer.toString( thisUnitKey ) );
                        catElem.setAttribute( "name", categoryName );

                        int ctyKey = resultSet.getInt( "cat_cty_lKey" );
                        // Set disabled if it does not have the correct content type
                        if ( categoryCriteria.useDisableAttribute() &&
                            ( contentTypes.size() > 0 && !contentTypes.contains( ctyKey ) || resultSet.wasNull() ) )
                        {
                            catElem.setAttribute( "disable", "true" );
                        }

                        if ( !resultSet.wasNull() )
                        {
                            catElem.setAttribute( "contenttypekey", String.valueOf( ctyKey ) );
                            // Set handler
                            String handlerClass = getContentHandler().getContentHandlerClassForContentType( ctyKey );
                            catElem.setAttribute( "handler", handlerClass );
                        }

                        // Does anonymous have access?
                        User anonUser = userHandler.getAnonymousUser();
                        CategoryAccessRight catAR = securityHandler.getCategoryAccessRight( anonUser, categoryKey );
                        catElem.setAttribute( "anonaccess", Boolean.valueOf( catAR.getRead() ).toString() );

                        catElem.setAttribute( "haschildren", Boolean.valueOf( hasChildren ).toString() );
                    }
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get the categories: %t";
            VerticalEngineLogger.error( this.getClass(), 0, message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public StringBuffer getPathString( CategoryKey categoryKey )
    {
        CommonHandler commonHandler = getCommonHandler();
        return commonHandler.getPathString( db.tCategory, db.tCategory.cat_lKey, db.tCategory.cat_cat_lSuper, db.tCategory.cat_sName,
                                            categoryKey.toInt(), null, true );
    }

    public void getPathXML( Document doc, Element child, CategoryKey categoryKey, int[] contentTypes )
    {
        CategoryKey superCategoryKey = null;
        Element elem = null;

        if ( categoryKey != null )
        {
            CategoryEntity entity = categoryDao.findByKey( categoryKey );
            if ( entity != null )
            {
                elem = XMLTool.createElement( doc, doc.getDocumentElement(), "category" );
                elem.setAttribute( "key", String.valueOf( categoryKey ) );
                elem.setAttribute( "name", entity.getName() );

                if ( child != null )
                {
                    elem.appendChild( child );
                }

                Integer contentTypeKey = null;
                if ( entity.getContentType() != null )
                {
                    contentTypeKey = entity.getContentType().getKey();
                    elem.setAttribute( "contenttypekey", String.valueOf( contentTypeKey ) );
                }

                if ( contentTypeKey == null || ( contentTypes != null && !ArrayUtil.contains( contentTypes, contentTypeKey ) ) )
                {
                    elem.setAttribute( "disable", "true" );
                }

                if ( entity.getParent() != null )
                {
                    superCategoryKey = entity.getParent().getKey();
                }
            }
        }

        if ( superCategoryKey != null )
        {
            getPathXML( doc, elem, superCategoryKey, contentTypes );
        }
        else
        {
            Element topCat = XMLTool.createElement( doc, doc.getDocumentElement(), "categories" );
            topCat.setAttribute( "disable", "true" );

            if ( elem != null )
            {
                topCat.appendChild( elem );
            }

            // Get path xml for the site
            //getSiteHandler().getPathXML(doc, topCat, siteKey);
        }
    }

    /**
     * Return the categories. - from DataSourceServiceImpl (through PresentationEngine)
     */
    public Document getCategories( User olduser, CategoryKey categoryKey, int levels, boolean topLevel, boolean details, boolean catCount,
                                   boolean contentCount )
    {
        Document doc = XMLTool.createDocument();

        try
        {
            RelationNode node = getCategoryTree( olduser, categoryKey, contentCount );
            if ( node != null )
            {
                fetchCategories( doc, node, levels, topLevel, details, catCount, contentCount );
            }
            else
            {
                doc = XMLTool.createDocument( "categories" );
                if ( contentCount )
                {
                    doc.getDocumentElement().setAttribute( "count", "0" );
                }
            }
        }
        catch ( SQLException e )
        {
            VerticalEngineLogger.error( this.getClass(), 0, "Failed to fetch relation keys: %t", e );
        }

        return doc;
    }

    /**
     * Return the categories.
     */
    private void fetchCategories( Document doc, RelationNode node, int levels, boolean topLevel, boolean details, boolean catCount,
                                  boolean contentCount )
        throws SQLException
    {
        node.selectLevels( levels == 0 ? Integer.MAX_VALUE : levels );
        Map<Number, Element> entryMap = fetchCategoryElements( doc, node.findSelectedKeys(), details );
        Element root = buildCategoriesElement( doc, entryMap, node, topLevel, catCount, contentCount );
        doc.appendChild( root );
    }

    /**
     * Bild the categories.
     */
    private Element buildCategoriesElement( Document doc, Map<Number, Element> entryMap, RelationNode node, boolean topLevel,
                                            boolean catCount, boolean contentCount )
    {
        Element elem = doc.createElement( "categories" );

        if ( catCount )
        {
            int count = node != null ? node.getChildCount() : 0;
            int totalCount = node != null ? node.getTotalChildCount() : 0;

            if ( topLevel )
            {
                count = 1;
                totalCount += 1;
            }

            elem.setAttribute( "count", String.valueOf( count ) );
            elem.setAttribute( "totalcount", String.valueOf( totalCount ) );
        }

        if ( topLevel )
        {
            Element tmp = buildCategoryElement( doc, entryMap, node, catCount, contentCount );
            if ( tmp != null )
            {
                elem.appendChild( tmp );
            }
        }
        else
        {
            List entries = node.getChildren();
            for ( Iterator i = entries.iterator(); i.hasNext(); )
            {
                Element tmp = buildCategoryElement( doc, entryMap, (RelationNode) i.next(), catCount, contentCount );

                if ( tmp != null )
                {
                    elem.appendChild( tmp );
                }
            }
        }

//	    if ((node != null) && !node.hasChildren() && !topLevel) {
//	        return null;
//	    }
//	    else {
        return elem;
//	    }
    }

    /**
     * Bild the categories.
     */
    private Element buildCategoryElement( Document doc, Map<Number, Element> entryMap, RelationNode node, boolean catCount,
                                          boolean contentCount )
    {
        if ( node == null )
        {
            return null;
        }

        if ( !node.isSelected() )
        {
            return null;
        }

        Element elem = entryMap.get( node.getKey() );
        if ( elem == null )
        {
            return null;
        }

        if ( contentCount )
        {
            int[] count = findContentCount( node );
            elem.setAttribute( "contentcount", String.valueOf( count[0] ) );
            elem.setAttribute( "totalcontentcount", String.valueOf( count[1] ) );
        }

        Element tmp = buildCategoriesElement( doc, entryMap, node, false, catCount, contentCount );
        if ( tmp != null )
        {
            elem.appendChild( tmp );
        }

        return elem;
    }

    /**
     * Return the content count of node.
     */
    private int[] findContentCount( RelationNode node )
    {
        int count = 0;
        int totalCount = 0;

        Object data = node.getData();
        if ( data instanceof Number )
        {
            count = ( (Number) data ).intValue();
        }

        data = node.accept( new RelationAggregator() );
        if ( data instanceof Number )
        {
            totalCount = ( (Number) data ).intValue();
        }

        return new int[]{count, totalCount};
    }

    /**
     * Fetch the categories. This will return a map of id to element.
     */
    private Map<Number, Element> fetchCategoryElements( Document doc, Set keys, boolean details )
        throws SQLException
    {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        Map<Number, Element> map = new HashMap<Number, Element>();

        if ( keys.isEmpty() )
        {
            return map;
        }

        try
        {
            conn = getConnection();
            String sql = getSelectCategoryByKeysSQL( keys );
            stmt = conn.prepareStatement( sql );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Number key = (Number) result.getObject( 1 );
                Number ctyKey = (Number) result.getObject( 2 );
                Number superKey = (Number) result.getObject( 3 );
                String ownerKey = (String) result.getObject( 4 );
                String ownerUID = result.getString( 5 );
                String ownerName = result.getString( 6 );
                String modifierKey = (String) result.getObject( 7 );
                String modifierUID = result.getString( 8 );
                String modifierName = result.getString( 9 );
                Timestamp created = result.getTimestamp( 10 );
                Timestamp timestamp = result.getTimestamp( 11 );
                String name = result.getString( 12 );
                String description = result.getString( 13 );

                Element root = doc.createElement( "category" );
                root.setAttribute( "key", key.toString() );

                if ( superKey != null )
                {
                    root.setAttribute( "superkey", superKey.toString() );
                }

                if ( details )
                {
                    if ( ctyKey != null )
                    {
                        root.setAttribute( "contenttypekey", ctyKey.toString() );
                    }

                    root.setAttribute( "created", CalendarUtil.formatTimestamp( created, false ) );
                    root.setAttribute( "timestamp", CalendarUtil.formatTimestamp( timestamp, false ) );

                    Element owner = XMLTool.createElement( doc, root, "owner", ownerName );
                    owner.setAttribute( "key", ownerKey );
                    owner.setAttribute( "uid", ownerUID );

                    Element modifier = XMLTool.createElement( doc, root, "modifier", modifierName );
                    modifier.setAttribute( "key", modifierKey );
                    modifier.setAttribute( "uid", modifierUID );

                    if ( description != null )
                    {
                        Element descr = XMLTool.createElement( doc, root, "description" );
                        XMLTool.createCDATASection( doc, descr, description );
                    }
                }

                XMLTool.createElement( doc, root, "title", name );
                map.put( key, root );
            }
        }
        finally
        {
            close( result );
            close( stmt );
            close( conn );
        }

        return map;
    }

    /**
     * Return the category tree.
     */
    private RelationNode getCategoryTree( User olduser, CategoryKey categoryKey, boolean contentCount )
        throws SQLException
    {
        GroupHandler handler = getGroupHandler();
        SecurityHandler secHandler = getSecurityHandler();

        String[] groups = null;
        if ( !secHandler.isEnterpriseAdmin( olduser ) )
        {
            groups = handler.getAllGroupMembershipsForUser( olduser );
        }

        Connection conn = getConnection();

        try
        {
            return getCategoryTree( olduser, categoryKey, groups, contentCount );
        }
        finally
        {
            close( conn );
        }
    }

    /**
     * Return the category tree.
     */
    private RelationNode getCategoryTree( User olduser, CategoryKey categoryKey, String[] groups, boolean contentCount )
        throws SQLException
    {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet result = null;
        RelationTree tree = new RelationTree();
        RelationNode node = null;

        int oldstyleCategoryKey = categoryKey != null ? categoryKey.toInt() : -1;
        try
        {
            con = getConnection();
            int unitKey = getUnitKey( categoryKey );
            String sql = getSelectCategoriesSQL( olduser );
            stmt = con.prepareStatement( sql );
            stmt.setInt( 1, unitKey );
            result = stmt.executeQuery();

            while ( result.next() )
            {
                Number child = (Number) result.getObject( 1 );
                Number parent = (Number) result.getObject( 2 );

                if ( parent == null )
                {
                    tree.addChild( child );
                }
                else
                {
                    tree.addChild( parent, child );
                }
            }
            tree.setRoot( oldstyleCategoryKey );
            node = tree.getNode( oldstyleCategoryKey );

            close( result );
            result = null;
            close( stmt );
            stmt = null;

            boolean anyCategoriesFound = node != null;

            if ( contentCount && anyCategoriesFound )
            {
                sql = getSelectContentCountSQL( categoryKey, groups );
                stmt = con.prepareStatement( sql );
                result = stmt.executeQuery();

                while ( result.next() )
                {
                    Integer entry = new Integer( result.getInt( 1 ) );
                    Integer count = new Integer( result.getInt( 2 ) );

                    RelationNode current = node.getNode( entry );
                    if ( current != null )
                    {
                        current.setData( count );
                    }
                }
            }
        }
        finally
        {
            close( result );
            close( stmt );
            close( con );
        }

        return node;
    }

    /**
     * Return the select unit keys by category sql.
     */
    private String getSelectCategoriesSQL( User olduser )
    {
        SecurityHandler securityHandler = getSecurityHandler();
        CategoryView view = CategoryView.getInstance();
        Column[] selectColumns = {view.cat_lKey, view.cat_cat_lSuper};
        Column[] whereColumns = {view.cat_uni_lKey};
        StringBuffer sql = XDG.generateSelectSQL( view, selectColumns, false, whereColumns );

        securityHandler.appendCategorySQL( olduser, sql, false, false );
        return sql.toString();
    }

    /**
     * Return the groups sql.
     */
    private String getKeySetSQL( String[] keys )
    {
        if ( keys != null )
        {
            StringBuffer sql = new StringBuffer();

            if ( keys.length == 0 )
            {
                sql.append( "IS NULL" );
            }
            else if ( keys.length == 1 )
            {
                sql.append( "= '" ).append( String.valueOf( keys[0] ) ).append( "'" );
            }
            else
            {
                sql.append( "IN (" );
                for ( int i = 0; i < keys.length; i++ )
                {
                    if ( i > 0 )
                    {
                        sql.append( ", " );
                    }
                    sql.append( "'" );
                    sql.append( keys[i] );
                    sql.append( "'" );
                }
                sql.append( ")" );
            }

            return sql.toString();
        }
        else
        {
            return null;
        }
    }

    /**
     * Return the content count sql.
     */
    private String getSelectContentCountSQL( CategoryKey key, String[] groups )
    {
        ContentPublishedView view = ContentPublishedView.getInstance();
        ConAccessRight2Table accessTable = this.db.tConAccessRight2;

        StringBuffer sql = new StringBuffer();
        sql.append( "SELECT " ).append( view.cat_lKey.getName() );
        sql.append( ", COUNT(" ).append( view.con_lKey.getName() ).append( ")" );
        sql.append( " FROM " ).append( view.getReplacementSql() );

        String groupSql = getKeySetSQL( groups );
        if ( groupSql != null )
        {
            sql.append( " LEFT JOIN " ).append( accessTable.getName() );
            sql.append( " ON " ).append( accessTable.coa_con_lKey.getName() );
            sql.append( " = " ).append( view.con_lKey );
        }

        sql.append( " WHERE " ).append( view.cat_uni_lKey.getName() );
        sql.append( " IN (" ).append( getSelectUnitByCategorySQL( key ) ).append( ")" );

        if ( groupSql != null )
        {
            sql.append( " AND " ).append( accessTable.coa_grp_hKey.getName() );
            sql.append( " " ).append( groupSql );
            sql.append( " AND " ).append( accessTable.coa_bRead.getName() ).append( " = 1" );
        }

        sql.append( " GROUP BY " ).append( view.cat_lKey.getName() );
        return sql.toString();
    }

    /**
     * Return the select unit keys by category sql.
     */
    private String getSelectUnitByCategorySQL( CategoryKey key )
    {
        CategoryTable table = this.db.tCategory;
        StringBuffer sql = new StringBuffer();
        sql.append( "SELECT " ).append( table.cat_uni_lKey.getName() );
        sql.append( " FROM " ).append( table.getName() );
        sql.append( " WHERE " ).append( table.cat_lKey.getName() );
        sql.append( " = " ).append( key.toInt() );
        return sql.toString();
    }

    /**
     * Return the select category by keys sql.
     */
    private String getSelectCategoryByKeysSQL( Set keys )
    {
        CategoryView table = CategoryView.getInstance();

        StringBuffer sql = new StringBuffer();
        sql.append( "SELECT " ).append( table.cat_lKey.getName() );
        sql.append( ", " ).append( table.cat_cty_lKey.getName() );
        sql.append( ", " ).append( table.cat_cat_lSuper.getName() );
        sql.append( ", " ).append( table.usr_hOwner.getName() );
        sql.append( ", " ).append( table.usr_sOwnerUID.getName() );
        sql.append( ", " ).append( table.usr_sOwnerName.getName() );
        sql.append( ", " ).append( table.usr_hModifier.getName() );
        sql.append( ", " ).append( table.usr_sModifierUID.getName() );
        sql.append( ", " ).append( table.usr_sModifierName.getName() );
        sql.append( ", " ).append( table.cat_dteCreated.getName() );
        sql.append( ", " ).append( table.cat_dteTimestamp.getName() );
        sql.append( ", " ).append( table.cat_sName.getName() );
        sql.append( ", " ).append( table.cat_sDescription.getName() );
        sql.append( " FROM " ).append( table.getReplacementSql() );
        sql.append( " WHERE " ).append( table.cat_lKey.getName() );
        sql.append( " IN (" );

        for ( Iterator i = keys.iterator(); i.hasNext(); )
        {
            sql.append( i.next() );

            if ( i.hasNext() )
            {
                sql.append( ", " );
            }
        }

        sql.append( ")" );
        return sql.toString();
    }

    /**
     * Returns the cagtegories for the administration interface.
     */
    public Document getCategoryMenu( User olduser, CategoryKey categoryKey, int[] contentTypes, boolean includeRootCategories )
    {
        final CategoryXmlCreator xmlCreator = new CategoryXmlCreator();
        final UserEntity user = securityService.getUser( olduser );

        if ( user == null )
        {
            return xmlCreator.createEmptyCategoriesDocument( "No user given" ).getAsDOMDocument();
        }

        xmlCreator.setCategoryAccessResolver( new CategoryAccessResolver( groupDao ) );
        xmlCreator.setUser( user );
        xmlCreator.setAnonymousUser( securityService.getUser( securityService.getAnonymousUserKey() ) );
        xmlCreator.setAllowedContentTypes( ContentTypeKey.convertToSet( contentTypes ) );
        xmlCreator.setIncludeOwnerAndModiferInfo( false );
        xmlCreator.setIncludeCreatedAndTimestampInfo( false );
        xmlCreator.setIncludeAutoApproveInfo( false );
        xmlCreator.setIncludeDescriptionInfo( false );
        xmlCreator.setIncludeSuperCategoryKeyInfo( false );

        if ( includeRootCategories )
        {
            final List<CategoryEntity> rootCategories = getRootCategories( user );
            if ( rootCategories.isEmpty() )
            {
                return xmlCreator.createEmptyCategoriesDocument( "No result" ).getAsDOMDocument();
            }
            final CategoryKey topCategoryKey = getTopCategoryKey( categoryKey );
            final XMLDocument newCategoryTree = xmlCreator.createCategoryBranch( rootCategories, topCategoryKey );
            return newCategoryTree.getAsDOMDocument();
        }
        else
        {
            final CategoryEntity category = categoryDao.findByKey( categoryKey );
            final XMLDocument newCategoryTree = xmlCreator.createCategoryBranch( category );
            return newCategoryTree.getAsDOMDocument();
        }
    }

    private List<CategoryEntity> getRootCategories( UserEntity user )
    {
        List<CategoryEntity> rootCategories;
        if ( user.isEnterpriseAdmin() )
        {
            rootCategories = categoryDao.findRootCategories();
        }
        else
        {
            rootCategories = categoryDao.findRootCategories( user.getAllMembershipsGroupKeys() );
        }
        return rootCategories;
    }

    private CategoryKey getTopCategoryKey( CategoryKey categoryKey )
    {
        if ( categoryKey == null )
        {
            return null;
        }

        CategoryEntity category = categoryDao.findByKey( categoryKey );
        if ( category == null )
        {
            return null;
        }

        while ( category.getParent() != null )
        {
            category = category.getParent();
        }
        return category.getKey();
    }

    public long getArchiveSizeByUnit( int unitKey )
    {

        return categoryStatisticsHelper.getArchiveSizeByUnit( unitKey );
    }

    public long getArchiveSizeByCategory( CategoryKey categoryKey )
    {

        return categoryStatisticsHelper.getArchiveSizeByCategory( categoryKey );
    }

    public Map<Integer, CategoryStatistics> getCategoryStatistics( int unitKey )
    {

        StringBuffer sql = new StringBuffer();
        sql.append( "select" );
        sql.append( " cat_lkey," );
        sql.append( " cat_cat_lsuper" );
        sql.append( " from tcategory" );
        sql.append( " where cat_uni_lkey = ?" );
        sql.append( " order by cat_cat_lsuper asc, cat_lkey asc" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        Map<Integer, CategoryStatistics> categoryStatistics = new HashMap<Integer, CategoryStatistics>();

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, unitKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {

                CategoryStatistics cs = new CategoryStatistics( resultSet.getInt( 1 ) );
                cs.setParentCategoryKey( resultSet.getInt( 2 ) );
                categoryStatistics.put( cs.getCategoryKey(), cs );
            }
        }
        catch ( SQLException sqle )
        {
            throw new RuntimeException( "Failed to get category tree", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
        return categoryStatistics;
    }

    public void collectStatisticsFromContent( int unitKey, Map<Integer, CategoryStatistics> catStats )
    {

        StringBuffer sql = new StringBuffer();
        sql.append( "select" );
        sql.append( " cat_lkey," );
        sql.append( " cat_cat_lsuper," );
        sql.append( " sum(@length@(cov_stitle))," );
        sql.append( " sum(@length@(cov_sdescription))," );
        sql.append( " sum(@length@(cov_xmlcontentdata))" );
        sql.append( " from tcontentversion" );
        sql.append( " left join tcontent on cov_con_lkey = con_lkey" );
        sql.append( " left join tcategory on con_cat_lkey = cat_lkey" );
        sql.append( " where cat_uni_lkey = ?" );
        sql.append( " group by cat_lkey, cat_cat_lsuper" );
        sql.append( " order by cat_cat_lsuper asc, cat_lkey asc" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        // 4 bytes times 6 for the number columns
        int constantSize = 4 * 6;
        // 20 bytes times 2 for the user columns
        constantSize += 20 * 2;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, unitKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {

                Integer categoryKey = resultSet.getInt( 1 );
                CategoryStatistics cs = catStats.get( categoryKey );
                if ( cs != null )
                {
                    long amount = constantSize + resultSet.getLong( 3 ) + resultSet.getLong( 4 ) + resultSet.getLong( 5 );
                    cs.addAmount( amount );
                }
            }
        }
        catch ( SQLException sqle )
        {
            throw new RuntimeException( "Failed to get category statistics", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

    public void collectStatisticsFromBinaryData( int unitKey, Map<Integer, CategoryStatistics> catStats )
    {

        StringBuffer sql = new StringBuffer();
        sql.append( "select" );
        sql.append( " cat_lkey," );
        sql.append( " cat_cat_lsuper," );
        sql.append( " sum(@length@(bda_sfilename))," );
        sql.append( " sum(bda_lfilesize)" );
        sql.append( " from tbinarydata" );
        sql.append( " left join tcontentbinarydata on bda_lkey = cbd_bda_lkey" );
        sql.append( " left join tcontentversion on cbd_cov_lkey = cov_lkey" );
        sql.append( " left join tcontent on cov_con_lkey = con_lkey" );
        sql.append( " left join tcategory on con_cat_lkey = cat_lkey" );
        sql.append( " where cat_uni_lkey = ?" );
        sql.append( " group by cat_lkey, cat_cat_lsuper" );
        sql.append( " order by cat_cat_lsuper asc, cat_lkey asc" );

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        // 4 bytes times 6 for the number columns
        int constantSize = 4 * 8;
        // 20 bytes times 2 for the user columns
        constantSize += 20 * 2;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql.toString() );
            preparedStmt.setInt( 1, unitKey );
            resultSet = preparedStmt.executeQuery();

            while ( resultSet.next() )
            {

                Integer categoryKey = new Integer( resultSet.getInt( 1 ) );
                CategoryStatistics cs = catStats.get( categoryKey );
                if ( cs != null )
                {
                    long filenameSize = resultSet.getLong( 3 ) * 2; // two times because it is in content version too
                    long amount = constantSize + filenameSize + resultSet.getLong( 4 );
                    cs.addAmount( amount );
                }
            }
        }
        catch ( SQLException sqle )
        {
            throw new RuntimeException( "Failed to get category statistics", sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
            close( con );
        }
    }

}
