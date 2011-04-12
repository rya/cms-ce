/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.enonic.cms.core.content.ContentKey;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.io.FileUtil;

import com.enonic.cms.core.content.contentdata.ContentDataParserException;
import com.enonic.cms.core.content.contentdata.ContentDataParserInvalidDataException;
import com.enonic.cms.core.content.contentdata.ContentDataParserUnsupportedTypeException;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.FormItemsGroup;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.FilesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.ImagesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.BinaryDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.CheckboxDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DateDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FileDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FilesDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImageDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImagesDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

/**
 * Created by rmy - Date: Jun 10, 2009
 */
public class CustomContentDataFormParser
{
    private static final DateFormat dateFormatFrom = new SimpleDateFormat( "dd.MM.yyyy" );

    private int binaryDataPlaceHolderCounter = 0;

    private ContentTypeConfig contentTypeConfig;

    private ExtendedMap formItems;

    private boolean parseOnlyCheckboxesMarkedAsInlcluded = false;

    private static final String UPLOADFILE_NEW_ITEM_SUFFIX = "_new";

    public CustomContentDataFormParser( ContentTypeConfig contentTypeConfig, ExtendedMap formItems )
    {
        this.contentTypeConfig = contentTypeConfig;
        this.formItems = formItems;
    }

    public void setParseOnlyCheckboxesMarkedAsInlcluded( boolean value )
    {
        this.parseOnlyCheckboxesMarkedAsInlcluded = value;
    }

    public CustomContentData parseContentData()
    {
        List<DataEntry> parsedEntries = new ArrayList<DataEntry>();

        for ( CtySetConfig setConfig : contentTypeConfig.getForm().getSetConfig() )
        {
            parsedEntries.addAll( parseEntriesInBlock( setConfig ) );
        }

        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        for ( DataEntry dataEntry : parsedEntries )
        {
            contentData.add( dataEntry );
        }

        return contentData;
    }


    private List<DataEntry> parseEntriesInBlock( CtySetConfig blockConfig )
    {
        List<DataEntry> entries = new ArrayList<DataEntry>();

        if ( isGroupTypeConfig( blockConfig ) )
        {
            entries.addAll( parseEntriesInGroupBlock( blockConfig ) );
        }
        else
        {
            entries.addAll( parseEntriesInNormalBlock( blockConfig ) );
        }

        return entries;
    }

    private List<GroupDataEntry> parseEntriesInGroupBlock( CtySetConfig blockConfig )
    {
        List<GroupDataEntry> groupDataEntries = new ArrayList<GroupDataEntry>();

        FormItemsGroup formEntriesGroup = new FormItemsGroup( blockConfig.getName(), formItems );

        final DataEntryParser dataEntryParser = new DataEntryParser();

        for ( Integer groupIndex : formEntriesGroup.getGroupIndexes() )
        {
            final Set<String> entryNamesAlreadyAdded = new HashSet<String>();

            GroupDataEntry groupDataEntry = new GroupDataEntry( blockConfig.getName(), blockConfig.getGroupXPath(), groupIndex );
            groupDataEntry.setConfig( blockConfig );

            List<String> entriesInGroup = formEntriesGroup.getFormEntriesInGroup( groupIndex );

            for ( String entryInGroup : entriesInGroup )
            {
                String entryInGroupConfigName = formEntriesGroup.getFormEntryConfigName( entryInGroup );

                DataEntryConfig dataEntryConfig = blockConfig.getInputConfig( entryInGroupConfigName );

                if ( dataEntryConfig == null )
                {
                    dataEntryConfig = getBaseBinaryDataEntryConfigForGroup( entryInGroup, blockConfig, entryInGroupConfigName );
                }

                if ( dataEntryConfig != null )
                {
                    DataEntry dataEntry = dataEntryParser.parseDataEntry( entryInGroup, dataEntryConfig );

                    if ( dataEntry != null )
                    {
                        groupDataEntry.add( dataEntry );
                        entryNamesAlreadyAdded.add( entryInGroup );
                    }
                }
            }

            List<DataEntry> checkBoxesToAdd = parseMissingCheckBoxEntries( blockConfig, entryNamesAlreadyAdded, groupIndex );

            for ( DataEntry checkBoxEntry : checkBoxesToAdd )
            {
                groupDataEntry.add( checkBoxEntry );
            }

            groupDataEntries.add( groupDataEntry );
        }

        return groupDataEntries;
    }

