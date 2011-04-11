/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.core.content.ContentService;

import com.enonic.cms.domain.content.ContentAccessEntity;
import com.enonic.cms.domain.content.ContentAccessType;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfig;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.BinaryDataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImplTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected ContentService contentService;

    private Element standardConfigEl;

    private XMLBytes standardConfig;

    private byte[] dummyBinary = new byte[]{1, 2, 3};

    @Before
    public void before()
        throws IOException, JDOMException
    {

        groupEntityDao.invalidateCachedKeys();

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        // setup needed common data for each test
        fixture.initSystemData();

        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );

        standardConfigXml.append( "         <title name=\"myTitle\"/>" );

        standardConfigXml.append( "         <block name=\"TestBlock1\">" );

        standardConfigXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myTitleInSubElement\" required=\"false\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title in sub element</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/subelement/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() ).getAsBytes();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

    }

    @Test
    public void testCreateContentWithBinary()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );
        fixture.flushAndClearHibernateSesssion();

        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );
        content.setName( "testcontet" );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setModifiedBy( fixture.findUserByName( "testuser" ) );
        version.setStatus( ContentStatus.DRAFT );
        version.setContent( content );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getInputConfig( "myTitle" );
        BinaryDataEntryConfig binaryConfig = (BinaryDataEntryConfig) contentData.getInputConfig( "myBinaryfile" );
        contentData.add( new TextDataEntry( titleConfig, "title" ) );
        contentData.add( new BinaryDataEntry( binaryConfig, "%0" ) );

        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser.getKey() );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        binaryDatas.add( factory.createBinaryDataAndBinary( "dummyBinary", dummyBinary ) );
        createContentCommand.setBinaryDatas( binaryDatas );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contenKey = contentService.createContent( createContentCommand );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contenKey );
        assertNotNull( persistedContent );
        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();

        // verify content binary data
        Set<ContentBinaryDataEntity> contentBinaryDatas = persistedVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );
        ContentBinaryDataEntity contentBinaryData = contentBinaryDatas.iterator().next();
        assertNull( contentBinaryData.getLabel() );

        // verify binary data
        BinaryDataEntity binaryData = contentBinaryData.getBinaryData();
        assertEquals( "dummyBinary", binaryData.getName() );

        // verify binary
        // BinaryEntity binary = binaryDao.findByKey( binaryData.getBinaryDataKey() );
        // assertArrayEquals( dummyBinary, binary.getData() );

        CustomContentData peristedContentData = (CustomContentData) persistedVersion.getContentData();

        // verify binary data entry in content data
        List<BinaryDataEntry> binaryDataEntryList = peristedContentData.getBinaryDataEntryList();
        assertEquals( 1, binaryDataEntryList.size() );
        BinaryDataEntry binaryDataEntry = binaryDataEntryList.get( 0 );
        assertEquals( binaryData.getBinaryDataKey().toInt(), binaryDataEntry.getExistingBinaryKey().intValue() );
    }


    @Test
    public void testCreateContent_TitleIsSaved()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );
        content.setName( "testcontent" );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setModifiedBy( fixture.findUserByName( "testuser" ) );
        version.setStatus( ContentStatus.DRAFT );
        version.setContent( content );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getInputConfig( "myTitle" );
        TextDataEntryConfig subElementConfig = (TextDataEntryConfig) contentData.getInputConfig( "myTitleInSubElement" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );
        contentData.add( new TextDataEntry( subElementConfig, "test subtitle" ) );

        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser.getKey() );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();

        createContentCommand.setBinaryDatas( binaryDatas );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contenKey = contentService.createContent( createContentCommand );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contenKey );
        assertNotNull( persistedContent );

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        assertEquals( "test title", persistedVersion.getTitle() );

        Document contentDataXml = persistedVersion.getContentDataAsJDomDocument();
        AssertTool.assertSingleXPathValueEquals( "/contentdata/mytitle", contentDataXml, "test title" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/subelement/mytitle", contentDataXml, "test subtitle" );

        CustomContentData peristedContentData = (CustomContentData) persistedVersion.getContentData();
        assertEquals( "test title", peristedContentData.getTitle() );
    }

    @Test
    public void testCreateContent_AccessRightsIsSaved()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );
        fixture.flushAndClearHibernateSesssion();

        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );
        content.setName( "testcontent" );
        ContentAccessEntity coa1 = new ContentAccessEntity();
        coa1.setGroup( fixture.findGroupByKey( "ABCGROUP" ) );
        coa1.setReadAccess( true );
        coa1.setUpdateAccess( true );
        coa1.setDeleteAccess( false );
        content.addContentAccessRight( coa1 );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setModifiedBy( fixture.findUserByName( "testuser" ) );
        version.setStatus( ContentStatus.DRAFT );
        version.setContent( content );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getInputConfig( "myTitle" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );
        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser.getKey() );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        createContentCommand.setBinaryDatas( binaryDatas );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contenKey = contentService.createContent( createContentCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contenKey );
        assertNotNull( persistedContent );

        assertTrue( persistedContent.hasAccessRightSet( fixture.findGroupByKey( "ABCGROUP" ), ContentAccessType.READ ) );
        assertTrue( persistedContent.hasAccessRightSet( fixture.findGroupByKey( "ABCGROUP" ), ContentAccessType.UPDATE ) );
        assertFalse( persistedContent.hasAccessRightSet( fixture.findGroupByKey( "ABCGROUP" ), ContentAccessType.DELETE ) );
    }


}
