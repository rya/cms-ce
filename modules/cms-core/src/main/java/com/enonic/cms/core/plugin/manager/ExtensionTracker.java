package com.enonic.cms.core.plugin.manager;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import com.enonic.cms.api.plugin.ext.Extension;

final class ExtensionTracker
    extends ServiceTracker
{
    private final ExtensionHolder holder;

    public ExtensionTracker(final BundleContext context, final ExtensionHolder holder)
    {
        super( context, Extension.class.getName(), null );
        this.holder = holder;
    }

    @Override
    public Object addingService( final ServiceReference ref )
    {
        final Object ext = super.addingService( ref );

        if ( ext instanceof Extension )
        {
            this.holder.add(ref, (Extension)ext);
        }

        return ext;
    }

    @Override
    public void removedService( final ServiceReference ref, final Object service )
    {
        this.holder.remove(ref);
        super.removedService( ref, service );
    }
}
