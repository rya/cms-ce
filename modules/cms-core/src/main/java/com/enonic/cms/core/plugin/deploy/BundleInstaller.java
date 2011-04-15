package com.enonic.cms.core.plugin.deploy;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.PackageAdmin;

import com.enonic.cms.api.util.LogFacade;
import com.enonic.cms.core.plugin.util.OsgiHelper;

final class BundleInstaller
{
    private final static LogFacade LOG = LogFacade.get( BundleInstaller.class );

    private final BundleContext context;

    private final FileScanner scanner;

    private final PackageAdmin packageAdmin;

    public BundleInstaller( final BundleContext context, final PackageAdmin packageAdmin, final FileScanner scanner )
    {
        this.context = context;
        this.scanner = scanner;
        this.packageAdmin = packageAdmin;
    }

    private Bundle install( final File file )
    {
        if ( !OsgiHelper.isOsgiBundle( file ) )
        {
            LOG.warning( "File [{0}] not a valid OSGi bundle. Will not install.", file.getAbsolutePath() );
            return null;
        }

        final String location = toLocation( file );
        final Bundle bundle = findBundle( location );

        if ( bundle != null )
        {
            return doUpdate( bundle );
        }
        else
        {
            return doInstall( location );
        }
    }

    private Bundle update( final File file )
    {
        if ( !OsgiHelper.isOsgiBundle( file ) )
        {
            LOG.warning( "File [{0}] not a valid OSGi bundle. Will not update.", file.getAbsolutePath() );
            return null;
        }

        final String location = toLocation( file );
        final Bundle bundle = findBundle( location );

        if ( bundle != null )
        {
            return doUpdate( bundle );
        }
        else
        {
            return null;
        }
    }

    private Bundle uninstall( final File file )
    {
        final String location = toLocation( file );
        final Bundle bundle = findBundle( location );

        if ( bundle != null )
        {
            return doUninstall( bundle );
        }
        else
        {
            return null;
        }
    }

    private Bundle doInstall( final String location )
    {
        if ( location == null )
        {
            return null;
        }

        try
        {
            return this.context.installBundle( location );
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error installing bundle from location [{0}]", location );
        }

        return null;
    }

    private Bundle doUpdate( final Bundle bundle )
    {
        if ( bundle == null )
        {
            return null;
        }

        try
        {
            bundle.update();
            return bundle;
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error updating bundle [{0}]", bundle.getSymbolicName() );
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
            return bundle;
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error occurred removing bundle [{0}]", bundle.getSymbolicName() );
        }

        return null;
    }

    private Bundle findBundle( final String location )
    {
        if ( location == null )
        {
            return null;
        }

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
            return null;
        }
    }

    public void process()
    {
        this.scanner.scan();

        final Collection<Bundle> uninstalled = uninstall( this.scanner.getDeleted() );
        final Collection<Bundle> updated = update( this.scanner.getModified() );
        final Collection<Bundle> installed = install( this.scanner.getAdded() );

        if ( !uninstalled.isEmpty() || !updated.isEmpty() )
        {
            refresh();
        }

        if ( uninstalled.isEmpty() && installed.isEmpty() && updated.isEmpty() )
        {
            return;
        }

        startAllBundles();
    }

    private Collection<Bundle> uninstall( final Set<File> files )
    {
        final ArrayList<Bundle> list = new ArrayList<Bundle>();
        for ( final File file : files )
        {
            final Bundle bundle = uninstall( file );
            if ( bundle != null )
            {
                list.add( bundle );
            }
        }

        return list;
    }

    private Collection<Bundle> update( final Set<File> files )
    {
        final ArrayList<Bundle> list = new ArrayList<Bundle>();
        for ( final File file : files )
        {
            final Bundle bundle = update( file );
            if ( bundle != null )
            {
                list.add( bundle );
            }
        }

        return list;
    }

    private Collection<Bundle> install( final Set<File> files )
    {
        final ArrayList<Bundle> list = new ArrayList<Bundle>();
        for ( final File file : files )
        {
            final Bundle bundle = install( file );
            if ( bundle != null )
            {
                list.add( bundle );
            }
        }

        return list;
    }

    private void start( final Collection<Bundle> bundles )
    {
        for ( final Bundle bundle : bundles )
        {
            start( bundle );
        }
    }

    private void start( final Bundle bundle )
    {
        if ( OsgiHelper.isFragment( bundle ) )
        {
            return;
        }

        if ( isStarting( bundle ) )
        {
            return;
        }

        try
        {
            bundle.start( 0 );
        }
        catch ( Exception e )
        {
            LOG.error( e, "Error starting bundle [{0}]", bundle.getSymbolicName() );
        }
    }

    private boolean isStarting( final Bundle bundle )
    {
        return ( bundle.getState() == Bundle.STARTING ) || ( bundle.getState() == Bundle.ACTIVE );
    }

    private void startAllBundles()
    {
        final ArrayList<Bundle> bundles = new ArrayList<Bundle>();
        for ( final Bundle bundle : this.context.getBundles() )
        {
            if ( bundle.getBundleId() > 0 )
            {
                if ( !isStarting( bundle ) )
                {
                    bundles.add( bundle );
                }
            }
        }

        start( bundles );
    }

    private void refresh()
    {
        if ( this.packageAdmin != null )
        {
            this.packageAdmin.refreshPackages( null );
        }
    }
}
