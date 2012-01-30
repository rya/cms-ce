/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.CmsDateAndTimeFormats;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.sql.model.Column;
import com.enonic.esl.util.ArrayUtil;
import com.enonic.esl.util.RelationAggregator;
import com.enonic.esl.util.RelationNode;
import com.enonic.esl.util.RelationTree;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.engine.AccessRight;
import com.enonic.vertical.engine.VerticalEngineLogger;
import com.enonic.vertical.engine.VerticalSecurityException;
import com.enonic.vertical.engine.XDG;
import com.enonic.vertical.engine.criteria.CategoryCriteria;
import com.enonic.vertical.engine.dbmodel.CategoryTable;
import com.enonic.vertical.engine.dbmodel.CategoryView;
import com.enonic.vertical.engine.dbmodel.ConAccessRight2Table;
import com.enonic.vertical.engine.dbmodel.ContentPublishedView;

import com.enonic.cms.framework.hibernate.support.InClauseBuilder;
import com.enonic.cms.framework.util.TIntArrayList;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.CalendarUtil;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.category.CategoryAccessRightsAccumulated;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.core.content.category.CategoryXmlCreator;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.store.dao.CategoryDao;

import com.enonic.cms.core.content.category.CategoryStatistics;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.security.user.UserEntity;

public class CategoryHandler
    extends BaseHandler
{
    private final static String CAT_TABLE = "tCategory";

    private final static String CAT_INSERT =
        "INSERT INTO " + CAT_TABLE + " (cat_lKey,cat_uni_lKey,cat_cty_lKey,cat_cat_lSuper,cat_usr_hOwner,cat_dteCreated,cat_bDeleted," +
            "cat_sName,cat_sDescription,cat_usr_hModifier,cat_dteTimestamp,cat_bautoapprove)" + " VALUES (?,?,?,?,?,@currentTimestamp@" +
            ",0,?,?,?," + "@currentTimestamp@,?" + ")";

    private final static String CAT_UPDATE =
        "UPDATE " + CAT_TABLE + " SET cat_uni_lKey = ?" + ",cat_cty_lKey = ?" + ",cat_cat_lSuper = ?" + ",cat_usr_hOwner = ?" +
            ",cat_dteCreated = ?" + ",cat_sName = ?" + ",cat_sDescription = ?" + ",cat_usr_hModifier = ?" + ",cat_dteTimestamp = " +
            "@currentTimestamp@" + ",cat_bAutoApprove = ?" + " WHERE cat_lKey = ?";

    private final static String CAT_SELECT_COUNT = "SELECT count(cat_lKey) FROM " + CAT_TABLE;

    private final static String CAT_WHERE_CLAUSE_CAT_NOT_DELETED = " cat_bDeleted = 0";

    private final static String CAT_WHERE_CLAUSE_SCA_ONE = " cat_cat_lSuper = ?";

    private final static String CAT_WHERE_CLAUSE_CTY = " cat_cty_lKey = ?";

    private final static String CAT_WHERE_CLAUSE_UNI = " cat_uni_lKey = ?";

    private final static String CAT_SET_UNITKEY = "UPDATE tCategory SET cat_uni_lKey = ? WHERE cat_lKey = ?";

    private final static String CAT_SET_UNITKEY_AND_SUPERKEY =
        "UPDATE tCategory SET cat_uni_lKey = ?, cat_cat_lSuper = ? WHERE cat_lKey = ?";

    // SQL objects:

    private CategoryStatisticsHelper categoryStatisticsHelper;

    @Autowired
    private CategoryDao categoryDao;

    public void init()
    {
        super.init();

        categoryStatisticsHelper = new CategoryStatisticsHelper( this );
    }

    public int getCategoryKey( int superCategoryKey, String name )
    {
        CategoryView view = CategoryView.getInstance();
        StringBuffer sql = XDG.generateSelectSQL( view, view.cat_lKey, (Column[]) null );
        XDG.appendWhereSQL( sql, view.cat_cat_lSuper, XDG.OPERATOR_EQUAL, superCategoryKey );
        XDG.appendWhereSQL( sql, view.cat_sName, name );
        CommonHandler commonHandler = getCommonHandler();
        return commonHandler.getInt( sql.toString(), (int[]) null );
    }

    private int[] getCategoryKeysBySuperCategory(CategoryKey superCategoryKey, boolean recursive)
    {

        return getCategoryKeysBySuperCategories( new int[]{superCategoryKey.toInt()}, recursive );
    }

    public int[] getCategoryKeysBySuperCategories(int[] superCategoryKeys, boolean recursive)
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

    private TIntArrayList getSubCategoriesByParent( CategoryKey parentKey, boolean recursive )
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

    public int getContentCount( CategoryKey categoryKey, boolean recursive )
    {

        if ( categoryKey == null )
        {
            return 0;
        }
        return getContentCount( new int[]{categoryKey.toInt()}, recursive );
    }

    private int getContentCount( int[] categoryKeys, boolean recursive )
    {

        if ( categoryKeys == null || categoryKeys.length == 0 )
        {
            return 0;
        }

        int contentCount = 0;

        for ( int i = 0; i < categoryKeys.length; i++ )
        {
            CategoryEntity category = categoryDao.findByKey( new CategoryKey( categoryKeys[i] ) );
            List<ContentKey> contents = contentDao.findContentKeysByCategory( category );
            contentCount += contents.size();

            if ( recursive )
            {
                List<CategoryKey> childrenKeys = category.getChildrenKeys();
                int[] catKeys = new int[childrenKeys.size()];
                for ( int j = 0; j < childrenKeys.size(); j++ ) {
                    catKeys[j] = childrenKeys.get( j ).toInt();
                }
                if ( childrenKeys.size() > 0 )
                {
                    contentCount += getContentCount( catKeys, recursive );
                }
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
                contentCountMap.put( category, getContentCount( category.getKey(), true ) );
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

    public boolean hasSubCategories( CategoryKey categoryKey )
    {
        return hasSubCategories( null, categoryKey, null, false );
    }

    private boolean hasSubCategories( User olduser, CategoryKey categoryKey, int[] contentTypeKeys, boolean adminRead )
    {
        Connection con;
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
                getSecurityHandler().appendCategorySQL(olduser, sql, true );
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
                VerticalEngineLogger.error(message );
            }

            if ( !subCategories )
            {
                int keys[] = getCategoryKeysBySuperCategory(categoryKey, false );
                for ( int i = 0; !subCategories && i < keys.length; i++ )
                {
                    subCategories = hasSubCategories( olduser, new CategoryKey( keys[i] ), contentTypeKeys, adminRead );
                }
            }
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to count sub-categories: %t";
            VerticalEngineLogger.error(message, sqle );
            subCategories = false;
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }

        return subCategories;
    }

    public void updateCategory( User olduser, Document doc )
    {

        Connection con;
        PreparedStatement preparedStmt = null;
        Element root = doc.getDocumentElement();
        Map subElems = XMLTool.filterElements( root.getChildNodes() );

        int categoryKey;
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

            con = getConnection();
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
            Date createdDate = CmsDateAndTimeFormats.parseFrom_STORE_DATE(root.getAttribute("created"));
            preparedStmt.setTimestamp( 5, new Timestamp( createdDate.getTime() ) );

            // attribute: name
            String name = root.getAttribute( "name" );
            preparedStmt.setString(6, name);

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
                    preparedStmt.setString(7, description);
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
                VerticalEngineLogger.errorUpdate(message, null );
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
            VerticalEngineLogger.errorUpdate(message, sqle );
        }
        catch ( NumberFormatException nfe )
        {
            String message = "Failed to parse a key field: %t";
            VerticalEngineLogger.errorUpdate(message, nfe );
        }
        finally
        {
            close( preparedStmt );
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

    public boolean hasContent( CategoryKey categoryKey )
    {

        return getContentCount( categoryKey, true ) > 0;
    }

    public boolean isSubCategory( CategoryKey categoryKey, CategoryKey subCategoryKey )
    {
        CategoryKey parentKey = getParentCategoryKey( subCategoryKey );
        return parentKey != null && ( parentKey.equals( categoryKey ) || isSubCategory( categoryKey, parentKey ) );
    }

    public void moveCategory( User olduser, CategoryKey catKey, CategoryKey superCatKey )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;

        try
        {
            con = getConnection();

            if ( !getSecurityHandler().validateCategoryUpdate( olduser, categoryDao.findByKey( catKey ).getParent().getKey() ) )
            {
                VerticalEngineLogger.errorSecurity("User is not allowed to move the category.", null );
            }

            if ( !getSecurityHandler().validateCategoryUpdate( olduser, superCatKey ) )
            {
                VerticalEngineLogger.errorSecurity("User is not allowed to update the new parent category.", null );
            }

            int[] children = getCategoryKeysBySuperCategory(catKey, true );

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
            VerticalEngineLogger.errorUpdate("A database error occurred: %t", e );
        }
        finally
        {
            close( preparedStmt );
        }
    }

    public List<CategoryKey> getSubCategories( CategoryKey categoryKey )
    {
        return categoryDao.findByKey( categoryKey ).getChildrenKeys();
    }

    public boolean hasCategoriesWithRights( Connection _con, User olduser, int unitKey, int contentTypeKey )
    {

        Connection con;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int count = 0;

        List<Integer> paramValues = new ArrayList<Integer>( 2 );
        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT COUNT(*) FROM " );
        sql.append( CAT_TABLE );
        sql.append( " WHERE" );
        sql.append( CAT_WHERE_CLAUSE_CAT_NOT_DELETED );
        if ( unitKey >= 0 )
        {
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_UNI );
            paramValues.add( unitKey );
        }
        if ( contentTypeKey >= 0 )
        {
            sql.append( " AND" );
            sql.append( CAT_WHERE_CLAUSE_CTY );
            paramValues.add( contentTypeKey );
        }
        String sqlString = sql.toString();
        if ( olduser != null )
        {

            sqlString = getSecurityHandler().appendCategorySQL( olduser, sqlString );
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
            VerticalEngineLogger.error(message, sqle );
        }
        finally
        {
            close( resultSet );
            close( statement );
        }

        return ( count > 0 );
    }

    public void getMenu( User olduser, Element root, CategoryCriteria categoryCriteria )
    {
        UserHandler userHandler = getUserHandler();

        List<Integer> paramValues = new ArrayList<Integer>();
        StringBuilder sqlCategories = new StringBuilder( "SELECT cat_lKey, cat_uni_lKey, cat_cat_lSuper, cat_sName, cat_cty_lKey FROM " );
        sqlCategories.append( CategoryView.getInstance().getReplacementSql() );
        StringBuffer sqlCategoriesWHERE = new StringBuffer( " WHERE " );

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
            getSecurityHandler().appendCategorySQL( olduser, sqlCategoriesWHERE, true );
        }

        Connection con;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        Document doc = root.getOwnerDocument();
        root = XMLTool.createElement( doc, root, "categories" );

        if ( categoryCriteria.useDisableAttribute() )
        {
            root.setAttribute( "disable", "true" );
        }

        try
        {
            con = getConnection();

            String sql = sqlCategories.toString() + sqlCategoriesWHERE.toString();
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
                StringBuilder sqlCategoriesWHEREPK = new StringBuilder( " WHERE cat_lKey IN (" );

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
        }
        catch ( SQLException sqle )
        {
            String message = "Failed to get the categories: %t";
            VerticalEngineLogger.error(message, sqle );
        }
        finally
        {
            close( resultSet );
            close( preparedStmt );
        }
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
        }
    }

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
            VerticalEngineLogger.error("Failed to fetch relation keys: %t", e );
        }

        return doc;
    }

    private void fetchCategories( Document doc, RelationNode node, int levels, boolean topLevel, boolean details, boolean catCount,
                                  boolean contentCount )
        throws SQLException
    {
        node.selectLevels( levels == 0 ? Integer.MAX_VALUE : levels );
        Map<Number, Element> entryMap = fetchCategoryElements( doc, node.findSelectedKeys(), details );
        Element root = buildCategoriesElement( doc, entryMap, node, topLevel, catCount, contentCount );
        doc.appendChild( root );
    }

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

        return elem;
    }


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

    private Map<Number, Element> fetchCategoryElements( Document doc, Set keys, boolean details )
        throws SQLException
    {
        Connection conn;
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
        }

        return map;
    }

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

        return getCategoryTree( olduser, categoryKey, groups, contentCount );
    }

    private RelationNode getCategoryTree( User olduser, CategoryKey categoryKey, String[] groups, boolean contentCount )
        throws SQLException
    {
        Connection con;
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
                    Integer entry = result.getInt( 1 );
                    Integer count = result.getInt( 2 );

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
        }

        return node;
    }

    private String getSelectCategoriesSQL( User olduser )
    {
        SecurityHandler securityHandler = getSecurityHandler();
        CategoryView view = CategoryView.getInstance();
        Column[] selectColumns = {view.cat_lKey, view.cat_cat_lSuper};
        Column[] whereColumns = {view.cat_uni_lKey};
        StringBuffer sql = XDG.generateSelectSQL( view, selectColumns, false, whereColumns );

        securityHandler.appendCategorySQL( olduser, sql, false );
        return sql.toString();
    }

    private String getKeySetSQL( String[] keys )
    {
        if ( keys != null )
        {
            StringBuilder sql = new StringBuilder();

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

    private String getSelectContentCountSQL( CategoryKey key, String[] groups )
    {
        ContentPublishedView view = ContentPublishedView.getInstance();
        ConAccessRight2Table accessTable = this.db.tConAccessRight2;

        StringBuilder sql = new StringBuilder();
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

    private String getSelectUnitByCategorySQL( CategoryKey key )
    {
        CategoryTable table = this.db.tCategory;
        StringBuilder sql = new StringBuilder();
        sql.append( "SELECT " ).append( table.cat_uni_lKey.getName() );
        sql.append( " FROM " ).append( table.getName() );
        sql.append( " WHERE " ).append( table.cat_lKey.getName() );
        sql.append( " = " ).append( key.toInt() );
        return sql.toString();
    }

    private String getSelectCategoryByKeysSQL( Set keys )
    {
        CategoryView table = CategoryView.getInstance();

        StringBuilder sql = new StringBuilder();
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
        sql.append( " WHERE " );

        InClauseBuilder inClauseFilter = new InClauseBuilder<Object>( table.cat_lKey.getName(), keys )
        {
            public void appendValue( StringBuffer sql, Object value )
            {
                sql.append( value );
            }
        };

        sql.append( inClauseFilter );
        return sql.toString();
    }

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

        StringBuilder sql = new StringBuilder();
        sql.append( "select" );
        sql.append( " cat_lkey," );
        sql.append( " cat_cat_lsuper" );
        sql.append( " from tcategory" );
        sql.append( " where cat_uni_lkey = ?" );
        sql.append( " order by cat_cat_lsuper asc, cat_lkey asc" );

        Connection con;
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
        }
        return categoryStatistics;
    }

    public void collectStatisticsFromContent( int unitKey, Map<Integer, CategoryStatistics> catStats )
    {

        StringBuilder sql = new StringBuilder();
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

        Connection con;
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
        }
    }

    public void collectStatisticsFromBinaryData( int unitKey, Map<Integer, CategoryStatistics> catStats )
    {

        StringBuilder sql = new StringBuilder();
        sql.append( "select" );
        sql.append( " cat_lkey," );
        sql.append( " cat_cat_lsuper," );
        sql.append( " sum(@length@(bda_sfilename))," );
        sql.append( " sum(bda_lfilesize * 1.0)" );
        sql.append( " from tbinarydata" );
        sql.append( " left join tcontentbinarydata on bda_lkey = cbd_bda_lkey" );
        sql.append( " left join tcontentversion on cbd_cov_lkey = cov_lkey" );
        sql.append( " left join tcontent on cov_con_lkey = con_lkey" );
        sql.append( " left join tcategory on con_cat_lkey = cat_lkey" );
        sql.append( " where cat_uni_lkey = ?" );
        sql.append( " group by cat_lkey, cat_cat_lsuper" );
        sql.append( " order by cat_cat_lsuper asc, cat_lkey asc" );

        Connection con;
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

                Integer categoryKey = resultSet.getInt( 1 );
                CategoryStatistics cs = catStats.get( categoryKey );
                if ( cs != null )
                {
                    long filenameSize = resultSet.getLong( 3 ) * 2; // two times because it is in content version too
                    long amount = constantSize + filenameSize + new Double( resultSet.getDouble( 4 ) ).longValue();
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
        }
    }

}
