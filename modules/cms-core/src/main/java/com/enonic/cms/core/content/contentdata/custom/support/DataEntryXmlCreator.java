/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.util.List;

import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;

import com.enonic.esl.util.DateUtil;

import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.framework.xml.XMLException;

import com.enonic.cms.core.content.contentdata.ContentDataXPathCreator;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.KeywordsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.MultipleChoiceAlternative;
import com.enonic.cms.core.content.contentdata.custom.MultipleChoiceDataEntry;
import com.enonic.cms.core.content.contentdata.custom.RelationDataEntry;
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
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

/**
 * 07.01.11
 */
public class DataEntryXmlCreator
{
    private FileDataEntryXmlCreator fileDataEntryXmlCreator;

    private BinaryDataEntryXmlCreator binaryDataEntryXmlCreator;

    private boolean inBlockGroup;

    public DataEntryXmlCreator( FileDataEntryXmlCreator fileDataEntryXmlCreator, BinaryDataEntryXmlCreator binaryDataEntryXmlCreator,
                                boolean inBlockGroup )
    {
        this.fileDataEntryXmlCreator = fileDataEntryXmlCreator;
        this.binaryDataEntryXmlCreator = binaryDataEntryXmlCreator;
        this.inBlockGroup = inBlockGroup;
    }

