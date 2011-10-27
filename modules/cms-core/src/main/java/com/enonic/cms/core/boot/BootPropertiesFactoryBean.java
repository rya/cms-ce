/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.core.boot;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

public final class BootPropertiesFactoryBean
    implements FactoryBean
{
    public Object getObject()
    {
        return new Properties();
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
