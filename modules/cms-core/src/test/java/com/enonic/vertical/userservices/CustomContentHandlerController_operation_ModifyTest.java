/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.userservices;

import java.rmi.RemoteException;

import com.enonic.cms.core.content.*;
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

import com.enonic.esl.containers.ExtendedMap;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.core.business.AbstractPersistContentTest;

import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.portal.SiteRedirectHelper;

import com.enonic.cms.domain.SiteKey;
import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.category.CategoryEntity;
import com.enonic.cms.core.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.core.content.contentdata.custom.BooleanDataEntry;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.security.user.User;

import static junitx.framework.Assert.assertFalse;
import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CustomContentHandlerController_operation_ModifyTest
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
    public void before()
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

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        // setup
        fixture.initSystemData();
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.createAndStoreNormalUserWithUserGroup( "testuser", "Test user", "testuserstore" );
        SecurityHolder.setAnonUser( fixture.findUserByName( "testuser" ).getKey() );
    }

    @Test
    public void modify_with_checkbox_input_with_no_value_becomes_unchanged()
        throws RemoteException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType1", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/mytitle", "Mandantory", true );
        ctyconf.addInput( "myCheckbox", "checkbox", "contentdata/mycheckbox", "My checkbox", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyContentType1", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        // setup content repository
        fixture.save( factory.createUnit( "MyUnit1" ) );
        final CategoryEntity categoryEntity =
            factory.createCategory( "MyCategory1", "MyContentType1", "MyUnit1", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true );
        fixture.save( categoryEntity );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory1", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // setup: create the content to update
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "MyCategory1" ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "myCheckbox", "true" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory1" ) ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory1" ) );
        assertNotNull( content );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();

        assertEquals( "true", ( (BooleanDataEntry) contentData.getEntry( "myCheckbox" ) ).getValueAsString() );
    }

    @Test
    public void modify_with_checkbox_input_with_false_value_becomes_false()
        throws RemoteException
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType2", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/mytitle", "Mandantory", true );
        ctyconf.addInput( "myIncludedCheckbox", "checkbox", "contentdata/myincludedcheckbox", "My checkbox to change", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyContentType2", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        // setup content repository
        fixture.save( factory.createUnit( "MyUnit2" ) );
        final CategoryEntity categoryEntity =
            factory.createCategory( "MyCategory2", "MyContentType2", "MyUnit2", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true );
        fixture.save( categoryEntity );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory2", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // setup: create the content to update
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "MyCategory2" ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "myIncludedCheckbox", "true" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // verify that content was created as wanted
        ContentEntity content = fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory2" ) );
        assertEquals( "true", ( (BooleanDataEntry) ( (CustomContentData) content.getMainVersion().getContentData() ).getEntry(
            "myIncludedCheckbox" ) ).getValueAsString() );

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory2" ) ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "_included_checkbox", "myIncludedCheckbox" );
        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        // verify
        content = fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory2" ) );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();

        assertEquals( "false", ( (BooleanDataEntry) contentData.getEntry( "myIncludedCheckbox" ) ).getValueAsString() );
    }

    @Test
    public void modify()
        throws RemoteException
    {

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType3", "myTitle" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "myTitle", "text", "contentdata/mytitle", "Mandantory", true );
        ctyconf.addInput( "tochange", "text", "contentdata/tochange", "To be changed", false );
        ctyconf.addInput( "tochangetoblank", "text", "contentdata/tochangetoblank", "To be changed to blank", false );
        ctyconf.addInput( "toalsochangetoblank", "text", "contentdata/toalsochangetoblank", "To also be changed to blank", false );
        ctyconf.addInput( "unchanged", "text", "contentdata/unchanged", "Should not be changed", false );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "MyContentType3", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        // setup content repository
        fixture.save( factory.createUnit( "MyUnit3", "en" ) );
        fixture.save( factory.createCategory( "MyCategory3", "MyContentType3", "MyUnit3", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory3", "testuser", "read, create" ) );

        fixture.flushAndClearHibernateSesssion();

        // setup: create the content to modify
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", findCategoryByName( "MyCategory3" ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "tochange", "Initial" );
        formItems.putString( "tochangetoblank", "Not blank" );
        formItems.putString( "toalsochangetoblank", "Not blank" );
        formItems.putString( "unchanged", "Unchanged" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", findFirstContentByCategory( findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.putString( "myTitle", "Mandantory" );
        formItems.putString( "tochange", "Changed" );
        formItems.putString( "tochangetoblank", "" );
        formItems.putString( "toalsochangetoblank", null );
        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( findCategoryByName( "MyCategory3" ) );
        assertNotNull( content );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData contentData = (CustomContentData) version.getContentData();

        TextDataEntry tochange = (TextDataEntry) contentData.getEntry( "tochange" );
        assertEquals( "Changed", tochange.getValue() );

        TextDataEntry unchanged = (TextDataEntry) contentData.getEntry( "unchanged" );
        assertEquals( "Unchanged", unchanged.getValue() );

        TextDataEntry tochangetoblank = (TextDataEntry) contentData.getEntry( "tochangetoblank" );
        assertEquals( "", tochangetoblank.getValue() );

        TextDataEntry toalsochangetoblank = (TextDataEntry) contentData.getEntry( "toalsochangetoblank" );
        assertEquals( "", toalsochangetoblank.getValue() );
    }


    @Test
    public void modifying_an_entry_in_a_block_group_affects_only_the_modified_entry()
        throws RemoteException
    {

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        ctyconf.startBlock( "Phone", "contentdata/phone" );
        ctyconf.addInput( "phone_label", "text", "label", "Label", true );
        ctyconf.addInput( "phone_number", "text", "number", "Number", false );
        ctyconf.endBlock();

        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "PersonContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content repository
        fixture.save( factory.createUnit( "PersonsUnit", "en" ) );
        fixture.save(
            factory.createCategory( "PersonsCategory", "PersonContentType", "PersonsUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true ) );
        fixture.save( factory.createCategoryAccessForUser( "PersonsCategory", "testuser", "read, create" ) );
        fixture.flushAndClearHibernateSesssion();

        //CategoryEntity category = findCategoryByName( "PersonsCategory" );
        //ContentTypeEntity contentType = category.getContentType();
        //ContentTypeConfig contentTypeConfig = contentType.getContentTypeConfig();

        // setup: create the content to modify
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "PersonsCategory" ).getKey().toString() );
        formItems.putString( "name", "Runar Myklebust" );
        formItems.putString( "Phone[1].phone_label", "Mobile" );
        formItems.putString( "Phone[1].phone_number", "99999999" );
        formItems.putString( "Phone[2].phone_label", "Home" );
        formItems.putString( "Phone[2].phone_number", "22222222" );
        formItems.putString( "Phone[3].phone_label", "Fax" );
        formItems.putString( "Phone[3].phone_number", "00000000" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // exercise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key",
                             fixture.findFirstContentByCategory( fixture.findCategoryByName( "PersonsCategory" ) ).getKey().toString() );
        formItems.putString( "Phone[2].phone_number", "33333333" );
        formItems.putString( "Phone[4].phone_label", "Cabin" );
        formItems.putString( "Phone[4].phone_number", "55555555" );
        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( findCategoryByName( "PersonsCategory" ) );
        CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();

        GroupDataEntry groupDataEntry1 = contentData.getGroupDataEntry( "Phone", 1 );
        assertEquals( "99999999", ( (TextDataEntry) groupDataEntry1.getEntry( "phone_number" ) ).getValue() );

        GroupDataEntry groupDataEntry2 = contentData.getGroupDataEntry( "Phone", 2 );
        assertEquals( "33333333", ( (TextDataEntry) groupDataEntry2.getEntry( "phone_number" ) ).getValue() );

        GroupDataEntry groupDataEntry3 = contentData.getGroupDataEntry( "Phone", 3 );
        assertEquals( "00000000", ( (TextDataEntry) groupDataEntry3.getEntry( "phone_number" ) ).getValue() );

        GroupDataEntry groupDataEntry4 = contentData.getGroupDataEntry( "Phone", 4 );
        assertEquals( "55555555", ( (TextDataEntry) groupDataEntry4.getEntry( "phone_number" ) ).getValue() );
    }

    //@Test
    public void modify2()
        throws RemoteException
    {

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        ctyconf.startBlock( "Phones", "contentdata/phones/phone" );
        ctyconf.addInput( "phone_label", "text", "label", "Label", true );
        ctyconf.addInput( "phone_number", "text", "number", "Number", false );
        ctyconf.endBlock();

        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save(
            factory.createContentType( "PersonContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content repository
        fixture.save( factory.createUnit( "PersonsUnit", "en" ) );
        fixture.save(
            factory.createCategory( "PersonsCategory", "PersonContentType", "PersonsUnit", User.ANONYMOUS_UID, User.ANONYMOUS_UID, true ) );
        fixture.save( factory.createCategoryAccessForUser( "PersonsCategory", "testuser", "read, create" ) );
        fixture.flushAndClearHibernateSesssion();

        //CategoryEntity category = findCategoryByName( "PersonsCategory" );
        //ContentTypeEntity contentType = category.getContentType();
        //ContentTypeConfig contentTypeConfig = contentType.getContentTypeConfig();

        // setup: create the content to modify
        ExtendedMap formItems = new ExtendedMap( true );
        formItems.putString( "categorykey", fixture.findCategoryByName( "PersonsCategory" ).getKey().toString() );
        formItems.putString( "name", "Runar Myklebust" );
        formItems.putString( "Phone[1].phone_label", "Mobile" );
        formItems.putString( "Phone[1].phone_number", "99999999" );
        formItems.putString( "Phone[2].phone_label", "Home" );
        formItems.putString( "Phone[2].phone_number", "22222222" );
        customContentHandlerController.handlerCreate( request, response, session, formItems, null, siteKey_1 );

        fixture.flushAndClearHibernateSesssion();

        // exercise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key",
                             fixture.findFirstContentByCategory( fixture.findCategoryByName( "PersonsCategory" ) ).getKey().toString() );
        formItems.putString( "Phone[2].phone_label", "Home" );
        formItems.putString( "Phone[2].phone_number", "33333333" );
        formItems.putString( "Phone[3].phone_label", "Cabin" );
        formItems.putString( "Phone[3].phone_number", "55555555" );
        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        // verify
        ContentEntity content = fixture.findFirstContentByCategory( findCategoryByName( "PersonsCategory" ) );
        CustomContentData contentData = (CustomContentData) content.getMainVersion().getContentData();

        GroupDataEntry groupDataEntry1 = contentData.getGroupDataEntry( "Phone", 1 );
        assertEquals( "33333333", ( (TextDataEntry) groupDataEntry1.getEntry( "phone_number" ) ).getValue() );

        GroupDataEntry groupDataEntry2 = contentData.getGroupDataEntry( "Phone", 2 );
        assertEquals( "55555555", ( (TextDataEntry) groupDataEntry2.getEntry( "phone_number" ) ).getValue() );

    }

    @Test
    public void handlerModify_uploadfile_input_keep_existing_binary()
        throws RemoteException
    {
        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( false );

        createAndSaveContentTypeAndCategory( "MyContentType3", "MyCategory3", ctyconf );
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

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.put( "unrequired", existingBinaryKey );

        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        contentData = getCustomContentDataResult( categoryName );

        assertEquals( getExistingBinaryKey( contentData, "unrequired" ), existingBinaryKey );
    }

    @Test
    public void handlerModify_uploadfile_input_overwrite_existing_binary()
        throws RemoteException
    {
        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( false );

        createAndSaveContentTypeAndCategory( "MyContentType3", "MyCategory3", ctyconf );
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

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.put( "unrequired", existingBinaryKey );
        formItems.put( "unrequired_new", new MockFileItem( "thisIsATestFile".getBytes() ) );

        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        contentData = getCustomContentDataResult( categoryName );

        assertFalse( getExistingBinaryKey( contentData, "unrequired" ).equals( existingBinaryKey ) );
    }

    @Test
    public void handlerModify_uploadfile_input_remove_existing_binary()
        throws RemoteException
    {
        ContentTypeConfigBuilder ctyconf = setUpUploadFileTestContent( false );

        createAndSaveContentTypeAndCategory( "MyContentType3", "MyCategory3", ctyconf );
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

        // execise: modify the content
        formItems = new ExtendedMap( true );
        formItems.putString( "key", fixture.findFirstContentByCategory( fixture.findCategoryByName( "MyCategory3" ) ).getKey().toString() );
        formItems.put( "unrequired", "" );

        customContentHandlerController.handlerModify( request, response, formItems );

        fixture.flushAndClearHibernateSesssion();

        contentData = getCustomContentDataResult( categoryName );

        // The binary should have been removed
        assertNull( getExistingBinaryKey( contentData, "unrequired" ) );
    }

    private ContentTypeConfigBuilder setUpUploadFileTestContent( boolean requiredUploadFile )
    {
        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "MyContentType3", "title" );
        ctyconf.startBlock( "General" );
        ctyconf.addInput( "title", "text", "contentdata/title", "Title", true );
        ctyconf.addInput( "unrequired", "uploadfile", "contentdata/unrequired", "Unrequired", requiredUploadFile );
        ctyconf.endBlock();
        return ctyconf;
    }

    private Integer getExistingBinaryKey( CustomContentData contentData, String entryName )
    {
        return ( (BinaryDataEntry) contentData.getEntry( "unrequired" ) ).getExistingBinaryKey();
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