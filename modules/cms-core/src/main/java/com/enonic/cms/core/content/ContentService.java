/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Collection;
import java.util.List;

import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.category.CategoryKey;
import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.SnapshotContentCommand;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.command.UpdateAssignmentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.content.query.ContentByCategoryQuery;
import com.enonic.cms.core.content.query.ContentByContentQuery;
import com.enonic.cms.core.content.query.ContentByQueryQuery;
import com.enonic.cms.core.content.query.ContentBySectionQuery;
import com.enonic.cms.core.content.query.OpenContentQuery;
import com.enonic.cms.core.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.core.content.query.RelatedContentQuery;
import com.enonic.cms.core.content.resultset.ContentResultSet;
import com.enonic.cms.core.content.resultset.ContentVersionResultSet;
import com.enonic.cms.core.content.resultset.RelatedContentResultSet;
import com.enonic.cms.core.security.user.UserEntity;

public interface ContentService
{

    SnapshotContentResult snapshotContent( SnapshotContentCommand command );

    AssignContentResult assignContent( AssignContentCommand command );

    void updateAssignment( UpdateAssignmentCommand command );

    UnassignContentResult unassignContent( UnassignContentCommand command );

    void deleteContent( UserEntity deleter, ContentEntity content );

    /**
     * Flags the given content as archived. NB! The content must be an existing content!
     *
     * @param archiver The running user
     * @param content  The content to archive. Cannot be null
     * @return true if the content was updated, otherwise false
     */
    boolean archiveContent( final UserEntity archiver, final ContentEntity content );

    /**
     * Flags the given content as approved. NB! The content must be an existing content!
     *
     * @param approver The running user
     * @param content  The content to approve. Cannot be null
     * @return true if the content was updated, otherwise false
     */
    boolean approveContent( final UserEntity approver, final ContentEntity content );

    void deleteVersion( UserEntity deleter, ContentVersionKey contentVersionKey );

    void deleteByCategory( UserEntity deleter, CategoryEntity category );

    void moveContent( UserEntity mover, ContentEntity content, CategoryEntity toCategory );

    ContentKey copyContent( UserEntity copier, ContentEntity content, CategoryEntity toCategory );

    ContentKey createContent( CreateContentCommand command );

    UpdateContentResult updateContent( UpdateContentCommand command );

    /**
     * Get either the children og parents of a set of content. Only the related content that is related to every content passed in, will be
     * included in the content returned.
     *
     * @param user     The currently logged in user.
     * @param relation If this number is positive, its the related children that will be returned. If it is negative, the parents will be
     *                 returned.
     * @param content  The set of content to find the related content for.
     * @return A set of all content, related as specified by <code>relation</code> to the set in <code>content</code>.
     */
    RelatedContentResultSet getRelatedContentRequiresAll( UserEntity user, int relation, ContentResultSet content );

    boolean isContentInUse( ContentKey contentKey );

    boolean isContentInUse( List<ContentKey> contentKeys );

    ContentResultSet queryContent( OpenContentQuery query );

    ContentResultSet queryContent( ContentByQueryQuery query );

    ContentResultSet queryContent( ContentByContentQuery query );

    ContentResultSet queryContent( ContentByCategoryQuery query );

    ContentResultSet queryContent( ContentBySectionQuery query );

    RelatedContentResultSet queryRelatedContent( RelatedContentQuery query );

    RelatedContentResultSet queryRelatedContent( RelatedChildrenContentQuery spec );

    ContentResultSet getContent( ContentSpecification specification, String orderByCol, int count, int index );

    ContentVersionResultSet getContentVersions( ContentVersionSpecification specification, String orderBy, int count, int index );

    /**
     * Collect all index values and create an aggregated set of all.
     *
     * @param user                 The currently logged in user.
     * @param field                The field from which to get the index values.
     * @param categoryFilter       A filter, to limit the values to a certain category.
     * @param includeSubCategories Whether or not to include subcategories of the categories in the filter.
     * @param contentTypeFilter    A filter, to limit the values to a certain content type.
     * @return A result set of aggregated index values.
     */
    XMLDocument getAggregatedIndexValues( UserEntity user, String field, Collection<CategoryKey> categoryFilter,
                                          boolean includeSubCategories, Collection<ContentTypeKey> contentTypeFilter );

    /**
     * Find index values.
     *
     * @param user                 The currently logged in user.
     * @param field                The field from which to get the index values.
     * @param categoryFilter       A filter, to limit the values to a certain category.
     * @param includeSubCategories Whether or not to include subcategories of the categories in the filter.
     * @param contentTypeFilter    A filter, to limit the values to a certain content type.
     * @param index                The starting index of the index values to return.
     * @param count                The number of matches to return for each call to this method.
     * @param descOrder            Whether or not to sort the result in the opposite order.
     * @return A result set of index values.
     */
    XMLDocument getIndexValues( UserEntity user, String field, Collection<CategoryKey> categoryFilter, boolean includeSubCategories,
                                Collection<ContentTypeKey> contentTypeFilter, int index, int count, boolean descOrder );

    /**
     * Find the specific content corresponding to the given menuItemID. This is not done by searching, but a simple lookup in the database.
     *
     * @param menuItemKey The menuItem to find the content for.
     * @return A result set with the one content, index is 0 and count is 1.
     */
    ContentResultSet getPageContent( int menuItemKey );

    List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType );

    List<ContentTypeEntity> getAllContentTypes();

}