    private List<DataEntry> parseEntriesInNormalBlock( final CtySetConfig blockConfig )
    {
        final DataEntryParser dataEntryParser = new DataEntryParser();
        final List<DataEntry> entries = new ArrayList<DataEntry>();
        final Set<String> entryNamesAlreadyAdded = new HashSet<String>();
        for ( Object formItemKey : formItems.keySet() )
        {
            final String formInputName = (String) formItemKey;
            DataEntryConfig dataEntryConfig = getConfig( blockConfig, formInputName );

            if ( dataEntryConfig == null )
            {
                dataEntryConfig = getBaseBinaryDataEntryConfig( formInputName, blockConfig );
            }

            if ( dataEntryConfig != null )
            {
                DataEntry dataEntry = dataEntryParser.parseDataEntry( formInputName, dataEntryConfig );

                if ( dataEntry != null )
                {
                    entries.add( dataEntry );
                    entryNamesAlreadyAdded.add( formInputName );
                }
            }
        }

        entries.addAll( parseMissingCheckBoxEntries( blockConfig, entryNamesAlreadyAdded ) );
        return entries;
    }


    private DataEntryConfig getBaseBinaryDataEntryConfigForGroup( String formInputName, CtySetConfig blockConfig,
                                                                  String entryInGroupConfigName )
    {
        return doGetBaseBinaryDataEntryConfig( formInputName, blockConfig, entryInGroupConfigName );
    }

    private DataEntryConfig getBaseBinaryDataEntryConfig( String formInputName, CtySetConfig blockConfig )
    {
        return doGetBaseBinaryDataEntryConfig( formInputName, blockConfig, null );
    }


    private DataEntryConfig doGetBaseBinaryDataEntryConfig( String formInputName, CtySetConfig blockConfig, String entryInGroupConfigName )
    {
        DataEntryConfig dataEntryConfig;

        if ( !formInputName.endsWith( UPLOADFILE_NEW_ITEM_SUFFIX ) )
        {
            return null;
        }

        String baseBinaryDataEntryKey = formInputName.substring( 0, formInputName.indexOf( UPLOADFILE_NEW_ITEM_SUFFIX ) );

        boolean baseBinaryDataEntryKeyInFormItems = formItems.containsKey( baseBinaryDataEntryKey );

        if ( baseBinaryDataEntryKeyInFormItems )
        {
            return null;
        }

        String baseBinaryDataEntryConfigName = baseBinaryDataEntryKey;

        boolean isGroupItem = StringUtils.isNotEmpty( entryInGroupConfigName );

        if ( isGroupItem )
        {
            baseBinaryDataEntryConfigName =
                entryInGroupConfigName.substring( 0, entryInGroupConfigName.indexOf( UPLOADFILE_NEW_ITEM_SUFFIX ) );
        }

        dataEntryConfig = getConfig( blockConfig, baseBinaryDataEntryConfigName );

        if ( dataEntryConfig != null && dataEntryConfig.getType().equals( DataEntryConfigType.BINARY ) )
        {
            return dataEntryConfig;
        }

        return null;
    }

    private List<DataEntry> parseMissingCheckBoxEntries( final CtySetConfig blockConfig, final Set<String> entryNamesAlreadyAdded )
    {
        return parseMissingCheckBoxEntries( blockConfig, entryNamesAlreadyAdded, null );
    }


