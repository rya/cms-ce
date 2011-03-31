/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.framework.cache.standard;

import com.enonic.cms.framework.cache.BasicCacheTest;
import com.enonic.cms.framework.cache.base.AbstractCacheManager;

public class StandardCacheBasicTest
    extends BasicCacheTest
{
    protected AbstractCacheManager createManager()
        throws Exception
    {
        return new StandardCacheManager();
    }
}
