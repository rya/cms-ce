package com.enonic.cms.core.plugin.spring;

import java.io.IOException;

import org.osgi.framework.Bundle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.osgi.io.OsgiBundleResourcePatternResolver;

final class XmlAppContext
    extends ClassPathXmlApplicationContext
{
    private ResourcePatternResolver resolver;

    public XmlAppContext( final Bundle bundle )
    {
        setClassLoader( new BundleClassLoader( bundle ) );
        setConfigLocation("classpath:META-INF/spring/context.xml");
        setResourcePatternResolver(new OsgiBundleResourcePatternResolver(bundle));
    }

    public void setResourcePatternResolver(final ResourcePatternResolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public Resource getResource(final String location)
    {
        return this.resolver.getResource(location);
    }

    @Override
    public Resource[] getResources(final String locationPattern)
        throws IOException
    {
        return this.resolver.getResources(locationPattern);
    }
}
