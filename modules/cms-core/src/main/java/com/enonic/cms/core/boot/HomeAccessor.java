/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.io.File;

import com.enonic.cms.core.resolver.HomeResolver;

public final class HomeAccessor
{
    private static File DIR;

    public static File getHomeDir()
    {
        if (DIR == null) {
            DIR = resolve();
        }

        return DIR;
    }

    private static File resolve()
    {
        final HomeResolver homeResolver = new HomeResolver();
        return homeResolver.resolve();
    }
}
