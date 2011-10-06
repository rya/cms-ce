/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.support;

import java.util.List;

import org.jdom.Element;
import org.junit.Test;

import com.enonic.cms.core.content.contentdata.ContentDataXPathCreator;

import static org.junit.Assert.*;


public class ContentDataXPathCreatorTest
{

    @Test
    public void testCreateNewPathSimple()
    {
        Element contentdataEl = new Element( "contentdata" );

        ContentDataXPathCreator.createNewPath( contentdataEl, "heading" );

        Element headingEl = contentdataEl.getChild( "heading" );
        assertNotNull( headingEl );
    }

    @Test
    public void testCreateNewPathSimple2()
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "myGroup" ) );

        ContentDataXPathCreator.createNewPath( contentdataEl, "myGroup" );

        List<Element> relatedcontentList = contentdataEl.getChildren( "myGroup" );
        assertEquals( 2, relatedcontentList.size() );
    }

    @Test
    public void testCreateNewPathSimple3()
    {
        Element contentdataEl = new Element( "contentdata" );

        ContentDataXPathCreator.createNewPath( contentdataEl, "myGroup/a/b" );

        List<Element> contentList = contentdataEl.getChildren( "myGroup" );
        assertEquals( 1, contentList.size() );
        contentList = contentList.get( 0 ).getChildren( "a" );
        assertEquals( 1, contentList.size() );
        contentList = contentList.get( 0 ).getChildren( "b" );
        assertEquals( 1, contentList.size() );
    }

    @Test
    public void testCreateNewPathSimple4()
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "myGroup" ) );

        ContentDataXPathCreator.createNewPath( contentdataEl, "myGroup/a/b" );

        List<Element> contentList = contentdataEl.getChildren( "myGroup" );
        assertEquals( 1, contentList.size() );
        contentList = contentList.get( 0 ).getChildren( "a" );
        assertEquals( 1, contentList.size() );
        contentList = contentList.get( 0 ).getChildren( "b" );
        assertEquals( 1, contentList.size() );
    }

    @Test
    public void testEnsurePathSimple()
    {
        Element contentdataEl = new Element( "contentdata" );

        ContentDataXPathCreator.ensurePath( contentdataEl, "heading" );

        Element headingEl = contentdataEl.getChild( "heading" );
        assertNotNull( headingEl );
    }

    @Test
    public void testEnsurePath1()
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "heading" ) );

        ContentDataXPathCreator.ensurePath( contentdataEl, "sub/heading" );

        Element subEl = contentdataEl.getChild( "sub" );
        assertNotNull( subEl );

        Element headingEl = contentdataEl.getChild( "heading" );
        assertNotNull( headingEl );
    }

    @Test
    public void testEnsurePath2()
    {
        Element contentdataEl = new Element( "contentdata" );
        contentdataEl.addContent( new Element( "article" ).addContent( new Element( "heading" ) ) );
        contentdataEl.addContent( new Element( "teaser" ) );
        ContentDataXPathCreator.ensurePath( contentdataEl, "sub/heading" );

        Element subEl = contentdataEl.getChild( "sub" );
        assertNotNull( subEl );

        Element headingEl = subEl.getChild( "heading" );
        assertNotNull( headingEl );
    }
}
