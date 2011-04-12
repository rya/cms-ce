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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.group.GroupType;
import com.enonic.cms.core.security.userstore.UserStoreKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;
import com.enonic.vertical.event.VerticalEventListener;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.User;
import com.enonic.cms.core.security.user.UserType;

public final class GroupHandler
    extends BaseHandler
    implements VerticalEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( GroupHandler.class.getName() );

    final static private String USER_TABLE = "tUser";

    final static private String GROUP_TABLE = "tGroup";

    final static private String GRPGRPMEM_TABLE = "tGrpGrpMembership";

    final static private String GROUP_ALL_FIELDS =
        "grp_hkey, grp_sname, grp_dom_lkey, grp_ltype, grp_sdescription, grp_bisdeleted, grp_brestricted, grp_ssyncvalue";

    final static private String GROUP_GET = "SELECT " + GROUP_ALL_FIELDS + " FROM " + GROUP_TABLE;

    final static private String GROUP_GET_KEY_BY_GROUPTYPE = "SELECT grp_hKey FROM " + GROUP_TABLE + " WHERE grp_lType = ?";


    final static private String GROUPS_GET_BY_DOMAIN_AND_TYPE =
        GROUP_GET + " LEFT JOIN " + USER_TABLE + " ON " + USER_TABLE + ".usr_grp_hKey = " + GROUP_TABLE +
            ".grp_hKey WHERE (grp_dom_lKey = ? OR usr_dom_lKey = ?) AND grp_lType = ? AND grp_bIsDeleted != 1";

    final static private String GRPGRPMEM_GET_MEMBERSHIPS =
        "SELECT * FROM " + GRPGRPMEM_TABLE + " LEFT JOIN " + GROUP_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + GRPGRPMEM_TABLE +
            ".ggm_mbr_grp_hKey LEFT JOIN " + USER_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + USER_TABLE + ".usr_grp_hKey " +
            " WHERE ggm_grp_hKey=?";

    final static private String GRPGRPMEM_GET_MULTIPLE_MEMBERSHIPS =
        "SELECT * FROM " + GRPGRPMEM_TABLE + " LEFT JOIN " + GROUP_TABLE + " ON " + GROUP_TABLE + ".grp_hKey = " + GRPGRPMEM_TABLE +
            ".ggm_mbr_grp_hKey WHERE ggm_mbr_grp_hKey IN ";


    static private Map<UserStoreKey, String> authenticatedUsersGroupKeys = new HashMap<UserStoreKey, String>();

    synchronized public String getAuthenticatedUsersGroupKey( UserStoreKey userStoreKey )
    {
        if ( userStoreKey == null )
        {
            return null;
        }

        String groupKey = authenticatedUsersGroupKeys.get( userStoreKey );

        if ( groupKey == null )
        {
            // get group key and insert it into the cache
            // (perhaps this code should be replaced with something more efficient)
            Document doc = getGroupsByDomain( userStoreKey, GroupType.AUTHENTICATED_USERS );
            Element groupsElement = doc.getDocumentElement();
            Element groupElement = XMLTool.getElement( groupsElement, "group" );
            groupKey = groupElement.getAttribute( "key" );

            authenticatedUsersGroupKeys.put( userStoreKey, groupKey );
        }

        return groupKey;
    }

    private String getGroupKeyByGroupType( GroupType groupType )
    {

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        String key = null;
        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( GROUP_GET_KEY_BY_GROUPTYPE );
            preparedStmt.setInt( 1, groupType.toInteger() );
            resultSet = preparedStmt.executeQuery();

            if ( resultSet.next() )
            {
                key = resultSet.getString( 1 );
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

        return key;
    }


    /**
     * Generic method for retrieving groups.
     *
     * @param sql            The SQL query to execute.
     * @param paramValues    An array containing integer paramters that should be set in the PreparedStatement instance.
     * @param includeMembers Boolean parameter specifying if the result should include a list of the group members.
     */
    private Document getGroups( String sql, Object[] paramValues, boolean includeMembers )
    {
        Document doc = XMLTool.createDocument("groups");

        Connection con = null;
        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;

        try
        {
            con = getConnection();
            preparedStmt = con.prepareStatement( sql );
            for ( int i = 0; i < paramValues.length; ++i )
            {
                preparedStmt.setObject( i + 1, paramValues[i] );
            }
            resultSet = preparedStmt.executeQuery();

            groupResultSetToDom( con, doc.getDocumentElement(), resultSet, includeMembers, 0, Integer.MAX_VALUE );
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

        return doc;
    }

    private Document getGroupsByDomain( UserStoreKey userStoreKey, GroupType type )
    {
        return getGroups( GROUPS_GET_BY_DOMAIN_AND_TYPE, new Object[]{userStoreKey.toInt(), userStoreKey.toInt(), type.toInteger()}, true );
    }

    private void groupResultSetToDom( Connection con, Element rootElement, ResultSet grpResultSet, boolean includeMembers, int index,
                                      int count )
        throws SQLException
    {
        Document doc = rootElement.getOwnerDocument();

        PreparedStatement preparedStmt = con.prepareStatement( GRPGRPMEM_GET_MEMBERSHIPS );

        try
        {
            int i = 0;
            boolean moreResults = false;
            for (; ( i < index + count ) && ( moreResults = grpResultSet.next() ); i++ )
            {
                if ( i < index )
                {
                    continue;
                }

                Element groupElement = XMLTool.createElement( doc, rootElement, "group" );

                // attribute: group key
                String gKey = grpResultSet.getString( "grp_hKey" );
                groupElement.setAttribute( "key", String.valueOf( gKey ) );

                // attribute: group type
                GroupType groupType = GroupType.get( grpResultSet.getInt( "grp_lType" ) );
                if ( grpResultSet.wasNull() )
                {
                    if ( gKey.equals( getEnterpriseAdministratorGroupKey() ) )
                    {
                        groupElement.setAttribute( "type", "0" );
                    }
                    else if ( gKey.equals( getAnonymousGroupKey() ) )
                    {
                        groupElement.setAttribute( "type", "7" );
                    }
                }
                else
                {
                    groupElement.setAttribute( "type", groupType.toInteger().toString() );
                }

                // attribute: restricted
                groupElement.setAttribute( "restricted", String.valueOf( grpResultSet.getBoolean( "grp_bRestricted" ) ) );

                if ( groupType == GroupType.ENTERPRISE_ADMINS )
                {
                    groupElement.setAttribute( "scope", "0" );
                }
                else if ( groupType == GroupType.USERSTORE_GROUP || groupType == GroupType.USERSTORE_ADMINS ||
                    groupType == GroupType.AUTHENTICATED_USERS || groupType == GroupType.ANONYMOUS )
                {
                    groupElement.setAttribute( "scope", "1" );
                }
                else if ( groupType == GroupType.GLOBAL_GROUP || groupType == GroupType.ADMINS || groupType == GroupType.CONTRIBUTORS )
                {
                    groupElement.setAttribute( "scope", "2" );
                }
                else
                {
                    groupElement.setAttribute( "scope", "-1" );
                }

                // element: group name
                XMLTool.createElement( doc, groupElement, "name", grpResultSet.getString( "grp_sName" ) );

                // element: group description
                XMLTool.createElement( doc, groupElement, "description", grpResultSet.getString( "grp_sDescription" ) );

                if ( includeMembers )
                {
                    buildGroupMembersDOM( preparedStmt, groupElement, gKey );
                }
            }

            if ( moreResults )
            {
                while ( grpResultSet.next() )
                {
                    ++i;
                }
            }

            rootElement.setAttribute( "totalcount", String.valueOf( i ) );
        }
        finally
        {
            // the outer try/finally-block makes sure we close the
            // PreparedStatement even if we encounter errors.
            close( preparedStmt );
        }
    }

    private void buildGroupMembersDOM( PreparedStatement preparedStmt, Element groupElement, String gKey )
        throws SQLException
    {
        Document doc = groupElement.getOwnerDocument();

        ResultSet resultSet = null;
        try
        {
            preparedStmt.setString( 1, gKey );
            resultSet = preparedStmt.executeQuery();

            Element membersElement = XMLTool.createElement( doc, groupElement, "members" );
            Element groupMembersElement = XMLTool.createElement( doc, membersElement, "groups" );

            int membercount = 0;
            while ( resultSet.next() )
            {
                Element memberGroupElement = XMLTool.createElement( doc, groupMembersElement, "group" );
                memberGroupElement.setAttribute( "key", resultSet.getString( "grp_hKey" ) );

                GroupType groupType = GroupType.get( resultSet.getInt( "grp_lType" ) );
                memberGroupElement.setAttribute( "type", groupType.toInteger().toString() );
                memberGroupElement.setAttribute( "restricted", String.valueOf( resultSet.getBoolean( "grp_dom_lKey" ) ) );

                // sort is used as a prefix to the title
                int sort = 1;

                memberGroupElement.setAttribute( "name", "hey" );

                memberGroupElement.setAttribute( "sort", String.valueOf( sort ) );
                membercount++;
            }

            membersElement.setAttribute( "membercount", String.valueOf( membercount ) );
        }
        finally
        {
            close( resultSet );
        }
    }

    public String[] getAllGroupMembershipsForUser( User user )
    {
        return getAllGroupMembershipsForUser( user.getKey().toString(), user.getType(), user.getUserGroupKey(), user.getUserStoreKey() );
    }

    private String[] getAllGroupMembershipsForUser( String userKey, UserType userType, GroupKey userGroupKey, UserStoreKey userStoreKey )
    {

        Connection con = null;
        String[] groups;
        if ( userKey != null )
        {
            if ( userType != UserType.ANONYMOUS && userType != UserType.ADMINISTRATOR )
            {
                groups = new String[]{userGroupKey.toString(), getAuthenticatedUsersGroupKey( userStoreKey ), getAnonymousGroupKey()};
            }
            else
            {
                groups = new String[]{getAnonymousGroupKey()};
            }
        }
        else
        {
            groups = new String[]{getAnonymousGroupKey()};
        }

        Set<String> excludeGroups = new HashSet<String>();

        excludeGroups.addAll( Arrays.asList( groups ) );

        try
        {
            con = getConnection();
            String[] addGroups = null;
            while ( addGroups == null || addGroups.length > 0 )
            {
                addGroups = getGroupMemberships( con, groups, excludeGroups );

                // extend group array
                String[] newGroups = new String[groups.length + addGroups.length];
                System.arraycopy( groups, 0, newGroups, 0, groups.length ); // copy old groups
                System.arraycopy( addGroups, 0, newGroups, groups.length, addGroups.length ); // add new groups

                // add groups to the excluded set
                excludeGroups.addAll( Arrays.asList( addGroups ) );

                groups = newGroups;
            }
        }
        catch ( SQLException e )
        {
            LOG.error( StringUtil.expandString( "A database error occurred: %t", (Object) null, e ), e );
        }
        finally
        {
            close( con );
        }

        return groups;
    }


    public String[] getGroupMemberships( Connection _con, String[] groups, Set<String> excludeGroups )
    {

        PreparedStatement preparedStmt = null;
        ResultSet resultSet = null;
        ArrayList<String> newGroups = new ArrayList<String>();

        Connection con = _con;

        try
        {
            if ( con == null )
            {
                con = getConnection();
            }

            StringBuffer sql = new StringBuffer( "(" );
            int groupCount = 0;
            for ( int i = 0; i < groups.length; ++i )
            {
                if ( i > 0 )
                {
                    sql.append( "," );
                }

                sql.append( "'" );
                sql.append( groups[i] );
                sql.append( "'" );
                ++groupCount;
            }
            sql.append( ")" );

            // there is no point in going any further if there are no groups
            if ( groupCount > 0 )
            {
                preparedStmt = con.prepareStatement( GRPGRPMEM_GET_MULTIPLE_MEMBERSHIPS + sql.toString() );

                resultSet = preparedStmt.executeQuery();

                while ( resultSet.next() )
                {
                    String groupKey = resultSet.getString( "ggm_grp_hKey" );
                    if ( ( excludeGroups == null || !excludeGroups.contains( groupKey ) ) && !newGroups.contains( groupKey ) )
                    {
                        newGroups.add( groupKey );
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
            if ( _con == null )
            {
                close( con );
            }
        }

        return newGroups.toArray( new String[newGroups.size()] );
    }

    public String getAdminGroupKey()
    {
        return getGroupKeyByGroupType(GroupType.ADMINS);
    }

    public String getAnonymousGroupKey()
    {
        return getGroupKey( GroupType.ANONYMOUS );
    }

    public String getEnterpriseAdministratorGroupKey()
    {
        return getGroupKey( GroupType.ENTERPRISE_ADMINS );
    }

    private String getGroupKey( GroupType type )
    {
        GroupEntity group = groupDao.findSingleByGroupType( type );
        return group.getGroupKey().toString();
    }
}

