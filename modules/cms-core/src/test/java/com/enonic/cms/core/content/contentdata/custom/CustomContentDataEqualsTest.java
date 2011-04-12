/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.io.IOException;

import com.enonic.cms.core.content.ContentKey;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.domain.AbstractEqualsTest;
import com.enonic.cms.core.content.ContentHandlerName;
import com.enonic.cms.core.content.contentdata.custom.contentkeybased.ImageDataEntry;
import com.enonic.cms.core.content.contentdata.custom.relationdataentrylistbased.ImagesDataEntry;
import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;
import com.enonic.cms.core.content.contenttype.dataentryconfig.DateDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.ImageDataEntryConfig;
import com.enonic.cms.core.content.contenttype.dataentryconfig.TextDataEntryConfig;


public class CustomContentDataEqualsTest
    extends AbstractEqualsTest
{

    private Element standardConfigEl;

    private ImageDataEntryConfig imagesConfig = new ImageDataEntryConfig( "myImages", false, "My images", "contentdata/myimages" );

    private TextDataEntryConfig titleConfig = new TextDataEntryConfig( "myTitle", true, "My title", "contentdata/mytitle" );

    private DateDataEntryConfig dateConfig = new DateDataEntryConfig( "myDate", false, "My date", "contentdata/mydate" );

    private ContentTypeConfig config;

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

        standardConfigXml.append( "             <input name=\"myDate\" type=\"date\">" );
        standardConfigXml.append( "                 <display>My date</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mydate</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myBinaryfile\" type=\"uploadfile\">" );
        standardConfigXml.append( "                 <display>My binaryfile</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mybinaryfile</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myImages\" type=\"images\">" );
        standardConfigXml.append( "                 <display>My images</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/myimages</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
    }

    @Test
    public void testEquals()
    {
        assertEqualsContract();
    }

    public Object getObjectX()
    {
        CustomContentData instance1 = new CustomContentData( config );

        instance1.add( new TextDataEntry( titleConfig, "Hallo" ) );
        instance1.add( new DateDataEntry( dateConfig, new DateTime( 2009, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        instance1.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 1 ) ) ) );

        return instance1;
    }

    public Object[] getObjectsThatNotEqualsX()
    {
        CustomContentData instance1 = new CustomContentData( config );

        instance1.add( new TextDataEntry( titleConfig, "Hallo 2" ) );
        instance1.add( new DateDataEntry( dateConfig, new DateTime( 2005, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        instance1.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 1 ), "imagetext" ) ) );

        CustomContentData instance2 = new CustomContentData( config );
        instance2.add( new TextDataEntry( titleConfig, "Hallo 3" ) );
        instance2.add( new DateDataEntry( dateConfig, new DateTime( 2006, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        instance2.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 2 ) ) ) );

        return new Object[]{instance1, instance2};
    }

    public Object getObjectThatEqualsXButNotTheSame()
    {
        CustomContentData instance1 = new CustomContentData( config );

        instance1.add( new TextDataEntry( titleConfig, "Hallo" ) );
        instance1.add( new DateDataEntry( dateConfig, new DateTime( 2009, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        instance1.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 1 ) ) ) );

        return instance1;
    }

    public Object getObjectThatEqualsXButNotTheSame2()
    {
        CustomContentData instance1 = new CustomContentData( config );

        instance1.add( new TextDataEntry( titleConfig, "Hallo" ) );
        instance1.add( new DateDataEntry( dateConfig, new DateTime( 2009, 1, 1, 1, 1, 1, 1 ).toDate() ) );
        instance1.add( new ImagesDataEntry( imagesConfig ).add( new ImageDataEntry( imagesConfig, new ContentKey( 1 ) ) ) );

        return instance1;
    }
}