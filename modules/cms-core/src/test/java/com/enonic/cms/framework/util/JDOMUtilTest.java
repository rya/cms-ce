/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.UnsupportedEncodingException;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junitx.framework.Assert;

import com.enonic.cms.framework.xml.XMLBytes;
import com.enonic.cms.framework.xml.XMLDocument;
import com.enonic.cms.framework.xml.XMLDocumentFactory;


public class JDOMUtilTest
{
    private static final Logger LOG = LoggerFactory.getLogger( JDOMUtilTest.class.getName() );

    @Test
    public void testToBytes()
        throws UnsupportedEncodingException
    {
        Element rootEl = new Element( "root" );
        rootEl.addContent( new CDATA( "<p>jalla</p>" ) );

        byte[] bytes = JDOMUtil.toBytes( new Document( rootEl ) );

        XMLDocument xmlDoc = XMLDocumentFactory.create( bytes, "UTF-8" );
        Document doc = xmlDoc.getAsJDOMDocument();
        Element actualRootEl = doc.getRootElement();
        CDATA cdata = (CDATA) actualRootEl.getContent( 0 );
        Assert.assertNotNull( cdata );
        Assert.assertEquals( "<p>jalla</p>", cdata.getText() );

        XMLBytes xmlBytes = xmlDoc.getAsBytes();
        LOG.info( new String( xmlBytes.getData(), "UTF-8" ) );
    }
}
