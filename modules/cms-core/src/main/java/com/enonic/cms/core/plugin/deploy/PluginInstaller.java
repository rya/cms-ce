package com.enonic.cms.core.plugin.deploy;

import com.enonic.cms.api.util.LogFacade;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import java.io.File;

final class PluginInstaller
    extends FileAlterationListenerAdaptor
{
    private final static LogFacade LOG = LogFacade.get( PluginInstaller.class );

    private final BundleContext context;

    public PluginInstaller(final BundleContext context)
    {
        this.context = context;
    }

    @Override
    public void onFileCreate(final File file)
    {
        install(file);
    }

    @Override
    public void onFileChange(final File file)
    {
        install(file);
    }

    @Override
    public void onFileDelete(final File file)
    {
        uninstall(file);
    }

    private void install( final File file )
    {
        final String location = toLocation( file );
        Bundle bundle = findBundle( location );

        if (bundle != null) {
            doUpdate(bundle);
        } else {
            doInstall(location);
        }
    }

    private void uninstall( final File file )
    {
        final String location = toLocation( file );
        final Bundle bundle = findBundle( location );

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

    private String toLocation( final File file )
    {
        try
        {
            return file.toURI().toURL().toExternalForm();
        }
        catch ( Exception e )
        {
            throw new AssertionError(e);
        }
    }
}
