package com.enonic.cms.core.plugin.spring;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import com.enonic.cms.core.plugin.util.OsgiHelper;

final class SpringExtender
    extends BundleTracker
{
    public SpringExtender( final BundleContext context )
    {
        super( context, Bundle.ACTIVE, null );
    }

    @Override
    public Object addingBundle( final Bundle bundle, final BundleEvent event )
    {
        if ( OsgiHelper.isFrameworkBundle( bundle ) )
        {
            return null;
        }

        final SpringHandler context = new SpringHandler( bundle );
        context.activate();
        return context;
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Object object )
    {
        if ( object instanceof SpringHandler )
        {
            ( (SpringHandler) object ).deactivate();
        }
    }
}
