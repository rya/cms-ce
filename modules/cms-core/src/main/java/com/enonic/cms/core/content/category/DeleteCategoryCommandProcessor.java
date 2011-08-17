package com.enonic.cms.core.content.category;


import java.util.ArrayList;
import java.util.List;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.UnitEntity;
import com.enonic.cms.core.content.category.command.DeleteCategoryCommand;
import com.enonic.cms.core.resolver.CategoryAccessResolver;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;


class DeleteCategoryCommandProcessor
{
    private UserEntity deleter;

    private GroupDao groupDao;

    private ContentDao contentDao;

    private CategoryDao categoryDao;

    private ContentStorer contentStorer;

    private DeleteCategoryCommand command;

    private List<ContentEntity> deletedContent = new ArrayList<ContentEntity>();

    DeleteCategoryCommandProcessor( UserEntity deleter, GroupDao groupDao, ContentDao contentDao, CategoryDao categoryDao,
                                    ContentStorer contentStorer, DeleteCategoryCommand command )
    {
        this.deleter = deleter;
        this.groupDao = groupDao;
        this.contentDao = contentDao;
        this.categoryDao = categoryDao;
        this.contentStorer = contentStorer;
        this.command = command;
    }

    void deleteCategory( CategoryEntity categoryToDelete )
    {
        if ( !new CategoryAccessResolver( groupDao ).hasDeleteCategoryAccess( deleter, categoryToDelete ) )
        {
            throw new CategoryAccessException( "Cannot delete category", deleter.getQualifiedName(), CategoryAccessType.ADMINISTRATE,
                                               categoryToDelete.getKey() );
        }

        if ( !command.isRecursive() && categoryDao.countChildrenByCategory( categoryToDelete ) > 0 )
        {
            throw new IllegalArgumentException( "Category [" + categoryToDelete.getPathAsString() +
                                                    "] contains categories. Deleting a category that contains categories is not allowed when recursive flag is false." );
        }

        if ( command.isRecursive() )
        {
            doDeleteRecursively( categoryToDelete );
        }
        else
        {
            doDeleteCategory( categoryToDelete );
        }
    }

    private void doDeleteRecursively( CategoryEntity category )
    {
        // delete "leaf nodes" first...
        for ( CategoryEntity childCategory : category.getChildren() )
        {
            doDeleteRecursively( childCategory );
        }

        // if category contains content it cannot be deleted unless includeContent is true
        if ( !command.isIncludeContent() )
        {
            if ( contentDao.countContentByCategory( category ) > 0 )
            {
                throw new IllegalArgumentException( "Category [" + category.getPathAsString() +
                                                        "] contains content. Deleting a category that contains content is not allowed when includeContent is false." );
            }
        }

        doDeleteCategory( category );
    }

    private void doDeleteCategory( CategoryEntity category )
    {
        // delete content
        if ( command.isIncludeContent() )
        {
            deletedContent.addAll( contentStorer.deleteByCategory( deleter, category ) );
        }

        categoryDao.deleteCategory( category );

        if ( category.getParent() == null )
        {
            // delete unit if top category
            UnitEntity unitToDelete = category.getUnit();
            if ( unitToDelete != null )
            {
                unitToDelete.setDeleted( true );
                unitToDelete.removeContentTypes();
            }
        }
    }

    public List<ContentEntity> getDeletedContent()
    {
        return deletedContent;
    }
}
