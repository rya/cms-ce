/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import org.jdom.Document;
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

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.file.FileBinaryInput;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;
import com.enonic.cms.api.client.model.content.file.FileDescriptionInput;
import com.enonic.cms.api.client.model.content.file.FileKeywordsInput;
import com.enonic.cms.api.client.model.content.file.FileNameInput;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.test.AssertTool;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.business.client.InternalClient;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataEntity;
import com.enonic.cms.domain.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InternalClientImpl_CreateFileContentTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private InternalClient internalClient;

    private byte[] dummyBinary = new byte[]{1, 2, 3};

    private XMLBytes contentTypeConfig;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );
        fixture.initSystemData();

        StringBuffer contentTypeConfigXml = new StringBuffer();
        contentTypeConfigXml.append( "<moduledata/>" );
        contentTypeConfig = XMLDocumentFactory.create( contentTypeConfigXml.toString() ).getAsBytes();

        hibernateTemplate.flush();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

    }


    @Test
    public void testCreateFileContent()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.FILE.getHandlerClassShortName(), contentTypeConfig ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read,create" ) );
        fixture.flushAndClearHibernateSesssion();

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        FileContentDataInput fileContentData = new FileContentDataInput();
        fileContentData.binary = new FileBinaryInput( dummyBinary, "Dummy Name" );
        fileContentData.description = new FileDescriptionInput( "Dummy description." );
        fileContentData.keywords = new FileKeywordsInput().addKeyword( "keyword1" ).addKeyword( "keyword2" );
        fileContentData.name = new FileNameInput( "test binary" );

        CreateFileContentParams params = new CreateFileContentParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.publishFrom = new Date();
        params.publishTo = null;
        params.status = ContentStatus.STATUS_DRAFT;
        params.fileContentData = fileContentData;
        int contentKey = internalClient.createFileContent( params );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = fixture.findContentByKey( new ContentKey( contentKey ) );
        assertNotNull( persistedContent );
        assertEquals( "MyCategory", persistedContent.getCategory().getName() );

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        assertNotNull( persistedVersion );
        assertEquals( "test binary", persistedVersion.getTitle() );
        assertEquals( com.enonic.cms.domain.content.ContentStatus.DRAFT.getKey(), persistedVersion.getStatus().getKey() );

        Set<ContentBinaryDataEntity> contentBinaryDatas = persistedVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );

        BinaryDataEntity binaryDataResolvedFromContentBinaryData = contentBinaryDatas.iterator().next().getBinaryData();
        assertEquals( "Dummy Name", binaryDataResolvedFromContentBinaryData.getName() );

        LegacyFileContentData contentData = (LegacyFileContentData) persistedVersion.getContentData();
        assertNotNull( contentData );

        Document contentDataXml = contentData.getContentDataXml();
        AssertTool.assertSingleXPathValueEquals( "/contentdata/name", contentDataXml, "test binary" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/description", contentDataXml, "Dummy description." );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[1]", contentDataXml, "keyword1" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords/keyword[2]", contentDataXml, "keyword2" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/filesize", contentDataXml, String.valueOf( dummyBinary.length ) );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/binarydata/@key", contentDataXml,
                                                 binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
    }


}