package com.enonic.cms.core.plugin.installer;

public interface BundleInstaller
{
    public void install(String location);

    public void uninstall(String location);
}
