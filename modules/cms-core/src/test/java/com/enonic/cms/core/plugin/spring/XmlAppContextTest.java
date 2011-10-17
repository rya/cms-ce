package com.enonic.cms.core.plugin.spring;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import static org.junit.Assert.*;

public class XmlAppContextTest
{
    private XmlAppContext context;
    private ResourcePatternResolver resolver;

    @Before
    public void setUp()
    {
        final Bundle bundle = Mockito.mock(Bundle.class);
        this.resolver = Mockito.mock(ResourcePatternResolver.class);
        
        this.context = new XmlAppContext(bundle);
        this.context.setResourcePatternResolver(this.resolver);
    }

    @Test
    public void testGetResource()
    {
        final Resource res = Mockito.mock(Resource.class);
        Mockito.when(this.resolver.getResource("classpath:META-INF/some.txt")).thenReturn(res);

        final Resource result = this.context.getResource("classpath:META-INF/some.txt");
        assertSame(res, result);
    }

    @Test
    public void testGetResources()
        throws Exception
    {
        final Resource[] res = new Resource[] { Mockito.mock(Resource.class) };
        Mockito.when(this.resolver.getResources("classpath:META-INF/*.txt")).thenReturn(res);

        final Resource[] result = this.context.getResources("classpath:META-INF/*.txt");
        assertSame(res, result);
    }
}
