/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import org.junit.Test;
import javax.xml.transform.Source;
import static org.junit.Assert.*;

public class XsltResourceTest
{
    @Test
    public void testGetContent()
        throws Exception
    {
        final XsltResource res = new XsltResource("xslt content");
        assertEquals("xslt content", res.getContent());

        final Source source = res.getAsSource();
        assertNotNull(source);
    }

    @Test
    public void testGetNameUnknown()
        throws Exception
    {
        final XsltResource res = new XsltResource("xslt content");
        assertEquals("unknown", res.getName());

        final Source source = res.getAsSource();
        assertNotNull(source);
        assertEquals("dummy:/unknown", source.getSystemId());
    }

    @Test
    public void testGetNameLocal()
        throws Exception
    {
        final XsltResource res = new XsltResource("file.xsl", "xslt content");
        assertEquals("file.xsl", res.getName());

        final Source source = res.getAsSource();
        assertNotNull(source);
        assertEquals("dummy:/file.xsl", source.getSystemId());
    }

    @Test
    public void testGetNameWithScheme()
        throws Exception
    {
        final XsltResource res = new XsltResource("http://mydomain.com/file.xsl", "xslt content");
        assertEquals("http://mydomain.com/file.xsl", res.getName());

        final Source source = res.getAsSource();
        assertNotNull(source);
        assertEquals("http://mydomain.com/file.xsl", source.getSystemId());
    }
}
