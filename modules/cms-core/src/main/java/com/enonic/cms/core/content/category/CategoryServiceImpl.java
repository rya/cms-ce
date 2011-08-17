/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.ContentTitleValidator;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.resolver.CategoryAccessResolver;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

/**
 * Mar 9, 2010
 */
public class CategoryServiceImpl
    implements CategoryService
{
    private static final int TIMEOUT_24HOURS = 86400;

    @Inject
    private MemberOfResolver memberOfResolver;

    @Inject
    private UserDao userDao;

    @Inject
    private GroupDao groupDao;

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private ContentDao contentDao;

    @Inject
    private ContentTypeDao contentTypeDao;

    private TimeService timeService;

    @Inject
    private ContentStorer contentStorer;

    @Inject
    private LogService logService;

    /**
     * This method is currently only used by the Client API.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoryKey storeNewCategory( StoreNewCategoryCommand command )
    {
        UserEntity creator = userDao.findByKey( command.getCreator() );

        final CategoryKey parentCategoryKey = command.getParentCategory();
        if ( parentCategoryKey == null )
        {
            throw new UnsupportedOperationException( "Creating a top category is currently not supported by this method" );
        }

        CategoryEntity parentCategory = categoryDao.findByKey( parentCategoryKey );

        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        final boolean noAdministrateAccessByRights =
            !categoryAccessResolver.hasAccess( creator, parentCategory, CategoryAccessType.ADMINISTRATE );

        if ( noAdministrateAccessByRights )
        {
            boolean isNotAdministrator = !memberOfResolver.isMemberOfAdministratorsGroup( creator );
            if ( isNotAdministrator )
            {
                throw new CategoryAccessException( "The currently logged in user does not have create access on the category",
                                                   creator.getQualifiedName(), CategoryAccessType.ADMINISTRATE, parentCategory.getKey() );
            }
        }

        Date timeStamp = timeService.getNowAsDateTime().toDate();
        ContentTypeEntity contentType = null;
        if ( command.getContentType() != null )
        {
            contentType = contentTypeDao.findByKey( command.getContentType().toInt() );
        }

        CategoryEntity category = new CategoryEntity();
        category.setContentType( contentType );
        category.setCreated( timeStamp );
        category.setDeleted( false );
        category.setModifier( creator );
        category.setName( command.getName() );
        category.setOwner( creator );
        category.setParent( parentCategory );
        category.setTimestamp( timeStamp );
        category.setUnit( parentCategory.getUnitExcludeDeleted() );
        category.setAutoMakeAvailable( command.getAutoApprove() );

        parentCategory.addChild( category );
        categoryDao.storeNew( category );

        Map<GroupKey, CategoryAccessEntity> accessRights = parentCategory.getAccessRights();
        for ( GroupKey group : accessRights.keySet() )
        {
            CategoryAccessEntity parentAccessRight = accessRights.get( group );
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( category.getKey(), group ) );
            accessRight.setAdminAccess( parentAccessRight.isAdminAccess() );
            accessRight.setAdminBrowseAccess( parentAccessRight.isAdminBrowseAccess() );
            accessRight.setCreateAccess( parentAccessRight.isCreateAccess() );
            accessRight.setPublishAccess( parentAccessRight.isPublishAccess() );
            accessRight.setReadAccess( parentAccessRight.isReadAccess() );
            category.addAccessRight( accessRight );
        }

        return category.getKey();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = TIMEOUT_24HOURS)
    public void deleteCategory( final DeleteCategoryCommand command )
    {
        try
        {
            doDeleteCategory( command );
        }
        catch ( RuntimeException e )
        {
            throw new DeleteCategoryException( e );
        }
    }

    private void doDeleteCategory( final DeleteCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getDeleter(), "deleter must be specified" );
        Preconditions.checkNotNull( command.getCategoryKey(), "categoryKey must be specified" );

        final UserEntity deleter = userDao.findByKey( command.getDeleter() );
        Preconditions.checkNotNull( deleter, "Given deleter does not exist, userKey:" + command.getDeleter() );

        final CategoryEntity categoryToDelete = categoryDao.findByKey( command.getCategoryKey() );
        Preconditions.checkNotNull( categoryToDelete, "Given category does not exist, categoryKey:" + command.getCategoryKey() );

        DeleteCategoryCommandProcessor processor =
            new DeleteCategoryCommandProcessor( deleter, groupDao, contentDao, categoryDao, contentStorer, command );
        processor.deleteCategory( categoryToDelete );

        for ( ContentEntity deletedContent : processor.getDeletedContent() )
        {
            logEvent( deleter.getKey(), deletedContent, LogType.ENTITY_REMOVED );
        }
    }

    private void logEvent( UserKey actor, ContentEntity content, LogType type )
    {
        String title = content.getMainVersion().getTitle();
        String titleKey = " (" + content.getKey().toInt() + ")";
        if ( title.length() + titleKey.length() > ContentTitleValidator.CONTENT_TITLE_MAX_LENGTH )
        {
            title = title.substring( 0, ContentTitleValidator.CONTENT_TITLE_MAX_LENGTH - titleKey.length() );
        }
        title = title + titleKey;
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

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }
}
