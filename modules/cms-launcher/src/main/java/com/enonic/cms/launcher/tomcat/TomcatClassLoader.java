package com.enonic.cms.launcher.tomcat;

import java.net.URL;
import java.net.URLClassLoader;

final class TomcatClassLoader
    extends URLClassLoader
{
    public TomcatClassLoader()
    {
        super(new URL[0]);
    }

    @Override
    public void addURL(final URL url)
    {
        super.addURL(url);
    }
}
