/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.adminweb;

import java.util.Collection;
import java.util.Date;

import org.w3c.dom.Document;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.util.DateUtil;
import com.enonic.esl.util.StringUtil;
import com.enonic.esl.xml.XMLTool;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentXMLCreator;
import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.content.query.OpenContentQuery;
import com.enonic.cms.domain.content.query.RelatedContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.security.user.UserSpecification;

/**
 * This class contains common methods used to search for content.
 */
public final class SearchUtility
{
    private UserDao userDao;

    private ContentService contentService;

    private SecurityService securityService;

    private GroupDao groupDao;

    /**
     * Private constructor.
     */
    public SearchUtility( UserDao userDao, GroupDao groupDao, SecurityService securityService, ContentService contentService )
    {
        this.userDao = userDao;
        this.groupDao = groupDao;
        this.securityService = securityService;
        this.contentService = contentService;
    }

    /**
     * Return the simple search string.
     */
    private static String parseSimpleSearch( ExtendedMap items )
    {
        boolean scopeAll = items.getString( "scope", "" ).equals( "all" );
        String search = items.getString( "searchtext", "" );

        return SearchBuilder.buildFromUserInput( search, true, scopeAll, false ).toString();
    }

    /**
     * Return the advanced search string.
     */
    private String parseAdvancedSearch( ExtendedMap items )
    {
        SearchStringBuffer search = new SearchStringBuffer();

        String key = items.getString( "acontentkey", null );
        if ( key != null )
        {
            search.appendKey( key );
        }

        if ( items.containsKey( "owner.key" ) )
        {
            search.appendOwner( "'" + items.getString( "owner.key" ) + "'" );
        }

        if ( items.containsKey( "modifier.key" ) )
        {
            search.appendModifier( "'" + items.getString( "modifier.key" ) + "'" );
        }

        boolean assigneeOrAssignerSet = false;

        if ( items.containsKey( "_assignee" ) )
        {
            UserSpecification userSpec = new UserSpecification();
            userSpec.setKey( new UserKey( items.getString( "_assignee" ) ) );
            UserEntity assigneeUser = userDao.findSingleBySpecification( userSpec );
            search.appendAssigneeQualifiedName( "'" + assigneeUser.getQualifiedName().toString() + "'" );
            assigneeOrAssignerSet = true;
        }

        if ( items.containsKey( "_assigner" ) )
        {
            UserSpecification userSpec = new UserSpecification();
            userSpec.setKey( new UserKey( items.getString( "_assigner" ) ) );
            UserEntity assignerUser = userDao.findSingleBySpecification( userSpec );
            search.appendAssignerQualifiedName( "'" + assignerUser.getQualifiedName().toString() + "'" );
            assigneeOrAssignerSet = true;
        }

        if ( items.containsKey( "date_assignmentDueDate" ) )
        {
            String assignmentDueDateOperator = items.getString( "_assignmentDueDate.op" );
            String assignmentDueDate = items.getString( "date_assignmentDueDate" );

            String assigmentDueDateQueryValue = parseDatetime( assignmentDueDate );
            search.appendAssignmentDueDate( parseDatetimeOperator( assignmentDueDateOperator ), assigmentDueDateQueryValue );

            search.appendNotEmpty( "assignmentDueDate" );

            if ( !assigneeOrAssignerSet )
            {
                search.appendNotEmpty( "assignee/qualifiedName" );
            }

        }

        if ( items.containsKey( "state" ) )
        {
            String nowDate = DateUtil.formatISODateTime( new Date() );

            if ( items.getString( "state" ).equals( "4" ) )
            {
                search.appendStatus( "" + ContentStatus.APPROVED.getKey() );
                search.appendPublishFrom( " > ", "date(\"" + nowDate + "\")" );

            }
            else if ( items.getString( "state" ).equals( "5" ) )
            {
                // nothing to do, handled outside this function with publishedOnly flag
            }
            else if ( items.getString( "state" ).equals( "6" ) )
            {
                search.appendStatus( "" + ContentStatus.APPROVED.getKey() );
                search.appendPublishTo( " < ", "date(\"" + nowDate + "\")" );
            }
            else
            {
                search.appendStatus( items.getString( "state" ) );
            }
        }

        if ( items.containsKey( "datecreated" ) )
        {
            search.appendCreated( parseDatetimeOperator( items.getString( "created.op" ) ),
                                  parseDatetime( items.getString( "datecreated" ) ) );
        }

        if ( items.containsKey( "datemodified" ) )
        {
            search.appendTimestamp( parseDatetimeOperator( items.getString( "modified.op" ) ),
                                    parseDatetime( items.getString( "datemodified" ) ) );
        }

        String filter = items.getString( "filter", "" ).trim();
        if ( filter.length() > 0 )
        {
            search.appendRaw( filter );
        }

        final String scopeStr = items.getString( "ascope", "" );
        search.appendRaw( SearchBuilder.buildFromUserInput( items.getString( "asearchtext", "" ), true, scopeStr.equals( "all" ),
                                                            scopeStr.equals( "fileAttachments" ) ) );

        return search.toString();
    }

