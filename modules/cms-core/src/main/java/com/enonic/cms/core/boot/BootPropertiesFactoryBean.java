/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

import com.enonic.cms.core.boot.StartupHelper;

public final class BootPropertiesFactoryBean
    implements FactoryBean
{
    public Object getObject()
    {
        return StartupHelper.getProperties();
    }

    public Class getObjectType()
    {
        return Properties.class;
    }

    public boolean isSingleton()
    {
        return true;
    }
}
