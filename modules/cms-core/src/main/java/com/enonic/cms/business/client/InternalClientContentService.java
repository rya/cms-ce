/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.esl.util.Base64Util;

import com.enonic.cms.framework.blob.BlobRecord;
import com.enonic.cms.framework.time.TimeService;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.ContentDataInputUpdateStrategy;
import com.enonic.cms.api.client.model.CreateCategoryParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.CreateImageContentParams;
import com.enonic.cms.api.client.model.DeleteContentParams;
import com.enonic.cms.api.client.model.GetBinaryParams;
import com.enonic.cms.api.client.model.GetContentBinaryParams;
import com.enonic.cms.api.client.model.SnapshotContentParams;
import com.enonic.cms.api.client.model.UnassignContentParams;
import com.enonic.cms.api.client.model.UpdateContentParams;
import com.enonic.cms.api.client.model.UpdateFileContentParams;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentBinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;

import com.enonic.cms.business.core.content.ContentService;
import com.enonic.cms.business.core.content.PageCacheInvalidatorForContent;
import com.enonic.cms.business.core.content.UpdateContentResult;
import com.enonic.cms.business.core.content.access.ContentAccessResolver;
import com.enonic.cms.business.core.content.category.CategoryService;
import com.enonic.cms.business.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.business.core.content.command.AssignContentCommand;
import com.enonic.cms.business.core.content.command.CreateContentCommand;
import com.enonic.cms.business.core.content.command.SnapshotContentCommand;
import com.enonic.cms.business.core.content.command.UnassignContentCommand;
import com.enonic.cms.business.core.content.command.UpdateContentCommand;
import com.enonic.cms.business.core.content.command.UpdateContentCommand.UpdateStrategy;
import com.enonic.cms.business.core.content.image.ContentImageUtil;
import com.enonic.cms.business.core.content.image.ImageUtil;
import com.enonic.cms.business.core.security.SecurityService;
import com.enonic.cms.business.core.security.UserParser;
import com.enonic.cms.business.portal.cache.PageCacheService;
import com.enonic.cms.business.portal.cache.SiteCachesService;
import com.enonic.cms.business.preview.PreviewContext;
import com.enonic.cms.business.preview.PreviewService;

