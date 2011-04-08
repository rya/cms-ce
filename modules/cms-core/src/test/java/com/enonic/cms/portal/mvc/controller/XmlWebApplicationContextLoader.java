/*
 * Copyright 2000-2011 Enonic AS
 * http://www.enonic.com/license
 */
package com.enonic.cms.portal.mvc.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AbstractGenericContextLoader;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * Project : cms-server Created : May 11, 2009
 */
public class XmlWebApplicationContextLoader
    extends AbstractContextLoader
{
    protected static final Log logger = LogFactory.getLog( AbstractGenericContextLoader.class );


    public final ConfigurableApplicationContext loadContext( String... locations )
        throws Exception
    {
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "Loading ApplicationContext for locations [" + StringUtils.arrayToCommaDelimitedString( locations ) + "]." );
        }
        GenericApplicationContext context = new GenericWebApplicationContext();
        prepareContext( context );
        customizeBeanFactory( context.getDefaultListableBeanFactory() );
        createBeanDefinitionReader( context ).loadBeanDefinitions( locations );
        AnnotationConfigUtils.registerAnnotationConfigProcessors( context );
        customizeContext( context );
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.springframework.test.context.support.AbstractContextLoader#getResourceSuffix()
     */
    @Override
    protected String getResourceSuffix()
    {
        return "-context.xml";
    }

    protected void prepareContext( GenericApplicationContext context )
    {
    }

    protected void customizeBeanFactory( DefaultListableBeanFactory beanFactory )
    {
    }

    protected BeanDefinitionReader createBeanDefinitionReader( GenericApplicationContext context )
    {
        return new XmlBeanDefinitionReader( context );
    }

    protected void customizeContext( GenericApplicationContext context )
    {
    }


}