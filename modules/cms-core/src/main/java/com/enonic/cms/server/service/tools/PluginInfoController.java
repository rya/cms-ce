package com.enonic.cms.server.service.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.plugin.*;
import com.enonic.esl.containers.ExtendedMap;
import com.enonic.esl.net.URL;
import com.enonic.vertical.adminweb.AdminHelper;

import com.enonic.cms.api.plugin.ext.Extension;

public final class PluginInfoController
    extends AbstractToolController
{
    private PluginManager pluginManager;

    public void setPluginManager(final PluginManager pluginManager)
    {
        this.pluginManager = pluginManager;
    }

    protected void doHandleRequest( final HttpServletRequest req, final HttpServletResponse res, ExtendedMap formItems )
    {
        final String updateKey = formItems.getString("update", null);

        if ( updateKey != null )
        {
            doUpdatePlugin(new Long(updateKey), req, res);
        }

        final HashMap<String, Object> model = new HashMap<String, Object>();

        final ExtensionSet extensions = this.pluginManager.getExtensions();
        model.put( "baseUrl", AdminHelper.getAdminPath( req, true ) );
        model.put( "functionLibraryExtensions", toWrappers( extensions.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensions.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensions.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensions.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensions.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( extensions.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( this.pluginManager.getPlugins() ) );

        process( res, model, "pluginInfoPage.ftl" );
    }

    private void doUpdatePlugin( final long pluginKey, final HttpServletRequest req, final HttpServletResponse res )
    {
        final PluginHandle handle = this.pluginManager.findPluginByKey(pluginKey);
        if (handle != null) {
            handle.update();
        }

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

    private Collection<ExtensionWrapper> toWrappers( final List<? extends Extension> list )
    {
        return ExtensionWrapper.toWrapperList( list );
    }

    private Collection<PluginWrapper> toPluginWrappers( final List<PluginHandle> list )
    {
        return PluginWrapper.toWrapperList( list );
    }
}