    private List<DataEntry> parseMissingCheckBoxEntries( final CtySetConfig blockConfig, final Set<String> entryNamesAlreadyAdded,
                                                         Integer groupIndex )
    {
        List<DataEntry> entries = new ArrayList<DataEntry>();

        final DataEntryParser dataEntryParser = new DataEntryParser();
        dataEntryParser.treatNonExistingSubmittedValueAsNull = true;

        for ( DataEntryConfig dataEntryConfig : blockConfig.getInputConfigs() )
        {
            if ( dataEntryConfig.getType() == DataEntryConfigType.CHECKBOX )
            {
                boolean parsingWithinGroup = groupIndex != null;

                final String inputName;

                if ( parsingWithinGroup )
                {
                    inputName = createGroupDataEntryInputName( groupIndex, dataEntryConfig );
                }
                else
                {
                    inputName = dataEntryConfig.getName();
                }

                final boolean checkboxEntryIsNotMissing = entryNamesAlreadyAdded.contains( inputName );

                if ( checkboxEntryIsNotMissing )
                {
                    continue;
                }
                if ( parseOnlyCheckboxesMarkedAsInlcluded && !isCheckboxInputMarkedAsIncluded( inputName ) )
                {
                    continue;
                }

                DataEntry dataEntry = dataEntryParser.parseDataEntry( inputName, dataEntryConfig );
                if ( dataEntry != null )
                {
                    entries.add( dataEntry );
                }
            }
        }

        return entries;
    }


    private String createGroupDataEntryInputName( int groupIndex, DataEntryConfig dataEntryConfig )
    {
        return dataEntryConfig.getSetConfig().getName() + '[' + groupIndex + ']' + '.' + dataEntryConfig.getName();
    }


    private boolean isCheckboxInputMarkedAsIncluded( String inputName )
    {
        String[] values = formItems.getStringArray( "_included_checkbox" );
        for ( String value : values )
        {
            if ( inputName.equals( value ) )
            {
                return true;
            }
        }
        return false;
    }

    private DataEntryConfig getConfig( CtySetConfig blockConfig, String inputName )
    {
        DataEntryConfig dataEntryConfig;

        dataEntryConfig = blockConfig.getInputConfig( inputName );

        return dataEntryConfig;
    }

    private boolean isGroupTypeConfig( CtySetConfig contentTypeConfig )
    {
        return contentTypeConfig.getGroupXPath() != null;
    }

    protected String getNextBinaryPlaceholder()
    {
        String placeHolder = "%" + binaryDataPlaceHolderCounter;
        binaryDataPlaceHolderCounter++;
        return placeHolder;
    }

    private class DataEntryParser
    {
        private boolean treatNonExistingSubmittedValueAsNull = false;

        private DataEntry parseDataEntry( String name, DataEntryConfig config )
        {
            DataEntryConfigType type = config.getType();

            switch ( type )
            {
                case TEXT:
                    return parseTextDataEntry( name, (TextDataEntryConfig) config );
                case TEXT_AREA:
                    return parseTextAreaDataEntry( name, (TextAreaDataEntryConfig) config );
                case HTMLAREA:
                    return parseHtmlAreaEntry( name, (HtmlAreaDataEntryConfig) config );
                case DATE:
                    return parseDateDataEntry( name, (DateDataEntryConfig) config );
                case BINARY:
                    return parseBinaryDataEntry( name, (BinaryDataEntryConfig) config );
                case CHECKBOX:
                    return parseCheckboxDataEntry( name, (CheckboxDataEntryConfig) config );
                case IMAGES:
                    return parseImagesDataEntry( name, (ImagesDataEntryConfig) config ); // Verify; If not isArrayFormItem
                case IMAGE:
                    return parseImageDataEntry( name, (ImageDataEntryConfig) config );
                case RELATEDCONTENT:
                    return parseRelatedContentDataEntry( name, (RelatedContentDataEntryConfig) config );
                case FILES:
                    return parseFilesDataEntry( name, (FilesDataEntryConfig) config );
                case FILE:
                    return parseFileDataEntry( name, (FileDataEntryConfig) config );
                case RADIOBUTTON:
                    return parseRadioButtonDataEntry( name, config );
                case URL:
                    return parseURLDataEntry( name, config );
                case XML:
                    return parseXmlDataEntry( name, config );
                case DROPDOWN:
                    return parseDropdownDataEntry( name, config );
                case MULTIPLE_CHOICE:
                    return parseMultipleChoiceDataEntry();
                case KEYWORDS:
                    return null;
            }
            return null;
        }

