/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.custom.stringbased.SelectorDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextAreaDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.UrlDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigBuilder;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextAreaDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.UrlDataEntryConfig;

import static org.junit.Assert.*;


public class CustomContentDataTest
{
    private Element standardConfigEl;

    @Before
    public void before()
        throws IOException, JDOMException
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

        standardConfigXml.append( "             <input name=\"myTextarea\" type=\"textarea\">" );
        standardConfigXml.append( "                 <display>My textarea</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytextarea</xpath>" );
        standardConfigXml.append( "                 <help>My text area help text</help>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myXml\" type=\"xml\">" );
        standardConfigXml.append( "                 <display>My xml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myxml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaDefault\" type=\"htmlarea\">" );
        standardConfigXml.append( "                 <display>My htmlarea default</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareadefault</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaXhtml\" type=\"htmlarea\" mode=\"xhtml\">" );
        standardConfigXml.append( "                 <display>My htmlarea xhtml</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareaxhtml</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myHtmlareaCdata\" type=\"htmlarea\" mode=\"cdata\">" );
        standardConfigXml.append( "                 <display>My htmlarea cdata</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myhtmlareacdata</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myUrl\" type=\"url\">" );
        standardConfigXml.append( "                 <display>My url</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myurl</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>My date</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myCheckbox\" type=\"checkbox\">" );
        standardConfigXml.append( "                 <display>My checkbox</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mycheckbox</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRadiobutton\" type=\"radiobutton\">" );
        standardConfigXml.append( "                 <display>My radio button</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myradiobutton</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                     <option value=\"5\" checked=\"true\">Fem</option>" );
        standardConfigXml.append( "                     <option value=\"10\">Ti</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myDropdown\" type=\"dropdown\">" );
        standardConfigXml.append( "                 <display>My dropdown</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydropdown</xpath>" );
        standardConfigXml.append( "                 <options>" );
        standardConfigXml.append( "                     <option value=\"1\" checked=\"true\">Toyota</option>" );
        standardConfigXml.append( "                     <option value=\"2\">Opel</option>" );
        standardConfigXml.append( "                     <option value=\"3\">Skoda</option>" );
        standardConfigXml.append( "                 </options>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRelatedSingle\" type=\"relatedcontent\" multiple=\"false\">" );
        standardConfigXml.append( "                 <display>My related single</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myrelatedsingle</xpath>" );
        standardConfigXml.append( "                 <contenttype name=\"MyContentType\"/>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myRelatedMultiple\" type=\"relatedcontent\" multiple=\"true\">" );
        standardConfigXml.append( "                 <display>My related multipe</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myrelatedmultiple</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myFile\" type=\"file\">" );
        standardConfigXml.append( "                 <display>My file</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myImage\" type=\"image\">" );
        standardConfigXml.append( "                 <display>My image</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimage</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myMchoiceInput\" type=\"multiplechoice\">" );
        standardConfigXml.append( "                 <display>Question</display>" );
        standardConfigXml.append( "                 <displayalternatives>Alternatives</displayalternatives>" );
        standardConfigXml.append( "                 <newbuttontext>New alternative</newbuttontext>" );
        standardConfigXml.append( "                 <column2text>Answer</column2text>" );
        standardConfigXml.append( "                 <xpath>contentdata/question</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "         <block name=\"JustAnotherBlock\">" );
        standardConfigXml.append( "             <input name=\"justAnotherTitle\" required=\"false\" type=\"text\">" );
        standardConfigXml.append( "                 <display>Just Another title</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/justanothertitle</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "             <input name=\"justAnotherUrl\" required=\"false\" type=\"url\">" );
        standardConfigXml.append( "                 <display>Just Another url</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/justanotherurl</xpath>" );
        standardConfigXml.append( "             </input>" );
        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();
    }

    @Test
    public void testAddDataEntryOfWrongType()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        try
        {
            UrlDataEntryConfig urlDataConfig = new UrlDataEntryConfig( "myTitle", false, "My title", "contentdata/mytitle", 100 );
            contentData.add( new UrlDataEntry( urlDataConfig, "Hallo" ) );
            fail( "Expected IllegalArgumentException" );
        }
        catch ( IllegalArgumentException e )
        {
            // ok
        }
    }

    @Test
    public void testGetEntries()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );

        TextDataEntryConfig titleConfig1 = new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" );
        UrlDataEntryConfig urlDataConfig1 = new UrlDataEntryConfig( "myUrl", false, "My url", "contentdata/myurl", 100 );
        TextDataEntryConfig titleConfig2 =
            new TextDataEntryConfig( "justAnotherTitle", true, "Just another title", "contentdata/justanothertitle" );
        UrlDataEntryConfig urlDataConfig2 =
            new UrlDataEntryConfig( "justAnotherUrl", false, "Just another url", "contentdata/justanotherurl", 100 );

        contentData.add( new TextDataEntry( titleConfig1, "Hallo" ) );
        contentData.add( new UrlDataEntry( urlDataConfig1, "http://www.bg.no" ) );
        contentData.add( new TextDataEntry( titleConfig2, "Just another hallo" ) );
        contentData.add( new UrlDataEntry( urlDataConfig2, "http://www.justanother.no" ) );

        List<DataEntry> entries = contentData.getEntries();
        assertEquals( 4, entries.size() );
    }

    @Test
    public void testAddTextDataEntry()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        contentData.add( new TextDataEntry( config.getInputConfig( "myTitle" ), "Hallo" ) );
        TextDataEntry dataEntry = (TextDataEntry) contentData.getEntry( "myTitle" );
        assertNotNull( dataEntry );
        assertEquals( "myTitle", dataEntry.getName() );
        assertEquals( "Hallo", dataEntry.getValue() );
    }

    @Test
    public void testAddRadiobuttonDataEntry()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        // Test for B-01293
