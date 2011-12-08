/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.AbstractSpringTest;
import com.enonic.cms.itest.util.DomainFactory;
import com.enonic.cms.itest.util.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;

import static org.junit.Assert.*;

public class ContentServiceImpl_updateContentStatusTest
    extends AbstractSpringTest
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

    @Autowired
    private DomainFixture fixture;

    private Element standardConfigEl;

    private Document standardConfig;

    @Before
    public void before()
        throws IOException, JDOMException
    {

        factory = fixture.getFactory();

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
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() ).getAsJDOMDocument();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        hibernateTemplate.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        hibernateTemplate.save(
            factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        hibernateTemplate.save( factory.createUnit( "MyUnit" ) );
        hibernateTemplate.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        hibernateTemplate.save(
            factory.createCategoryAccess( "MyCategory", fixture.findUserByName( "testuser" ), "read, create, approve" ) );
        hibernateTemplate.flush();
        hibernateTemplate.clear();

    }


    private ContentKey createContent( Integer status )
    {

        UserEntity runningUser = fixture.findUserByName( "testuser" );

        ContentEntity content = new ContentEntity();
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );
        content.setPriority( 0 );
        content.setName( "testcontent" );

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

    private UpdateContentResult updateContent( ContentKey contentKey, ContentVersionKey versionKey, Integer status, boolean asMainVersion,
                                               boolean asNewVersion )
    {

        ContentEntity content = new ContentEntity();
        content.setKey( contentKey );
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setOwner( fixture.findUserByName( "testuser" ) );

        ContentVersionEntity version = new ContentVersionEntity();
        if ( versionKey != null )
        {
            version.setKey( versionKey );
        }
        version.setStatus( ContentStatus.get( status ) );
        version.setContent( content );

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
        command.populateContentValuesFromContent( content );
        command.populateContentVersionValuesFromContentVersion( version );

        return contentService.updateContent( command );
    }

    @Test
    public void testContentTimestampUpdatedOnStatusChange()
    {

        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        Date startTime = Calendar.getInstance().getTime();
        // excersise
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.APPROVED.getKey(), true, false );

        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );
        assertNotNull( "no persisted content found", persistedContent );

        assertNotNull( persistedContent.getTimestamp() );

        // on fast machines sometimes persistedContent.getTimestamp() == startTime
        assertTrue( persistedContent.getTimestamp().compareTo( startTime ) >= 0 );
    }


    @Test
    public void testDraftRelationOnDraftToApproveUpdate()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( "no persisted content found", persistedContent );
        assertNotNull( "A relation to draft-version should exist", persistedContent.getDraftVersion() );

        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.APPROVED.getKey(), true, false );

        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );
        assertNotNull( "no persisted content found", persistedContent );

        // verify
        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.APPROVED.getKey() );
        assertNull( "A relation to draft-version should not exist", persistedContent.getDraftVersion() );

    }

    @Test
    public void testDraftRelationOnApprovedToNewDraft()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.APPROVED.getKey() );
        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.DRAFT.getKey(), true, true );

        // verify
        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );

        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.DRAFT.getKey() );

        assertNotNull( "no persisted content found", persistedContent );
        assertNotNull( "Should have draft-relation", persistedContent.getDraftVersion() );
        assertEquals( "The new persisted version should be the draft-relation", persistedContent.getDraftVersion(),
                      updateResult.getTargetedVersion() );

    }


    @Test
    public void testDraftRelationOnArchivedToNewDraft()
    {

        // setup
        ContentKey contentKey = createContent( ContentStatus.ARCHIVED.getKey() );
        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.DRAFT.getKey(), true, true );

        // verify
        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );
        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.DRAFT.getKey() );

        assertNotNull( "no persisted content found", persistedContent );
        assertNotNull( "Should have draft-relation", persistedContent.getDraftVersion() );
        assertEquals( "The new persisted version should be the draft-relation", persistedContent.getDraftVersion(),
                      updateResult.getTargetedVersion() );

    }

    @Test
    public void testDraftRelationOnApprovedToArchived()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.APPROVED.getKey() );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( "no persisted content found", persistedContent );
        assertNull( "Should not have draft-relation before update", persistedContent.getDraftVersion() );

        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.ARCHIVED.getKey(), true, true );

        // verify
        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );
        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.ARCHIVED.getKey() );

        assertNotNull( "no persisted content found", persistedContent );
        assertNull( "Should not have draft-relation after update", persistedContent.getDraftVersion() );

    }

    @Test
    public void testDraftRelationWithDraftAndApprovedToArchived()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( "no persisted content found", persistedContent );
        assertNotNull( "Should have draft-relation before update", persistedContent.getDraftVersion() );

        ContentVersionKey draftVersionKey = persistedContent.getDraftVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, draftVersionKey, ContentStatus.APPROVED.getKey(), true, true );

        // verify
        hibernateTemplate.clear();

        persistedContent = contentDao.findByKey( contentKey );

        assertEquals( "Should have 2 versions", persistedContent.getVersionCount(), 2 );

        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.APPROVED.getKey() );

        assertNotNull( "no persisted content found", persistedContent );
        assertNull( "Should not have draft-relation, one should be archived, one approved", persistedContent.getDraftVersion() );

    }


    @Test
    public void testDraftToApproved()
    {
        Date startDate = Calendar.getInstance().getTime();
        // setup
        ContentKey contentKey = createContent( ContentStatus.DRAFT.getKey() );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.APPROVED.getKey(), true, false );

        // verify
        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.APPROVED.getKey() );
    }

    @Test
    public void testAprovedToArchived()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.APPROVED.getKey() );

        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.ARCHIVED.getKey(), true, false );

        // verify
        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.ARCHIVED.getKey() );
    }

    @Test
    public void testArchivedToApproved()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.ARCHIVED.getKey() );
        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.APPROVED.getKey(), true, false );

        // verify
        hibernateTemplate.clear();

        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.APPROVED.getKey() );
    }

    @Test
    public void testWorkflow_ApprovedToDraftNotAllowed()
    {
        // setup
        ContentKey contentKey = createContent( ContentStatus.APPROVED.getKey() );
        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        ContentVersionKey versionKey = persistedContent.getMainVersion().getKey();

        // excersise
        hibernateTemplate.clear();
        UpdateContentResult updateResult = updateContent( contentKey, versionKey, ContentStatus.DRAFT.getKey(), true, false );

        // verify
        hibernateTemplate.clear();

        ContentVersionEntity version = contentVersionDao.findByKey( updateResult.getTargetedVersionKey() );
        assertEquals( version.getStatus().getKey(), ContentStatus.DRAFT.getKey() );
    }

}