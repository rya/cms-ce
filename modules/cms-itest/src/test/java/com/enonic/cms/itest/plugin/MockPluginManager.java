package com.enonic.cms.itest.plugin;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;
import com.enonic.cms.core.plugin.ExtensionSet;
import com.enonic.cms.core.plugin.PluginHandle;
import com.enonic.cms.core.plugin.PluginManager;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class MockPluginManager
    implements PluginManager
{
    public List<PluginHandle> getPlugins()
    {
        return Lists.newArrayList();
    }

    public ExtensionSet getExtensions()
    {
        return new ExtensionSet()
        {
            public List<Extension> getAllExtensions()
            {
                return Lists.newArrayList();
            }

            public List<FunctionLibrary> getAllFunctionLibraries() {
                return Lists.newArrayList();
            }

            public List<HttpInterceptor> getAllHttpInterceptors() {
                return Lists.newArrayList();
            }

            public List<HttpResponseFilter> getAllHttpResponseFilters() {
                return Lists.newArrayList();
            }

            public List<HttpAutoLogin> getAllHttpAutoLoginPlugins() {
                return Lists.newArrayList();
            }

            public List<TaskHandler> getAllTaskPlugins() {
                return Lists.newArrayList();
            }

            public TaskHandler findTaskPlugin(String className) {
                return null;
            }

            public FunctionLibrary findFunctionLibrary(String namespace) {
                return null;
            }

            public List<HttpInterceptor> findMatchingHttpInterceptors(String path) {
                return Lists.newArrayList();
            }

            public List<HttpResponseFilter> findMatchingHttpResponseFilters(String path) {
                return Lists.newArrayList();
            }

            public HttpAutoLogin findMatchingHttpAutoLoginPlugin(String path) {
                return null;
            }

            public TextExtractor findTextExtractorPluginByMimeType(String mimeType) {
                return null;
            }

            public List<TextExtractor> getAllTextExtractorPlugins() {
                return Lists.newArrayList();
            }

            public Iterator<Extension> iterator()
            {
                return Iterators.emptyIterator();
            }
        };
    }

    public PluginHandle findPluginByKey(long key)
    {
        return null;
    }
}
