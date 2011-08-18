package com.enonic.cms.core.jaxrs;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class SpringRestServlet
    extends SpringServlet
{
    private final String basePackage;

    public SpringRestServlet(final String basePackage)
    {
        this.basePackage = basePackage;
    }

    @Override
    protected ConfigurableApplicationContext getContext()
    {
        final AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setParent(WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()));
        context.setConfigLocation(this.basePackage);
        context.refresh();
        
        return context;
    }
}
