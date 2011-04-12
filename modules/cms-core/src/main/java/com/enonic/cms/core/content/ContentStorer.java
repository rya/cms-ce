/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.category.CategoryAccessException;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.structure.menuitem.ContentHomeEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import com.enonic.cms.framework.blob.BlobStoreObject;

import com.enonic.cms.core.content.access.ContentAccessResolver;
import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.BaseContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentHomeDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.LanguageDao;
import com.enonic.cms.store.dao.RelatedContentDao;
import com.enonic.cms.store.dao.SectionContentDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.command.SnapshotContentCommand;
import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.command.UpdateAssignmentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand.UpdateStrategy;

import com.enonic.cms.domain.LanguageEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.MissingRequiredContentDataException;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.CustomContentDataModifier;
import com.enonic.cms.portal.ContentNotFoundException;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserKey;
import com.enonic.cms.core.security.user.UserNotFoundException;

public class ContentStorer
{
    @Autowired
    private BinaryDataDao binaryDataDao;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ContentHomeDao contentHomeDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private RelatedContentDao relatedContentDao;

    @Autowired
    private SectionContentDao sectionContentDao;

    @Autowired
    private LanguageDao languageDao;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ContentValidator contentValidator;

    public ContentEntity createContent( final CreateContentCommand createContentCommand )
    {
        Preconditions.checkNotNull( createContentCommand.getCreator(), "creatorKey cannot be null" );
        Preconditions.checkNotNull( createContentCommand.getCategory(), "categoryKey cannot be null" );
        final ContentStatus contentStatus = createContentCommand.getStatus();

        if ( contentStatus != null && contentStatus.equals( ContentStatus.SNAPSHOT ) )
        {
            throw new ContentOperationException( "Status for new content cannot be SNAPSHOT" );
        }

        final UserEntity creator = getAndVerifyUser( createContentCommand.getCreator() );

        final CategoryEntity category = categoryDao.findByKey( createContentCommand.getCategory() );

        if ( category.getContentType() == null )
        {
            throw new IllegalArgumentException(
                "Unable to create content in category " + category.getKey() + ". Category has no contenttype set." );
        }

        checkCreateContentAccess( createContentCommand, creator, category );

        final Date creationDate = new Date();

        ContentEntity newContent = new ContentEntity();
        newContent.setName( createContentCommand.getContentName() );
        newContent.setCreatedAt( creationDate );
        newContent.setTimestamp( creationDate );
        newContent.setDeleted( false );
        newContent.setOwner( creator );
        newContent.setCategory( category );
        newContent.setAvailableFrom( createContentCommand.getAvailableFromAsDate() );
        newContent.setAvailableTo( createContentCommand.getAvailableToAsDate() );
        newContent.setPriority( createContentCommand.getPriority() );

        newContent.setLanguage( languageDao.findByKey( createContentCommand.getLanguage() ) );
        if ( createContentCommand.getSource() != null )
        {
            newContent.setSource( contentDao.findByKey( createContentCommand.getSource() ) );
        }
        newContent.addContentAccessRights( createContentCommand.getContentAccessRights() );

        ContentVersionEntity newContentVersion = new ContentVersionEntity();
        newContentVersion.setCreatedAt( creationDate );
        newContentVersion.setModifiedAt( creationDate );
        newContentVersion.setModifiedBy( creator );
        newContentVersion.setStatus( contentStatus );
        newContentVersion.setChangeComment( createContentCommand.getChangeComment() );
        newContentVersion.setContentData( createContentCommand.getContentData() );

        newContent.addVersion( newContentVersion );

        final List<BinaryDataAndBinary> binariesToAdd = doStoreBinaryData( createContentCommand, newContentVersion );

        flushPendingHibernateWork();

        final ContentEntity persistedContent =
            doStoreNewContent( createContentCommand.getAccessRightsStrategy(), newContent, newContentVersion );

        final List<ContentBinaryDataEntity> contentBinaryDatasToAdd = ContentBinaryDataEntity.createNewFrom( binariesToAdd );
        doAddContentBinariesToVersion( persistedContent.getMainVersion(), contentBinaryDatasToAdd );

        flushPendingHibernateWork();

        indexService.index( newContent );

        flushPendingHibernateWork();

        return persistedContent;
    }

    private void checkCreateContentAccess( CreateContentCommand createContentCommand, UserEntity creator, CategoryEntity category )
    {
        boolean hasCreateAccess = new CategoryAccessResolver( groupDao ).hasCreateContentAccess( creator, category );
        if ( !hasCreateAccess )
        {
            throw new CategoryAccessException( "Cannot create new content", creator.getQualifiedName(), CategoryAccessType.CREATE,
                                               createContentCommand.getCategory() );
        }
        final boolean statusApprovedOrArchived =
            ContentStatus.APPROVED == createContentCommand.getStatus() || ContentStatus.ARCHIVED == createContentCommand.getStatus();

        if ( statusApprovedOrArchived )
        {
            boolean hasCreateAndApproveAccess;
            if ( category.getAutoMakeAvailableAsBoolean() )
            {
                hasCreateAndApproveAccess = hasCreateAccess;
            }
            else
            {
                hasCreateAndApproveAccess = new CategoryAccessResolver( groupDao ).hasApproveContentAccess( creator, category );
            }

            if ( !hasCreateAndApproveAccess )
            {
                throw new CategoryAccessException( "Cannot approve new content", creator.getQualifiedName(), CategoryAccessType.APPROVE,
                                                   createContentCommand.getCategory() );
            }
        }
    }

    private List<BinaryDataAndBinary> doStoreBinaryData( BaseContentCommand command, ContentVersionEntity contentVersion )
    {
        List<BinaryDataAndBinary> binariesToAdd = getBinariesToAdd( command );

        doStoreNewBinaries( contentVersion, binariesToAdd );

        return binariesToAdd;
    }

    /*
     * Dette er en temporær løsning for bruk før man får skrevet om all kode til å bruke contentData for å holde binærdata. Man også skrive
     * seg bort fra å bruke BinaryDataAndBinary til BinaryDataEntry hele veien til lagring når man kan gjøre dette på en enhetlig måte hele
     * veien.
     */
    private List<BinaryDataAndBinary> getBinariesToAdd( BaseContentCommand command )
    {
        if ( command.useCommandsBinaryDataToAdd() )
        {
            return command.getBinaryDatas();
        }

        if ( command.getContentData() instanceof CustomContentData )
        {
            ArrayList<BinaryDataAndBinary> binariesFromContentData = new ArrayList<BinaryDataAndBinary>();

            CustomContentData customContentData = (CustomContentData) command.getContentData();

            for ( BinaryDataEntry binaryDataEntry : customContentData.getBinaryDataEntryList() )
            {
                // Add new binaries, but not existing or empty ones
                boolean newNotEmptyBinaryEntry = !binaryDataEntry.hasExistingBinaryKey() && !binaryDataEntry.hasNullBinaryKey();

                if ( newNotEmptyBinaryEntry )
                {
                    BinaryDataAndBinary binaryDataAndBinary = BinaryDataAndBinary.convertFromBinaryEntry( binaryDataEntry );
                    binariesFromContentData.add( binaryDataAndBinary );
                }
            }

            return binariesFromContentData;
        }

        return null;
    }

