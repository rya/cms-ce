/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contentdata.legacy.LegacyImageContentData;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfig;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_copyContentTest
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

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() ).getAsBytes();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

    }

    private void initNeededData()
    {

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    private ContentKey createContent( Integer status )
    {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        UserEntity runningUser = fixture.findUserByName( "admin" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );
        content.setName( "testcontent_" + status );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setModifiedBy( fixture.findUserByName( "testuser" ) );
        version.setStatus( ContentStatus.get( status ) );
        version.setContent( content );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "Tittel", "contentdata/mytitle" );
        TextDataEntryConfig subElementConfig =
            new TextDataEntryConfig( "myTitleInSubElement", false, "My title in sub element", "contentdata/subelement/mytitle" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );
        contentData.add( new TextDataEntry( subElementConfig, "test subtitle" ) );

        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        createContentCommand.setBinaryDatas( binaryDatas );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );
        return contentService.createContent( createContentCommand );
    }

    @Test
    public void testCopy()
    {
        // setup
        initNeededData();

        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        hibernateTemplate.clear();

        ContentEntity existingContent = contentDao.findByKey( contentKey );

        // excersise

        final UserEntity copier = fixture.findUserByName( "admin" );
        final CategoryEntity toCategory = fixture.findCategoryByName( "MyCategory" );
        ContentKey newContentKey = contentService.copyContent( copier, existingContent, toCategory );

        hibernateTemplate.clear();

        // verify
        assertNotNull( newContentKey );

        ContentEntity newContent = contentDao.findByKey( newContentKey );
        assertNotNull( newContent );

        assertEquals( 1, newContent.getVersionCount() );
        assertEquals( ContentStatus.DRAFT.getKey(), newContent.getMainVersion().getStatus().getKey() );

    }

    // Now: newContent.getMainVersion() has no binaries attaches, should have 3
//    @Test
//    public void testCopyBinary()
//    {
//
//        ContentKey contentKey = setupCopyBinary();
//        ContentEntity existingContent = contentDao.findByKey( contentKey );
//        assertNotNull ( existingContent );
//
//        ContentVersionEntity mainVersion = existingContent.getMainVersion();
//        assertNotNull( mainVersion );
//        assertNotNull( mainVersion.getContentBinaryData() );
//        assertEquals( 3, mainVersion.getContentBinaryData().size() );
//
//        // excersise
//        final UserEntity copier = findUserByName( "admin" );
//        final CategoryEntity toCategory = findCategoryByName( "MyCategory" );
//        ContentKey newContentKey = contentService.copyContent( copier, existingContent, toCategory );
//
//        hibernateTemplate.clear();
//
//        // verify
//        assertNotNull( newContentKey );
//
//        ContentEntity newContent = contentDao.findByKey( newContentKey );
//        assertNotNull( newContent );
//        Set< ContentBinaryDataEntity > binaryData = newContent.getMainVersion().getContentBinaryData();
//        assertEquals( 3 , binaryData.size() );
//
//
//        ContentBinaryDataEntity binaryArray[] = new ContentBinaryDataEntity[ binaryData.size() ];
//        binaryArray = binaryData.toArray( binaryArray );
//        assertEquals( "bilde_scaled_1",  binaryArray[0].getBinaryData().getName() );
//        assertEquals( "bilde_scaled_2",  binaryArray[1].getBinaryData().getName() );
//        assertEquals( "bilde_original",  binaryArray[2].getBinaryData().getName() );
//
//        assertEquals( 1, newContent.getVersionCount() );
//        assertEquals( ContentVersionEntity.STATUS_DRAFT, newContent.getMainVersion().getStatus().intValue() );
//
//    }

    private ContentKey setupCopyBinary()
    {

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "Image content", ContentHandlerName.IMAGE.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.IMAGE.getHandlerClassShortName() ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.flushAndClearHibernateSesssion();

        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setModifiedBy( fixture.findUserByName( "testuser" ) );
        version.setStatus( ContentStatus.DRAFT );
        version.setContent( content );

        Document contentDataDoc = createImageContentDataDoc( "my image", 2 );
        LegacyImageContentData contentData = new LegacyImageContentData( contentDataDoc );
        version.setContentData( contentData );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        binaryDatas.add( factory.createBinaryDataAndBinary( "bilde_scaled_1", new byte[]{1, 0, 1} ) );
        binaryDatas.add( factory.createBinaryDataAndBinary( "bilde_scaled_2", new byte[]{1, 0, 2} ) );
        binaryDatas.add( factory.createBinaryDataAndBinary( "bilde_original", new byte[]{1, 0, 3} ) );

        CreateContentCommand command = new CreateContentCommand();
        command.setCreator( runningUser );

        command.populateCommandWithContentValues( content );
        command.populateCommandWithContentVersionValues( version );

        command.setBinaryDatas( binaryDatas );
        command.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( command );

        hibernateTemplate.clear();

        return contentKey;
    }

    private Document createImageContentDataDoc( String name, int imageCountIncludingSourceImage )
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "name" ).setText( name ) );
        Element imagesEl = new Element( "images" );
        contentdataEl.addContent( imagesEl );
        int nextPlaceHolderIndex = 0;
        if ( imageCountIncludingSourceImage > 0 )
        {
            for ( int i = 0; i < imageCountIncludingSourceImage; i++ )
            {
                Element imageEl = new Element( "image" );
                imagesEl.addContent( imageEl );
                Element binarydataEl = new Element( "binarydata" );
                binarydataEl.setAttribute( "key", "%" + String.valueOf( nextPlaceHolderIndex++ ) );
                imageEl.addContent( binarydataEl );
            }
            Element sourceimageEl = new Element( "sourceimage" );
            Element binarydataEl = new Element( "binarydata" );
            binarydataEl.setAttribute( "key", "%" + String.valueOf( nextPlaceHolderIndex++ ) );
            sourceimageEl.addContent( binarydataEl );
            contentdataEl.addContent( sourceimageEl );
        }
        return new Document( contentdataEl );
    }
}