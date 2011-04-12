/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.support;

import java.io.IOException;
import java.util.Collection;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DataEntry;
import com.enonic.cms.core.content.contentdata.custom.DataEntryType;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.support.CustomContentDataXmlParser;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.contenttype.CtyImportModeConfig;
import com.enonic.cms.core.content.contenttype.CtyImportPurgeConfig;
import com.enonic.cms.core.content.contenttype.CtyImportStatusConfig;

import static org.junit.Assert.*;


public class CustomContentDataParserTest
{

    private Element standardConfigEl;

    private Element importConfigEl;

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

        standardConfigXml.append( "             <input name=\"myTextarea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>My textarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myXml\" type=\"xml\">" );
        standardConfigXml.append( "                 <display>My xml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myxml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaDefault\" type=\"htmlarea\">" );
        standardConfigXml.append( "                 <display>My htmlarea default</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareadefault</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaXhtml\" type=\"htmlarea\" mode=\"xhtml\">" );
        standardConfigXml.append( "                 <display>My htmlarea xhtml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareaxhtml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaCdata\" type=\"htmlarea\" mode=\"cdata\">" );
        standardConfigXml.append( "                 <display>My htmlarea cdata</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareacdata</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myUrl\" type=\"url\">" );
        standardConfigXml.append( "                 <display>My url</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myurl</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>My date</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myCheckbox\" type=\"checkbox\">" );
        standardConfigXml.append( "                 <display>My checkbox</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mycheckbox</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRadiobutton\" type=\"radiobutton\">" );
        standardConfigXml.append( "                 <display>My radio button</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myradiobutton</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                     <option value=\"5\" checked=\"true\">Fem</option>" );
        standardConfigXml.append( "                     <option value=\"10\">Ti</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDropdown\" type=\"dropdown\">" );
        standardConfigXml.append( "                 <display>My dropdown</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydropdown</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                     <option value=\"1\" checked=\"true\">Toyota</option>" );
        standardConfigXml.append( "                     <option value=\"2\">Opel</option>" );
        standardConfigXml.append( "                     <option value=\"3\">Skoda</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRelatedSingle\" type=\"relatedcontent\" multiple=\"false\">" );
        standardConfigXml.append( "                 <display>My related single</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myrelatedsingle</xpath>" );
        standardConfigXml.append( "                 <contenttype name=\"MyContentType\"/>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRelatedMultiple\" type=\"relatedcontent\" multiple=\"true\">" );
        standardConfigXml.append( "                 <display>My related multipe</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myrelatedmultiple</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myFile\" type=\"file\">" );
        standardConfigXml.append( "                 <display>My file</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myImage\" type=\"image\">" );
        standardConfigXml.append( "                 <display>My image</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimage</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        StringBuffer importConfigXml = new StringBuffer();
        importConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        importConfigXml.append( "     <form>" );

        importConfigXml.append( "         <title name=\"myName\"/>" );

        importConfigXml.append( "         <block name=\"TestBlock1\">" );

        importConfigXml.append( "             <input name=\"myName\" required=\"true\" type=\"text\">" );
        importConfigXml.append( "                 <display>My name</display>" );
        importConfigXml.append( "                 <xpath>contentdata/myname</xpath>" );
        importConfigXml.append( "             </input>" );

        importConfigXml.append( "             <input name=\"myCount\" required=\"true\" type=\"text\">" );
        importConfigXml.append( "                 <display>My count</display>" );
        importConfigXml.append( "                 <xpath>contentdata/mycount</xpath>" );
        importConfigXml.append( "             </input>" );

        importConfigXml.append( "             <input name=\"myDescription\" required=\"true\" type=\"text\">" );
        importConfigXml.append( "                 <display>My description</display>" );
        importConfigXml.append( "                 <xpath>contentdata/mydescription</xpath>" );
        importConfigXml.append( "             </input>" );

        importConfigXml.append( "             <input name=\"myFile\" type=\"file\">" );
        importConfigXml.append( "                 <display>My file</display>" );
        importConfigXml.append( "                 <xpath>contentdata/myfile</xpath>" );
        importConfigXml.append( "             </input>" );

        importConfigXml.append( "             <input name=\"myImage\" type=\"image\">" );
        importConfigXml.append( "                 <display>My image</display>" );
        importConfigXml.append( "                 <xpath>contentdata/myimage</xpath>" );
        importConfigXml.append( "             </input>" );

        importConfigXml.append( "         </block>" );
        importConfigXml.append( "     </form>" );

        importConfigXml.append( "     <imports>" );

        importConfigXml.append( "         <import name=\"myXMLImport\" mode=\"xml\" base=\"/rows/row\" sync=\"myName\"" );
        importConfigXml.append( "                 purge=\"archive\" status=\"2\" >" );
        importConfigXml.append( "             <mapping dest=\"myName\" src=\"name\"/>" );
        importConfigXml.append( "             <mapping dest=\"myCount\" src=\"count\"/>" );
        importConfigXml.append( "             <mapping dest=\"myDescription\" src=\"descr\"/>" );
        importConfigXml.append( "         </import>" );

        importConfigXml.append( "         <import name=\"myCSVImport\" mode=\"csv\" separator=\";\" skip=\"0\" sync=\"myName\"" );
        importConfigXml.append( "                 purge=\"archive\" status=\"2\" >" );
        importConfigXml.append( "             <mapping dest=\"myName\" src=\"name\"/>" );
        importConfigXml.append( "             <mapping dest=\"myCount\" src=\"count\"/>" );
        importConfigXml.append( "             <mapping dest=\"myDescription\" src=\"descr\"/>" );
        importConfigXml.append( "         </import>" );

        importConfigXml.append( "     </imports>" );

        importConfigXml.append( "</config>" );
        importConfigEl = JDOMUtil.parseDocument( importConfigXml.toString() ).getRootElement();

    }

