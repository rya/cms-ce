/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.api.client.model.content.BinaryInput;
import com.enonic.cms.api.client.model.content.BooleanInput;
import com.enonic.cms.api.client.model.content.ContentDataInput;
import com.enonic.cms.api.client.model.content.DateInput;
import com.enonic.cms.api.client.model.content.DeprecatedFilesInput;
import com.enonic.cms.api.client.model.content.DeprecatedImagesInput;
import com.enonic.cms.api.client.model.content.FileInput;
import com.enonic.cms.api.client.model.content.GroupInput;
import com.enonic.cms.api.client.model.content.GroupInputImpl;
import com.enonic.cms.api.client.model.content.HtmlAreaInput;
import com.enonic.cms.api.client.model.content.ImageInput;
import com.enonic.cms.api.client.model.content.Input;
import com.enonic.cms.api.client.model.content.RelatedContentInput;
import com.enonic.cms.api.client.model.content.RelatedContentsInput;
import com.enonic.cms.api.client.model.content.SelectorInput;
import com.enonic.cms.api.client.model.content.TextAreaInput;
import com.enonic.cms.api.client.model.content.TextInput;
import com.enonic.cms.api.client.model.content.UrlInput;
import com.enonic.cms.api.client.model.content.XmlInput;

import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.DataEntrySet;
import com.enonic.cms.domain.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.FilesDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.ImagesDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfig;
import com.enonic.cms.domain.content.contenttype.ContentTypeEntity;
import com.enonic.cms.domain.content.contenttype.CtySet;
import com.enonic.cms.domain.content.contenttype.CtySetConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.UrlDataEntryConfig;

public class ContentDataResolver
{

    private int nextBinaryKeyPlaceHolderIndex = 0;

    private Map<String, Integer> groupDataEntryCountByGroupName = new HashMap<String, Integer>();

    public CustomContentData resolveContentdata( ContentDataInput contentDataInput, ContentTypeEntity contentType )
    {

        if ( contentDataInput == null )
        {
            throw new IllegalArgumentException( "Given contentDataInput cannot be null" );
        }
        if ( contentType == null )
        {
            throw new IllegalArgumentException( "Given contentType cannot be null" );
        }
        if ( !contentDataInput.getContentTypeName().equals( contentType.getName() ) )
        {
            throw new IllegalArgumentException(
                "Given content type name '" + contentDataInput.getContentTypeName() + "' is not as expected '" + contentType.getName() +
                    "'" );
        }

        ContentTypeConfig contentTypeConfig = contentType.getContentTypeConfig();
        return doResolveContentdata( contentDataInput, contentTypeConfig );
    }

    private CustomContentData doResolveContentdata( ContentDataInput contentDataInput, ContentTypeConfig contentTypeConfig )
    {

        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        addInputs( contentDataInput, contentData, contentTypeConfig );
        return contentData;
    }