    public void createEntryElement( Element dataEntrySetEl, DataEntry entry )
    {

        if ( entry.getType() == DataEntryType.TEXT )
        {
            verifyClass( entry, TextDataEntry.class );
            addTextDataEntry( dataEntrySetEl, (TextDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.TEXT_AREA )
        {
            verifyClass( entry, TextAreaDataEntry.class );
            addTextAreaDataEntry( dataEntrySetEl, (TextAreaDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.HTML_AREA )
        {
            verifyClass( entry, HtmlAreaDataEntry.class );
            addHtmlAreaDataEntry( dataEntrySetEl, (HtmlAreaDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.XML )
        {
            verifyClass( entry, XmlDataEntry.class );
            addXmlDataEntry( dataEntrySetEl, (XmlDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.BINARY )
        {
            verifyClass( entry, BinaryDataEntry.class );
            binaryDataEntryXmlCreator.createAndAddElement( dataEntrySetEl, (BinaryDataEntry) entry, inBlockGroup );
        }
        else if ( entry.getType() == DataEntryType.FILE )
        {
            verifyClass( entry, FileDataEntry.class );
            fileDataEntryXmlCreator.createAndAddElement( dataEntrySetEl, (FileDataEntry) entry, inBlockGroup );
        }
        else if ( entry.getType() == DataEntryType.RELATED_CONTENT )
        {
            verifyClass( entry, RelatedContentDataEntry.class );
            addRelatedContentDataEntry( dataEntrySetEl, (RelatedContentDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.RELATED_CONTENTS )
        {
            verifyClass( entry, RelatedContentsDataEntry.class );
            addRelatedContentsDataEntry( dataEntrySetEl, (RelatedContentsDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.FILES )
        {
            verifyClass( entry, FilesDataEntry.class );
            fileDataEntryXmlCreator.createAndAddElement( dataEntrySetEl, (FilesDataEntry) entry, inBlockGroup );
        }
        else if ( entry.getType() == DataEntryType.IMAGE )
        {
            verifyClass( entry, ImageDataEntry.class );
            addImageDataEntry( dataEntrySetEl, (ImageDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.IMAGES )
        {
            verifyClass( entry, ImagesDataEntry.class );
            addImagesDataEntry( dataEntrySetEl, (ImagesDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.KEYWORDS )
        {
            verifyClass( entry, KeywordsDataEntry.class );
            addKeywordsDataEntry( dataEntrySetEl, (KeywordsDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.URL )
        {
            verifyClass( entry, UrlDataEntry.class );
            addUrlDataEntry( dataEntrySetEl, (UrlDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.DATE )
        {
            verifyClass( entry, DateDataEntry.class );
            addDateDataEntry( dataEntrySetEl, (DateDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.SELECTOR )
        {
            verifyClass( entry, SelectorDataEntry.class );
            addSelectorDataEntry( dataEntrySetEl, (SelectorDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.BOOLEAN )
        {
            verifyClass( entry, BooleanDataEntry.class );
            addBooleanDataEntry( dataEntrySetEl, (BooleanDataEntry) entry );
        }
        else if ( entry.getType() == DataEntryType.MULTIPLE_CHOICE )
        {
            verifyClass( entry, MultipleChoiceDataEntry.class );
            addMultipleChoiceDataEntry( dataEntrySetEl, (MultipleChoiceDataEntry) entry );
        }
        else
        {
            throw new IllegalArgumentException( "DataEntry type not supported: " + entry.getType() );
        }
    }

    private void addMultipleChoiceDataEntry( Element dataEntrySetEl, MultipleChoiceDataEntry entry )
    {
        Element entryElem = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryElem.addContent( new Element( "text" ).setText( entry.getText() ) );
        for ( MultipleChoiceAlternative alternative : entry.getAlternatives() )
        {
            Element alternativeElement = new Element( "alternative" );
            alternativeElement.setText( alternative.getAlternativeText() );
            alternativeElement.setAttribute( "correct", alternative.isCorrectAsString() );
            entryElem.addContent( alternativeElement );
        }
    }

    private void addBooleanDataEntry( Element dataEntrySetEl, BooleanDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setText( entry.getValueAsString() );
    }

    private void addSelectorDataEntry( Element dataEntrySetEl, SelectorDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setText( entry.getValue() );
    }

    private void addDateDataEntry( Element dataEntrySetEl, DateDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setText( DateUtil.formatISODate( entry.getValue() ) );
    }

    private void addXmlDataEntry( Element dataEntrySetEl, XmlDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.addContent( entry.getValue().getRootElement().detach() );
    }

    private void addRelatedContentsDataEntry( Element dataEntrySetEl, RelatedContentsDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );

        for ( RelationDataEntry rel : entry.getEntries() )
        {
            Element contentEl = new Element( "content" );
            contentEl.setAttribute( "key", rel.getContentKey().toString() );
            entryEl.addContent( contentEl );
        }
    }

    private void addRelatedContentDataEntry( Element dataEntrySetEl, RelatedContentDataEntry entry )
    {
        final RelatedContentDataEntryConfig entryConfig = (RelatedContentDataEntryConfig) entry.getConfig();

        Element entryEl =
            ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entryConfig.getRelativeXPath() ) );

        if ( entryConfig.isMultiple() )
        {
            Element contentEl = new Element( "content" );
            contentEl.setAttribute( "key", entry.getContentKey().toString() );
            entryEl.addContent( contentEl );
        }
        else
        {
            entryEl.setAttribute( "key", entry.getContentKey().toString() );
        }
    }

    private void addUrlDataEntry( Element dataEntrySetEl, UrlDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setText( entry.getValue() );
    }

    private void addKeywordsDataEntry( Element dataEntrySetEl, KeywordsDataEntry entry )
    {
        Element keywordsEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        for ( String s : entry.getKeywords() )
        {
            Element keywordEl = new Element( "keyword" );
            keywordEl.setText( s );
            keywordsEl.addContent( keywordEl );
        }
    }

    private void addTextAreaDataEntry( Element dataEntrySetEl, TextAreaDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        //entryEl.addContent( new CDATA( StringUtil.replaceECC( entry.getValue() ) ) );
        String entryValue = entry.getValue();
        entryEl.addContent( entryValue );
    }

    private void addHtmlAreaDataEntry( final Element dataEntrySetEl, final HtmlAreaDataEntry entry )
    {
        final String wrapElementName = "temp-wraped-element-if-you-can-see-me-something-went-wrong";
        final Element entryEl =
            ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        if ( !entry.isEmpty() )
        {
            final String xmlStr = ensureXmlProlog( wrapElement( entry.getValue(), wrapElementName ) );

            Document document;
            try
            {
                final XMLDocument xml = XMLDocumentFactory.create( xmlStr );
                document = xml.getAsJDOMDocument();
            }
            catch ( XMLException e )
            {
                throw new RuntimeException( "Failed to parse xml when adding entry: " + entry.getName(), e );
            }
            final Element rootEl = document.getRootElement();
            if ( rootEl.getName().equals( wrapElementName ) )
            {
                @SuppressWarnings({"unchecked"}) final List<Content> children = rootEl.cloneContent();
                entryEl.addContent( children );
            }
            else
            {
                entryEl.addContent( (Element) rootEl.clone() );
            }
        }
    }

    private void addTextDataEntry( Element dataEntrySetEl, TextDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setText( entry.getValue() );
    }

    private void addImageDataEntry( Element dataEntrySetEl, ImageDataEntry entry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( entry.getConfig() ) );
        entryEl.setAttribute( "key", entry.getContentKey().toString() );
    }

    private void addImagesDataEntry( Element dataEntrySetEl, ImagesDataEntry arrayEntry )
    {
        Element entryEl = ContentDataXPathCreator.ensurePath( dataEntrySetEl, stripContentdataWhenNotBlockGroup( arrayEntry.getConfig() ) );

        for ( ImageDataEntry entry : arrayEntry.getEntries() )
        {
            Element imageEl = new Element( "image" );
            entryEl.addContent( imageEl );
            imageEl.setAttribute( "key", entry.getContentKey().toString() );
            Element textEl = new Element( "text" );
            imageEl.addContent( textEl );
            textEl.setText( entry.getImageText() );
        }
    }

    private void verifyClass( DataEntry entry, Class expectedClass )
    {
        if ( !entry.getClass().isAssignableFrom( expectedClass ) )
        {
            throw new IllegalArgumentException(
                "Input '" + entry.getName() + "' was not of expected class " + expectedClass.getName() + ", was " +
                    entry.getClass().getName() );
        }
    }


    private String wrapElement( String xmlStr, String elementToWrap )
    {
        if ( xmlStr.startsWith( "<?xml" ) )
        {
            return xmlStr;
        }

        StringBuffer newXml = new StringBuffer();
        newXml.append( "<" ).append( elementToWrap ).append( ">" );
        newXml.append( xmlStr );
        newXml.append( "</" ).append( elementToWrap ).append( ">" );
        return newXml.toString();
    }

    private String ensureXmlProlog( String xmlStr )
    {
        if ( xmlStr.startsWith( "<?xml" ) )
        {
            return xmlStr;
        }
        StringBuffer newXmlStr = new StringBuffer();
        newXmlStr.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        newXmlStr.append( xmlStr );
        return newXmlStr.toString();
    }

    private String stripContentdataWhenNotBlockGroup( DataEntryConfig dataEntryConfig )
    {
        return stripContentdataWhenNotBlockGroup( dataEntryConfig.getRelativeXPath() );
    }

    private String stripContentdataWhenNotBlockGroup( String xpath )
    {
        if ( !inBlockGroup && xpath.startsWith( "contentdata/" ) )
        {
            return xpath.substring( "contentdata/".length() );
        }
        else if ( !inBlockGroup && xpath.startsWith( "/contentdata/" ) )
        {
            return xpath.substring( "/contentdata/".length() );
        }
        return xpath;
    }
}