    @Test
    public void testParseXmlImport()
        throws IOException, JDOMException
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, importConfigEl );
        String name = "myXMLImport";
        CtyImportConfig importConfig = config.getImport( name );
        assertNotNull( importConfig );
        assertEquals( name, importConfig.getName() );
        assertTrue( CtyImportModeConfig.XML == importConfig.getMode() );
        assertEquals( "/rows/row", importConfig.getBase() );
        assertNull( importConfig.getSkip() );
        assertNull( importConfig.getSeparator() );
        assertEquals( "myName", importConfig.getSync() );
        assertTrue( CtyImportPurgeConfig.ARCHIVE == importConfig.getPurge() );
        assertTrue( CtyImportStatusConfig.APPROVED == importConfig.getStatus() );

        CtyImportMappingConfig mapping;
        assertTrue( 3 == importConfig.getMappings().size() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[0];
        assertNotNull( mapping );
        assertEquals( "myName", mapping.getDestination() );
        assertEquals( "name", mapping.getSource() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[1];
        assertNotNull( mapping );
        assertEquals( "myCount", mapping.getDestination() );
        assertEquals( "count", mapping.getSource() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[2];
        assertNotNull( mapping );
        assertEquals( "myDescription", mapping.getDestination() );
        assertEquals( "descr", mapping.getSource() );
    }


    @Test
    public void testParseCvsImport()
        throws IOException, JDOMException
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, importConfigEl );
        String name = "myCSVImport";
        CtyImportConfig importConfig = config.getImport( name );
        assertNotNull( importConfig );
        assertEquals( name, importConfig.getName() );
        assertTrue( CtyImportModeConfig.CSV == importConfig.getMode() );
        assertNull( importConfig.getBase() );
        assertTrue( 0 == importConfig.getSkip() );
        assertEquals( ";", importConfig.getSeparator() );
        assertEquals( "myName", importConfig.getSync() );
        assertTrue( CtyImportPurgeConfig.ARCHIVE == importConfig.getPurge() );
        assertTrue( CtyImportStatusConfig.APPROVED == importConfig.getStatus() );

        CtyImportMappingConfig mapping;
        assertTrue( 3 == importConfig.getMappings().size() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[0];
        assertNotNull( mapping );
        assertEquals( "myName", mapping.getDestination() );
        assertEquals( "name", mapping.getSource() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[1];
        assertNotNull( mapping );
        assertEquals( "myCount", mapping.getDestination() );
        assertEquals( "count", mapping.getSource() );
        mapping = (CtyImportMappingConfig) importConfig.getMappings().toArray()[2];
        assertNotNull( mapping );
        assertEquals( "myDescription", mapping.getDestination() );
        assertEquals( "descr", mapping.getSource() );
    }

    @Test
    public void testParseTextEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "    <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        TextDataEntry dataEntry = (TextDataEntry) contentData.getEntry( "myTitle" );
        assertNotNull( dataEntry );
        Assert.assertEquals( DataEntryType.TEXT, dataEntry.getType() );
        assertEquals( "Title 123", dataEntry.getValue() );
    }

    @Test
    public void testParseTitleEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "    <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        TextDataEntry dataEntry = (TextDataEntry) contentData.getTitleDataEntry();
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.TEXT, dataEntry.getType() );
        assertEquals( "Title 123", dataEntry.getValue() );
    }

    @Test
    public void testParseSingleRelatedContentEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myrelatedsingle key=\"113\"/>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        RelatedContentDataEntry dataEntry = (RelatedContentDataEntry) contentData.getEntry( "myRelatedSingle" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.RELATED_CONTENT, dataEntry.getType() );
        assertEquals( 113, dataEntry.getContentKey().toInt() );
    }

    @Test
    public void testParseSingleRelatedContentEntry2()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myrelatedsingle>" );
        contentDataXml.append( "        <content key=\"113\"/>" );
        contentDataXml.append( "     </myrelatedsingle>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        RelatedContentDataEntry dataEntry = (RelatedContentDataEntry) contentData.getEntry( "myRelatedSingle" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.RELATED_CONTENT, dataEntry.getType() );
        assertEquals( 113, dataEntry.getContentKey().toInt() );
    }

    @Test
    public void testParseMultipleRelatedContentEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myrelatedmultiple>" );
        contentDataXml.append( "        <content key=\"101\"/>" );
        contentDataXml.append( "        <content key=\"102\"/>" );
        contentDataXml.append( "        <content key=\"103\"/>" );
        contentDataXml.append( "     </myrelatedmultiple>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        RelatedContentsDataEntry dataEntry = (RelatedContentsDataEntry) contentData.getEntry( "myRelatedMultiple" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.RELATED_CONTENTS, dataEntry.getType() );

        Collection<ContentKey> relatedContentKeysCollection = dataEntry.getRelatedContentKeys();
        ContentKey[] relatedContentKeys = relatedContentKeysCollection.toArray( new ContentKey[relatedContentKeysCollection.size()] );

        assertArrayEquals( new ContentKey[]{new ContentKey( 101 ), new ContentKey( 102 ), new ContentKey( 103 )}, relatedContentKeys );
    }

    @Test
    public void testParseTextAreaEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <mytextarea>Text area 123</mytextarea>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        TextAreaDataEntry dataEntry = (TextAreaDataEntry) contentData.getEntry( "myTextarea" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.TEXT_AREA, dataEntry.getType() );
        assertEquals( "Text area 123", dataEntry.getValue() );
    }

    @Test
    public void testParseDefaultHtmlAreaEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myhtmlareadefault>Htmlarea 123</myhtmlareadefault>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        HtmlAreaDataEntry dataEntry = (HtmlAreaDataEntry) contentData.getEntry( "myHtmlareaDefault" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.HTML_AREA, dataEntry.getType() );
        assertEquals( "Htmlarea 123", dataEntry.getValue() );
    }

    public void testParseXhtmlHtmlAreaEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myhtmlareaxhtml>Htmlarea <br/>123</myhtmlareaxhtml>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        HtmlAreaDataEntry dataEntry = (HtmlAreaDataEntry) contentData.getEntry( "myHtmlareaXhtml" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.HTML_AREA, dataEntry.getType() );
        assertEquals( "Htmlarea <br/>123", dataEntry.getValue() );
    }

    public void testParseCdataHtmlAreaEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myhtmlareacdata>Htmlarea <br>123</myhtmlareacdata>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        HtmlAreaDataEntry dataEntry = (HtmlAreaDataEntry) contentData.getEntry( "myHtmlareaCdata" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.HTML_AREA, dataEntry.getType() );
        assertEquals( "Htmlarea <br>123", dataEntry.getValue() );
    }

    @Test
    public void testParseDateEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <mydate>2008-02-01</mydate>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        DateDataEntry dataEntry = (DateDataEntry) contentData.getEntry( "myDate" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.DATE, dataEntry.getType() );
        assertEquals( new DateTime( "2008-02-01" ).toDate().getTime(), dataEntry.getValue().getTime() );
    }

    @Test
    public void testParseUrlEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myurl>http://www.drummingafrica.co.za</myurl>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        UrlDataEntry dataEntry = (UrlDataEntry) contentData.getEntry( "myUrl" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.URL, dataEntry.getType() );
        assertEquals( "http://www.drummingafrica.co.za", dataEntry.getValue() );
    }

    @Test
    public void testParseRadiobuttonEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myradiobutton>5</myradiobutton>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        SelectorDataEntry dataEntry = (SelectorDataEntry) contentData.getEntry( "myRadiobutton" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.SELECTOR, dataEntry.getType() );
        assertEquals( "5", dataEntry.getValue() );
    }

    @Test
    public void testParseDropdownEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <mydropdown>2</mydropdown>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        SelectorDataEntry dataEntry = (SelectorDataEntry) contentData.getEntry( "myDropdown" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.SELECTOR, dataEntry.getType() );
        assertEquals( "2", dataEntry.getValue() );
    }

    @Test
    public void testParseFileEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myfile><file key=\"229\"/></myfile>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        FileDataEntry dataEntry = (FileDataEntry) contentData.getEntry( "myFile" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.FILE, dataEntry.getType() );
        assertEquals( 229, dataEntry.getContentKey().toInt() );
    }

    @Test
    public void testParseImageEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <myimage key=\"254\"></myimage>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        ImageDataEntry dataEntry = (ImageDataEntry) contentData.getEntry( "myImage" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.IMAGE, dataEntry.getType() );
        assertEquals( 254, dataEntry.getContentKey().toInt() );
    }


    @Test
    public void testParseSubBlocksNotSupported()
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

        standardConfigXml.append( "             <block name=\"innerBlock\">" );

        standardConfigXml.append( "             <input name=\"myTextarea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>My textarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             </block>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <mytextarea>My content</mytextarea>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        TextAreaDataEntry dataEntry = (TextAreaDataEntry) contentData.getEntry( "myTextarea" );
        assertNull( dataEntry );
    }


    @Test
    public void testParseMultipleBlocksSupported()
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
        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "         <block name=\"innerBlock\">" );
        standardConfigXml.append( "             <input name=\"myTextarea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>My textarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Title 123</mytitle>" );
        contentDataXml.append( "     <mytextarea>My content</mytextarea>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        TextAreaDataEntry dataEntry = (TextAreaDataEntry) contentData.getEntry( "myTextarea" );
        assertNotNull( dataEntry );
        assertEquals( DataEntryType.TEXT_AREA, dataEntry.getType() );
        assertEquals( "My content", dataEntry.getValue() );
    }


    @Test
    public void testParseGroupSupported()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myTitle\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My main title</display>" );
        configXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "         <block name=\"innerBlock\" group=\"contentdata/inner\">" );
        configXml.append( "             <input name=\"myInnerTitle\" type=\"text\">" );
        configXml.append( "                 <display>My inner text</display>" );
        configXml.append( "                 <xpath>mytitle</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myInnerTextarea\" type=\"textarea\">" );
        configXml.append( "                 <display>My inner textarea</display>" );
        configXml.append( "                 <xpath>mytextarea</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myInnerFile\" type=\"file\">" );
        configXml.append( "                 <display>My inner file</display>" );
        configXml.append( "                 <xpath>eveninner/myfile</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "     <mytitle>Main title</mytitle>" );
        contentDataXml.append( "     <inner>" );
        contentDataXml.append( "        <mytitle>Inner title</mytitle>" );
        contentDataXml.append( "        <mytextarea>Text in inner area</mytextarea>" );
        contentDataXml.append( "        <eveninner><myfile><file key=\"123\"/></myfile></eveninner>" );
        contentDataXml.append( "     </inner>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );

        CustomContentData contentData = CustomContentDataXmlParser.parse( contentDataDoc, config );
        assertTextDataEntry( "Main title", "myTitle", contentData );
        assertTextDataEntry( "Inner title", "myInnerTitle", contentData );
        assertTextAreaDataEntry( "Text in inner area", "myInnerTextarea", contentData );
        assertFileDataEntry( new ContentKey( 123 ), "myInnerFile", contentData );
    }

    private void assertTextDataEntry( String expectedValue, String entryName, CustomContentData contentData )
    {
        DataEntry entry = contentData.getEntry( entryName );
        assertNotNull( entryName + " not found", entry );
        assertEquals( entryName + " has unexpected type", DataEntryType.TEXT, entry.getType() );
        assertEquals( entryName + " has unexpected value", expectedValue, ( (TextDataEntry) entry ).getValue() );
    }

    private void assertTextAreaDataEntry( String expectedValue, String entryName, CustomContentData contentData )
    {
        DataEntry entry = contentData.getEntry( entryName );
        assertNotNull( entry );
        assertEquals( DataEntryType.TEXT_AREA, entry.getType() );
        assertEquals( expectedValue, ( (TextAreaDataEntry) entry ).getValue() );
    }

    private void assertFileDataEntry( ContentKey contentKey, String entryName, CustomContentData contentData )
    {
        DataEntry entry = contentData.getEntry( entryName );
        assertNotNull( entryName, entry );
        assertEquals( entryName, DataEntryType.FILE, entry.getType() );
        assertEquals( entryName, contentKey, ( (FileDataEntry) entry ).getContentKey() );
    }

}
