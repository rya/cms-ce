/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
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

import com.enonic.cms.api.client.model.ImportContentsParams;
import com.enonic.cms.core.security.SecurityHolder;
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
import com.enonic.cms.core.security.user.UserEntity;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class InternalClientImpl_ImportContentCsvTest
    extends AbstractInternalClientImpl_ImportContentTest
{

    @Test
    public void testStringBasedCSVImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedCSVImport() );
        doImport( getStringBasedCSVImportData( count, "Oslo" ) );

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
        AssertTool.assertSingleXPathValueEquals( "/myxml/@atr", xmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/myxml/elem", xmlDoc, testElem );
        AssertTool.assertSingleXPathValueEquals( "/myhtml/@atr", htmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/myhtml/elem", htmlDoc, testElem );
    }


    @Test
    public void testStringBasedCSVImport_with_assignment()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedCSVImport_Draft() );

        String data = getStringBasedCSVImportData( count, "Oslo" );

        final UserEntity testUser = fixture.findUserByName( "testuser" );
        final UserEntity runningUser = testUser;
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        final ImportContentsParams importParams = new ImportContentsParams();
        importParams.categoryKey = fixture.findCategoryByName( "MyImportCategory" ).getKey().toInt();
        importParams.importName = "MyImport";
        importParams.data = data;
        importParams.assignee = testUser.getUserStore().getName() + ":" + testUser.getName();
        importParams.assignmentDescription = "import-test";
        importParams.assignmentDueDate = Calendar.getInstance().getTime();

        internalClient.importContents( importParams );

        hibernateTemplate.clear();

        final List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        final ContentEntity content = contentDao.findByKey( contentKeys.get( 0 ) );

        assertEquals( ContentStatus.DRAFT.getKey(), content.getMainVersion().getStatus().getKey() );
        assertNotNull( content.getAssignee() );
        assertNotNull( content.getAssigner() );
        assertNotNull( content.getAssignmentDescription() );
        assertNotNull( content.getAssignmentDueDate() );
    }


    @Test
    public void testStringBasedCSVImportVersionCountCheck()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedCSVImport() );
        doImport( getStringBasedCSVImportData( count, "Oslo" ), "testuser", "MyImport" );
        doImport( getStringBasedCSVImportData( count, null ), "testuser2", "MyImport2" );

        List<ContentKey> contentKeys = contentDao.findContentKeysByCategory( fixture.findCategoryByName( "MyImportCategory" ) );
        assertEquals( count, contentKeys.size() );
        for ( int i = 0; i < count; i++ )
        {
            final ContentEntity content = contentDao.findByKey( contentKeys.get( i ) );
            assertEquals( 1, content.getVersionCount() );
            assertEquals( "testuser", content.getOwner().getName() );
        }

        doImport( getStringBasedCSVImportData( count, "Majorstua" ), "testuser", "MyImport" );

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
    public void testStringBasedCSVImportWithPublishFromAndToFromImportData()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedCSVImportWithPublishFromAndToFromImportData() );
        doImport( getStringBasedCSVImportDataWithPublishFromAndToFromImportData( count, "2001.01.01 01:01:01", "2020.20.20 20:20:20" ) );

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
    public void testStringBasedCSVImportWithoutPublishFromAndTo()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForStringBasedCSVImport() );
        doImport( getStringBasedCSVImportData( count, "Oslo" ), "testuser", "MyImport", null, null );

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
        AssertTool.assertSingleXPathValueEquals( "/myxml/@atr", xmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/myxml/elem", xmlDoc, testElem );
        AssertTool.assertSingleXPathValueEquals( "/myhtml/@atr", htmlDoc, testAtr );
        AssertTool.assertSingleXPathValueEquals( "/myhtml/elem", htmlDoc, testElem );
    }

    @Test
    public void testContentKeyBasedCSVImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForContentKeyBasedCSVImport() );
        setupImageCategory();
        final ContentKey key1 = setupImage();
        final ContentKey key2 = setupImage();
        doImport( getContentKeyBasedCSVImportData( count, key1, key1, key1, key2 ) );

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
    public void testCustomRelatedContentCSVImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForCustomRelatedContentCSVImport() );
        setupRelatedContentCategory();
        final ContentKey key1 = setupRelatedContent( "A" );
        final ContentKey key2 = setupRelatedContent( "B" );
        final ContentKey key3 = setupRelatedContent( "C" );
        doImport( getRelatedContentCSVImportData( count, "A", "B", "C" ) );

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
    public void testImageRelatedContentCSVImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForImageRelatedContentCSVImport() );
        setupImageCategory();
        final ContentKey key1 = setupImage( "A" );
        final ContentKey key2 = setupImage( "B" );
        final ContentKey key3 = setupImage( "C" );
        doImport( getRelatedContentCSVImportData( count, "A", "B", "C" ) );

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
    public void testMiscCSVImport()
        throws Exception
    {
        final long count = 10L;
        setupImport( getConfigForMiscCSVImport() );
        doImport( getMiscCSVImportData( count ) );

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

    private String getStringBasedCSVImportData( final long count, final String address )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "name;address;age\r\n" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            if ( address != null )
            {
                builder.append( address ).append( i + 1 ).append( ";" );
            }
            builder.append( i + 1 ).append( ";" );
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( getHTMLImportEntry( i + 1 ) ).append( ";" );
            builder.append( getXMLImportEntry( i + 1 ) ).append( ";" );
            builder.append( "\r\n" );
        }
        return builder.toString();
    }

    private StringBuilder getHTMLImportEntry( final int no )
    {
        final String testStr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test" + no;

        StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<myhtml atr=\"" + testStr + "\">" );
        builder.append( "<elem>" + testStr + "</elem>" );
        builder.append( "</myhtml>" );
        return builder;
    }

    private StringBuilder getXMLImportEntry( final int no )
    {
        final String testStr = "ABC.." + SpecialCharacterTestStrings.NORWEGIAN + "src/test" + SpecialCharacterTestStrings.CHINESE +
                "src/test" +
            SpecialCharacterTestStrings.AEC_ALL + "src/test" + no;

        StringBuilder builder = new StringBuilder();
        builder.append( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        builder.append( "<myxml atr=\"" + testStr + "\">" );
        builder.append( "<elem>" + testStr + "</elem>" );
        builder.append( "</myxml>" );
        return builder;
    }

    private String getStringBasedCSVImportDataWithPublishFromAndToFromImportData( final long count, final String publishFrom,
                                                                                  final String publishTo )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "name;address;age;from;to\r\n" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( "Oslo" ).append( i + 1 ).append( ";" );
            builder.append( i + 1 ).append( ";" );
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( publishFrom ).append( ";" );
            builder.append( publishTo ).append( "\r\n" );
        }
        return builder.toString();
    }

    private String getContentKeyBasedCSVImportData( final long count, final ContentKey imageContentKey, final ContentKey fileContentKey,
                                                    final ContentKey relatedContentKey1, final ContentKey relatedContentKey2 )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "name;picture;attachment;relCon\r\n" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( imageContentKey ).append( ";" );
            builder.append( fileContentKey ).append( ";" );
            builder.append( relatedContentKey1 ).append( ";" );
            builder.append( relatedContentKey1 ).append( "-" );
            builder.append( relatedContentKey2 ).append( "\r\n" );
        }
        return builder.toString();
    }

    private String getRelatedContentCSVImportData( final long count, final String filename1, final String filename2,
                                                   final String filename3 )
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "name;relCon\r\n" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( filename1 ).append( "-" );
            builder.append( filename2 ).append( "-" );
            builder.append( filename3 ).append( "\r\n" );
        }
        return builder.toString();
    }

    private String getMiscCSVImportData( final long count )
        throws Exception
    {
        final String dummyBinaryString = new String( new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0}, "UTF-8" );
        final StringBuilder builder = new StringBuilder();
        builder.append( "name;longHair;lastChecked;binary\r\n" );
        for ( int i = 0; i < count; i++ )
        {
            builder.append( "ABC.." ).append( SpecialCharacterTestStrings.NORWEGIAN ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.CHINESE ).append( "src/test" );
            builder.append( SpecialCharacterTestStrings.AEC_ALL ).append( "src/test" );
            builder.append( i + 1 ).append( ";" );
            builder.append( i % 2 == 0 ? "true" : "false" ).append( ";" );
            builder.append( "Tue Mar 10 09:04:48 CET 2009" ).append( ";" );
            builder.append( "fisk-ost-torsk-hyse" ).append( ";" );
            builder.append( dummyBinaryString ).append( "\r\n" );
        }
        return builder.toString();
    }

    private XMLBytes getConfigForStringBasedCSVImport()
    {
        return doGetConfigForStringBasedCSVImport( 2 );
    }

    private XMLBytes getConfigForStringBasedCSVImport_Draft()
    {
        return doGetConfigForStringBasedCSVImport( 0 );
    }

    private XMLBytes doGetConfigForStringBasedCSVImport( int status )
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
        config.append( "      <import mode=\"csv\" name=\"MyImport\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"" + status + "\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"address\" src=\"2\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"3\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"4\"/>" );
        config.append( "        <mapping dest=\"htmlInfo\" src=\"5\"/>" );
        config.append( "        <mapping dest=\"xmlInfo\" src=\"6\"/>" );
        config.append( "      </import>" );
        config.append( "      <import mode=\"csv\" name=\"MyImport2\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"" + status + "\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"2\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"3\"/>" );
        config.append( "        <mapping dest=\"htmlInfo\" src=\"4\"/>" );
        config.append( "        <mapping dest=\"xmlInfo\" src=\"5\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForStringBasedCSVImportWithPublishFromAndToFromImportData()
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
        config.append( "      <import mode=\"csv\" name=\"MyImport\" purge=\"archive\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"address\" src=\"2\"/>" );
        config.append( "        <mapping dest=\"age\" src=\"3\"/>" );
        config.append( "        <mapping dest=\"info\" src=\"4\"/>" );
        config.append( "        <mapping dest=\"@publishfrom\" src=\"5\" format=\"yyyy.MM.dd HH:mm:ss\"/>" );
        config.append( "        <mapping dest=\"@publishto\" src=\"6\" format=\"yyyy.MM.dd HH:mm:ss\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForContentKeyBasedCSVImport()
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
        config.append( "      <import mode=\"csv\" name=\"MyImport\" purge=\"archive\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"picture\" src=\"2\"/>" );
        config.append( "        <mapping dest=\"attachment\" src=\"3\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"4\"/>" );
        config.append( "        <mapping dest=\"multipleRelatedContent\" src=\"5\" separator=\"-\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForCustomRelatedContentCSVImport()
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
        config.append( "      <import mode=\"csv\" name=\"MyImport\" purge=\"archive\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"2\"" );
        config.append( "                 separator=\"-\" relatedcontenttype=\"MyRelatedContentType\" relatedfield=\"name\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForImageRelatedContentCSVImport()
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
        config.append( "      <import mode=\"csv\" name=\"MyImport\" purge=\"archive\" separator=\";\" " );
        config.append( "              skip=\"1\" status=\"2\" sync=\"name\">" );
        config.append( "        <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "        <mapping dest=\"relatedContent\" src=\"2\"" );
        config.append( "                 separator=\"-\" relatedcontenttype=\"MyImageContentType\" relatedfield=\"name\"/>" );
        config.append( "      </import>" );
        config.append( "    </imports>" );
        config.append( "  </config>" );
        config.append( "  <indexparameters>" );
        config.append( "    <index xpath=\"contentdata/name\"/>" );
        config.append( "  </indexparameters>" );
        config.append( "</contenttype>" );
        return XMLDocumentFactory.create( config.toString() ).getAsBytes();
    }

    private XMLBytes getConfigForMiscCSVImport()
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
        config.append( "     <import mode=\"csv\" name=\"MyImport\" purge=\"archive\" separator=\";\" " );
        config.append( "             skip=\"1\" status=\"2\" sync=\"name\">" );
        config.append( "       <mapping dest=\"name\" src=\"1\"/>" );
        config.append( "       <mapping dest=\"longHair\" src=\"2\"/>" );
        config.append( "       <mapping dest=\"lastChecked\" src=\"3\" format=\"EEE MMM d HH:mm:ss z yyyy\"/>" );
        config.append( "       <mapping dest=\"keywords\" src=\"4\" separator=\"-\"/>" );
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