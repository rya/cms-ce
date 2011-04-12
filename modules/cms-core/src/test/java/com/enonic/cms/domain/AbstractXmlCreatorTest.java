/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain;

import java.io.IOException;

import com.enonic.cms.core.content.ContentEntity;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import junit.framework.TestCase;

import com.enonic.cms.framework.util.JDOMUtil;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;

import com.enonic.cms.core.content.ContentKey;
import com.enonic.cms.core.content.contenttype.ContentTypeEntity;

public abstract class AbstractXmlCreatorTest
    extends TestCase
{

    protected String getFormattedXmlString( Document doc )
    {
        return JDOMUtil.prettyPrintDocument( doc, "  ", true );
    }

    protected String getFormattedXmlString( XMLDocument doc )
    {
        return getFormattedXmlString( doc.getAsJDOMDocument() );
    }

    protected String getXml( String path )
        throws JDOMException, IOException
    {
        Resource resource = new ClassPathResource( path );
        final Document document = JDOMUtil.parseDocument( resource.getInputStream() );
        XMLDocument xmlDocument = XMLDocumentFactory.create( document );
        return getFormattedXmlString( xmlDocument.getAsJDOMDocument() );
    }

    protected ContentTypeEntity createContentType( String key, String name )
    {

        ContentTypeEntity contentType = new ContentTypeEntity();
        contentType.setKey( Integer.valueOf( key ) );
        contentType.setName( name );
        return contentType;
    }

    protected ContentEntity createContent( String key )
    {

        ContentEntity content = new ContentEntity();
        content.setKey( new ContentKey( key ) );
        return content;
    }
}