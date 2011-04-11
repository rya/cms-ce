/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.itest.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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

import com.enonic.cms.api.client.model.ContentDataInputUpdateStrategy;
import com.enonic.cms.api.client.model.CreateContentParams;
import com.enonic.cms.api.client.model.GetContentParams;
import com.enonic.cms.api.client.model.UpdateContentParams;
import com.enonic.cms.api.client.model.content.BinaryInput;
import com.enonic.cms.api.client.model.content.ContentDataInput;
import com.enonic.cms.api.client.model.content.ContentStatus;
import com.enonic.cms.api.client.model.content.GroupInput;
import com.enonic.cms.api.client.model.content.TextInput;
import com.enonic.cms.core.client.InternalClient;
import com.enonic.cms.core.content.ContentService;
import com.enonic.cms.core.content.command.AssignContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand;
import com.enonic.cms.core.content.command.CreateContentCommand.AccessRightsStrategy;
import com.enonic.cms.core.security.SecurityHolder;
import com.enonic.cms.core.servlet.ServletRequestAccessor;
import com.enonic.cms.store.dao.ContentDao;
import com.enonic.cms.store.dao.ContentVersionDao;
import com.enonic.cms.testtools.DomainFactory;
import com.enonic.cms.testtools.DomainFixture;

import com.enonic.cms.domain.content.ContentEntity;
import com.enonic.cms.domain.content.ContentHandlerName;
import com.enonic.cms.domain.content.ContentKey;
import com.enonic.cms.domain.content.ContentVersionEntity;
import com.enonic.cms.domain.content.ContentVersionKey;
import com.enonic.cms.domain.content.binary.BinaryDataAndBinary;
import com.enonic.cms.domain.content.contentdata.custom.BinaryDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.BlockGroupDataEntries;
import com.enonic.cms.domain.content.contentdata.custom.CustomContentData;
import com.enonic.cms.domain.content.contentdata.custom.GroupDataEntry;
import com.enonic.cms.domain.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.domain.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.BinaryDataEntryConfig;
import com.enonic.cms.domain.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.domain.security.user.UserEntity;
import com.enonic.cms.domain.security.user.UserType;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class InternalClientImpl_UpdateContentTest
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

    private byte[] dummyBinary = new byte[]{1, 2, 3};

    private ContentKey updateContentKey;

    private ContentKey contentWithBinaryKey;

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

        createUpdateContent();
        createUpdateContentWithBinary();

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

        standardConfigXml.append( "             <input name=\"fieldToUpdate\" required=\"false\" type=\"text\">" );
        standardConfigXml.append( "                 <display>Update Field</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/updatefield</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );
        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        // standardConfig = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();
        standardConfig = XMLDocumentFactory.create( standardConfigXml.toString() ).getAsBytes();
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

    private void createUpdateContentWithBinary()
    {
        UserEntity runningUser = fixture.findUserByName( "testuser" );

        // prepare: save a new content
        ContentEntity content = new ContentEntity();
        content.setPriority( 0 );
        content.setAvailableFrom( null );
        content.setAvailableTo( null );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setName( "testcontentwithbinary" );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setContent( content );
        version.setModifiedBy( runningUser );
        version.setStatus( com.enonic.cms.domain.content.ContentStatus.DRAFT );
        version.setContent( content );
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );

        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "myTitle" );
        TextDataEntryConfig updateFieldConfig =
            (TextDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "fieldToUpdate" );
        BinaryDataEntryConfig binaryConfig =
            (BinaryDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "myBinaryfile" );

        contentData.add( new TextDataEntry( titleConfig, "testitle" ) );
        contentData.add( new TextDataEntry( updateFieldConfig, "foobar" ) );
        contentData.add( new BinaryDataEntry( binaryConfig, "%0" ) );

        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        List<BinaryDataAndBinary> binaryDatas = new ArrayList<BinaryDataAndBinary>();
        binaryDatas.add( factory.createBinaryDataAndBinary( "dummyBinary", dummyBinary ) );

        createContentCommand.setBinaryDatas( binaryDatas );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        contentWithBinaryKey = contentService.createContent( createContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();
    }

    @Test
    public void testUpdateContentWithBinary()
    {
        // exercise: updateContent

        ContentDataInput newContentData = new ContentDataInput( "MyContentType" );
        newContentData.add( new TextInput( "myTitle", "changedtitle" ) );
        newContentData.add( new BinaryInput( "myBinaryfile", dummyBinary, "dummyBinary" ) );

        UpdateContentParams params = new UpdateContentParams();
        params.contentKey = contentWithBinaryKey.toInt();
        params.contentData = newContentData;
        params.publishFrom = new Date();
        params.publishTo = null;
        params.createNewVersion = false;
        params.status = ContentStatus.STATUS_DRAFT;
        int contentVersionKey = internalClient.updateContent( params );

        ContentVersionEntity actualVersion = contentVersionDao.findByKey( new ContentVersionKey( contentVersionKey ) );
        assertEquals( com.enonic.cms.domain.content.ContentStatus.DRAFT.getKey(), actualVersion.getStatus().getKey() );
        assertEquals( "changedtitle", actualVersion.getTitle() );
        assertEquals( "changedtitle", actualVersion.getContentData().getTitle() );
    }

    @Test
    public void testUpdateContentWithReplaceAll_WithMissingValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_ALL;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fields are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with missing field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // get updated content
        xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertFalse( "XML skal ikke inneholde <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );
    }

    @Test
    public void testUpdateContentWithReplaceAll_WithBlankValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_ALL;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fields are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with empty field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        cdi.add( new TextInput( "fieldToUpdate", "" ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // verify
        ContentEntity content = contentDao.findByKey( updateContentKey );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData customContentData = (CustomContentData) version.getContentData();
        TextDataEntry myTitle = (TextDataEntry) customContentData.getEntry( "myTitle" );
        assertEquals( "updateTest", myTitle.getValue() );
        TextDataEntry fieldToUpdate = (TextDataEntry) customContentData.getEntry( "fieldToUpdate" );
        assertEquals( "", fieldToUpdate.getValue() );
    }

    @Test
    public void testUpdateContentWithReplaceAll_WithNullValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_ALL;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fields are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with empty field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        cdi.add( new TextInput( "fieldToUpdate", null ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // verify

        ContentEntity content = contentDao.findByKey( updateContentKey );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData customContentData = (CustomContentData) version.getContentData();
        TextDataEntry myTitle = (TextDataEntry) customContentData.getEntry( "myTitle" );
        assertEquals( "updateTest", myTitle.getValue() );
        TextDataEntry fieldToUpdate = (TextDataEntry) customContentData.getEntry( "fieldToUpdate" );
        assertFalse( fieldToUpdate.hasValue() );
        assertEquals( null, fieldToUpdate.getValue() );
    }

    @Test
    public void testUpdateContentWithReplaceNew_WithMissingValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_NEW;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fiels are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with missing field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // get updated content
        xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );
    }

    @Test
    public void testUpdateContentWithReplaceNew_WithBlankValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_NEW;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fiels are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with missing field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        cdi.add( new TextInput( "fieldToUpdate", "" ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // verify
        ContentEntity content = contentDao.findByKey( updateContentKey );
        ContentVersionEntity version = content.getMainVersion();
        CustomContentData customContentData = (CustomContentData) version.getContentData();
        TextDataEntry myTitle = (TextDataEntry) customContentData.getEntry( "myTitle" );
        assertEquals( "updateTest", myTitle.getValue() );
        TextDataEntry fieldToUpdate = (TextDataEntry) customContentData.getEntry( "fieldToUpdate" );
        assertTrue( fieldToUpdate.hasValue() );
        assertEquals( "", fieldToUpdate.getValue() );
    }

    @Test
    public void testUpdateContentWithReplaceNew_WithNullValue()
    {
        ContentDataInput cdi = getContentData( true );
        UpdateContentParams upcd = getUpdateContentParams( cdi );
        upcd.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_NEW;

        // update content with all fields set
        internalClient.updateContent( upcd );

        // get updated content - make sure all fiels are set
        String xml = getUpdatedContentXMLWithDao();
        assertTrue( "XML inneholder ikke <mytitle>updateTest</mytitle>", xml.contains( "<mytitle>updateTest</mytitle>" ) );
        assertTrue( "XML inneholder ikke <updatefield>foobar</updatefield>", xml.contains( "<updatefield>foobar</updatefield>" ) );

        // update content with missing field
        // update content with missing field
        cdi = new ContentDataInput( "MyContentType" );
        cdi.add( new TextInput( "myTitle", "updateTest" ) );
        cdi.add( new TextInput( "fieldToUpdate", null ) );
        upcd.contentData = cdi;
        internalClient.updateContent( upcd );

        // get updated content
        ContentEntity entity = contentDao.findByKey( updateContentKey );
        ContentVersionEntity version = entity.getMainVersion();
        CustomContentData customContentData = (CustomContentData) version.getContentData();
        TextDataEntry myTitle = (TextDataEntry) customContentData.getEntry( "myTitle" );
        assertEquals( "updateTest", myTitle.getValue() );
        TextDataEntry fieldToUpdate = (TextDataEntry) customContentData.getEntry( "fieldToUpdate" );
        assertFalse( fieldToUpdate.hasValue() );
    }

    @Test
    public void testUpdateContentWithBlockGroup()
    {
        fixture.createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );

        fixture.save( factory.createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );

        // setup content type
        ContentTypeConfigBuilder ctyconf = new ContentTypeConfigBuilder( "Skole", "tittel" );
        ctyconf.startBlock( "Skole" );
        ctyconf.addInput( "tittel", "text", "contentdata/tittel", "Tittel", true );
        ctyconf.endBlock();
        ctyconf.startBlock( "Elever", "contentdata/elever" );
        ctyconf.addInput( "elev-navn", "text", "navn", "Navn" );
        ctyconf.addInput( "elev-karakter", "text", "karakter", "Karakter" );
        ctyconf.endBlock();
        ctyconf.startBlock( "Laerere", "contentdata/laerere" );
        ctyconf.addInput( "laerer-navn", "text", "navn", "Navn" );
        ctyconf.addInput( "laerer-karakter", "text", "karakter", "Karakter" );
        ctyconf.endBlock();
        XMLBytes configAsXmlBytes = XMLDocumentFactory.create( ctyconf.toString() ).getAsBytes();
        fixture.save( factory.createContentType( "Skole", ContentHandlerName.CUSTOM.getHandlerClassShortName(), configAsXmlBytes ) );

        fixture.save( factory.createUnit( "MyUnit", "en" ) );
        fixture.save( factory.createCategory( "Skole", "Skole", "MyUnit", "testuser", "testuser" ) );
        fixture.save( factory.createCategoryAccessForUser( "Skole", "testuser", "read,create,approve" ) );

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        CreateContentParams createParams = new CreateContentParams();
        createParams.categoryKey = fixture.findCategoryByName( "Skole" ).getKey().toInt();
        createParams.publishFrom = new Date();
        createParams.status = ContentStatus.STATUS_DRAFT;

        ContentDataInput contentData = new ContentDataInput( "Skole" );
        contentData.add( new TextInput( "tittel", "St. Olav Videregaende skole" ) );

        GroupInput groupInputElev1 = contentData.addGroup( "Elever" );
        groupInputElev1.add( new TextInput( "elev-navn", "Vegar Jansen" ) );
        groupInputElev1.add( new TextInput( "elev-karakter", "S" ) );

        GroupInput groupInputElev2 = contentData.addGroup( "Elever" );
        groupInputElev2.add( new TextInput( "elev-navn", "Thomas Sigdestad" ) );
        groupInputElev2.add( new TextInput( "elev-karakter", "M" ) );

        GroupInput groupInputLaerer1 = contentData.addGroup( "Laerere" );
        groupInputLaerer1.add( new TextInput( "laerer-navn", "Mutt Hansen" ) );
        groupInputLaerer1.add( new TextInput( "laerer-karakter", "LG" ) );

        GroupInput groupInputLaerer2 = contentData.addGroup( "Laerere" );
        groupInputLaerer2.add( new TextInput( "laerer-navn", "Striks Jansen" ) );
        groupInputLaerer2.add( new TextInput( "laerer-karakter", "M" ) );

        createParams.contentData = contentData;
        ContentKey contentKey = new ContentKey( internalClient.createContent( createParams ) );

        UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.contentKey = contentKey.toInt();
        updateParams.createNewVersion = false;
        updateParams.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_ALL;
        contentData = new ContentDataInput( "Skole" );
        contentData.add( new TextInput( "tittel", "St. Olav Videregaende skole" ) );

        groupInputElev1 = contentData.addGroup( "Elever" );
        groupInputElev1.add( new TextInput( "elev-navn", "Vegar Jansen" ) );
        groupInputElev1.add( new TextInput( "elev-karakter", "G" ) );

        groupInputElev2 = contentData.addGroup( "Elever" );
        groupInputElev2.add( new TextInput( "elev-navn", "Thomas Sigdestad" ) );
        groupInputElev2.add( new TextInput( "elev-karakter", "S" ) );

        groupInputLaerer1 = contentData.addGroup( "Laerere" );
        groupInputLaerer1.add( new TextInput( "laerer-navn", "Mutt Hansen" ) );
        groupInputLaerer1.add( new TextInput( "laerer-karakter", "S" ) );

        groupInputLaerer2 = contentData.addGroup( "Laerere" );
        groupInputLaerer2.add( new TextInput( "laerer-navn", "Striks Jansen" ) );
        groupInputLaerer2.add( new TextInput( "laerer-karakter", "NG" ) );

        updateParams.contentData = contentData;
        internalClient.updateContent( updateParams );

        ContentEntity updatedContent = fixture.findContentByKey( contentKey );
        ContentVersionEntity updatedVersion = updatedContent.getMainVersion();
        CustomContentData updatedContentData = (CustomContentData) updatedVersion.getContentData();
        BlockGroupDataEntries elever = updatedContentData.getBlockGroupDataEntries( "Elever" );

        GroupDataEntry elev1 = elever.getGroupDataEntry( 1 );
        assertEquals( "Vegar Jansen", ( (TextDataEntry) elev1.getEntry( "elev-navn" ) ).getValue() );
        assertEquals( "G", ( (TextDataEntry) elev1.getEntry( "elev-karakter" ) ).getValue() );

        GroupDataEntry elev2 = elever.getGroupDataEntry( 2 );
        assertEquals( "Thomas Sigdestad", ( (TextDataEntry) elev2.getEntry( "elev-navn" ) ).getValue() );
        assertEquals( "S", ( (TextDataEntry) elev2.getEntry( "elev-karakter" ) ).getValue() );

        BlockGroupDataEntries laerere = updatedContentData.getBlockGroupDataEntries( "Laerere" );

        GroupDataEntry laerer1 = laerere.getGroupDataEntry( 1 );
        assertEquals( "Mutt Hansen", ( (TextDataEntry) laerer1.getEntry( "laerer-navn" ) ).getValue() );
        assertEquals( "S", ( (TextDataEntry) laerer1.getEntry( "laerer-karakter" ) ).getValue() );

        GroupDataEntry laerer2 = laerere.getGroupDataEntry( 2 );
        assertEquals( "Striks Jansen", ( (TextDataEntry) laerer2.getEntry( "laerer-navn" ) ).getValue() );
        assertEquals( "NG", ( (TextDataEntry) laerer2.getEntry( "laerer-karakter" ) ).getValue() );
    }


    @Test
    public void update_existing_version_with_publishFrom_without_contentdata_changes_only_publishFrom()
    {
        CreateContentParams createParams = new CreateContentParams();
        createParams.categoryKey = fixture.findCategoryByName( "MyCategory" ).getKey().toInt();
        createParams.publishFrom = new DateTime( 2100, 1, 1, 0, 0, 0, 0 ).toDate();
        createParams.status = ContentStatus.STATUS_APPROVED;
        ContentDataInput contentdataForCreate = new ContentDataInput( "MyContentType" );
        contentdataForCreate.add( new TextInput( "myTitle", "title from creation" ) );
        createParams.contentData = contentdataForCreate;
        ContentKey contentKeyToUpdate = new ContentKey( internalClient.createContent( createParams ) );

        UpdateContentParams updateParams = new UpdateContentParams();
        updateParams.updateStrategy = ContentDataInputUpdateStrategy.REPLACE_NEW;
        updateParams.contentKey = contentKeyToUpdate.toInt();
        updateParams.createNewVersion = false;
        updateParams.publishFrom = new DateTime( 2010, 1, 1, 0, 0, 0, 0 ).toDate();
        updateParams.setAsCurrentVersion = true;
        updateParams.status = null;

        ContentVersionKey versionKey = new ContentVersionKey( internalClient.updateContent( updateParams ) );
        assertNotNull( versionKey );

        assertEquals( 1, fixture.countContentVersionsByTitle( "title from creation" ) );
        assertEquals( versionKey, fixture.findFirstContentVersionByTitle( "title from creation" ).getKey() );
        assertEquals( new DateTime( 2010, 1, 1, 0, 0, 0, 0 ).toDate(),
                      fixture.findFirstContentVersionByTitle( "title from creation" ).getContent().getAvailableFrom() );
        assertEquals( 1, fixture.findFirstContentVersionByTitle( "title from creation" ).getContent().getVersions().size() );
    }

    @Test
    public void testUpdateContentDoNotChangeAssignment()
    {
        // exercise: updateContent

        AssignContentCommand assignContentCommand = new AssignContentCommand();
        assignContentCommand.setAssignerKey( fixture.findUserByName( "testuser" ).getKey() );
        assignContentCommand.setAssigneeKey( fixture.findUserByName( "testuser" ).getKey() );
        assignContentCommand.setAssignmentDescription( "test assignment" );
        assignContentCommand.setAssignmentDueDate( new DateTime( 2010, 6, 6, 10, 0, 0, 0 ).toDate() );
        assignContentCommand.setContentKey( contentWithBinaryKey );
        contentService.assignContent( assignContentCommand );

        ContentDataInput newContentData = new ContentDataInput( "MyContentType" );
        newContentData.add( new TextInput( "myTitle", "changedtitle" ) );
        newContentData.add( new BinaryInput( "myBinaryfile", dummyBinary, "dummyBinary" ) );

        UserEntity runningUser = fixture.findUserByName( "testuser" );
        SecurityHolder.setRunAsUser( runningUser.getKey() );

        UpdateContentParams params = new UpdateContentParams();
        params.contentKey = contentWithBinaryKey.toInt();
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

    private void createUpdateContent()
    {
        UserEntity runningUser = fixture.findUserByName( "testuser" );

        // prepare: save a new content
        ContentEntity content = new ContentEntity();
        content.setPriority( 0 );
        content.setAvailableFrom( new Date() );
        content.setAvailableTo( null );
        content.setCategory( fixture.findCategoryByName( "MyCategory" ) );
        content.setLanguage( fixture.findLanguageByCode( "en" ) );
        content.setName( "testcontent" );

        ContentVersionEntity version = new ContentVersionEntity();
        version.setContent( content );
        version.setModifiedBy( runningUser );
        version.setStatus( com.enonic.cms.domain.content.ContentStatus.APPROVED );
        version.setContent( content );
        CustomContentData contentData = new CustomContentData( fixture.findContentTypeByName( "MyContentType" ).getContentTypeConfig() );

        TextDataEntryConfig titleConfig = (TextDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "myTitle" );
        TextDataEntryConfig updateFieldConfig =
            (TextDataEntryConfig) contentData.getContentTypeConfig().getForm().getInputConfig( "fieldToUpdate" );
        contentData.add( new TextDataEntry( titleConfig, "testitle" ) );
        contentData.add( new TextDataEntry( updateFieldConfig, "foobar" ) );

        version.setContentData( contentData );
        version.setTitle( contentData.getTitle() );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );
        createContentCommand.setAccessRightsStrategy( AccessRightsStrategy.USE_GIVEN );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        updateContentKey = contentService.createContent( createContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

    }

    private String getUpdatedContentXML()
    {

        int keys[] = new int[1];
        keys[0] = updateContentKey.toInt();
        GetContentParams gcp = new GetContentParams();
        gcp.contentKeys = keys;
        gcp.includeData = true;
        Document content = internalClient.getContent( gcp );

        XMLOutputter outputter = new XMLOutputter( Format.getPrettyFormat() );
        String xml = outputter.outputString( content );
        return xml;
    }

    private String getUpdatedContentXMLWithDao()
    {

        ContentEntity entity = contentDao.findByKey( updateContentKey );
        return entity.getMainVersion().getContentDataAsXmlString();
    }

    private UpdateContentParams getUpdateContentParams( ContentDataInput cdi )
    {
        UpdateContentParams upcd = new UpdateContentParams();
        upcd.contentKey = updateContentKey.toInt();
        upcd.createNewVersion = true;
        upcd.publishFrom = new Date();
        upcd.publishTo = null;
        upcd.setAsCurrentVersion = true;
        upcd.status = ContentStatus.STATUS_APPROVED;
        upcd.contentData = cdi;
        return upcd;
    }

    private ContentDataInput getContentData( boolean all )
    {
        ContentDataInput cdi = new ContentDataInput( "MyContentType" );

        TextInput updatefield = new TextInput( "fieldToUpdate", "foobar" );
        TextInput title = new TextInput( "myTitle", "updateTest" );

        cdi.add( title );
        if ( all )
        {
            cdi.add( updatefield );
        }

        return cdi;
    }

}