/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.io.File;
import java.util.Properties;

public final class StartupHelper
{
    public static Properties getProperties()
    {
        final File homeDir = HomeAccessor.getHomeDir();
        final ConfigBuilder configBuilder = new ConfigBuilder( homeDir );
        return configBuilder.loadProperties();
    }
}
