/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.store.dao.BinaryDataDao;
import com.enonic.cms.store.dao.ContentDao;

import com.enonic.cms.core.content.index.ContentIndexService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.ContentTypeKey;
import com.enonic.cms.domain.content.index.BigText;
import com.enonic.cms.domain.content.index.ContentDocument;
import com.enonic.cms.domain.content.index.SimpleText;
import com.enonic.cms.domain.content.index.config.IndexDefinition;
import com.enonic.cms.domain.content.index.config.IndexDefinitionBuilder;
import com.enonic.cms.domain.security.user.UserEntity;

public final class IndexServiceImpl
        implements IndexService
{

    private static final Logger LOG = LoggerFactory.getLogger( IndexServiceImpl.class );

    @Autowired
    private ContentIndexService contentIndexService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private BinaryDataDao binaryDataDao;

    private final IndexDefinitionBuilder indexDefBuilder = new IndexDefinitionBuilder();

    public void removeContent( ContentEntity content )
    {
        doRemoveIndex( content );
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class, timeout = 3600)
    /* timeout: 12 timer (60 sec * 5 min = 300 sec) */
    /* OLD: timeout: 12 timer (3600 * 12 = 43200) */
    public void regenerateIndex( List<ContentKey> contentKeys )
    {
        for ( ContentKey contentKey : contentKeys )
        {
            ContentEntity content = contentDao.findByKey( contentKey );

            if ( content.isDeleted() )
            {
                doRemoveIndex( content );
            }
            else
            {
//                doIndex( content, true );
                doIndex( content, false );
            }
        }

        /* Clear all intances in first level cache since the transaction boundary doesn't (single session) */
        contentDao.getHibernateTemplate().clear();
    }

    public void index( ContentEntity content )
    {
//        doIndex( content, true );
        doIndex( content, false );
    }

    public void index( ContentEntity content, boolean deleteExisting )
    {
        doIndex( content, deleteExisting );
    }

    private int doRemoveIndex( ContentEntity content )
    {
        return contentIndexService.remove( content.getKey() );
    }

    private void doIndex( ContentEntity content, boolean deleteExisting )
    {
        ContentDocument indexedDoc = insertStandardValues( content );
        insertUserDefinedIndexValues( content, indexedDoc );
        insertBinaryIndexValues( content, indexedDoc );
        contentIndexService.index( indexedDoc, deleteExisting );

        contentDao.getHibernateTemplate().flush();
    }

    /**
     * Checks the database to see if there are any binary files associated with the document, and if so, an attempt is made to include the
     * contents of the binary file in the document to be indexed.
     * <p/>
     * Whether such an attempt at including contents of a binary file is successful or not, depends on whether a text extractor for the file
     * type exists.
     *
     * @param content    The content for which to look for binary data.
     * @param indexedDoc The document to check for binary files.  This document must have it's key set, and if there are binary files for
     *                   the document, full text data will be added.
     */
    private void insertBinaryIndexValues( ContentEntity content, ContentDocument indexedDoc )
    {
        Set<ContentBinaryDataEntity> binaryDataRef = content.getMainVersion().getContentBinaryData();
        for ( ContentBinaryDataEntity cbd : binaryDataRef )
        {
            BinaryDataEntity binaryData = cbd.getBinaryData();
            try
            {
                final BigText extractedText = extractText( binaryData );
                if ( extractedText != null )
                {
                    indexedDoc.setBinaryExtractedText( extractedText );
                }
            }
            catch ( Exception e )
            {
                StringBuffer sb = new StringBuffer();
                sb.append( "Failed to extract full text from binary data" );
                sb.append( "(key: " ).append( binaryData.getKey() ).append( ", name: " ).append( binaryData.getName() ).append(
                        ") from content" );
                sb.append( "(key: " ).append( content.getKey() ).append( ", type: " ).append( content.getContentType().getName() );
                sb.append( ", category: " ).append( content.getCategory().getName() ).append( "): " ).append( e.getMessage() );
                LOG.warn( sb.toString() );
            }
        }
    }

    private BigText extractText( BinaryDataEntity binaryData )
            throws Exception
    {
        return null;
    }

    /**
     * Retrieves the user defined index configuration and uses it to extract the text from the content which should be indexed.
     *
     * @param content    The content to be indexed.
     * @param indexedDoc The object to store the values to be indexed in.
     */
    private void insertUserDefinedIndexValues( ContentEntity content, ContentDocument indexedDoc )
    {
        Document doc = content.getMainVersion().getContentDataAsJDomDocument();
        for ( IndexDefinition def : this.indexDefBuilder.buildList( content.getContentType() ) )
        {
            for ( final String stringValue : def.evaluate( doc ) )
            {
                indexedDoc.addUserDefinedField( def.getName(), new SimpleText( stringValue ) );
            }
        }
    }

    /**
     * Initializes a <code>ContentDocument</code> for indexing, with all the standard values it should contain.
     *
     * @param content The entity that contains the values to store in the search engine.
     * @return A <code>ContentDocument</code> with all the standard values from the XML.
     */
    private ContentDocument insertStandardValues( ContentEntity content )
    {

        final ContentKey contentKey = content.getKey();
        final ContentTypeEntity contentType = content.getContentType();
        final CategoryEntity category = content.getCategory();

        final ContentVersionEntity contentVersion = content.getMainVersion();
        final UserEntity owner = content.getOwner();
        final UserEntity modifier = contentVersion.getModifiedBy();

        Date createdDate = content.getCreatedAt();
        Date publishFromDate = content.getAvailableFrom();
        Date publishToDate = content.getAvailableTo();

        String title = contentVersion.getTitle();

        ContentDocument indexedDoc = new ContentDocument( contentKey );
        indexedDoc.setCategoryKey( category.getKey() );
        indexedDoc.setContentTypeKey( new ContentTypeKey( contentType.getKey() ) );
        indexedDoc.setContentTypeName( contentType.getName() );
        if ( createdDate != null )
        {
            indexedDoc.setCreated( createdDate );
        }
        indexedDoc.setModifierKey( modifier.getKey().toString() );
        indexedDoc.setModifierQualifiedName( modifier.getQualifiedName().toString() );
        indexedDoc.setOwnerKey( owner.getKey().toString() );
        indexedDoc.setOwnerQualifiedName( owner.getQualifiedName().toString() );
        if ( content.getAssignee() != null )
        {
            indexedDoc.setAssigneeKey( content.getAssignee().getKey() );
            indexedDoc.setAssigneeQualifiedName( content.getAssignee().getQualifiedName().toString() );
        }
        if ( content.getAssigner() != null )
        {
            indexedDoc.setAssignerKey( content.getAssigner().getKey() );
            indexedDoc.setAssignerQualifiedName( content.getAssigner().getQualifiedName().toString() );
        }
        if ( content.getAssignmentDueDate() != null )
        {
            indexedDoc.setAssignmentDueDate( content.getAssignmentDueDate() );
        }

        if ( publishFromDate != null )
        {
            indexedDoc.setPublishFrom( publishFromDate );
        }
        if ( publishToDate != null )
        {
            indexedDoc.setPublishTo( publishToDate );
        }
        indexedDoc.setTimestamp( content.getTimestamp() );
        indexedDoc.setModified( contentVersion.getModifiedAt() );
        indexedDoc.setTitle( title );
        indexedDoc.setStatus( contentVersion.getStatus().getKey() );
        indexedDoc.setPriority( content.getPriority() );

        return indexedDoc;

    }

}
