/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.core.content.UpdateContentResult;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.DataEntry;
import com.enonic.cms.domain.content.contenttype.CtyImportBlockConfig;
import com.enonic.cms.domain.content.contenttype.CtyImportConfig;
import com.enonic.cms.domain.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.domain.content.contenttype.CtyImportUpdateStrategyConfig;
import com.enonic.cms.domain.content.imports.ImportDataEntry;
import com.enonic.cms.domain.content.imports.ImportDataReader;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.content.imports.ImportValueFormater;
import com.enonic.cms.domain.content.imports.InvalidImportDataException;
import com.enonic.cms.domain.content.imports.sourcevalueholders.AbstractSourceValue;
import com.enonic.cms.domain.portal.PrettyPathNameCreator;
import com.enonic.cms.domain.security.user.UserEntity;

public class ContentImporterImpl
{
    private RelatedContentFinder relatedContentFinder;

    private ContentDao contentDao;

    private ContentStorer contentStorer;

    private final CtyImportConfig importConfig;

    private final UserEntity importer;

    private final CategoryEntity category;

    private final DateTime defaultPublishFrom;

    private final DateTime defaultPublishTo;

    private final ImportJob importJob;

    private final ImportDataReader importDataReader;

    private final ImportResult importResult;

    private final Set<String> usedSyncValues = new HashSet<String>();

    public ContentImporterImpl( ImportJob importJob, ImportDataReader importDataReader )
    {
        this.importJob = importJob;
        this.importDataReader = importDataReader;
        this.category = importJob.getCategoryToImportTo();
        this.defaultPublishFrom = importJob.getDefaultPublishFrom();
        this.defaultPublishTo = importJob.getDefaultPublishTo();
        this.importer = importJob.getImporter();
        this.importResult = importJob.getImportResult();
        this.importConfig = importJob.getImportConfig();
    }

    public boolean importData()
    {
        if ( !importDataReader.hasMoreEntries() )
        {
            throw new IllegalStateException( "Missing import entries" );
        }

        do
        {
            final ImportDataEntry nextDataEntryToImport = importDataReader.getNextEntry();

            if ( importConfig.isSyncEnabled() )
            {
                checkSyncValueExist( nextDataEntryToImport );
                checkSyncValueUnused( nextDataEntryToImport );
                usedSyncValues.add( nextDataEntryToImport.getSyncValue() );
            }

            importDataEntry( nextDataEntryToImport );

        }
        while ( importDataReader.hasMoreEntries() );

        return true;
    }

    private void checkSyncValueExist( final ImportDataEntry importDataEntry )
    {
        if ( importDataEntry.getSyncValue() == null )
        {
            throw new InvalidImportDataException( "Sync value not found: " + importDataEntry );
        }
    }

    private void checkSyncValueUnused( final ImportDataEntry importDataEntry )
    {
        if ( usedSyncValues.contains( importDataEntry.getSyncValue() ) )
        {
            throw new InvalidImportDataException(
                "Import data did not contain unique entries as defined by the sync field, found duplicate with sync value: \"" +
                    importDataEntry.getSyncValue() + "\"" );
        }
    }

    private void importDataEntry( final ImportDataEntry importDataEntry )
    {
        if ( importConfig.isSyncEnabled() )
        {
            final ContentKey existingContentKey = importJob.resolveExistingContentBySyncValue( importDataEntry );

            if ( existingContentKey == null )
            {
                importByInsertingNewContent( importDataEntry );
            }
            else
            {
                importByChangeExistingContent( importDataEntry, existingContentKey );
            }
        }
        else
        {
            importByInsertingNewContent( importDataEntry );
        }
    }

    private void importByInsertingNewContent( final ImportDataEntry importDataEntry )
    {
        // create new content
        final ContentEntity newContent = storeNewContent( importDataEntry );

        importResult.addInserted( newContent );

        final boolean isDraft = newContent.getMainVersion().getStatus().equals( ContentStatus.DRAFT );

        if ( isDraft && importJob.getAssignee() != null )
        {
            doAssignContent( newContent );
        }

        importJob.registerImportedContent( newContent.getKey() );
    }

    private void doAssignContent( ContentEntity newContent )
    {
        UserEntity assignee = importJob.getAssignee();

        AssignContentCommand assignContentCommand = new AssignContentCommand();
        assignContentCommand.setAssigneeKey( assignee.getKey() );
        assignContentCommand.setContentKey( newContent.getKey() );
        assignContentCommand.setAssignerKey( importJob.getImporter().getKey() );
        assignContentCommand.setAssignmentDescription( importJob.getAssignmentDescription() );
        assignContentCommand.setAssignmentDueDate( importJob.getAssignmentDueDate() );

        contentStorer.assignContent( assignContentCommand );

        importResult.addAssigned( newContent );
    }

