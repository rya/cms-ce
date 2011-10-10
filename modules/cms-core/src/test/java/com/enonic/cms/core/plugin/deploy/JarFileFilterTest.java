package com.enonic.cms.core.plugin.deploy;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class JarFileFilterTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private JarFileFilter filter;

    @Before
    public void setUp()
    {
        this.filter = new JarFileFilter();
    }

    @Test
    public void testMatch()
        throws Exception
    {
        assertTrue(this.filter.accept(this.folder.newFile("file.jar")));
    }

    @Test
    public void testNoMatch()
        throws Exception
    {
        assertFalse(this.filter.accept(this.folder.newFolder("folder")));
        assertFalse(this.filter.accept(this.folder.newFile("file")));
        assertFalse(this.filter.accept(this.folder.newFile("file.txt")));
    }
}
