package com.enonic.cms.core.plugin.spring;

import java.util.Enumeration;

import org.osgi.framework.Bundle;
import org.springframework.osgi.io.OsgiBundleResource;

final class SpringContextFinder
{
    private static final String CONTEXT_DIR = "/META-INF/spring/";

    private static final String CONTEXT_FILES = "*.xml";

    private static final String DEFAULT_CONFIG = OsgiBundleResource.BUNDLE_URL_PREFIX + CONTEXT_DIR + CONTEXT_FILES;

    public String[] findContexts( final Bundle bundle )
    {
        final Enumeration defaultConfig = bundle.findEntries( CONTEXT_DIR, CONTEXT_FILES, false );
        if ( ( defaultConfig != null ) && defaultConfig.hasMoreElements() )
        {
            return new String[]{DEFAULT_CONFIG};
        }
        else
        {
            return new String[0];
        }
    }
}
