package com.enonic.cms.core.home;

import com.google.common.collect.Maps;
import java.io.File;
import java.net.URI;
import java.util.Map;

public final class HomeDir
{
    private final File dir;

    public HomeDir(final File dir)
    {
        this.dir = dir;
    }

    public File getFile()
    {
        return this.dir;
    }

    public URI getUri()
    {
        return this.dir.toURI();
    }

    public Map<String, String> getMap()
    {
        final Map<String, String> map = Maps.newHashMap();
        map.put("cms.home", this.dir.toString());
        map.put("cms.home.uri", this.dir.toURI().toString());
        return map;
    }

    public String toString()
    {
        return this.dir.toString();
    }
}
