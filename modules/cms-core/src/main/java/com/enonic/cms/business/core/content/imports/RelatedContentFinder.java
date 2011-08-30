/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.business.core.content.imports;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import com.enonic.cms.store.dao.ContentTypeDao;

import com.enonic.cms.business.core.content.index.ContentIndexService;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.contentdata.ContentData;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.DataEntry;
import com.enonic.cms.domain.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.AbstractContentKeyBasedInputDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.AbstractStringBasedInputDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.domain.content.imports.ImportException;
import com.enonic.cms.domain.content.imports.ImportValueFormater;
import com.enonic.cms.domain.content.imports.sourcevalueholders.AbstractSourceValue;
import com.enonic.cms.domain.content.imports.sourcevalueholders.StringArraySourceValue;
import com.enonic.cms.domain.content.index.ContentIndexQuery;
import com.enonic.cms.domain.content.index.config.IndexDefinition;
import com.enonic.cms.domain.content.index.config.IndexDefinitionBuilder;
import com.enonic.cms.domain.content.resultset.ContentResultSet;

public class RelatedContentFinder
{
    private ContentTypeDao contentTypeDao;

    private ContentIndexService contentIndexService;

    public RelatedContentFinder( ContentTypeDao contentTypeDao, ContentIndexService contentIndexService )
    {
        this.contentTypeDao = contentTypeDao;
        this.contentIndexService = contentIndexService;
    }

    public ContentKey getContentKey( final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue )
    {
        final ContentIndexQuery query = getQuery( configAndValue );
        final ContentResultSet resSet = contentIndexService.query( query );

        if ( resSet.getLength() != 1 )
        {
            throw new ImportException(
                "Could not find uniqely identify related content with query: \"" + query.getQuery() + "\". " + resSet.getLength() +
                    " contents found. Keys: " + getKeysToDisplay( resSet, 5 ) );
        }
        return resSet.getKey( 0 );
    }

    public List<ContentKey> getContentKeys( final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue )
    {
        final ContentIndexQuery query = getQuery( configAndValue );
        final ContentResultSet resSet = contentIndexService.query( query );

        StringArraySourceValue value = (StringArraySourceValue) configAndValue.getValue();
        LinkedList<String> orderMask = Lists.newLinkedList( value.getValues() );

        String relatedKey = configAndValue.getKey().getRelatedField();

        return getOrderedKeys( resSet, orderMask, relatedKey );
    }

    protected List<ContentKey> getOrderedKeys( final ContentResultSet resSet, List<String> orderMask, String relatedKey )
    {
        List<ContentEntity> contents = Lists.newArrayList( resSet.getContents() );
        Collections.sort( contents, new OrderComparator( orderMask, relatedKey ) );

        List<ContentKey> contentKeys = new LinkedList<ContentKey>();

        for ( ContentEntity content : contents )
        {
            contentKeys.add( content.getKey() );
        }

        return contentKeys;
    }

    private static class OrderComparator
        implements Comparator<ContentEntity>
    {
        private final List<String> orderMask;

        private final String relatedKey;

        private OrderComparator( List<String> orderMask, String relatedKey )
        {
            this.orderMask = orderMask;
            this.relatedKey = relatedKey;
        }

        /*
         * In <code>orderMask</code> linked list the position of element in list is used as order position
         */
        public int compare( ContentEntity content1, ContentEntity content2 )
        {
            String value1 = getValue( content1 );
            String value2 = getValue( content2 );

            Integer order1 = orderMask.indexOf( value1 );
            Integer order2 = orderMask.indexOf( value2 );

            if ( order1 == null || order2 == null )
            {
                return -1;
            }
            return order1 > order2 ? 1 : -1;
        }

        private String getValue( ContentEntity content )
        {
            ContentVersionEntity contentVersion = content.getMainVersion();
            ContentData contentData = contentVersion.getContentData();

            if( contentData instanceof CustomContentData )
            {
                return doGetValueFromCustomContentData( (CustomContentData) contentData );
            }
            else
            {
                return contentData.getTitle();
            }
        }

        private String doGetValueFromCustomContentData( CustomContentData contentData )
        {
            DataEntry dataEntry = contentData.getEntry( this.relatedKey );

            if ( dataEntry instanceof AbstractStringBasedInputDataEntry )
            {
                return ( (AbstractStringBasedInputDataEntry) dataEntry ).getValue();
            }
            else if ( dataEntry instanceof AbstractContentKeyBasedInputDataEntry )
            {
                return ( (AbstractContentKeyBasedInputDataEntry) dataEntry ).getContentKey().toString();
            }
            else if ( dataEntry instanceof DateDataEntry )
            {
                Date date = ( (DateDataEntry) dataEntry ).getValue();

                DateFormat isoFormat = new SimpleDateFormat( "yyyy-MM-dd" );
                return isoFormat.format( date );
            }

            throw new IllegalArgumentException( "Illegal value: " + dataEntry );
        }


    }

