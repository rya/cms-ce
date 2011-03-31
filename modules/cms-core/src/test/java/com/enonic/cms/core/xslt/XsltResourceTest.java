/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.xslt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import junit.framework.TestCase;

public class XsltResourceTest
    extends TestCase
{
    private String readXslt( String name )
        throws Exception
    {
        Resource resource = new ClassPathResource( name, getClass() );
        return new String( FileCopyUtils.copyToByteArray( resource.getInputStream() ) );
    }

    public void testGeneral()
        throws Exception
    {
        String source = readXslt( "XsltResourceTest.xsl" );
        XsltResource resource = new XsltResource( "name ÆØÅ æøå #1", source );
        assertEquals( "name ÆØÅ æøå #1", resource.getName() );
        assertNotNull( resource.getAsSource() );
        assertEquals( "dummy:/" + java.net.URLEncoder.encode( "name ÆØÅ æøå #1", "UTF-8" ), resource.getAsSource().getSystemId() );
        assertEquals( source, resource.getContent() );
    }
}
