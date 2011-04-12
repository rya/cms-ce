/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.io.IOException;
import java.util.Date;

import com.enonic.cms.core.content.ContentKey;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentHandlerName;
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
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataXmlCreator;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataXmlParser;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;

import static org.junit.Assert.*;

public class CustomContentDataCombinedXmlCreatorAndParserTest
{
    private ContentTypeConfig customConfig;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );
        standardConfigXml.append( "         <title name=\"myTitle\"/>" );
        standardConfigXml.append( "         <block name=\"TestBlock1\">" );

        standardConfigXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myCheckbox\" type=\"checkbox\">" );
        standardConfigXml.append( "                 <display>My checkbox</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mycheckbox</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>My date</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDropdown\" type=\"dropdown\">" );
        standardConfigXml.append( "                 <display>My dropdown</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydropdown</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                    <option value=\"o1\">Option 1</option>" );
        standardConfigXml.append( "                    <option value=\"o2\">Option 2</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myFile\" type=\"file\">" );
        standardConfigXml.append( "                 <display>My file</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myFiles\" type=\"files\">" );
        standardConfigXml.append( "                 <display>My files</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfiles</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlarea\" type=\"htmlarea\">" );
        standardConfigXml.append( "                 <display>My htmlarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myImage\" type=\"image\">" );
        standardConfigXml.append( "                 <display>My image</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimage</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myImages\" type=\"images\">" );
        standardConfigXml.append( "                 <display>My images</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimages</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myKeywords\" type=\"keywords\">" );
        standardConfigXml.append( "                 <display>My keywords</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mykeywords</xpath>" );
        standardConfigXml.append( "             </input>" );

        //standardConfigXml.append( "             <input name=\"myMultiplechoice\" type=\"multiplechoice\">" );
        //standardConfigXml.append( "                 <display>My Question</display>" );
        //standardConfigXml.append( "                 <xpath>contentdata/mymultiplechoice</xpath>" );
        //standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRadiobutton\" type=\"radiobutton\">" );
        standardConfigXml.append( "                 <display>My radiobutton</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myradiobutton</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                    <option value=\"r1\">Choice 1</option>" );
        standardConfigXml.append( "                    <option value=\"r2\">Choice 2</option>" );
        standardConfigXml.append( "                    <option value=\"r3\">Choice 3</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"mySingleRelatedcontent\" type=\"relatedcontent\" multiple=\"false\">" );
        standardConfigXml.append( "                 <display>My single related content</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mysinglerelatedcontent</xpath>" );
        standardConfigXml.append( "                 <contenttype name=\"myContentType\"/>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myMultipleRelatedcontent\" type=\"relatedcontent\" multiple=\"true\">" );
        standardConfigXml.append( "                 <display>My multiple related content</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mymultiplerelatedcontent</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myText\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My text</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myTextarea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>My textarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myUploadfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My uploadfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myuploadfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myUrl\" type=\"url\">" );
        standardConfigXml.append( "                 <display>My URL</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myurl</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myXml\" type=\"xml\">" );
        standardConfigXml.append( "                 <display>My xml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myxml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        Element standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();
        //( standardConfigXml );
        customConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
    }

    @Test
    public void xmlCreatedFromObjectsEqualsCreatedFromXml_AllDataEntriesPresentWithNullValues()
        throws IOException, SAXException, JDOMException
    {
        Document contentDataAsXmlCreatedFromObjects = createContentDataXml( createContentdataWithAllValuesFilledOutWithNullValues() );
        String actualXml = documentToString( contentDataAsXmlCreatedFromObjects );

        String expectedXml = documentToString( createContentdataWithOnlyTitleFilledOutAsXml() );
        assertEquals( expectedXml, actualXml );
    }

    @Test
    public void xmlCreatedFromObjectsEqualsCreatedFromXml_AllDataEntriesPresentWithBlankValues()
        throws IOException, SAXException, JDOMException
    {
        Document contentDataAsXmlCreatedFromObjects = createContentDataXml( createContentdataWithAllValuesFilledOutWithBlankValues() );
        String actualXml = documentToString( contentDataAsXmlCreatedFromObjects );

        String expectedXml = documentToString( createContentdataWithOnlyTitleFilledOutAsXml_AllOtherBlank() );
        assertEquals( expectedXml, actualXml );
    }

    @Test
    public void xmlCreatedFromObjectsCreatesElementsForMissingDataEntries()
        throws IOException, SAXException, JDOMException
    {
        CustomContentData contentData = createContentdataWithOnlyTitleFilledOut();
        Document contentDataXmlCreatedFromContentDataObject = createContentDataXml( contentData );

        String expectedContentDataAsString = documentToString( createContentdataWithOnlyTitleFilledOutAsXml() );
        String actualContentDataXmlAsString = documentToString( contentDataXmlCreatedFromContentDataObject );
        assertEquals( expectedContentDataAsString, actualContentDataXmlAsString );
    }

    @Test
    public void parsedContentDataFromXmlEqualsCreatedFromObjects_AllDataEntriesPresent()
        throws IOException, SAXException, JDOMException
    {
        Document contentdataAsXml = createContentdataWithAllValuesFilledOutAsXml();
        CustomContentData contentDataParsedFromXml = CustomContentDataXmlParser.parse( contentdataAsXml, customConfig );

        CustomContentData expectedContentData = createContentdataWithAllValuesFilledOut();
        assertEquals( expectedContentData, contentDataParsedFromXml );
    }

    @Test
    public void parsedContentDataFromXmlEqualsCreatedFromObjects_AllDataEntriesPresentWithNullValues()
        throws IOException, SAXException, JDOMException
    {
        Document contentdataAsXml = createContentdataWithOnlyTitleFilledOutAsXml();
        CustomContentData contentDataParsedFromXml = CustomContentDataXmlParser.parse( contentdataAsXml, customConfig );

        CustomContentData expectedContentData = createContentdataWithAllValuesFilledOutWithNullValues();
        assertEquals( expectedContentData, contentDataParsedFromXml );
    }

    @Test
    public void parsedContentDataFromXmlEqualsCreatedFromObjects_AllDataEntriesPresentWithBlankValues()
        throws IOException, SAXException, JDOMException
    {
        Document contentdataAsXml = createContentdataWithOnlyTitleFilledOutAsXml_AllOtherBlank();
        CustomContentData contentDataParsedFromXml = CustomContentDataXmlParser.parse( contentdataAsXml, customConfig );

        CustomContentData expectedContentData = createContentdataWithAllValuesFilledOutWithBlankValues();
        assertEquals( expectedContentData, contentDataParsedFromXml );
    }

    @Test
    public void parsedContentDataFromXmlDoNotIncludeEmptyDataEntries()
        throws IOException, SAXException, JDOMException
    {
        Document expectedContentdataAsXml = createContentdataWithOnlyTitleFilledOutAsXml();
        CustomContentData contentdataParsedFromXml = CustomContentDataXmlParser.parse( expectedContentdataAsXml, customConfig );
        Document actualContentDataAsXml = createContentDataXml( contentdataParsedFromXml );

        assertEquals( documentToString( expectedContentdataAsXml ), documentToString( actualContentDataAsXml ) );
    }

    private CustomContentData createContentdataWithAllValuesFilledOut()
    {
        CustomContentData data = new CustomContentData( customConfig );
        data.add( new TextDataEntry( data.getInputConfig( "myTitle" ), "title" ) );

        data.add( new BooleanDataEntry( data.getInputConfig( "myCheckbox" ), getBooleanDataEntryValue() ) );
        data.add( new DateDataEntry( data.getInputConfig( "myDate" ), getDateDataEntryValue() ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myDropdown" ), getSelectorDataEntryValueForDropdown() ) );
        data.add( new FileDataEntry( data.getInputConfig( "myFile" ), getFileDataEntryValue() ) );
        data.add( new FilesDataEntry( data.getInputConfig( "myFiles" ) ).add(
            new FileDataEntry( data.getInputConfig( "myFiles" ), getFilesFileDataEntryValue1() ) ).add(
            new FileDataEntry( data.getInputConfig( "myFiles" ), getFilesFileDataEntryValue2() ) ) );
        data.add( new HtmlAreaDataEntry( data.getInputConfig( "myHtmlarea" ), getHtmlDataEntryValue() ) );
        data.add( new ImageDataEntry( data.getInputConfig( "myImage" ), getImageDataEntryValue() ) );
        data.add( new ImagesDataEntry( data.getInputConfig( "myImages" ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), getImagesImageDataEntryValue1() ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), getImagesImageDataEntryValue2(), getImagesDataEntryImage2Text() ) ) );
        data.add( new KeywordsDataEntry( data.getInputConfig( "myKeywords" ) ).addKeyword( getKeywordsDataEntryValue1() ).addKeyword(
            getKeywordsDataEntryValue2() ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myRadiobutton" ), getSelectorDataEntryValueForRadiobutton() ) );
        data.add( new RelatedContentDataEntry( data.getInputConfig( "mySingleRelatedcontent" ), getSingleRelatedContentDataEntryValue() ) );
        data.add( new RelatedContentsDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ),
                                         getMultipleRelatedContentDataEntryValue1() ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ),
                                         getMultipleRelatedContentDataEntryValue2() ) ) );
        data.add( new TextDataEntry( data.getInputConfig( "myText" ), getTextDataEntryValue() ) );
        data.add( new TextAreaDataEntry( data.getInputConfig( "myTextarea" ), getTextAreaDataEntryValue() ) );
        data.add( new BinaryDataEntry( data.getInputConfig( "myUploadfile" ), getBinaryDataEntryValue() ) );
        data.add( new UrlDataEntry( data.getInputConfig( "myUrl" ), getUrlDataEntryValue().toString() ) );
        data.add( new XmlDataEntry( data.getInputConfig( "myXml" ), getXmlDataEntryValue() ) );

        return data;
    }

    private CustomContentData createContentdataWithAllValuesFilledOutWithNullValues()
    {
        CustomContentData data = new CustomContentData( customConfig );
        data.add( new TextDataEntry( data.getInputConfig( "myTitle" ), "title" ) );
        data.add( new BooleanDataEntry( data.getInputConfig( "myCheckbox" ), null ) );
        data.add( new DateDataEntry( data.getInputConfig( "myDate" ), null ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myDropdown" ), null ) );
        data.add( new FileDataEntry( data.getInputConfig( "myFile" ), null ) );
        data.add(
            new FilesDataEntry( data.getInputConfig( "myFiles" ) ).add( new FileDataEntry( data.getInputConfig( "myFiles" ), null ) ).add(
                new FileDataEntry( data.getInputConfig( "myFiles" ), null ) ).add( null ) );
        data.add( new HtmlAreaDataEntry( data.getInputConfig( "myHtmlarea" ), null ) );
        data.add( new ImageDataEntry( data.getInputConfig( "myImage" ), null ) );
        data.add( new ImagesDataEntry( data.getInputConfig( "myImages" ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), null ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), null ) ).add( null ) );
        data.add( new KeywordsDataEntry( data.getInputConfig( "myKeywords" ) ).addKeyword( null ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myRadiobutton" ), null ) );
        data.add( new RelatedContentDataEntry( data.getInputConfig( "mySingleRelatedcontent" ), null ) );
        data.add( new RelatedContentsDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ), null ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ), null ) ).add( null ) );
        data.add( new TextDataEntry( data.getInputConfig( "myText" ), null ) );
        data.add( new TextAreaDataEntry( data.getInputConfig( "myTextarea" ), null ) );
        data.add( new BinaryDataEntry( data.getInputConfig( "myUploadfile" ), null ) );
        data.add( new UrlDataEntry( data.getInputConfig( "myUrl" ), null ) );
        data.add( new XmlDataEntry( data.getInputConfig( "myXml" ), null ) );
        return data;
    }

    private CustomContentData createContentdataWithAllValuesFilledOutWithBlankValues()
    {
        CustomContentData data = new CustomContentData( customConfig );
        data.add( new TextDataEntry( data.getInputConfig( "myTitle" ), "title" ) );
        data.add( new BooleanDataEntry( data.getInputConfig( "myCheckbox" ), null ) );
        data.add( new DateDataEntry( data.getInputConfig( "myDate" ), null ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myDropdown" ), "" ) );
        data.add( new FileDataEntry( data.getInputConfig( "myFile" ), null ) );
        data.add(
            new FilesDataEntry( data.getInputConfig( "myFiles" ) ).add( new FileDataEntry( data.getInputConfig( "myFiles" ), null ) ).add(
                new FileDataEntry( data.getInputConfig( "myFiles" ), null ) ).add( null ) );
        data.add( new HtmlAreaDataEntry( data.getInputConfig( "myHtmlarea" ), "" ) );
        data.add( new ImageDataEntry( data.getInputConfig( "myImage" ), null ) );
        data.add( new ImagesDataEntry( data.getInputConfig( "myImages" ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), null ) ).add(
            new ImageDataEntry( data.getInputConfig( "myImages" ), null ) ).add( null ) );
        data.add( new KeywordsDataEntry( data.getInputConfig( "myKeywords" ) ).addKeyword( "" ) );
        data.add( new SelectorDataEntry( data.getInputConfig( "myRadiobutton" ), "" ) );
        data.add( new RelatedContentDataEntry( data.getInputConfig( "mySingleRelatedcontent" ), null ) );
        data.add( new RelatedContentsDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ), null ) ).add(
            new RelatedContentDataEntry( data.getInputConfig( "myMultipleRelatedcontent" ), null ) ).add( null ) );
        data.add( new TextDataEntry( data.getInputConfig( "myText" ), "" ) );
        data.add( new TextAreaDataEntry( data.getInputConfig( "myTextarea" ), "" ) );
        data.add( new BinaryDataEntry( data.getInputConfig( "myUploadfile" ), null ) );
        data.add( new UrlDataEntry( data.getInputConfig( "myUrl" ), "" ) );
        data.add( new XmlDataEntry( data.getInputConfig( "myXml" ), "" ) );
        return data;
    }


    private Document createContentdataWithAllValuesFilledOutAsXml()
        throws IOException, SAXException, JDOMException
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "<contentdata>" );
        builder.append( "<mytitle>title</mytitle>" );
        builder.append( "<mycheckbox>" ).append( getBooleanDataEntryValue() ).append( "</mycheckbox>" );
        builder.append( "<mydate>2009-10-10</mydate>" );
        builder.append( "<mydropdown>" ).append( getSelectorDataEntryValueForDropdown() ).append( "</mydropdown>" );
        builder.append( "<myfile><file key=\"" ).append( getFileDataEntryValue() ).append( "\"/></myfile>" );
        builder.append( "<myfiles>" );
        builder.append( "<file key=\"" ).append( getFilesFileDataEntryValue1() ).append( "\"></file>" );
        builder.append( "<file key=\"" ).append( getFilesFileDataEntryValue2() ).append( "\">" ).append( "</file>" );
        builder.append( "</myfiles>" );
        builder.append( "<myhtmlarea>" ).append( getHtmlDataEntryValue() ).append( "</myhtmlarea>" );
        builder.append( "<myimage key=\"" ).append( getImageDataEntryValue() ).append( "\"/>" );
        builder.append( "<myimages>" );
        builder.append( "<image key=\"" ).append( getImagesImageDataEntryValue1() ).append( "\"><text/></image>" );
        builder.append( "<image key=\"" ).append( getImagesImageDataEntryValue2() ).append( "\"><text>" ).append(
            getImagesDataEntryImage2Text() ).append( "</text></image>" );
        builder.append( "</myimages>" );
        builder.append( "<mykeywords>" );
        builder.append( "<keyword>" ).append( getKeywordsDataEntryValue1() ).append( "</keyword>" );
        builder.append( "<keyword>" ).append( getKeywordsDataEntryValue2() ).append( "</keyword>" );
        builder.append( "</mykeywords>" );
        builder.append( "<myradiobutton>" ).append( getSelectorDataEntryValueForRadiobutton() ).append( "</myradiobutton>" );
        builder.append( "<mysinglerelatedcontent key=\"" ).append( getSingleRelatedContentDataEntryValue() ).append( "\"/>" );
        builder.append( "<mymultiplerelatedcontent>" );
        builder.append( "<content key=\"" ).append( getMultipleRelatedContentDataEntryValue1() ).append( "\"/>" );
        builder.append( "<content key=\"" ).append( getMultipleRelatedContentDataEntryValue2() ).append( "\"/>" );
        builder.append( "</mymultiplerelatedcontent>" );
        builder.append( "<mytext>" ).append( getTextDataEntryValue() ).append( "</mytext>" );
        builder.append( "<mytextarea>" ).append( getTextAreaDataEntryValue() ).append( "</mytextarea>" );
        builder.append( "<myuploadfile><binarydata key=\"" ).append( getBinaryDataEntryValue() ).append( "\"/></myuploadfile>" );
        builder.append( "<myurl>" ).append( getUrlDataEntryValue() ).append( "</myurl>" );
        builder.append( "<myxml>" ).append( getXmlDataEntryValue() ).append( "</myxml>" );

        builder.append( "</contentdata>" );

        return JDOMUtil.parseDocument( builder.toString() );
    }

    private CustomContentData createContentdataWithOnlyTitleFilledOut()
    {
        CustomContentData data = new CustomContentData( customConfig );
        data.add( new TextDataEntry( data.getInputConfig( "myTitle" ), "title" ) );
        return data;
    }

    private Document createContentdataWithOnlyTitleFilledOutAsXml()
        throws IOException, SAXException, JDOMException
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "<contentdata>" );
        builder.append( "<mytitle>title</mytitle>" );

        builder.append( "<mycheckbox has-value=\"false\"/>" );
        builder.append( "<mydate has-value=\"false\"/>" );
        builder.append( "<mydropdown has-value=\"false\"/>" );
        builder.append( "<myfile has-value=\"false\"/>" );
        builder.append( "<myfiles has-value=\"false\"/>" );
        builder.append( "<myhtmlarea has-value=\"false\"/>" );
        builder.append( "<myimage has-value=\"false\"/>" );
        builder.append( "<myimages has-value=\"false\"/>" );
        builder.append( "<mykeywords has-value=\"false\"/>" );
        builder.append( "<myradiobutton has-value=\"false\"/>" );
        builder.append( "<mysinglerelatedcontent has-value=\"false\"/>" );
        builder.append( "<mymultiplerelatedcontent has-value=\"false\"/>" );
        builder.append( "<mytext has-value=\"false\"/>" );
        builder.append( "<mytextarea has-value=\"false\"/>" );
        builder.append( "<myuploadfile has-value=\"false\"/>" );
        builder.append( "<myurl has-value=\"false\"/>" );
        builder.append( "<myxml has-value=\"false\"/>" );

        builder.append( "</contentdata>" );

        return JDOMUtil.parseDocument( builder.toString() );
    }

    private Document createContentdataWithOnlyTitleFilledOutAsXml_AllOtherBlank()
        throws IOException, SAXException, JDOMException
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "<contentdata>" );
        builder.append( "<mytitle>title</mytitle>" );

        builder.append( "<mycheckbox has-value=\"false\"/>" );
        builder.append( "<mydate has-value=\"false\"/>" );
        builder.append( "<mydropdown/>" );
        builder.append( "<myfile has-value=\"false\"/>" );
        builder.append( "<myfiles has-value=\"false\"/>" );
        builder.append( "<myhtmlarea/>" );
        builder.append( "<myimage has-value=\"false\"/>" );
        builder.append( "<myimages has-value=\"false\"/>" );
        builder.append( "<mykeywords has-value=\"false\"/>" );
        builder.append( "<myradiobutton/>" );
        builder.append( "<mysinglerelatedcontent has-value=\"false\"/>" );
        builder.append( "<mymultiplerelatedcontent has-value=\"false\"/>" );
        builder.append( "<mytext/>" );
        builder.append( "<mytextarea/>" );
        builder.append( "<myuploadfile has-value=\"false\"/>" );
        builder.append( "<myurl/>" );
        builder.append( "<myxml has-value=\"false\"/>" );

        builder.append( "</contentdata>" );

        return JDOMUtil.parseDocument( builder.toString() );
    }

    private boolean getBooleanDataEntryValue()
    {
        return true;
    }

    private String getSelectorDataEntryValueForDropdown()
    {
        return "o2";
    }

    private String getSelectorDataEntryValueForRadiobutton()
    {
        return "r3";
    }

    private Date getDateDataEntryValue()
    {
        return new DateTime( 2009, 10, 10, 0, 0, 0, 0 ).toDate();
    }

    private ContentKey getSingleRelatedContentDataEntryValue()
    {
        return new ContentKey( 555 );
    }

    private ContentKey getMultipleRelatedContentDataEntryValue1()
    {
        return new ContentKey( 666 );
    }

    private ContentKey getMultipleRelatedContentDataEntryValue2()
    {
        return new ContentKey( 777 );
    }

    private ContentKey getFileDataEntryValue()
    {
        return new ContentKey( 123 );
    }

    private ContentKey getFilesFileDataEntryValue1()
    {
        return new ContentKey( 2001 );
    }

    private ContentKey getFilesFileDataEntryValue2()
    {
        return new ContentKey( 2001 );
    }

    private ContentKey getImageDataEntryValue()
    {
        return new ContentKey( 125 );
    }

    private ContentKey getImagesImageDataEntryValue1()
    {
        return new ContentKey( 1001 );
    }

    private ContentKey getImagesImageDataEntryValue2()
    {
        return new ContentKey( 1002 );
    }

    private String getImagesDataEntryImage2Text()
    {
        return "Nobel price winner";
    }

    private String getTextDataEntryValue()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( SpecialCharacterTestStrings.NORWEGIAN );
        builder.append( SpecialCharacterTestStrings.CHINESE );
        builder.append( SpecialCharacterTestStrings.AEC_ALL );

        return builder.toString();
    }

    private String getKeywordsDataEntryValue1()
    {
        return "keyword1";
    }

    private String getKeywordsDataEntryValue2()
    {
        return "keyword2";
    }

    private String getTextAreaDataEntryValue()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( SpecialCharacterTestStrings.NORWEGIAN );
        builder.append( SpecialCharacterTestStrings.CHINESE );
        builder.append( SpecialCharacterTestStrings.AEC_ALL );

        return builder.toString();
    }

    private int getBinaryDataEntryValue()
    {
        return 101010101;
    }

    private String getHtmlDataEntryValue()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "  <no>" + SpecialCharacterTestStrings.NORWEGIAN + "</no>" );
        builder.append( "  <ch>" + SpecialCharacterTestStrings.CHINESE + "</ch>" );
        builder.append( "  <ecc>" + SpecialCharacterTestStrings.AEC_ALL + "</ecc>" );
        builder.append( "  <ecc-enc>" + SpecialCharacterTestStrings.AEC_ALL_ENCODED + "</ecc-enc>" );
        builder.append( "  <xmlres-enc>" + SpecialCharacterTestStrings.XML_RESERVED_ENCODED + "</xmlres-enc>" );
        return builder.toString();
    }

    private StringBuilder getUrlDataEntryValue()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "http://www.myurl.org" );
        return builder;
    }

    private String getXmlDataEntryValue()
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "  <root>" );
        builder.append( "    <no>" + SpecialCharacterTestStrings.NORWEGIAN + "</no>" );
        builder.append( "    <ch>" + SpecialCharacterTestStrings.CHINESE + "</ch>" );
        builder.append( "    <ecc>" + SpecialCharacterTestStrings.AEC_ALL + "</ecc>" );
        builder.append( "    <ecc-enc>" + SpecialCharacterTestStrings.AEC_ALL_ENCODED + "</ecc-enc>" );
        builder.append( "    <xmlres-enc>" + SpecialCharacterTestStrings.XML_RESERVED_ENCODED + "</xmlres-enc>" );
        builder.append( "  </root>" );
        return builder.toString();
    }

    private Document createContentDataXml( CustomContentData contentData )
    {
        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        return new Document( xmlCreator.createElement( contentData ) );
    }

    private String documentToString( Document expectedContentDataXml )
    {
        return JDOMUtil.prettyPrintDocument( expectedContentDataXml, "", true );
    }

    private class SpecialCharacterTestStrings
    {

        public static final String NORWEGIAN = "\u00c6\u00d8\u00c5\u00e6\u00f8\u00e5";     // AE, OE, AA, ae, oe, aa

        public static final String CHINESE = "\u306d\u304e\u30de\u30e8\u713c\u304d";

        public static final String AEC_ALL = "\u0082\u0083\u0084\u0085\u0086\u0087\u0089\u008a\u008b\u008c\u0091\u0092\u0093" +
            "\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009f";

        public static final String AEC_ALL_ENCODED = "&#130;&#131;&#132;&#133;&#134;&#135;&#137;&#138;&#139;&#140;&#145;&#146;&#147;" +
            "&#148;&#149;&#150;&#151;&#152;&#153;&#154;&#155;&#156;&#159;";

        public static final String XML_RESERVED = "<>&";

        public static final String XML_RESERVED_ENCODED = "&gt;&lt;&amp;";

    }
}
