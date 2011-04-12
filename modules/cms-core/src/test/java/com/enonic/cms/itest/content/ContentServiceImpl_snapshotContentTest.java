/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.enonic.cms.core.content.*;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Assert;
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
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import com.enonic.cms.core.content.command.SnapshotContentCommand;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: May 21, 2010
 * Time: 9:20:23 AM
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_snapshotContentTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    @Autowired
    private GroupEntityDao groupEntityDao;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentVersionDao contentVersionDao;

    private DomainFactory factory;

    private DomainFixture fixture;

    private Element standardConfigEl;

    private XMLBytes standardConfig;

    @Before
    public void setUp()
        throws IOException, JDOMException
    {
        groupEntityDao.invalidateCachedKeys();

        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

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

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );

        setupReadCreateAndApproveUsers();

        fixture.flushAndClearHibernateSesssion();
    }

    private void setupReadCreateAndApproveUsers()
    {
        fixture.createAndStoreNormalUserWithUserGroup( "userWithRead", "userWithRead", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "userWithCreate", "userWithCreate", "testuserstore" );
        fixture.createAndStoreNormalUserWithUserGroup( "userWithApprove", "userWithApprove", "testuserstore" );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "userWithRead", "read" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "userWithCreate", "read, create" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "userWithApprove", "read, create, approve" ) );
    }


    private ContentKey createContent( Integer status )
    {
        CreateContentCommand createContentCommand = createCreateContentCommand( status );

        return contentService.createContent( createContentCommand );
    }

    private CreateContentCommand createCreateContentCommand( Integer status )
    {
        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentTypeEntity contentType = fixture.findContentTypeByName( "MyContentType" );
        CustomContentData contentData = new CustomContentData( contentType.getContentTypeConfig() );
        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "Tittel", "contentdata/mytitle" );
        TextDataEntryConfig subElementConfig =
            new TextDataEntryConfig( "myTitleInSubElement", false, "My title in sub element", "contentdata/subelement/mytitle" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );
        contentData.add( new TextDataEntry( subElementConfig, "test subtitle" ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ) );
        createContentCommand.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( ContentStatus.get( status ) );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testcontent_" + status );

        return createContentCommand;
    }

    private UpdateContentResult updateContent( ContentKey contentKey, ContentVersionKey versionKey, Integer status, boolean asMainVersion,
                                               boolean asNewVersion )
    {
        UpdateContentCommand command;
        if ( asNewVersion )
        {
            command = UpdateContentCommand.storeNewVersionEvenIfUnchanged( versionKey );
        }
        else
        {
            command = UpdateContentCommand.updateExistingVersion2( versionKey );
        }

        command.setModifier( fixture.findUserByName( "testuser" ) );
        command.setUpdateAsMainVersion( asMainVersion );

        // Populate command with contentEntity data
        command.setLanguage( fixture.findLanguageByCode( "en" ) );
        command.setStatus( ContentStatus.get( status ) );
        command.setContentKey( contentKey );

        return contentService.updateContent( command );
    }

    private SnapshotContentResult createContentVersionSnapshot( ContentEntity content )
    {
        SnapshotContentCommand command = new SnapshotContentCommand();
        command.setContentKey( content.getKey() );
        command.setSnapshotterKey( fixture.findUserByName( "testuser" ).getKey() );

        return contentService.snapshotContent( command );
    }

    @Test(expected = SnapshotContentException.class)
    public void testSnapshotContentAccessRights_NotAllowedForReadOnlyUser()
    {
        UserEntity readUser = fixture.findUserByName( "userWithRead" );
        UserEntity createUser = fixture.findUserByName( "userWithCreate" );

        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey() );
        createCommand.setCreator( createUser );
        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( persistedContent.getKey() );
        snapshotCommand.setSnapshotterKey( readUser.getKey() );

        contentService.snapshotContent( snapshotCommand );
    }

    @Test
    public void testSnapshotContentAccessRights_AllowedForCreateAccessUser()
    {
        UserEntity createUser = fixture.findUserByName( "userWithCreate" );

        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey() );
        createCommand.setCreator( createUser );
        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( persistedContent.getKey() );
        snapshotCommand.setSnapshotterKey( createUser.getKey() );

        contentService.snapshotContent( snapshotCommand );
    }


    @Test
    public void testSnapshotContentAccessRights_AllowedForApproveAccessUser()
    {
        UserEntity createUser = fixture.findUserByName( "userWithCreate" );
        UserEntity approveUser = fixture.findUserByName( "userWithApprove" );

        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey() );
        createCommand.setCreator( createUser );
        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( persistedContent.getKey() );
        snapshotCommand.setSnapshotterKey( approveUser.getKey() );

        contentService.snapshotContent( snapshotCommand );
    }


    @Test
    public void testContentTimestampUpdatedOnSnapshot()
    {
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        fixture.flushAndClearHibernateSesssion();

        Date startTime = Calendar.getInstance().getTime();

        ContentEntity originalContent = contentDao.findByKey( contentKey );

        SnapshotContentResult result = createContentVersionSnapshot( originalContent );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity snapshottedContent = contentDao.findByKey( contentKey );

        assertNotNull( snapshottedContent.getTimestamp() );
        Assert.assertTrue( snapshottedContent.getTimestamp().compareTo( startTime ) > 0 );
    }

    @Test
    public void testCreateSnapshot()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity originalContent = contentDao.findByKey( contentKey );

        assertNotNull( originalContent.getDraftVersion() );

        SnapshotContentResult result = createContentVersionSnapshot( originalContent );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity snapshottedContent = contentDao.findByKey( contentKey );

        assertNotNull( snapshottedContent );
        assertEquals( "Should have 2 versions", 2, snapshottedContent.getVersionCount() );

        ContentVersionEntity draftVersion = snapshottedContent.getDraftVersion();
        assertNotNull( "Draft should exist", draftVersion );

        Set<ContentVersionEntity> snapshots = draftVersion.getSnapshots();
        assertNotNull( "Draft should have snapshot", snapshots );
        assertTrue( "Draft should have 1 snapshot", snapshots.size() == 1 );

        assertTrue( "Draft should be in snapshot-list of draft", snapshots.contains( result.getStoredSnapshotContentVersion() ) );

        ContentVersionEntity persistedSnapshot = contentVersionDao.findByKey( result.getStoredSnapshotContentVersion().getKey() );

        assertNotNull( "Snapshot should be persisted", persistedSnapshot );
        assertTrue( "Snapshot should point to draft as source", persistedSnapshot.getSnapshotSource().equals( draftVersion ) );

        assertNotSame( originalContent, snapshottedContent );
        assertEquals( originalContent.getAvailableFrom(), snapshottedContent.getAvailableFrom() );
        assertEquals( originalContent.getAvailableTo(), snapshottedContent.getAvailableTo() );
        assertEquals( originalContent.getAssignee(), snapshottedContent.getAssignee() );
        assertEquals( originalContent.getLanguage().getCode(), snapshottedContent.getLanguage().getCode() );
        assertEquals( originalContent.getCategory(), snapshottedContent.getCategory() );
        assertEquals( originalContent.getCreatedAt(), snapshottedContent.getCreatedAt() );
        assertEquals( originalContent.getAssignmentDueDate(), snapshottedContent.getAssignmentDueDate() );
        assertEquals( originalContent.getOwner(), snapshottedContent.getOwner() );
        assertEquals( originalContent.getName(), snapshottedContent.getName() );
    }

    @Test
    public void testApproveVersionWithSnapshot()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent.getDraftVersion() );

        SnapshotContentResult result = createContentVersionSnapshot( persistedContent );

        ContentVersionEntity snapshot = result.getStoredSnapshotContentVersion();

        ContentVersionKey snapshotVersionKey = snapshot.getKey();

        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent );
        assertEquals( "Should have 2 versions", 2, persistedContent.getVersionCount() );

        UpdateContentResult updateResult =
            updateContent( contentKey, persistedContent.getDraftVersion().getKey(), ContentStatus.APPROVED.getKey(), true, false );

        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );
        ContentVersionEntity persistedSnapshot = contentVersionDao.findByKey( snapshotVersionKey );

        assertNotNull( "Snapshot should exist", persistedSnapshot );
        assertEquals( "Status should be 'SNAPSHOT' for the snapshotter version", persistedSnapshot.getStatus(), ContentStatus.SNAPSHOT );
        assertNotNull( "Snapshot should have relation to parent", persistedSnapshot.getSnapshotSource() );
        assertTrue( "Content should be maked as changed", updateResult.isAnyChangesMade() );
    }

    @Test
    public void testDeleteDraftAndSnapshots()
    {
        ContentKey contentKey = createContent( ContentStatus.APPROVED.getKey() );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        UpdateContentResult updateResult =
            updateContent( contentKey, persistedContent.getMainVersion().getKey(), ContentStatus.DRAFT.getKey(), false, true );

        persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent.getDraftVersion() );

        createContentVersionSnapshot( persistedContent );
        createContentVersionSnapshot( persistedContent );
        createContentVersionSnapshot( persistedContent );

        persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent.getDraftVersion() );
        assertEquals( 5, persistedContent.getVersionCount() );
        assertNotNull( "Draft should not be null", persistedContent.getDraftVersion() );
        assertNotNull( "Snapshots should not be null", persistedContent.getDraftVersion().getSnapshots() );
        assertEquals( "Draft should have 3 snapshots", 3, persistedContent.getDraftVersion().getSnapshots().size() );
        assertEquals( "Should have 5 versions", 5, fixture.countContentVersionsByTitle( "test title" ) );

        contentService.deleteVersion( fixture.findUserByName( "admin" ), updateResult.getTargetedVersionKey() );

        persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent );
        assertEquals( 1, persistedContent.getVersionCount() );
        assertEquals( 1, fixture.countContentVersionsByTitle( "test title" ) );
        assertEquals( ContentStatus.APPROVED, persistedContent.getMainVersion().getStatus() );
    }

    @Test
    public void testCommentFlow()
    {
        CreateContentCommand createContentCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey() );

        createContentCommand.setChangeComment( "Initial comment" );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionEntity persistedVersion = persistedContent.getDraftVersion();

        assertEquals( "Initial comment", persistedVersion.getChangeComment() );

        SnapshotContentCommand snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( persistedContent.getKey() );
        snapshotCommand.setClearCommentInDraft( false );
        snapshotCommand.setSnapshotterKey( fixture.findUserByName( "testuser" ).getKey() );

        persistedVersion = persistedContent.getDraftVersion();
        assertEquals( "Comment should not be cleared from draft when clearCommentInDraftIsFalse", "Initial comment",
                      persistedVersion.getChangeComment() );

        snapshotCommand = new SnapshotContentCommand();
        snapshotCommand.setContentKey( persistedContent.getKey() );
        snapshotCommand.setClearCommentInDraft( true );
        snapshotCommand.setSnapshotterKey( fixture.findUserByName( "testuser" ).getKey() );
        snapshotCommand.setSnapshotComment( "Snapshot comment" );

        SnapshotContentResult snapshotResult = contentService.snapshotContent( snapshotCommand );

        ContentVersionEntity storedSnapshotVersion = snapshotResult.getStoredSnapshotContentVersion();

        persistedContent = contentDao.findByKey( contentKey );

        assertNull( "Comment should be cleared from draft", persistedContent.getDraftVersion().getChangeComment() );

        boolean found = false;
        for ( ContentVersionEntity contentVersion : persistedContent.getVersions() )
        {
            if ( contentVersion.equals( storedSnapshotVersion ) )
            {
                found = true;
                assertEquals( "Snapshot comment", contentVersion.getChangeComment() );
                break;
            }
        }

        assertTrue( found );
    }

}