/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.cms.framework.util.BatchedList;

import com.enonic.cms.core.content.index.ContentIndexService;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contenttype.CtyImportConfig;
import com.enonic.cms.domain.content.contenttype.CtyImportPurgeConfig;
import com.enonic.cms.domain.content.imports.BatchedImportDataReader;
import com.enonic.cms.domain.content.imports.ImportDataEntry;
import com.enonic.cms.domain.content.imports.ImportDataReader;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.security.user.UserEntity;


public class ImportJobImpl
        implements ImportJob
{
    private static final Logger LOG = LoggerFactory.getLogger( ImportJobImpl.class );

    private ImportService importService;

    private ContentIndexService contentIndexService;

    private ContentDao contentDao;

    private ImportResult importResult;

    private UserEntity importer;

    private CategoryEntity categoryToImportTo;

    private CtyImportConfig importConfig;

    private ImportDataReader importDataReader;

    private DateTime defaultPublishFrom;

    private DateTime defaultPublishTo;

    private List<ContentKey> contentNotAffectedByImport;

    private Map<String, ContentKey> existingContentKeysBySyncValue;

    private boolean executeInOneTransaction = false;

    private UserEntity assignee;

    private Date assignmentDueDate;

    private String assignmentDescription;

    public ImportResult start()
    {
        LOG.info( "Starting content import job #" + this.getImportJobNumber() );
        LOG.info( "Import job #" + this.getImportJobNumber() + ": importing to category: key = " + categoryToImportTo.getKey() + ", path = " +
                          categoryToImportTo.getPathAsString() );

        if ( importConfig.isSyncEnabled() )
        {
            initSyncMode();
        }

        importResult = new ImportResult();
        importResult.startTimer();

        final long batchSize = 20L;
        final BatchedImportDataReader batchedDataReader = new BatchedImportDataReader( importDataReader, batchSize );

        LOG.info( "Import job #" + this.getImportJobNumber() + ": Importing content in transactional batches of " +
                          batchSize + " content per transaction." );

        int batchCount = 0;
        long lastBatchStartTime;
        while ( batchedDataReader.hasMoreEntries() )
        {
            batchCount++;
            LOG.info( "Import job #" + this.getImportJobNumber() + ": batch #" + batchCount + " starting..." );
            lastBatchStartTime = System.currentTimeMillis();

            while ( batchedDataReader.hasMoreEntries() )
            {
                if ( executeInOneTransaction )
                {
                    importService.importDataWithoutRequiresNewPropagation( batchedDataReader, this );
                }
                else
                {
                    importService.importData( batchedDataReader, this );
                }
            }

            LOG.info( "Import job #" + this.getImportJobNumber() + ": batch #" + batchCount + " finished in " + ( System.currentTimeMillis() - lastBatchStartTime ) + " milliseconds." );

            batchedDataReader.startNewBatch();
        }

        if ( importConfig.isSyncEnabled() )
        {
            // Import content is done... now what to do with the unaffected content in the category we imported to?
            handleUnaffectedContentInCategory();
        }

        importResult.stopTimer();

        LOG.info( "Finished content import job #" + this.getImportJobNumber() );

        return importResult;
    }

    private int getImportJobNumber()
    {
        return this.hashCode();
    }

    private void initSyncMode()
    {
        contentNotAffectedByImport = contentDao.findContentKeysByCategory( categoryToImportTo );

        LOG.info( "Import job #" + this.getImportJobNumber() + ": found " + contentNotAffectedByImport.size() +
                          " existing content in category: " + categoryToImportTo.getPathAsString() );

        existingContentKeysBySyncValue =
                new ExistingContentBySyncValueResolver( contentIndexService ).resolve( categoryToImportTo, importConfig );

        LOG.info( "Import job #" + this.getImportJobNumber() + ": found " + existingContentKeysBySyncValue.size() +
                          " matching content keys (by sync value) in category: " + categoryToImportTo.getPathAsString() );
    }

    private void handleUnaffectedContentInCategory()
    {
        if ( contentNotAffectedByImport.isEmpty() )
        {
            LOG.info( "Import job #" + this.getImportJobNumber() +
                              ": No remaining content to purge. All content in the category was affected by the import the job." );
            return;
        }

        BatchedList<ContentKey> batchedContentKeyList = new BatchedList<ContentKey>( contentNotAffectedByImport, 20 );

        while ( batchedContentKeyList.hasMoreBatches() )
        {
            List<ContentKey> batchOfContentKeys = batchedContentKeyList.getNextBatch();
            if ( !batchOfContentKeys.isEmpty() )
            {
                handleUnaffectedContent( batchOfContentKeys );
            }
        }
    }

    private void handleUnaffectedContent( List<ContentKey> contentKeys )
    {
        if ( CtyImportPurgeConfig.ARCHIVE == importConfig.getPurge() )
        {
            if ( executeInOneTransaction )
            {
                importService.archiveContentWithoutRequiresNewPropagation( importer, contentKeys, importResult );
            }
            else
            {
                importService.archiveContent( importer, contentKeys, importResult );
            }

        }
        else if ( CtyImportPurgeConfig.DELETE == importConfig.getPurge() )
        {
            if ( executeInOneTransaction )
            {
                importService.deleteContentWithoutRequiresNewPropagation( importer, contentKeys, importResult );
            }
            else
            {
                importService.deleteContent( importer, contentKeys, importResult );
            }
        }
        else if ( CtyImportPurgeConfig.NONE == importConfig.getPurge() )
        {
            for ( ContentKey contentKey : contentKeys )
            {
                final ContentEntity content = contentDao.findByKey( contentKey );
                importResult.addRemaining( content );
            }
        }
    }

    public ContentKey resolveExistingContentBySyncValue( final ImportDataEntry importDataEntry )
    {
        //String value = StringUtil.replaceECC( importDataEntry.getSyncValue() );
        String value = importDataEntry.getSyncValue();
        return existingContentKeysBySyncValue.get( value.toLowerCase() );
    }

    public void registerImportedContent( ContentKey contentKey )
    {
        if ( importConfig.isSyncEnabled() )
        {
            contentNotAffectedByImport.remove( contentKey );
        }
    }

    public CategoryEntity getCategoryToImportTo()
    {
        return categoryToImportTo;
    }

    public DateTime getDefaultPublishFrom()
    {
        return defaultPublishFrom;
    }

    public DateTime getDefaultPublishTo()
    {
        return defaultPublishTo;
    }

    public UserEntity getImporter()
    {
        return importer;
    }

    public CtyImportConfig getImportConfig()
    {
        return importConfig;
    }

    public ImportResult getImportResult()
    {
        return importResult;
    }

    public void setImportService( ImportService value )
    {
        this.importService = value;
    }

    public void setContentIndexService( ContentIndexService value )
    {
        this.contentIndexService = value;
    }

    public void setContentDao( ContentDao value )
    {
        this.contentDao = value;
    }

    public void setImporter( UserEntity value )
    {
        this.importer = value;
    }

    public void setCategoryToImportTo( CategoryEntity value )
    {
        this.categoryToImportTo = value;
    }

    public void setImportConfig( CtyImportConfig value )
    {
        this.importConfig = value;
    }

    public void setImportDataReader( ImportDataReader value )
    {
        this.importDataReader = value;
    }

    public void setDefaultPublishFrom( DateTime value )
    {
        this.defaultPublishFrom = value;
    }

    public void setDefaultPublishTo( DateTime value )
    {
        this.defaultPublishTo = value;
    }

    public void setExecuteInOneTransaction( boolean value )
    {
        this.executeInOneTransaction = value;
    }

    public UserEntity getAssignee()
    {
        return assignee;
    }

    public void setAssignee( UserEntity assignee )
    {
        this.assignee = assignee;
    }

    public Date getAssignmentDueDate()
    {
        return assignmentDueDate;
    }

    public String getAssignmentDescription()
    {
        return assignmentDescription;
    }

    public void setAssignmentDueDate( Date assignmentDueDate )
    {
        this.assignmentDueDate = assignmentDueDate;
    }

    public void setAssignmentDescription( String assignmentDescription )
    {
        this.assignmentDescription = assignmentDescription;
    }
}

