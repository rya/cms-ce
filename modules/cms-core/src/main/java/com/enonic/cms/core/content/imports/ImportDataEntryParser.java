/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.imports;

import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntrySet;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.KeywordsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyImportBlockConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;

/**
 * Apr 17, 2009
 */
public class ImportDataEntryParser
{

    private RelatedContentFinder relatedContentFinder;

    private ContentTypeConfig contentTypeConfig;

    public ImportDataEntryParser( RelatedContentFinder relatedContentFinder, ContentTypeConfig contentTypeConfig )
    {
        this.relatedContentFinder = relatedContentFinder;
        this.contentTypeConfig = contentTypeConfig;
    }

    public CustomContentData parseToCustomContentData( final ImportDataEntry importData, final ContentEntity existingContent )
    {
        final CustomContentData contentData = new CustomContentData( contentTypeConfig );

        parseSet( importData, contentData, true );

        parseBlocks( importData, contentData, new BlockIndexResolver( existingContent ) );

        return contentData;
    }

    private void parseBlocks( final ImportDataEntry importDataEntry, final CustomContentData contentData,
                              final BlockIndexResolver blockIndexResolver )
    {
        for ( final Map.Entry<CtyImportBlockConfig, ImportDataEntry> blockConfigAndValue : importDataEntry.getBlockConfigAndValue().entries() )
        {
            final CtyImportBlockConfig blockImportConfig = blockConfigAndValue.getKey();
            final CtySetConfig blockConfig = contentTypeConfig.getSetConfig( blockImportConfig.getDestination() );
            final ImportDataEntry blockImportDataEntry = blockConfigAndValue.getValue();

            String syncInputName = null;
            if ( blockImportConfig.hasSyncSetting() )
            {
                final CtyImportMappingConfig mappingConfigForSync = blockImportConfig.getSyncMapping();
                syncInputName = mappingConfigForSync.getDestination();
            }

            final int blockGroupIndex =
                blockIndexResolver.resolve( blockConfig.getName(), syncInputName, blockImportDataEntry.getSyncValue() );
            final DataEntrySet blockEntrySet = getNewGroupBlock( contentData, blockConfig, blockGroupIndex );
            parseSet( blockImportDataEntry, blockEntrySet, false );
        }
    }

    private void parseSet( final ImportDataEntry importData, final DataEntrySet entrySet, final boolean allowBlockCreation )
    {
        int nextBinaryKeyPlaceHolderIndex = 0;
        for ( final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue : importData.getConfigAndValueMap().entrySet() )
        {
            final DataEntryConfig dataEntryConfig = contentTypeConfig.getInputConfig( configAndValue.getKey().getDestination() );

            DataEntrySet dataEntrySet = null;
            final boolean inBlockGroup = dataEntryConfig.getSetConfig().getGroupXPath() != null;
            if ( inBlockGroup && allowBlockCreation )
            {
                dataEntrySet = getBlockGroup( entrySet, dataEntryConfig.getSetConfig() );
            }
            else
            {
                dataEntrySet = entrySet;
            }

            final DataEntry dataEntry = dataEntrySet.getEntry( dataEntryConfig.getName() );
            boolean dataEntryAlreadyExist = dataEntry != null;

            if ( dataEntryAlreadyExist && inBlockGroup && allowBlockCreation )
            {
                /* Entry already exist in block group - create a new one */
                dataEntrySet = getNewBlockGroup( entrySet, dataEntryConfig.getSetConfig() );
                dataEntryAlreadyExist = false;
            }

            if ( !dataEntryAlreadyExist )
            {
                final DataEntry newDataEntry = getNewDataEntry( dataEntryConfig, configAndValue, nextBinaryKeyPlaceHolderIndex );
                dataEntrySet.add( newDataEntry );
            }
        }
    }

    private DataEntrySet getBlockGroup( final DataEntrySet entrySet, final CtySetConfig setConfig )
    {
        final List<GroupDataEntry> groups = entrySet.getGroupDataSets( setConfig.getName() );

        if ( groups.isEmpty() )
        {
            return getNewGroupBlock( entrySet, setConfig, 1 );
        }
        return groups.get( groups.size() - 1 );
    }

    private DataEntrySet getNewBlockGroup( final DataEntrySet entrySet, final CtySetConfig setConfig )
    {
        final List<GroupDataEntry> groups = entrySet.getGroupDataSets( setConfig.getName() );
        return getNewGroupBlock( entrySet, setConfig, groups.size() + 1 );
    }