        private TextDataEntry parseTextDataEntry( String name, TextDataEntryConfig config )
        {
            String value = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new TextDataEntry( config, value );
        }

        private TextAreaDataEntry parseTextAreaDataEntry( String name, TextAreaDataEntryConfig config )
        {
            String value = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new TextAreaDataEntry( config, value );
        }

        private HtmlAreaDataEntry parseHtmlAreaEntry( String name, HtmlAreaDataEntryConfig config )
        {
            String value = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new HtmlAreaDataEntry( config, value );
        }

        private DateDataEntry parseDateDataEntry( String name, DateDataEntryConfig config )
        {
            String dateString = ensureNotNull( getSubmittedStringValue( name, formItems ) );

            if ( StringUtils.isEmpty( dateString ) )
            {
                return new DateDataEntry( config, null );
            }

            DateDataEntry dataEntry;

            try
            {
                Date date = dateFormatFrom.parse( dateString );
                dataEntry = new DateDataEntry( config, date );
            }
            catch ( ParseException e )
            {
                String message = "Failed to parse date from input: " + dateString;
                throw new ContentDataParserInvalidDataException( message );
            }

            return dataEntry;
        }

        private BinaryDataEntry parseBinaryDataEntry( String name, BinaryDataEntryConfig config )
        {
            String newUploadFileItemKey = name;

            boolean isUploadFileFormItem = name.endsWith( UPLOADFILE_NEW_ITEM_SUFFIX );

            if ( !isUploadFileFormItem )
            {
                newUploadFileItemKey = getBinaryDataEntryUploadKey( name );
            }

            FileItem fileItem = formItems.getFileItem( newUploadFileItemKey, null );

            // This is done to support old forms submitting FileItems in the base-key
            if ( fileItem == null && !isUploadFileFormItem )
            {
                fileItem = tryFetchFileItemFromBinaryDataUploadBaseKey( name );
            }

            boolean newBinaryUploaded = fileItem != null;

            if ( newBinaryUploaded )
            {
                String fileName = FileUtil.getFileName( fileItem );

                byte[] binaryData = getBinaryData( fileItem );

                return new BinaryDataEntry( config, getNextBinaryPlaceholder(), binaryData, fileName );
            }

            String existingBinaryKey = ensureNotNull( getSubmittedStringValue( name, formItems ) );

            boolean existingBinaryKeyGiven = StringUtils.isNotEmpty( existingBinaryKey );

            if ( existingBinaryKeyGiven )
            {
                return new BinaryDataEntry( config, new Integer( existingBinaryKey ) );
            }

            return new BinaryDataEntry( config );
        }

        private FileItem tryFetchFileItemFromBinaryDataUploadBaseKey( String name )
        {
            FileItem fileItem;

            try
            {
                fileItem = formItems.getFileItem( name, null );
            }
            catch ( ClassCastException e )
            {
                fileItem = null;
            }

            return fileItem;
        }

        private String getBinaryDataEntryUploadKey( String name )
        {
            return name + UPLOADFILE_NEW_ITEM_SUFFIX;
        }

        private BooleanDataEntry parseCheckboxDataEntry( String name, CheckboxDataEntryConfig config )
        {
            String stringValue = ensureNotNull( getSubmittedStringValue( name, formItems ) );

            Boolean value;
            if ( StringUtils.isNotEmpty( stringValue ) )
            {
                value = true;
            }
            else
            {
                value = false;
            }

            return new BooleanDataEntry( config, value );
        }

