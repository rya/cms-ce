package com.enonic.cms.core.plugin.deploy;

import com.enonic.cms.core.plugin.installer.BundleInstaller;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import java.io.File;

final class HotDeployListener
    extends FileAlterationListenerAdaptor
{
    private final BundleInstaller installer;

    public HotDeployListener(final BundleInstaller installer)
    {
        this.installer = installer;
    }

    @Override
    public void onFileCreate(final File file)
    {
        this.installer.install(toLocation(file));
    }

    @Override
    public void onFileChange(final File file)
    {
        this.installer.install(toLocation(file));
    }

    @Override
    public void onFileDelete(final File file)
    {
        this.installer.uninstall(toLocation(file));
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
