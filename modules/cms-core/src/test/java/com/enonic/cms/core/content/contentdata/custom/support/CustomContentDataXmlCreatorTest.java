/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.io.IOException;

import com.enonic.cms.core.content.ContentKey;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.FileDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyFormConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DateDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.FileDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;


public class CustomContentDataXmlCreatorTest
{
    private ContentTypeConfig customConfigTest = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Test" );

    private ContentTypeConfig customConfigEvent = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Event" );

    private ContentTypeConfig customConfigArticle = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Article" );

    @Test
    public void testCreateContentWithTwoTextElements()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new TextDataEntryConfig( "test1", true, "Test 1", "contentdata/test1" ) );
        ctyBlock.addInput( new TextDataEntryConfig( "test2", true, "Test 2", "contentdata/test2" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        contentData.add( new TextDataEntry( ctyBlock.getInputConfig( "test1" ), "a" ) );
        contentData.add( new TextDataEntry( ctyBlock.getInputConfig( "test2" ), "b" ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.printDocument( new Document( contentEl ) );

        String expectedXml = "<contentdata><test1>a</test1><test2>b</test2></contentdata>";

        assertXMLEqual( expectedXml, actualXml );
    }


    @Test
    public void testCreateContentWithDateElement()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new DateDataEntryConfig( "start", true, "Test", "contentdata/start" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        contentData.add( new DateDataEntry( ctyBlock.getInputConfig( "start" ), new DateTime( 2008, 6, 1, 12, 0, 0, 0 ).toDate() ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.printDocument( new Document( contentEl ) );

        String expectedXml = "<contentdata><start>2008-06-01</start></contentdata>";

        assertXMLEqual( expectedXml, actualXml );
    }

    @Test
    public void testCreateContentWithTextAreaElement()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new TextAreaDataEntryConfig( "preface", true, "Test", "contentdata/test/preface" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );

        contentData.add( new TextAreaDataEntry( contentData.getInputConfig( "preface" ), "This is the preface." ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.printDocument( new Document( contentEl ) );

        String expectedXml = "<contentdata><test><preface>This is the preface.</preface></test></contentdata>";

        assertXMLEqual( expectedXml, actualXml );
    }

    @Test
    public void testCreateContentWithXmlElement()
        throws IOException, SAXException, JDOMException
    {
        String xml = "<this is=\"true\"><some>xml</some></this>";
        doTestCreateContentWithXmlElement( xml, xml );
    }

    @Test
    public void testCreateContentWithXmlElement_withProlog()
        throws IOException, SAXException, JDOMException
    {
        String xml = "<this is=\"true\"><some>xml</some></this>";
        doTestCreateContentWithXmlElement( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml, xml );
    }

    private void doTestCreateContentWithXmlElement( String inXml, String outXml )
        throws IOException, SAXException, JDOMException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new XmlDataEntryConfig( "somexml", true, "Test", "contentdata/test/somexml" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        contentData.add( new XmlDataEntry( ctyBlock.getInputConfig( "somexml" ), inXml ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.prettyPrintDocument( new Document( contentEl ) );

        String expectedXml = JDOMUtil.prettyPrintDocument(
            JDOMUtil.parseDocument( "<contentdata><test><somexml>" + outXml + "</somexml></test></contentdata>" ) );

        assertXMLEqual( expectedXml, actualXml );
    }

    @Test
    public void testCreateContentWithHtmlElement()
        throws IOException, SAXException, JDOMException
    {
        String html = "<this is=\"true\"><some>html</some></this>";
        doTestCreateContentWithHtmlElement( html, html );
    }

    @Test
    public void testCreateContentWithHtmlElement_withProlog()
        throws IOException, SAXException, JDOMException
    {
        String html = "<this is=\"true\"><some>html</some></this>";
        doTestCreateContentWithHtmlElement( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + html, html );
    }

    private void doTestCreateContentWithHtmlElement( String inHtml, String outHtml )
        throws IOException, SAXException, JDOMException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new HtmlAreaDataEntryConfig( "somehtml", true, "Test", "contentdata/test/somehtml" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        contentData.add( new HtmlAreaDataEntry( ctyBlock.getInputConfig( "somehtml" ), inHtml ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualHtml = JDOMUtil.prettyPrintDocument( new Document( contentEl ) );

        String expectedHtml = JDOMUtil.prettyPrintDocument(
            JDOMUtil.parseDocument( "<contentdata><test><somehtml>" + outHtml + "</somehtml></test></contentdata>" ) );

        assertXMLEqual( expectedHtml, actualHtml );
    }

    @Test
    public void testCreateContentWithUrlElement()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new UrlDataEntryConfig( "someurl", true, "Test", "contentdata/test/someurl", 100 ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        contentData.add( new UrlDataEntry( ctyBlock.getInputConfig( "someurl" ), "http://www.drummingafrica.co.za" ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.printDocument( new Document( contentEl ) );

        String expectedXml = "<contentdata><test><someurl>http://www.drummingafrica.co.za</someurl></test></contentdata>";

        assertXMLEqual( expectedXml, actualXml );
    }

    @Test
    public void testCreateElementOneBlock()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigEvent );
        customConfigEvent.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Event", null ) );
        ctyBlock.addInput( new TextDataEntryConfig( "name", true, "Name", "contentdata/event/name" ) );

        CustomContentData contentData = new CustomContentData( customConfigEvent );
        contentData.add( new TextDataEntry( ctyBlock.getInputConfig( "name" ), "Bursdag" ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        String actualXml = JDOMUtil.printDocument( new Document( contentEl ) );

        String expectedXml = "<contentdata><event><name>Bursdag</name></event></contentdata>";

        assertXMLEqual( expectedXml, actualXml );
    }

    @Test
    public void testCreateElementTwoBlocks()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigArticle );
        customConfigArticle.setForm( ctyForm );
        CtySetConfig ctyBlockArticle = ctyForm.addBlock( new CtySetConfig( ctyForm, "Article", null ) );
        ctyBlockArticle.addInput( new TextDataEntryConfig( "article_title", true, "Article heading", "contentdata/article/title" ) );
        ctyBlockArticle.addInput(
            new TextAreaDataEntryConfig( "article_preface", false, "Article preface", "contentdata/article/preface" ) );
        ctyBlockArticle.addInput( new HtmlAreaDataEntryConfig( "article_text", false, "Article text", "contentdata/article/text" ) );

        CtySetConfig ctyBlockTeaser = ctyForm.addBlock( new CtySetConfig( ctyForm, "Teaser", null ) );
        ctyBlockTeaser.addInput( new TextDataEntryConfig( "teaser_title", true, "Teaser title", "contentdata/teaser/title" ) );
        ctyBlockTeaser.addInput( new TextAreaDataEntryConfig( "teaser_preface", false, "Teaser preface", "contentdata/teaser/preface" ) );

        CustomContentData contentData = new CustomContentData( customConfigArticle );
        contentData.add(
            new TextDataEntry( ctyBlockArticle.getInputConfig( "article_title" ), "Article title: Laverne har kommet til Norge" ) );
        contentData.add(
            new TextAreaDataEntry( ctyBlockArticle.getInputConfig( "article_preface" ), "Article preface: Endelig har hun kommet." ) );
        contentData.add(
            new TextDataEntry( ctyBlockTeaser.getInputConfig( "teaser_title" ), "Teaser title: Laverne har ankommet Norge!" ) );
        contentData.add(
            new TextAreaDataEntry( ctyBlockTeaser.getInputConfig( "teaser_preface" ), "Teaser preface: Hipp hurra! Alle gleder seg!" ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        Document contentDoc = new Document( contentEl );
        String actualXml = JDOMUtil.printDocument( contentDoc );

        StringBuffer expXml = new StringBuffer();
        expXml.append( "<contentdata>" );
        expXml.append( "<article>" );
        expXml.append( "<title>" );
        expXml.append( "Article title: Laverne har kommet til Norge" );
        expXml.append( "</title>" );
        expXml.append( "<preface>" );
        expXml.append( "Article preface: Endelig har hun kommet." );
        expXml.append( "</preface>" );
        expXml.append( "<text has-value=\"false\"/>" );
        expXml.append( "</article>" );
        expXml.append( "<teaser>" );
        expXml.append( "<title>" );
        expXml.append( "Teaser title: Laverne har ankommet Norge!" );
        expXml.append( "</title>" );
        expXml.append( "<preface>" );
        expXml.append( "Teaser preface: Hipp hurra! Alle gleder seg!" );
        expXml.append( "</preface>" );
        expXml.append( "</teaser>" );
        expXml.append( "</contentdata>" );
        assertXMLEqual( expXml.toString(), actualXml );
    }

    @Test
    public void testGroupBlockWhereGroupXpathIsOneElement()
        throws IOException, SAXException
    {
        ContentTypeConfig customConfigArticle = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Article" );
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigArticle );
        customConfigArticle.setForm( ctyForm );
        CtySetConfig ctyBlockMain = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyContentType", null ) );
        ctyBlockMain.addInput( new TextDataEntryConfig( "title", true, "Title", "contentdata/title" ) );
        CtySetConfig myBlockConfig = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyBlock", "contentdata/myblock" ) );
        myBlockConfig.addInput( new TextDataEntryConfig( "titleinblock", true, "Title in block", "titleinblock" ) );
        myBlockConfig.addInput( new TextDataEntryConfig( "textinblock", false, "Text in block", "textinblock" ) );

        CustomContentData contentData = new CustomContentData( customConfigArticle );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), "Laverne har kommet til Norge" ) );

        for ( int i = 1; i <= 2; i++ )
        {
            GroupDataEntry grpRelated = new GroupDataEntry( "MyBlock", "contentdata/myblock" );
            contentData.add( grpRelated );
            grpRelated.add( new TextDataEntry( myBlockConfig.getInputConfig( "titleinblock" ), "Group entry " + i ) );
        }

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        Document contentDoc = new Document( contentEl );
        String actualXml = JDOMUtil.printDocument( contentDoc );

        StringBuffer expXml = new StringBuffer();
        expXml.append( "<contentdata>" );
        expXml.append( "<title>" );
        expXml.append( "Laverne har kommet til Norge" );
        expXml.append( "</title>" );
        expXml.append( "<myblock>" );
        expXml.append( "<titleinblock>" );
        expXml.append( "Group entry 1" );
        expXml.append( "</titleinblock>" );
        expXml.append( "<textinblock has-value=\"false\"/>" );
        expXml.append( "</myblock>" );
        expXml.append( "<myblock>" );
        expXml.append( "<titleinblock>" );
        expXml.append( "Group entry 2" );
        expXml.append( "</titleinblock>" );
        expXml.append( "<textinblock has-value=\"false\"/>" );
        expXml.append( "</myblock>" );
        expXml.append( "</contentdata>" );
        assertXMLEqual( expXml.toString(), actualXml );
    }

    @Test
    public void testGroupBlockWhereGroupXpathIsTwoElements()
        throws IOException, SAXException
    {
        ContentTypeConfig customConfigArticle = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Article" );
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigArticle );
        customConfigArticle.setForm( ctyForm );
        CtySetConfig ctyBlockMain = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyContentType", null ) );
        ctyBlockMain.addInput( new TextDataEntryConfig( "title", true, "Title", "contentdata/title" ) );
        CtySetConfig myBlockConfig = ctyForm.addBlock( new CtySetConfig( ctyForm, "MyBlock", "contentdata/myblocks/myblock" ) );
        myBlockConfig.addInput( new TextDataEntryConfig( "titleinblock", true, "Title in block", "titleinblock" ) );
        myBlockConfig.addInput( new TextDataEntryConfig( "textinblock", false, "Text in block", "textinblock" ) );

        CustomContentData contentData = new CustomContentData( customConfigArticle );
        contentData.add( new TextDataEntry( contentData.getInputConfig( "title" ), "Laverne har kommet til Norge" ) );

        for ( int i = 1; i <= 2; i++ )
        {
            GroupDataEntry grpRelated = new GroupDataEntry( "MyBlock", "contentdata/myblock" );
            contentData.add( grpRelated );
            grpRelated.add( new TextDataEntry( myBlockConfig.getInputConfig( "titleinblock" ), "Group entry " + i ) );
        }

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        Document contentDoc = new Document( contentEl );
        String actualXml = JDOMUtil.printDocument( contentDoc );

        StringBuffer expXml = new StringBuffer();
        expXml.append( "<contentdata>" );
        expXml.append( "<title>" );
        expXml.append( "Laverne har kommet til Norge" );
        expXml.append( "</title>" );
        expXml.append( "<myblocks>" );
        expXml.append( "<myblock>" );
        expXml.append( "<titleinblock>" );
        expXml.append( "Group entry 1" );
        expXml.append( "</titleinblock>" );
        expXml.append( "<textinblock has-value=\"false\"/>" );
        expXml.append( "</myblock>" );
        expXml.append( "<myblock>" );
        expXml.append( "<titleinblock>" );
        expXml.append( "Group entry 2" );
        expXml.append( "</titleinblock>" );
        expXml.append( "<textinblock has-value=\"false\"/>" );
        expXml.append( "</myblock>" );
        expXml.append( "</myblocks>" );
        expXml.append( "</contentdata>" );
        assertXMLEqual( expXml.toString(), actualXml );
    }

    @Test
    public void testCreateElementGroupBlock()
        throws IOException, SAXException
    {
        ContentTypeConfig customConfigArticle = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Article" );
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigArticle );
        customConfigArticle.setForm( ctyForm );
        CtySetConfig ctyBlockArticle = ctyForm.addBlock( new CtySetConfig( ctyForm, "Article", null ) );
        ctyBlockArticle.addInput( new TextDataEntryConfig( "article_title", true, "Article heading", "contentdata/article/title" ) );
        ctyBlockArticle.addInput(
            new TextAreaDataEntryConfig( "article_preface", false, "Article preface", "contentdata/article/preface" ) );
        ctyBlockArticle.addInput( new HtmlAreaDataEntryConfig( "article_text", false, "Article text", "contentdata/article/text" ) );
        CtySetConfig configBlockRelated = ctyForm.addBlock( new CtySetConfig( ctyForm, "related", "contentdata/related" ) );
        configBlockRelated.addInput( new TextDataEntryConfig( "related_title", true, "Related title", "title" ) );
        configBlockRelated.addInput( new TextDataEntryConfig( "related_text", false, "Related title", "text" ) );

        CustomContentData contentData = new CustomContentData( customConfigArticle );

        contentData.add( new TextDataEntry( ctyBlockArticle.getInputConfig( "article_title" ), "Laverne har kommet til Norge" ) );
        contentData.add( new TextAreaDataEntry( ctyBlockArticle.getInputConfig( "article_preface" ), "Endelig har hun kommet." ) );

        for ( int i = 1; i <= 2; i++ )
        {
            GroupDataEntry grpRelated = new GroupDataEntry( "related", "contentdata/related" );
            contentData.add( grpRelated );
            grpRelated.add( new TextDataEntry( configBlockRelated.getInputConfig( "related_title" ), "Relatert " + i ) );
            grpRelated.add( new TextDataEntry( configBlockRelated.getInputConfig( "related_text" ), "Tekst " + i ) );
        }

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        Document contentDoc = new Document( contentEl );
        String actualXml = JDOMUtil.printDocument( contentDoc );

        StringBuffer expXml = new StringBuffer();
        expXml.append( "<contentdata>" );
        expXml.append( "<article>" );
        expXml.append( "<title>" );
        expXml.append( "Laverne har kommet til Norge" );
        expXml.append( "</title>" );
        expXml.append( "<preface>" );
        expXml.append( "Endelig har hun kommet." );
        expXml.append( "</preface>" );
        expXml.append( "<text has-value=\"false\"/>" );
        expXml.append( "</article>" );
        expXml.append( "<related>" );
        expXml.append( "<title>" );
        expXml.append( "Relatert 1" );
        expXml.append( "</title>" );
        expXml.append( "<text>" );
        expXml.append( "Tekst 1" );
        expXml.append( "</text>" );
        expXml.append( "</related>" );
        expXml.append( "<related>" );
        expXml.append( "<title>" );
        expXml.append( "Relatert 2" );
        expXml.append( "</title>" );
        expXml.append( "<text>" );
        expXml.append( "Tekst 2" );
        expXml.append( "</text>" );
        expXml.append( "</related>" );
        expXml.append( "</contentdata>" );
        assertXMLEqual( expXml.toString(), actualXml );
    }

    @Test
    public void testCreateElementGroupBlockWithTwoBinaryElements()
        throws IOException, SAXException
    {
        CtyFormConfig ctyForm = new CtyFormConfig( customConfigTest );
        customConfigTest.setForm( ctyForm );
        CtySetConfig ctyBlock = ctyForm.addBlock( new CtySetConfig( ctyForm, "Test", null ) );
        ctyBlock.addInput( new TextDataEntryConfig( "name", true, "jamTest", "contentdata/name" ) );

        CtySetConfig configBlockRelated = ctyForm.addBlock( new CtySetConfig( ctyForm, "files", "contentdata/files/file" ) );
        configBlockRelated.addInput( new FileDataEntryConfig( "binary", true, "File", "binary" ) );
        configBlockRelated.addInput( new TextDataEntryConfig( "binarydescription", true, "Description", "description" ) );

        CustomContentData contentData = new CustomContentData( customConfigTest );
        TextDataEntryConfig nameConfig = new TextDataEntryConfig( "name", true, "article_title", "contentdata/name" );
        contentData.add( new TextDataEntry( nameConfig, "jamTest" ) );

        GroupDataEntry grpRelated1 = new GroupDataEntry( "files", "contentdata/files/file" );
        contentData.add( grpRelated1 );

        FileDataEntryConfig fileConfig = new FileDataEntryConfig( "binary", false, "File", "binary" );
        TextDataEntryConfig bindaryDescConfig = new TextDataEntryConfig( "binarydescription", true, "binarydescription", "description" );

        grpRelated1.add( new FileDataEntry( fileConfig, new ContentKey( 888 ) ) );
        grpRelated1.add( new TextDataEntry( bindaryDescConfig, "A" ) );

        GroupDataEntry grpRelated2 = new GroupDataEntry( "files", "contentdata/files/file" );
        contentData.add( grpRelated2 );
        grpRelated2.add( new FileDataEntry( fileConfig, new ContentKey( 999 ) ) );
        grpRelated2.add( new TextDataEntry( bindaryDescConfig, "B" ) );

        CustomContentDataXmlCreator xmlCreator = new CustomContentDataXmlCreator();
        Element contentEl = xmlCreator.createElement( contentData );
        Document contentDataDoc = new Document( contentEl );

        assertXPathEquals( "/contentdata/files/file[1]/binary/file/@key", contentDataDoc, "888" );
        assertXPathEquals( "/contentdata/files/file[1]/description", contentDataDoc, "A" );

        assertXPathEquals( "/contentdata/files/file[2]/binary/file/@key", contentDataDoc, "999" );
        assertXPathEquals( "/contentdata/files/file[2]/description", contentDataDoc, "B" );
    }

    protected void assertXPathEquals( String xpathString, Document doc, String expectedValue )
    {
        String actualValue = JDOMUtil.evaluateSingleXPathValueAsString( xpathString, doc );
        Assert.assertEquals( xpathString, expectedValue, actualValue );

    }
}
