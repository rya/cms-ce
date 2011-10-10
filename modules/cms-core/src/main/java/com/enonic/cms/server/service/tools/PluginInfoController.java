package com.enonic.cms.server.service.tools;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.ExtensionManager;
import com.enonic.cms.core.plugin.Plugin;

public final class PluginInfoController
    extends AbstractToolController
{
    private ExtensionManager extensionManager;

    public void setExtensionManager(final ExtensionManager extensionManager)
    {
        this.extensionManager = extensionManager;
    }

    protected void doHandleRequest( final HttpServletRequest req, final HttpServletResponse res, ExtendedMap formItems )
    {
        final String updateKey = formItems.getString("update", null);

        if ( updateKey != null )
        {
            doUpdatePlugin(new Long(updateKey), req, res);
        }

        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "functionLibraryExtensions", toWrappers( extensionManager.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensionManager.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensionManager.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensionManager.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensionManager.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( this.extensionManager.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( this.extensionManager.getPluginManager().getPlugins() ) );

        process( res, model, "pluginInfoPage.ftl" );
    }

    private void doUpdatePlugin( final long pluginKey, final HttpServletRequest req, final HttpServletResponse res )
    {
        this.extensionManager.getPluginManager().updatePlugin( pluginKey );

        try
        {
            URL referer = new URL( req.getHeader( "referer" ) );
            redirectClientToURL( referer, res );
        }
        catch ( Exception e )
        {
            //TODO: FIX
        }

    }

    private Collection<ExtensionWrapper> toWrappers( final Collection<? extends Extension> list )
    {
        return ExtensionWrapper.toWrapperList( list );
    }

    private Collection<PluginWrapper> toPluginWrappers( final Collection<? extends Plugin> list )
    {
        return PluginWrapper.toWrapperList( list );
    }
}
