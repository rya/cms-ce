/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.configuration;

import java.util.Properties;

public interface SiteMBean
{
    int getSiteKey();

    Properties getSiteProperties();

    String getSiteUrl();

    boolean getPageCacheEnabled();
}
