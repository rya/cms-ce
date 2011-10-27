package com.enonic.cms.itest.client;

import com.enonic.cms.api.client.ClientException;
import com.enonic.cms.api.client.model.CreateFileContentParams;
import com.enonic.cms.api.client.model.GetBinaryParams;
import com.enonic.cms.api.client.model.GetContentBinaryParams;
import com.enonic.cms.api.client.model.content.file.FileBinaryInput;
import com.enonic.cms.api.client.model.content.file.FileContentDataInput;
import com.enonic.cms.api.client.model.content.file.FileDescriptionInput;
import com.enonic.cms.api.client.model.content.file.FileNameInput;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.*;
import com.enonic.cms.core.content.contentdata.legacy.LegacyFileContentData;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.AssertTool;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class InternalClientImpl_getBinaryTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private InternalClient internalClient;

    private Document contentTypeConfig;

    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );
        fixture.initSystemData();

        StringBuffer contentTypeConfigXml = new StringBuffer();
        contentTypeConfigXml.append( "<moduledata/>" );
        contentTypeConfig = XMLDocumentFactory.create( contentTypeConfigXml.toString() ).getAsJDOMDocument();

        fixture.flushAndClearHibernateSesssion();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        fixture.createAndStoreUserAndUserGroup( "creator", "creator fullname", UserType.NORMAL, "testuserstore" );
        fixture.createAndStoreUserAndUserGroup( "getter", "getter fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "File content", ContentHandlerName.FILE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.FILE.getHandlerClassShortName(), contentTypeConfig ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "creator", "creator" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "creator", "read,create,approve" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "getter", "read,create" ) );
        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void getBinary()
    {
        SecurityHolder.setRunAsUser( fixture.findUserByName( "creator" ).getKey() );

        ContentKey contentKey =
            createFileContent( "Dummy name", new byte[]{1, 2, 3}, ContentStatus.APPROVED, "MyCategory" );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = fixture.findContentByKey( contentKey );
        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        LegacyFileContentData contentData = (LegacyFileContentData) persistedVersion.getContentData();

        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetBinaryParams params = new GetBinaryParams();
        params.binaryKey = contentData.resolveBinaryDataKey().toInt();
        Document binaryDoc = internalClient.getBinary( params );

        // verify
        AssertTool.assertSingleXPathValueEquals( "/binary/filename", binaryDoc, "Dummy name" );
        AssertTool.assertSingleXPathValueEquals( "/binary/binarykey", binaryDoc, "" + contentData.resolveBinaryDataKey() );
        AssertTool.assertSingleXPathValueEquals( "/binary/contentkey", binaryDoc, "" + persistedContent.getKey() );
        AssertTool.assertSingleXPathValueEquals( "/binary/size", binaryDoc, "" + 3 );
    }

    @Test
    public void getContentBinary()
    {
        SecurityHolder.setRunAsUser( fixture.findUserByName( "creator" ).getKey() );

        ContentKey contentKey =
            createFileContent( "Dummy name", new byte[]{1, 2, 3}, ContentStatus.APPROVED, "MyCategory" );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = fixture.findContentByKey( contentKey );
        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        LegacyFileContentData contentData = (LegacyFileContentData) persistedVersion.getContentData();

        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetContentBinaryParams params = new GetContentBinaryParams();
        params.contentKey = contentKey.toInt();
        params.label = "source";
        Document binaryDoc = internalClient.getContentBinary( params );

        // verify
        AssertTool.assertSingleXPathValueEquals( "/binary/filename", binaryDoc, "Dummy name" );
        AssertTool.assertSingleXPathValueEquals( "/binary/binarykey", binaryDoc, "" + contentData.resolveBinaryDataKey() );
        AssertTool.assertSingleXPathValueEquals( "/binary/contentkey", binaryDoc, "" + persistedContent.getKey() );
        AssertTool.assertSingleXPathValueEquals( "/binary/size", binaryDoc, "" + 3 );
    }

    @Test
    public void getContentBinary_with_no_label()
    {
        SecurityHolder.setRunAsUser( fixture.findUserByName( "creator" ).getKey() );

        ContentKey contentKey =
            createFileContent( "Dummy name", new byte[]{1, 2, 3}, ContentStatus.APPROVED, "MyCategory" );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = fixture.findContentByKey( contentKey );
        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        LegacyFileContentData contentData = (LegacyFileContentData) persistedVersion.getContentData();

        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetContentBinaryParams params = new GetContentBinaryParams();
        params.contentKey = contentKey.toInt();
        Document binaryDoc = internalClient.getContentBinary( params );

        // verify
        AssertTool.assertSingleXPathValueEquals( "/binary/filename", binaryDoc, "Dummy name" );
        AssertTool.assertSingleXPathValueEquals( "/binary/binarykey", binaryDoc, "" + contentData.resolveBinaryDataKey() );
        AssertTool.assertSingleXPathValueEquals( "/binary/contentkey", binaryDoc, "" + persistedContent.getKey() );
        AssertTool.assertSingleXPathValueEquals("/binary/size", binaryDoc, "" + 3);
    }

    @Test
    public void getContentBinary_with_non_existing_content_key_throws_exception()
    {
        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetContentBinaryParams params = new GetContentBinaryParams();
        params.contentKey = 123;
        params.label = "source";
        try
        {
            internalClient.getContentBinary( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains( "Attachment not found, contentKey: '123'" ) );
        }
    }

    @Test
    public void getContentBinary_with_non_existing_label_throws_exception()
    {
        SecurityHolder.setRunAsUser( fixture.findUserByName( "creator" ).getKey() );
        ContentKey contentKey =
            createFileContent( "Dummy name", new byte[]{1, 2, 3}, ContentStatus.APPROVED, "MyCategory" );

        fixture.flushAndClearHibernateSesssion();

        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetContentBinaryParams params = new GetContentBinaryParams();
        params.contentKey = contentKey.toInt();
        params.label = "nolabel";
        try
        {
            internalClient.getContentBinary( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains( "Attachment not found, contentKey: '" + contentKey + "'" ) );
        }
    }

    @Test
    public void getBinary_with_non_existing_binary_key_throws_exception()
    {
        // exercise
        SecurityHolder.setRunAsUser( fixture.findUserByName( "getter" ).getKey() );
        GetBinaryParams params = new GetBinaryParams();
        params.binaryKey = 123;
        try
        {
            internalClient.getBinary( params );
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof ClientException );
            assertTrue( e.getMessage().contains( "Attachment not found, binaryDataKey: '123'" ) );
        }
    }

    private ContentKey createFileContent( String name, byte[] bytes, ContentStatus contentStatus,
                                          String categoryName )
    {
        FileContentDataInput fileContentData = new FileContentDataInput();
        fileContentData.binary = new FileBinaryInput( bytes, name );
        fileContentData.description = new FileDescriptionInput( "Dummy description." );
        fileContentData.name = new FileNameInput( name );

        CreateFileContentParams params = new CreateFileContentParams();
        params.categoryKey = fixture.findCategoryByName( categoryName ).getKey().toInt();
        params.publishFrom = new Date();
        params.publishTo = null;
        params.status = contentStatus.getKey();
        params.fileContentData = fileContentData;

        int contentKey = internalClient.createFileContent( params );
        return new ContentKey( contentKey );
    }


}
