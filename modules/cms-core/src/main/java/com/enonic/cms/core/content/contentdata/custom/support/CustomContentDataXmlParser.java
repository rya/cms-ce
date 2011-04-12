/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.esl.util.DateUtil;

import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntrySet;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.KeywordsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.MultipleChoiceAlternative;
import com.enonic.cms.core.content.contentdata.custom.MultipleChoiceDataEntry;
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
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;

public class CustomContentDataXmlParser
{

    private final static Logger LOG = LoggerFactory.getLogger( CustomContentDataXmlParser.class );

    private Document contentDataXml;

    private ContentTypeConfig contentTypeConfig;

    private List<BinaryDataKey> binaryDatas;

    private KeywordsDataEntryParser keywordsDataEntryParser = new KeywordsDataEntryParser();

    private XmlDataEntryParser xmlDataEntryParser = new XmlDataEntryParser();

    private HtmlAreaDataEntryParser htmlAreaDataEntryParser = new HtmlAreaDataEntryParser();

    public static CustomContentData parse( Document contentDataXml, ContentTypeConfig contentTypeConfig )
    {
        CustomContentDataXmlParser xmlParser = new CustomContentDataXmlParser( contentDataXml, contentTypeConfig );
        return xmlParser.parse();
    }

    public static CustomContentData parse( Document contentDataXml, ContentTypeConfig contentTypeConfig, List<BinaryDataKey> binaryDatas )
    {
        CustomContentDataXmlParser xmlParser = new CustomContentDataXmlParser( contentDataXml, contentTypeConfig );
        xmlParser.setBinaryDatas( binaryDatas );
        return xmlParser.parse();
    }

    public CustomContentDataXmlParser( Document contentDataXml, ContentTypeConfig contentTypeConfig )
    {
        if ( contentDataXml == null )
        {
            throw new IllegalArgumentException( "Given contentDataXml cannot be null" );
        }
        if ( contentTypeConfig == null )
        {
            throw new IllegalArgumentException( "Given contentTypeConfig cannot be null" );
        }

        this.contentDataXml = contentDataXml;
        this.contentTypeConfig = contentTypeConfig;
    }

    public void setBinaryDatas( List<BinaryDataKey> list )
    {
        this.binaryDatas = list;
    }

    public CustomContentData parse()
    {
        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        Element contentdataEl = contentDataXml.getRootElement();
        List<Element> children = contentdataEl.getChildren();
        for ( Element dataEntryEl : children )
        {
            parseElement( dataEntryEl, contentData, "contentdata" );
        }

        populateGroupDataEntryIndexes( contentData );

        return contentData;
    }

    private void populateGroupDataEntryIndexes( CustomContentData customContentData )
    {

        Map<String, Integer> groupIndexCount = new HashMap<String, Integer>();

        for ( DataEntry dataEntry : customContentData.getEntries() )
        {

            if ( dataEntry instanceof GroupDataEntry )
            {

                Integer count = groupIndexCount.get( dataEntry.getName() );

                if ( count == null )
                {
                    count = 1;
                }

                GroupDataEntry groupDataEntry = (GroupDataEntry) dataEntry;
                groupDataEntry.setGroupIndex( count );

                groupIndexCount.put( dataEntry.getName(), count + 1 );
            }
        }
    }

