/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.xml.XMLDocument;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.SnapshotContentCommand;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.command.UpdateAssignmentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.index.ContentEntityFetcherImpl;
import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.core.content.index.ContentVersionEntityFetcherImpl;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.MenuItemDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentSpecification;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.ContentVersionSpecification;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.content.index.AggregatedQuery;
import com.enonic.cms.domain.content.index.ContentIndexQuery;
import com.enonic.cms.domain.content.index.IndexValueQuery;
import com.enonic.cms.domain.content.query.AbstractContentArchiveQuery;
import com.enonic.cms.domain.content.query.ContentByCategoryQuery;
import com.enonic.cms.domain.content.query.ContentByContentQuery;
import com.enonic.cms.domain.content.query.ContentByQueryQuery;
import com.enonic.cms.domain.content.query.ContentBySectionQuery;
import com.enonic.cms.domain.content.query.OpenContentQuery;
import com.enonic.cms.domain.content.query.RelatedChildrenContentQuery;
import com.enonic.cms.domain.content.query.RelatedContentQuery;
import com.enonic.cms.domain.content.resultset.ContentResultSet;
import com.enonic.cms.domain.content.resultset.ContentResultSetLazyFetcher;
import com.enonic.cms.domain.content.resultset.ContentResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.ContentVersionResultSet;
import com.enonic.cms.domain.content.resultset.ContentVersionResultSetLazyFetcher;
import com.enonic.cms.domain.content.resultset.ContentVersionResultSetNonLazy;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSet;
import com.enonic.cms.domain.content.resultset.RelatedContentResultSetImpl;
import com.enonic.cms.domain.log.LogType;
import com.enonic.cms.domain.log.StoreNewLogEntryCommand;
import com.enonic.cms.domain.log.Table;
import com.enonic.cms.domain.security.group.GroupKey;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserKey;
import com.enonic.cms.domain.structure.menuitem.MenuItemEntity;
import com.enonic.cms.domain.structure.menuitem.MenuItemKey;

