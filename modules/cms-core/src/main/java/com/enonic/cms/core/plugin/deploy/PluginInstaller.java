package com.enonic.cms.core.plugin.deploy;

import com.enonic.cms.api.util.LogFacade;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;
import java.io.File;

final class PluginInstaller
    extends FileAlterationListenerAdaptor
{
    private final static LogFacade LOG = LogFacade.get( PluginInstaller.class );

    private final BundleContext context;

    private final PackageAdmin packageAdmin;

    public PluginInstaller(final BundleContext context, final PackageAdmin packageAdmin)
    {
        this.context = context;
        this.packageAdmin = packageAdmin;
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

        if (bundle == null) {
            bundle = doInstall(location);
        } else {
            doUpdate(bundle);
        }

        if (bundle != null) {
            start(bundle);
            refresh();
        }
    }

    private void uninstall( final File file )
    {
        final String location = toLocation( file );
        final Bundle bundle = findBundle( location );

        if ( bundle != null )
        {
            doUninstall( bundle );
            refresh();
        }
    }

    private Bundle doInstall( final String location )
    {
        try
        {
            final Bundle bundle = this.context.installBundle( location );
            LOG.info( "Installed plugin [{0}] from location [{1}]", bundle.getSymbolicName(), location );
            return bundle;
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error installing plugin from location [{0}]", location );
        }

        return null;
    }

    private Bundle doUpdate( final Bundle bundle )
    {
        try
        {
            bundle.update();
            LOG.info( "Updated plugin [{0}] from location [{1}]", bundle.getSymbolicName(), bundle.getLocation() );

            return bundle;
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error updating plugin [{0}]", bundle.getSymbolicName() );
            return null;
        }
    }

    private Bundle doUninstall( final Bundle bundle )
    {
        if ( bundle == null )
        {
            return null;
        }

        try
        {
            bundle.uninstall();
            LOG.info( "Uninstalled plugin [{0}] from location [{1}]", bundle.getSymbolicName(), bundle.getLocation() );

            return bundle;
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error occurred removing plugin [{0}]", bundle.getSymbolicName() );
        }

        return null;
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

    private void start( final Bundle bundle )
    {
        try
        {
            bundle.start( 0 );
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error starting plugin [{0}]", bundle.getSymbolicName() );
        }
    }

    private void refresh()
    {
        this.packageAdmin.refreshPackages( null );
    }
}