//        try
//        {
//            contentData.add( new SelectorDataEntry( config.getInputConfig( "myRadiobutton" ), "Choice2" ) );
//            fail( "Choice2 is not a valid choice for this radiobutton" );
//        }
//        catch ( Exception e )
//        {
//            // Just continue;
//        }
        contentData.add( new SelectorDataEntry( config.getInputConfig( "myRadiobutton" ), "10" ) );
        SelectorDataEntry dataEntry = (SelectorDataEntry) contentData.getEntry( "myRadiobutton" );
        assertNotNull( dataEntry );
        assertEquals( "myRadiobutton", dataEntry.getName() );
        assertEquals( "10", dataEntry.getValue() );
    }

    @Test
    public void testAddMultipleChoiceDataEntry()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        DataEntryConfig choiceConfig = config.getInputConfig( "myMchoiceInput" );
        List<MultipleChoiceAlternative> alts = new LinkedList<MultipleChoiceAlternative>();
        alts.add( new MultipleChoiceAlternative( "1996", true ) );
        alts.add( new MultipleChoiceAlternative( "1998", false ) );
        alts.add( new MultipleChoiceAlternative( "1999", false ) );
        alts.add( new MultipleChoiceAlternative( "2000", false ) );
        contentData.add( new MultipleChoiceDataEntry( choiceConfig, "Når ble Enonic grunnlagt?", alts ) );

        MultipleChoiceDataEntry dataEntry = (MultipleChoiceDataEntry) contentData.getEntry( "myMchoiceInput" );
        assertNotNull( dataEntry );
        assertEquals( "myMchoiceInput", dataEntry.getName() );
        assertEquals( "Når ble Enonic grunnlagt?", dataEntry.getText() );
        assertEquals( 4, dataEntry.getAlternatives().size() );
    }

    @Test
    public void testAddUrlDataEntry()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        UrlDataEntryConfig urlDataConfig = new UrlDataEntryConfig( "myUrl", false, "My url", "contentdata/myurl", 100 );
        contentData.add( new UrlDataEntry( urlDataConfig, "http://www.bg.no" ) );
        UrlDataEntry dataEntry = (UrlDataEntry) contentData.getEntry( "myUrl" );
        assertNotNull( dataEntry );
        assertEquals( "myUrl", dataEntry.getName() );
        assertEquals( "http://www.bg.no", dataEntry.getValue() );
    }

    @Test
    public void testAddTwoEntries()
    {
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );

        CustomContentData contentData = new CustomContentData( config );
        TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" );
        TextAreaDataEntryConfig textAreaConfig =
            new TextAreaDataEntryConfig( "myTextarea", false, "My text area", "contentdata/mytextarea" );

        contentData.add( new TextDataEntry( titleConfig, "x" ) );
        contentData.add( new TextAreaDataEntry( textAreaConfig, "x" ) );

        DataEntry dataEntry1 = contentData.getEntry( "myTitle" );
        assertNotNull( dataEntry1 );
        assertEquals( "myTitle", dataEntry1.getName() );

        DataEntry dataEntry2 = contentData.getEntry( "myTextarea" );
        assertNotNull( dataEntry2 );
        assertEquals( "myTextarea", dataEntry2.getName() );
    }

    @Test
    public void validatingChecksGroupPositions()
        throws IOException, JDOMException
    {
        ContentTypeConfigBuilder ctyBuilder = new ContentTypeConfigBuilder( "MyContentType", "myTitle" );
        ctyBuilder.startBlock( "General" );
        ctyBuilder.addInput( "myTitle", "text", "contentdata/mytitle", "My Title", true );
        ctyBuilder.endBlock();
        ctyBuilder.startBlock( "MyGroup", "contentdata/mygroup" );
        ctyBuilder.addInput( "myGroupEntryRequiredField1", "text", "my-group-entry-field-1", "myGroupEntryRequiredField1", true );
        ctyBuilder.addInput( "myGroupEntryRequiredField2", "text", "my-group-entry-field-2", "myGroupEntryRequiredField2", false );
        ctyBuilder.endBlock();

        Element configEl = JDOMUtil.parseDocument( ctyBuilder.toString() ).getRootElement();
        ContentTypeConfig config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, configEl );

        CustomContentData contentData = new CustomContentData( config );
        contentData.add( new TextDataEntry( config.getInputConfig( "myTitle" ), "value 1" ) );
        GroupDataEntry groupDataEntry = new GroupDataEntry( "MyGroup", "contentdata/mygroup", 1 );
        groupDataEntry.setConfig( config.getSetConfig( "MyGroup" ) );
        groupDataEntry.add( new TextDataEntry( config.getInputConfig( "myGroupEntryRequiredField1" ), "value 2" ) );
        contentData.add( groupDataEntry );
        contentData.validate();
    }

}
