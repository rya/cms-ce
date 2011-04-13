/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.engine.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.enonic.cms.core.content.category.CategoryKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.enonic.esl.util.StringUtil;
import com.enonic.vertical.engine.CategoryAccessRight;
import com.enonic.vertical.engine.criteria.Criteria;
import com.enonic.vertical.engine.criteria.MenuItemCriteria;
import com.enonic.vertical.engine.dbmodel.CatAccessRightView;
import com.enonic.vertical.event.VerticalEventListener;
import com.enonic.cms.core.security.user.User;

final public class SecurityHandler
    extends BaseHandler
    implements VerticalEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SecurityHandler.class.getName() );


    // constants -----------------------------------------------------------------------------------

    private final static String MENUITEMAR_TABLE = "tMenuItemAR";

    private final static String COA_TABLE = "tConAccessRight2";

    private final static String MENUITEMAR_SECURITY_FILTER_1 =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lKey" + " AND mia_grp_hKey IN (";

    private final static String MENUITEMAR_SECURITY_FILTER_ON_PARENT =
        " EXISTS (SELECT mia_mei_lKey FROM " + MENUITEMAR_TABLE + " WHERE mia_mei_lKey = mei_lParent" + " AND mia_grp_hKey IN (";

    private final static String CAR_SELECT =
        "SELECT car_cat_lKey, grp_hKey, grp_sName, grp_lType, usr_hKey, usr_sUID, usr_sFullName, car_bRead, car_bCreate," +
            " car_bPublish, car_bAdministrate, car_bAdminRead FROM " + CatAccessRightView.getInstance().getReplacementSql();

    private final static String CAR_WHERE_CLAUSE_CAT = " car_cat_lKey = ?";

    private final static String CAR_WHERE_CLAUSE_GROUP_IN = " grp_hKey IN ";

    private final static String COA_WHERE_CLAUSE_SECURITY_FILTER =
        " EXISTS (SELECT coa_grp_hKey FROM " + COA_TABLE + " WHERE coa_con_lKey = con_lKey" + " AND coa_grp_hKey IN (%0))";

    // methods --------------------------------------------------------------------------------------

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

}
