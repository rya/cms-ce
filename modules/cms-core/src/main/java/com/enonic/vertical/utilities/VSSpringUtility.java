/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.vertical.utilities;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class VSSpringUtility
{

    public static void autoWireObject( Object object, ServletContext servletContext )
    {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext( servletContext );
        ConfigurableApplicationContext configureContext = (ConfigurableApplicationContext) context;
        AutowireCapableBeanFactory beanFactory = configureContext.getBeanFactory();
        beanFactory.autowireBeanProperties( object, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false );
    }

}