    private String getKeysToDisplay( ContentResultSet resSet, int maxKeyCount )
    {
        final StringBuilder msgBuilder = new StringBuilder();
        final int count = Math.min( resSet.getLength(), maxKeyCount );
        for ( int i = 0; i < count; i++ )
        {
            if ( i > 0 )
            {
                msgBuilder.append( ", " );
            }
            msgBuilder.append( resSet.getKey( i ) );
        }
        if ( count < resSet.getLength() )
        {
            msgBuilder.append( "..." );
        }
        return msgBuilder.toString();
    }

    private ContentIndexQuery getQuery( final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue )
    {
        final CtyImportMappingConfig mappingConfig = configAndValue.getKey();
        final ContentTypeEntity contentType = getContentType( mappingConfig );
        final String relatedField = mappingConfig.getRelatedField();
        final String fieldXpath = getRelatedContentXpath( contentType, relatedField );
        final List<String> values = getValues( configAndValue.getValue() );
        final StringBuilder builder = new StringBuilder();
        for ( int i = 0; i < values.size(); i++ )
        {
            if ( i > 0 )
            {
                builder.append( ", " );
            }
            builder.append( "\"" );
            builder.append( values.get( i ) );
            builder.append( "\"" );
        }
        return new ContentIndexQuery(
            "contentTypeKey = " + contentType.getKey() + " AND " + fieldXpath + " IN(" + builder.toString() + ")" );
    }

    private List<String> getValues( AbstractSourceValue value )
    {
        try
        {
            return ImportValueFormater.getRelatedContent( value );
        }
        catch ( ImportException ex )
        {
            throw new ImportException( "Invalid related content value found.", ex );
        }
    }


    private ContentTypeEntity getContentType( CtyImportMappingConfig mappingConfig )
    {
        final String contentTypeName = mappingConfig.getRelatedContentType();
        final ContentTypeEntity contentType = contentTypeDao.findByName( contentTypeName );
        if ( contentType == null )
        {
            throw new ImportException(
                "Could not find related content type \"" + contentTypeName + "\" from import mapping \"" + mappingConfig.getDestination() +
                    "\"." );
        }
        return contentType;
    }

    private String getRelatedContentXpath( final ContentTypeEntity contentType, final String relatedField )
    {
        switch ( contentType.getContentHandlerName() )
        {
            case CUSTOM:
                return getRelatedContentXpathFromCustomContent( contentType, relatedField );

            case FILE:
            case IMAGE:
                return getRelatedContentXpathFromLegacyContent( contentType, relatedField );

            default:
                throw new ImportException(
                    "Import does not support related content types based on handler \"" + contentType.getContentHandlerName() + "\"" );
        }
    }

    private String getRelatedContentXpathFromCustomContent( final ContentTypeEntity contentType, final String relatedField )
    {
        final DataEntryConfig dataEntryConfig = contentType.getContentTypeConfig().getInputConfig( relatedField );
        if ( dataEntryConfig == null )
        {
            throw new ImportException(
                "Could not find field from from mapping attribute \"relatedfield\" with value \"" + relatedField + "\" in content type \"" +
                    contentType.getName() + "\"" );
        }
        final String fieldXpath = dataEntryConfig.getXpath();

        verifyXpath( contentType, relatedField, fieldXpath );
        return fieldXpath;
    }

    private String getRelatedContentXpathFromLegacyContent( final ContentTypeEntity contentType, final String relatedField )
    {
        final String fieldXpath = "contentdata/" + relatedField;
        verifyXpath( contentType, relatedField, fieldXpath );
        return fieldXpath;
    }

    private void verifyXpath( ContentTypeEntity contentType, String relatedField, String fieldXpath )
    {
        if ( !isXpathIndexed( contentType, fieldXpath ) )
        {
            throw new ImportException(
                "Could not find index using xpath \"" + fieldXpath + "\" from from mapping attribute \"relatedfield\" with value \"" +
                    relatedField + "\" in content type \"" + contentType.getName() + "\". Related field must be indexed." );
        }
    }

    private boolean isXpathIndexed( ContentTypeEntity contentType, String syncXpath )
    {
        final List<IndexDefinition> indexes = new IndexDefinitionBuilder().buildList( contentType );
        for ( IndexDefinition index : indexes )
        {
            if ( index.getXPath().equals( syncXpath ) )
            {
                return true;
            }
        }
        return false;
    }
}
