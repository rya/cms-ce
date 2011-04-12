/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.BinaryDataKey;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.servlet.ServletRequestAccessor;

import com.enonic.cms.core.business.AbstractPersistContentTest;

import com.enonic.cms.core.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.core.content.contentdata.legacy.support.FileContentDataParser;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_withFileHandlerTest
    extends AbstractPersistContentTest
{
    private byte[] dummyBinary = new byte[]{1, 2, 3};

    @Before
    public void before()
    {
        initSystemData();

        createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        hibernateTemplate.save( createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        hibernateTemplate.save( createContentType( "MyContentType", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        hibernateTemplate.save( createUnit( "MyUnit" ) );
        hibernateTemplate.save( createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        hibernateTemplate.save(
            createCategoryAccess( "MyCategory", findUserByName( "testuser" ).getUserGroup().getName(), "true", "true", "true", "true",
                                  "true" ) );
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
        assertXPathEquals( "/contentdata/name", contentDataXml, "test file" );
        assertXPathEquals( "/contentdata/description", contentDataXml, "test description" );
        assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "keyword1" );
        assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "keyword2" );
        assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( dummyBinary.length ) );
        assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
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
        binaryDataToAdd.add( createBinaryDataAndBinary( "changedBinary.dat", changedDummyBytes ) );
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
        assertXPathEquals( "/contentdata/name", contentDataXml, "changed file" );
        assertXPathEquals( "/contentdata/description", contentDataXml, "changed description" );
        assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "changed1" );
        assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "changed2" );
        assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( changedDummyBytes.length ) );
        assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
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
        binaryDataToAdd.add( createBinaryDataAndBinary( "changedBinary.dat", changedDummyBytes ) );
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
        assertXPathEquals( "/contentdata/name", contentDataXml, "changed file" );
        assertXPathEquals( "/contentdata/description", contentDataXml, "changed description" );
        assertXPathEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "changed1" );
        assertXPathEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "changed2" );
        assertXPathEquals( "/contentdata/filesize", contentDataXml, String.valueOf( changedDummyBytes.length ) );
        assertXPathEquals( "/contentdata/binarydata/@key", contentDataXml,
                           binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
    }

    private void doCreate_UpdateContentCommand( UpdateContentCommand command, ContentKey contentKey, String name, String description,
                                                int fileBytesLength, String[] keywords )
    {
        ContentEntity content = createContent( "MyCategory", "en", "testuser", "0" );
        content.setKey( contentKey );
        ContentVersionEntity version = createContentVersion( "0", "testuser" );

        Document fileContentDataDoc = createFileContentDataDoc( name, description, keywords, String.valueOf( fileBytesLength ) );

        version.setContentData( FileContentDataParser.parse( fileContentDataDoc, null ) );

        ContentAndVersion contentAndVersion = new ContentAndVersion( content, version );
        command.setModifier( findUserByName( "testuser" ) );

        // Populate command with contentEntity data
        command.populateContentValuesFromContent( content );
        command.populateContentVersionValuesFromContentVersion( contentAndVersion.getVersion() );
    }

    private CreateContentCommand doCreate_CreateContentCommand( String name, String description, byte[] fileBytes, String filename,
                                                                String[] keywords )
    {
        ContentEntity content = createContent( "MyCategory", "en", "testuser", "0" );
        ContentVersionEntity version = createContentVersion( "0", "testuser" );

        Document fileContentDataDoc = createFileContentDataDoc( name, description, keywords, String.valueOf( fileBytes.length ) );
        version.setContentData( FileContentDataParser.parse( fileContentDataDoc, null ) );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        binaryDatas.add( createBinaryDataAndBinary( filename, fileBytes ) );

        CreateContentCommand command = new CreateContentCommand();
        command.setCreator( findUserByName( "testuser" ) );

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