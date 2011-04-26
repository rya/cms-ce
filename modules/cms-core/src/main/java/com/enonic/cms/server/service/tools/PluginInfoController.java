package com.enonic.cms.server.service.tools;

import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.enonic.cms.core.plugin.ExtensionManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.core.plugin.Plugin;

import com.enonic.cms.core.plugin.ExtensionManagerAccessor;

@RequestMapping(value = "/tools/plugininfo")
public final class PluginInfoController
    extends AbstractToolController
{
    protected ModelAndView doHandleRequest( final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final Long updateKey = getLongParam( req, "update" );

        if ( updateKey != null )
        {
            return doUpdatePlugin( updateKey, req, res );
        }

        final ExtensionManager extensionManager = ExtensionManagerAccessor.getExtensionManager();
        final HashMap<String, Object> model = new HashMap<String, Object>();

        model.put( "functionLibraryExtensions", toWrappers( extensionManager.getAllFunctionLibraries() ) );
        model.put( "autoLoginExtensions", toWrappers( extensionManager.getAllHttpAutoLoginPlugins() ) );
        model.put( "httpInterceptors", toWrappers( extensionManager.getAllHttpInterceptors() ) );
        model.put( "httpResponseFilters", toWrappers( extensionManager.getAllHttpResponseFilters() ) );
        model.put( "taskExtensions", toWrappers( extensionManager.getAllTaskPlugins() ) );
        model.put( "textExtractorExtensions", toWrappers( extensionManager.getAllTextExtractorPlugins() ) );
        model.put( "pluginHandles", toPluginWrappers( extensionManager.getPluginRegistry().getPlugins() ) );

        return new ModelAndView( "pluginInfoPage", model );
    }

    private ModelAndView doUpdatePlugin( final long pluginKey, final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final Plugin plugin = ExtensionManagerAccessor.getExtensionManager().getPluginRegistry().getPluginByKey( pluginKey );
        if ( plugin != null )
        {
            plugin.update();
        }

        res.sendRedirect( req.getHeader( "Referer" ) );
        return null;
    }

    private Collection<ExtensionWrapper> toWrappers( final Collection<? extends Extension> list )
    {
        return ExtensionWrapper.toWrapperList( list );
    }

    private Collection<PluginWrapper> toPluginWrappers( final Collection<? extends Plugin> list )
    {
        return PluginWrapper.toWrapperList( list );
    }

    private Long getLongParam( final HttpServletRequest req, final String key )
    {
        final String value = req.getParameter( key );
        if ( value == null )
        {
            return null;
        }

        try
        {
            return Long.parseLong( value );
        }
        catch ( Throwable e )
        {
            return null;
        }
    }
}
