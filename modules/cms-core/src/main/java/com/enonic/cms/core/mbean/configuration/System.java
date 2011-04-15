/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.mbean.configuration;

import java.util.Properties;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.enonic.cms.api.Version;

public class System
    implements SystemMBean
{
    @Inject
    @Qualifier("loadedVerticalProperties")
    private Properties properties;

    @ManagedAttribute
    public String getCmsVersion()
    {
        return Version.getVersion();
    }

    @ManagedAttribute
    public Properties getCmsProperties()
    {
        return properties;
    }

    @ManagedAttribute
    public String getSaxonVersion()
    {
        return net.sf.saxon.Version.getProductVersion();
    }
}