/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.domain.portal;

import com.enonic.cms.portal.ContentPath;
import com.enonic.cms.portal.ContentPathResolver;
import org.junit.Test;

import junit.framework.TestCase;

import com.enonic.cms.domain.Path;

/**
 * Created by IntelliJ IDEA.
 * User: rmh
 * Date: Apr 8, 2010
 * Time: 12:12:32 PM
 */
public class ContentPathResolverTest
    extends TestCase
{

    @Test
    public void testPathsWithoutContentKey()
    {
        ContentPath resolvedPath = resolvePath( "/This/is/a/test/without/content-key/content-name" );
        assertNull( resolvedPath );

        resolvedPath = resolvePath( "/This/is/a/test/without/content-key/content-name/content-title--" );
        assertNull( resolvedPath );

        resolvedPath = resolvePath( "/This/is/a/test/without/content-key/content-name/content-title-1234" );
        assertNull( resolvedPath );
    }

    @Test
    public void testPathsWithContentKey()
    {
        ContentPath resolvedPath = resolvePath( "/This/is/a/test/with/content-key/content-name--1234" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "This/is/a/test/with/content-key/content-name--1234" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "/This/is/a/test/with/content-key/content-name--1234#withfragment" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "/content-name--1234" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "content-name--1234" );
        verifyContentPath( resolvedPath, "1234", "content-name" );
    }

    @Test
    public void testOldTypeContentPath()
    {
        ContentPath resolvedPath = resolvePath( "/This/is/a/test/with/content-key/content-name.1234.cms" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "/content-name.1234.cms" );
        verifyContentPath( resolvedPath, "1234", "content-name" );

        resolvedPath = resolvePath( "content-name.1234.cms" );
        verifyContentPath( resolvedPath, "1234", "content-name" );
    }

    @Test
    public void testInvalidOldTypeContentPath()
    {
        ContentPath resolvedPath = resolvePath( "/This/is/a/test/with/content-key/content-name.xxxx.cms" );
        assertNull( resolvedPath );

        resolvedPath = resolvePath( "/This/is/a/test/with/content-key/aaa--1234.cms" );
        assertNull( resolvedPath );

        resolvedPath = resolvePath( "/This/is/a/test/with/content-key/aaa.cms" );
        assertNull( resolvedPath );
    }

    private ContentPath resolvePath( String pathAsString )
    {
        return ContentPathResolver.resolveContentPath(new Path(pathAsString));
    }

    private void verifyContentPath( ContentPath resolvedPath, String contentKey, String contentName )
    {
        assertNotNull( resolvedPath );
        assertEquals( contentKey, resolvedPath.getContentKey().toString() );
        assertEquals( contentName, resolvedPath.getContentName() );
    }


}
