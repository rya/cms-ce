package com.enonic.cms.core.plugin;

import java.util.Collection;
import java.util.Map;

import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;

import com.enonic.cms.domain.SitePath;

public interface ExtensionManager
{
    public Collection<FunctionLibrary> getAllFunctionLibraries();

    public Collection<HttpInterceptor> getAllHttpInterceptors();

    public Collection<HttpResponseFilter> getAllHttpResponseFilters();

    public Collection<HttpAutoLogin> getAllHttpAutoLoginPlugins();

    public Collection<TaskHandler> getAllTaskPlugins();

    public TaskHandler findTaskPlugin(String className);

    public FunctionLibrary findFunctionLibrary(String namespace);

    public Collection<HttpInterceptor> findMatchingHttpInterceptors(String path);

    public Collection<HttpResponseFilter> findMatchingHttpResponseFilters(SitePath originalSitePath);

    public HttpAutoLogin findMatchingHttpAutoLoginPlugin(String path);

    public TextExtractor findTextExtractorPluginByMimeType(String mimeType);

    public Collection<TextExtractor> getAllTextExtractorPlugins();

    public PluginRegistry getPluginRegistry();

    public Map<String, ExtensionListener> getListeners();

    public void setListeners( Map<String, ExtensionListener> listeners );
}
