/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.content.imports;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.imports.ImportDataEntry;
import com.enonic.cms.core.content.imports.ImportDataReaderXml;
import com.enonic.cms.core.content.imports.sourcevalueholders.AbstractSourceValue;
import com.enonic.cms.core.content.imports.sourcevalueholders.StringSourceValue;
import org.jdom.Document;
import org.jdom.xpath.XPath;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.CtyFormConfig;
import com.enonic.cms.core.content.contenttype.CtyImportConfig;
import com.enonic.cms.core.content.contenttype.CtyImportMappingConfig;
import com.enonic.cms.core.content.contenttype.CtyImportModeConfig;
import com.enonic.cms.core.content.contenttype.CtySetConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.HtmlAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.XmlDataEntryConfig;

import static org.junit.Assert.*;

/**
 * Feb 6, 2010
 */
public class ImportDataReaderXmlTest
{
    private ContentTypeConfig contentTypeConfig;

    private CtyFormConfig ctyFormConfig;

    private CtySetConfig ctySetConfig;

    private CtyImportConfig importMultiTypeXml;

    private CtyImportMappingConfig importMappingId;


    @Before
    public void before()
    {
        contentTypeConfig = new ContentTypeConfig( ContentHandlerName.CUSTOM, "Employee" );
        ctyFormConfig = new CtyFormConfig( contentTypeConfig );
        ctySetConfig = new CtySetConfig( ctyFormConfig, "MultiType", null );
        ctySetConfig.addInput( new TextDataEntryConfig( "id", false, "Required id", "contentdata/id" ) );
        ctySetConfig.addInput( new HtmlAreaDataEntryConfig( "htmlarea", false, "HTML area", "contentdata/htmlarea" ) );
        ctySetConfig.addInput( new XmlDataEntryConfig( "xml", false, "XML", "contentdata/xml" ) );
        ctySetConfig.addInput( new TextAreaDataEntryConfig( "textarea", false, "Text area", "contentdata/textarea" ) );
        ctySetConfig.addInput( new TextAreaDataEntryConfig( "text", false, "Text", "contentdata/text" ) );
        ctyFormConfig.addBlock( ctySetConfig );

        importMultiTypeXml = new CtyImportConfig( ctyFormConfig, "import-xml", null, null );
        importMultiTypeXml.setBase( "/root/multi-type" );
        importMultiTypeXml.setMode( CtyImportModeConfig.XML );

        importMappingId = new CtyImportMappingConfig( importMultiTypeXml, "id/@value", "id" );
        importMultiTypeXml.addMapping( importMappingId );

    }

    @Test
    public void wrapping_element_referred_by_htmlarea_mapping_is_not_included_in_value()
        throws UnsupportedEncodingException
    {
        CtyImportMappingConfig importMappingHtmlarea = new CtyImportMappingConfig( importMultiTypeXml, "my-htmlarea", "htmlarea" );
        importMultiTypeXml.addMapping( importMappingHtmlarea );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "<root><multi-type><id value='1001'/><my-htmlarea><div>some text</div></my-htmlarea></multi-type></root>" );

        ImportDataReaderXml reader = new ImportDataReaderXml( importMultiTypeXml, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );

        ImportDataEntry importEntry = reader.getNextEntry();