        private ImagesDataEntry parseImagesDataEntry( String name, ImagesDataEntryConfig config )
        {
            ImagesDataEntry imagesDataEntry = new ImagesDataEntry( config );

            if ( StringUtils.isEmpty( ensureNotNull( getSubmittedStringValue( name, formItems ) ) ) )
            {
                return imagesDataEntry;
            }

            if ( isArrayFormItem( formItems, name ) )
            {
                String[] images = (String[]) formItems.get( name );
                String[] text = (String[]) formItems.get( name + "text", null );

                for ( int i = 0; i < images.length; i++ )
                {
                    if ( images[i] == null || images[i].length() == 0 )
                    {
                        continue;
                    }

                    ContentKey imageContentKey = new ContentKey( images[i] );
                    String imageText = ensureNotNull( text[i] );

                    ImageDataEntry imageDataEntry = new ImageDataEntry( config, imageContentKey, imageText );
                    imagesDataEntry.add( imageDataEntry );
                }
            }
            else
            {
                String imageKeyStr = ensureNotNull( getSubmittedStringValue( name, formItems ) );
                String imageText = ensureNotNull( formItems.getString( name + "text", null ) );

                ContentKey imageContentKey = new ContentKey( imageKeyStr );

                imagesDataEntry.add( new ImageDataEntry( config, imageContentKey, imageText ) );
            }

            return imagesDataEntry;
        }

        private ImageDataEntry parseImageDataEntry( String name, ImageDataEntryConfig config )
        {
            String imageString = ensureNotNull( getSubmittedStringValue( name, formItems ) );

            ContentKey imageContentKey = null;
            if ( StringUtils.isNotEmpty( imageString ) )
            {
                imageContentKey = new ContentKey( imageString );
            }

            return new ImageDataEntry( config, imageContentKey );
        }

        private DataEntry parseRelatedContentDataEntry( String name, RelatedContentDataEntryConfig config )
        {
            if ( config.isMultiple() )
            {
                RelatedContentsDataEntry relatedContentsDataEntry = new RelatedContentsDataEntry( config );

                if ( StringUtils.isEmpty( getSubmittedStringValue( name, formItems ) ) )
                {
                    return relatedContentsDataEntry;
                }

                if ( isArrayFormItem( formItems, name ) )
                {
                    String[] contentKeyStrings = (String[]) formItems.get( name );
                    for ( String contentKeyString : contentKeyStrings )
                    {
                        ContentKey contentKey = null;
                        if ( StringUtils.isNotEmpty( contentKeyString ) )
                        {
                            contentKey = new ContentKey( contentKeyString );
                        }
                        relatedContentsDataEntry.add( new RelatedContentDataEntry( config, contentKey ) );
                    }
                }
                else
                {
                    String contentKeyString = ensureNotNull( getSubmittedStringValue( name, formItems ) );
                    ContentKey contentKey = null;
                    if ( StringUtils.isNotEmpty( contentKeyString ) )
                    {
                        contentKey = new ContentKey( contentKeyString );
                    }
                    relatedContentsDataEntry.add( new RelatedContentDataEntry( config, contentKey ) );
                }

                return relatedContentsDataEntry;
            }
            else
            {
                String contentKeyString = getSubmittedStringValue( name, formItems );
                ContentKey contentKey = null;
                if ( StringUtils.isNotEmpty( contentKeyString ) )
                {
                    contentKey = new ContentKey( contentKeyString );
                }
                return new RelatedContentDataEntry( config, contentKey );
            }
        }

