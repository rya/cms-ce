/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.upgrade.service;

import com.enonic.cms.core.SiteKey;

public interface PropertyResolver
{
    public String getProperty( String name );

    public String getProperty( SiteKey siteKey, String name );

    public String getConfigDirPath();

}