    public XMLDocument simpleSearch( User user, ExtendedMap formItems, CategoryKey categoryKey, int[] contentTypeKeys, String orderBy,
                                     int index, int count )
        throws VerticalAdminException
    {
        String query = parseSimpleSearch( formItems );
        boolean publishedOnly = false;

        return findContent( user, categoryKey, contentTypeKeys, query, true, orderBy, publishedOnly, index, count, true, true );
    }

    public String simpleReport( User user, ExtendedMap formItems, int cat )
        throws VerticalAdminException
    {
        String query = parseSimpleSearch( formItems );
        boolean publishedOnly = false;

        int[] categoryKeys = null;
        if ( cat != -1 )
        {
            categoryKeys = new int[]{cat};
        }

        return findContent( user, categoryKeys, null, query, publishedOnly, true, 0, Integer.MAX_VALUE, 1, 1, 0, true, true, true, true );
    }

    public XMLDocument advancedSearch( User user, ExtendedMap formItems, int[] contentTypeKeys, String orderBy, int index, int count )
        throws VerticalAdminException
    {
        boolean includeSubCategories = formItems.getString( "subcategories", "0" ).equals( "1" );
        boolean publishedOnly = formItems.getString( "state", "" ).equals( "5" ); // if status = Online => search with publishedOnly flag

        String query = parseAdvancedSearch( formItems );

        int submittetCategoryKey = formItems.getInt( "categorykey", -1 );

        if ( submittetCategoryKey == -1 )
        {
            submittetCategoryKey = formItems.getInt( "cat", -1 );
        }

        CategoryKey categoryKey = CategoryKey.parse( submittetCategoryKey );

        return findContent( user, categoryKey, contentTypeKeys, query, includeSubCategories, orderBy, publishedOnly, index, count, true,
                            true );
    }

    private static String parseDatetimeOperator( String op )
    {
        if ( op.equals( "lte" ) )
        {
            return " <= ";
        }
        else if ( op.equals( "gte" ) )
        {
            return " >= ";
        }
        else
        {
            return " = ";
        }
    }

    private static String parseDatetime( String date )
    {
        StringBuffer str = new StringBuffer();
        str.append( " date(\"" );
        String[] fields = StringUtil.splitString( date, '.' );
        str.append( fields[2] ).append( "-" );
        str.append( fields[1] ).append( "-" );
        str.append( fields[0] );
        str.append( "\")" );

        return str.toString();
    }

    public String advancedReport( User user, ExtendedMap formItems, int[] contentTypeKeys )
        throws VerticalAdminException
    {

        boolean includeSubCategories = formItems.getString( "subcategories", "0" ).equals( "1" );
        boolean publishedOnly = formItems.getString( "state", "" ).equals( "5" ); // if status = Online => search with publishedOnly flag

        String query = parseAdvancedSearch( formItems );
        int[] categoryKeys = null;
        if ( formItems.getInt( "cat", -1 ) != -1 )
        {
            categoryKeys = new int[]{formItems.getInt( "cat" )};
        }

        return findContent( user, categoryKeys, contentTypeKeys, query, publishedOnly, includeSubCategories, 0, Integer.MAX_VALUE, 1, 1, 0,
                            true, true, true, true );
    }


    private XMLDocument findContent( User user, CategoryKey categoryKey, int[] contentTypeKeys, String criteria, boolean subCategories,
                                     String orderBy, boolean publishedOnly, int index, int count, boolean includeUserRights,
                                     boolean adminReadOnly )
    {
        int[] categoryKeys = null;
        if ( categoryKey != null )
        {
            categoryKeys = new int[]{categoryKey.toInt()};
        }
        return doFindContent( user, categoryKeys, contentTypeKeys, criteria, orderBy, publishedOnly, subCategories, index, count, false, 0,
                              0, 0, false, includeUserRights, adminReadOnly, true );
    }

