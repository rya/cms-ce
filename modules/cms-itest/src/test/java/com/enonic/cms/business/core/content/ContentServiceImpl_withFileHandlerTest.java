package com.enonic.cms.business.core.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
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

import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.business.core.content.command.CreateContentCommand;
import com.enonic.cms.business.core.content.command.UpdateContentCommand;

import com.enonic.cms.domain.content.ContentAndVersion;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.BinaryDataKey;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.domain.content.contentdata.legacy.support.FileContentDataParser;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_withFileHandlerTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentVersionDao contentVersionDao;

    private DomainFactory factory;

    private DomainFixture fixture;

    private byte[] dummyBinary = new byte[]{1, 2, 3};

    @Before
    public void before()
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        hibernateTemplate.save( factory.createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        hibernateTemplate.save( factory.createContentType( "MyContentType", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        hibernateTemplate.save( factory.createUnit( "MyUnit" ) );
        hibernateTemplate.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        hibernateTemplate.save( factory.createCategoryAccess( "MyCategory", fixture.findUserByName( "testuser" ), "read, create" ) );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

    }


    @Test
    public void testStoreNewContent()
    {
        CreateContentCommand createCommand = doCreate_CreateContentCommand( "test file", "test description", dummyBinary, "dummyBinary.dat",
                                                                            new String[]{"keyword1", "keyword2"} );
        ContentKey contenKey = contentService.createContent( createCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity actualContent = contentDao.findByKey( contenKey );
        assertNotNull( actualContent );

        ContentVersionEntity persistedVersion = actualContent.getMainVersion();

        Set<ContentBinaryDataEntity> contentBinaryDatas = persistedVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );

        BinaryDataEntity binaryDataResolvedFromContentBinaryData = contentBinaryDatas.iterator().next().getBinaryData();
        assertEquals( "dummyBinary.dat", binaryDataResolvedFromContentBinaryData.getName() );
        assertNotNull( "binaryDatakey", binaryDataResolvedFromContentBinaryData.getBinaryDataKey() );
        assertEquals( 3, binaryDataResolvedFromContentBinaryData.getSize() );
        assertNotNull( "timestamp", binaryDataResolvedFromContentBinaryData.getCreatedAt() );

        LegacyFileContentData contentData = (LegacyFileContentData) persistedVersion.getContentData();
        assertNotNull( contentData );

        Document contentDataXml = contentData.getContentDataXml();
        AssertTool.assertXPathEquals( "/contentdata/name", contentDataXml, "test file" );
        AssertTool.assertXPathEquals( "/contentdata/description", contentDataXml, "test description" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "keyword1" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "keyword2" );
        AssertTool.assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( dummyBinary.length ) );
        AssertTool.assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
                                      binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
    }

    @Test
    public void testUpdateCurrentVersion()
    {
        CreateContentCommand createCommand = doCreate_CreateContentCommand( "test file", "test description", dummyBinary, "dummyBinary.dat",
                                                                            new String[]{"keyword1", "keyword2"} );
        ContentKey contentKey = contentService.createContent( createCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        byte[] changedDummyBytes = new byte[]{1, 2, 3, 4, 5, 6};
        UpdateContentCommand updateCommand = UpdateContentCommand.updateExistingVersion2( persistedContent.getMainVersion().getKey() );
        doCreate_UpdateContentCommand( updateCommand, contentKey, "changed file", "changed description", changedDummyBytes.length,
                                       new String[]{"changed1", "changed2"} );
        updateCommand.setUpdateAsMainVersion( false );

        ContentVersionEntity persistedCersion = persistedContent.getMainVersion();
        BinaryDataKey persistedFileBinaryDataKey =
            persistedCersion.getContentBinaryData().iterator().next().getBinaryData().getBinaryDataKey();

        List<BinaryDataAndBinary> binaryDataToAdd = new ArrayList<BinaryDataAndBinary>();
        binaryDataToAdd.add( factory.createBinaryDataAndBinary( "changedBinary.dat", changedDummyBytes ) );
        updateCommand.setBinaryDataToAdd( binaryDataToAdd );
        updateCommand.setUseCommandsBinaryDataToAdd( true );
        List<BinaryDataKey> binaryDataToRemove = new ArrayList<BinaryDataKey>();
        binaryDataToRemove.add( persistedFileBinaryDataKey );
        updateCommand.setBinaryDataToRemove( binaryDataToRemove );
        updateCommand.setUseCommandsBinaryDataToRemove( true );

        UpdateContentResult updateContentResult = contentService.updateContent( updateCommand );
        ContentVersionKey versionKey = updateContentResult.getTargetedVersionKey();
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        // assertNull( "expected previous binary to not exist any more", binaryDao.findByKey( persistedFileBinaryDataKey ) );

        ContentEntity actualContent = contentDao.findByKey( contentKey );
        assertNotNull( actualContent );
        assertEquals( "expected only one version", 1, actualContent.getVersions().size() );

        ContentVersionEntity actualVersion = contentVersionDao.findByKey( versionKey );
        assertNotNull( actualVersion );

        Set<ContentBinaryDataEntity> contentBinaryDatas = actualVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );

        BinaryDataEntity binaryDataResolvedFromContentBinaryData = contentBinaryDatas.iterator().next().getBinaryData();
        assertEquals( "changedBinary.dat", binaryDataResolvedFromContentBinaryData.getName() );
        assertNotNull( "binaryDatakey", binaryDataResolvedFromContentBinaryData.getBinaryDataKey() );
        assertEquals( 6, binaryDataResolvedFromContentBinaryData.getSize() );
        assertNotNull( "timestamp", binaryDataResolvedFromContentBinaryData.getCreatedAt() );

        LegacyFileContentData contentData = (LegacyFileContentData) actualVersion.getContentData();
        assertNotNull( contentData );

        Document contentDataXml = contentData.getContentDataXml();
        AssertTool.assertXPathEquals( "/contentdata/name", contentDataXml, "changed file" );
        AssertTool.assertXPathEquals( "/contentdata/description", contentDataXml, "changed description" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "changed1" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "changed2" );
        AssertTool.assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( changedDummyBytes.length ) );
        AssertTool.assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
                                      binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
    }

    @Test
    public void testUpdateAsNewVersionButNotMainVersion()
    {
        CreateContentCommand createCommand = doCreate_CreateContentCommand( "test file", "test description", dummyBinary, "dummyBinary.dat",
                                                                            new String[]{"keyword1", "keyword2"} );
        ContentKey contentKey = contentService.createContent( createCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        byte[] changedDummyBytes = new byte[]{1, 2, 3, 4, 5, 6};
        UpdateContentCommand updateCommand =
            UpdateContentCommand.storeNewVersionEvenIfUnchanged( persistedContent.getMainVersion().getKey() );
        doCreate_UpdateContentCommand( updateCommand, contentKey, "changed file", "changed description", changedDummyBytes.length,
                                       new String[]{"changed1", "changed2"} );
        updateCommand.setUpdateAsMainVersion( false );

        ContentVersionEntity persistedCersion = persistedContent.getMainVersion();
        BinaryDataEntity persistedFileBinaryData = persistedCersion.getContentBinaryData().iterator().next().getBinaryData();

        List<BinaryDataAndBinary> binaryDataToAdd = new ArrayList<BinaryDataAndBinary>();
        binaryDataToAdd.add( factory.createBinaryDataAndBinary( "changedBinary.dat", changedDummyBytes ) );
        updateCommand.setBinaryDataToAdd( binaryDataToAdd );
        updateCommand.setUseCommandsBinaryDataToAdd( true );
        List<BinaryDataKey> binaryDataToRemove = new ArrayList<BinaryDataKey>();
        binaryDataToRemove.add( persistedFileBinaryData.getBinaryDataKey() );
        updateCommand.setBinaryDataToRemove( binaryDataToRemove );
        updateCommand.setUseCommandsBinaryDataToRemove( true );

        UpdateContentResult updateContentResult = contentService.updateContent( updateCommand );
        ContentVersionKey versionKey = updateContentResult.getTargetedVersionKey();
        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity actualContent = contentDao.findByKey( contentKey );
        assertNotNull( actualContent );
        assertEquals( "expected two versions", 2, actualContent.getVersions().size() );

        ContentVersionEntity actualVersion = contentVersionDao.findByKey( versionKey );
        assertNotNull( actualVersion );

        Set<ContentBinaryDataEntity> contentBinaryDatas = actualVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );

        BinaryDataEntity binaryDataResolvedFromContentBinaryData = contentBinaryDatas.iterator().next().getBinaryData();
        assertEquals( "changedBinary.dat", binaryDataResolvedFromContentBinaryData.getName() );
        assertNotNull( "binaryDatakey", binaryDataResolvedFromContentBinaryData.getBinaryDataKey() );
        assertEquals( 6, binaryDataResolvedFromContentBinaryData.getSize() );
        assertNotNull( "timestamp", binaryDataResolvedFromContentBinaryData.getCreatedAt() );

        LegacyFileContentData contentData = (LegacyFileContentData) actualVersion.getContentData();
        assertNotNull( contentData );

        Document contentDataXml = contentData.getContentDataXml();
        AssertTool.assertXPathEquals( "/contentdata/name", contentDataXml, "changed file" );
        AssertTool.assertXPathEquals( "/contentdata/description", contentDataXml, "changed description" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "changed1" );
        AssertTool.assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "changed2" );
        AssertTool.assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( changedDummyBytes.length ) );
        AssertTool.assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
                                      binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
    }

    private void doCreate_UpdateContentCommand( UpdateContentCommand command, ContentKey contentKey, String name, String description,
                                                int fileBytesLength, String[] keywords )
    {
        ContentEntity content = factory.createContent( "MyCategory", "en", "testuser", "0", new Date() );
        content.setKey( contentKey );
        ContentVersionEntity version = factory.createContentVersion( "0", "testuser" );

        Document fileContentDataDoc = createFileContentDataDoc( name, description, keywords, String.valueOf( fileBytesLength ) );

        version.setContentData( FileContentDataParser.parse( fileContentDataDoc, null ) );

        ContentAndVersion contentAndVersion = new ContentAndVersion( content, version );
        command.setModifier( fixture.findUserByName( "testuser" ) );

        // Populate command with contentEntity data
        command.populateContentValuesFromContent( content );
        command.populateContentVersionValuesFromContentVersion( contentAndVersion.getVersion() );
    }

    private CreateContentCommand doCreate_CreateContentCommand( String name, String description, byte[] fileBytes, String filename,
                                                                String[] keywords )
    {
        ContentEntity content = factory.createContent( "MyCategory", "en", "testuser", "0", new Date() );
        ContentVersionEntity version = factory.createContentVersion( "0", "testuser" );

        Document fileContentDataDoc = createFileContentDataDoc( name, description, keywords, String.valueOf( fileBytes.length ) );
        version.setContentData( FileContentDataParser.parse( fileContentDataDoc, null ) );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        binaryDatas.add( factory.createBinaryDataAndBinary( filename, fileBytes ) );

        CreateContentCommand command = new CreateContentCommand();
        command.setCreator( fixture.findUserByName( "testuser" ) );

        command.populateCommandWithContentValues( content );
        command.populateCommandWithContentVersionValues( version );

        command.setBinaryDatas( binaryDatas );
        command.setUseCommandsBinaryDataToAdd( true );

        return command;
    }


    private Document createFileContentDataDoc( String name, String description, String[] keywords, String filesize )
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "name" ).setText( name ) );
        contentdataEl.addContent( new Element( "description" ).setText( description ) );

        Element keywordsEl = new Element( "keywords" );
        for ( String keyword : keywords )
        {
            keywordsEl.addContent( new Element( "keyword" ).setText( keyword ) );
        }
        contentdataEl.addContent( keywordsEl );
        if ( filesize != null )
        {
            contentdataEl.addContent( new Element( "filesize" ).setText( filesize ) );
        }
        contentdataEl.addContent( new Element( "binarydata" ).setAttribute( "key", "%0" ) );

        return new Document( contentdataEl );
    }

}