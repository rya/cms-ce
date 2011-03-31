/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.util;

import java.io.File;

import junit.framework.TestCase;

public class FileUtilTest
    extends TestCase
{
    public void testTempDir()
    {
        File dir1 = FileUtil.createTempDir( null, null, true );
        assertTrue( dir1.isDirectory() );
        assertTrue( dir1.exists() );

        File dir2 = FileUtil.createTempDir( null, null, true );
        assertTrue( dir2.isDirectory() );
        assertTrue( dir2.exists() );
        assertFalse( dir1.equals( dir2 ) );
    }
}