    private String findContent( User user, int[] categoryKeys, int[] contentTypeKeys, String criteria, boolean publishedOnly,
                                boolean subCategories, int index, int count, int childrenLevel, int parentLevel, int parentChildrenLevel,
                                boolean includeContentOwners, boolean includeRelatedContentOwners, boolean includeUserRights,
                                boolean adminReadOnly )
    {
        Document result =
            doFindContent( user, categoryKeys, contentTypeKeys, criteria, null, publishedOnly, subCategories, index, count, false,
                           childrenLevel, parentLevel, parentChildrenLevel, false, includeUserRights, adminReadOnly,
                           ( includeContentOwners || includeRelatedContentOwners ) ).getAsDOMDocument();

        return XMLTool.documentToString( result );
    }


    /**
     * Find content, based on given criteria.
     *
     * @param user                        The logged in user.
     * @param categoryKeysInt             One or more categories to search in.
     * @param contentTypeKeys             One or more content types to search in.
     * @param criteria                    The search string from the user.
     * @param orderBy                     The requested ordering.
     * @param publishedOnly               Whether or not to limit the search to currently published documents.
     * @param includeSubCategories        Whether or not to include subcategories for the defined <code>categoryKeys</code>.
     * @param index                       The start index in the search list.
     * @param count                       The number of items in the search list.
     * @param titlesOnly                  Whether or not to limit the resulting XML to titles only.
     * @param childrenLevel               The level of children to include.
     * @param parentLevel                 The level of parents to include.
     * @param parentChildrenLevel         The level of children for parents to include.
     * @param relatedTitlesOnly           Whether or not to limit related contents to titles only.
     * @param includeUserRight            Whether or not to include user rights in the resulting XML.
     * @param adminReadOnly               Return only the doucuments that have the admin read only property set.
     * @param includeOwnerAndModifierInfo Decides whether or not to include owner and modifier info in the resulting XML.
     * @return An XML Document with the result of the search.
     */
    private XMLDocument doFindContent( User user, int[] categoryKeysInt, int[] contentTypeKeys, String criteria, String orderBy,
                                       boolean publishedOnly, boolean includeSubCategories, int index, int count, boolean titlesOnly,
                                       int childrenLevel, int parentLevel, int parentChildrenLevel, boolean relatedTitlesOnly,
                                       boolean includeUserRight, boolean adminReadOnly, boolean includeOwnerAndModifierInfo )
    {
        UserEntity userEntity = securityService.getUser( user.getKey() );
        Collection<CategoryKey> categories = CategoryKey.convertToList( categoryKeysInt );
        Collection<ContentTypeKey> contentTypeFilter = ContentTypeKey.convertToList( contentTypeKeys );
        final Date now = new Date();
        OpenContentQuery openContentQuery = new OpenContentQuery();
        openContentQuery.setUser( userEntity );
        openContentQuery.setCategoryKeyFilter( categories, includeSubCategories ? Integer.MAX_VALUE : 1 );
        openContentQuery.setQuery( criteria );
        openContentQuery.setOrderBy( orderBy );
        openContentQuery.setContentTypeFilter( contentTypeFilter );
        openContentQuery.setIndex( index );
        openContentQuery.setCount( count );
        if ( publishedOnly )
        {
            openContentQuery.setFilterContentOnlineAt( now );
        }
        else
        {
            openContentQuery.setFilterIncludeOfflineContent();
        }
        openContentQuery.setFilterAdminBrowseOnly( adminReadOnly );

        ContentResultSet contents = contentService.queryContent( openContentQuery );

        RelatedContentQuery relatedContentQuery = new RelatedContentQuery( now );
        relatedContentQuery.setUser( userEntity );
        relatedContentQuery.setContentResultSet( contents );
        relatedContentQuery.setParentLevel( parentLevel );
        relatedContentQuery.setChildrenLevel( childrenLevel );
        relatedContentQuery.setParentChildrenLevel( parentChildrenLevel );
        relatedContentQuery.setIncludeOnlyMainVersions( true );

        RelatedContentResultSet relatedContents = contentService.queryRelatedContent( relatedContentQuery );

        ContentXMLCreator xmlCreator = new ContentXMLCreator();
        xmlCreator.setResultIndexing( index, count );
        xmlCreator.setIncludeCategoryData( !titlesOnly );
        xmlCreator.setIncludeContentData( !titlesOnly );
        xmlCreator.setIncludeOwnerAndModifierData( !titlesOnly );
        xmlCreator.setIncludeOwnerAndModifierData( includeOwnerAndModifierInfo );
        xmlCreator.setIncludeRelatedContentData( !relatedTitlesOnly );
        xmlCreator.setIncludeUserRightsInfo( includeUserRight, new CategoryAccessResolver( groupDao ),
                                             new ContentAccessResolver( groupDao ) );
        return xmlCreator.createContentsDocument( userEntity, contents, relatedContents );
    }

}