    private void importByChangeExistingContent( final ImportDataEntry importDataEntry, final ContentKey existingContentKey )
    {
        final ContentEntity existingContent = contentDao.findByKey( existingContentKey );

        if ( existingContent == null )
        {
        }

        // update existing content
        final boolean anyChangesMade = updateExistingContent( existingContent, importDataEntry );
        if ( anyChangesMade )
        {
            importResult.addUpdated( existingContent );
            importJob.registerImportedContent( existingContent.getKey() );
        }
        else
        {
            importResult.addUnchanged( existingContent );
            importJob.registerImportedContent( existingContent.getKey() );
        }
    }


    private boolean updateExistingContent( final ContentEntity existingContent, final ImportDataEntry importDataDataEntry )
    {
        // Content Data
        ImportDataEntryParser importDataEntryParser =
            new ImportDataEntryParser( relatedContentFinder, category.getContentType().getContentTypeConfig() );
        final CustomContentData newContentData = importDataEntryParser.parseToCustomContentData( importDataDataEntry, existingContent );

        final ContentVersionEntity versionToBaseNewVersionOn = existingContent.getMainVersion();
        final Set<BinaryDataKey> binaryDataToRemove = getBinaryDataToRemove( versionToBaseNewVersionOn );
        final List<BinaryDataAndBinary> binaryDataToAdd = getBinaryData( newContentData );

        // Command
        final UpdateContentCommand command = createUpdateContentCommand( existingContent, versionToBaseNewVersionOn );
        command.setModifier( importer );
        command.setContentKey( existingContent.getKey() );
        command.setAvailableFrom( existingContent.getAvailableFrom() );
        command.setAvailableTo( existingContent.getAvailableTo() );
        command.setLanguage( existingContent.getLanguage() );
        command.setOwner( existingContent.getOwner().getKey() );
        command.setPriority( existingContent.getPriority() );
        command.addContentAccessRights( existingContent.getContentAccessRights(), existingContent );
        command.setContentData( newContentData );
        for ( CtyImportBlockConfig blockConfig : importConfig.getBlocks() )
        {
            if ( blockConfig.purgeRemainingEntries() )
            {
                command.addBlockGroupToPurge( blockConfig.getDestination() );
            }
        }

        command.setBinaryDataToAdd( binaryDataToAdd );
        command.setUseCommandsBinaryDataToAdd( true );

        command.setBinaryDataToRemove( binaryDataToRemove );
        command.setUseCommandsBinaryDataToRemove( true );

        command.setUpdateStrategy( UpdateContentCommand.UpdateStrategy.MODIFY );

        final UpdateContentResult updateResult = contentStorer.updateContent( command );
        return updateResult.isAnyChangesMade();
    }

    private UpdateContentCommand createUpdateContentCommand( final ContentEntity existingContent,
                                                             ContentVersionEntity versionToBaseNewVersionOn )
    {
        ContentStatus existingContentStatus = existingContent.getMainVersion().getStatus();

        if ( importConfig.getUpdateStrategy().equals( CtyImportUpdateStrategyConfig.UPDATE_CONTENT_KEEP_STATUS ) )
        {
            final UpdateContentCommand command;

            if ( existingContentStatus.equals( ContentStatus.DRAFT ) )
            {
                command = UpdateContentCommand.updateExistingVersion2( versionToBaseNewVersionOn.getKey() );
                command.setStatus( existingContentStatus );
                command.setUpdateAsMainVersion( true );
            }
            else
            {
                command = UpdateContentCommand.storeNewVersionIfChanged( versionToBaseNewVersionOn.getKey() );
                command.setStatus( existingContentStatus );
                command.setUpdateAsMainVersion( true );
            }

            return command;
        }
        else if ( importConfig.getUpdateStrategy().equals( CtyImportUpdateStrategyConfig.UPDATE_CONTENT_DRAFT ) )
        {
            final UpdateContentCommand command;

            if ( existingContentStatus.equals( ContentStatus.DRAFT ) )
            {
                command = UpdateContentCommand.updateExistingVersion2( versionToBaseNewVersionOn.getKey() );
                command.setStatus( existingContentStatus );
                command.setUpdateAsMainVersion( true );
            }
            else if ( existingContentStatus.equals( ContentStatus.ARCHIVED ) )
            {
                command = UpdateContentCommand.storeNewVersionIfChanged( versionToBaseNewVersionOn.getKey() );
                command.setStatus( ContentStatus.DRAFT );
                command.setUpdateAsMainVersion( true );
            }
            else
            {
                // main version is APPROVED
                if ( existingContent.hasDraft() )
                {
                    command = UpdateContentCommand.updateExistingVersion2( existingContent.getDraftVersion().getKey() );
                    command.setStatus( ContentStatus.DRAFT );
                    command.setUpdateAsMainVersion( false );
                }
                else
                {
                    command = UpdateContentCommand.storeNewVersionIfChanged( versionToBaseNewVersionOn.getKey() );
                    command.setStatus( ContentStatus.DRAFT );
                    command.setUpdateAsMainVersion( false );
                }
            }
            return command;
        }
        else if ( importConfig.getUpdateStrategy().equals( CtyImportUpdateStrategyConfig.UPDATE_AND_APPROVE_CONTENT ) )
        {
            final UpdateContentCommand command;
            command = UpdateContentCommand.storeNewVersionIfChanged( versionToBaseNewVersionOn.getKey() );
            command.setStatus( ContentStatus.APPROVED );
            command.setUpdateAsMainVersion( true );
            return command;
        }
        else if ( importConfig.getUpdateStrategy().equals( CtyImportUpdateStrategyConfig.UPDATE_AND_ARCHIVE_CONTENT ) )
        {
            final UpdateContentCommand command;
            command = UpdateContentCommand.storeNewVersionIfChanged( versionToBaseNewVersionOn.getKey() );
            command.setStatus( ContentStatus.ARCHIVED );
            command.setUpdateAsMainVersion( true );
            return command;
        }

        throw new UnsupportedOperationException( "Unsupported update strategy: " + importConfig.getUpdateStrategy() );
    }

