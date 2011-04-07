/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import junitx.framework.Assert;

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.DomainFactory;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.business.AbstractPersistContentTest;

import com.enonic.cms.core.content.DomainFixture;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.business.portal.SiteRedirectHelper;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.category.CategoryEntity;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.security.user.User;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CustomContentHandlerController_operation_UpdateTest
    extends AbstractPersistContentTest
{
    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private ContentService contenService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private ContentDao contentDao;

    private SiteRedirectHelper siteRedirectHelper;

    private CustomContentHandlerController customContentHandlerController;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    private MockHttpSession session = new MockHttpSession();

    private SiteKey siteKey_1 = new SiteKey( 1 );

    private DomainFactory factory;

    private DomainFixture fixture;

    @Before
    public void setUp()
    {
        groupEntityDao.invalidateCachedKeys();

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        customContentHandlerController = new CustomContentHandlerController();
        customContentHandlerController.setContentService( contenService );
        customContentHandlerController.setSecurityService( securityService );
        customContentHandlerController.setCategoryDao( categoryDao );
        customContentHandlerController.setContentDao( contentDao );
        customContentHandlerController.setUserServicesRedirectHelper( new UserServicesRedirectUrlResolver() );

        // just need a dummy of the SiteRedirectHelper
        siteRedirectHelper = createMock( SiteRedirectHelper.class );
        customContentHandlerController.setSiteRedirectHelper( siteRedirectHelper );

        // setup
        initSystemData();
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );
        SecurityHolder.setAnonUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void update_with_text_input()
        throws RemoteException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/mytitle", "Mandantory", true );
        ctyconf.addInput( "tochange", "text", "contentdata/toupdate", "To be changed", false );
        ctyconf.addInput( "tobeempty", "text", "contentdata/tobeempty", "To be set to empty", false );
        ctyconf.addInput( "tobenull", "text", "contentdata/tobenull", "To be set to null", false );
        ctyconf.addInput( "toalsobeempty", "text", "contentdata/toalsobeempty", "To also be set to empty", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        // setup content repository
        fixture.save( factory.createUnit( "MyUnit" ) );
        final CategoryEntity categoryEntity =
            factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser", true );
        fixture.save( categoryEntity );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create" ) );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        // setup: create the content to update
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "MyCategory" ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "tochange", "Initial" );
        formItems.putString( "tobeempty", "Not empty" );
        formItems.putString( "tobenull", "Not empty" );
        formItems.putString( "toalsobeempty", "Not empty" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // execise: update the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "tochange", "Changed" );
        formItems.putString( "tobeempty", "" );
        formItems.putString( "toalsobeempty", null );
        customContentHandlerController.handlerUpdate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory" ) );
        assertNotNull( content );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();

        assertEquals( "Changed", ( (TextDataEntry) contentData.getEntry( "tochange" ) ).getValue() );

        assertTrue( contentData.getEntry( "tobeempty" ).hasValue() );
        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "tobeempty" ) ).getValue() );

        assertFalse( contentData.getEntry( "tobenull" ).hasValue() );
        assertEquals( null, ( (TextDataEntry) contentData.getEntry( "tobenull" ) ).getValue() );

        assertEquals( "", ( (TextDataEntry) contentData.getEntry( "toalsobeempty" ) ).getValue() );
        assertTrue( contentData.getEntry( "toalsobeempty" ).hasValue() );
    }

    @Test
    public void update_with_checkbox_input_with_no_value_becomes_false()
        throws RemoteException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/mytitle", "Mandantory", true );
        ctyconf.addInput( "myCheckbox", "checkbox", "contentdata/mycheckbox", "My checkbox", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        // setup content repository
        fixture.save( factory.createUnit( "MyUnit" ) );
        final CategoryEntity categoryEntity =
            factory.createCategory( "MyCategory", "MyContentType", "MyUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true );
        fixture.save( categoryEntity );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // setup: create the content to update
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "MyCategory" ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "myCheckbox", "true" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // execise: update the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory" ) ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        customContentHandlerController.handlerUpdate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory" ) );
        assertNotNull( content );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();

        assertEquals( "false", ( (BooleanDataEntry) contentData.getEntry( "myCheckbox" ) ).getValueAsString() );
    }


    @Test
    public void handlerUpdate_uploadfile_input_keep_existing_binary()
        throws RemoteException
    {
        String myCategoryName = "MyCategory3";
        String myContentTypeName = "MyContentType3";

        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( myContentTypeName, false );

        createAndSaveContentTypeAndCategory( myContentTypeName, myCategoryName, ctyconf );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory3", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // execise: create the content
        String categoryName = myCategoryName;
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( categoryName ).getKey().toString() );
        formItems.putString( "title", "Title" );
        formItems.put( "unrequired_new", new MockFileItem( "thisIsATestFile".getBytes() ) );

        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        CustomContentData contentData = getCustomContentDataResult( categoryName );
        Integer existingBinaryKey = getExistingBinaryKey( contentData, "unrequired" );

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key",
                             fixture.findFirstContentByCategory( fixture.findCategoryByName( myCategoryName ) ).getKey().toString() );
        formItems.putString( "title", "Title2" );
        formItems.put( "unrequired", existingBinaryKey );

        customContentHandlerController.handlerUpdate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        contentData = getCustomContentDataResult( categoryName );

        assertEquals( getExistingBinaryKey( contentData, "unrequired" ), existingBinaryKey );
        assertEquals( "Title2", ( (TextDataEntry) contentData.getEntry( "title" ) ).getValue() );
    }

    @Test
    public void handlerUpdate_uploadfile_input_overwrite_existing_binary()
        throws RemoteException
    {
        String myContentTypeName = "MyContentType3";

        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( myContentTypeName, false );

        createAndSaveContentTypeAndCategory( myContentTypeName, "MyCategory3", ctyconf );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory3", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // execise: create the content
        String categoryName = "MyCategory3";
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( categoryName ).getKey().toString() );
        formItems.putString( "title", "Title" );
        formItems.put( "unrequired_new", new MockFileItem( "thisIsATestFile".getBytes() ) );

        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        CustomContentData contentData = getCustomContentDataResult( categoryName );
        Integer existingBinaryKey = getExistingBinaryKey( contentData, "unrequired" );

        // execise: update the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.putString( "title", "Title2" );
        formItems.put( "unrequired", existingBinaryKey );
        formItems.put( "unrequired_new", new MockFileItem( "thisIsATestFile".getBytes() ) );

        customContentHandlerController.handlerUpdate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        contentData = getCustomContentDataResult( categoryName );

        Assert.assertFalse( getExistingBinaryKey( contentData, "unrequired" ).equals( existingBinaryKey ) );
        assertEquals( "Title2", ( (TextDataEntry) contentData.getEntry( "title" ) ).getValue() );
    }

    @Test
    public void handlerUpdate_uploadfile_input_remove_existing_binary()
        throws RemoteException
    {
        String myContentTypeName = "MyContentType3";

        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( myContentTypeName, false );

        createAndSaveContentTypeAndCategory( myContentTypeName, "MyCategory3", ctyconf );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory3", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // execise: create the content
        String categoryName = "MyCategory3";
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( categoryName ).getKey().toString() );
        formItems.putString( "title", "Title" );
        formItems.put( "unrequired_new", new MockFileItem( "thisIsATestFile".getBytes() ) );

        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // execise: update the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.putString( "title", "Title2" );
        formItems.put( "unrequired", "" );

        customContentHandlerController.handlerUpdate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        CustomContentData contentData = getCustomContentDataResult( categoryName );

        // The binary should have been removed
        assertBinaryDataEntryHasNoValue( contentData, "unrequired" );

        assertEquals( "Title2", ( (TextDataEntry) contentData.getEntry( "title" ) ).getValue() );
    }


    private ContentTypeConfigBuilder setUpUploadFileTestContent( String myContentTypeName, boolean requiredUploadFile )
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( myContentTypeName, "title" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addInput( "unrequired", "uploadfile", "contentdata/unrequired", "Unrequired", requiredUploadFile );
        ctyconf.endBlock();
        return ctyconf;
    }

    private Integer getExistingBinaryKey( CustomContentData contentData, String entryName )
    {
        return ( (BinaryDataEntry) contentData.getEntry( entryName ) ).getExistingBinaryKey();
    }

    private void assertBinaryDataEntryHasNoValue( CustomContentData contentData, String entryName )
    {
        assertTrue( ( (BinaryDataEntry) contentData.getEntry( entryName ) ).hasNullBinaryKey() );
    }


    private void createAndSaveContentTypeAndCategory( String contentTypeName, String categoryName, ContentTypeConfigBuilder ctyconf )
    {
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( contentTypeName, ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        createAndSaveCategoryOfContentType( categoryName, contentTypeName );
    }

    private void createAndSaveCategoryOfContentType( String categoryName, String contentTypeName )
    {
        String unitName = "UnitFor_" + categoryName;
        fixture.save( factory.createUnit( unitName, "en" ) );

        fixture.flushAndClearHibernateSesssion();

        final CategoryEntity categoryEntity =
            factory.createCategory( categoryName, contentTypeName, unitName, User.ANONYMOUS_UID, User.ANONYMOUS_UID, true );

        fixture.save( categoryEntity );

        fixture.flushAndClearHibernateSesssion();
    }

    private CustomContentData getCustomContentDataResult( String categoryName )
    {
        ContentEntity content = fixture.findFirstContentByCategory( fixture.findCategoryByName( categoryName ) );
        assertNotNull( content );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();
        return contentData;
    }

}