/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;
import java.util.Date;

import com.enonic.cms.core.content.*;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
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

import com.enonic.cms.api.client.model.AssignContentParams;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.UnassignContentParams;
import com.enonic.cms.api.client.model.UpdateContentParams;
import com.enonic.cms.api.client.model.content.ContentDataInput;
import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.TextInput;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.itest.DomainFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentVersionEntity;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InternalClientImpl_AssignContentTest
{
    @Autowired
    private HibernateTemplate hibernateTemplate;

    private DomainFactory factory;

    private DomainFixture fixture;

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private ContentService contentService;

    @Autowired
    private ContentVersionDao contentVersionDao;

    @Autowired
    private InternalClient internalClient;

    private XMLBytes standardConfig;


    @Before
    public void before()
        throws IOException, JDOMException
    {
        fixture = new DomainFixture( hibernateTemplate );
        factory = new DomainFactory( fixture );

        fixture.initSystemData();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        createContentTypeXml();

        saveNeededEntities();

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

    }


    private void saveNeededEntities()
    {
        // prepare: save needed entities
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );
        fixture.flushAndClearHibernateSesssion();
    }

    @Test
    public void testUpdateContentDoNotChangeAssignment()
    {
        ContentKey contentKey = createUpdateContent();

        AssignContentCommand assignContentCommand = new AssignContentCommand();
        assignContentCommand.setAssignerKey( fixture.findUserByName( "testuser" ).getKey() );
        assignContentCommand.setAssigneeKey( fixture.findUserByName( "testuser" ).getKey() );
        assignContentCommand.setAssignmentDescription( "test assignment" );
        assignContentCommand.setAssignmentDueDate( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate() );
        assignContentCommand.setContentKey( contentKey );
        contentService.assignContent( assignContentCommand );

        ContentDataInput newContentData = new ContentDataInput( "MyContentType" );
        newContentData.add( new TextInput( "myTitle", "changedtitle" ) );

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        UpdateContentParams params = new UpdateContentParams();
        params.contentKey = contentKey.toInt();
        params.contentData = newContentData;
        params.publishFrom = new Date();
        params.publishTo = null;
        params.createNewVersion = false;
        params.status = ContentStatus.STATUS_DRAFT;
        int contentVersionKey = internalClient.updateContent( params );

        fixture.flushAndClearHibernateSesssion();

        ContentVersionEntity actualVersion = contentVersionDao.findByKey( new ContentVersionKey( contentVersionKey ) );
        ContentEntity persistedContent = contentDao.findByKey( actualVersion.getContent().getKey() );

        assertEquals( runningUser, persistedContent.getAssignee() );
        assertEquals( runningUser, persistedContent.getAssigner() );
        assertEquals( "test assignment", persistedContent.getAssignmentDescription() );
        assertEquals( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate(), persistedContent.getAssignmentDueDate() );
    }


    @Test
    public void testCreateAndAssignContent()
    {
        ContentDataInput contentData = new ContentDataInput( "MyContentType" );
        contentData.add( new TextInput( "myTitle", "testtitle" ) );

        CreateContentParams params = new CreateContentParams();
        params.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        params.contentData = contentData;
        params.publishFrom = new Date();
        params.publishTo = null;
        params.status = ContentStatus.STATUS_DRAFT;
        int contentKey = internalClient.createContent( params );

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        AssignContentParams assignContentParams = new AssignContentParams();
        assignContentParams.assignee = createClientUserQualifiedName( runningUser );
        assignContentParams.assignmentDescription = "test assignment";
        assignContentParams.assignmentDueDate = new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate();
        assignContentParams.contentKey = contentKey;
        internalClient.assignContent( assignContentParams );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( new ContentKey( contentKey ) );

        assertEquals( runningUser, persistedContent.getAssignee() );
        assertEquals( runningUser, persistedContent.getAssigner() );
        assertEquals( "test assignment", persistedContent.getAssignmentDescription() );
        assertEquals( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate(), persistedContent.getAssignmentDueDate() );
    }

    @Test
    public void testUnassignContent()
    {
        UserEntity testUser = fixture.findUserByName( "testuser" );

        ContentKey contentKey = createUpdateContent();

        AssignContentCommand assignContentCommand = new AssignContentCommand();

        assignContentCommand.setAssignerKey( testUser.getKey() );
        assignContentCommand.setAssigneeKey( testUser.getKey() );
        assignContentCommand.setAssignmentDescription( "test assignment" );
        assignContentCommand.setAssignmentDueDate( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate() );
        assignContentCommand.setContentKey( contentKey );
        contentService.assignContent( assignContentCommand );
        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        assertEquals( testUser, persistedContent.getAssignee() );
        assertEquals( testUser, persistedContent.getAssigner() );
        assertEquals( "test assignment", persistedContent.getAssignmentDescription() );
        assertEquals( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate(), persistedContent.getAssignmentDueDate() );

        UnassignContentParams unassignContentparams = new UnassignContentParams();
        unassignContentparams.contentKey = contentKey.toInt();

        internalClient.unassignContent( unassignContentparams );
        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );

        assertNull( persistedContent.getAssignee() );
        assertNull( persistedContent.getAssigner() );
        assertNull( persistedContent.getAssignmentDescription() );
        assertNull( persistedContent.getAssignmentDueDate() );

    }


    private ContentKey createUpdateContent()
    {
        UserEntity runningUser = fixture.findUserByName( "testuser" );

        // prepare: save a new content

        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );
        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "myTitle" );
        contentData.add( new TextDataEntry( titleConfig, "testitle" ) );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );
        createContentCommand.setAvailableFrom( new DateTime( 2010, 1, 1, 0, 0, 0, 0 ).toDate() );
        createContentCommand.setAccessRightsStrategy( CreateContentCommand.AccessRightsStrategy.USE_GIVEN );
        createContentCommand.setPriority( 0 );
        createContentCommand.setStatus( com.enonic.cms.core.content.ContentStatus.DRAFT );
        createContentCommand.setLanguage( fixture.findLanguageByCode( "en" ).getKey() );
        createContentCommand.setCategory( fixture.findCategoryByName( "MyCategory" ).getKey() );
        createContentCommand.setContentData( contentData );
        createContentCommand.setContentName( "testcontent" );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        return contentKey;
    }

    private void createContentTypeXml()
    {
        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );

        standardConfigXml.append( "         <title name=\"myTitle\"/>" );

        standardConfigXml.append( "         <block name=\"TestBlock1\">" );

        standardConfigXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() ).getAsBytes();
    }

    private String createClientUserQualifiedName( UserEntity user )
    {
        return user.getUserStoreKey().toString() + ":" + user.getUserGroup().getName();
    }


}