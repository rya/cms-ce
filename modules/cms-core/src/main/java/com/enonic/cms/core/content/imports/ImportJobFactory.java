/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.io.InputStream;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.category.CategoryAccessType;
import com.enonic.cms.core.content.category.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;

import com.enonic.cms.core.content.category.access.CategoryAccessResolver;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupDao;
import com.enonic.cms.store.dao.UserDao;

import com.enonic.cms.core.content.command.ImportContentCommand;
import com.enonic.cms.core.content.index.ContentIndexService;

import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportModeConfig;
import com.enonic.cms.core.content.contenttype.CtyImportStatusConfig;
import com.enonic.cms.core.security.user.UserEntity;


public class ImportJobFactory
{
    @Autowired
    private ImportService importService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private UserDao userDao;

    public ImportJob createImportJob( final ImportContentCommand command )
    {
        if ( command.publishFrom != null && command.publishTo != null && !command.publishFrom.isBefore( command.publishTo ) )
        {
            throw new ImportException(
                "Given publishFrom (" + command.publishFrom + ") bust be before given publishTo (" + command.publishTo + ")" );
        }
        CtyImportConfig importConfig = getImportConfig( command.importer, command.categoryToImportTo, command.importName );
        ImportDataReader importDataReader = getImportDataReader( importConfig, command.inputStream );

        ImportJobImpl importJob = new ImportJobImpl();

        importJob.setImportService( importService );
        importJob.setContentDao( contentDao );
        importJob.setContentIndexService( contentIndexService );

        importJob.setImporter( command.importer );
        importJob.setCategoryToImportTo( command.categoryToImportTo );
        importJob.setImportConfig( importConfig );
        importJob.setImportDataReader( importDataReader );
        importJob.setDefaultPublishFrom( command.publishFrom );
        importJob.setDefaultPublishTo( command.publishTo );
        importJob.setExecuteInOneTransaction( command.executeInOneTransaction );

        if ( command.assigneeKey != null )
        {
            importJob.setAssignee( userDao.findByKey( command.assigneeKey ) );
            importJob.setAssignmentDescription( command.assignmentDescription );
            importJob.setAssignmentDueDate( command.assignmentDueDate );
        }

        return importJob;
    }

    private CtyImportConfig getImportConfig( final UserEntity user, final CategoryEntity category, final String importName )
    {
        CategoryAccessResolver categoryAccessResolver = new CategoryAccessResolver( groupDao );
        if ( !categoryAccessResolver.hasAccess( user, category, CategoryAccessType.CREATE ) )
        {
            throw new ImportException(
                "Given user (" + user.getDisplayName() + ") does not have create rights on given category, category key: " +
                    category.getKey() + ", category name: " + category.getName() );
        }

        final ContentTypeEntity contentType = category.getContentType();
        if ( contentType == null )
        {
            throw new ImportException(
                "Given category must have a content type, category key: " + category.getKey() + ", category name: " + category.getName() );
        }

        final ContentHandlerName contentHandlerName = contentType.getContentHandlerName();
        if ( !ContentHandlerName.CUSTOM.equals( contentHandlerName ) )
        {
            throw new ImportException(
                "Import only supported when the content type is based on the custom handler, category key: " + category.getKey() +
                    ", category name: " + category.getName() + ", content type: " + contentType.getName() + ", content handler: " +
                    contentHandlerName.getHandlerClassShortName() );
        }

        final ContentTypeConfig contentTypeConfig = contentType.getContentTypeConfig();
        if ( contentTypeConfig == null )
        {
            throw new ImportException(
                "Content type '" + contentType.getName() + "' does not have any configuration, category key: " + category.getKey() +
                    ", category name: " + category.getName() );
        }

        final CtyImportConfig importConfig = contentTypeConfig.getImport( importName );
        if ( importConfig == null )
        {
            throw new ImportException(
                "Content type '" + contentType.getName() + "' does not have any import configuration with given name: " + importName +
                    ", category key: " + category.getKey() + ", category name: " + category.getName() );
        }

        if ( importConfig.getStatus() == CtyImportStatusConfig.APPROVED )
        {
            if ( !categoryAccessResolver.hasAccess( user, category, CategoryAccessType.APPROVE ) )
            {
                throw new ImportException(
                    "Given user does not have publish rights on given category, category key: " + category.getKey() + ", category name: " +
                        category.getName() );
            }
        }
        return importConfig;
    }

    private AbstractImportDataReader getImportDataReader( final CtyImportConfig config, final InputStream data )
    {
        if ( config.getMode() == CtyImportModeConfig.CSV )
        {
            return new ImportDataReaderCsv( config, data );
        }
        else if ( config.getMode() == CtyImportModeConfig.XML )
        {
            return new ImportDataReaderXml( config, data );
        }
        else
        {
            throw new ImportException( "Unknown import mode: " + config.getMode().toString() );
        }
    }
}
