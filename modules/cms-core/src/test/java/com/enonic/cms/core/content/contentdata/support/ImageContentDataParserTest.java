/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.content.contentdata.support;

import java.io.IOException;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.junit.Test;

import com.enonic.cms.framework.util.JDOMUtil;

import com.enonic.cms.core.content.contentdata.ContentData;
import com.enonic.cms.core.content.contentdata.legacy.support.ImageContentDataParser;

import static org.junit.Assert.*;


public class ImageContentDataParserTest
{

    @Test
    public void testParseNameEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "    <name>Test Arnesen</name>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentData contentData = ImageContentDataParser.parse( contentDataDoc, null );

        assertEquals( "Test Arnesen", contentData.getTitle() );
    }

    @Test
    public void testParseDescriptionEntry()
        throws IOException, JDOMException
    {

        StringBuffer contentDataXml = new StringBuffer();
        contentDataXml.append( "<contentdata>" );
        contentDataXml.append( "    <name>Test Arnesen</name>" );
        contentDataXml.append( "    <description>Test description</description>" );
        contentDataXml.append( "    <photographer email=\"jvs@enonic.com\" name=\"J�rund Vier Skriubakken\"/>" );
        contentDataXml.append( "</contentdata>" );
        Document contentDataDoc = JDOMUtil.parseDocument( contentDataXml.toString() );

        ContentData contentData = ImageContentDataParser.parse( contentDataDoc, null );

        assertEquals( "Test Arnesen", contentData.getTitle() );

        //TextAreaDataEntry descriptionDataEntry = (TextAreaDataEntry) contentData.getEntry( "description" );
        //assertNotNull( descriptionDataEntry );
        //assertEquals( "Test description", descriptionDataEntry.getValue() );

        //PhotographerDataEntry photographerDataEntry = (PhotographerDataEntry) contentData.getEntry( "photographer" );
        //assertNotNull( photographerDataEntry );
        //assertEquals( "jvs@enonic.com", photographerDataEntry.getPhotographersEmail() );
        //assertEquals( "J�rund Vier Skriubakken", photographerDataEntry.getPhotographersName() );
    }


}