        assertFalse( reader.hasMoreEntries() );

        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap = importEntry.getConfigAndValueMap();
        String htmlareaAsString = ( (StringSourceValue) configAndValueMap.get( importMappingHtmlarea ) ).getValue();
        Document htlmareaAsDocument = XMLDocumentFactory.create( htmlareaAsString ).getAsJDOMDocument();
        assertFalse( xpathExists( "/my-htmlarea/div", htlmareaAsDocument ) );
        assertTrue( xpathExists( "/div", htlmareaAsDocument ) );
    }

    @Test
    public void reading_htmlarea_includes_all_root_nodes()
        throws UnsupportedEncodingException
    {
        CtyImportMappingConfig importMappingHtmlarea = new CtyImportMappingConfig( importMultiTypeXml, "my-htmlarea", "htmlarea" );
        importMultiTypeXml.addMapping( importMappingHtmlarea );

        StringBuffer importSource = new StringBuffer();
        importSource.append(
            "<root><multi-type><id value='1001'/><my-htmlarea>before<div>first</div><p>second</p></my-htmlarea></multi-type></root>" );

        ImportDataReaderXml reader = new ImportDataReaderXml( importMultiTypeXml, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );

        // exercise
        ImportDataEntry importEntry = reader.getNextEntry();

        assertFalse( reader.hasMoreEntries() );

        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap = importEntry.getConfigAndValueMap();
        String htmlareaAsString =
            XMLDocumentFactory.create( ( (StringSourceValue) configAndValueMap.get( importMappingHtmlarea ) ).getValue() ).getAsString();
        assertEquals( "before<div>first</div><p>second</p>", htmlareaAsString );
    }

    @Test
    public void wrapping_element_referred_by_xml_mapping_is_not_included_in_value()
        throws UnsupportedEncodingException
    {
        CtyImportMappingConfig importMappingXml = new CtyImportMappingConfig( importMultiTypeXml, "my-xml", "xml" );
        importMultiTypeXml.addMapping( importMappingXml );

        StringBuffer importSource = new StringBuffer();
        importSource.append(
            "<root><multi-type><id value='1001'/><my-xml><root>some text<subnode>subtext</subnode></root></my-xml></multi-type></root>" );

        ImportDataReaderXml reader = new ImportDataReaderXml( importMultiTypeXml, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );

        ImportDataEntry importEntry = reader.getNextEntry();

        assertFalse( reader.hasMoreEntries() );

        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap = importEntry.getConfigAndValueMap();

        Document xmlAsDocument =
            XMLDocumentFactory.create( ( (StringSourceValue) configAndValueMap.get( importMappingXml ) ).getValue() ).getAsJDOMDocument();
        assertFalse( xpathExists( "/my-xml/root", xmlAsDocument ) );
        assertTrue( xpathExists( "/root", xmlAsDocument ) );

        String xmlAsString =
            XMLDocumentFactory.create( ( (StringSourceValue) configAndValueMap.get( importMappingXml ) ).getValue() ).getAsString();
        assertEquals( "<root>some text<subnode>subtext</subnode></root>", xmlAsString );
    }

    @Test
    public void wrapping_element_referred_by_textarea_mapping_is_not_included_in_value()
        throws UnsupportedEncodingException
    {
        CtyImportMappingConfig importMappingTextarea = new CtyImportMappingConfig( importMultiTypeXml, "my-textarea", "textarea" );
        importMultiTypeXml.addMapping( importMappingTextarea );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "<root><multi-type><id value='1001'/><my-textarea>some text</my-textarea></multi-type></root>" );

        ImportDataReaderXml reader = new ImportDataReaderXml( importMultiTypeXml, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );

        ImportDataEntry importEntry = reader.getNextEntry();

        assertFalse( reader.hasMoreEntries() );

        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap = importEntry.getConfigAndValueMap();
        String textareaAsString = ( (StringSourceValue) configAndValueMap.get( importMappingTextarea ) ).getValue();
        assertEquals( "some text", textareaAsString );
    }

    @Test
    public void wrapping_element_referred_by_text_mapping_is_not_included_in_value()
        throws UnsupportedEncodingException
    {
        CtyImportMappingConfig importMappingText = new CtyImportMappingConfig( importMultiTypeXml, "my-text", "text" );
        importMultiTypeXml.addMapping( importMappingText );

        StringBuffer importSource = new StringBuffer();
        importSource.append( "<root><multi-type><id value='1001'/><my-text>some text</my-text></multi-type></root>" );

        ImportDataReaderXml reader = new ImportDataReaderXml( importMultiTypeXml, stringBufferToInputStream( importSource ) );

        assertTrue( reader.hasMoreEntries() );

        ImportDataEntry importEntry = reader.getNextEntry();

        assertFalse( reader.hasMoreEntries() );

        Map<CtyImportMappingConfig, AbstractSourceValue> configAndValueMap = importEntry.getConfigAndValueMap();
        String textAsString = ( (StringSourceValue) configAndValueMap.get( importMappingText ) ).getValue();
        assertEquals( "some text", textAsString );
    }

    private InputStream stringBufferToInputStream( StringBuffer buffer )
        throws UnsupportedEncodingException
    {
        return new ByteArrayInputStream( buffer.toString().getBytes( "UTF-8" ) );
    }

    private static boolean xpathExists( String xpathString, Document doc )
    {
        try
        {
            List nodes = XPath.selectNodes( doc.getRootElement(), xpathString );
            return nodes.size() > 0;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
}