    @SuppressWarnings({"unchecked"})
    private void parseElement( Element element, DataEntrySet dataEntrySet, String parentXPath )
    {
        String elementName = element.getName();
        StringBuffer relativeXPath = new StringBuffer();
        if ( parentXPath != null )
        {
            relativeXPath.append( parentXPath ).append( "/" );
        }
        relativeXPath.append( elementName );
        String xpath = relativeXPath.toString();
        if ( dataEntrySet.getType() == DataEntryType.GROUP && xpath.startsWith( dataEntrySet.getXPath() ) )
        {
            xpath = xpath.substring( dataEntrySet.getXPath().length() + 1 );
        }

        DataEntryConfig inputConfig = dataEntrySet.getInputConfigByRelateiveXPath( xpath );
        if ( inputConfig == null )
        {
            CtySetConfig setConfig = dataEntrySet.getConfig().getSetConfigByRelativeXPath( xpath );
            if ( setConfig != null && dataEntrySet instanceof CustomContentData )
            {
                GroupDataEntry groupDataEntry = new GroupDataEntry( setConfig.getName(), xpath );
                dataEntrySet.add( groupDataEntry );
                List<Element> children = element.getChildren();
                for ( Element childElement : children )
                {
                    parseElement( childElement, groupDataEntry, xpath );
                }
            }
            else if ( setConfig != null && dataEntrySet instanceof GroupDataEntry )
            {
                throw new IllegalArgumentException( "Groups in groups is not allowed" );
            }
        }

        if ( inputConfig == null )
        {
            List<Element> children = element.getChildren();
            for ( Element childElement : children )
            {
                parseElement( childElement, dataEntrySet, xpath );
            }
            return;
        }

        DataEntryConfigType type = inputConfig.getType();

        if ( type == DataEntryConfigType.TEXT )
        {
            final String value = getTextFromElement( element );
            final TextDataEntry entry = new TextDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.KEYWORDS )
        {
            final KeywordsDataEntry entry = keywordsDataEntryParser.parse( element, inputConfig );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.TEXT_AREA )
        {
            final String value = getValueFromElement( element );
            final TextAreaDataEntry entry = new TextAreaDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.HTMLAREA )
        {
            final HtmlAreaDataEntry entry = htmlAreaDataEntryParser.parse( element, inputConfig );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.XML )
        {
            final XmlDataEntry entry = xmlDataEntryParser.parse( element, inputConfig );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.BINARY )
        {
            String keyStr = element.getAttributeValue( "key" );
            Element possibleSubBinarydataEl = element.getChild( "binarydata" );
            if ( possibleSubBinarydataEl != null )
            {
                keyStr = parseString( possibleSubBinarydataEl.getAttributeValue( "key" ), keyStr );
            }

            BinaryDataEntry entry;
            if ( keyStr != null )
            {
                if ( keyStr.startsWith( "%" ) )
                {
                    Integer binaryIndex = new Integer( keyStr.substring( 1 ) );
                    if ( binaryDatas != null )
                    {
                        final BinaryDataKey binaryDataKey2 = binaryDatas.get( binaryIndex );
                        if ( binaryDataKey2 != null )
                        {
                            entry = new BinaryDataEntry( inputConfig, binaryDataKey2.toInt() );
                        }
                        else
                        {
                            entry = new BinaryDataEntry( inputConfig, keyStr );
                        }
                    }
                    else
                    {
                        entry = new BinaryDataEntry( inputConfig, keyStr );
                    }
                }
                else
                {
                    final Integer binaryDataKey = new Integer( keyStr );
                    entry = new BinaryDataEntry( inputConfig, binaryDataKey );
                }
            }
            else
            {
                entry = new BinaryDataEntry( inputConfig );
            }
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.RELATEDCONTENT )
        {
            final RelatedContentDataEntryConfig relConfig = (RelatedContentDataEntryConfig) inputConfig;

            if ( !relConfig.isMultiple() )
            {
                Integer key = parseInteger( element.getAttributeValue( "key" ) );
                // Do a extra check if the key can be put in a inner content element
                final Element contentEl = element.getChild( "content" );
                if ( contentEl != null )
                {
                    final Integer keyInContentElement = parseInteger( contentEl.getAttributeValue( "key" ) );
                    if ( keyInContentElement != null )
                    {
                        // Let the key specified on the inner content element override
                        key = keyInContentElement;
                    }
                }

                ContentKey contentKey = null;
                if ( key != null )
                {
                    contentKey = new ContentKey( key );
                }
                final RelatedContentDataEntry entry = new RelatedContentDataEntry( inputConfig, contentKey );
                dataEntrySet.add( entry );
            }
            else
            {
                final RelatedContentsDataEntry entry = new RelatedContentsDataEntry( inputConfig );
                final List<Element> contentEls = element.getChildren( "content" );
                for ( final Element contentEl : contentEls )
                {
                    final Integer key = parseInteger( contentEl.getAttributeValue( "key" ) );
                    if ( key != null )
                    {
                        entry.add( new RelatedContentDataEntry( inputConfig, new ContentKey( key ) ) );
                    }
                }
                dataEntrySet.add( entry );
            }
        }
        else if ( type == DataEntryConfigType.FILE )
        {
            ContentKey contentKey = null;
            final Element fileEl = element.getChild( "file" );
            if ( fileEl != null )
            {
                final Integer key = parseInteger( fileEl.getAttributeValue( "key" ) );
                if ( key != null )
                {
                    contentKey = new ContentKey( key );
                }
            }
            final FileDataEntry entry = new FileDataEntry( inputConfig, contentKey );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.FILES )
        {
            final FilesDataEntry entry = new FilesDataEntry( inputConfig );
            final List<Element> filesEls = element.getChildren( "file" );
            for ( final Element fileEl : filesEls )
            {
                final Integer key = parseInteger( fileEl.getAttributeValue( "key" ) );
                if ( key != null )
                {
                    entry.add( new FileDataEntry( inputConfig, new ContentKey( key ) ) );
                }
            }
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.IMAGE )
        {
            final Integer key = parseInteger( element.getAttributeValue( "key" ) );
            ContentKey contentKey = null;
            if ( key != null )
            {
                contentKey = new ContentKey( key );
            }
            final ImageDataEntry entry = new ImageDataEntry( inputConfig, contentKey );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.IMAGES )
        {
            final ImagesDataEntry entry = new ImagesDataEntry( inputConfig );
            final List<Element> imageEls = element.getChildren( "image" );
            for ( final Element imageEl : imageEls )
            {
                final Integer key = parseInteger( imageEl.getAttributeValue( "key" ) );
                String text = parseText( imageEl.getChild( "text" ) );
                if ( "".equals( text ) )
                {
                    text = null;
                }
                entry.add( new ImageDataEntry( inputConfig, new ContentKey( key ), text ) );
            }
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.URL )
        {
            final String value = getTextFromElement( element );
            final UrlDataEntry entry = new UrlDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.DATE )
        {
            final Date value = parseDate( getTextFromElement( element ) );
            final DateDataEntry entry = new DateDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.CHECKBOX )
        {
            final Boolean value = parseBoolean( getTextFromElement( element ) );
            final BooleanDataEntry entry = new BooleanDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.RADIOBUTTON || type == DataEntryConfigType.DROPDOWN )
        {
            final String value = getTextFromElement( element );
            final SelectorDataEntry entry = new SelectorDataEntry( inputConfig, value );
            dataEntrySet.add( entry );
        }
        else if ( type == DataEntryConfigType.MULTIPLE_CHOICE )
        {
            final MultipleChoiceDataEntry entry;
            if ( hasValueIsFalse( element ) || ( element.getChildren().size() == 0 && element.getChild( "text" ) == null ) )
            {
                entry = new MultipleChoiceDataEntry( inputConfig, null, null );
            }
            else
            {
                final String value = getTextFromChildElement( element, "text" );
                List children = element.getChildren( "alternative" );
                List<MultipleChoiceAlternative> alternatives = new ArrayList<MultipleChoiceAlternative>();
                for ( Object child : children )
                {
                    alternatives.add( createMultipleChoiceAlternative( (Element) child ) );
                }
                entry = new MultipleChoiceDataEntry( inputConfig, value, alternatives );
            }

            dataEntrySet.add( entry );
        }
        else
        {
            LOG.warn( "Input type currently not supported: " + type );
        }
    }

