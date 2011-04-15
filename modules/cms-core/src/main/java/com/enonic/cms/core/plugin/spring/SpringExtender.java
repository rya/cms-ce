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
        if ( event == null )
        {
            return null;
        }

        if ( OsgiHelper.isFrameworkBundle( bundle ) )
        {
            return null;
        }

        return activate( bundle );
    }

    @Override
    public void removedBundle( final Bundle bundle, final BundleEvent event, final Object object )
    {
        if ( object instanceof SpringHandler )
        {
            ( (SpringHandler) object ).deactivate();
        }
    }

    private SpringHandler activate( final Bundle bundle )
    {
        final SpringHandler context = new SpringHandler( bundle );
        if ( !context.canHandle() )
        {
            return null;
        }

        if ( !context.activate() )
        {
            try
            {
                bundle.stop();
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }

            return null;
        }

        return context;
    }
}