    private void addInputs( GroupInput set, DataEntrySet dataEntrySet, CtySet setConfig )
    {
        List<Input> inputs = set.getInputs();
        for ( Input input : inputs )
        {

            if ( input instanceof GroupInputImpl )
            {

                CtySetConfig subSetConfig = setConfig.getSetConfig( input.getName() );
                if ( subSetConfig == null )
                {
                    throw new IllegalArgumentException( "Input set of name '" + input.getName() + "' does not exist" );
                }
                int groupDataEntryIndex = resolveNextGroupDataEntryIndex( input.getName() );
                GroupDataEntry groupDataEntry = new GroupDataEntry( input.getName(), subSetConfig.getRelativeXPath(), groupDataEntryIndex );
                dataEntrySet.add( groupDataEntry );
                addInputs( (GroupInputImpl) input, groupDataEntry, subSetConfig );

            }
            else
            {

                DataEntryConfig inputConfig = setConfig.getInputConfig( input.getName() );
                if ( inputConfig == null )
                {
                    throw new IllegalArgumentException( "Input of name '" + input.getName() + "' does not exist" );
                }
                DataEntryConfigType type = inputConfig.getType();
                String name = input.getName();

                if ( type == DataEntryConfigType.TEXT )
                {
                    verifyInputClass( input, TextInput.class );
                    TextInput textInput = (TextInput) input;
                    TextDataEntryConfig textDataEntryConfig = (TextDataEntryConfig) inputConfig;
                    if ( textDataEntryConfig.getMaxLength() != null && textInput.getLength() > textDataEntryConfig.getMaxLength() )
                    {
                        throw new IllegalArgumentException(
                            "Input '" + name + "' has a maximum length of " + textDataEntryConfig.getMaxLength() +
                                ", length of given value was : " + textInput.getLength() );
                    }
                    TextDataEntry entry = new TextDataEntry( inputConfig, textInput.getValueAsString() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.TEXT_AREA )
                {
                    verifyInputClass( input, TextAreaInput.class );
                    TextAreaInput textAreaInput = (TextAreaInput) input;
                    TextAreaDataEntry entry = new TextAreaDataEntry( inputConfig, textAreaInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.BINARY )
                {
                    verifyInputClass( input, BinaryInput.class );
                    BinaryInput binaryInput = (BinaryInput) input;
                    BinaryDataEntry entry;
                    if ( binaryInput.hasExistingBinaryKey() )
                    {
                        entry = new BinaryDataEntry( inputConfig, binaryInput.getExistingBinaryKey() );
                    }
                    else
                    {
                        entry = new BinaryDataEntry( inputConfig, "%" + String.valueOf( nextBinaryKeyPlaceHolderIndex++ ),
                                                     binaryInput.getBinary(), binaryInput.getBinaryName() );
                    }
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.HTMLAREA )
                {
                    verifyInputClass( input, HtmlAreaInput.class );
                    HtmlAreaInput htmlAreaInput = (HtmlAreaInput) input;
                    HtmlAreaDataEntry entry = new HtmlAreaDataEntry( inputConfig, htmlAreaInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.XML )
                {
                    verifyInputClass( input, XmlInput.class );
                    XmlInput xmlInput = (XmlInput) input;
                    XmlDataEntry entry = new XmlDataEntry( inputConfig, xmlInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.RELATEDCONTENT )
                {

                    RelatedContentDataEntryConfig relConfig = (RelatedContentDataEntryConfig) inputConfig;

                    if ( !relConfig.isMultiple() )
                    {
                        verifyInputClass( input, RelatedContentInput.class );
                        RelatedContentInput relatedContentInput = (RelatedContentInput) input;
                        RelatedContentDataEntry entry =
                            new RelatedContentDataEntry( inputConfig, new ContentKey( relatedContentInput.getValueAsInt() ) );
                        dataEntrySet.add( entry );
                    }
                    else
                    {
                        verifyInputClass( input, RelatedContentsInput.class );
                        RelatedContentsInput relatedContentsInput = (RelatedContentsInput) input;
                        RelatedContentsDataEntry entry = new RelatedContentsDataEntry( inputConfig );
                        for ( RelatedContentInput relatedContentInput : relatedContentsInput.getRelatedContents() )
                        {
                            entry.add( new RelatedContentDataEntry( inputConfig, new ContentKey( relatedContentInput.getValueAsInt() ) ) );
                        }
                        dataEntrySet.add( entry );
                    }
                }
                else if ( type == DataEntryConfigType.FILE )
                {
                    verifyInputClass( input, FileInput.class );
                    FileInput fileInput = (FileInput) input;
                    FileDataEntry entry = new FileDataEntry( inputConfig, new ContentKey( fileInput.getValueAsInt() ) );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.FILES )
                {
                    verifyInputClass( input, DeprecatedFilesInput.class );
                    DeprecatedFilesInput filesInput = (DeprecatedFilesInput) input;
                    FilesDataEntry entry = new FilesDataEntry( inputConfig );
                    for ( FileInput fileInput : filesInput.getFiles() )
                    {
                        entry.add( new FileDataEntry( inputConfig, new ContentKey( fileInput.getValueAsInt() ) ) );
                    }
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.IMAGE )
                {
                    verifyInputClass( input, ImageInput.class );
                    ImageInput imageInput = (ImageInput) input;
                    ImageDataEntry entry = new ImageDataEntry( inputConfig, new ContentKey( imageInput.getValueAsInt() ) );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.IMAGES )
                {
                    verifyInputClass( input, DeprecatedImagesInput.class );
                    DeprecatedImagesInput imagesInput = (DeprecatedImagesInput) input;
                    ImagesDataEntry entry = new ImagesDataEntry( inputConfig );
                    for ( ImageInput imageInput : imagesInput.getImages() )
                    {
                        entry.add( new ImageDataEntry( inputConfig, new ContentKey( imageInput.getValueAsInt() ), imageInput.getText() ) );
                    }
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.URL )
                {
                    verifyInputClass( input, UrlInput.class );
                    UrlDataEntryConfig urlDataEntryConfig = (UrlDataEntryConfig) inputConfig;
                    UrlInput urlInput = (UrlInput) input;
                    if ( urlDataEntryConfig.getMaxLength() != null && urlInput.getLength() > urlDataEntryConfig.getMaxLength() )
                    {
                        throw new IllegalArgumentException(
                            "Input '" + name + "' has a maximum length of " + urlDataEntryConfig.getMaxLength() +
                                ", length of given value was : " + urlInput.getLength() );
                    }
                    UrlDataEntry entry = new UrlDataEntry( inputConfig, urlInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.DATE )
                {
                    verifyInputClass( input, DateInput.class );
                    DateInput dateInput = (DateInput) input;
                    DateDataEntry entry = new DateDataEntry( inputConfig, dateInput.getValueAsDate() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.RADIOBUTTON || type == DataEntryConfigType.DROPDOWN )
                {
                    verifyInputClass( input, SelectorInput.class );
                    SelectorInput selectorInput = (SelectorInput) input;
                    SelectorDataEntry entry = new SelectorDataEntry( inputConfig, selectorInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else if ( type == DataEntryConfigType.CHECKBOX )
                {
                    verifyInputClass( input, BooleanInput.class );
                    BooleanInput checkboxInput = (BooleanInput) input;
                    BooleanDataEntry entry = new BooleanDataEntry( inputConfig, checkboxInput.getValue() );
                    dataEntrySet.add( entry );
                }
                else
                {
                    throw new IllegalArgumentException( "Input type not supported: " + input.getType() );
                }
            }
        }
    }

    private int resolveNextGroupDataEntryIndex( String groupName )
    {
        Integer count = groupDataEntryCountByGroupName.get( groupName );
        if ( count == null )
        {
            groupDataEntryCountByGroupName.put( groupName, 1 );
            return 1;
        }

        int next = count + 1;
        groupDataEntryCountByGroupName.put( groupName, next );
        return next;
    }

    private void verifyInputClass( Input input, Class expectedClass )
    {

        if ( !input.getClass().isAssignableFrom( expectedClass ) )
        {
            throw new IllegalArgumentException(
                "Input '" + input.getName() + "' was not of expected class " + expectedClass.getName() + ", was " +
                    input.getClass().getName() );
        }
    }


}
