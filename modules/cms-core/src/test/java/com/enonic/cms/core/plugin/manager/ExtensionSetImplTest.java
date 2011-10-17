package com.enonic.cms.core.plugin.manager;

import com.enonic.cms.api.plugin.ext.Extension;
import com.enonic.cms.api.plugin.ext.FunctionLibrary;
import com.enonic.cms.api.plugin.ext.TaskHandler;
import com.enonic.cms.api.plugin.ext.TextExtractor;
import com.enonic.cms.api.plugin.ext.http.HttpAutoLogin;
import com.enonic.cms.api.plugin.ext.http.HttpInterceptor;
import com.enonic.cms.api.plugin.ext.http.HttpResponseFilter;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import java.util.List;

public class ExtensionSetImplTest
{
    @Test
    public void testIterate()
    {
        final Extension ext = Mockito.mock(Extension.class);
        final List<Extension> list = Lists.newArrayList(ext);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<Extension> result = Lists.newArrayList(set);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext, result.get(0));
    }

    @Test
    public void testGetAllExtensions()
    {
        final Extension ext = Mockito.mock(Extension.class);
        final List<Extension> list = Lists.newArrayList(ext);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<Extension> result = set.getAllExtensions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext, result.get(0));
    }

    @Test
    public void testGetTextExtractors()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(TextExtractor.class);
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<TextExtractor> result = set.getAllTextExtractorPlugins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testGetTaskHandlers()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(TaskHandler.class);
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<TaskHandler> result = set.getAllTaskPlugins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testGetFunctionLibraries()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = new FunctionLibrary();
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<FunctionLibrary> result = set.getAllFunctionLibraries();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testGetHttpAutoLogins()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(HttpAutoLogin.class);
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<HttpAutoLogin> result = set.getAllHttpAutoLoginPlugins();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testGetHttpResponseFilters()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(HttpResponseFilter.class);
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<HttpResponseFilter> result = set.getAllHttpResponseFilters();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testGetHttpInterceptors()
    {
        final Extension ext1 = Mockito.mock(Extension.class);
        final Extension ext2 = Mockito.mock(HttpInterceptor.class);
        final List<Extension> list = Lists.newArrayList(ext1, ext2);

        final ExtensionSetImpl set = new ExtensionSetImpl(list);
        final List<HttpInterceptor> result = set.getAllHttpInterceptors();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext2, result.get(0));
    }

    @Test
    public void testFindFunctionLibrary()
    {
        final FunctionLibrary ext = new FunctionLibrary();
        ext.setName("lib");

        final List<Extension> list = Lists.newArrayList((Extension)ext);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        FunctionLibrary result = set.findFunctionLibrary("other");
        assertNull(result);

        result = set.findFunctionLibrary("lib");
        assertSame(ext, result);
    }

    @Test
    public void testFindTaskHandler()
    {
        final TaskHandler ext = Mockito.mock(TaskHandler.class);
        ext.setName("task");

        final List<Extension> list = Lists.newArrayList((Extension)ext);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        TaskHandler result = set.findTaskPlugin("other");
        assertNull(result);

        result = set.findTaskPlugin("task");
        assertSame(ext, result);
    }

    @Test
    public void testFindTextExtractor()
    {
        final TextExtractor ext = Mockito.mock(TextExtractor.class);
        Mockito.when(ext.canHandle("text/plain")).thenReturn(true);

        final List<Extension> list = Lists.newArrayList((Extension)ext);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        TextExtractor result = set.findTextExtractorPluginByMimeType("text/html");
        assertNull(result);

        result = set.findTextExtractorPluginByMimeType("text/plain");
        assertSame(ext, result);
    }

    @Test
    public void testFindHttpInterceptors()
    {
        final HttpInterceptor ext1 = Mockito.mock(HttpInterceptor.class);
        ext1.setUrlPattern("/a/.+");

        final HttpInterceptor ext2 = Mockito.mock(HttpInterceptor.class);
        ext2.setUrlPattern("/b/.+");
        
        final List<Extension> list = Lists.newArrayList((Extension)ext1, ext2);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        List<HttpInterceptor> result = set.findMatchingHttpInterceptors("/c/d");
        assertNotNull(result);
        assertEquals(0, result.size());

        result = set.findMatchingHttpInterceptors("/a/b");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext1, result.get(0));
    }

    @Test
    public void testFindHttpResponseFilters()
    {
        final HttpResponseFilter ext1 = Mockito.mock(HttpResponseFilter.class);
        ext1.setUrlPattern("/a/.+");

        final HttpResponseFilter ext2 = Mockito.mock(HttpResponseFilter.class);
        ext2.setUrlPattern("/b/.+");

        final List<Extension> list = Lists.newArrayList((Extension)ext1, ext2);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        List<HttpResponseFilter> result = set.findMatchingHttpResponseFilters("/c/d");
        assertNotNull(result);
        assertEquals(0, result.size());

        result = set.findMatchingHttpResponseFilters("/a/b");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(ext1, result.get(0));
    }

    @Test
    public void testFindHttpAutoLogins()
    {
        final HttpAutoLogin ext1 = Mockito.mock(HttpAutoLogin.class);
        ext1.setUrlPattern("/a/.+");

        final HttpAutoLogin ext2 = Mockito.mock(HttpAutoLogin.class);
        ext2.setUrlPattern("/b/.+");

        final List<Extension> list = Lists.newArrayList((Extension)ext1, ext2);
        final ExtensionSetImpl set = new ExtensionSetImpl(list);

        HttpAutoLogin result = set.findMatchingHttpAutoLoginPlugin("/c/d");
        assertNull(result);

        result = set.findMatchingHttpAutoLoginPlugin("/a/b");
        assertNotNull(result);
        assertSame(ext1, result);
    }
}
