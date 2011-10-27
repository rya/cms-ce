package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.Bundle;

import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class SpringHandler
{
    private final static LogFacade LOG = LogFacade.get(SpringHandler.class);

    private final Bundle bundle;
    private XmlAppContext app;

    public SpringHandler( final Bundle bundle )
    {
        this.bundle = bundle;
    }

    public void activate()
    {
        try {
            doActivate();
        } catch (final Throwable e) {
            handleError(e);
        }
    }

    private PluginContext lookupPluginContext()
    {
        return OsgiHelper.requireService(this.bundle.getBundleContext(), PluginContext.class);
    }

    private void doActivate()
    {
        doActivate(lookupPluginContext());
    }

    private void doActivate(final PluginContext context)
    {
        this.app = new XmlAppContext( this.bundle );
        this.app.addBeanFactoryPostProcessor(new BeansProcessor(context));
        this.app.addBeanFactoryPostProcessor( new ConfigProcessor( context ) );
        this.app.refresh();

        for ( final Extension ext : this.app.getBeansOfType( Extension.class ).values() )
        {
            context.register( ext );
        }
    }

    public void deactivate()
    {
        if (this.app == null) {
            return;
        }

        if ( !this.app.isActive() ) {
            return;
        }

        this.app.close();
    }

    private void handleError(final Throwable cause)
    {
        LOG.errorCause("Failed to start plugin [{0}]", cause, this.bundle.getSymbolicName());

        try {
            this.bundle.stop();
        } catch (final Exception e) {
            LOG.warningCause("Exception when stopping plugin [{0}]", e, this.bundle.getSymbolicName());
        }
    }
}
