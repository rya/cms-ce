package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.BundleContext;
import org.springframework.stereotype.Component;

import com.enonic.cms.core.plugin.container.OsgiContributor;

@Component
public final class SpringActivator
    extends OsgiContributor
{
    private SpringExtender tracker;

    public SpringActivator()
    {
        super(5);
    }

    public void start( final BundleContext context )
        throws Exception
    {
        this.tracker = new SpringExtender( context );
        this.tracker.open();
    }

    public void stop( final BundleContext context )
        throws Exception
    {
        this.tracker.close();
    }
}