    private MultipleChoiceAlternative createMultipleChoiceAlternative( Element childElem )
    {
        return new MultipleChoiceAlternative( childElem.getText(), Boolean.parseBoolean( childElem.getAttributeValue( "correct" ) ) );
    }

    private String getTextFromElement( final Element element )
    {
        if ( hasValueIsFalse( element ) )
        {
            return null;
        }
        return element.getText();
    }

    private String getTextFromChildElement( Element element, String childName )
    {
        if ( hasValueIsFalse( element ) )
        {
            return null;
        }
        Element child = element.getChild( childName );
        if ( child == null )
        {
            return null;
        }
        return child.getText();
    }

    private boolean hasValueIsFalse( Element element )
    {
        final Attribute hasValueAtr = element.getAttribute( "has-value" );
        if ( hasValueAtr != null && hasValueAtr.getValue().equalsIgnoreCase( "false" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private String getValueFromElement( final Element element )
    {
        final Attribute hasValueAtr = element.getAttribute( "has-value" );
        if ( hasValueAtr != null && hasValueAtr.getValue().equalsIgnoreCase( "false" ) )
        {
            return null;
        }
        return element.getValue();
    }

    private Boolean parseBoolean( String text )
    {
        if ( StringUtils.isBlank( text ) )
        {
            return null;
        }
        return text.equalsIgnoreCase( "true" );
    }

    private Date parseDate( String s )
    {
        if ( s == null )
        {
            return null;
        }
        try
        {
            return DateUtil.parseISODate( s );
        }
        catch ( ParseException e )
        {
            return null;
        }
    }

    private String parseText( Element element )
    {
        if ( element == null )
        {
            return null;
        }

        return element.getText();
    }

    private String parseString( String s, String defaultValue )
    {
        if ( s == null )
        {
            return defaultValue;
        }
        return s;
    }

    private Integer parseInteger( String s )
    {
        if ( s == null )
        {
            return null;
        }

        try
        {
            return new Integer( s );
        }
        catch ( NumberFormatException e )
        {
            return null;
        }
    }
}