import com.enonic.cms.domain.content.ContentAccessType;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentLocation;
import com.enonic.cms.domain.content.ContentLocationSpecification;
import com.enonic.cms.domain.content.ContentLocations;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.AttachmentNotFoundException;
import com.enonic.cms.domain.content.binary.BinaryData;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.category.CategoryAccessType;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.category.CategoryKey;
import com.enonic.cms.domain.content.category.StoreNewCategoryCommand;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyImageContentData;
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
    private BinaryDataDao binaryDataDao;

    @Autowired
    private ContentBinaryDataDao contentBinaryDataDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private PreviewService previewService;

    @Autowired
    private TimeService timeService;

    private SiteCachesService siteCachesService;

    private FileContentdataResolver fileContentResolver = new FileContentdataResolver();

    private ImageContentdataResolver imageContentResolver = new ImageContentdataResolver();

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

    public int createImageContent( CreateImageContentParams params )
        throws IOException
    {
        validateCreateImageContentParams( params );

        CategoryEntity category = categoryDao.findByKey( new CategoryKey( params.categoryKey ) );

        UserEntity runningUser = securityService.getRunAsUser();

        LegacyImageContentData contentdata = (LegacyImageContentData) imageContentResolver.resolveContentdata( params.contentData );

        BinaryData[] binaryArray = getBinaries( params );
        List<BinaryDataAndBinary> binaryDatas = BinaryDataAndBinary.createNewFrom( binaryArray );

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
        createCommand.setBinaryDatas( binaryDatas );
        createCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( createCommand );
        return contentKey.toInt();
    }

    public Document getBinary( GetBinaryParams params )
        throws Exception
    {
        assertMinValue( "binaryKey", params.binaryKey, 0 );

        final UserEntity runAsUser = securityService.getRunAsUser();

        final BinaryDataKey binaryDataKey = new BinaryDataKey( params.binaryKey );
        final BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataKey );
        if ( binaryData == null )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }

        final ContentBinaryDataEntity contentBinaryDataInMainVersion =
            ContentBinaryDataEntity.resolveContentBinaryDataInMainVersion( contentBinaryDataDao.findAllByBinaryKey( binaryData.getKey() ) );
        if ( contentBinaryDataInMainVersion == null )
        {
            throw AttachmentNotFoundException.notFound( binaryData.getBinaryDataKey() );
        }

        final ContentEntity content = contentBinaryDataInMainVersion.getContentVersion().getContent();
        if ( content == null || content.isDeleted() )
        {
            throw AttachmentNotFoundException.notFound( binaryDataKey );
        }
        if ( !new ContentAccessResolver( groupDao ).hasReadContentAccess( runAsUser, content ) )
        {
            throw AttachmentNotFoundException.noAccess( content.getKey() );
        }
        checkContentIsOnline( content );

        return createBinaryDocument( createBinaryData( contentBinaryDataInMainVersion ) );
    }

    public Document getContentBinary( GetContentBinaryParams params )
        throws ClientException
    {
        assertMinValue( "contentKey", params.contentKey, 0 );

        final UserEntity runAsUser = securityService.getRunAsUser();

        final ContentKey contentKey = new ContentKey( params.contentKey );
        final ContentEntity content = contentDao.findByKey( contentKey );
        if ( content == null || content.isDeleted() )
        {
            throw AttachmentNotFoundException.notFound( contentKey );
        }
        if ( !new ContentAccessResolver( groupDao ).hasReadContentAccess( runAsUser, content ) )
        {
            throw AttachmentNotFoundException.noAccess( content.getKey() );
        }
        checkContentIsOnline( content );

        ContentBinaryDataEntity contentBinaryData;
        if ( params.label == null )
        {
            contentBinaryData = content.getMainVersion().getContentBinaryData( "source" );
            if ( contentBinaryData == null )
            {
                contentBinaryData = content.getMainVersion().getOneAndOnlyContentBinaryData();
            }
        }
        else
        {
            contentBinaryData = content.getMainVersion().getContentBinaryData( params.label );
        }

        if ( contentBinaryData == null )
        {
            throw AttachmentNotFoundException.notFound( contentKey );
        }

        return createBinaryDocument( createBinaryData( contentBinaryData ) );
    }

    private BinaryData[] getBinaries( CreateImageContentParams params )
        throws IOException
    {
        String binaryName = params.contentData.binary.getBinaryName();
        // find file type
        String type = null;
        String filenameWithoutExtension = binaryName;

        int idx = binaryName.lastIndexOf( "." );
        if ( idx != -1 )
        {
            type = binaryName.substring( idx + 1 ).toLowerCase();
            if ( "jpg".equals( type ) )
            {
                type = "jpeg";
            }

            filenameWithoutExtension = binaryName.substring( 0, idx );
        }

        BufferedImage origImage = ImageUtil.readImage( params.contentData.binary.getBinary() );

        ArrayList<BinaryData> binaryList = new ArrayList<BinaryData>();

        // Do not add original as un-labeled image with name = name-heightXwitdth

        // Add fixed size images
        binaryList.addAll(
            ContentImageUtil.createStandardSizeImages( origImage, ContentImageUtil.getEncodeType( type ), filenameWithoutExtension ) );

        // add source image
        BinaryData image = BinaryData.createBinaryDataFromStream( null, null, "source", params.contentData );
        binaryList.add( image );

        if ( binaryList == null )
        {
            return null;
        }
        else
        {
            return binaryList.toArray( new BinaryData[binaryList.size()] );
        }
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
        binaryDataEntries.add( BinaryDataAndBinary.convertFromFileBinaryInput( params.fileContentData.binary ) );

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

    private void validateCreateImageContentParams( CreateImageContentParams params )
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
            throw new IllegalArgumentException( "data must be specified" );
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

    private void checkContentIsOnline( final ContentEntity content )
    {
        if ( previewService.isInPreview() )
        {
            PreviewContext previewContext = previewService.getPreviewContext();
            if ( previewContext.isPreviewingContent() &&
                previewContext.getContentPreviewContext().treatContentAsAvailableEvenIfOffline( content.getKey() ) )
            {
                // when in preview, the content doesn't need to be online
                return;
            }
        }

        if ( !content.isOnline( timeService.getNowAsDateTime() ) )
        {
            throw AttachmentNotFoundException.notFound( content.getKey() );
        }
    }

    private BinaryData createBinaryData( ContentBinaryDataEntity contentBinaryData )
    {
        boolean anonAccess =
            contentBinaryData.getContentVersion().getContent().hasAccessRightSet( securityService.getAnonymousUser().getUserGroup(),
                                                                                  ContentAccessType.READ );

        BinaryData binaryData = new BinaryData();
        binaryData.key = contentBinaryData.getBinaryData().getKey();
        binaryData.contentKey = contentBinaryData.getContentVersion().getContent().getKey().toInt();
        binaryData.setSafeFileName( contentBinaryData.getBinaryData().getName() );
        binaryData.timestamp = contentBinaryData.getBinaryData().getCreatedAt();
        binaryData.anonymousAccess = anonAccess;

        BlobRecord blob = this.binaryDataDao.getBlob( contentBinaryData.getBinaryData() );
        binaryData.data = blob.getAsBytes();

        return binaryData;
    }

    private void assertMinValue( String name, int value, int minValue )
    {
        if ( value < minValue )
        {
            throw new IllegalArgumentException( "Parameter [" + name + "] must be >= " + minValue );
        }
    }

    private Document createBinaryDocument( BinaryData binaryData )
    {
        if ( binaryData == null )
        {
            return null;
        }

        Element binaryElem = new Element( "binary" );
        Element fileNameElem = new Element( "filename" );
        fileNameElem.setText( binaryData.fileName );
        binaryElem.addContent( fileNameElem );

        Element binaryKeyElem = new Element( "binarykey" );
        binaryKeyElem.setText( String.valueOf( binaryData.key ) );
        binaryElem.addContent( binaryKeyElem );

        Element contentKeyElem = new Element( "contentkey" );
        contentKeyElem.setText( String.valueOf( binaryData.contentKey ) );
        binaryElem.addContent( contentKeyElem );

        Element sizeElem = new Element( "size" );
        sizeElem.setText( String.valueOf( binaryData.data.length ) );
        binaryElem.addContent( sizeElem );

        Element timestampElem = new Element( "timestamp" );
        timestampElem.setText( binaryData.timestamp.toString() );
        binaryElem.addContent( timestampElem );

        Element dataElem = new Element( "data" );
        dataElem.setText( Base64Util.encode( binaryData.data ) );
        binaryElem.addContent( dataElem );

        return new Document( binaryElem );
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
