/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom.support;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;

import com.enonic.cms.core.content.ContentHandlerName;
import org.apache.commons.fileupload.FileItem;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.DateDataEntry;
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
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;

import static org.junit.Assert.*;

/**
 * Created by rmy - Date: Jun 11, 2009
 */
public class CustomContentDataFormParserTest
{
    private CustomContentDataFormParser customContentParser;

    private ContentTypeConfig customConfig;

    @Before
    public void setUp()
        throws IOException, JDOMException
    {
        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"TestContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );
        standardConfigXml.append( "         <title name=\"MyTitle\"/>" );
        standardConfigXml.append( "         <block name=\"TestBlock\">" );

        standardConfigXml.append( "             <input name=\"MyTitle\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>MyTitle</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyText\" type=\"text\">" );
        standardConfigXml.append( "                 <display>MyText</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyTextArea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>MyTextArea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyHtmlArea\" type=\"htmlarea\">" );
        standardConfigXml.append( "                 <display>MyHtmlArea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlarea</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>MyDate</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyUploadfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>MyUploadfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myuploadfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyCheckbox\" type=\"checkbox\">" );
        standardConfigXml.append( "                 <display>MyCheckbox</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mycheckbox</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyImages\" type=\"images\">" );
        standardConfigXml.append( "                 <display>MyImages</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimages</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyImage\" type=\"image\">" );
        standardConfigXml.append( "                 <display>MyIage</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimage</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MySingleRelatedcontent\" type=\"relatedcontent\" multiple=\"false\">" );
        standardConfigXml.append( "                 <display>MySingleRelatedContent</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mysinglerelatedcontent</xpath>" );
        standardConfigXml.append( "                 <contenttype name=\"TestContentType\"/>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyMultipleRelatedcontent\" type=\"relatedcontent\" multiple=\"true\">" );
        standardConfigXml.append( "                 <display>MyMultipleRelatedContent</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mymultiplerelatedcontent</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyFiles\" type=\"files\">" );
        standardConfigXml.append( "                 <display>MyFiles</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfiles</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyFile\" type=\"file\">" );
        standardConfigXml.append( "                 <display>MyFile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyRadiobutton\" type=\"radiobutton\">" );
        standardConfigXml.append( "                 <display>MyRadiobutton</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myradiobutton</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                    <option value=\"r1\">Choice 1</option>" );
        standardConfigXml.append( "                    <option value=\"r2\">Choice 2</option>" );
        standardConfigXml.append( "                    <option value=\"r3\">Choice 3</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyUrl\" type=\"url\">" );
        standardConfigXml.append( "                 <display>MyUrl</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myurl</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyXml\" type=\"xml\">" );
        standardConfigXml.append( "                 <display>MyXml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myxml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"MyDropdown\" type=\"dropdown\">" );
        standardConfigXml.append( "                 <display>MyDropdown</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydropdown</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                    <option value=\"o1\">Option 1</option>" );
        standardConfigXml.append( "                    <option value=\"o2\">Option 2</option>" );
        standardConfigXml.append( "                    <option value=\"o3\">Option 3</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        Element standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        customConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
    }

    @Test
    public void testGetNextPlaceHolder()
    {

        customContentParser = new CustomContentDataFormParser( customConfig, new ExtendedMap( true ) );

        assertEquals( "%0", customContentParser.getNextBinaryPlaceholder() );
        assertEquals( "%1", customContentParser.getNextBinaryPlaceholder() );
        assertEquals( "%2", customContentParser.getNextBinaryPlaceholder() );
        assertEquals( "%3", customContentParser.getNextBinaryPlaceholder() );

        CustomContentDataFormParser parser2 = new CustomContentDataFormParser( customConfig, new ExtendedMap( true ) );

        assertEquals( "%0", parser2.getNextBinaryPlaceholder() );
        assertEquals( "%1", parser2.getNextBinaryPlaceholder() );
    }

    @Test
    public void testParseAllFromItems_ValuePresent()
        throws Exception
    {
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.put( "MyTitle", "TestTitle" );
        formItems.put( "MyText", "TestText" );
        formItems.put( "MyTextArea", "TestTextArea" );
        formItems.put( "Fisk", "Ost" );
        formItems.put( "MyHtmlArea", "<head></head><body></body>" );
        formItems.put( "MyDate", "10.10.2009" );
        formItems.put( "MyUploadfile", new TestFileItem( new byte[]{0xA, 0xB, 0xC} ) );
        formItems.put( "MyCheckbox", "on" );
        formItems.put( "MyImages", new String[]{"123", "234", "345"} );
        formItems.put( "MyImagestext", new String[]{"123text", "234text", "345text"} );
        formItems.put( "MyImage", "678" );
        formItems.put( "MySingleRelatedcontent", "567" );
        formItems.put( "MyMultipleRelatedcontent", new String[]{"33", "44", "55"} );
        formItems.put( "MyFiles", new String[]{"321", "432", "543"} );
        formItems.put( "MyFile", "789" );
        formItems.put( "MyRadiobutton", "r2" );
        formItems.put( "MyUrl", "http://www.enonic.com" );
        formItems.put( "MyXml", "<root><node></node></root>" );
        formItems.put( "MyDropdown", "o2" );

        customContentParser = new CustomContentDataFormParser( customConfig, formItems );
        CustomContentData contentData = customContentParser.parseContentData();

        assertEquals( 17, contentData.getEntries().size() );
        assertEquals( "TestTitle", contentData.getTitle() );
        assertTrue( contentData.getEntry( "MyTitle" ).hasValue() );
        assertEquals( "TestTitle", ( (TextDataEntry) contentData.getEntry( "MyTitle" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyText" ).hasValue() );
        assertEquals( "TestText", ( (TextDataEntry) contentData.getEntry( "MyText" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyTextArea" ).hasValue() );
        assertEquals( "TestTextArea", ( (TextAreaDataEntry) contentData.getEntry( "MyTextArea" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyHtmlArea" ).hasValue() );
        assertEquals( "<head></head><body></body>", ( (HtmlAreaDataEntry) contentData.getEntry( "MyHtmlArea" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyDate" ).hasValue() );
        assertEquals( ( new SimpleDateFormat( "dd.MM.yyyy" ) ).parse( "10.10.2009" ),
                      ( (DateDataEntry) contentData.getEntry( "MyDate" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyUploadfile" ).hasValue() );
        BinaryDataEntry data = ( (BinaryDataEntry) contentData.getEntry( "MyUploadfile" ) );
        assertEquals( 3, data.getBinary().length );
        assertEquals( 0xA, data.getBinary()[0] );
        assertEquals( 0xB, data.getBinary()[1] );
        assertEquals( 0xC, data.getBinary()[2] );

        assertTrue( contentData.getEntry( "MyCheckbox" ).hasValue() );
        assertEquals( true, ( (BooleanDataEntry) contentData.getEntry( "MyCheckbox" ) ).getValueAsBoolean() );

        assertTrue( contentData.getEntry( "MyImages" ).hasValue() );
        ImagesDataEntry images = ( (ImagesDataEntry) contentData.getEntry( "MyImages" ) );
        assertEquals( 3, images.getEntries().size() );
        assertEquals( 123, ( (ImageDataEntry) images.getEntries().toArray()[0] ).getContentKey().toInt() );
        assertEquals( "123text", ( (ImageDataEntry) images.getEntries().toArray()[0] ).getImageText() );
        assertEquals( 234, ( (ImageDataEntry) images.getEntries().toArray()[1] ).getContentKey().toInt() );
        assertEquals( "234text", ( (ImageDataEntry) images.getEntries().toArray()[1] ).getImageText() );
        assertEquals( 345, ( (ImageDataEntry) images.getEntries().toArray()[2] ).getContentKey().toInt() );
        assertEquals( "345text", ( (ImageDataEntry) images.getEntries().toArray()[2] ).getImageText() );

        assertTrue( contentData.getEntry( "MyImage" ).hasValue() );
        assertEquals( 678, ( (ImageDataEntry) contentData.getEntry( "MyImage" ) ).getContentKey().toInt() );

        assertTrue( contentData.getEntry( "MySingleRelatedcontent" ).hasValue() );
        assertEquals( 567, ( (RelatedContentDataEntry) contentData.getEntry( "MySingleRelatedcontent" ) ).getContentKey().toInt() );

        assertTrue( contentData.getEntry( "MyMultipleRelatedcontent" ).hasValue() );
        RelatedContentsDataEntry releatedContents = ( (RelatedContentsDataEntry) contentData.getEntry( "MyMultipleRelatedcontent" ) );
        assertEquals( 3, releatedContents.getEntries().size() );
        assertEquals( 33, ( (RelatedContentDataEntry) releatedContents.getEntries().toArray()[0] ).getContentKey().toInt() );
        assertEquals( 44, ( (RelatedContentDataEntry) releatedContents.getEntries().toArray()[1] ).getContentKey().toInt() );
        assertEquals( 55, ( (RelatedContentDataEntry) releatedContents.getEntries().toArray()[2] ).getContentKey().toInt() );

        assertTrue( contentData.getEntry( "MyFiles" ).hasValue() );
        FilesDataEntry files = ( (FilesDataEntry) contentData.getEntry( "MyFiles" ) );
        assertEquals( 3, files.getEntries().size() );
        assertEquals( 321, ( (FileDataEntry) files.getEntries().toArray()[0] ).getContentKey().toInt() );
        assertEquals( 432, ( (FileDataEntry) files.getEntries().toArray()[1] ).getContentKey().toInt() );
        assertEquals( 543, ( (FileDataEntry) files.getEntries().toArray()[2] ).getContentKey().toInt() );

        assertTrue( contentData.getEntry( "MyFile" ).hasValue() );
        assertEquals( 789, ( (FileDataEntry) contentData.getEntry( "MyFile" ) ).getContentKey().toInt() );

        assertTrue( contentData.getEntry( "MyRadiobutton" ).hasValue() );
        assertEquals( "r2", ( (SelectorDataEntry) contentData.getEntry( "MyRadiobutton" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyUrl" ).hasValue() );
        assertEquals( "http://www.enonic.com", ( (UrlDataEntry) contentData.getEntry( "MyUrl" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyXml" ).hasValue() );

        Document doc = ( (XmlDataEntry) contentData.getEntry( "MyXml" ) ).getValue();
        assertEquals( JDOMUtil.prettyPrintDocument( JDOMUtil.parseDocument( "<root><node></node></root>" ) ),
                      JDOMUtil.prettyPrintDocument( doc ) );

        assertTrue( contentData.getEntry( "MyDropdown" ).hasValue() );
        assertEquals( "o2", ( (SelectorDataEntry) contentData.getEntry( "MyDropdown" ) ).getValue() );
    }


    @Test
    public void testParseAllFromItems_ValueEmpty()
        throws Exception
    {
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.put( "MyTitle", "TestTitle" );
        formItems.put( "MyText", "" );
        formItems.put( "MyTextArea", "" );
        formItems.put( "Fisk", "Ost" );
        formItems.put( "MyHtmlArea", "" );
        formItems.put( "MyDate", "" );
        formItems.put( "MyUploadfile", new TestFileItem( null ) );
        formItems.put( "MyCheckbox", "" );
        formItems.put( "MyImages", new String[]{"", "", ""} );
        formItems.put( "MyImagestext", new String[]{"", "", ""} );
        formItems.put( "MyImage", "" );
        formItems.put( "MySingleRelatedcontent", "" );
        formItems.put( "MyMultipleRelatedcontent", new String[]{"", "", ""} );
        formItems.put( "MyFiles", new String[]{"", "", ""} );
        formItems.put( "MyFile", "" );
        formItems.put( "MyRadiobutton", "" );
        formItems.put( "MyUrl", "" );
        formItems.put( "MyXml", "" );
        formItems.put( "MyDropdown", "" );

        customContentParser = new CustomContentDataFormParser( customConfig, formItems );
        CustomContentData contentData = customContentParser.parseContentData();

        assertEquals( 17, contentData.getEntries().size() );
        assertEquals( "TestTitle", contentData.getTitle() );
        assertTrue( contentData.getEntry( "MyTitle" ).hasValue() );
        assertEquals( "TestTitle", ( (TextDataEntry) contentData.getEntry( "MyTitle" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyText" ).hasValue() );
        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "MyText" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyTextArea" ).hasValue() );
        assertEquals( "", ( (TextAreaDataEntry) contentData.getEntry( "MyTextArea" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyHtmlArea" ).hasValue() );
        assertEquals( "", ( (HtmlAreaDataEntry) contentData.getEntry( "MyHtmlArea" ) ).getValue() );

        assertFalse( contentData.getEntry( "MyDate" ).hasValue() );
        assertNull( ( (DateDataEntry) contentData.getEntry( "MyDate" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyUploadfile" ).hasValue() );
        BinaryDataEntry data = ( (BinaryDataEntry) contentData.getEntry( "MyUploadfile" ) );
        assertEquals( 0, data.getBinary().length );

        assertTrue( contentData.getEntry( "MyCheckbox" ).hasValue() );
        assertEquals( false, ( (BooleanDataEntry) contentData.getEntry( "MyCheckbox" ) ).getValueAsBoolean() );

        assertFalse( contentData.getEntry( "MyImages" ).hasValue() );
        ImagesDataEntry images = ( (ImagesDataEntry) contentData.getEntry( "MyImages" ) );
        assertEquals( 0, images.getEntries().size() );

        assertFalse( contentData.getEntry( "MyImage" ).hasValue() );
        assertNull( ( (ImageDataEntry) contentData.getEntry( "MyImage" ) ).getContentKey() );

        assertFalse( contentData.getEntry( "MySingleRelatedcontent" ).hasValue() );
        assertNull( ( (RelatedContentDataEntry) contentData.getEntry( "MySingleRelatedcontent" ) ).getContentKey() );

        assertFalse( contentData.getEntry( "MyMultipleRelatedcontent" ).hasValue() );
        RelatedContentsDataEntry releatedContents = ( (RelatedContentsDataEntry) contentData.getEntry( "MyMultipleRelatedcontent" ) );
        assertEquals( 0, releatedContents.getEntries().size() );

        assertFalse( contentData.getEntry( "MyFiles" ).hasValue() );
        FilesDataEntry files = ( (FilesDataEntry) contentData.getEntry( "MyFiles" ) );
        assertEquals( 0, files.getEntries().size() );

        assertFalse( contentData.getEntry( "MyFile" ).hasValue() );
        assertNull( ( (FileDataEntry) contentData.getEntry( "MyFile" ) ).getContentKey() );

        assertTrue( contentData.getEntry( "MyRadiobutton" ).hasValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "MyRadiobutton" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyUrl" ).hasValue() );
        assertEquals( "", ( (UrlDataEntry) contentData.getEntry( "MyUrl" ) ).getValue() );

        assertFalse( contentData.getEntry( "MyXml" ).hasValue() );
        assertNull( ( (XmlDataEntry) contentData.getEntry( "MyXml" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyDropdown" ).hasValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "MyDropdown" ) ).getValue() );
    }

    @Test
    public void testParseAllFromItems_ValueNull()
        throws Exception
    {
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.put( "MyTitle", "TestTitle" );
        formItems.put( "MyText", null );
        formItems.put( "MyTextArea", null );
        formItems.put( "Fisk", "Ost" );
        formItems.put( "MyHtmlArea", null );
        formItems.put( "MyDate", null );
        formItems.put( "MyUploadfile", null );
        formItems.put( "MyCheckbox", null );
        formItems.put( "MyImages", null );
        formItems.put( "MyImagestext", null );
        formItems.put( "MyImage", null );
        formItems.put( "MySingleRelatedcontent", null );
        formItems.put( "MyMultipleRelatedcontent", null );
        formItems.put( "MyFiles", null );
        formItems.put( "MyFile", null );
        formItems.put( "MyRadiobutton", null );
        formItems.put( "MyUrl", null );
        formItems.put( "MyXml", null );
        formItems.put( "MyDropdown", null );

        customContentParser = new CustomContentDataFormParser( customConfig, formItems );
        CustomContentData contentData = customContentParser.parseContentData();

        assertEquals( 17, contentData.getEntries().size() );
        assertEquals( "TestTitle", contentData.getTitle() );
        assertTrue( contentData.getEntry( "MyTitle" ).hasValue() );
        assertEquals( "TestTitle", ( (TextDataEntry) contentData.getEntry( "MyTitle" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyText" ).hasValue() );
        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "MyText" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyTextArea" ).hasValue() );
        assertEquals( "", ( (TextAreaDataEntry) contentData.getEntry( "MyTextArea" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyHtmlArea" ).hasValue() );
        assertEquals( "", ( (HtmlAreaDataEntry) contentData.getEntry( "MyHtmlArea" ) ).getValue() );

        assertFalse( contentData.getEntry( "MyDate" ).hasValue() );
        assertNull( ( (DateDataEntry) contentData.getEntry( "MyDate" ) ).getValue() );

        assertFalse( contentData.getEntry( "MyUploadfile" ).hasValue() );
        assertNull( ( (BinaryDataEntry) contentData.getEntry( "MyUploadfile" ) ).getBinary() );

        assertTrue( contentData.getEntry( "MyCheckbox" ).hasValue() );
        assertEquals( "false", ( (BooleanDataEntry) contentData.getEntry( "MyCheckbox" ) ).getValueAsString() );

        assertFalse( contentData.getEntry( "MyImages" ).hasValue() );
        assertEquals( 0, ( (ImagesDataEntry) contentData.getEntry( "MyImages" ) ).getEntries().size() );

        assertFalse( contentData.getEntry( "MyImage" ).hasValue() );
        assertNull( ( (ImageDataEntry) contentData.getEntry( "MyImage" ) ).getContentKey() );

        assertFalse( contentData.getEntry( "MySingleRelatedcontent" ).hasValue() );
        assertNull( ( (RelatedContentDataEntry) contentData.getEntry( "MySingleRelatedcontent" ) ).getContentKey() );

        assertFalse( contentData.getEntry( "MyMultipleRelatedcontent" ).hasValue() );
        assertEquals( 0, ( (RelatedContentsDataEntry) contentData.getEntry( "MyMultipleRelatedcontent" ) ).getEntries().size() );

        assertFalse( contentData.getEntry( "MyFiles" ).hasValue() );
        assertEquals( 0, ( (FilesDataEntry) contentData.getEntry( "MyFiles" ) ).getEntries().size() );

        assertFalse( contentData.getEntry( "MyFile" ).hasValue() );
        assertNull( ( (FileDataEntry) contentData.getEntry( "MyFile" ) ).getContentKey() );

        assertTrue( contentData.getEntry( "MyRadiobutton" ).hasValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "MyRadiobutton" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyUrl" ).hasValue() );
        assertEquals( "", ( (UrlDataEntry) contentData.getEntry( "MyUrl" ) ).getValue() );

        assertFalse( contentData.getEntry( "MyXml" ).hasValue() );
        assertNull( ( (XmlDataEntry) contentData.getEntry( "MyXml" ) ).getValue() );

        assertTrue( contentData.getEntry( "MyDropdown" ).hasValue() );
        assertEquals( "", ( (SelectorDataEntry) contentData.getEntry( "MyDropdown" ) ).getValue() );
    }

    private class TestFileItem
        implements FileItem
    {
        private byte[] value = null;

        private TestFileItem( byte[] value )
        {
            this.value = value;
        }

        public InputStream getInputStream()
            throws IOException
        {
            if ( value == null )
            {
                return new ByteArrayInputStream( new byte[]{} );
            }
            return new ByteArrayInputStream( value );
        }

        public String getContentType()
        {
            return null;
        }

        public String getName()
        {
            return "TestFileItem";
        }

        public boolean isInMemory()
        {
            return true;
        }

        public long getSize()
        {
            return value.length;
        }

        public byte[] get()
        {
            return value;
        }

        public String getString( String s )
            throws UnsupportedEncodingException
        {
            return null;
        }

        public String getString()
        {
            return null;
        }

        public void write( File file )
            throws Exception
        {
        }

        public void delete()
        {
        }

        public String getFieldName()
        {
            return null;
        }

        public void setFieldName( String s )
        {
        }

        public boolean isFormField()
        {
            return false;
        }

        public void setFormField( boolean b )
        {
        }

        public OutputStream getOutputStream()
            throws IOException
        {
            return null;
        }
    }

}