        private FilesDataEntry parseFilesDataEntry( String name, FilesDataEntryConfig config )
        {
            FilesDataEntry filesDataEntry = new FilesDataEntry( config );

            if ( StringUtils.isEmpty( getSubmittedStringValue( name, formItems ) ) )
            {
                return filesDataEntry;
            }

            if ( isArrayFormItem( formItems, name ) )
            {
                String[] fileNames = (String[]) formItems.get( name );

                for ( String fileName : fileNames )
                {
                    ContentKey contentKey = null;
                    if ( StringUtils.isNotEmpty( fileName ) )
                    {
                        contentKey = new ContentKey( fileName );
                    }
                    filesDataEntry.add( new FileDataEntry( config, contentKey ) );
                }
            }
            else
            {
                String fileName = ensureNotNull( getSubmittedStringValue( name, formItems ) );
                ContentKey contentKey = null;
                if ( StringUtils.isNotEmpty( fileName ) )
                {
                    contentKey = new ContentKey( fileName );
                }
                filesDataEntry.add( new FileDataEntry( config, contentKey ) );
            }

            return filesDataEntry;
        }

        private FileDataEntry parseFileDataEntry( String name, FileDataEntryConfig config )
        {
            String contentKeyString = getSubmittedStringValue( name, formItems );

            ContentKey contentKey = null;
            if ( StringUtils.isNotEmpty( contentKeyString ) )
            {
                contentKey = new ContentKey( contentKeyString );
            }

            return new FileDataEntry( config, contentKey );
        }

        private UrlDataEntry parseURLDataEntry( String name, DataEntryConfig config )
        {
            String urlString = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new UrlDataEntry( config, urlString );
        }

        private XmlDataEntry parseXmlDataEntry( String name, DataEntryConfig config )
        {
            String xmlString = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new XmlDataEntry( config, xmlString );
        }

        private DataEntry parseDropdownDataEntry( String name, DataEntryConfig config )
        {
            String selectorValue = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new SelectorDataEntry( config, selectorValue );
        }

        private SelectorDataEntry parseRadioButtonDataEntry( String name, DataEntryConfig config )
        {
            String selectorValue = ensureNotNull( getSubmittedStringValue( name, formItems ) );
            return new SelectorDataEntry( config, selectorValue );
        }

        private DataEntry parseMultipleChoiceDataEntry()
        {
            // Det har blitt besluttet at multiple choice ikke skal støttes i user services. (JVS, TSI og JSI, 26. nov, 2009)

            String message = "Elements of type multiple choice is not supported";
            throw new ContentDataParserUnsupportedTypeException( message );
        }

        private boolean isArrayFormItem( Map formItems, String formItemObjectKey )
        {
            if ( formItemObjectKey == null )
            {
                return false;
            }

            if ( !formItems.containsKey( formItemObjectKey ) )
            {
                return false;
            }

            return formItems.get( formItemObjectKey ).getClass() == String[].class;
        }


        private byte[] getBinaryData( FileItem fileItem )
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            InputStream fileInputStream = null;

            try
            {
                fileInputStream = fileItem.getInputStream();

                byte[] buffer = new byte[1024 * 16];
                int size;

                while ( ( size = fileInputStream.read( buffer ) ) > 0 )
                {
                    byteArrayOutputStream.write( buffer, 0, size );
                }

            }
            catch ( IOException e )
            {
                String message = "Failed to read binary data: " + fileItem.getName();
                throw new ContentDataParserException( message, e );
            }
            finally
            {
                try
                {
                    if ( fileInputStream != null )
                    {
                        fileInputStream.close();
                    }
                }
                catch ( IOException ioe )
                {
                    String message = "Failed to close file input stream: %t";
                    throw new ContentDataParserException( message, ioe );
                }
            }
            return byteArrayOutputStream.toByteArray();
        }

        private String ensureNotNull( String value )
        {
            return value == null ? "" : value;
        }

        private String getSubmittedStringValue( String name, ExtendedMap formItems )
        {
            if ( treatNonExistingSubmittedValueAsNull )
            {
                return formItems.getString( name, null );
            }
            else
            {
                return formItems.getString( name );
            }
        }
    }

}