public class ContentServiceImpl
    implements ContentService
{
    private static final int TIMEOUT_24HOURS = 86400;

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private MenuItemDao menuItemDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private ContentSecurityFilterResolver contentSecurityFilterResolver;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    private ContentStorer contentStorer;

    @Autowired
    private LogService logService;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public SnapshotContentResult snapshotContent( SnapshotContentCommand command )
    {
        try
        {
            return contentStorer.snapshotContent( command );
        }
        catch ( RuntimeException e )
        {
            throw new SnapshotContentException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public AssignContentResult assignContent( AssignContentCommand command )
    {
        try
        {
            return contentStorer.assignContent( command );
        }
        catch ( RuntimeException e )
        {
            throw new AssignContentException( e );
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateAssignment( UpdateAssignmentCommand command )
    {
        try
        {
            contentStorer.updateAssignment( command );
        }
        catch ( RuntimeException e )
        {
            throw new UpdateAssignmentException( e );
        }
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UnassignContentResult unassignContent( UnassignContentCommand command )
    {
        try
        {
            return contentStorer.unassignContent( command );
        }
        catch ( RuntimeException e )
        {
            throw new UnassignContentException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ContentKey createContent( CreateContentCommand command )
    {
        try
        {
            ContentEntity content = contentStorer.createContent( command );
            logEvent( command.getCreator(), content, LogType.ENTITY_CREATED );
            return content.getKey();
        }
        catch ( RuntimeException e )
        {
            throw new CreateContentException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UpdateContentResult updateContent( UpdateContentCommand command )
    {
        try
        {
            final UpdateContentResult updateContentResult = contentStorer.updateContent( command );

            if ( updateContentResult.isAnyChangesMade() )
            {
                logEvent( command.getModifier(), updateContentResult.getTargetedVersion().getContent(), LogType.ENTITY_UPDATED );
            }

            return updateContentResult;

        }
        catch ( RuntimeException e )
        {
            throw new UpdateContentException( e );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteContent( UserEntity deleter, ContentEntity content )
    {
        if ( content == null )
        {
            throw new IllegalArgumentException( "Given content cannot be null" );
        }
        if ( deleter == null )
        {
            throw new IllegalArgumentException( "Given deleter cannot be null" );
        }
        if ( content.hasDirectMenuItemPlacements() )
        {
            throw new RuntimeException( "Cannot delete content because it is in use by a page" );
        }

        contentStorer.deleteContent( deleter, content );
        logEvent( deleter.getKey(), content, LogType.ENTITY_REMOVED );

    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteVersion( UserEntity deleter, final ContentVersionKey contentVersionKey )
    {
        if ( deleter == null )
        {
            throw new IllegalArgumentException( "Given deleter cannot be null" );
        }

        ContentVersionEntity version = contentVersionDao.findByKey( contentVersionKey );

        if ( version == null )
        {
            throw new IllegalArgumentException( "Content version does not exists: " + contentVersionKey );
        }

        if ( version.isMainVersion() )
        {
            throw new IllegalArgumentException( "Cannot delete main version of a content" );
        }

        if ( version.getStatus() == ContentStatus.APPROVED )
        {
            throw new IllegalArgumentException( "Cannot delete the approved version of a content" );
        }

        if ( version.getStatus() == ContentStatus.ARCHIVED )
        {
            throw new IllegalArgumentException( "Cannot delete an archived version of a content" );
        }

        if ( version.getContent().getVersionCount() == 1 )
        {
            throw new IllegalArgumentException( "Cannot delete the last version of a content" );
        }

        contentStorer.deleteVersion( deleter, version );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean archiveContent( final UserEntity archiver, final ContentEntity content )
    {
        if ( content == null )
        {
            throw new IllegalArgumentException( "Given content cannot be null" );
        }
        if ( archiver == null )
        {
            throw new IllegalArgumentException( "Given archiver cannot be null" );
        }

        boolean updated = contentStorer.archiveMainVersion( archiver, content );
        if ( updated )
        {
            logEvent( archiver.getKey(), content, LogType.ENTITY_UPDATED );
        }

        return updated;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean approveContent( final UserEntity approver, final ContentEntity content )
    {

        if ( content == null )
        {
            throw new IllegalArgumentException( "Given content cannot be null" );
        }
        if ( approver == null )
        {
            throw new IllegalArgumentException( "Given approver cannot be null" );
        }

        boolean updated = contentStorer.approveMainVersion( approver, content );
        if ( updated )
        {
            logEvent( approver.getKey(), content, LogType.ENTITY_UPDATED );
        }

        return updated;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = TIMEOUT_24HOURS)
    public void deleteByCategory( UserEntity deleter, CategoryEntity category )
    {
        List<ContentEntity> deletedContents = contentStorer.deleteByCategory( deleter, category );

        for ( ContentEntity deletedContent : deletedContents )
        {
            logEvent( deleter.getKey(), deletedContent, LogType.ENTITY_REMOVED );
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void moveContent( UserEntity mover, ContentEntity content, CategoryEntity toCategory )
    {
        contentStorer.moveContent( mover, content, toCategory );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ContentKey copyContent( UserEntity copier, ContentEntity content, CategoryEntity toCategory )
    {
        return contentStorer.copyContent( copier, content, toCategory );
    }

    public ContentResultSet queryContent( ContentBySectionQuery spec )
    {
        spec.validate();

        Collection<MenuItemEntity> sections = doGetSectionKeysByMenuItems( spec.getMenuItemKeys(), spec.getLevels() );

        Collection<GroupKey> securityFilter = contentSecurityFilterResolver.resolveGroupKeys( spec.getUser() );
        ContentIndexQuery query = spec.createAndSetupContentQuery( sections, securityFilter );
        return contentIndexService.query( query );
    }

    public ContentResultSet queryContent( OpenContentQuery query )
    {
        return doQueryContent( query );
    }

    public ContentResultSet queryContent( ContentByQueryQuery query )
    {
        return doQueryContent( query );
    }

    public ContentResultSet queryContent( ContentByCategoryQuery query )
    {
        return doQueryContent( query );
    }

    public ContentResultSet queryContent( ContentByContentQuery query )
    {
        return doQueryContent( query );
    }

    private ContentResultSet doQueryContent( AbstractContentArchiveQuery spec )
    {
        spec.validate();

        Set<CategoryKey> allCategories = null;
        if ( spec.useCategoryKeyFilter() )
        {
            allCategories = new HashSet<CategoryKey>();
            if ( spec.getLevels() == 0 )
            {
                fillInSubCategories( allCategories, spec.getCategoryKeyFilter(), Integer.MAX_VALUE );
            }
            else if ( spec.getLevels() > 0 )
            {
                fillInSubCategories( allCategories, spec.getCategoryKeyFilter(), spec.getLevels() - 1 );
            }
            allCategories.addAll( spec.getCategoryKeyFilter() );
        }

        Collection<GroupKey> securityFilter = contentSecurityFilterResolver.resolveGroupKeys( spec.getUser() );
        ContentIndexQuery query = spec.createAndSetupContentQuery( allCategories, securityFilter );

        return contentIndexService.query( query );
    }

    public ContentResultSet getContent( ContentSpecification specification, String orderByCol, int count, int index )
    {
        List<ContentKey> contentKeys = contentDao.findBySpecification( specification, orderByCol, count + index );

        final int totalCount = contentDao.findCountBySpecification( specification );

        final int queryResultTotalSize = contentKeys.size();

        if ( index > queryResultTotalSize )
        {
            return (ContentResultSet) new ContentResultSetNonLazy( index ).addError(
                "Index greater than result count: " + index + " greater than " + queryResultTotalSize );
        }

        int fromIndex = Math.max( index, 0 );
        int toIndex = Math.min( queryResultTotalSize, fromIndex + count );
        final List<ContentKey> actualKeysWanted = contentKeys.subList( fromIndex, toIndex );

        return new ContentResultSetLazyFetcher( new ContentEntityFetcherImpl( contentDao ), actualKeysWanted, index, totalCount );
    }

    public RelatedContentResultSet queryRelatedContent( RelatedContentQuery spec )
    {
        RelatedContentFetcher fetcher = new RelatedContentFetcher( contentDao, new ContentAccessResolver( groupDao ) );
        fetcher.setRunningUser( spec.getUser() );
        fetcher.setMaxChildrenLevel( spec.getChildrenLevel() );
        fetcher.setMaxParentLevel( spec.getParentLevel() );
        fetcher.setMaxParentChildrenLevel( spec.getParentChildrenLevel() );
        fetcher.setIncludeOnlyMainVersions( spec.includeOnlyMainVersions() );
        fetcher.setAvailableCheckDate( spec.getOnlineCheckDate() );
        fetcher.setIncludeOfflineContent( !spec.isFilterContentOnline() );

        return fetcher.fetch( spec.getContentResultSet() );
    }

    public RelatedContentResultSet queryRelatedContent( RelatedChildrenContentQuery spec )
    {
        RelatedContentFetcherForContentVersion fetcher =
            new RelatedContentFetcherForContentVersion( contentDao, new ContentAccessResolver( groupDao ) );
        fetcher.setRunningUser( spec.getUser() );
        fetcher.setAvailableCheckDate( spec.getOnlineCheckDate() );
        fetcher.setMaxChildrenLevel( spec.getChildrenLevel() );
        fetcher.setIncludeOfflineContent( !spec.isOnline() );

        return fetcher.fetch( spec.getContentVersions() );
    }

    public RelatedContentResultSet getRelatedContentRequiresAll( UserEntity user, int relation, ContentResultSet contents )
    {
        int parentLevel = relation > 0 ? 0 : 1;
        int childrenLevel = relation > 0 ? 1 : 0;

        RelatedContentFetcher relatedContentFetcher = new RelatedContentFetcher( contentDao, new ContentAccessResolver( groupDao ) );
        relatedContentFetcher.setRunningUser( user );
        relatedContentFetcher.setAvailableCheckDate( new Date() );
        relatedContentFetcher.setMaxChildrenLevel( childrenLevel );
        relatedContentFetcher.setMaxParentLevel( parentLevel );
        relatedContentFetcher.setMaxParentChildrenLevel( 0 );

        Set<ContentKey> relatedContentIntersectionSet = new HashSet<ContentKey>();
        final RelatedContentResultSet firstRelatedContentSet = relatedContentFetcher.fetch( contents.getContent( 0 ) );
        relatedContentIntersectionSet.addAll( firstRelatedContentSet.getContentKeys() );

        for ( int i = 1; i < contents.getLength(); i++ )
        {
            final RelatedContentResultSet otherRelatedContent = relatedContentFetcher.fetch( contents.getContent( i ) );
            relatedContentIntersectionSet.retainAll( otherRelatedContent.getContentKeys() );

            if ( relatedContentIntersectionSet.size() == 0 )
            {
                break;
            }
        }
        RelatedContentResultSetImpl relatedContent = new RelatedContentResultSetImpl();
        for ( ContentKey contentKey : relatedContentIntersectionSet )
        {
            relatedContent.add( firstRelatedContentSet.getRelatedContent( contentKey ) );
        }
        return relatedContent;
    }

    public ContentResultSet getPageContent( int menuItemKey )
    {
        MenuItemEntity menuItem = menuItemDao.findByKey( menuItemKey );
        List<ContentEntity> resultList = new ArrayList<ContentEntity>();
        if ( menuItem != null )
        {
            ContentEntity pageContent = menuItem.getContent();
            if ( pageContent != null )
            {
                resultList.add( pageContent );
            }
        }
        return new ContentResultSetNonLazy( resultList, 0, resultList.size() );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getAggregatedIndexValues( UserEntity user, String field, Collection<CategoryKey> categoryFilter,
                                                 boolean includeSubCategories, Collection<ContentTypeKey> contentTypeFilter )
    {
        Set<CategoryKey> categoryKeySet = new HashSet<CategoryKey>();
        categoryKeySet.addAll( categoryFilter );
        if ( includeSubCategories )
        {
            fillInSubCategories( categoryKeySet, categoryFilter, Integer.MAX_VALUE );
        }

        Collection<GroupKey> userGroups = user.isEnterpriseAdmin() ? null : user.getAllMembershipsGroupKeys();
        AggregatedQuery query = new AggregatedQuery( field );
        query.setCategoryFilter( categoryKeySet );
        query.setSecurityFilter( userGroups );
        query.setContentTypeFilter( contentTypeFilter );

        ContentIndexValuesXMLCreator xmlCreator = new ContentIndexValuesXMLCreator();
        return xmlCreator.createIndexValuesDocument( field, contentIndexService.query( query ) );
    }

    /**
     * @inheritDoc
     */
    public XMLDocument getIndexValues( UserEntity user, String field, Collection<CategoryKey> categoryFilter, boolean includeSubCategories,
                                       Collection<ContentTypeKey> contentTypeFilter, int index, int count, boolean descOrder )
    {
        Set<CategoryKey> categoryKeySet = new HashSet<CategoryKey>();
        categoryKeySet.addAll( categoryFilter );
        if ( includeSubCategories )
        {
            fillInSubCategories( categoryKeySet, categoryFilter, Integer.MAX_VALUE );
        }

        Collection<GroupKey> userGroups = user.isEnterpriseAdmin() ? null : user.getAllMembershipsGroupKeys();
        IndexValueQuery query = new IndexValueQuery( field );
        query.setCategoryFilter( categoryKeySet );
        query.setSecurityFilter( userGroups );
        query.setContentTypeFilter( contentTypeFilter );
        query.setIndex( index );
        query.setCount( count );
        query.setDescOrder( descOrder );

        ContentIndexValuesXMLCreator xmlCreator = new ContentIndexValuesXMLCreator();
        return xmlCreator.createIndexValuesDocument( field, contentIndexService.query( query ) );
    }

    private void fillInSubCategories( Set<CategoryKey> subCategories, Collection<CategoryKey> categoryKeys, int levels )
    {

        if ( ( categoryKeys != null ) && ( categoryKeys.size() > 0 ) && levels > 0 )
        {

            for ( CategoryKey categoryKey : categoryKeys )
            {
                CategoryEntity category = categoryDao.findByKey( categoryKey );
                if ( category == null )
                {
                    continue;
                }
                List<CategoryKey> childrenKeys = category.getChildrenKeys();
                fillInSubCategories( subCategories, childrenKeys, levels - 1 );
                subCategories.addAll( childrenKeys );
            }
        }
    }

    private Set<MenuItemEntity> doGetSectionKeysByMenuItems( Collection<MenuItemKey> menuItemKeys, int levels )
    {

        Set<MenuItemEntity> sections = new HashSet<MenuItemEntity>();
        if ( ( levels >= 0 ) && ( menuItemKeys != null ) && ( menuItemKeys.size() > 0 ) )
        {
            if ( levels == 0 )
            {
                levels = Integer.MAX_VALUE;
            }

            List<MenuItemEntity> menuItems = menuItemDao.findByKeys( menuItemKeys );

            for ( MenuItemEntity menuItem : menuItems )
            {
                if ( menuItem.isSection() )
                {
                    sections.add( menuItem );
                }
                Collection<MenuItemEntity> descendantSections = menuItem.getDescendantSections( levels - 1 );
                for ( MenuItemEntity section : descendantSections )
                {
                    sections.add( section );
                }
            }
        }
        return sections;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public List<ContentKey> findContentKeysByContentType( ContentTypeEntity contentType )
    {
        return contentDao.findContentKeysByContentType( contentType );
    }

    public List<ContentTypeEntity> getAllContentTypes()
    {
        return contentTypeDao.getAll();
    }

    public boolean isContentInUse( ContentKey contentKey )
    {
        List<ContentKey> contentKeys = new ArrayList<ContentKey>();
        contentKeys.add( contentKey );

        return isContentInUse( contentKeys );
    }

    public boolean isContentInUse( List<ContentKey> contentKeys )
    {
        return doIsContentInUse( contentKeys );
    }

    private boolean doIsContentInUse( List<ContentKey> contentKeys )
    {
        int numberOfRelatedParents = contentDao.getNumberOfRelatedParentsByKey( contentKeys );

        if ( numberOfRelatedParents > 0 )
        {
            return true;
        }

        for ( ContentKey contentKey : contentKeys )
        {

            ContentEntity contentEntity = contentDao.findByKey( contentKey );

            if ( contentEntity.hasDirectMenuItemPlacements() )
            {
                return true;
            }
        }

        return false;
    }

    public void setContentStorer( ContentStorer value )
    {
        this.contentStorer = value;
    }

    public ContentVersionResultSet getContentVersions( ContentVersionSpecification specification, String orderByCol, int count, int index )
    {

        if ( specification.getUser() != null )
        {
            Collection<GroupKey> securityFilter = contentSecurityFilterResolver.resolveGroupKeys( specification.getUser() );
            specification.setSecurityFilter( securityFilter );
        }
        List<ContentVersionKey> versionKeys = contentVersionDao.findBySpecification( specification, orderByCol, count + index );

        final int totalCount = contentVersionDao.findCountBySpecification( specification );

        final int queryResultTotalSize = versionKeys.size();

        if ( index > queryResultTotalSize )
        {
            return (ContentVersionResultSet) new ContentVersionResultSetNonLazy( index ).addError(
                "Index greater than result count: " + index + " greater than " + queryResultTotalSize );
        }

        int fromIndex = Math.max( index, 0 );
        int toIndex = Math.min( queryResultTotalSize, fromIndex + count );
        final List<ContentVersionKey> actualKeysWanted = versionKeys.subList( fromIndex, toIndex );

        return new ContentVersionResultSetLazyFetcher( new ContentVersionEntityFetcherImpl( contentVersionDao ), actualKeysWanted, index,
                                                       totalCount );
    }

    private void logEvent( UserKey actor, ContentEntity content, LogType type )
    {
        String title = content.getMainVersion().getTitle() + " (" + content.getKey().toInt() + ")";
        StoreNewLogEntryCommand command = new StoreNewLogEntryCommand();
        command.setUser( actor );
        command.setTableKeyValue( content.getKey().toInt() );
        command.setTableKey( Table.CONTENT );
        command.setType( type );
        command.setTitle( title );
        command.setPath( content.getPathAsString() );
        command.setXmlData( content.getMainVersion().getContentDataAsJDomDocument() );

        logService.storeNew( command );
    }

}

