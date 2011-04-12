/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.custom;

import java.io.IOException;

import com.enonic.cms.core.content.ContentHandlerName;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.Before;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.custom.stringbased.TextDataEntry;
import com.enonic.cms.core.content.contenttype.ContentTypeConfig;
import com.enonic.cms.core.content.contenttype.ContentTypeConfigParser;

import static org.junit.Assert.*;


public class CustomContentDataExtensiveEqualsTest
{

    private Element standardConfigEl;

    private ContentTypeConfig config;

    @Before
    public void before()
        throws IOException, JDOMException
    {

        StringBuffer standardConfigXml = new StringBuffer();
        standardConfigXml.append( "<config name=\"MyContentType\" version=\"1.0\">" );
        standardConfigXml.append( "     <form>" );

        standardConfigXml.append( "         <title name=\"myTitle1\"/>" );

        standardConfigXml.append( "         <block name=\"TestBlock1\">" );

        standardConfigXml.append( "             <input name=\"myTitle1\" required=\"true\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title 1</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle1</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "             <input name=\"myTitle2\" required=\"false\" type=\"text\">" );
        standardConfigXml.append( "                 <display>My title 2</display>" );
        standardConfigXml.append( "                 <xpath>contentdata/mytitle2</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "         <block name=\"TestBlockGroup\" group=\"contentdata/group\">" );

        standardConfigXml.append( "             <input name=\"groupTitle\" required=\"false\" type=\"text\">" );
        standardConfigXml.append( "                 <display>Group title</display>" );
        standardConfigXml.append( "                 <xpath>grouptitle</xpath>" );
        standardConfigXml.append( "             </input>" );

        standardConfigXml.append( "         </block>" );

        standardConfigXml.append( "     </form>" );
        standardConfigXml.append( "</config>" );
        standardConfigEl = JDOMUtil.parseDocument( standardConfigXml.toString() ).getRootElement();

        config = ContentTypeConfigParser.parse( ContentHandlerName.CUSTOM, standardConfigEl );
    }

    @Test
    public void testEqualsWhenSameValuesAndSameOrder()
    {
        CustomContentData instance1 = new CustomContentData( config );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle2" ), "t1" ) );

        CustomContentData instance2 = new CustomContentData( config );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle2" ), "t1" ) );

        assertTrue( instance1.equals( instance2 ) );
    }

    @Test
    public void testEqualsWhenSameValuesButNotSameOrder()
    {
        CustomContentData instance1 = new CustomContentData( config );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle2" ), "t1" ) );

        CustomContentData instance2 = new CustomContentData( config );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle2" ), "t1" ) );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );

        assertTrue( instance1.equals( instance2 ) );
    }

    @Test
    public void testGroupedNotEqualsWhenSameValuesButNotSameOrder()
    {
        CustomContentData instance1 = new CustomContentData( config );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        GroupDataEntry group1i1 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance1.add( group1i1 );
        group1i1.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g1" ) );
        GroupDataEntry group1i2 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance1.add( group1i2 );
        group1i2.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g2" ) );

        CustomContentData instance2 = new CustomContentData( config );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        GroupDataEntry group2i1 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance2.add( group2i1 );
        group2i1.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g2" ) );
        GroupDataEntry group2i2 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance2.add( group2i2 );
        group2i2.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g1" ) );

        assertFalse( instance1.equals( instance2 ) );
    }

    @Test
    public void testGroupedEqualsWhenSameValuesAndSameOrder()
    {
        CustomContentData instance1 = new CustomContentData( config );
        instance1.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        GroupDataEntry group1i1 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance1.add( group1i1 );
        group1i1.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g1" ) );
        GroupDataEntry group1i2 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance1.add( group1i2 );
        group1i2.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g2" ) );

        CustomContentData instance2 = new CustomContentData( config );
        instance2.add( new TextDataEntry( config.getInputConfig( "myTitle1" ), "t1" ) );
        GroupDataEntry group2i1 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance2.add( group2i1 );
        group2i1.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g1" ) );
        GroupDataEntry group2i2 = new GroupDataEntry( "TestBlockGroup", "contentdata/group" );
        instance2.add( group2i2 );
        group2i2.add( new TextDataEntry( config.getInputConfig( "groupTitle" ), "g2" ) );

        assertTrue( instance1.equals( instance2 ) );
    }


}