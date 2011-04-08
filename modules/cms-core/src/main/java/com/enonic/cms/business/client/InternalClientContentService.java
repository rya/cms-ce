/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.ContentDataInputUpdateStrategy;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.DeleteContentParams;
import com.enonic.cms.api.client.model.SnapshotContentParams;
import com.enonic.cms.api.client.model.UnassignContentParams;
import com.enonic.cms.api.client.model.UpdateContentParams;
import com.enonic.cms.api.client.model.UpdateFileContentParams;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.SnapshotContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.security.UserParser;
import com.enonic.cms.portal.cache.PageCacheService;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.core.content.category.CategoryService;

import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand.UpdateStrategy;
import com.enonic.cms.core.security.SecurityService;

import com.enonic.cms.portal.cache.SiteCachesService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentLocation;
import com.enonic.cms.domain.content.ContentLocationSpecification;
import com.enonic.cms.domain.content.ContentLocations;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.category.CategoryAccessType;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.category.StoreNewCategoryCommand;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.security.user.UserEntity;


public class InternalClientContentService
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private GroupDao groupDao;

    private SiteCachesService siteCachesService;

    private FileContentdataResolver fileContentResolver = new FileContentdataResolver();

    @Autowired
    private CategoryService categoryService;

    private UserParser userParser;

    public int createCategory( CreateCategoryParams params )
    {
        if ( params.contentTypeKey != null && params.contentTypeName != null )
        {
            throw new ClientException( "Specify content type by either key or name, not both" );
        }

        if ( params.parentCategoryKey == null )
        {
            throw new ClientException( "The parent category must be set" );
        }
        CategoryEntity parentCategory = categoryDao.findByKey( new CategoryKey( params.parentCategoryKey ) );
        if ( parentCategory == null )
        {
            throw new ClientException( "The parent category does not exist" );
        }

        UserEntity runningUser = securityService.getRunAsUser();

        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        if ( !categoryAccessResolver.hasAccess( runningUser, parentCategory, CategoryAccessType.ADMINISTRATE ) )
        {
            throw new ClientException( "The currently logged in user does not have create access on the category" );
        }

        ContentTypeKey contentTypeKey = null;
        if ( params.contentTypeKey != null )
        {
            contentTypeKey = new ContentTypeKey( params.contentTypeKey );
        }
        else if ( params.contentTypeName != null )
        {
            ContentTypeEntity contentType = contentTypeDao.findByName( params.contentTypeName );
            if ( contentType == null )
            {
                throw new ClientException( "Specified content type by name '" + params.contentTypeName + "' does not exist" );
            }
            contentTypeKey = contentType.getContentTypeKey();
        }

        final StoreNewCategoryCommand command = new StoreNewCategoryCommand();
        command.setCreator( runningUser.getKey() );
        command.setName( params.name );
        command.setParentCategory( new CategoryKey( params.parentCategoryKey ) );
        command.setContentType( contentTypeKey );
        command.setAutoApprove( params.autoApprove );
        final CategoryKey newCategoryKey = categoryService.storeNewCategory( command );
        return newCategoryKey.toInt();
    }


    public void deleteContent( DeleteContentParams params )
    {
        UserEntity runningUser = securityService.getRunAsUser();

        ContentEntity content = contentDao.findByKey( new ContentKey( params.contentKey ) );

        if ( content != null && !content.isDeleted() )
        {
            ContentLocationSpecification contentLocationSpecification = new ContentLocationSpecification();
            contentLocationSpecification.setIncludeInactiveLocationsInSection( false );
            ContentLocations contentLocations = content.getLocations( contentLocationSpecification );

            contentService.deleteContent( runningUser, content );

            for ( ContentLocation contentLocation : contentLocations.getAllLocations() )
            {
                PageCacheService pageCacheService = siteCachesService.getPageCacheService( contentLocation.getSiteKey() );
                pageCacheService.removeEntriesByMenuItem( contentLocation.getMenuItemKey() );
            }
        }
    }

    public int createContent( CreateContentParams params )
        throws ClientException
    {
        final CategoryEntity category = validateCreateContentParams( params );

        final CreateContentCommand command = new CreateContentCommand();
        command.setPriority( 0 );
        command.setAvailableFrom( params.publishFrom );
        command.setAvailableTo( params.publishTo );
        command.setCategory( category );
        command.setLanguage( category.getUnitExcludeDeleted().getLanguage() );

        command.setStatus( ContentStatus.get( params.status ) );

        command.setChangeComment( params.changeComment );

        // content data
        final ContentTypeEntity contentType = category.getContentType();
        if ( contentType == null )
        {
            throw new IllegalArgumentException(
                "Unable to create content in category " + category.getKey() + ". Category has no contenttype set." );
        }

        final ContentDataResolver customContentResolver = new ContentDataResolver();

        final CustomContentData contentdata = customContentResolver.resolveContentdata( params.contentData, contentType );

        command.setContentData( contentdata );
        command.setContentName( PrettyPathNameCreator.generatePrettyPathName( contentdata.getTitle() ) );

        final List<BinaryDataAndBinary> binaryDatas = BinaryDataAndBinary.convertFromBinaryInputs( params.contentData.getBinaryInputs() );

        command.setBinaryDatas( binaryDatas );
        command.setUseCommandsBinaryDataToAdd( true );

        command.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        command.setCreator( securityService.getRunAsUser() );

        return contentService.createContent( command ).toInt();
    }

    public void assignContent( AssignContentParams params )
    {
        final UserEntity assigner = securityService.getRunAsUser();
        final UserEntity assignee = userParser.parseUser( params.assignee );
        final ContentKey contentToAssignOn = new ContentKey( params.contentKey );

        AssignContentCommand command = new AssignContentCommand();
        command.setContentKey( contentToAssignOn );
        command.setAssignerKey( assigner.getKey() );
        command.setAssigneeKey( assignee.getKey() );
        command.setAssignmentDueDate( params.assignmentDueDate );
        command.setAssignmentDescription( params.assignmentDescription );
        contentService.assignContent( command );
    }

    public void unassignContent( UnassignContentParams params )
    {
        final UserEntity unassigner = securityService.getRunAsUser();

        UnassignContentCommand command = new UnassignContentCommand();
        command.setContentKey( new ContentKey( params.contentKey ) );
        command.setUnassigner( unassigner.getKey() );
        contentService.unassignContent( command );
    }


    public void snapshotContent( SnapshotContentParams params )
    {
        final UserEntity modifier = securityService.getRunAsUser();

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setSnapshotComment( params.snapshotComment );
        snapshotCommand.setSnapshotterKey( modifier.getKey() );
        snapshotCommand.setContentKey( new ContentKey( params.contentKey ) );
        snapshotCommand.setClearCommentInDraft( params.clearCommentInDraft );

        contentService.snapshotContent( snapshotCommand );
    }

    public int updateContent( UpdateContentParams params )
    {
        validateUpdateContentParams( params );

        final ContentVersionKey contentVersionKey =
            resolveContentVersionKey( params.createNewVersion, params.contentKey, params.contentVersionKey );

        UpdateContentCommand command;
        if ( params.createNewVersion )
        {
            command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( contentVersionKey );
        }
        else
        {
            command = UpdateContentCommand.updateExistingVersion2( contentVersionKey );
        }

        command.setSyncRelatedContent( true );
        command.setSyncAccessRights( false );
        command.setModifier( securityService.getRunAsUser() );
        command.setUpdateAsMainVersion( params.setAsCurrentVersion );
        command.setContentKey( new ContentKey( params.contentKey ) );
        command.setAvailableFrom( params.publishFrom );
        command.setAvailableTo( params.publishTo );
        command.setStatus( ContentStatus.get( params.status ) );
        command.setUseCommandsBinaryDataToRemove( true );
        command.setUseCommandsBinaryDataToAdd( true );
        command.setChangeComment( params.changeComment );

        if ( params.contentData != null )
        {
            final ContentTypeEntity contentType = resolveContentType( params.contentKey );
            final ContentDataResolver customContentResolver = new ContentDataResolver();
            final CustomContentData newContentData = customContentResolver.resolveContentdata( params.contentData, contentType );
            command.setContentData( newContentData );
            if ( !params.createNewVersion )
            {
                // only delete previous binaries if we are overwriting current version
                final ContentVersionEntity persistedVersion = contentVersionDao.findByKey( contentVersionKey );
                final CustomContentData persistedContentData = (CustomContentData) persistedVersion.getContentData();
                final List<BinaryDataEntry> deletedBinaries = persistedContentData.getRemovedBinaryDataEntries( newContentData );
                command.setBinaryDataToRemove( BinaryDataEntry.createBinaryDataKeyList( deletedBinaries ) );
                command.setUseCommandsBinaryDataToRemove( true );
            }

            // Find new binaries
            final List<BinaryDataEntry> binaryEntries = newContentData.getBinaryDataEntryList();
            command.setBinaryDataToAdd( BinaryDataAndBinary.convert( binaryEntries ) );
            command.setUseCommandsBinaryDataToAdd( true );
        }
        else
        {
            // only update the meta data in this case..
        }

        if ( params.updateStrategy == ContentDataInputUpdateStrategy.REPLACE_NEW )
        {
            command.setUpdateStrategy( UpdateStrategy.MODIFY );
        }

        final UpdateContentResult updateContentResult = contentService.updateContent( command );

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        return updateContentResult.getTargetedVersionKey().toInt();
    }

    public int createFileContent( CreateFileContentParams params )
    {
        validateCreateFileContentParams( params );

        CategoryEntity category = categoryDao.findByKey( new CategoryKey( params.categoryKey ) );

        UserEntity runningUser = securityService.getRunAsUser();

        LegacyFileContentData contentdata = (LegacyFileContentData) fileContentResolver.resolveContentdata( params.fileContentData );
        List<BinaryDataAndBinary> binaryDataEntries = new ArrayList<BinaryDataAndBinary>();
        binaryDataEntries.add( BinaryDataAndBinary.convertFromBinaryInput( params.fileContentData.binary ) );

        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createCommand.setCreator( runningUser );
        createCommand.setStatus( ContentStatus.get( params.status ) );
        createCommand.setPriority( 0 );
        createCommand.setCategory( category );
        createCommand.setLanguage( category.getUnitExcludeDeleted().getLanguage() );
        createCommand.setAvailableFrom( params.publishFrom );
        createCommand.setAvailableTo( params.publishTo );
        createCommand.setContentData( contentdata );
        createCommand.setContentName( PrettyPathNameCreator.generatePrettyPathName( contentdata.getTitle() ) );
        createCommand.setBinaryDatas( binaryDataEntries );
        createCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( createCommand );
        return contentKey.toInt();
    }

    public int updateFileContent( UpdateFileContentParams params )
    {
        validateUpdateFileContentParams( params );

        ContentVersionKey contentVersionKey =
            resolveContentVersionKey( params.createNewVersion, params.contentKey, params.contentVersionKey );

        UpdateContentCommand command;
        if ( params.createNewVersion )
        {
            command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( contentVersionKey );
        }
        else
        {
            command = UpdateContentCommand.updateExistingVersion2( contentVersionKey );
        }

        command.setContentKey( new ContentKey( params.contentKey ) );
        command.setSyncRelatedContent( false );
        command.setSyncAccessRights( false );
        command.setModifier( securityService.getRunAsUser() );
        command.setAvailableFrom( params.publishFrom );
        command.setAvailableTo( params.publishTo );
        command.setStatus( ContentStatus.get( params.status ) );

        LegacyFileContentData newContentData;
        List<BinaryDataAndBinary> binariesToAdd = null;
        List<BinaryDataKey> binariesToRemove = null;
        if ( params.fileContentData != null )
        {
            newContentData = (LegacyFileContentData) fileContentResolver.resolveContentdata( params.fileContentData );
            command.setContentData( newContentData );
            if ( !params.createNewVersion )
            {
                // only delete previous binaries if we are overwriting current version
                ContentVersionEntity persistedVersion = contentVersionDao.findByKey( contentVersionKey );
                LegacyFileContentData previousContentData = (LegacyFileContentData) persistedVersion.getContentData();
                binariesToRemove = previousContentData.getRemovedBinaries( newContentData );
            }
            // Find new binaries
            binariesToAdd = newContentData.getBinaryDataAndBinaryList();
        }
        else
        {
            // only update the meta data in this case..
        }

        command.setUpdateAsMainVersion( params.setAsCurrentVersion );
        command.setUseCommandsBinaryDataToAdd( true );
        command.setBinaryDataToAdd( binariesToAdd );

        command.setUseCommandsBinaryDataToRemove( true );
        command.setBinaryDataToRemove( binariesToRemove );

        UpdateContentResult updateContentResult = contentService.updateContent( command );

        if ( updateContentResult.isAnyChangesMade() )
        {
            new PageCacheInvalidatorForContent( siteCachesService ).invalidateForContent( updateContentResult.getTargetedVersion() );
        }

        return updateContentResult.getTargetedVersionKey().toInt();
    }

    private void validateUpdateFileContentParams( UpdateFileContentParams params )
    {
        if ( params.contentKey == null )
        {
            throw new IllegalArgumentException( "contentKey must be specified" );
        }
        if ( params.fileContentData == null )
        {
            throw new IllegalArgumentException( "data must be specified" );
        }
        if ( params.status == null )
        {
            throw new IllegalArgumentException( "status must be specified" );
        }
        if ( params.publishFrom != null && params.publishTo != null && !params.publishTo.after( params.publishFrom ) )
        {
            throw new IllegalArgumentException( "publishTo must be after publishFrom" );
        }

        ContentEntity content = contentDao.findByKey( new ContentKey( params.contentKey ) );
        if ( content == null )
        {
            throw new IllegalArgumentException( "No content for given contentKey: " + params.contentKey );
        }
    }

    private CategoryEntity validateCreateContentParams( CreateContentParams params )
    {
        if ( params.categoryKey == null )
        {
            throw new IllegalArgumentException( "categoryKey must be specified" );
        }
        if ( params.status == null )
        {
            throw new IllegalArgumentException( "status must be specified" );
        }
        if ( params.contentData == null )
        {
            throw new IllegalArgumentException( "contentData must be specified" );
        }
        if ( params.publishFrom != null && params.publishTo != null && !params.publishTo.after( params.publishFrom ) )
        {
            throw new IllegalArgumentException( "publishTo must be after publishFrom" );
        }

        CategoryEntity category = categoryDao.findByKey( new CategoryKey( params.categoryKey ) );
        if ( category == null )
        {
            throw new IllegalArgumentException( "category does not exists, id: " + params.categoryKey );
        }
        return category;
    }

    private void validateCreateFileContentParams( CreateFileContentParams params )
    {
        if ( params.categoryKey == null )
        {
            throw new IllegalArgumentException( "categoryKey must be specified" );
        }
        if ( params.fileContentData == null )
        {
            throw new IllegalArgumentException( "data must be specified" );
        }
        if ( params.status == null )
        {
            throw new IllegalArgumentException( "status must be specified" );
        }
        if ( params.publishFrom != null && params.publishTo != null && !params.publishTo.after( params.publishFrom ) )
        {
            throw new IllegalArgumentException( "publishTo must be after publishFrom" );
        }

        CategoryEntity category = categoryDao.findByKey( new CategoryKey( params.categoryKey ) );
        if ( category == null )
        {
            throw new IllegalArgumentException( "category does not exists, id: " + params.categoryKey );
        }
    }

    private void validateUpdateContentParams( UpdateContentParams params )
    {
        if ( params.contentKey == null )
        {
            throw new IllegalArgumentException( "contentKey must be specified" );
        }
        if ( params.updateStrategy == null )
        {
            throw new IllegalArgumentException( "updateStrategy must be specified" );
        }
        if ( params.publishFrom != null && params.publishTo != null && !params.publishTo.after( params.publishFrom ) )
        {
            throw new IllegalArgumentException( "publishTo must be after publishFrom" );
        }
        if ( params.createNewVersion && params.contentData == null && params.updateStrategy == ContentDataInputUpdateStrategy.REPLACE_ALL )
        {
            throw new IllegalArgumentException( "contentData must be specified if you want to create new version when updateStrategy is " +
                ContentDataInputUpdateStrategy.REPLACE_ALL );
        }
        if ( params.contentVersionKey != null && params.createNewVersion )
        {
            throw new IllegalArgumentException(
                "There is no meaning in wanting to both update one specific version and create a new version" );
        }

        ContentEntity persistedContent = contentDao.findByKey( new ContentKey( params.contentKey ) );
        if ( persistedContent == null )
        {
            throw new IllegalArgumentException( "No content for given contentKey: " + params.contentKey );
        }

        int currentStatus = persistedContent.getMainVersion().getStatus().getKey();
        if ( !params.createNewVersion && currentStatus != ContentStatus.DRAFT.getKey() && params.contentData != null )
        {
            throw new IllegalArgumentException( "Only allowed to overwrite a draft content version - create new version instead" );
        }

        if ( params.status != null )
        {
            boolean currentStatusIsApprovedOrArchived =
                currentStatus == ContentStatus.APPROVED.getKey() || currentStatus == ContentStatus.ARCHIVED.getKey();

            if ( currentStatusIsApprovedOrArchived )
            {
                boolean statusChangingingToDraft = params.status == ContentStatus.DRAFT.getKey();
                boolean statusChangingToSnapshot = params.status == ContentStatus.SNAPSHOT.getKey();

                if ( !params.createNewVersion && ( statusChangingingToDraft || statusChangingToSnapshot ) )
                {
                    throw new IllegalArgumentException(
                        "Not allowed to change status of an approved or archived content version - create new content version instead" );
                }
            }
        }
    }


    private ContentVersionKey resolveContentVersionKey( boolean createNewVersion, int contentKey, Integer contentVeresionKey )
    {
        ContentEntity persistedContent = contentDao.findByKey( new ContentKey( contentKey ) );

        ContentVersionKey contentVersionKey;
        if ( createNewVersion )
        {
            contentVersionKey = persistedContent.getMainVersion().getKey();
        }
        else if ( contentVeresionKey == null )
        {
            contentVersionKey = persistedContent.getMainVersion().getKey();
        }
        else
        {
            contentVersionKey = new ContentVersionKey( contentVeresionKey );
        }
        return contentVersionKey;
    }

    private ContentTypeEntity resolveContentType( int contentKey )
    {
        ContentEntity persistedContent = contentDao.findByKey( new ContentKey( contentKey ) );
        return persistedContent.getCategory().getContentType();
    }

    public void setSiteCachesService( SiteCachesService value )
    {
        this.siteCachesService = value;
    }

    public void setUserParser( UserParser userParser )
    {
        this.userParser = userParser;
    }


}
