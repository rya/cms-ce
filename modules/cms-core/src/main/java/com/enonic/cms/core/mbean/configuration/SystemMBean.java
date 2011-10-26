/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.configuration;

import java.util.Properties;

public interface SystemMBean
{
    String getCmsVersion();

    int getDatabaseModelVersion();

    Properties getCmsProperties();

    String getSaxonVersion();
}
