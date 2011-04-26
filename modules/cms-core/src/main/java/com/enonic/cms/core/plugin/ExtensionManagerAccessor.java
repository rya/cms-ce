package com.enonic.cms.core.plugin;

/**
 * This class implements the plugin manager accessor.
 */
public final class ExtensionManagerAccessor
{
    /**
     * Plugin manager.
     */
    private static ExtensionManager PLUGIN_MANAGER;

    /**
     * Return the plugin manager.
     */
    public static ExtensionManager getExtensionManager()
    {
        return PLUGIN_MANAGER;
    }

    /**
     * Set the plugin manager.
     */
    public static void setExtensionManager( ExtensionManager pluginManager )
    {
        PLUGIN_MANAGER = pluginManager;
    }
}
