/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.net.URL;

import javax.servlet.ServletContext;

import org.mockito.Mockito;

import junit.framework.TestCase;

public class MimeTypeResolverTest
    extends TestCase
{
    private MimeTypeResolverImpl resolver;

    public void setUp()
        throws Exception
    {
        this.resolver = new MimeTypeResolverImpl();

        final String filename = "com/enonic/cms/framework/util/user-defined-mimetypes.properties";
        final URL resource = getClass().getClassLoader().getResource( filename );

        this.resolver.setMimetypesLocation( resource.getFile() );
        this.resolver.setServletContext( Mockito.mock( ServletContext.class ) );
        this.resolver.afterPropertiesSet();
    }

    public void testGetMimeType()
    {
        assertEquals( "text/html", this.resolver.getMimeType( "test.html" ) );
        assertEquals( "image/my-jpeg", this.resolver.getMimeType( "test.jpg" ) );
        assertEquals( "application/octet-stream", this.resolver.getMimeType( "test.unknown" ) );
        assertEquals( "audio/my-midi", this.resolver.getMimeType( "test.mmm" ) );
    }

    public void testGetMimeTypeByExtension()
    {
        assertEquals( "text/html", this.resolver.getMimeTypeByExtension( "html" ) );
        assertEquals( "image/my-jpeg", this.resolver.getMimeTypeByExtension( "jpg" ) );
        assertEquals( "application/octet-stream", this.resolver.getMimeTypeByExtension( "unknown" ) );
    }

    public void testGetExtension()
    {
        assertEquals( "html", this.resolver.getExtension( "text/html" ) );
        assertEquals( "jpeg", this.resolver.getExtension( "image/jpeg" ) );
        assertEquals( "mmm", this.resolver.getExtension( "audio/my-midi" ) );
        assertEquals( "", this.resolver.getExtension( "application/octet-stream" ) );
    }
}