    private DataEntrySet getNewGroupBlock( final DataEntrySet entrySet, final CtySetConfig setConfig, final int blockGroupIndex )
    {
        final GroupDataEntry group = new GroupDataEntry( setConfig.getName(), setConfig.getGroupXPath(), blockGroupIndex );
        entrySet.add( group );
        return group;
    }

    private DataEntry getNewDataEntry( final DataEntryConfig dataEntryConfig,
                                       final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue,
                                       int nextBinaryKeyPlaceHolderIndex )
    {
        final CtyImportMappingConfig mappingConfig = configAndValue.getKey();
        final AbstractSourceValue value = configAndValue.getValue();

        switch ( dataEntryConfig.getType() )
        {

// String based types
            case TEXT:
                return new TextDataEntry( dataEntryConfig, ImportValueFormater.getText( value ) );

            case TEXT_AREA:
                return new TextAreaDataEntry( dataEntryConfig, ImportValueFormater.getTextArea( value ) );

            case XML:
                return new XmlDataEntry( dataEntryConfig, ImportValueFormater.getXml( value ) );

            case HTMLAREA:
                return new HtmlAreaDataEntry( dataEntryConfig, ImportValueFormater.getHtmlArea( value ) );

            case URL:
                return new UrlDataEntry( dataEntryConfig, ImportValueFormater.getUrl( value ) );

            case RADIOBUTTON:
            case DROPDOWN:
                return new SelectorDataEntry( dataEntryConfig, ImportValueFormater.getSelector( value ) );

// Content Key based types

            case IMAGE:
                return new ImageDataEntry( dataEntryConfig, ImportValueFormater.getContentKey( value ) );

            case FILE:
                return new FileDataEntry( dataEntryConfig, ImportValueFormater.getContentKey( value ) );

            case RELATEDCONTENT:
                return getRelatedContentEntry( dataEntryConfig, configAndValue );

// Other types
            case CHECKBOX:
                return new BooleanDataEntry( dataEntryConfig, ImportValueFormater.getBoolean( value ) );

            case DATE:
                return new DateDataEntry( dataEntryConfig, ImportValueFormater.getDate( value, mappingConfig.getFormat() ) );

            case BINARY:
                final String binaryName = ImportValueFormater.getAdditionalValue( value, "<unknown>" );
                return new BinaryDataEntry( dataEntryConfig, "%" + String.valueOf( nextBinaryKeyPlaceHolderIndex++ ),
                                            ImportValueFormater.getBinary( value ), binaryName );

            case KEYWORDS:
                return new KeywordsDataEntry( dataEntryConfig, ImportValueFormater.getKeywords( value ) );

// Unsupported types
            case MULTIPLE_CHOICE: // ???
            case FILES:  // Discontinuted
            case IMAGES: // Discontinuted
            default:
                throw new ImportException( "Import of data entrys of type \"" + dataEntryConfig.getType() + "\" is not supported." );
        }
    }

    private DataEntry getRelatedContentEntry( final DataEntryConfig dataEntryConfig,
                                              final Map.Entry<CtyImportMappingConfig, AbstractSourceValue> configAndValue )
    {
        final RelatedContentDataEntryConfig relConfig = (RelatedContentDataEntryConfig) dataEntryConfig;

        final CtyImportMappingConfig importMappingConfig = configAndValue.getKey();

        final boolean lookupRelatedContent = importMappingConfig.getRelatedContentType() != null;

        final AbstractSourceValue sourceValue = configAndValue.getValue();

        if ( relConfig.isMultiple() )
        {
            /* Multiple content */
            List<ContentKey> contentKeys;
            if ( lookupRelatedContent )
            {
                contentKeys = relatedContentFinder.getContentKeys( configAndValue );
            }
            else
            {
                contentKeys = ImportValueFormater.getContentKeys( sourceValue );
            }

            final RelatedContentsDataEntry relatedContents = new RelatedContentsDataEntry( dataEntryConfig );
            for ( ContentKey contentKey : contentKeys )
            {
                relatedContents.add( new RelatedContentDataEntry( dataEntryConfig, contentKey ) );
            }
            return relatedContents;
        }
        else
        {
            /* Single content */
            ContentKey contentKey;
            if ( lookupRelatedContent )
            {
                contentKey = relatedContentFinder.getContentKey( configAndValue );
            }
            else
            {
                contentKey = ImportValueFormater.getContentKey( sourceValue );
            }
            return new RelatedContentDataEntry( dataEntryConfig, contentKey );
        }
    }
}
