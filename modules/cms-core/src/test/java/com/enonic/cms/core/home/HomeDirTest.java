package com.enonic.cms.core.home;

import static junit.framework.Assert.*;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class HomeDirTest
{
    @Test
    public void testSimple()
    {
        final File file = new File("some-directory");
        final HomeDir homeDir = new HomeDir(file);

        assertEquals(file, homeDir.getFile());
        assertEquals(file.toURI(), homeDir.getUri());
        assertEquals(file.toString(), homeDir.toString());
    }

    @Test
    public void testGetMap()
    {
        final File file = new File("some-directory");
        final HomeDir homeDir = new HomeDir(file);
        final Map<String, String> map = homeDir.getMap();

        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals(file.toString(), map.get("cms.home"));
        assertEquals(file.toURI().toString(), map.get("cms.home.uri"));
    }
}
