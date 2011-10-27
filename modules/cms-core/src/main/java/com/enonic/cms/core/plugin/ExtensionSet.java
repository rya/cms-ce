package com.enonic.cms.core.plugin;

import java.util.List;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;

public interface ExtensionSet
    extends Iterable<Extension>
{
    public List<Extension> getAllExtensions();

    public List<FunctionLibrary> getAllFunctionLibraries();

    public List<HttpInterceptor> getAllHttpInterceptors();

    public List<HttpResponseFilter> getAllHttpResponseFilters();

    public List<HttpAutoLogin> getAllHttpAutoLoginPlugins();

    public List<TaskHandler> getAllTaskPlugins();

    public TaskHandler findTaskPlugin( String className );

    public FunctionLibrary findFunctionLibrary( String namespace );

    public List<HttpInterceptor> findMatchingHttpInterceptors( String path );

    public List<HttpResponseFilter> findMatchingHttpResponseFilters( String path );

    public HttpAutoLogin findMatchingHttpAutoLoginPlugin( String path );

    public TextExtractor findTextExtractorPluginByMimeType( String mimeType );

    public List<TextExtractor> getAllTextExtractorPlugins();
}
