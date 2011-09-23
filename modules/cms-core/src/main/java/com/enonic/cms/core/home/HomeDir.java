package com.enonic.cms.core.home;

import com.google.common.collect.Maps;
import java.io.File;
import java.net.URI;
import java.util.Map;

public final class HomeDir
    implements HomeConstants
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
        map.put(CMS_HOME, this.dir.toString());
        map.put(CMS_HOME_URI, this.dir.toURI().toString());
        return map;
    }

    public String toString()
    {
        return this.dir.toString();
    }
}
