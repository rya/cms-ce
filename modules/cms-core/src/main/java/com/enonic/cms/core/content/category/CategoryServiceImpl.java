/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.api.util.Preconditions;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.ContentTitleValidator;
import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessRights;
import com.enonic.cms.core.content.category.access.CreateCategoryAccessException;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.ContentTypeKey;
import com.enonic.cms.core.log.LogService;
import com.enonic.cms.core.log.LogType;
import com.enonic.cms.core.log.StoreNewLogEntryCommand;
import com.enonic.cms.core.log.Table;
import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.group.GroupKey;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.core.time.TimeService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UnitDao;
import com.enonic.cms.store.dao.UserDao;

/**
 * Mar 9, 2010
 */
public class CategoryServiceImpl
    implements CategoryService
{
    private static final int TIMEOUT_24HOURS = 86400;

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private UnitDao unitDao;

    private TimeService timeService;

    private KeyService keyService;

    @Autowired
    private ContentStorer contentStorer;

    @Autowired
    private LogService logService;

    /**
     * This method is currently only used by the Client API.
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public CategoryKey storeNewCategory( final StoreNewCategoryCommand command )
    {
        Preconditions.checkNotNull( command.getCreator(), "creator in command must be specified" );
        final UserEntity creator = resolveUser( command.getCreator(), "creator" );

        final CategoryEntity parentCategory = resolveCategory( command.getParentCategory() );
        checkCreateCategoryAccess( parentCategory, creator );

        final ContentTypeEntity contentType = resolveContentType( command.getContentType() );

        final CategoryKey key = new CategoryKey( keyService.generateNextKeySafe( "tCategory" ) );
        final Date now = timeService.getNowAsDateTime().toDate();
        final CategoryEntity category = new CategoryEntity();
        category.setKey( key );
        category.setTimestamp( now );
        category.setContentType( contentType );
        category.setCreated( now );
        category.setDeleted( false );
        category.setModifier( creator );
        category.setName( command.getName() );
        category.setOwner( creator );
        category.setAutoMakeAvailable( command.getAutoApprove() );

        if ( parentCategory != null )
        {
            category.setParent( parentCategory );
            category.setUnit( parentCategory.getUnitExcludeDeleted() );
            parentCategory.addChild( category );
        }
        else
        {
            Preconditions.checkNotNull( command.getUnitKey(), "command must specify a unit when creating a top category" );
            UnitEntity unit = unitDao.findByKey( command.getUnitKey().toInt() );
            Preconditions.checkNotNull( unit, "specified unit does not exist: " + command.getUnitKey() );
            category.setUnit( unit );
        }

        addAccessRightsToCategory( command, parentCategory, category );
        categoryDao.storeNew( category );
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

        final UserEntity deleter = resolveUser( command.getDeleter(), "deleter" );
        final CategoryEntity categoryToDelete = resolveCategory( command.getCategoryKey() );

        DeleteCategoryCommandProcessor processor =
            new DeleteCategoryCommandProcessor( deleter, groupDao, contentDao, categoryDao, contentStorer, command );
        processor.deleteCategory( categoryToDelete );

        for ( ContentEntity deletedContent : processor.getDeletedContent() )
        {
            logEvent( deleter.getKey(), deletedContent, LogType.ENTITY_REMOVED );
        }
    }

    private void checkCreateCategoryAccess( CategoryEntity parentCategory, UserEntity creator )
        throws CreateCategoryAccessException
    {
        final CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        if ( parentCategory == null )
        {
            // needs at least administrator rights
            if ( !memberOfResolver.hasAdministratorPowers( creator ) )
            {
                throw new CreateCategoryAccessException( "To create a top category the user needs to be an administrator",
                                                         creator.getQualifiedName() );
            }
        }
        else
        {
            final boolean noAdministrateAccessByRights =
                !categoryAccessResolver.hasAccess( creator, parentCategory, CategoryAccessType.ADMINISTRATE );

            if ( noAdministrateAccessByRights )
            {
                if ( !memberOfResolver.isMemberOfAdministratorsGroup( creator ) )
                {
                    throw new CreateCategoryAccessException(
                        "To create a category the user needs to have the administrate access on the parent category or be an administrator",
                        creator.getQualifiedName() );


                }
            }
        }
    }

    private void addAccessRightsToCategory( StoreNewCategoryCommand command, CategoryEntity parentCategory, CategoryEntity category )
    {
        if ( command.getAccessRights() == null && parentCategory != null )
        {
            Map<GroupKey, CategoryAccessEntity> accessRights = parentCategory.getAccessRights();
            for ( GroupKey group : accessRights.keySet() )
            {
                CategoryAccessEntity parentAccessRight = accessRights.get( group );
                CategoryAccessEntity accessRight = new CategoryAccessEntity();
                accessRight.setKey( new CategoryAccessKey( category.getKey(), group ) );
                accessRight.setGroup( parentAccessRight.getGroup() );
                accessRight.setAdminAccess( parentAccessRight.isAdminAccess() );
                accessRight.setAdminBrowseAccess( parentAccessRight.isAdminBrowseAccess() );
                accessRight.setCreateAccess( parentAccessRight.isCreateAccess() );
                accessRight.setPublishAccess( parentAccessRight.isPublishAccess() );
                accessRight.setReadAccess( parentAccessRight.isReadAccess() );
                category.addAccessRight( accessRight );
            }
        }
        else if ( command.getAccessRights() != null )
        {
            for ( CategoryAccessRights aRight : command.getAccessRights() )
            {
                CategoryAccessEntity accessRight = new CategoryAccessEntity();
                accessRight.setKey( new CategoryAccessKey( category.getKey(), aRight.getGroupKey() ) );
                accessRight.setGroup( groupDao.findByKey( aRight.getGroupKey() ) );
                accessRight.setAdminAccess( aRight.isAdminAccess() );
                accessRight.setAdminBrowseAccess( aRight.isAdminBrowseAccess() );
                accessRight.setCreateAccess( aRight.isCreateAccess() );
                accessRight.setPublishAccess( aRight.isPublishAccess() );
                accessRight.setReadAccess( aRight.isReadAccess() );
                category.addAccessRight( accessRight );
            }
        }

        ensureAccessRightForAdministratorGroup( category );
    }

    private void ensureAccessRightForAdministratorGroup( CategoryEntity category )
    {
        final GroupEntity administrator = groupDao.findBuiltInAdministrator();
        if ( category.getAccessRights() == null || category.getAccessRights().isEmpty() )
        {
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
            accessRight.setGroup( administrator );
            setAllRightsToTrue( accessRight );
            category.addAccessRight( accessRight );
        }
        else if ( category.getAccessRights().size() > 0 )
        {
            boolean isAdministratorAccessRightsExist = false;
            for ( CategoryAccessEntity categoryAccess : category.getAccessRights().values() )
            {
                if ( categoryAccess.getKey().getGroupKey().equals( administrator.getGroupKey() ) )
                {
                    setAllRightsToTrue( categoryAccess );
                    isAdministratorAccessRightsExist = true;
                }
            }
            if ( !isAdministratorAccessRightsExist )
            {
                CategoryAccessEntity accessRight = new CategoryAccessEntity();
                accessRight.setGroup( administrator );
                accessRight.setKey( new CategoryAccessKey( category.getKey(), administrator.getGroupKey() ) );
                setAllRightsToTrue( accessRight );
                category.addAccessRight( accessRight );
            }
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

    private UserEntity resolveUser( UserKey key, String subject )
    {
        if ( key != null )
        {
            UserEntity user = userDao.findByKey( key );
            Preconditions.checkNotNull( user, "given " + subject + " does not exist: " + key );
            return user;
        }
        return null;
    }

    private CategoryEntity resolveCategory( CategoryKey key )
    {
        if ( key != null )
        {
            CategoryEntity category = categoryDao.findByKey( key );
            Preconditions.checkNotNull( category, "given category does not exist: " + key );
            return category;
        }

        return null;
    }

    private ContentTypeEntity resolveContentType( ContentTypeKey key )
    {
        if ( key != null )
        {
            ContentTypeEntity contentType = contentTypeDao.findByKey( key.toInt() );
            Preconditions.checkNotNull( contentType, "given content type does not exist: " + key );
            return contentType;
        }

        return null;
    }

    private void setAllRightsToTrue( CategoryAccessEntity accessRight )
    {
        accessRight.setAdminAccess( true );
        accessRight.setAdminBrowseAccess( true );
        accessRight.setCreateAccess( true );
        accessRight.setPublishAccess( true );
        accessRight.setReadAccess( true );
    }

    public void setTimeService( TimeService timeService )
    {
        this.timeService = timeService;
    }

    public void setKeyService( KeyService keyService )
    {
        this.keyService = keyService;
    }
}
