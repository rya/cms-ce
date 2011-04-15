package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.springframework.osgi.context.support.OsgiBundleXmlApplicationContext;

import com.enonic.cms.api.plugin.PluginContext;
import com.enonic.cms.api.plugin.PluginException;
import com.enonic.cms.api.plugin.ext.Extension;

final class SpringHandler
{
    private final Bundle bundle;

    private final String[] configs;

    private OsgiBundleXmlApplicationContext app;

    public SpringHandler( final Bundle bundle )
    {
        this.bundle = bundle;
        this.configs = new SpringContextFinder().findContexts( this.bundle );
    }

    public boolean canHandle()
    {
        return this.configs != null;
    }

    public boolean activate()
    {
        try
        {
            doActivate();
            return true;
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public void deactivate()
    {
        try
        {
            doDeactivate();
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
    }

    private void doActivate()
        throws Exception
    {
        final PluginContext context = lookupPluginContext();

        this.app = new XmlAppContext( this.bundle, this.configs );
        this.app.addBeanFactoryPostProcessor( new BeansProcessor( context ) );
        this.app.addBeanFactoryPostProcessor( new ConfigProcessor( context ) );
        this.app.refresh();

        for ( final Object ext : this.app.getBeansOfType( Extension.class ).values() )
        {
            context.register( (Extension) ext );
        }
    }

    private void doDeactivate()
        throws Exception
    {
        if ( this.app.isActive() )
        {
            this.app.close();
        }
    }

    private PluginContext lookupPluginContext()
    {
        final BundleContext context = this.bundle.getBundleContext();
        final ServiceReference ref = context.getServiceReference( PluginContext.class.getName() );

        if ( ref == null )
        {
            throw new PluginException( "Failed to find plugin context for bundle [{0}]", this.bundle.getSymbolicName() );
        }

        return (PluginContext) context.getService( ref );
    }
}
