/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import junit.framework.TestCase;

public class MimeTypeResolverTest
    extends TestCase
{
    private MimeTypeResolver resolver;

    public void setUp()
    {
        this.resolver = MimeTypeResolver.getInstance();
    }

    public void testLookup()
    {
        assertEquals( "text/html", this.resolver.getMimeType( "test.html" ) );
        assertEquals( "image/jpeg", this.resolver.getMimeType( "test.jpg" ) );
        assertEquals( "application/octet-stream", this.resolver.getMimeType( "test.unknown" ) );
    }

    public void testLookupByExtension()
    {
        assertEquals( "text/html", this.resolver.getMimeTypeByExtension( "html" ) );
        assertEquals( "image/jpeg", this.resolver.getMimeTypeByExtension( "jpg" ) );
        assertEquals( "application/octet-stream", this.resolver.getMimeTypeByExtension( "unknown" ) );
    }

    public void testReverseLookup()
    {
        assertEquals( "html", this.resolver.getExtension( "text/html" ) );
        assertEquals( "jpeg", this.resolver.getExtension( "image/jpeg" ) );
        assertEquals( "", this.resolver.getExtension( "application/octet-stream" ) );
    }
}
