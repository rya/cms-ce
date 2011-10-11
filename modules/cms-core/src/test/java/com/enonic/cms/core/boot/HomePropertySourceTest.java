package com.enonic.cms.core.boot;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;
import java.io.File;
import java.util.Set;

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
        assertEquals(2, names.length);

        final Set<String> set = Sets.newHashSet(names);
        assertTrue(set.contains("cms.home"));
        assertTrue(set.contains("cms.home.uri"));
    }

    @Test
    public void testPropertyValues()
    {
        final Object o1 = this.source.getProperty("cms.home");
        assertNotNull(o1);
        assertEquals(this.homeDir, o1);

        final Object o2 = this.source.getProperty("cms.home.uri");
        assertNotNull(o2);
        assertEquals(this.homeDir.toURI(), o2);
    }
}
