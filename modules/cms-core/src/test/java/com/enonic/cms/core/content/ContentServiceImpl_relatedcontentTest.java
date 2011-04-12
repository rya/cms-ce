/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content;

import java.io.IOException;
import java.util.ArrayList;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
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

import com.enonic.cms.core.business.AbstractPersistContentTest;

import com.enonic.cms.core.content.binary.BinaryDataAndBinary;
import com.enonic.cms.core.content.contentdata.custom.CustomContentData;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.RelatedContentDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.RelatedContentsDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.user.UserType;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ContentServiceImpl_relatedcontentTest
    extends AbstractPersistContentTest
{
    private Element configEl;

    private XMLBytes config;

    @Before
    public void before()
        throws IOException, JDOMException
    {

        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );

        configXml.append( "         <title name=\"myTitle\"/>" );

        configXml.append( "         <block name=\"General\">" );

        configXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My title</display>" );
        configXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        configXml.append( "             </input>" );

        configXml.append( "         </block>" );

        configXml.append( "         <block name=\"Related content\">" );

        configXml.append( "             <input name=\"myMultipleRelatedContent\" type=\"relatedcontent\" multiple=\"true\">" );
        configXml.append( "                 <display>My related content</display>" );
        configXml.append( "                 <xpath>contentdata/myrelatedcontents</xpath>" );
        configXml.append( "             </input>" );

        configXml.append( "             <input name=\"mySoleRelatedContent\" type=\"relatedcontent\" multiple=\"false\">" );
        configXml.append( "                 <display>My sole related content</display>" );
        configXml.append( "                 <xpath>contentdata/mysolerelatedcontent</xpath>" );
        configXml.append( "                 <contenttype name=\"MyContentType\"/>" );
        configXml.append( "             </input>" );

        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();
        config = XMLDocumentFactory.create( configXml.toString() ).getAsBytes();

        initSystemData();

        createAndStoreUserAndUserGroup( "testuser", "testuser fullname", UserType.NORMAL, "testuserstore" );
        hibernateTemplate.save( createContentHandler( "Custom content", ContentHandlerName.CUSTOM.getHandlerClassShortName() ) );
        hibernateTemplate.save( createContentType( "MyContentType", ContentHandlerName.CUSTOM.getHandlerClassShortName(), config ) );
        hibernateTemplate.save( createUnit( "MyUnit" ) );
        hibernateTemplate.save( createCategory( "MyCategory", "MyContentType", "MyUnit", "testuser", "testuser" ) );
        hibernateTemplate.save(
            createCategoryAccess( "MyCategory", findUserByName( "testuser" ).getUserGroup().getName(), "true", "true", "true", "true",
                                  "true" ) );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr( "127.0.0.1" );
        ServletRequestAccessor.setRequest( request );

    }


    @Test
    public void testCreateContentWithRelatedContent()
    {

        ContentKey relatedContentKey1 = storeSimpleContent( "rel1" );
        ContentKey relatedContentKey2 = storeSimpleContent( "rel2" );
        ContentKey relatedContentKey3 = storeSimpleContent( "rel3" );

        ContentEntity content = createContent( "MyCategory", "en", "testuser", "0" );
        ContentVersionEntity version = createContentVersion( "0", "testuser" );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "Tittel", "contentdata/mytitle" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );

        RelatedContentDataEntryConfig multipleRelatedContentsConfig =
            (RelatedContentDataEntryConfig) contentTypeConfig.getInputConfig( "myMultipleRelatedContent" );

        contentData.add( new RelatedContentsDataEntry( multipleRelatedContentsConfig ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey1 ) ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey2 ) ) );

        RelatedContentDataEntryConfig soleRelatedConfig =
            (RelatedContentDataEntryConfig) contentTypeConfig.getInputConfig( "mySoleRelatedContent" );

        contentData.add( new RelatedContentDataEntry( soleRelatedConfig, relatedContentKey3 ) );

        version.setContentData( contentData );

        UserEntity runningUser = findUserByName( "testuser" );

        CreateContentCommand createCommand = new CreateContentCommand();
        createCommand.setCreator( runningUser );

        createCommand.populateCommandWithContentValues( content );
        createCommand.populateCommandWithContentVersionValues( version );

        createCommand.setBinaryDatas( new ArrayList<BinaryDataAndBinary>() );
        createCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contenKey = contentService.createContent( createCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contenKey );
        assertNotNull( persistedContent );

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();

        assertEquals( 3, persistedVersion.getRelatedChildren( true ).size() );

        Document contentDataXml = persistedVersion.getContentDataAsJDomDocument();
        assertXPathEquals( "contentdata/myrelatedcontents/content/@key", contentDataXml,
                           new String[]{relatedContentKey1.toString(), relatedContentKey2.toString()} );
        assertXPathEquals( "contentdata/mysolerelatedcontent/@key", contentDataXml, relatedContentKey3.toString() );
    }

    @Test
    public void testUpdateCurrentVersion()
    {
        ContentKey relatedContentKey1 = storeSimpleContent( "rel1" );
        ContentKey relatedContentKey2 = storeSimpleContent( "rel2" );
        ContentKey relatedContentKey3 = storeSimpleContent( "rel3" );
        ContentKey relatedContentKey4 = storeSimpleContent( "rel4" );
        ContentKey relatedContentKey5 = storeSimpleContent( "rel5" );

        ContentEntity content = createContent( "MyCategory", "en", "testuser", "0" );
        ContentVersionEntity version = createContentVersion( "0", "testuser" );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        CustomContentData contentData = new CustomContentData( contentTypeConfig );
        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "Tittel", "contentdata/mytitle" );
        contentData.add( new TextDataEntry( titleConfig, "test title" ) );

        RelatedContentDataEntryConfig multipleRelatedContentsConfig =
            (RelatedContentDataEntryConfig) contentTypeConfig.getInputConfig( "myMultipleRelatedContent" );

        contentData.add( new RelatedContentsDataEntry( multipleRelatedContentsConfig ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey1 ) ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey2 ) ) );

        RelatedContentDataEntryConfig soleRelatedConfig =
            (RelatedContentDataEntryConfig) contentTypeConfig.getInputConfig( "mySoleRelatedContent" );

        contentData.add( new RelatedContentDataEntry( soleRelatedConfig, relatedContentKey3 ) );

        version.setContentData( contentData );

        UserEntity runningUser = findUserByName( "testuser" );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        createContentCommand.setBinaryDatas( new ArrayList<BinaryDataAndBinary>() );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity persistedContent = contentDao.findByKey( contentKey );
        assertNotNull( persistedContent );

        ContentVersionEntity persistedVersion = persistedContent.getMainVersion();
        assertNotNull( persistedVersion );

        assertEquals( 3, persistedVersion.getRelatedChildren( true ).size() );

        ContentEntity changedContent = createContent( "MyCategory", "en", "testuser", "0" );
        changedContent.setKey( contentKey );
        ContentVersionEntity changedVersion = createContentVersion( "0", "testuser" );
        changedVersion.setKey( persistedVersion.getKey() );

        CustomContentData changedCD = new CustomContentData( contentTypeConfig );

        TextDataEntryConfig changedTitleConfig = new TextDataEntryConfig( "myTitle", true, "Tittel", "contentdata/mytitle" );
        changedCD.add( new TextDataEntry( changedTitleConfig, "changed title" ) );

        changedCD.add( new RelatedContentsDataEntry( multipleRelatedContentsConfig ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey3 ) ).add(
            new RelatedContentDataEntry( multipleRelatedContentsConfig, relatedContentKey5 ) ) );

        changedCD.add( new RelatedContentDataEntry( soleRelatedConfig, relatedContentKey4 ) );

        changedVersion.setContentData( changedCD );

        UpdateContentCommand updateContentCommand = UpdateContentCommand.updateExistingVersion2(
                persistedVersion.getKey() );
        updateContentCommand.setModifier( runningUser );
        updateContentCommand.setUpdateAsMainVersion( false );

        updateContentCommand.populateContentValuesFromContent( persistedContent );
        updateContentCommand.populateContentVersionValuesFromContentVersion( changedVersion );

        contentService.updateContent( updateContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        ContentEntity contentAfterUpdate = contentDao.findByKey( contentKey );
        ContentVersionEntity versionAfterUpdate = contentVersionDao.findByKey( persistedVersion.getKey() );

        Document contentDataXmlAfterUpdate = versionAfterUpdate.getContentDataAsJDomDocument();

        assertXPathEquals( "/contentdata/mysolerelatedcontent/@key", contentDataXmlAfterUpdate, relatedContentKey4.toString() );
        assertXPathEquals( "/contentdata/myrelatedcontents/content[1]/@key", contentDataXmlAfterUpdate, relatedContentKey3.toString() );
        assertXPathEquals( "/contentdata/myrelatedcontents/content[2]/@key", contentDataXmlAfterUpdate, relatedContentKey5.toString() );

        assertEquals( 3, versionAfterUpdate.getRelatedChildren( true ).size() );
    }


    private Element createSimpleContentTypeConfig()
    {

        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );

        configXml.append( "         <title name=\"myTitle\"/>" );

        configXml.append( "         <block name=\"General\">" );

        configXml.append( "             <input name=\"myTitle\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My title</display>" );
        configXml.append( "                 <xpath>contentdata/mytitle</xpath>" );
        configXml.append( "             </input>" );

        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );

        try
        {
            return JDOMUtil.parseDocument( configXml.toString() ).getRootElement();
        }
        catch ( IOException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch ( JDOMException e )
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }


    private ContentKey storeSimpleContent( String title )
    {

        ContentEntity content = createContent( "MyCategory", "en", "testuser", "0" );
        ContentVersionEntity version = createContentVersion( "0", "testuser" );

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, createSimpleContentTypeConfig() );

        CustomContentData contentData = new CustomContentData( contentTypeConfig );

        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, title, "contentdata/mytitle" );
        contentData.add( new TextDataEntry( titleConfig, "relatedconfig" ) );

        version.setContentData( contentData );

        UserEntity runningUser = findUserByName( "testuser" );

        CreateContentCommand createContentCommand = new CreateContentCommand();
        createContentCommand.setCreator( runningUser );

        createContentCommand.populateCommandWithContentValues( content );
        createContentCommand.populateCommandWithContentVersionValues( version );

        createContentCommand.setBinaryDatas( new ArrayList<BinaryDataAndBinary>() );
        createContentCommand.setUseCommandsBinaryDataToAdd( true );

        ContentKey contentKey = contentService.createContent( createContentCommand );

        hibernateTemplate.flush();
        hibernateTemplate.clear();

        return contentKey;
    }

}
