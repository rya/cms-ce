package com.enonic.cms.core.plugin.installer;

import com.enonic.cms.core.plugin.spring.SpringActivator;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestTransformerImplTest
{
    private Manifest mf;
    private Attributes attr;
    private ManifestTransformerImpl transformer;
    private Set<String> entries;

    @Before
    public void setUp()
    {
        this.mf = new Manifest();
        this.attr = this.mf.getMainAttributes();
        this.transformer = new ManifestTransformerImpl();
        this.entries = Sets.newHashSet();
    }

    @Test(expected = IOException.class)
    public void testIllegal()
        throws Exception
    {
        transform();
    }

    @Test
    public void testMinimal()
        throws Exception
    {
        this.attr.putValue("Plugin-Id", "some.id");
        transform();

        assertEquals("some.id", this.attr.getValue("Bundle-SymbolicName"));
        assertEquals("some.id", this.attr.getValue("Bundle-Name"));
        assertEquals("0.0.0", this.attr.getValue("Bundle-Version"));
        assertEquals(SpringActivator.class.getName(), this.attr.getValue("Bundle-Activator"));
        assertEquals(".", this.attr.getValue("Bundle-ClassPath"));
        assertEquals("2", this.attr.getValue("Bundle-ManifestVersion"));
        assertEquals("org.apache.felix.framework", this.attr.getValue("Require-Bundle"));
    }

    @Test
    public void testClassPath()
        throws Exception
    {
        this.entries.add("some/resource.txt");
        this.entries.add("META-INF/lib/lib1.jar");
        this.entries.add("META-INF/lib/lib2.jar");
        this.entries.add("lib/lib2.jar");

        this.attr.putValue("Plugin-Id", "some.id");
        transform();

        assertEquals("some.id", this.attr.getValue("Bundle-SymbolicName"));
        assertEquals(".,/META-INF/lib/lib1.jar,/META-INF/lib/lib2.jar", this.attr.getValue("Bundle-ClassPath"));
    }

    @Test
    public void testPluginMeta()
        throws Exception
    {
        this.attr.putValue("Plugin-Id", "some.id");
        this.attr.putValue("Plugin-Name", "Some Name");
        this.attr.putValue("Plugin-Version", "1.1.1");
        transform();

        assertEquals("some.id", this.attr.getValue("Bundle-SymbolicName"));
        assertEquals("Some Name", this.attr.getValue("Bundle-Name"));
        assertEquals("1.1.1", this.attr.getValue("Bundle-Version"));
    }

    @Test
    public void testOSGiMeta()
        throws Exception
    {
        this.attr.putValue("Bundle-SymbolicName", "some.id");
        this.attr.putValue("Bundle-Name", "Some Name");
        this.attr.putValue("Bundle-Version", "1.1.1");
        transform();

        assertEquals("some.id", this.attr.getValue("Bundle-SymbolicName"));
        assertEquals("Some Name", this.attr.getValue("Bundle-Name"));
        assertEquals("1.1.1", this.attr.getValue("Bundle-Version"));
    }

    @Test
    public void testPriorityMeta()
        throws Exception
    {
        this.attr.putValue("Plugin-Id", "from.plugin.id");
        this.attr.putValue("Bundle-SymbolicName", "from.bundle.id");
        transform();

        assertEquals("from.plugin.id", this.attr.getValue("Bundle-SymbolicName"));
    }

    @Test
    public void testRemoveUnusedMeta()
        throws Exception
    {
        this.attr.putValue("Plugin-Id", "some.id");
        this.attr.putValue("Import-Package", "some.package");
        this.attr.putValue("Export-Package", "some.package");

        transform();

        assertEquals("some.id", this.attr.getValue("Bundle-SymbolicName"));
        assertNull(this.attr.getValue("Import-Package"));
        assertNull(this.attr.getValue("Export-Package"));
    }

    private void transform()
        throws Exception
    {
        this.transformer.transform(this.mf, this.entries);
    }
}
