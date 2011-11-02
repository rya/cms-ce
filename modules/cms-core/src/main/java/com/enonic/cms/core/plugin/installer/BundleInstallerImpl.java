package com.enonic.cms.core.plugin.installer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.enonic.cms.api.util.LogFacade;

final class BundleInstallerImpl
    implements BundleInstaller
{
    private final static LogFacade LOG = LogFacade.get( BundleInstallerImpl.class );

    private final BundleContext context;

    public BundleInstallerImpl(final BundleContext context)
    {
        this.context = context;
    }

    public void install( final String location )
    {
        final String url = convertLocation(location);
        final Bundle bundle = findBundle(url);

        if (bundle != null) {
            doUpdate(bundle);
        } else {
            doInstall(url);
        }
    }

    public void uninstall( final String location )
    {
        final String url = convertLocation(location);
        final Bundle bundle = findBundle( url );
        if ( bundle != null )
        {
            doUninstall( bundle );
        }
    }

    private void doUpdate( final Bundle bundle )
    {
        try
        {
            bundle.update();
            bundle.start(0);
        }
        catch ( Exception e )
        {
            LOG.errorCause("Error updating plugin from location [{0}]", e, bundle.getLocation());
        }
    }

    private void doInstall( final String location )
    {
        try
        {
            final Bundle bundle = this.context.installBundle( location );
            bundle.start(0);
        }
        catch ( Exception e )
        {
            LOG.errorCause("Error installing plugin from location [{0}]", e, location);
        }
    }

    private void doUninstall( final Bundle bundle )
    {
        try
        {
            bundle.uninstall();
        }
        catch ( Exception e )
        {
            LOG.errorCause("Error occurred removing plugin [{0}]", e, bundle.getSymbolicName());
        }
    }

    private Bundle findBundle( final String location )
    {
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( location.equals( bundle.getLocation() ) )
            {
                return bundle;
            }
        }

        return null;
    }

    private String convertLocation(final String location)
    {
        return TransformerStreamHandler.SCHEME + ":" + location;
    }
}
