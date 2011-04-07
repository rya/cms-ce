/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.ContentStorer;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentTypeDao;

import com.enonic.cms.core.content.command.UnassignContentCommand;
import com.enonic.cms.core.content.index.ContentIndexService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.imports.ImportDataReader;
import com.enonic.cms.domain.content.imports.ImportResult;
import com.enonic.cms.domain.security.user.UserEntity;

public class ImportServiceImpl
    implements ImportService
{
    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentStorer contentStorer;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentTypeDao contentTypeDao;

    @Autowired
    private ContentIndexService contentIndexService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public boolean importData( ImportDataReader importDataReader, ImportJob importJob )
    {
        return doimportData( importDataReader, importJob );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public boolean importDataWithoutRequiresNewPropagation( ImportDataReader importDataReader, ImportJob importJob )
    {
        return doimportData( importDataReader, importJob );
    }

    private boolean doimportData( ImportDataReader importDataReader, ImportJob importJob )
    {
        try
        {
            ContentImporterImpl contentImporter = new ContentImporterImpl( importJob, importDataReader );
            contentImporter.setContentStorer( contentStorer );
            contentImporter.setContentDao( contentDao );

            RelatedContentFinder relatedContentFinder = new RelatedContentFinder( contentTypeDao, contentIndexService );
            contentImporter.setRelatedContentFinder( relatedContentFinder );

            return contentImporter.importData();
        }
        finally
        {
            /* Clear all intances in first level cache since the transaction boundary doesn't (single session) */
            contentDao.getHibernateTemplate().clear();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public void archiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doArchiveContent( importer, contentKeys, importResult );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public void archiveContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doArchiveContent( importer, contentKeys, importResult );
    }

    private void doArchiveContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        for ( ContentKey contentKey : contentKeys )
        {
            final ContentEntity content = contentDao.findByKey( contentKey );

            if ( content == null )
            {
                return;
            }

            boolean contentArchived = contentStorer.archiveMainVersion( importer, content );
            if ( contentArchived )
            {
                importResult.addArchived( content );

                UnassignContentCommand unassignContentCommand = new UnassignContentCommand();
                unassignContentCommand.setContentKey( content.getKey() );
                unassignContentCommand.setUnassigner( importer.getKey() );
                contentStorer.unassignContent( unassignContentCommand );
            }
            else
            {
                importResult.addAlreadyArchived( content );
            }

        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    public void deleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doDeleteContent( importer, contentKeys, importResult );
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, timeout = 3600)
    public void deleteContentWithoutRequiresNewPropagation( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        doDeleteContent( importer, contentKeys, importResult );
    }

    private void doDeleteContent( UserEntity importer, List<ContentKey> contentKeys, ImportResult importResult )
    {
        for ( ContentKey contentKey : contentKeys )
        {
            final ContentEntity content = contentDao.findByKey( contentKey );

            if ( content == null )
            {
                // content must have been removed by another process during the import
            }
            else
            {
                contentStorer.deleteContent( importer, content );
                importResult.addDeleted( content );
            }
        }
    }
}
