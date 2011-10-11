package com.enonic.cms.core.boot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import java.io.File;

public class HomePropertySourceTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File homeDir;
    private HomePropertySource source;

    @Before
    public void setUp()
    {
        this.homeDir = this.folder.newFolder("cms-home");
        this.source = new HomePropertySource(this.homeDir);
    }

    @Test
    public void testPropertyNames()
    {
        final String[] names = this.source.getPropertyNames();
        assertNotNull(names);
        assertEquals(1, names.length);
        assertEquals("cms.home", names[0]);
    }

    @Test
    public void testPropertyValues()
    {
        final Object o = this.source.getProperty("cms.home");
        assertNotNull(o);
        assertEquals(this.homeDir, o);
    }
}
