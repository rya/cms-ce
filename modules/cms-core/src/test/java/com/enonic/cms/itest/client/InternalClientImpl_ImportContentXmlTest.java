/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jdom.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.itest.test.AssertTool;

import com.enonic.cms.core.business.SpecialCharacterTestStrings;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.HtmlAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.xmlbased.XmlDataEntry;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class InternalClientImpl_ImportContentXmlTest
    extends AbstractInternalClientImpl_ImportContentTest
{
    @Test
    public void testStringBasedXmlImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImport() );
        doImport( getStringBasedXmlImportData( count, "Oslo" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/age", dataDoc, "1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/info", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );

        final CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData.getEntry( "name" ) ).getValue().equals(
            "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                    "src/test" +
                SpecialCharacterTestStrings.AEC_ALL + "src/test1" ) );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "address" ) ).getValue().equals( "Oslo1" ) );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "age" ) ).getValue().equals( "1" ) );
        assertTrue( ( (TextAreaDataEntry) contentData.getEntry( "info" ) ).getValue().equals(
            "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                    "src/test" +
                SpecialCharacterTestStrings.AEC_ALL + "src/test1" ) );

        final Document xmlDoc = ( (XmlDataEntry) contentData.getEntry( "xmlInfo" ) ).getValue();
        final Document htmlDoc = JDOMUtil.parseDocument( ( (HtmlAreaDataEntry) contentData.getEntry( "htmlInfo" ) ).getValue() );
        final String testAtr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test1";
        final String testElem = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test1";
        AssertTool.assertSingleXPathValueEquals( "/root/@atr", xmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/root/elem", xmlDoc, testElem );
        AssertTool.assertSingleXPathValueEquals( "/p/@class", htmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/p/div", htmlDoc, testElem );
    }

    @Test
    public void testStringBasedXmlImportVersionCountCheck()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImport() );
        doImport( getStringBasedXmlImportData( count, "Oslo" ), "testuser", "MyImport" );
        doImport( getStringBasedXmlImportData( count, null ), "testuser2", "MyImport2" );

        List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        for ( int i = 0; i < count; i++ )
        {
            final ContentEntity content = contentDao.findByKey( contentKeys.get( i ) );
            assertEquals( 1, content.getVersionCount() );
            assertEquals( "testuser", content.getOwner().getName() );
        }

        doImport( getStringBasedXmlImportData( count, "Majorstua" ), "testuser", "MyImport" );

        contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        for ( int i = 0; i < count; i++ )
        {
            final ContentEntity content = contentDao.findByKey( contentKeys.get( i ) );
            assertEquals( 2, content.getVersionCount() );
            assertEquals( "testuser", content.getOwner().getName() );
        }
    }

    @Test
    public void testStringBasedXmlImportWithBlocks()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImportWithBlocks() );
        doImport( getStringBasedXmlImportDataWithBlocks( count ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/age", dataDoc, "1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/info", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
    }

    @Test
    public void testStringBasedXmlImportWithBlocks_ToBlockGroups()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImportWithBlocksToBlocks() );
        doImport( getStringBasedXmlImportDataWithBlocksToBlocks( count, "value", false ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );

        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyA']/value", dataDoc, "valueA" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyB']/value", dataDoc, "valueB" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyC']/value", dataDoc, "valueC" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyD']/value", dataDoc, "valueD" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyE']/value", dataDoc, "valueE" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyF']/value", dataDoc, "valueF" );
    }

    @Test
    public void testStringBasedXmlImportWithBlocks_ToBlockGroups2()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImportWithBlocksToBlocks() );

        /* Import I */
        doImport( getStringBasedXmlImportDataWithBlocksToBlocks( count, "value", false ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );

        assertEquals( 1, content.getVersionCount() );
        assertEquals( count, contentKeys.size() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();

        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );

        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyA']/value", dataDoc, "valueA" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyB']/value", dataDoc, "valueB" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyC']/value", dataDoc, "valueC" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyD']/value", dataDoc, "valueD" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyE']/value", dataDoc, "valueE" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyF']/value", dataDoc, "valueF" );

        /* Import II - changes in block groups */
        doImport( getStringBasedXmlImportDataWithBlocksToBlocks( count, "newValue", true ) );

        final List<ContentKey> contentKeys2 = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys2.size() );
        final ContentEntity content2 = contentDao.findByKey( contentKeys2.get( 0 ) );

        assertEquals( 2, content2.getVersionCount() );
        assertEquals( count, contentKeys2.size() );

        final Document dataDoc2 = content2.getMainVersion().getContentDataAsJDomDocument();

        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc2, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );

        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyA']/value", dataDoc2, "valueA" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyB']/value", dataDoc2, "newValueB" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv1[key='keyC']/value", dataDoc2, "valueC" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyD']/value", dataDoc2, "valueD" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyE']/value", dataDoc2, "newValueE" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyF']/value", dataDoc2, "valueF" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyG']/value", dataDoc2, "newValueG" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/kv2[key='keyH']/value", dataDoc2, "newValueH" );

        /* Import III - no changes */
        doImport( getStringBasedXmlImportDataWithBlocksToBlocks( count, "newValue", true ) );

        final List<ContentKey> contentKeys3 = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys3.size() );
        final ContentEntity content3 = contentDao.findByKey( contentKeys3.get( 0 ) );

        assertEquals( 2, content3.getVersionCount() );
        assertEquals( count, contentKeys3.size() );
    }

    @Test
    public void testStringBasedXmlImportWithNamespaces()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImportWithNamespaces() );
        doImport( getStringBasedXmlImportDataWithNamespaces( count ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/max", dataDoc, "789" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/min", dataDoc, "123" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/year", dataDoc,
                                                 String.valueOf( ( new GregorianCalendar().get( Calendar.YEAR ) ) ) );
    }

    @Test
    public void testStringBasedXmlImport_WithBlankValues()
        throws Exception
    {
        /* Import I */
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImport_Simple() );
        doImport( getStringBasedXmlImportData_Simple( count, "JAM", "Oslo" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "JAM1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo" );

        final CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData.getEntry( "name" ) ).getValue().equals( "JAM1" ) );
        assertTrue( contentData.getEntry( "address" ).hasValue() );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "address" ) ).getValue().equals( "Oslo" ) );

        /* Import II - Blank address */
        doImport( getStringBasedXmlImportData_Simple( count, "JAM", "" ) );

        final List<ContentKey> contentKeys2 = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys2.size() );
        final ContentEntity content2 = contentDao.findByKey( contentKeys2.get( 0 ) );

        final Document dataDoc2 = content2.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc2, "JAM1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc2, "" );

        final CustomContentData contentData2 = (CustomContentData) content2.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData2.getEntry( "name" ) ).getValue().equals( "JAM1" ) );
        assertTrue( contentData2.getEntry( "address" ).hasValue() );
        assertTrue( ( (TextDataEntry) contentData2.getEntry( "address" ) ).getValue().equals( "" ) );

        /* Address changed - two versions */
        assertEquals( 2, content2.getVersionCount() );
    }

    @Test
    public void testStringBasedXmlImport_WithNullValues()
        throws Exception
    {
        /* Import I */
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImport_Simple() );
        doImport( getStringBasedXmlImportData_Simple( count, "JAM", "Oslo" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "JAM1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo" );

        final CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData.getEntry( "name" ) ).getValue().equals( "JAM1" ) );
        assertTrue( contentData.getEntry( "address" ).hasValue() );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "address" ) ).getValue().equals( "Oslo" ) );

        /* Import II - Null address */
        doImport( getStringBasedXmlImportData_Simple( count, "JAM", null ) );

        final List<ContentKey> contentKeys2 = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys2.size() );
        final ContentEntity content2 = contentDao.findByKey( contentKeys2.get( 0 ) );

        final Document dataDoc2 = content2.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc2, "JAM1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc2, "Oslo" );

        final CustomContentData contentData2 = (CustomContentData) content2.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData2.getEntry( "name" ) ).getValue().equals( "JAM1" ) );
        assertTrue( contentData2.getEntry( "address" ).hasValue() );
        assertTrue( ( (TextDataEntry) contentData2.getEntry( "address" ) ).getValue().equals( "Oslo" ) );

        /* Address not part of import - no changes - one version */
        assertEquals( 1, content2.getVersionCount() );
    }

    @Test
    public void testStringBasedXmlImportWithPublishFromAndToFromImportData()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImportWithPublishFromAndToFromImportData() );
        doImport( getStringBasedXmlImportDataWithPublishFromAndToFromImportData( count, "2001.01.01 01:01:01", "2020.20.20 20:20:20" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.01 01:01:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.20.20 20:20:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/age", dataDoc, "1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/info", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
    }

    @Test
    public void testStringBasedXmlImportWithoutPublishFromAndTo()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedXmlImport() );
        doImport( getStringBasedXmlImportData( count, "Oslo" ), "testuser", "MyImport", null, null );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertNull( content.getAvailableFrom() );
        assertNull( content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/address", dataDoc, "Oslo1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/age", dataDoc, "1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/info", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );

        final CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();
        assertTrue( ( (TextDataEntry) contentData.getEntry( "name" ) ).getValue().equals(
            "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                    "src/test" +
                SpecialCharacterTestStrings.AEC_ALL + "src/test1" ) );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "address" ) ).getValue().equals( "Oslo1" ) );
        assertTrue( ( (TextDataEntry) contentData.getEntry( "age" ) ).getValue().equals( "1" ) );
        assertTrue( ( (TextAreaDataEntry) contentData.getEntry( "info" ) ).getValue().equals(
            "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                    "src/test" +
                SpecialCharacterTestStrings.AEC_ALL + "src/test1" ) );

        final Document xmlDoc = ( (XmlDataEntry) contentData.getEntry( "xmlInfo" ) ).getValue();
        final Document htmlDoc = JDOMUtil.parseDocument( ( (HtmlAreaDataEntry) contentData.getEntry( "htmlInfo" ) ).getValue() );
        final String testAtr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test1";
        final String testElem = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test1";
        AssertTool.assertSingleXPathValueEquals( "/root/@atr", xmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/root/elem", xmlDoc, testElem );
        AssertTool.assertSingleXPathValueEquals( "/p/@class", htmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/p/div", htmlDoc, testElem );
    }

    @Test
    public void testContentKeyBasedXmlImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForContentKeyBasedXmlImport() );
        setupImageCategory();
        final ContentKey key1 = setupImage();
        final ContentKey key2 = setupImage();
        doImport( getContentKeyBasedXmlImportData( count, key1, key1, key1, key2 ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );
        assertEquals( 2, content.getMainVersion().getRelatedChildren( true ).size() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/picture/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/attachment/file/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/multirelcon/content[@key=\"" + key1 + "\"]/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/multirelcon/content[@key=\"" + key2 + "\"]/@key", dataDoc, key2.toString() );

        final Collection<ContentEntity> relatedContents = content.getMainVersion().getRelatedChildren( false );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key1 ) );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key2 ) );
    }

    @Test
    public void testCustomRelatedContentXmlImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForCustomRelatedContentXmlImport() );
        setupRelatedContentCategory();
        final ContentKey key1 = setupRelatedContent( "A" );
        final ContentKey key2 = setupRelatedContent( "B" );
        final ContentKey key3 = setupRelatedContent( "C" );
        doImport( getRelatedContentXmlImportData( count, "A", "B", "C" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );
        assertEquals( 3, content.getMainVersion().getRelatedChildren( true ).size() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key1 + "\"]/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key2 + "\"]/@key", dataDoc, key2.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key3 + "\"]/@key", dataDoc, key3.toString() );

        final Collection<ContentEntity> relatedContents = content.getMainVersion().getRelatedChildren( false );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key1 ) );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key2 ) );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key3 ) );
    }

    @Test
    public void testImageRelatedContentXmlImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForImageRelatedContentXmlImport() );
        setupImageCategory();
        final ContentKey key1 = setupImage( "A" );
        final ContentKey key2 = setupImage( "B" );
        final ContentKey key3 = setupImage( "C" );
        doImport( getRelatedContentXmlImportData( count, "A", "B", "C" ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );
        assertEquals( 3, content.getMainVersion().getRelatedChildren( true ).size() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key1 + "\"]/@key", dataDoc, key1.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key2 + "\"]/@key", dataDoc, key2.toString() );
        AssertTool.assertSingleXPathValueEquals( "contentdata/relcon/content[@key=\"" + key3 + "\"]/@key", dataDoc, key3.toString() );

        final Collection<ContentEntity> relatedContents = content.getMainVersion().getRelatedChildren( false );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key1 ) );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key2 ) );
        assertTrue( contentKeyExistInContentCollection( relatedContents, key3 ) );
    }

    @Test
    public void testMiscXmlImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForMiscXmlImport() );
        doImport( getMiscXmlImportData( count ) );

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2001.01.02 03:04:00" ), content.getAvailableFrom() );
        assertEquals( new SimpleDateFormat( "yyyy.MM.dd HH:mm:ss" ).parse( "2020.21.22 23:24:00" ), content.getAvailableTo() );
        assertEquals( ContentStatus.APPROVED.getKey(), content.getMainVersion().getStatus().getKey() );
        assertEquals( ContentVersionEntity.STATE_PUBLISHED, content.getMainVersion().getState( new Date() ) );
        assertEquals( "testuser", content.getOwner().getName() );
        assertEquals( "testuser", content.getMainVersion().getModifiedBy().getName() );

        final Document dataDoc = content.getMainVersion().getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "contentdata/name", dataDoc, "ABC.." + SpecialCharacterTestStrings.NORWEGIAN +
                "src/test" +
            SpecialCharacterTestStrings.CHINESE + "src/test" + SpecialCharacterTestStrings.AEC_ALL + "src/test1" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/longHair", dataDoc, "true" );
        AssertTool.assertSingleXPathValueEquals( "contentdata/lastChecked", dataDoc, "2009-03-10" );
        AssertTool.assertSingleXPathValueEquals( "count(/contentdata/keywords/keyword)", dataDoc, "4" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[node() = \"fisk\"]", dataDoc, "fisk" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[node() = \"ost\"]", dataDoc, "ost" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[node() = \"torsk\"]", dataDoc, "torsk" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[node() = \"hyse\"]", dataDoc, "hyse" );
    }

    @Test
    public void testBinaryXmlImport()
        throws Exception
    {
    }


    private String getStringBasedXmlImportData( final long count, final String address )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\">" );
            if ( address != null )
            {
                builder.append( "<address>" ).append( address ).append( i + 1 ).append( "</address>" );
            }
            builder.append( "<persondata>" );
            builder.append( "<age>" ).append( i + 1 ).append( "</age>" );
            builder.append( "<info>" );
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "</info>" );
            builder.append( getHTMLImportEntry( i + 1 ) );
            builder.append( getXMLImportEntry( i + 1 ) );
            builder.append( "</persondata>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getStringBasedXmlImportData_Simple( final long count, final String name, final String address )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name='" ).append( name + ( i + 1 ) ).append( "' title='" ).append( name + ( i + 1 ) ).append( "'>" );
            if ( address != null )
            {
                builder.append( "<address>" ).append( address ).append( "</address>" );
            }
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private StringBuilder getHTMLImportEntry( final int no )
    {
        final String testStr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test" + no;

        StringBuilder builder = new StringBuilder();
        builder.append( "<myhtml>" );
        builder.append( "<p class=\"" + testStr + "\">" );
        builder.append( "<div>" + testStr + "</div>" );
        builder.append( "</p>" );
        builder.append( "</myhtml>" );
        return builder;
    }

    private StringBuilder getXMLImportEntry( final int no )
    {
        final String testStr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test" + no;

        StringBuilder builder = new StringBuilder();
        builder.append( "<myxml>" );
        builder.append( "<root atr=\"" + testStr + "\">" );
        builder.append( "<elem>" + testStr + "</elem>" );
        builder.append( "</root>" );
        builder.append( "</myxml>" );
        return builder;
    }

    private String getStringBasedXmlImportDataWithBlocks( final long count )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\">" );
            builder.append( "<persondata1>" );
            builder.append( "<address>Oslo" ).append( i + 1 ).append( "</address>" );
            builder.append( "</persondata1>" );
            builder.append( "<persondata2>" );
            builder.append( "<age>" ).append( i + 1 ).append( "</age>" );
            builder.append( "<info>" );
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "</info>" );
            builder.append( "</persondata2>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getStringBasedXmlImportDataWithBlocksToBlocks( final long count, final String valuePrefix, final boolean limitBlock )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\">" );
            if ( !limitBlock )
            {
                builder.append( "<keyvaluepair1>" );
                builder.append( "  <key>keyA</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "A</value>" );
                builder.append( "</keyvaluepair1>" );
            }
            builder.append( "<keyvaluepair1>" );
            builder.append( "  <key>keyB</key>" );
            builder.append( "  <value>" ).append( valuePrefix ).append( "B</value>" );
            builder.append( "</keyvaluepair1>" );
            if ( !limitBlock )
            {
                builder.append( "<keyvaluepair1>" );
                builder.append( "  <key>keyC</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "C</value>" );
                builder.append( "</keyvaluepair1>" );
                builder.append( "<keyvaluepair2>" );
                builder.append( "  <key>keyD</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "D</value>" );
                builder.append( "</keyvaluepair2>" );
            }
            builder.append( "<keyvaluepair2>" );
            builder.append( "  <key>keyE</key>" );
            builder.append( "  <value>" ).append( valuePrefix ).append( "E</value>" );
            builder.append( "</keyvaluepair2>" );
            if ( !limitBlock )
            {
                builder.append( "<keyvaluepair2>" );
                builder.append( "  <key>keyF</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "F</value>" );
                builder.append( "</keyvaluepair2>" );
            }
            if ( limitBlock )
            {
                builder.append( "<keyvaluepair2>" );
                builder.append( "  <key>keyG</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "G</value>" );
                builder.append( "</keyvaluepair2>" );
                builder.append( "<keyvaluepair2>" );
                builder.append( "  <key>keyH</key>" );
                builder.append( "  <value>" ).append( valuePrefix ).append( "H</value>" );
                builder.append( "</keyvaluepair2>" );
            }
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getStringBasedXmlImportDataWithNamespaces( long count )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\" />" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getStringBasedXmlImportDataWithPublishFromAndToFromImportData( long count, String publishFrom, String publishTo )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\">" );
            builder.append( "<address>Oslo" ).append( i + 1 ).append( "</address>" );
            builder.append( "<persondata>" );
            builder.append( "<age>" ).append( i + 1 ).append( "</age>" );
            builder.append( "<info>" );
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "</info>" );
            builder.append( "</persondata>" );
            builder.append( "<online " );
            builder.append( "from=\"" ).append( publishFrom ).append( "\" " );
            builder.append( "to=\"" ).append( publishTo ).append( "\"/>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getContentKeyBasedXmlImportData( final long count, final ContentKey imageContentKey, final ContentKey fileContentKey,
                                                    final ContentKey relatedContentKey1, final ContentKey relatedContentKey2 )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\" " );
            builder.append( "pictureKey=\"" ).append( imageContentKey ).append( "\" " );
            builder.append( "attachmentKey=\"" ).append( fileContentKey ).append( "\" " );
            builder.append( "relatedContentKey=\"" ).append( relatedContentKey1 ).append( "\" >" );
            builder.append( "<relConKey>" ).append( relatedContentKey1 ).append( "</relConKey>" );
            builder.append( "<relConKey>" ).append( relatedContentKey2 ).append( "</relConKey>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getRelatedContentXmlImportData( final long count, final String filename1, final String filename2,
                                                   final String filename3 )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\">" );
            builder.append( "<relatedContent name=\"" ).append( filename1 ).append( "\"/>" );
            builder.append( "<relatedContent name=\"" ).append( filename2 ).append( "\"/>" );
            builder.append( "<relatedContent name=\"" ).append( filename3 ).append( "\"/>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private String getMiscXmlImportData( final long count )
        throws Exception
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<fisk><torsk>" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "<entry name=\"ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( "\" " );
            builder.append( "longHair=\"" ).append( i % 2 == 0 ? "true" : "false" ).append( "\" " );
            builder.append( "lastChecked=\"" ).append( "Tue Mar 10 09:04:48 CET 2009" ).append( "\" >" );
            builder.append( "<keyword>" ).append( "fisk" ).append( "</keyword>" );
            builder.append( "<keyword>" ).append( "ost" ).append( "</keyword>" );
            builder.append( "<keyword>" ).append( "torsk" ).append( "</keyword>" );
            builder.append( "<keyword>" ).append( "hyse" ).append( "</keyword>" );
            builder.append( "</entry>" );
        }
        builder.append( "</torsk></fisk>" );
        return builder.toString();
    }

    private XMLBytes getConfigForStringBasedXmlImport()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"address\" type=\"text\">" );
        config.append( "          <display>Address</display>" );
        config.append( "          <xpath>contentdata/address</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"age\" type=\"text\">" );
        config.append( "          <display>Age</display>" );
        config.append( "          <xpath>contentdata/age</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"info\" type=\"textarea\">" );
        config.append( "          <display>Info</display>" );
        config.append( "          <xpath>contentdata/info</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"htmlInfo\" type=\"htmlarea\" mode=\"xhtml\">" );
        config.append( "          <display>HTML</display>" );
        config.append( "          <xpath>contentdata/htmlInfo</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"xmlInfo\" type=\"xml\">" );
        config.append( "          <display>XML</display>" );
        config.append( "          <xpath>contentdata/xmlInfo</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"address\" src=\"address\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"persondata/age\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"persondata/info\"/>" );
        config.append( "        <mapping dest=\"htmlInfo\" src=\"persondata/myhtml\"/>" );
        config.append( "        <mapping dest=\"xmlInfo\" src=\"persondata/myxml\"/>" );
        config.append( "      </import>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport2\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"persondata/age\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"persondata/info\"/>" );
        config.append( "        <mapping dest=\"htmlInfo\" src=\"persondata/myhtml\"/>" );
        config.append( "        <mapping dest=\"xmlInfo\" src=\"persondata/myxml\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedXmlImport_Simple()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"title\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"title\" type=\"text\" required='true'>" );
        config.append( "          <display>Title</display>" );
        config.append( "          <xpath>contentdata/title</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"name\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"address\" type=\"text\">" );
        config.append( "          <display>Address</display>" );
        config.append( "          <xpath>contentdata/address</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"title\" src=\"@title\"/>" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"address\" src=\"address\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedXmlImportWithBlocks()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"address\" type=\"text\">" );
        config.append( "          <display>Address</display>" );
        config.append( "          <xpath>contentdata/address</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"age\" type=\"text\">" );
        config.append( "          <display>Age</display>" );
        config.append( "          <xpath>contentdata/age</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"info\" type=\"textarea\">" );
        config.append( "          <display>Info</display>" );
        config.append( "          <xpath>contentdata/info</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <block base=\"persondata1\">" );
        config.append( "          <mapping dest=\"address\" src=\"address\"/>" );
        config.append( "        </block>" );
        config.append( "        <block base=\"persondata2\">" );
        config.append( "          <mapping dest=\"age\" src=\"age\"/>" );
        config.append( "          <mapping dest=\"info\" src=\"info\"/>" );
        config.append( "        </block>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedXmlImportWithBlocksToBlocks()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "      <block name=\"keyvaluepair1\" group=\"contentdata/kv1\">" );
        config.append( "        <input name=\"key1\" type=\"text\">" );
        config.append( "          <display>Key</display>" );
        config.append( "          <xpath>key</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"value1\" type=\"text\">" );
        config.append( "          <display>Value</display>" );
        config.append( "          <xpath>value</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "      <block name=\"keyvaluepair2\" group=\"contentdata/kv2\">" );
        config.append( "        <input name=\"key2\" type=\"text\">" );
        config.append( "          <display>Key</display>" );
        config.append( "          <xpath>key</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"value2\" type=\"text\">" );
        config.append( "          <display>Value</display>" );
        config.append( "          <xpath>value</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <block base=\"keyvaluepair1\" dest=\"keyvaluepair1\" sync=\"key1\">" );
        config.append( "          <mapping dest=\"key1\" src=\"key\"/>" );
        config.append( "          <mapping dest=\"value1\" src=\"value\"/>" );
        config.append( "        </block>" );
        config.append( "        <block base=\"keyvaluepair2\" dest=\"keyvaluepair2\" sync=\"key2\">" );
        config.append( "          <mapping dest=\"key2\" src=\"key\"/>" );
        config.append( "          <mapping dest=\"value2\" src=\"value\"/>" );
        config.append( "        </block>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedXmlImportWithNamespaces()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"max\" type=\"text\">" );
        config.append( "          <display>Max</display>" );
        config.append( "          <xpath>contentdata/max</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"min\" type=\"text\">" );
        config.append( "          <display>Min</display>" );
        config.append( "          <xpath>contentdata/min</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"timestamp\" type=\"text\">" );
        config.append( "          <display>Imprted timestamp</display>" );
        config.append( "          <xpath>contentdata/timestamp</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"year\" type=\"text\">" );
        config.append( "          <display>Year</display>" );
        config.append( "          <xpath>contentdata/year</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\"" );
        config.append( "              xmlns:math=\"java:java.lang.Math\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"max\" src=\"math:max(123, 789)\"/>" );
        config.append( "        <mapping dest=\"min\" src=\"math:min(123, 789)\"/>" );
        config.append( "        <mapping dest=\"timestamp\" src=\"current-dateTime()\"/>" );
        config.append( "        <mapping dest=\"year\" src=\"year-from-date(current-date())\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedXmlImportWithPublishFromAndToFromImportData()
    {
        StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"address\" type=\"text\">" );
        config.append( "          <display>Address</display>" );
        config.append( "          <xpath>contentdata/address</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"age\" type=\"text\">" );
        config.append( "          <display>Age</display>" );
        config.append( "          <xpath>contentdata/age</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"info\" type=\"textarea\">" );
        config.append( "          <display>Info</display>" );
        config.append( "          <xpath>contentdata/info</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"address\" src=\"address\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"persondata/age\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"persondata/info\"/>" );
        config.append( "        <mapping dest=\"@publishfrom\" src=\"online/@from\" format=\"yyyy.MM.dd HH:mm:ss\"/>" );
        config.append( "        <mapping dest=\"@publishto\" src=\"online/@to\" format=\"yyyy.MM.dd HH:mm:ss\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForContentKeyBasedXmlImport()
    {
        StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"picture\" type=\"image\">" );
        config.append( "          <display>Picture</display>" );
        config.append( "          <xpath>contentdata/picture</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"attachment\" type=\"file\">" );
        config.append( "          <display>Attachment</display>" );
        config.append( "          <xpath>contentdata/attachment</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"relatedContent\" type=\"relatedcontent\" multiple=\"false\" >" );
        config.append( "          <display>RelatedContent</display>" );
        config.append( "          <xpath>contentdata/relcon</xpath>" );
        config.append( "          <contenttype name=\"MyImageContentType\"/>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"multipleRelatedContent\" type=\"relatedcontent\" multiple=\"true\" >" );
        config.append( "          <display>MultipleRelatedContent</display>" );
        config.append( "          <xpath>contentdata/multirelcon</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"picture\" src=\"@pictureKey\"/>" );
        config.append( "        <mapping dest=\"attachment\" src=\"@attachmentKey\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"@relatedContentKey\"/>" );
        config.append( "        <mapping dest=\"multipleRelatedContent\" src=\"relConKey\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForCustomRelatedContentXmlImport()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"relatedContent\" type=\"relatedcontent\" multiple=\"true\" >" );
        config.append( "          <display>Images</display>" );
        config.append( "          <xpath>contentdata/relcon</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"relatedContent/@name\"" );
        config.append( "                 relatedcontenttype=\"MyRelatedContentType\" relatedfield=\"name\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForImageRelatedContentXmlImport()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"relatedContent\" type=\"relatedcontent\" multiple=\"true\" >" );
        config.append( "          <display>Images</display>" );
        config.append( "          <xpath>contentdata/relcon</xpath>" );
        config.append( "        </input>" );
        config.append( "      </block>" );
        config.append( "    </form>" );
        config.append( "    <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"relatedContent/@name\"" );
        config.append( "                 relatedcontenttype=\"MyImageContentType\" relatedfield=\"name\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForMiscXmlImport()
    {
        final StringBuffer config = new StringBuffer();
        config.append( "<contenttype>" );
        config.append( "  <config name=\"MyContentType\" version=\"1.0\">" );
        config.append( "    <form>" );
        config.append( "      <title name=\"name\"/>" );
        config.append( "      <block name=\"info\">" );
        config.append( "        <input name=\"name\" required=\"true\" type=\"text\">" );
        config.append( "          <display>Name</display>" );
        config.append( "          <xpath>contentdata/name</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"longHair\" type=\"checkbox\">" );
        config.append( "          <display>Long Hair</display>" );
        config.append( "          <xpath>contentdata/longHair</xpath>" );
        config.append( "        </input>" );
        config.append( "        <input name=\"lastChecked\" type=\"date\">" );
        config.append( "          <display>Last Checked</display>" );
        config.append( "          <xpath>contentdata/lastChecked</xpath>" );
        config.append( "         </input>" );
        config.append( "        <input name=\"keywords\" type=\"keywords\">" );
        config.append( "          <display>Keywords</display>" );
        config.append( "          <xpath>contentdata/keywords</xpath>" );
        config.append( "         </input>" );
        config.append( "      </block>" );
        config.append( "   </form>" );
        config.append( "   <imports>" );
        config.append( "      <import mode=\"xml\" name=\"MyImport\" purge=\"archive\"" );
        config.append( "              base=\"/fisk/torsk/entry\" status=\"2\" sync=\"name\">" );
        config.append( "       <mapping dest=\"name\" src=\"@name\"/>" );
        config.append( "       <mapping dest=\"longHair\" src=\"@longHair\"/>" );
        config.append( "       <mapping dest=\"lastChecked\" src=\"@lastChecked\" format=\"EEE MMM d HH:mm:ss z yyyy\"/>" );
        config.append( "       <mapping dest=\"keywords\" src=\"keyword\" />" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }
}