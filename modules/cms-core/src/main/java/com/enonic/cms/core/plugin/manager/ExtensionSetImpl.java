package com.enonic.cms.core.plugin.manager;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.api.plugin.ext.http.HttpProcessor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.google.common.collect.Lists;
import java.util.*;

final class ExtensionSetImpl
    implements ExtensionSet
{
    private final List<Extension> list;

    public ExtensionSetImpl(final List<Extension> list)
    {
        this.list = list;
    }

    public List<Extension> getAllExtensions()
    {
        return this.list;
    }

    @SuppressWarnings("unchecked")
    private <T extends Extension> List<T> getExtensions( final Class<T> type )
    {
        final ArrayList<T> result = Lists.newArrayList();
        for (final Extension ext : this.list) {
            if (type.isAssignableFrom(ext.getClass())) {
                result.add((T)ext);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends HttpProcessor> List<T> findHttpProcessorPlugins( Class<T> type, String path )
    {
        final LinkedList<T> list = new LinkedList<T>();
        for ( final T plugin : getExtensions(type) )
        {
            if ( plugin.matchesUrlPattern( path ) )
            {
                list.add( plugin );
            }
        }

        Collections.sort(list);
        return list;
    }

    public FunctionLibrary findFunctionLibrary( final String namespace )
    {
        for ( final FunctionLibrary plugin : getAllFunctionLibraries() )
        {
            if ( namespace.equals( plugin.getName() ) )
            {
                return plugin;
            }
        }

        return null;
    }

    public TaskHandler findTaskPlugin( final String name )
    {
        for ( final TaskHandler plugin : getAllTaskPlugins() )
        {
            if ( name.equals( plugin.getName() ) )
            {
                return plugin;
            }
        }
        
        return null;
    }

    public TextExtractor findTextExtractorPluginByMimeType( final String mimeType )
    {
        for ( final TextExtractor plugin : getAllTextExtractorPlugins() )
        {
            if ( plugin.canHandle( mimeType ) )
            {
                return plugin;
            }
        }
        return null;
    }

    public List<HttpInterceptor> findMatchingHttpInterceptors( final String path )
    {
        return findHttpProcessorPlugins( HttpInterceptor.class, path );
    }

    public List<HttpInterceptor> getAllHttpInterceptors()
    {
        final List<HttpInterceptor> plugins = getExtensions(HttpInterceptor.class);
        Collections.sort( plugins );
        return plugins;
    }

    public List<HttpResponseFilter> findMatchingHttpResponseFilters( final String path )
    {
        return findHttpProcessorPlugins( HttpResponseFilter.class, path );
    }

    public List<HttpResponseFilter> getAllHttpResponseFilters()
    {
        final List<HttpResponseFilter> plugins = getExtensions(HttpResponseFilter.class);
        Collections.sort( plugins );
        return plugins;
    }

    public HttpAutoLogin findMatchingHttpAutoLoginPlugin( final String path )
    {
        final Collection<HttpAutoLogin> list = findHttpProcessorPlugins( HttpAutoLogin.class, path );
        return list.isEmpty() ? null : list.iterator().next();
    }

    public List<HttpAutoLogin> getAllHttpAutoLoginPlugins()
    {
        final List<HttpAutoLogin> plugins = getExtensions(HttpAutoLogin.class);
        Collections.sort( plugins );
        return plugins;
    }

    public List<FunctionLibrary> getAllFunctionLibraries()
    {
        return getExtensions(FunctionLibrary.class);
    }

    public List<TaskHandler> getAllTaskPlugins()
    {
        return getExtensions(TaskHandler.class);
    }

    public List<TextExtractor> getAllTextExtractorPlugins()
    {
        return getExtensions(TextExtractor.class);
    }

    public Iterator<Extension> iterator()
    {
        return this.list.iterator();
    }
}
