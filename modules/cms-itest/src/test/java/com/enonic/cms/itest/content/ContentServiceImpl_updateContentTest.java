/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.content;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentEntity;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.ContentNameValidator;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.ContentStatus;
import com.enonic.cms.core.content.ContentVersionKey;
import com.enonic.cms.core.content.UpdateContentException;
import com.enonic.cms.core.content.UpdateContentResult;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.UpdateContentCommand;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.itest.DomainFactory;
import com.enonic.cms.itest.DomainFixture;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.store.dao.GroupEntityDao;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_updateContentTest
{

    @Inject
    private HibernateTemplate hibernateTemplate;

    @Inject
    private GroupEntityDao groupEntityDao;

    @Inject
    private ContentDao contentDao;

    @Inject
    private ContentService contentService;

    @Inject
    private ContentVersionDao contentVersionDao;

    private DomainFactory factory;

    private DomainFixture fixture;

    private Element standardConfigEl;

    private Document standardConfig;

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
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        fixture.save( factory.createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), standardConfig ) );
        fixture.save( factory.createUnit( "MyUnit" ) );
        fixture.save( factory.createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );

        fixture.save( factory.createCategoryAccessForUser( "MyCategory", "testuser", "read, create, approve" ) );

        fixture.flushAndClearHibernateSesssion();
    }

    private CreateContentCommand createCreateContentCommand( Integer status, UserEntity runningUser )
    {
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
        createContentCommand.setContentName( "testcontent" );

        return createContentCommand;
    }

    private UpdateContentCommand createUpdateContentCommand( ContentKey contentKey, ContentVersionKey versionKey, Integer status,
                                                             boolean asMainVersion, boolean asNewVersion )
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
        return command;
    }

    @Test
    public void testNoTimestampUpdateOnContentUnchanged()
    {
        UserEntity testUser = fixture.findUserByName( "testuser" );

        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey(), testUser );

        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        Date originalTimestamp = persistedContent.getTimestamp();

        assertNotNull( originalTimestamp );

        UpdateContentCommand command =
            createUpdateContentCommand( contentKey, persistedContent.getDraftVersion().getKey(), ContentStatus.DRAFT.getKey(), false,
                                        false );

        UpdateContentResult result = contentService.updateContent( command );

        assertFalse( "No changes should have been done to content or version", result.isAnyChangesMade() );

        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );

        assertEquals( "Timestamp should be unchanged", originalTimestamp, persistedContent.getTimestamp() );
    }

    @Test
    public void testUpdateContentMaximumNameLength()
    {
        UserEntity testUser = fixture.findUserByName( "testuser" );
        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey(), testUser );
        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        UpdateContentCommand command =
            createUpdateContentCommand( contentKey, persistedContent.getDraftVersion().getKey(), ContentStatus.DRAFT.getKey(), false,
                                        false );
        String newName = StringUtils.repeat( "x", ContentNameValidator.CONTENT_NAME_MAX_LENGTH );
        command.setContentName( newName );

        UpdateContentResult result = contentService.updateContent( command );

        assertTrue( "Content should have been updated", result.isAnyChangesMade() );

        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );

        assertNotNull( persistedContent.getTimestamp() );
        assertEquals( "Content name should have been updated", persistedContent.getName(), newName );
    }

    @Test
    public void testUpdateContentNameTooLong()
    {
        UserEntity testUser = fixture.findUserByName( "testuser" );
        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey(), testUser );
        ContentKey contentKey = contentService.createContent( createCommand );

        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        UpdateContentCommand command =
            createUpdateContentCommand( contentKey, persistedContent.getDraftVersion().getKey(), ContentStatus.DRAFT.getKey(), false,
                                        false );
        String newName = StringUtils.repeat( "x", ContentNameValidator.CONTENT_NAME_MAX_LENGTH + 1 );
        command.setContentName( newName );

        try
        {
            contentService.updateContent( command );
            fail( "Expected exception" );
        }
        catch ( AssertionError e )
        {
            throw e;
        }
        catch ( Throwable e )
        {
            assertTrue( e instanceof UpdateContentException );
            assertTrue( e.getMessage().toLowerCase().contains( "too long" ) );
        }
    }

    @Test
    public void testUpdateDeletedContent()
    {
        UserEntity testUser = fixture.findUserByName( "testuser" );

        CreateContentCommand createCommand = createCreateContentCommand( ContentStatus.DRAFT.getKey(), testUser );
        ContentKey contentKey = contentService.createContent( createCommand );
        fixture.flushAndClearHibernateSesssion();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );

        contentService.deleteContent( fixture.findUserByName( "testuser" ), persistedContent );
        fixture.flushAndClearHibernateSesssion();

        persistedContent = contentDao.findByKey( contentKey );
        assertTrue( persistedContent.isDeleted() );

        UpdateContentCommand command =
            createUpdateContentCommand( contentKey, persistedContent.getDraftVersion().getKey(), ContentStatus.DRAFT.getKey(), false,
                                        false );

        String newName = "content-updated";
        command.setContentName( newName );

        try
        {
            contentService.updateContent( command );
            fail( "Expected exception" );
}
        catch ( AssertionError e )
        {
            throw e;
        }
        catch ( Throwable e )
        {
            assertTrue( e instanceof UpdateContentException );
            assertTrue( e.getMessage().toLowerCase().contains( "deleted" ) );
        }
    }

}
