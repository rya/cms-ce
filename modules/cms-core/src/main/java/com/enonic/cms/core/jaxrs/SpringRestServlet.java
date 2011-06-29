package com.enonic.cms.core.jaxrs;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.context.ConfigurableApplicationContext;

public final class SpringRestServlet
    extends SpringServlet
{
    @Override
    protected ConfigurableApplicationContext getContext()
    {
        return getDefaultContext();
    }
}
