/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contenttype;

import java.io.IOException;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contenttype.dataentryconfig.AbstractBaseDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfigType;
import com.enonic.cms.core.content.contenttype.dataentryconfig.RelatedContentDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;

import static org.junit.Assert.*;


public class ContentTypeConfigParserTest
{
    @Test
    public void testParseBasicConfig()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"test_title\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"test_title\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>Title</display>" );
        configXml.append( "                 <xpath>contentdata/test/title</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        assertNotNull( config );
        assertEquals( "MyContentType", config.getName() );
        assertEquals( "test_title", config.getForm().getTitleInputName() );
        assertNotNull( config.getForm().getTitleInput() );
        assertNotNull( config.getInputConfig( "test_title" ) );
    }

    @Test
    public void testParseTextInputConfig()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myText\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myText\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My text</display>" );
        configXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        TextDataEntryConfig dataEntryConfig = (TextDataEntryConfig) config.getInputConfig( "myText" );
        assertEquals( DataEntryConfigType.TEXT, dataEntryConfig.getType() );
        assertEquals( "My text", dataEntryConfig.getDisplayName() );
        assertEquals( "myText", dataEntryConfig.getName() );
        assertEquals( "contentdata/mytext", dataEntryConfig.getRelativeXPath() );
        assertTrue( dataEntryConfig.isRequired() );
    }

    @Test
    public void testParseTextAreaInputConfig()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myText\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myText\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My text</display>" );
        configXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myTextArea\" required=\"false\" type=\"textarea\">" );
        configXml.append( "                 <display>My textarea</display>" );
        configXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        AbstractBaseDataEntryConfig inputConfig = (AbstractBaseDataEntryConfig) config.getInputConfig( "myTextArea" );
        assertEquals( DataEntryConfigType.TEXT_AREA, inputConfig.getType() );
        assertEquals( "My textarea", inputConfig.getDisplayName() );
        assertEquals( "myTextArea", inputConfig.getName() );
        assertEquals( "contentdata/mytextarea", inputConfig.getRelativeXPath() );
        assertFalse( inputConfig.isRequired() );
    }

    @Test
    public void testParseRelatedContentInputConfig()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myText\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myText\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My text</display>" );
        configXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myRel\" required=\"false\" type=\"relatedcontent\">" );
        configXml.append( "                 <display>My related</display>" );
        configXml.append( "                 <xpath>contentdata/myrelated</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        RelatedContentDataEntryConfig dataEntryConfig = (RelatedContentDataEntryConfig) config.getInputConfig( "myRel" );
        assertEquals( DataEntryConfigType.RELATEDCONTENT, dataEntryConfig.getType() );
        assertEquals( "My related", dataEntryConfig.getDisplayName() );
        assertEquals( "myRel", dataEntryConfig.getName() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getRelativeXPath() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getXpath() );
        assertTrue( dataEntryConfig.isMultiple() );
        assertFalse( dataEntryConfig.isRequired() );
    }

    @Test
    public void testParseRelatedContentInputConfigThatIsMultiple()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myText\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myText\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My text</display>" );
        configXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myRel\" type=\"relatedcontent\" multiple=\"true\">" );
        configXml.append( "                 <display>My related</display>" );
        configXml.append( "                 <xpath>contentdata/myrelated</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        RelatedContentDataEntryConfig dataEntryConfig = (RelatedContentDataEntryConfig) config.getInputConfig( "myRel" );
        assertEquals( DataEntryConfigType.RELATEDCONTENT, dataEntryConfig.getType() );
        assertEquals( "My related", dataEntryConfig.getDisplayName() );
        assertEquals( "myRel", dataEntryConfig.getName() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getRelativeXPath() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getXpath() );
        assertTrue( dataEntryConfig.isMultiple() );
        assertFalse( dataEntryConfig.isRequired() );
    }

    @Test
    public void testParseGroupBlock()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myText\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myText\" required=\"true\" type=\"text\">" );
        configXml.append( "                 <display>My text</display>" );
        configXml.append( "                 <xpath>contentdata/mytext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "             <input name=\"myRel\" type=\"relatedcontent\" multiple=\"true\">" );
        configXml.append( "                 <display>My related</display>" );
        configXml.append( "                 <xpath>contentdata/myrelated</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "         <block name=\"inner\" group=\"contentdata/inner\">" );
        configXml.append( "             <input name=\"myInnerText\" type=\"text\">" );
        configXml.append( "                 <display>My related</display>" );
        configXml.append( "                 <xpath>innertext</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        ContentTypeConfig contentTypeConfig = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        CtySetConfig innerSetConfig = contentTypeConfig.getSetConfig( "inner" );
        assertNotNull( innerSetConfig );
        assertEquals( "contentdata/inner", innerSetConfig.getRelativeXPath() );
        assertEquals( "contentdata/inner", innerSetConfig.getGroupXPath() );
        assertNotNull( innerSetConfig.getInputConfig( "myInnerText" ) );
        assertNotNull( innerSetConfig.getInputConfigByRelativeXPath( "innertext" ) );

        RelatedContentDataEntryConfig dataEntryConfig = (RelatedContentDataEntryConfig) contentTypeConfig.getInputConfig( "myRel" );
        assertEquals( DataEntryConfigType.RELATEDCONTENT, dataEntryConfig.getType() );
        assertEquals( "My related", dataEntryConfig.getDisplayName() );
        assertEquals( "myRel", dataEntryConfig.getName() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getRelativeXPath() );
        assertEquals( "contentdata/myrelated", dataEntryConfig.getXpath() );
        assertTrue( dataEntryConfig.isMultiple() );
        assertFalse( dataEntryConfig.isRequired() );
    }

    @Test
    public void parse_throws_exception_when_referred_input_field_for_title_is_not_required()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myUnrequired\"/>" );
        configXml.append( "         <block name=\"TestBlock1\">" );
        configXml.append( "             <input name=\"myUnrequired\" required=\"false\" type=\"text\">" );
        configXml.append( "                 <display>My unrequired</display>" );
        configXml.append( "                 <xpath>contentdata/myunrequired</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        }
        catch ( Throwable e )
        {
            assertNotNull( e );
            assertTrue( e instanceof InvalidContentTypeConfigException );
            assertEquals( "Referred input field for title 'myUnrequired' must be configured to be required.", e.getMessage() );
        }
    }

    @Test
    public void parse_throws_exception_when_referred_related_input_field_is_missing_name_attribute()
        throws IOException, JDOMException
    {
        StringBuffer configXml = new StringBuffer();
        configXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        configXml.append( "     <form>" );
        configXml.append( "         <title name=\"myUnrequired\"/>" );
        configXml.append( "         <block name='TestBlock1'>" );
        configXml.append( "             <input name='myUnrequired' required='true' type='text'>" );
        configXml.append( "                 <display>My unrequired</display>" );
        configXml.append( "                 <xpath>contentdata/myunrequired</xpath>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );
        configXml.append( "         <block name='TestGroupBlock' group='contentdata/myrelateds'>" );
        configXml.append( "             <input name='myRelated' required='false' type='relatedcontent'>" );
        configXml.append( "                 <display>My related</display>" );
        configXml.append( "                 <xpath>myrelated</xpath>" );
        configXml.append( "                 <contenttype key=''/>" );
        configXml.append( "             </input>" );
        configXml.append( "         </block>" );

        configXml.append( "     </form>" );
        configXml.append( "</config>" );
        Element configEl = JDOMUtil.parseDocument( configXml.toString() ).getRootElement();

        try
        {
            ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );
        }
        catch ( Throwable e )
        {
            assertNotNull( e );
            assertTrue( e instanceof InvalidContentTypeConfigException );
            assertEquals( "Missing name attribute for contenttype element in input config 'myRelated' in position: 1", e.getMessage() );
        }
    }
}
