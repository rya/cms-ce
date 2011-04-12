/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.category;

import java.util.Date;
import java.util.Map;

import com.enonic.cms.core.security.group.GroupKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.security.userstore.MemberOfResolver;
import com.enonic.cms.core.service.KeyService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.security.user.UserEntity;

/**
 * Mar 9, 2010
 */
public class CategoryServiceImpl
    implements CategoryService
{

    @Autowired
    private MemberOfResolver memberOfResolver;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    private TimeService timeService;

    private KeyService keyService;

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

        CategoryKey key = new CategoryKey( keyService.generateNextKeySafe( "tCategory" ) );

        CategoryEntity category = new CategoryEntity();
        category.setKey( key );
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

        Map<GroupKey, CategoryAccessEntity> accessRights = parentCategory.getAccessRights();
        for ( GroupKey group : accessRights.keySet() )
        {
            CategoryAccessEntity parentAccessRight = accessRights.get( group );
            CategoryAccessEntity accessRight = new CategoryAccessEntity();
            accessRight.setKey( new CategoryAccessKey( key, group ) );
            accessRight.setAdminAccess( parentAccessRight.isAdminAccess() );
            accessRight.setAdminBrowseAccess( parentAccessRight.isAdminBrowseAccess() );
            accessRight.setCreateAccess( parentAccessRight.isCreateAccess() );
            accessRight.setPublishAccess( parentAccessRight.isPublishAccess() );
            accessRight.setReadAccess( parentAccessRight.isReadAccess() );
            category.addAccessRight( accessRight );
        }

        parentCategory.addChild( category );

        categoryDao.storeNew( category );

        return category.getKey();
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
