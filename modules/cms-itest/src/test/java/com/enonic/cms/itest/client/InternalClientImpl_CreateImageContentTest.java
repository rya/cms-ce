package com.enonic.cms.itest.client;

import com.enonic.cms.api.client.model.CreateImageContentParams;
import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.image.*;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.binary.BinaryDataEntity;
import com.enonic.cms.core.content.binary.ContentBinaryDataEntity;
import com.enonic.cms.core.content.contentdata.legacy.LegacyImageContentData;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.framework.xml.XMLDocumentFactory;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.AssertTool;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InternalClientImpl_CreateImageContentTest
    extends AbstractSpringTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private InternalClient internalClient;

    private byte[] dummyBinary =
        new byte[]{66, 77, 58, 0, 0, 0, 0, 0, 0, 0, 54, 0, 0, 0, 40, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 24, 0, 0, 0, 0, 0, 4, 0, 0, 0,
            -60, 14, 0, 0, -60, 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -22, -18, -23, 0};

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

        hibernateTemplate.flush();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );
    }


    @Test
    public void testCreateImageContent()
        throws Exception
    {
        setUpContentAndCategory();

        setRunningUser();

        CreateImageContentParams params = new CreateImageContentParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.publishFrom = new Date();
        params.publishTo = null;
        params.status = ContentStatus.STATUS_DRAFT;
        params.contentData = createImageContentData( "200" );

        int contentKey = internalClient.createImageContent( params );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = fixture.findContentByKey( new ContentKey( contentKey ) );
        assertNotNull( persistedContent );
        assertEquals( "MyCategory", persistedContent.getCategory().getName() );

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        assertNotNull( persistedVersion );
        assertEquals( "test binary", persistedVersion.getTitle() );
        assertEquals( com.enonic.cms.core.content.ContentStatus.DRAFT.getKey(), persistedVersion.getStatus().getKey() );

        Set<ContentBinaryDataEntity> contentBinaryDatas = persistedVersion.getContentBinaryData();
        assertEquals( 1, contentBinaryDatas.size() );
        assertEquals( "source", contentBinaryDatas.iterator().next().getLabel() );

        BinaryDataEntity binaryDataResolvedFromContentBinaryData = contentBinaryDatas.iterator().next().getBinaryData();
        assertEquals( "Dummy Name", binaryDataResolvedFromContentBinaryData.getName() );

        LegacyImageContentData contentData = (LegacyImageContentData) persistedVersion.getContentData();
        assertNotNull( contentData );

        Document contentDataXml = contentData.getContentDataXml();
        AssertTool.assertSingleXPathValueEquals( "/contentdata/name", contentDataXml, "test binary" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/description", contentDataXml, "Dummy description." );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/keywords", contentDataXml, "keyword1 keyword2" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/sourceimage/binarydata/@key", contentDataXml,
                                                 binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString() );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/sourceimage/@width", contentDataXml, "200" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/sourceimage/@height", contentDataXml, "200" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/images/@border", contentDataXml, "no" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/images/image/@rotation", contentDataXml, "none" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/images/image/@type", contentDataXml, "original" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/images/image/width", contentDataXml, "200" );
        AssertTool.assertSingleXPathValueEquals( "/contentdata/images/image/height", contentDataXml, "200" );
        AssertTool.assertSingleXPathValueEquals("/contentdata/images/image/binarydata/@key", contentDataXml,
                binaryDataResolvedFromContentBinaryData.getBinaryDataKey().toString());
    }

    private void setRunningUser()
    {
        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );
    }

    private void setUpContentAndCategory()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "File content", ContentHandlerName.IMAGE.getHandlerClassShortName() ) );
        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.IMAGE.getHandlerClassShortName(), contentTypeConfig ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read,create" ) );
        fixture.flushAndClearHibernateSesssion();
    }

    private ImageContentDataInput createImageContentData( String fileName )
        throws Exception
    {
        ImageContentDataInput imageContentData = new ImageContentDataInput();

        imageContentData.binary = new ImageBinaryInput( loadImageFile( fileName ), "Dummy Name" );

        imageContentData.description = new ImageDescriptionInput( "Dummy description." );

        imageContentData.keywords = new ImageKeywordsInput().addKeyword( "keyword1" ).addKeyword( "keyword2" );
        imageContentData.name = new ImageNameInput( "test binary" );

        return imageContentData;
    }


    private String createFileName( String fileName )
    {
        return InternalClientImpl_CreateImageContentTest.class.getName().replace( ".", "/" ) + "-" + fileName + "px.jpg";
    }

    private byte[] loadImageFile( String fileName )
        throws IOException
    {
        ClassPathResource resource = new ClassPathResource( createFileName( fileName ) );
        InputStream in = resource.getInputStream();
        return IOUtils.toByteArray( in );
    }

}
