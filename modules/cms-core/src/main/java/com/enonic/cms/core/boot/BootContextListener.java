package com.enonic.cms.core.boot;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import javax.servlet.ServletContext;

public final class BootContextListener
    extends ContextLoaderListener
{
    @Override
    protected void configureAndRefreshWebApplicationContext(final ConfigurableWebApplicationContext appContext,
                                                            final ServletContext servletContext)
    {
        BootEnvironment.start(appContext.getEnvironment());
        super.configureAndRefreshWebApplicationContext(appContext, servletContext);
    }
}
