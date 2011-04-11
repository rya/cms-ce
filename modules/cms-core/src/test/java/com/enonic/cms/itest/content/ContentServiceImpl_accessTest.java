/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.vertical.userservices.CustomContentHandlerController;
import com.enonic.vertical.userservices.UserServicesRedirectUrlResolver;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.UpdateContentException;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.security.SecurityService;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.portal.SiteRedirectHelper;
import com.enonic.cms.store.dao.CategoryDao;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.GroupEntityDao;
import com.enonic.cms.itest.DomainFactory;

import com.enonic.cms.core.content.CreateContentException;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentStatus;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfig;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.security.group.GroupEntity;
import com.enonic.cms.domain.security.group.GroupType;
import com.enonic.cms.domain.security.user.User;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_accessTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    protected ContentDao contentDao;

    @Autowired
    protected ContentService contentService;

    private SiteRedirectHelper siteRedirectHelper;

    private CustomContentHandlerController customContentHandlerController;

    private UserServicesRedirectUrlResolver userServicesRedirectUrlResolver;

    private DomainFactory factory;

    private DomainFixture fixture;


    @Before
    public void setUp()
    {
        groupEntityDao.invalidateCachedKeys();

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        customContentHandlerController = new CustomContentHandlerController();
        customContentHandlerController.setContentService( contentService );
        customContentHandlerController.setSecurityService( securityService );
        customContentHandlerController.setCategoryDao( categoryDao );

        userServicesRedirectUrlResolver = Mockito.mock( UserServicesRedirectUrlResolver.class );
        customContentHandlerController.setUserServicesRedirectHelper( userServicesRedirectUrlResolver );

        // just need a dummy of the SiteRedirectHelper
        siteRedirectHelper = createMock( SiteRedirectHelper.class );
        customContentHandlerController.setSiteRedirectHelper( siteRedirectHelper );

        // setup needed common data for each test
        fixture.initSystemData();

        //SecurityHolder.setUser( findUserByName( User.ANONYMOUS_UID ).getKey() );
        SecurityHolder.setAnonUser( fixture.findUserByName( User.ANONYMOUS_UID ).getKey() );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        fixture.flushAndClearHibernateSesssion();

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Person", "name" );
        ctyconf.startBlock( "Person" );
        ctyconf.addInput( "name", "text", "contentdata/name", "Name", true );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save( factory.createContentType( "Person", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.flushAndClearHibernateSesssion();

        fixture.save( factory.createUnit( "UnitForPerson", "en" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void create_content_with_status_draft_passes_when_user_have_create_or_administrate()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "creator", "testuserstore" );
        createAndSaveNormalUser( "administrator", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "administrator", "read, administrate" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "creator", ContentStatus.DRAFT ) );
        assertNotNull( contentDao.findByKey( contentKey ) );

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "administrator", ContentStatus.DRAFT ) );
        assertNotNull( contentDao.findByKey( contentKey ) );
    }

    @Test
    public void create_content_with_status_approved_passes_when_user_have_create_and_approve_or_administrate()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "creatorAndApprover", "testuserstore" );
        createAndSaveNormalUser( "administrator", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "creatorAndApprover", "read, create, approve" );
        createAndSaveCategoryAccess( categoryName, "administrator", "read, administrate" );

        ContentKey contentKey;

        contentKey =
            contentService.createContent( createCreateContentCommand( categoryName, "creatorAndApprover", ContentStatus.APPROVED ) );
        assertNotNull( contentDao.findByKey( contentKey ) );

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "administrator", ContentStatus.APPROVED ) );
        assertNotNull( contentDao.findByKey( contentKey ) );

    }

    @Test
    public void create_content_with_status_approved_passes_when_user_have_create_and_category_has_autoapprove()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName, true );

        createAndSaveNormalUser( "creator", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "creator", "read, create, approve" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "creator", ContentStatus.APPROVED ) );
        assertNotNull( contentDao.findByKey( contentKey ) );
    }

    @Test(expected = CreateContentException.class)
    public void create_content_with_status_approved_fails_when_user_have_no_approve_access()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );
        createAndSaveNormalUser( "creatorOnly", "testuserstore" );
        createAndSaveCategoryAccess( categoryName, "creatorOnly", "read, create" );

        contentService.createContent( createCreateContentCommand( categoryName, "creatorOnly", ContentStatus.APPROVED ) );
        fail();
    }

    @Test
    public void update_draft_content_passes_when_user_have_create_access_on_category()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "other-category-creator", "testuserstore" );
        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "category-approver", "testuserstore" );
        createAndSaveNormalUser( "category-administrator", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "other-category-creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "category-approver", "read, approve" );

        ContentKey contentKey;

        contentKey =
            contentService.createContent( createCreateContentCommand( categoryName, "other-category-creator", ContentStatus.DRAFT ) );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionToUpdate = content.getMainVersion().getKey();

        UpdateContentResult result;

        // verify:

        result = contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "category-creator", ContentStatus.DRAFT, versionToUpdate ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "category-approver", ContentStatus.APPROVED, versionToUpdate ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "category-creator", ContentStatus.DRAFT, result.getTargetedVersionKey() ) );
        assertNotNull( result.getTargetedVersionKey() );
    }

    @Test
    public void update_draft_content_passes_when_user_have_update_access_on_content()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "category-approver", "testuserstore" );
        createAndSaveNormalUser( "category-administrator", "testuserstore" );
        createAndSaveNormalUser( "content-updater", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "category-approver", "approve" );
        createAndSaveCategoryAccess( categoryName, "category-administrator", "administrate" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "category-creator", ContentStatus.DRAFT ) );
        createAndSaveContentAccess( contentKey, "content-updater", "update" );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionToUpdate = content.getMainVersion().getKey();

        UpdateContentResult result;

        // verify: 

        result = contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "content-updater", ContentStatus.DRAFT, versionToUpdate ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "category-approver", ContentStatus.DRAFT, versionToUpdate ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "category-administrator", ContentStatus.DRAFT, versionToUpdate ) );
        assertNotNull( result.getTargetedVersionKey() );
    }

    @Test(expected = UpdateContentException.class)
    public void update_draft_content_fails_when_user_have_only_read_access_on_content()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "content-reader", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "category-creator", ContentStatus.DRAFT ) );
        createAndSaveContentAccess( contentKey, "content-reader", "read" );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionToUpdate = content.getMainVersion().getKey();

        // verify:
        contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "content-reader", ContentStatus.DRAFT, versionToUpdate ) );
        fail();
    }

    @Test(expected = UpdateContentException.class)
    public void change_draft_content_to_approved_fails_when_user_have_only_create_access_on_category()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "content-reader", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "category-creator", ContentStatus.DRAFT ) );
        createAndSaveContentAccess( contentKey, "content-reader", "read" );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionToUpdate = content.getMainVersion().getKey();

        // verify:
        contentService.updateContent(
            createUpdateExistingContentCommand( contentKey, "category-creator", ContentStatus.APPROVED, versionToUpdate ) );
        fail();
    }

    @Test
    public void create_new_content_version_with_status_draft_passes_when_user_have_update_access_on_content()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "category-approver", "testuserstore" );
        createAndSaveNormalUser( "category-administrator", "testuserstore" );
        createAndSaveNormalUser( "content-updater", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "category-approver", "approve" );
        createAndSaveCategoryAccess( categoryName, "category-administrator", "administrate" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "category-creator", ContentStatus.DRAFT ) );
        createAndSaveContentAccess( contentKey, "content-updater", "update" );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionKeyToBaseOn = content.getMainVersion().getKey();

        UpdateContentResult result;

        // verify:

        result = contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "content-updater", ContentStatus.DRAFT, versionKeyToBaseOn ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "category-approver", ContentStatus.DRAFT, versionKeyToBaseOn ) );
        assertNotNull( result.getTargetedVersionKey() );

        result = contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "category-administrator", ContentStatus.DRAFT, versionKeyToBaseOn ) );
        assertNotNull( result.getTargetedVersionKey() );
    }

    @Test
    public void create_new_content_version_with_status_draft_passes_when_user_have_create_access_on_category()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "category-approver", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );
        createAndSaveCategoryAccess( categoryName, "category-approver", "create, approve" );

        ContentKey contentKey;

        contentKey =
            contentService.createContent( createCreateContentCommand( categoryName, "category-approver", ContentStatus.APPROVED ) );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionKeyToBaseOn = content.getMainVersion().getKey();

        UpdateContentResult result;

        // exercise
        result = contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "category-creator", ContentStatus.DRAFT, versionKeyToBaseOn ) );

        // verify
        assertNotNull( result.getTargetedVersionKey() );
    }

    @Test(expected = UpdateContentException.class)
    public void create_new_content_version_with_status_draft_fails_when_user_have_only_read_access_on_content()
    {
        String categoryName = "category";
        createAndStoreCategory( categoryName );

        createAndSaveNormalUser( "category-creator", "testuserstore" );
        createAndSaveNormalUser( "content-reader", "testuserstore" );

        createAndSaveCategoryAccess( categoryName, "category-creator", "read, create" );

        ContentKey contentKey;

        contentKey = contentService.createContent( createCreateContentCommand( categoryName, "category-creator", ContentStatus.DRAFT ) );
        createAndSaveContentAccess( contentKey, "content-reader", "read" );

        ContentEntity content = fixture.findContentByKey( contentKey );
        ContentVersionKey versionKeyToBaseOn = content.getMainVersion().getKey();

        // exercise and verify

        contentService.updateContent(
            createCreateNewVersionCommand( contentKey, "content-reader", ContentStatus.DRAFT, versionKeyToBaseOn ) );
        fail();

    }

    private void createAndSaveCategoryAccess( String categoryName, String userUid, String accesses )
    {
        final UserEntity user = fixture.findUserByName( userUid );
        fixture.save( factory.createCategoryAccess( categoryName, user, accesses ) );
        fixture.flushAndClearHibernateSesssion();
    }

    private void createAndSaveContentAccess( ContentKey contentKey, String userUid, String accesses )
    {
        final UserEntity user = fixture.findUserByName( userUid );
        fixture.save( factory.createContentAccess( contentKey, user, accesses ) );
        fixture.flushAndClearHibernateSesssion();
    }

    private CreateContentCommand createCreateContentCommand( String categoryName, String creatorUid, ContentStatus contentStatus )
    {
        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCategory( fixture.findCategoryByName( categoryName ) );
        createContentCommand.setCreator( fixture.findUserByName( creatorUid ).getKey() );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setStatus( contentStatus );
        createContentCommand.setPriority( 0 );
        createContentCommand.setContentName( "name_" + categoryName + "_" + contentStatus );

        ContentTypeConfig contentTypeConfig = fixture.findContentTypeByName( "Person" ).getContentTypeConfig();
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Initial" ) );
        createContentCommand.setContentData( contentData );
        return createContentCommand;
    }

    private UpdateContentCommand createUpdateExistingContentCommand( ContentKey contentKey, String modifier, ContentStatus contentStatus,
                                                                     ContentVersionKey versionKeyToUpdate )
    {
        UpdateContentCommand updateContentCommand = UpdateContentCommand.updateExistingVersion2( versionKeyToUpdate );
        updateContentCommand.setContentKey( contentKey );
        updateContentCommand.setModifier( fixture.findUserByName( modifier ) );
        updateContentCommand.setStatus( contentStatus );

        ContentTypeConfig contentTypeConfig = fixture.findContentTypeByName( "Person" ).getContentTypeConfig();
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Changed" ) );
        updateContentCommand.setContentData( contentData );
        return updateContentCommand;
    }

    private UpdateContentCommand createCreateNewVersionCommand( ContentKey contentKey, String modifier, ContentStatus contentStatus,
                                                                ContentVersionKey versionKeyToBaseOn )
    {
        UpdateContentCommand updateContentCommand = UpdateContentCommand.storeNewVersionEvenIfUnchanged( versionKeyToBaseOn );
        updateContentCommand.setContentKey( contentKey );
        updateContentCommand.setModifier( fixture.findUserByName( modifier ) );
        updateContentCommand.setStatus( contentStatus );

        ContentTypeConfig contentTypeConfig = fixture.findContentTypeByName( "Person" ).getContentTypeConfig();
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        contentData.add( new TextDataEntry( contentTypeConfig.getInputConfig( "name" ), "Changed" ) );
        updateContentCommand.setContentData( contentData );
        return updateContentCommand;
    }

    private void createAndSaveNormalUser( String uid, String userstoreName )
    {
        GroupEntity userGroup = factory.createGroupInUserstore( uid + "_group", GroupType.USERSTORE_GROUP, userstoreName );

        fixture.save( userGroup );

        UserEntity user = factory.createUser( uid, uid, UserType.NORMAL, userstoreName, userGroup );

        fixture.save( user );

        fixture.flushAndClearHibernateSesssion();
    }

    private void createAndStoreCategory( String categoryName )
    {
        createAndStoreCategory( categoryName, false );
    }

    private void createAndStoreCategory( String categoryName, boolean autoApprove )
    {
        fixture.save(
            factory.createCategory( categoryName, "Person", "UnitForPerson", User.ANONYMOUS_UID, User.ANONYMOUS_UID, autoApprove ) );

        fixture.flushAndClearHibernateSesssion();
    }
}
