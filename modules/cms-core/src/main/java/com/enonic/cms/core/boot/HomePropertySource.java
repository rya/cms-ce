package com.enonic.cms.core.boot;

import com.google.common.collect.Maps;
import org.springframework.core.env.MapPropertySource;
import java.io.File;
import java.util.Map;

final class HomePropertySource
    extends MapPropertySource implements HomeConstants
{
    public HomePropertySource(final File dir)
    {
        super("home", buildMap(dir));
    }

    private static Map<String, Object> buildMap(final File dir)
    {
        final Map<String, Object> map = Maps.newHashMap();
        map.put(CMS_HOME, dir);
        return map;
    }
}
