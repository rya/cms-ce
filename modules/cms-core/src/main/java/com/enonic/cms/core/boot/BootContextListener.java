package com.enonic.cms.core.boot;

import javax.servlet.ServletContext;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

public final class BootContextListener
    extends ContextLoaderListener
{
    @Override
    protected void configureAndRefreshWebApplicationContext(final ConfigurableWebApplicationContext appContext,
                                                            final ServletContext servletContext)
    {
        BootEnvironment.configure(appContext.getEnvironment());
        super.configureAndRefreshWebApplicationContext(appContext, servletContext);
    }
}