    private ContentEntity doStoreNewContent( final CreateContentCommand.AccessRightsStrategy accessRightsStrategy, ContentEntity newContent,
                                             ContentVersionEntity newContentVersion )
    {
        contentValidator.validate( newContentVersion.getContentData() );
        ContentNameValidator.validate( newContent.getName() );

        if ( accessRightsStrategy == CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY )
        {
            inheritContentAccessRights( newContent );
        }

        // we need to set current version to null to work around some model/Hibernate problem
        newContent.setMainVersion( null );

        contentDao.storeNew( newContent );

        contentVersionDao.storeNew( newContentVersion );

        flushPendingHibernateWork();

        // we set the current version back again (will be updated when Hibernate sessions ends)
        newContent.setMainVersion( newContentVersion );

        if ( newContentVersion.getStatus().equals( ContentStatus.DRAFT ) )
        {
            newContent.setDraftVersion( newContentVersion );
        }

        flushPendingHibernateWork();

        doStoreNewRelatedContent( newContentVersion.getKey(), newContentVersion.getContentData() );

        flushPendingHibernateWork();

        return newContent;
    }

    private void inheritContentAccessRights( ContentEntity newContent )
    {
        // be sure to remove any given righs, since we are going to inherit the rights now
        newContent.removeAllContentAccessRights();
        InheritContentAccessRightsAlgorithm inheritContentAccessRightsAlgorithm = new InheritContentAccessRightsAlgorithm();
        inheritContentAccessRightsAlgorithm.setGroupDao( groupDao );
        inheritContentAccessRightsAlgorithm.inherit( newContent, newContent.getCategory() );
    }

    public UpdateContentResult updateContent( final UpdateContentCommand updateCommand )
    {
        Preconditions.checkNotNull( updateCommand.getContentKey(), "contentKey cannot be null" );
        Preconditions.checkNotNull( updateCommand.getModifier(), "modifier cannot be null" );

        final ContentEntity persistedContent = getAndVerifyContent( updateCommand.getContentKey() );

        final UpdateContentResult result = new UpdateContentResult();

        final boolean contentHasChanged = doUpdateContentProperties( persistedContent, updateCommand );

        ContentNameValidator.validate( persistedContent.getName() );

        if ( contentHasChanged )
        {
            result.markContentAsChanged();
        }

        if ( updateCommand.getUpdateAsNewVersion() )
        {
            if ( updateCommand.getVersionKeyToBaseNewVersionOn() == null )
            {
                throw new ContentOperationException( "Missing version-key for version to base new version upon" );
            }

            checkCreateNewVersionAccess( updateCommand, persistedContent );
            doStoreAsNewVersion( updateCommand, result );
        }
        else
        {
            if ( updateCommand.getVersionKeyToUpdate() == null )
            {
                throw new ContentOperationException( "Missing version-key for version to be updated" );
            }

            ContentVersionEntity existingVersion = contentVersionDao.findByKey( updateCommand.getVersionKeyToUpdate() );

            if ( existingVersion == null )
            {
                throw new ContentOperationException( "Version to update not found" );
            }

            checkUpdateExistingVersionAccess( updateCommand, persistedContent );
            doUpdateStoredVersion( updateCommand, result );
        }

        boolean hasChangedDraftVersion = ensureDraftRelation( persistedContent );

        if ( hasChangedDraftVersion )
        {
            result.markContentAsChanged();
        }

        flushPendingHibernateWork();

        if ( updateCommand.getUpdateAsMainVersion() && result.getTargetedVersion() != null )
        {
            final boolean alreadyMainVersion = persistedContent.getMainVersion().equals( result.getTargetedVersion() );
            if ( !alreadyMainVersion )
            {
                persistedContent.setMainVersion( result.getTargetedVersion() );
                result.markContentAsChanged();
            }
        }

        if ( updateCommand.getSyncAccessRights() )
        {
            final boolean accessRightsModified = doSynchronizeContentAccessRights( updateCommand );
            if ( accessRightsModified )
            {
                result.markAccessRightsAsChanged();
            }
        }

        if ( result.isAnyChangesMade() )
        {
            persistedContent.setTimestamp( getNow() );
        }

        flushPendingHibernateWork();

        if ( result.isAnyChangesMade() )
        {
            indexService.index( persistedContent );
        }

        flushPendingHibernateWork();

        return result;
    }

    private boolean ensureDraftRelation( ContentEntity persistedContent )
    {
        final ContentVersionEntity existingDraft = persistedContent.getDraftVersion();

        for ( ContentVersionEntity version : persistedContent.getVersions() )
        {
            if ( version.getStatus().equals( ContentStatus.DRAFT ) )
            {
                if ( !version.equals( existingDraft ) )
                {
                    persistedContent.setDraftVersion( version );
                    return true;
                }

                return false;
            }
        }

        // No drafts exists at this point...
        if ( existingDraft != null )
        {
            persistedContent.setDraftVersion( null );
            return true;
        }

        return false;
    }

