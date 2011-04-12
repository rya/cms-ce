/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

public class ContentVersionEntityTest
{

    private Element standardConfigEl;

    private XMLBytes standardConfigAsXMLBytes;

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

        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        final XMLDocument xmlDoc = XMLDocumentFactory.create( standardConfigXml.toString() );
        standardConfigAsXMLBytes = xmlDoc.getAsBytes();
    }

    @Test
    public void testSetContentData()
        throws UnsupportedEncodingException
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
        CustomContentData contentData = new CustomContentData( config );

        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" );
        TextAreaDataEntryConfig textareaConfig =
            new TextAreaDataEntryConfig( "myTextarea", true, "My text area", "contentdata/mytextarea" );

        HtmlAreaDataEntryConfig htmlDefaultConfig =
            new HtmlAreaDataEntryConfig( "myHtmlareaDefault", false, "My html area default", "contentdata/myhtmlareadefault" );
        HtmlAreaDataEntryConfig htmlXhtmlConfig =
            new HtmlAreaDataEntryConfig( "myHtmlareaXhtml", false, "My html area xhtml", "contentdata/myhtmlareaxhtml" );
        HtmlAreaDataEntryConfig htmlCdataConfig =
            new HtmlAreaDataEntryConfig( "myHtmlareaCdata", false, "My html area cdata", "contentdata/myhtmlareacdata" );

        contentData.add( new TextDataEntry( titleConfig, "x" ) );
        contentData.add( new TextAreaDataEntry( textareaConfig, "x" ) );
        contentData.add( new HtmlAreaDataEntry( htmlDefaultConfig, "<x/>" ) );
        contentData.add( new HtmlAreaDataEntry( htmlXhtmlConfig, "<x/>" ) );
        contentData.add( new HtmlAreaDataEntry( htmlCdataConfig, "<x/>" ) );

        ContentHandlerEntity contentHandler = new ContentHandlerEntity();
        contentHandler.setClassName( ContentHandlerName.CUSTOM.getHandlerClassShortName() );
        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setHandler( contentHandler );
        contentType.setData( standardConfigAsXMLBytes );
        CategoryEntity category = new CategoryEntity();
        category.setContentType( contentType );
        ContentEntity content = new ContentEntity();
        content.setCategory( category );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setContent( content );
        version.setContentData( contentData );
        /*XMLBytes xmlBytes = version.getXmlData();
        String xmlAsString = xmlBytes.getAsString();
        ( "xmlAsString: " + xmlAsString );
        Document xmlASJdomDoc = xmlBytes.getAsJDOMDocument();
        ( "xmlASJdomDoc:" );
        ( JDOMUtil.prettyPrintDocument( xmlASJdomDoc, "   " ) );
        org.w3c.dom.Document xmlASDomDoc = xmlBytes.getAsDOMDocument();
        ( "xmlASDomDoc:" );
        XMLTool.printDocument( xmlASDomDoc, 3 );

        byte[] bytes = xmlBytes.getData();
        XMLBytes newXmlBytes = new XMLBytes(bytes);

        version.setXmlData( newXmlBytes );
        ContentData contentData2 = version.getContentData();
        HtmlAreaDataEntry htmlAreaDataEntry = (HtmlAreaDataEntry) contentData2.getEntry( "myHtmlareaCdata" );
        ( "myHtmlareaCdata: " + htmlAreaDataEntry.getValue() );


        String test = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<contentdata><test><mytitle>test1</mytitle><mybinaryfile><binarydata key=\"2078\"/></mybinaryfile><related key=\"193\"/><relatedmultiple><content key=\"194\"/><content key=\"193\"/></relatedmultiple><myxml><balle>rusk</balle></myxml><myxhtmlarea>\n" +
            "        <p>xhtml area</p>\n" + "        <p>ny linje</p>\n" + "        <p>her kommer <strong>fet</strong> skrift</p>\n" +
            "    </myxhtmlarea><myhtmlarea><![CDATA[<p>vanlig cdata html area</p>\n" + "<p>her kommer ny linje</p>\n" +
            "<p>her kommer <strong>fet</strong> skrift</p>]]></myhtmlarea></test></contentdata>";

        ( "Xerces version: " + org.apache.xerces.impl.Version.getVersion() );
        XMLBytes otherXMLBytes = XMLDocumentParser.getInstance().parseDocument( new String( test.getBytes(), "UTF-8" ));
        Document testDoc = otherXMLBytes.getAsJDOMDocument();                 */
    }
}