    private Set<BinaryDataKey> getBinaryDataToRemove( final ContentVersionEntity existingVersion )
    {
        return existingVersion.getContentBinaryDataKeys();
    }

    private ContentEntity storeNewContent( final ImportDataEntry importData )
    {
        // Content Data
        final CustomContentData newContentData =
            new ImportDataEntryParser( relatedContentFinder, category.getContentType().getContentTypeConfig() ).parseToCustomContentData(
                importData, null );

        final List<BinaryDataAndBinary> binaryData = getBinaryData( newContentData );

        // Command
        final CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setCreator( importer );
        createCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.INHERIT_FROM_CATEGORY );
        createCommand.setContentName( PrettyPathNameCreator.generatePrettyPathName( newContentData.getTitle() ) );
        createCommand.setCategory( category );
        final DateTime availableFrom = resolveAvailableFrom( importData );
        if ( availableFrom != null )
        {
            createCommand.setAvailableFrom( availableFrom.toDate() );
        }
        final DateTime availableTo = resolveAvailableTo( importData );
        if ( availableTo != null )
        {
            createCommand.setAvailableTo( availableTo.toDate() );
        }
        createCommand.setPriority( 0 );
        createCommand.setLanguage( category.getLanguage() );
        createCommand.setContentData( newContentData );
        createCommand.setStatus( importConfig.getStatus().toContentStatus() );
        createCommand.setBinaryDatas( binaryData );
        createCommand.setUseCommandsBinaryDataToAdd( true );

        return contentStorer.createContent( createCommand );
    }

    private List<BinaryDataAndBinary> getBinaryData( final CustomContentData newContentData )
    {
        final List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();

        for ( final DataEntry entry : newContentData.getEntries() )
        {
            if ( entry instanceof BinaryDataEntry )
            {
                binaryDatas.add( BinaryDataAndBinary.convertFromBinaryEntry( (BinaryDataEntry) entry ) );
            }
        }
        return binaryDatas;
    }

    private DateTime resolveAvailableFrom( final ImportDataEntry importData )
    {
        final DateTime value = getMetadataDateValue( importData, "@publishfrom" );
        return value != null ? value : defaultPublishFrom;
    }

    private DateTime resolveAvailableTo( final ImportDataEntry importData )
    {
        final DateTime value = getMetadataDateValue( importData, "@publishto" );
        return value != null ? value : defaultPublishTo;
    }

    private DateTime getMetadataDateValue( final ImportDataEntry importData, final String destination )
    {
        for ( final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndMetadataValue : importData.getConfigAndMetadataValueMap().entrySet() )
        {
            final CtyImportMappingConfig config = configAndMetadataValue.getKey();
            if ( config.getDestination().equals( destination ) )
            {
                final Date date = ImportValueFormater.getDate( configAndMetadataValue.getValue(), config.getFormat() );
                if ( date == null )
                {
                    return null;
                }
                return new DateTime( date );
            }
        }
        return null;
    }

    public void setContentDao( final ContentDao value )
    {
        this.contentDao = value;
    }

    public void setContentStorer( final ContentStorer value )
    {
        this.contentStorer = value;
    }

    public void setRelatedContentFinder( final RelatedContentFinder value )
    {
        this.relatedContentFinder = value;
    }
}
