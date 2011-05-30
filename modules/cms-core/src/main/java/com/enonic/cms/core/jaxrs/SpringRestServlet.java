package com.enonic.cms.core.jaxrs;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public abstract class SpringRestServlet
    extends SpringServlet
{
    private final String[] packages;
    private final boolean useGlobalContext;

    public SpringRestServlet(final String... packages)
    {
        this.packages = packages;
        this.useGlobalContext = (this.packages == null) || (this.packages.length == 0);
    }

    @Override
    protected ConfigurableApplicationContext getContext()
    {
        if (this.useGlobalContext) {
            return getDefaultContext();
        }

        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setParent(getDefaultContext());
        context.setServletContext(getServletContext());
        context.setConfigLocations(this.packages);
        context.refresh();
        return context;
    }
}