    private void checkCreateNewVersionAccess( final UpdateContentCommand command, final ContentEntity content )
    {
        final UserEntity updater = userDao.findByKey( command.getModifier() );

        boolean newVersionStatusIsDraft = command.getStatus() != null && command.getStatus() == ContentStatus.DRAFT;
        boolean newVersionStatusIsApproved = command.getStatus() != null && command.getStatus() == ContentStatus.APPROVED;
        boolean newVersionStatusIsSnapshot = command.getStatus() != null && command.getStatus() == ContentStatus.SNAPSHOT;

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
        if ( newVersionStatusIsDraft )
        {
            if ( !contentAccessResolver.hasCreateNewVersionAsDraftAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot create new version as draft", updater.getQualifiedName(),
                                                   CategoryAccessType.CREATE, content.getCategory().getKey() );
            }
        }
        else if ( newVersionStatusIsSnapshot )
        {
            if ( !contentAccessResolver.hasCreateSnapshotAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot create snapshot", updater.getQualifiedName(), CategoryAccessType.CREATE,
                                                   content.getCategory().getKey() );
            }
        }
        else if ( newVersionStatusIsApproved )
        {
            if ( !contentAccessResolver.hasApproveContentAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot approve new version", updater.getQualifiedName(), CategoryAccessType.APPROVE,
                                                   content.getCategory().getKey() );
            }
        }
        else
        {
            if ( !contentAccessResolver.hasCreateNewVersionAccess( updater, content ) )
            {
                throw new ContentAccessException( "Cannot create new version", updater.getQualifiedName(), ContentAccessType.UPDATE,
                                                  content.getKey() );
            }
        }

    }

    private void checkUpdateExistingVersionAccess( final UpdateContentCommand command, final ContentEntity content )
    {
        final UserEntity updater = userDao.findByKey( command.getModifier() );
        final ContentVersionEntity versionToUpdate = contentVersionDao.findByKey( command.getVersionKeyToUpdate() );

        boolean toBeSetToDraft = command.getStatus() != null && command.getStatus() == ContentStatus.DRAFT;
        boolean toBeSetToDraftOrUnchangedStatus = toBeSetToDraft || command.getStatus() == null;
        boolean toBeSetToApproved = command.getStatus() != null && command.getStatus() == ContentStatus.APPROVED;
        boolean toBeSetToArchived = command.getStatus() != null && command.getStatus() == ContentStatus.ARCHIVED;
        boolean isDraft = versionToUpdate.isDraft();
        boolean isApprovedOrArchivedOrSnapshot =
            versionToUpdate.isApproved() || versionToUpdate.isArchived() || versionToUpdate.isSnapshot();

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
        if ( toBeSetToApproved )
        {
            if ( !contentAccessResolver.hasApproveContentAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot approve a version", updater.getQualifiedName(), CategoryAccessType.APPROVE,
                                                   content.getCategory().getKey() );
            }
        }
        else if ( isDraft && toBeSetToArchived )
        {
            if ( !contentAccessResolver.hasUpdateDraftVersionAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot archive a version that has status draft", updater.getQualifiedName(),
                                                   CategoryAccessType.CREATE, content.getCategory().getKey() );
            }
        }
        else if ( isDraft && toBeSetToDraftOrUnchangedStatus )
        {
            if ( !contentAccessResolver.hasUpdateDraftVersionAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot update a version that has status draft", updater.getQualifiedName(),
                                                   CategoryAccessType.CREATE, content.getCategory().getKey() );
            }
        }
        else if ( isApprovedOrArchivedOrSnapshot )
        {
            if ( !contentAccessResolver.hasApproveContentAccess( updater, content ) )
            {
                throw new CategoryAccessException( "Cannot update a version that has status snapshot, approved or archived",
                                                   updater.getQualifiedName(), CategoryAccessType.APPROVE, content.getCategory().getKey() );
            }
        }
        else
        {
            /**
             * Covers scenarios:
             *  - update a draft
             *  - archive a draft
             */
            if ( !contentAccessResolver.hasUpdateDraftVersionAccess( updater, content ) )
            {
                throw new ContentAccessException( "Cannot update existing version", updater.getQualifiedName(), ContentAccessType.UPDATE,
                                                  content.getKey() );
            }
        }
    }

    private void doUpdateStoredVersion( final UpdateContentCommand updateContentCommand, final UpdateContentResult result )
    {
        final ContentVersionEntity persistedVersion = contentVersionDao.findByKey( updateContentCommand.getVersionKeyToUpdate() );

        // Archiving other approved versions, if approving this version
        if ( ( updateContentCommand.getStatus() != null ) && ( updateContentCommand.getStatus() == ContentStatus.APPROVED ) &&
            ( persistedVersion.getStatus() != ContentStatus.APPROVED ) )
        {
            archiveOtherApprovedVersions( persistedVersion );
        }

        final boolean modifiedByRemovedBinaryData =
            persistedVersion.removeContentBinaryDataByBinaryDataKeys( updateContentCommand.getBinaryDataToRemove() );

        flushPendingHibernateWork();

        removeBinaryDataIfUnreferenced( updateContentCommand.getBinaryDataToRemove() );

        final boolean modifiedByUserModifyableProperties = doUpdateContentVersionProperties( persistedVersion, updateContentCommand );

        contentValidator.validate( persistedVersion.getContentData() );

        final List<BinaryDataAndBinary> binariesToAdd = updateContentCommand.getBinaryDataToAdd();
        final boolean modifiedByAddedBinaries = doStoreNewBinaries( persistedVersion, binariesToAdd );

        final List<ContentBinaryDataEntity> cbdsToAdd = ContentBinaryDataEntity.createNewFrom( binariesToAdd );
        doAddContentBinariesToVersion( persistedVersion, cbdsToAdd );

        boolean modifiedByRelatedContent = false;
        if ( updateContentCommand.getSyncRelatedContent() && updateContentCommand.getContentData() != null )
        {
            // do not try synchronize related content if contentdata is not given either
            modifiedByRelatedContent = doSynchronizeRelatedContent( persistedVersion );
        }

        flushPendingHibernateWork();

        boolean changesMade =
            modifiedByUserModifyableProperties || modifiedByRemovedBinaryData || modifiedByAddedBinaries || modifiedByRelatedContent;

        if ( changesMade )
        {
            final UserEntity modifier = userDao.findByKey( updateContentCommand.getModifier() );
            DateTime modifiedTime = new DateTime();
            persistedVersion.setModifiedAt( modifiedTime.toDate() );
            persistedVersion.setModifiedBy( modifier );

            result.markTargetedVersionAsChanged();
        }

        result.setTargetedVersion( persistedVersion );
    }

    private void doStoreAsNewVersion( final UpdateContentCommand updateContentCommand, final UpdateContentResult result )
    {
        final ContentVersionEntity persistedVersion = contentVersionDao.findByKey( updateContentCommand.getVersionKeyToBaseNewVersionOn() );

        final ContentVersionEntity newVersionToPersist = new ContentVersionEntity();

        // Connect the version to it's content...
        final ContentEntity persistedContent = persistedVersion.getContent();
        persistedContent.addVersion( newVersionToPersist );

        newVersionToPersist.setStatus( persistedVersion.getStatus() );
        newVersionToPersist.setChangeComment( persistedVersion.getChangeComment() );
        newVersionToPersist.setContentDataXml( persistedVersion.getContentDataAsXmlString() );
        newVersionToPersist.setTitle( persistedVersion.getTitle() );

        final boolean modifiedByUserModifyableProperties = doUpdateContentVersionProperties( newVersionToPersist, updateContentCommand );

        contentValidator.validate( newVersionToPersist.getContentData() );

        boolean anyBinaryChanges = false;
        if ( updateContentCommand.useCommandsBinaryDataToAdd() && updateContentCommand.useCommandsBinaryDataToRemove() )
        {
            anyBinaryChanges =
                updateContentCommand.getBinaryDataToAdd().size() > 0 || updateContentCommand.getBinaryDataToRemove().size() > 0;
        }

        boolean noChanges = !modifiedByUserModifyableProperties && !anyBinaryChanges;
        // No changes made and no forcing of creating new version - lets return by doing nothing
        if ( noChanges && !updateContentCommand.forceNewVersionEventIfUnchanged() )
        {
            return;
        }

        // Changing the status only if any changes
        if ( updateContentCommand.getStatus() != null && !updateContentCommand.getStatus().equals( newVersionToPersist.getStatus() ) )
        {
            newVersionToPersist.setStatus( updateContentCommand.getStatus() );
        }

        // Set system properties...
        final Date creationDate = new Date();
        final UserEntity modifier = userDao.findByKey( updateContentCommand.getModifier() );
        newVersionToPersist.setCreatedAt( creationDate );
        newVersionToPersist.setModifiedAt( creationDate );
        newVersionToPersist.setModifiedBy( modifier );
        newVersionToPersist.setSnapshotSource( updateContentCommand.getSnapshotSource() );

        ContentVersionEntity mainVersion = persistedContent.getMainVersion();
        boolean mainVersionIsArchivedOrMinor = mainVersion.isArchived() || mainVersion.isSnapshot();

        // Archiving other approved versions, if approving this version
        if ( newVersionToPersist.getStatus() != null &&
            ( newVersionToPersist.getStatus() == ContentStatus.APPROVED || updateContentCommand.getUpdateAsMainVersion() ) )
        {
            archiveOtherApprovedVersions( newVersionToPersist );
        }

        // Snapshots should not affect other versions' statuses
        if ( newVersionToPersist.getStatus() != ContentStatus.SNAPSHOT )
        {
            archiveOtherDraftVersions( newVersionToPersist );
        }

        List<BinaryDataAndBinary> binariesToAdd = doStoreBinaryData( updateContentCommand, newVersionToPersist );

        final List<ContentBinaryDataEntity> contentBinaryDataEntitiesToAdd = ContentBinaryDataEntity.createNewFrom( binariesToAdd );

        Set<BinaryDataKey> binariesToRemove = findBinariesToRemove( persistedVersion, newVersionToPersist, updateContentCommand );

        contentBinaryDataEntitiesToAdd.addAll( resolveContentBinaryDatasToCopyFromPersistedVersion( persistedVersion, binariesToRemove ) );

        doAddContentBinariesToVersion( newVersionToPersist, contentBinaryDataEntitiesToAdd );

        contentVersionDao.storeNew( newVersionToPersist );

        if ( mainVersionIsArchivedOrMinor )
        {
            // Make new version main version if main version is archived
            persistedContent.setMainVersion( newVersionToPersist );
        }

        // must flush before storing the related content
        flushPendingHibernateWork();

        if ( updateContentCommand.getSyncRelatedContent() )
        {
            doPersistRelatedContent( newVersionToPersist );
        }

        flushPendingHibernateWork();

        result.setTargetedVersion( newVersionToPersist );
        result.markTargetedVersionAsChanged();
    }

    private Set<BinaryDataKey> findBinariesToRemove( final ContentVersionEntity persistedVersion,
                                                     final ContentVersionEntity newVersionToPersist,
                                                     final UpdateContentCommand updateContentCommand )
    {

        // Old usage of updateContentCommand to set removeable binaries
        if ( updateContentCommand.useCommandsBinaryDataToRemove() )
        {
            return updateContentCommand.getBinaryDataToRemove();
        }

        // New usage of contentdata to decide what to use
        TreeSet<BinaryDataKey> binariesToRemove = new TreeSet<BinaryDataKey>();

        if ( newVersionToPersist.getContentData() instanceof CustomContentData )
        {
            ArrayList<BinaryDataKey> binariesFromContentData = new ArrayList<BinaryDataKey>();

            CustomContentData customContentData = (CustomContentData) newVersionToPersist.getContentData();

            for ( BinaryDataEntry binaryDataEntry : customContentData.getBinaryDataEntryList() )
            {
                if ( binaryDataEntry.hasExistingBinaryKey() )
                {
                    binariesFromContentData.add( new BinaryDataKey( binaryDataEntry.getExistingBinaryKey() ) );
                }
            }

            for ( ContentBinaryDataEntity persistedCBD : persistedVersion.getContentBinaryData() )
            {
                BinaryDataKey binaryDataKey = persistedCBD.getBinaryData().getBinaryDataKey();
                if ( !binariesFromContentData.contains( binaryDataKey ) ) // do not add those to be removed
                {
                    binariesToRemove.add( binaryDataKey );
                }
            }
        }

        return binariesToRemove;
    }

    private boolean doPersistRelatedContent( final ContentVersionEntity persistedVersionWithUpdatedContentData )
    {
        boolean modified = false;

        ContentData contentData = persistedVersionWithUpdatedContentData.getContentData();

        final Collection<ContentKey> newRelatedChildren = contentData.resolveRelatedContentKeys();

        for ( ContentKey relatedChildKey : newRelatedChildren )
        {
            ContentEntity relatedChild = contentDao.findByKey( relatedChildKey );
            if ( relatedChild == null )
            {
                throw new IllegalArgumentException( "Did expect to find related child in storage, contentKey: " + relatedChildKey );
            }

            if ( !persistedVersionWithUpdatedContentData.hasRelatedChild( relatedChild ) )
            {
                final RelatedContentKey relatedContentKey =
                    new RelatedContentKey( persistedVersionWithUpdatedContentData.getKey(), relatedChildKey );
                final RelatedContentEntity relatedContent = new RelatedContentEntity();
                relatedContent.setKey( relatedContentKey );
                relatedContentDao.storeNew( relatedContent );

                persistedVersionWithUpdatedContentData.addRelatedChild( relatedChild );

                modified = true;
            }
        }

        return modified;
    }

    private boolean doSynchronizeRelatedContent( final ContentVersionEntity persistedVersion )
    {
        ContentData newContentData = persistedVersion.getContentData();
        // First: remove related content from persistedVersion that is no longer in newContentData
        final List<ContentEntity> relatedContentToRemove = new ArrayList<ContentEntity>();
        final Collection<ContentEntity> existingRelatedChildren = persistedVersion.getRelatedChildren( true );
        for ( ContentEntity existingRelatedChild : existingRelatedChildren )
        {
            if ( !newContentData.hasRelatedChild( existingRelatedChild.getKey() ) )
            {
                relatedContentToRemove.add( existingRelatedChild );
            }
        }
        for ( ContentEntity contentToRemove : relatedContentToRemove )
        {
            persistedVersion.removeRelatedChild( contentToRemove );
            final RelatedContentEntity relatedContent = new RelatedContentEntity();
            relatedContent.setKey( new RelatedContentKey( persistedVersion.getKey(), contentToRemove.getKey() ) );
            relatedContentDao.delete( relatedContent );
        }

        boolean modified = !relatedContentToRemove.isEmpty();

        boolean added = doPersistRelatedContent( persistedVersion );
        if ( added )
        {
            modified = true;
        }

        return modified;
    }

    private void doStoreNewRelatedContent( final ContentVersionKey versionKey, final ContentData contentData )
    {
        for ( ContentKey relatedChild : contentData.resolveRelatedContentKeys() )
        {
            final RelatedContentEntity relatedContent = new RelatedContentEntity();
            relatedContent.setKey( new RelatedContentKey( versionKey, relatedChild ) );
            relatedContentDao.storeNew( relatedContent );
        }
    }

    public void deleteContent( final UserEntity deleter, final ContentEntity content )
    {
        /**
         * Covers scenario:
         *  - content main-version which is draft
         *  - content main-version which is approved
         *  - content main-version which is archived
         */
        if ( !new ContentAccessResolver( groupDao ).hasDeleteContentAccess( deleter, content ) )
        {
            throw new ContentAccessException( content.getKey(), ContentAccessType.DELETE );
        }

        doDeleteContent( content );
    }

    public void deleteVersion( UserEntity deleter, ContentVersionEntity version )
    {
        if ( version.isDraft() )
        {
            if ( !new ContentAccessResolver( groupDao ).hasDeleteDraftContentVersionAccess( deleter, version.getContent() ) )
            {
                throw new ContentAccessException( version.getContent().getKey(), ContentAccessType.DELETE );
            }
        }

        doDeleteVersion( version );
    }

    public AssignContentResult assignContent( AssignContentCommand command )
    {
        Preconditions.checkNotNull( command.getContentKey(), "contentKey cannot be null" );
        Preconditions.checkNotNull( command.getAssigneeKey(), "assigneeKey cannot be null" );
        Preconditions.checkNotNull( command.getAssignerKey(), "assignerKey cannot be null" );

        ContentEntity content = getAndVerifyContent( command.getContentKey() );

        UserEntity assigner = getAndVerifyUser( command.getAssignerKey() );

        if ( !new ContentAccessResolver( groupDao ).hasUpdateDraftVersionAccess( assigner, content ) )
        {
            throw new CategoryAccessException( "Not allowed to assign content", assigner.getQualifiedName(), CategoryAccessType.CREATE,
                                               content.getCategory().getKey() );
        }

        UserEntity assignee = getAndVerifyUser( command.getAssigneeKey() );

        if ( assignee.isAnonymous() )
        {
            throw new ContentOperationException( "Anonymous not allowed as assignee" );
        }

        AssignContentResult result = new AssignContentResult();

        result.setAssignedContentKey( content.getKey() );

        if ( content.getAssignee() != null )
        {
            result.setOriginalAssignee( content.getAssignee() );
            result.setOriginalAssigner( content.getAssigner() );
        }

        content.setAssignmentDueDate( command.getAssignmentDueDate() );
        content.setAssignee( assignee );
        content.setAssignmentDescription( command.getAssignmentDescription() );
        content.setAssigner( assigner );
        content.setTimestamp( getNow() );

        flushPendingHibernateWork();

        result.setNewAssignee( assignee );

        indexService.index( content );

        return result;
    }

    private ContentEntity getAndVerifyContent( ContentKey contentKey )
    {
        if ( contentKey == null )
        {
            throw new IllegalArgumentException( "ContentKey cannot be null" );
        }

        ContentEntity content = contentDao.findByKey( contentKey );

        if ( content == null )
        {
            throw new ContentNotFoundException( contentKey );
        }
        return content;
    }

    private UserEntity getAndVerifyUser( UserKey userKey )
    {
        if ( userKey == null )
        {
            throw new IllegalArgumentException( "userKey cannot be null" );
        }

        UserEntity user = userDao.findByKey( userKey );

        if ( user == null )
        {
            throw new UserNotFoundException( userKey );
        }

        return user;
    }

    public void updateAssignment( UpdateAssignmentCommand command )
    {
        Preconditions.checkNotNull( command.getContentKey(), "contentKey cannot be null" );
        Preconditions.checkNotNull( command.getUpdater(), "updaterKey cannot be null" );

        ContentEntity content = getAndVerifyContent( command.getContentKey() );

        UserEntity updater = getAndVerifyUser( command.getUpdater() );

        if ( !new ContentAccessResolver( groupDao ).hasUpdateDraftVersionAccess( updater, content ) )
        {
            throw new CategoryAccessException( "Not allowed to update assignment", updater.getQualifiedName(), CategoryAccessType.CREATE,
                                               content.getCategory().getKey() );
        }

        content.setAssignmentDescription( command.getAssignmentDescription() );
        content.setAssignmentDueDate( command.getAssignmentDueDate() );
        content.setTimestamp( getNow() );

        flushPendingHibernateWork();

        indexService.index( content );
    }

    public UnassignContentResult unassignContent( UnassignContentCommand command )
    {
        Preconditions.checkNotNull( command.getContentKey(), "contentKey cannot be null" );
        Preconditions.checkNotNull( command.getUnassigner(), "unassignerKey cannot be null" );

        UnassignContentResult result = new UnassignContentResult();

        ContentEntity content = getAndVerifyContent( command.getContentKey() );

        result.setOriginalAssigner( content.getAssigner() != null ? content.getAssigner().getKey() : null );
        result.setUnassignedContent( content.getKey() );

        UserEntity unassigner = getAndVerifyUser( command.getUnassigner() );

        if ( !new ContentAccessResolver( groupDao ).hasUpdateDraftVersionAccess( unassigner, content ) )
        {
            throw new CategoryAccessException( "Not allowed to unassign content", unassigner.getQualifiedName(), CategoryAccessType.CREATE,
                                               content.getCategory().getKey() );
        }

        content.setAssignee( null );
        content.setAssigner( null );
        content.setAssignmentDueDate( null );
        content.setAssignmentDescription( null );
        content.setTimestamp( getNow() );

        flushPendingHibernateWork();

        indexService.index( content );

        return result;
    }

    public SnapshotContentResult snapshotContent( SnapshotContentCommand snapshotCommand )
    {
        Preconditions.checkNotNull( snapshotCommand.getContentKey(), "contentKey cannot be null" );
        Preconditions.checkNotNull( snapshotCommand.getModifier(), "modifier cannot be null" );

        ContentEntity parentContent = getAndVerifyContent( snapshotCommand.getContentKey() );

        UserEntity snapshotter = getAndVerifyUser( snapshotCommand.getModifier() );

        ContentVersionEntity contentVersion = parentContent.getDraftVersion();

        if ( contentVersion == null )
        {
            throw new ContentOperationException( "Not allowed to snapshot content with no draft" );
        }

        UpdateContentCommand updateContentCommand;
        updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( contentVersion.getKey() );
        updateContentCommand.setModifier( snapshotter );
        updateContentCommand.setStatus( ContentStatus.SNAPSHOT );
        updateContentCommand.setSnapshotSource( contentVersion );
        updateContentCommand.setContentKey( parentContent.getKey() );
        updateContentCommand.setUpdateAsMainVersion( false );
        updateContentCommand.populateContentValuesFromContent( parentContent );

        if ( snapshotCommand.getSnapshotComment() != null )
        {
            updateContentCommand.setChangeComment( snapshotCommand.getSnapshotComment() );
        }

        UpdateContentResult storeSnapshotResult = updateContent( updateContentCommand );

        contentVersion.addSnapshot( storeSnapshotResult.getTargetedVersion() );

        if ( snapshotCommand.doWipeComment() )
        {
            contentVersion.setChangeComment( null );
        }

        parentContent.setTimestamp();

        SnapshotContentResult result = new SnapshotContentResult();

        result.setStoredSnapshotContentVersion( storeSnapshotResult.getTargetedVersion() );

        flushPendingHibernateWork();

        indexService.index( parentContent );

        return result;
    }

    public boolean archiveMainVersion( final UserEntity archiver, final ContentEntity content )
    {
        boolean isDraft = content.getMainVersion().isDraft();
        boolean isApproved = content.getMainVersion().isApproved();

        ContentAccessResolver contentAccessResolver = new ContentAccessResolver( groupDao );
        if ( isDraft )
        {
            if ( !contentAccessResolver.hasUpdateDraftVersionAccess( archiver, content ) )
            {
                throw new CategoryAccessException( "Cannot archive a version that has status draft", archiver.getQualifiedName(),
                                                   CategoryAccessType.CREATE, content.getCategory().getKey() );
            }
        }
        else if ( isApproved )
        {
            if ( !contentAccessResolver.hasApproveContentAccess( archiver, content ) )
            {
                throw new CategoryAccessException( "Cannot archive a content that has status approved", archiver.getQualifiedName(),
                                                   CategoryAccessType.APPROVE, content.getCategory().getKey() );
            }
        }

        return doChangeMainVersionStatus( archiver, content, ContentStatus.ARCHIVED );
    }

    public boolean approveMainVersion( final UserEntity approver, final ContentEntity content )
    {
        if ( !new ContentAccessResolver( groupDao ).hasApproveContentAccess( approver, content ) )
        {
            throw new CategoryAccessException( "Cannot approve content", approver.getQualifiedName(), CategoryAccessType.APPROVE,
                                               content.getCategory().getKey() );
        }

        return doChangeMainVersionStatus( approver, content, ContentStatus.APPROVED );
    }

    private boolean doChangeMainVersionStatus( final UserEntity changer, final ContentEntity content, ContentStatus status )
    {
        ContentVersionEntity contentVersion = content.getMainVersion();
        if ( contentVersion.hasStatus( status ) )
        {
            return false;
        }

        final ContentVersionEntity newVersion = new ContentVersionEntity();
        newVersion.setModifiedBy( changer );
        newVersion.setStatus( status );
        newVersion.setContent( content );

        final UpdateContentCommand updateContentCommand = UpdateContentCommand.updateExistingVersion2( contentVersion.getKey() );
        updateContentCommand.setModifier( changer );

        // Populate command with ContentVersionData
        updateContentCommand.populateContentVersionValuesFromContentVersion( newVersion );

        // Populate command with contentEntity data
        updateContentCommand.populateContentValuesFromContent( content );

        updateContentCommand.setUpdateAsMainVersion( false );

        return updateContent( updateContentCommand ).isAnyChangesMade();
    }

    public void moveContent( final UserEntity mover, final ContentEntity content, final CategoryEntity toCategory )
    {
        if ( !new ContentAccessResolver( groupDao ).hasDeleteContentAccess( mover, content ) )
        {
            throw new ContentMoveAccessException( content.getKey() );
        }
        if ( !new CategoryAccessResolver( groupDao ).hasCreateContentAccess( mover, toCategory ) )
        {
            throw new ContentMoveAccessException( content.getKey() );
        }

        content.setCategory( toCategory );

        content.setTimestamp( getNow() );

        flushPendingHibernateWork();

        indexService.index( content );
    }

    public ContentKey copyContent( final UserEntity copier, final ContentEntity sourceContent, final CategoryEntity toCategory )
    {
        if ( !new CategoryAccessResolver( groupDao ).hasCreateContentAccess( copier, toCategory ) )
        {
            throw new CategoryAccessException( "Cannot copy content.", copier.getQualifiedName(), CategoryAccessType.CREATE,
                                               toCategory.getKey() );
        }

        if ( sourceContent.getContentType().getContentHandlerName() == ContentHandlerName.POLL )
        {
            throw new UnsupportedOperationException(
                "Copy operation for content handler " + ContentHandlerName.POLL + " currently not supported" );
        }

        ContentVersionEntity sourceVersion = sourceContent.getMainVersion();

        Date creationDate = new Date();

        ContentEntity newContent = new ContentEntity();

        newContent.setName( sourceContent.getName() );
        newContent.setCategory( toCategory );
        newContent.setLanguage( sourceContent.getLanguage() );
        newContent.setSource( sourceContent );
        newContent.setPriority( 0 );
        newContent.setOwner( copier );
        newContent.setCreatedAt( creationDate );
        newContent.setTimestamp( creationDate );
        newContent.setDeleted( false );

        newContent.setAssignee( copier );
        newContent.setAssigner( copier );

        final ContentVersionEntity newVersion = new ContentVersionEntity();
        newVersion.setChangeComment( sourceVersion.getChangeComment() );
        newVersion.setContentData( sourceVersion.getContentData() );
        newVersion.setTitle( sourceVersion.getTitle() );
        newVersion.setStatus( ContentStatus.DRAFT );
        for ( ContentEntity relatedChild : sourceVersion.getRelatedChildren( false ) )
        {
            newVersion.addRelatedChild( relatedChild );
        }
        newVersion.setModifiedAt( creationDate );
        newVersion.setCreatedAt( creationDate );
        newVersion.setModifiedBy( copier );

        newContent.addVersion( newVersion );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        Map<BinaryDataKey, Integer> indexByBinaryDataKey = new HashMap<BinaryDataKey, Integer>();
        int index = 0;
        for ( ContentBinaryDataEntity cbd : sourceVersion.getContentBinaryData() )
        {
            BinaryDataEntity binaryData = cbd.getBinaryData();

            BlobStoreObject blobStoreObject = binaryDataDao.getBlob( binaryData );
            BinaryDataAndBinary newBinary = new BinaryDataAndBinary( binaryData, blobStoreObject );
            newBinary.setLabel( cbd.getLabel() );
            binaryDatas.add( newBinary );

            indexByBinaryDataKey.put( binaryData.getBinaryDataKey(), index++ );
        }

        newVersion.getContentData().turnBinaryKeysIntoPlaceHolders( indexByBinaryDataKey );

        doStoreNewBinaries( newVersion, binaryDatas );

        ContentEntity persistedContent;
        try
        {
            persistedContent = doStoreNewContent( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY, newContent, newVersion );
        }
        catch ( MissingRequiredContentDataException e )
        {
            throw new CopyContentException(
                "Failed to copy content. Maybe content type configuration have changed and the title reference is now invalid. Try edit and save the content before you try copying it again.",
                e );
        }

        doAddContentBinariesToVersion( newVersion, ContentBinaryDataEntity.createNewFrom( binaryDatas ) );

        flushPendingHibernateWork();

        indexService.index( newContent );

        flushPendingHibernateWork();

        return persistedContent.getKey();
    }

    public List<ContentEntity> deleteByCategory( final UserEntity deleter, final CategoryEntity category )
    {
        if ( category == null )
        {
            throw new IllegalArgumentException( "Given category cannot be null" );
        }
        if ( deleter == null )
        {
            throw new IllegalArgumentException( "Given deleter cannot be null" );
        }
        if ( !new CategoryAccessResolver( groupDao ).hasAdministrateCategoryAccess( deleter, category ) )
        {
            throw new CategoryAccessException( "Cannot empty category.", deleter.getQualifiedName(), CategoryAccessType.ADMINISTRATE,
                                               category.getKey() );
        }

        final List<ContentEntity> deletedContent = new ArrayList<ContentEntity>();

        final Set<ContentEntity> contentsInCategory = category.getContents();
        for ( ContentEntity content : contentsInCategory )
        {
            if ( !content.isDeleted() && !content.hasDirectMenuItemPlacements() )
            {
                doDeleteContent( content );
                deletedContent.add( content );
            }
        }

        return deletedContent;
    }

    private boolean doStoreNewBinaries( final ContentVersionEntity version, final List<BinaryDataAndBinary> binaries )
    {
        if ( binaries == null || binaries.isEmpty() )
        {
            return false;
        }

        for ( BinaryDataAndBinary binaryDataAndBinary : binaries )
        {
            doStoreNewBinary( binaryDataAndBinary );
        }

        version.getContentData().replaceBinaryKeyPlaceholders( BinaryDataKey.convertList( binaries ) );
        version.setXmlDataFromContentData();
        return true;
    }

    private void doStoreNewBinary( final BinaryDataAndBinary binaryDataAndBinary )
    {
        final BinaryDataEntity binaryDataToSave = binaryDataAndBinary.createBinaryDataForSave();

        binaryDataToSave.setBinaryDataKey( null );

        if ( binaryDataToSave.getCreatedAt() == null )
        {
            binaryDataToSave.setCreatedAt( new Date() );
        }

        if ( binaryDataToSave.getBlobKey() == null )
        {
            throw new IllegalStateException( "No blobKey set for entity: " + binaryDataToSave.getKey() );
        }

        binaryDataDao.setBlob( binaryDataToSave, binaryDataAndBinary.getBinary() );
        binaryDataDao.storeNew( binaryDataToSave );
        flushPendingHibernateWork();

        final BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataToSave.getBinaryDataKey() );
        if ( binaryData == null )
        {
            throw new IllegalStateException( "Unexpected state, expected to find binary data with key: " + binaryDataToSave.getKey() );
        }

        binaryDataAndBinary.setBinaryData( binaryData );
    }

    private boolean doSynchronizeContentAccessRights( final UpdateContentCommand updateCommand )
    {
        boolean modified = false;

        final ContentEntity persistedContent = contentDao.findByKey( updateCommand.getContentKey() );

        final List<ContentAccessEntity> accessRightsToRemove = new ArrayList<ContentAccessEntity>();

        // remove content access rights that is no longer there
        final Collection<ContentAccessEntity> existingAccessRights = persistedContent.getContentAccessRights();
        for ( ContentAccessEntity existingAccessRight : existingAccessRights )
        {
            boolean remove = !updateCommand.hasContentAccessRight( existingAccessRight );
            if ( remove )
            {
                accessRightsToRemove.add( existingAccessRight );
            }
        }
        for ( ContentAccessEntity accessRight : accessRightsToRemove )
        {
            boolean modifiedByRemove = persistedContent.removeContentAccessRightByGroup( accessRight.getGroup().getGroupKey() );
            if ( modifiedByRemove )
            {
                modified = true;
            }
        }

        for ( ContentAccessEntity givenContentAccess : updateCommand.getContentAccessRights() )
        {
            final ContentAccessEntity persistedContentAccess =
                persistedContent.getContentAccessRight( givenContentAccess.getGroup().getGroupKey() );
            if ( persistedContentAccess != null )
            {
                boolean modifiedByUpdate = persistedContentAccess.overwriteRightsFrom( givenContentAccess );
                if ( modifiedByUpdate )
                {
                    modified = true;
                }
            }
            else
            {
                persistedContent.addContentAccessRight( givenContentAccess );
                modified = true;
            }
        }

        return modified;
    }

    private void doDeleteContent( final ContentEntity content )
    {
        if ( content.isDeleted() )
        {
            throw new IllegalArgumentException( "Content is already marked as deleted: " + content.getKey() );
        }

        // mark content as deleted
        content.setDeleted( true );
        content.setTimestamp( getNow() );

        // delete references from different tables.
        doDeleteRelatedContent( content );
        doDeleteContentHome( content );
        doDeleteSectionContent( content );

        indexService.removeContent( content );
    }

    private void doDeleteSectionContent( ContentEntity content )
    {
        sectionContentDao.deleteByContentKey( content.getKey() );
    }

    private void doDeleteContentHome( ContentEntity content )
    {
        Collection<ContentHomeEntity> homes = content.getContentHomes();
        for ( ContentHomeEntity home : homes )
        {
            contentHomeDao.delete( home );
        }
    }

    private void doDeleteRelatedContent( ContentEntity content )
    {
        // remove related content referencing this content.
        // Set<ContentVersionEntity> parentContentVersions = content.getRelatedParentContentVersions();
        // for (ContentVersionEntity parent: parentContentVersions) {
        // RelatedContentEntity relatedContent = relatedContentDao.findByKey( new RelatedContentKey( parent.getKey(),
        // content.getKey()) )
        // relatedContentDao.delete( relatedContent );
        // }

        // remove related contents referenced by this content.
        for ( ContentVersionEntity version : content.getVersions() )
        {
            final Collection<ContentEntity> contentEntityCollection = version.getRelatedChildren( true );
            for ( ContentEntity relContent : contentEntityCollection )
            {
                RelatedContentEntity relatedContent =
                    relatedContentDao.findByKey( new RelatedContentKey( version.getKey(), relContent.getKey() ) );
                relatedContentDao.delete( relatedContent );
            }
        }
    }

    private void doDeleteVersion( ContentVersionEntity version )
    {
        removeRelatedChildrenFromVersion( version );

        removeUnreferencedBinaryData( version );

        ContentEntity parentContent = version.getContent();

        removeSnapshots( version );

        parentContent.removeVersion( version );

        if ( version.equals( parentContent.getDraftVersion() ) )
        {
            parentContent.setDraftVersion( null );
        }

        parentContent.setTimestamp( getNow() );

        if ( version.isDraft() && parentContent.isAssigned() )
        {
            parentContent.setAssigner( null );
            parentContent.setAssignee( null );
            parentContent.setAssignmentDescription( null );
            parentContent.setAssignmentDueDate( null );
        }

        flushPendingHibernateWork();

        contentVersionDao.delete( version );
    }

    private void removeUnreferencedBinaryData( ContentVersionEntity version )
    {
        // Remove contentBinaries and unreferenced binaries
        List<BinaryDataKey> removedBinaryDataKeys = version.removeContentBinaryData();
        removeBinaryDataIfUnreferenced( removedBinaryDataKeys );
    }

    private Date getNow()
    {
        Date now = Calendar.getInstance().getTime();
        return now;
    }

    private void removeSnapshots( ContentVersionEntity version )
    {
        ContentEntity parentContent = version.getContent();
        Set<ContentVersionEntity> snapshots = version.getSnapshots();

        for ( ContentVersionEntity snapshot : snapshots )
        {
            parentContent.removeVersion( snapshot );
            removeRelatedChildrenFromVersion( snapshot );
            removeUnreferencedBinaryData( snapshot );
        }
    }

    private void removeRelatedChildrenFromVersion( ContentVersionEntity snapshot )
    {
        for ( ContentEntity relContent : snapshot.getRelatedChildren( true ) )
        {
            RelatedContentEntity relatedContent =
                relatedContentDao.findByKey( new RelatedContentKey( snapshot.getKey(), relContent.getKey() ) );
            relatedContentDao.delete( relatedContent );
        }
    }

    private boolean doUpdateContentProperties( final ContentEntity dest, final UpdateContentCommand updateCommand )
    {
        boolean modified = false;
        // NB! Properties we never update:
        // - key (will only be set once at creation)
        // - createdAt (will only be set once at creation)
        // - deleted (can only be changed via deleteContent operation)
        // - category (can only be changed via moveContent operation)

        // properties we always update, even if source value is null..

        if ( dest.setAvailableFrom( updateCommand.getAvailableFromAsDate() ) )
        {
            modified = true;
        }

        if ( dest.setAvailableTo( updateCommand.getAvailableToAsDate() ) )
        {
            modified = true;
        }

        // properties we only update if source value is set - ergo these properties are not nullable
        String contentName = updateCommand.getContentName();

        String existingName = dest.getName();

        if ( StringUtils.isNotEmpty( contentName ) && !contentName.equals( existingName ) )
        {
            dest.setName( contentName );
            modified = true;
        }

        if ( updateCommand.getPriority() != null && !dest.getPriority().equals( updateCommand.getPriority() ) )
        {
            dest.setPriority( updateCommand.getPriority() );
            modified = true;
        }

        if ( updateCommand.getOwner() != null )
        {
            final UserEntity newOwner = userDao.findByKey( updateCommand.getOwner() );
            if ( !dest.getOwner().equals( newOwner ) )
            {
                dest.setOwner( newOwner );
                modified = true;
            }
        }

        if ( updateCommand.getLanguage() != null )
        {
            final LanguageEntity newLanguage = languageDao.findByKey( updateCommand.getLanguage() );
            if ( !dest.getLanguage().equals( newLanguage ) )
            {
                dest.setLanguage( newLanguage );
                modified = true;
            }
        }

        /*
        * if ( updateCommand.getSource() != null && !dest.getSource().equals( updateCommand.getSource() ) ) {
        * dest.setSource( updateCommand.getSource() ); modified = true; }
        */

        return modified;
    }

    private boolean doUpdateContentVersionProperties( final ContentVersionEntity dest, final UpdateContentCommand updateContentCommand )
    {
        boolean modified = false;

        if ( updateContentCommand.getChangeComment() != null && !updateContentCommand.getChangeComment().equals( dest.getChangeComment() ) )
        {
            dest.setChangeComment( updateContentCommand.getChangeComment() );
            modified = true;
        }
        else if ( updateContentCommand.getChangeComment() == null && dest.getChangeComment() != null )
        {
            dest.setChangeComment( null );
            modified = true;
        }

        // Lets wait with updating status if creating new version
        if ( !updateContentCommand.getUpdateAsNewVersion() )
        {
            if ( updateContentCommand.getStatus() != null && !updateContentCommand.getStatus().equals( dest.getStatus() ) )
            {
                dest.setStatus( updateContentCommand.getStatus() );
                modified = true;
            }
        }

        if ( updateContentCommand.getContentData() != null && !dest.getContentData().equals( updateContentCommand.getContentData() ) )
        {
            if ( updateContentCommand.getUpdateStrategy() == UpdateStrategy.MODIFY )
            {
                if ( !( updateContentCommand.getContentData() instanceof CustomContentData ) ||
                    !( dest.getContentData() instanceof CustomContentData ) )
                {
                    throw new UnsupportedOperationException( "Strategy REPLACE_NEW only supported for CustomContentData" );
                }

                CustomContentData destContentData = (CustomContentData) dest.getContentData();
                CustomContentData sourceContentData = (CustomContentData) updateContentCommand.getContentData();
                CustomContentDataModifier modifier = new CustomContentDataModifier( destContentData );
                modifier.addBlockGroupsToPurge( updateContentCommand.getBlockGroupsToPurgeByName() );
                CustomContentData modifiedContentData = modifier.modify( sourceContentData );
                modifiedContentData.validate();

                if ( !destContentData.equals( modifiedContentData ) )
                {
                    dest.setContentData( modifiedContentData );
                    modified = true;
                }
            }
            else
            {
                dest.setContentData( updateContentCommand.getContentData() );
                modified = true;
            }
        }
        else if ( updateContentCommand.getContentData() == null && updateContentCommand.getUpdateStrategy() == UpdateStrategy.MODIFY )
        {
        }

        return modified;
    }

    private void doAddContentBinariesToVersion( final ContentVersionEntity version, final Collection<ContentBinaryDataEntity> cbds )
    {
        for ( ContentBinaryDataEntity cbd : cbds )
        {
            version.addContentBinaryData( createCopyForAnotherVersion( cbd ) );
        }
    }

    private List<ContentBinaryDataEntity> resolveContentBinaryDatasToCopyFromPersistedVersion( final ContentVersionEntity persistedVersion,
                                                                                               final Collection<BinaryDataKey> binaryDataKeysToRemove )
    {
        final List<ContentBinaryDataEntity> list = new ArrayList<ContentBinaryDataEntity>();

        // resolve binaries to copy from persisted version....
        for ( ContentBinaryDataEntity persistedCBD : persistedVersion.getContentBinaryData() )
        {
            BinaryDataKey binaryDataKey = persistedCBD.getBinaryData().getBinaryDataKey();
            if ( !binaryDataKeysToRemove.contains( binaryDataKey ) ) // do not add those to be removed
            {
                // be sure to create new copy of the existing CBD...
                list.add( createCopyForAnotherVersion( persistedCBD ) );
            }
        }

        return list;
    }

    private void archiveOtherApprovedVersions( ContentVersionEntity persistedVersion )
    {
        List<ContentVersionEntity> allVersions = persistedVersion.getContent().getVersions();
        for ( ContentVersionEntity version : allVersions )
        {
            if ( version.getStatus() == ContentStatus.APPROVED && !version.equals( persistedVersion ) )
            {
                version.setStatus( ContentStatus.ARCHIVED );
            }
        }
    }

    private void archiveOtherDraftVersions( ContentVersionEntity persistedVersion )
    {
        List<ContentVersionEntity> allVersions = persistedVersion.getContent().getVersions();
        for ( ContentVersionEntity version : allVersions )
        {
            if ( version.getStatus() == ContentStatus.DRAFT && !version.equals( persistedVersion ) )
            {
                version.setStatus( ContentStatus.ARCHIVED );
            }
        }
    }

    private ContentBinaryDataEntity createCopyForAnotherVersion( final ContentBinaryDataEntity otherCBD )
    {
        final ContentBinaryDataEntity newCBD = new ContentBinaryDataEntity();
        newCBD.setLabel( otherCBD.getLabel() );
        newCBD.setBinaryData( otherCBD.getBinaryData() );
        return newCBD;
    }

    private void removeBinaryDataIfUnreferenced( Collection<BinaryDataKey> binaryDataKeys )
    {
        for ( BinaryDataKey binaryDataKey : binaryDataKeys )
        {
            removeBinaryDataIfUnreferenced( binaryDataKey );
        }
    }

    private void removeBinaryDataIfUnreferenced( BinaryDataKey binaryDataKey )
    {
        BinaryDataEntity binaryData = binaryDataDao.findByKey( binaryDataKey );
        if ( binaryDataDao.countReferences( binaryData ) == 0 )
        {
            binaryDataDao.delete( binaryData );
        }
    }

    private void flushPendingHibernateWork()
    {
        contentDao.getHibernateTemplate().flush();
    }
}
